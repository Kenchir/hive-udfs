package com.bigdata.hive.v2.service;

public interface EncryptionAlgorithm {
    /**
     *
     * @param algorithm
     * @param cipherText
     * @param key
     * @return
     */
    String encrypt(String algorithm, String cipherText, String key);

    /***
     *
     * @param algorithm
     * @param cipherText
     * @param key
     * @return
     */
    String decrypt(String algorithm, String cipherText, String key);
}
