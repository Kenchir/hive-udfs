package com.bigdata.hive.v2;

import com.bigdata.hive.v2.service.AesEncryption;
import com.bigdata.hive.v2.util.Helper;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hive.com.esotericsoftware.kryo.DefaultSerializer;


@DefaultSerializer(value = DoNothingSerializer.class)
public class Encrypt extends GenericUDF {


    private StringObjectInspector id;
    private StringObjectInspector column;
    volatile String algorithm = "AES/CBC/PKCS5Padding";
    volatile public AesEncryption aesEncryption= new AesEncryption() ;
    private final Helper helper  =new Helper();

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length != 2)
        {
            throw new UDFArgumentLengthException("encrypt() only takes 2 arguments: column<String>,identifier<String>,");
        }
        // 1. Check we received the right object types.
        ObjectInspector a = arguments[0];
        ObjectInspector b = arguments[1];


        if (!(a instanceof StringObjectInspector) || !(b instanceof StringObjectInspector) )
        {
            throw new UDFArgumentException("first argument must be a string, second argument must be a string");
        }
        this.id = (StringObjectInspector) b;
        this.column = (StringObjectInspector) a;

        return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {

        String identifier = id.getPrimitiveJavaObject(arguments[1].get());

        String colName = column.getPrimitiveJavaObject(arguments[0].get());

        if (identifier == null || colName == null ) {
            return null;
        }

        String encKey = helper.getKeyFromCache("hive", identifier);

        if (encKey.contains("Invalid") || encKey.contains("unauthorized")){
            return  encKey;
        }

        return  aesEncryption.encrypt(algorithm,colName,encKey);
    }

    @Override
    public String getDisplayString(String[] strings) {
        return "encrypt(column,identifier)";
    }
}