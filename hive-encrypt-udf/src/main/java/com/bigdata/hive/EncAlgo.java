package com.bigdata.hive;

public enum EncAlgo {
    AES_ECB("AES/ECB/PKCS5Padding"),
    AES_CBC("AES/CBC/PKCS5Padding");

    private final String algo;

    EncAlgo(String algo){
        this.algo = algo;
    }

    public String getAlgo(){
        return algo;
    }

}
