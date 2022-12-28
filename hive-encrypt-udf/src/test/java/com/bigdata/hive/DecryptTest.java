package com.bigdata.hive;

import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.JavaStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.junit.Assert;
import org.junit.Test;

public class DecryptTest {

    private final String encKey = "n9Tp9+69gxNdUg9F632u1cCRuqcOuGmN";
    private final String encAlgo = "AES/CBC/PKCS5Padding";
    private final String colVal = "123456789";
    private final String cipherText = "123456789";

    @Test
    public void testDecryptReturnsCorrectValues() throws HiveException {
        Decrypt decrypt = new Decrypt();
        ObjectInspector stringKey = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringAlgo = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringColVal = PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        JavaStringObjectInspector resultInspector = (JavaStringObjectInspector) decrypt.initialize(new ObjectInspector[]{stringKey, stringAlgo, stringColVal});

        Object result = decrypt.evaluate(new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject(encKey), new GenericUDF.DeferredJavaObject(encAlgo), new GenericUDF.DeferredJavaObject(cipherText)});
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
