package com.bigdata.hive;

import org.junit.Assert;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.JavaStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.junit.Test;

public class EncryptTest {

    private final String encKey = "n9Tp9+69gxNdUg9F632u1cCRuqcOuGmN";
    private final String encAlgo = "AES/CBC/PKCS5Padding";
    private final String colVal = "123456789";
    private final String res = "b2vgwX61osfSwv/pMEBQzg==";

    @Test
    public void testEncryptReturnsCorrectValues() throws HiveException {
        Encrypt encrypt = new Encrypt();
        ObjectInspector stringKey = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringAlgo = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringColVal = PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        JavaStringObjectInspector resultInspector = (JavaStringObjectInspector) encrypt.initialize(new ObjectInspector[]{stringKey, stringAlgo, stringColVal});

        Object result = encrypt.evaluate(new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject(encKey), new GenericUDF.DeferredJavaObject(encAlgo), new GenericUDF.DeferredJavaObject(colVal)});
        Assert.assertEquals(res, resultInspector.getPrimitiveJavaObject(result));
    }

    @Test
    public void testEncryptReturnsWrongValues() throws HiveException {
        Encrypt encrypt = new Encrypt();
        ObjectInspector stringKey = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringAlgo = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringColVal = PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        JavaStringObjectInspector resultInspector = (JavaStringObjectInspector) encrypt.initialize(new ObjectInspector[]{stringKey, stringAlgo, stringColVal});

        Object result = encrypt.evaluate(new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject(encKey), new GenericUDF.DeferredJavaObject(encAlgo), new GenericUDF.DeferredJavaObject(colVal)});
        Assert.assertNotEquals("wrongValue", resultInspector.getPrimitiveJavaObject(result));
    }

    @Test
    public void testEncryptReturnsNullValues() throws HiveException {
        Encrypt encrypt = new Encrypt();
        ObjectInspector stringKey = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringAlgo = PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringColVal = PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        JavaStringObjectInspector resultInspector = (JavaStringObjectInspector) encrypt.initialize(new ObjectInspector[]{stringKey, stringAlgo, stringColVal});

        Object result = encrypt.evaluate(new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject(null), new GenericUDF.DeferredJavaObject(null), new GenericUDF.DeferredJavaObject(null)});
        Assert.assertNull(resultInspector.getPrimitiveJavaObject(result));
    }
}
