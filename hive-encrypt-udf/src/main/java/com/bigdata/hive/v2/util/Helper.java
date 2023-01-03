package com.bigdata.hive.v2.util;


import com.bigdata.hive.v2.Model.Response;
import com.bigdata.hive.v2.Model.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;

public class Helper {
    public final Long cachedItemsExpiry = 1L;
      HiveConf hiveConf = new HiveConf();

    SparkConf sparkConf = new SparkConf();


//    private
    private static final Logger log = Logger.getLogger(Helper.class);
    public  volatile  String base_url ;

    public  volatile String bearerAuth;

    public  Helper(){
        this.base_url = hiveConf.get("kms.api.url") == null?
                sparkConf.get("kms.api.url") :hiveConf.get("kms.api.url");
    }
    public final LoadingCache<String, String> aesKeyCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(cachedItemsExpiry, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    String[] parts = key.split("\\^");
                    return getKeyFromHttp(parts[0], parts[1]);
                }
            });
    public final LoadingCache<String, String> bearerTokenCache = CacheBuilder.newBuilder()
            .maximumSize(2)
            .expireAfterWrite(cachedItemsExpiry, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    return  getBearerToken();
                }
            });


    public String getKeyFromHttp(String username, String id) {

        String aesKey = "unauthorized";

        try {
            String url = String.format(this.base_url + "/api/v1/key?id=%s&username=%s", id, username);
            HttpGet request = new HttpGet(url);
            request.addHeader("Authorization", this.getBearerFromCache());

            CloseableHttpClient httpclient = HttpClients.custom().build();
            CloseableHttpResponse closeableHttpResponse = httpclient.execute(request);
            try {
                if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                    System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
                } else {
                    HttpEntity entity = closeableHttpResponse.getEntity();
                    ObjectMapper mapper = new ObjectMapper();
                    Response response = mapper.readValue(EntityUtils.toString(entity), Response.class);
                    aesKey = response.getKey();
                }

            } catch (IOException | ParseException e) {
                aesKey = aesKey+" IOException or ParseException error";
                log.error(e);
                e.printStackTrace();
            } finally {
                closeableHttpResponse.close();
            }
        } catch (IOException e) {
            aesKey = aesKey+ " IOException or ParseException error";
          log.error(e);
        }
        log.info("AesKey: "+ aesKey);
        return aesKey;
    }


    public  String getBearerToken(){
        try {
            String authUsername = hiveConf.get("kms.api.auth.username") == null?sparkConf.get("kms.api.auth.username"):hiveConf.get("kms.api.auth.username");
            String authPassword = hiveConf.get("kms.api.auth.password")==null?sparkConf.get("kms.api.auth.password"):hiveConf.get("kms.api.auth.password");

            String url = String.format(this.base_url + "/access/token");
            String creds = "Basic "+ Base64.getEncoder().encodeToString( (authUsername+":"+authPassword).getBytes());
            HttpPost request = new HttpPost(url);
            request.addHeader("Authorization", creds);
            CloseableHttpClient httpclient = HttpClients.custom().build();
            CloseableHttpResponse closeableHttpResponse = httpclient.execute(request);

            try {
                if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                    System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
                    this.bearerAuth= "Wrong kms user Credentials";
                } else {
                    HttpEntity entity = closeableHttpResponse.getEntity();
                    ObjectMapper mapper = new ObjectMapper();
                    Token response = mapper.readValue(EntityUtils.toString(entity), Token.class);
                    this.bearerAuth= "Bearer "+ response.getToken();
                }
            } catch (IOException | ParseException e) {
                this.bearerAuth= "Get Bearer Error";
                log.error("Kms: {}",e);
            } finally {
                closeableHttpResponse.close();
//                this.bearerAuth= "Get Bearer Error";
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Kms: {}",e);
            this.bearerAuth= "Get Bearer Error";
        }
        return  bearerAuth;

    }
    public String getKeyFromCache(String username, String id) {

        String cacheKeyName = username + "^" + id;
        try {
            return this.aesKeyCache.get(cacheKeyName);
        } catch (ExecutionException e) {
            e.printStackTrace();
            log.error(e);
            return "getKeyFromCache: unauthorized";
        }
    }

    public  String getBearerFromCache(){
        try {
            return this.bearerTokenCache.get("token");
        } catch (ExecutionException e) {
            e.printStackTrace();
            log.error(e);
            return "getBearerFromCache: unauthorized";
        }
    }


}
