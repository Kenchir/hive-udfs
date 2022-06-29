package com.bigdata.hive;

import org.apache.hadoop.hive.conf.HiveConf;
import org.junit.Assert;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.JavaStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.junit.Test;

public class EncryptTest {


    private final String colVal = "123456789";
    private final String res = "b2vgwX61osfSwv/pMEBQzg==";
    private final String idVal = "callingpartynumbershpsqldb";
//    private final HiveConf hiveConf = new HiveConf();

    @Test
    public void testEncryptReturnsCorrectValues() throws HiveException {
//        hiveConf.get("hive.kms.api.url","https://kms.big-data2.safaricomet.net/api/v1/token");
//
//        hiveConf.get("hive.kms.api.auth.token","a");
        System.out.println(EncAlgo.AES_CBC);
        Encrypt encrypt = new Encrypt();

        ObjectInspector stringIdVal= PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringColVal = PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        JavaStringObjectInspector resultInspector = (JavaStringObjectInspector) encrypt.initialize(new ObjectInspector[]{stringColVal, stringIdVal});

        Object result = encrypt.evaluate(new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject(colVal), new GenericUDF.DeferredJavaObject(idVal)});
        Assert.assertEquals(res, resultInspector.getPrimitiveJavaObject(result));
    }

    @Test
    public void testEncryptReturnsWrongValues() throws HiveException {
        Encrypt encrypt = new Encrypt();

        ObjectInspector stringIdVal= PrimitiveObjectInspectorFactory.javaStringObjectInspector;
        ObjectInspector stringColVal = PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        JavaStringObjectInspector resultInspector = (JavaStringObjectInspector) encrypt.initialize(new ObjectInspector[]{stringColVal,stringIdVal});
        Object result = encrypt.evaluate(new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject(colVal),  new GenericUDF.DeferredJavaObject(idVal)});
        Assert.assertNotEquals("wrongValue", resultInspector.getPrimitiveJavaObject(result));
    }

    @Test
    public void testEncryptReturnsNullValues() throws HiveException {
        Encrypt encrypt = new Encrypt();
        ObjectInspector stringIdVal= PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        ObjectInspector stringColVal = PrimitiveObjectInspectorFactory.javaStringObjectInspector;

        JavaStringObjectInspector resultInspector = (JavaStringObjectInspector) encrypt.initialize(new ObjectInspector[]{stringColVal,stringIdVal});

        Object result = encrypt.evaluate(new GenericUDF.DeferredObject[]{new GenericUDF.DeferredJavaObject(null), new GenericUDF.DeferredJavaObject(null)});
        Assert.assertNull(resultInspector.getPrimitiveJavaObject(result));
    }
}
