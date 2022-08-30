package com.bigdata.hive.Model;


import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonProperty;
//import com.google.api.client.util.Key;

public class Response {
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("key")
    private String key;


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getKey() {
        return key;
    }




}
