package com.bigdata.hive;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt extends GenericUDF {

    private StringObjectInspector key;
    private StringObjectInspector algorithm;
    private StringObjectInspector column;

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length != 3)
        {
            throw new UDFArgumentLengthException("encrypt() only takes 3 arguments: key<String>, algorithm<String>, column<String>");
        }
        // 1. Check we received the right object types.
        ObjectInspector a = arguments[0];
        ObjectInspector b = arguments[1];
        ObjectInspector c = arguments[2];

        if (!(a instanceof StringObjectInspector) || !(b instanceof StringObjectInspector) || !(c instanceof StringObjectInspector))
        {
            throw new UDFArgumentException("first argument must be a string, second argument must be a string, third argument must be a string");
        }
        this.key = (StringObjectInspector) a;
        this.algorithm = (StringObjectInspector) b;
        this.column = (StringObjectInspector) c;

        return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {
        String encKey = key.getPrimitiveJavaObject(arguments[0].get());
        String encAlgorithm = algorithm.getPrimitiveJavaObject(arguments[1].get());

        String colName = column.getPrimitiveJavaObject(arguments[2].get());

        if (encKey == null || colName == null || encAlgorithm == null) {
            return null;
        }

//        try {
//            EncAlgo.valueOf(encAlgorithm).getAlgo();
//        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(encKey), "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(encAlgorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[16]));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        byte[] cipherText = new byte[0];
        try {
            cipherText = cipher.doFinal(colName.getBytes());
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(cipherText);


    }

    @Override
    public String getDisplayString(String[] strings) {
        return "encrypt(key, algorithm, column)";
    }
}
