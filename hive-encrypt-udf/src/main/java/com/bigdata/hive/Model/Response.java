package com.bigdata.hive.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
//import com.google.api.client.util.Key;

public class Response {
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("key")
    private String key;
    @JsonProperty("expired")
    private boolean expired;



    public String getKey() {
        return key;
    }


}
