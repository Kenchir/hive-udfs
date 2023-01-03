package com.bigdata.hive.v2;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.JavaStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.spark.SparkConf;
import org.junit.Assert;
import org.junit.Test;

public class DecryptTest {

    private final String encKey = "nifi.datarobot.msisdns";
    private final String encAlgo = "AES/CBC/PKCS5Padding";
    private final String colVal = "0123456789";
    private final String cipherText = "0123456789";
    public  volatile   HiveConf hiveConf = new HiveConf();
    public volatile SparkConf sparkConf = new SparkConf();
//    private
    @Test
    public void testDecryptReturnsCorrectValues() throws HiveException {
        hiveConf.set("kms.api.url","https://10.3.41.29:8080");
        sparkConf.get("kms.api.url","https://10.3.41.29:8080");
//
//        hiveConf.get("hive.kms.api.auth.token","a");
        hiveConf.set("kms.api.auth.username","svc-bigdata-admin");
        hiveConf.set("kms.api.auth.password","lyOdmSBwCsZnD4dLnOAE");
        Decrypt decrypt = new Decrypt();
        ObjectInspector stringKey = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringAlgo = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringColVal = PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        JavaStringObjectInspector resultInspector = (JavaStringObjectInspector) decrypt.initialize(new ObjectInspector[]{ stringColVal,stringKey});

        Object result = decrypt.evaluate(new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject(cipherText),new GenericUDF.DeferredJavaObject(encKey),});
        Assert.assertEquals(colVal, resultInspector.getPrimitiveJavaObject(result));
    }

    @Test
    public void testDecryptReturnsWrongValues() throws HiveException {
        Decrypt decrypt = new Decrypt();
        ObjectInspector stringKey = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringAlgo = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringColVal = PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        JavaStringObjectInspector resultInspector = (JavaStringObjectInspector) decrypt.initialize(new ObjectInspector[]{stringKey, stringAlgo, stringColVal});

        Object result = decrypt.evaluate(new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject(encKey), new GenericUDF.DeferredJavaObject(encAlgo), new GenericUDF.DeferredJavaObject(cipherText)});
        Assert.assertNotEquals("wrongValue", resultInspector.getPrimitiveJavaObject(result));
    }
}
