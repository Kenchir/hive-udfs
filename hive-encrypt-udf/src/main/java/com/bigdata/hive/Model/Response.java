package com.bigdata.hive.Model;


import com.fasterxml.jackson.annotation.JsonProperty;
//import com.google.api.client.util.Key;

public class Response {

    @JsonProperty
    Data data;

    @JsonProperty
    String message;

    @JsonProperty
    Integer status;


    public String getKey() {
        return data.key;
    }

    private static class  Data{
        @JsonProperty("identifier")
        private String identifier;
        @JsonProperty("key")
        private String key;

        @JsonProperty("expired")
        private boolean expired;

        @JsonProperty("username")
        private  String username;
    }

}
