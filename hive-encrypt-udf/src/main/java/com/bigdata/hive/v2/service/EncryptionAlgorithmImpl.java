package com.bigdata.hive.v2.service;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.apache.commons.codec.binary.Base64.isBase64;


public class EncryptionAlgorithmImpl implements EncryptionAlgorithm {


    /**
     *
     * @param algorithm
     * @param plainText
     * @param key
     * @return  cipherText
     */
    @Override
    public String encrypt(String algorithm, final String plainText, final String key) {
        String result = null;

        if (plainText == null || plainText.trim().equals("")){
            return  null;
        }

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
            Cipher cipher;
            byte[] cipherText;
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
            cipherText = cipher.doFinal(plainText.getBytes());
            result = Base64.getEncoder().encodeToString(cipherText);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException e) {

            result = "Invalid";
        } catch (   IllegalBlockSizeException | BadPaddingException e){

            if(!plainText.contains("==")){
                result= plainText;
            }else {
                result = "Invalid";
            }
        } finally {
            return result;
        }



    }

    /**
     *
     * @param algorithm
     * @param cipherText
     * @param key
     * @return plainText
     */


    @Override
    public String decrypt(String algorithm, String cipherText, String key) {
        if (cipherText == null || cipherText.trim().equals("")){
            return  null;
        }

        String result = null;

        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
            Cipher cipher;
            byte[] plainText;
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
            plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            result = new String(plainText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                  InvalidKeyException e) {

            result = "Invalid";
        } catch (   IllegalBlockSizeException | BadPaddingException e){

            if(cipherText.contains("==")){
                result= "Invalid";
            }else {
                result = cipherText;
            }
        } finally {
            return result;
        }

//        return  result;

    }

}
