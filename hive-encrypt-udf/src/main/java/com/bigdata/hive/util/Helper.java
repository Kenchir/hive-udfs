package com.bigdata.hive.util;


import com.bigdata.hive.Model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.calcite.avatica.org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Helper {
    public final Long cachedItemsExpiry = 1L;
    private final HiveConf hiveConf = new HiveConf();

    public final LoadingCache<String, String> aesKeyCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(cachedItemsExpiry, TimeUnit.DAYS)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    String[] parts = key.split("_");
                    return getKeyFromHttp(parts[0], parts[1]);
                }
            });

    public String getKeyFromHttp(String username, String id) {

        String aesKey = "unauthorized";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            String base_url = hiveConf.get("hive.kms.api.url");

            String auth_token = hiveConf.get("hive.kms.api.auth.token");

            String url = String.format(base_url + "?id=%s&username=%s", id, username);
            HttpGet request = new HttpGet(url);
            request.addHeader("Authorization", auth_token);


            CloseableHttpClient httpclient = HttpClients.custom().
                    setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                        public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                            return true;
                        }
                    }).build()).build();

            CloseableHttpResponse closeableHttpResponse = httpclient.execute(request);

            try {
                if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
                    System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
                    return aesKey;
                } else {
                    HttpEntity entity = closeableHttpResponse.getEntity();
                    ObjectMapper mapper = new ObjectMapper();
                    Response response = mapper.readValue(EntityUtils.toString(entity), Response.class);
                    aesKey = response.getKey();
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            } finally {
                closeableHttpResponse.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return aesKey;
    }

    public String getKeyFromCache(String username, String id) {

        String cacheKeyName = username + "_" + id;
        try {
            return this.aesKeyCache.get(cacheKeyName);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "unauthorized";
        }
    }


}
