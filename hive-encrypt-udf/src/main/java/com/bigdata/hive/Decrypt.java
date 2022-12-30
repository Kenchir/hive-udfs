package com.bigdata.hive;

import com.bigdata.hive.util.Helper;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.session.SessionState;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import com.bigdata.hive.service.AesEncryption;
import org.apache.hive.com.esotericsoftware.kryo.DefaultSerializer;
import org.apache.log4j.Logger;

import static org.apache.commons.codec.binary.Base64.isBase64;

@DefaultSerializer(value = DoNothingSerializer1.class)
public class Decrypt extends GenericUDF {
    private static final Logger log = Logger.getLogger(Decrypt.class);
    private StringObjectInspector identifier;

    private StringObjectInspector column;
    private final AesEncryption aesEncryption = new AesEncryption();
    volatile String algorithm = "AES/CBC/PKCS5Padding";
    volatile Helper helper = new Helper();

    @Override
    public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
        if (arguments.length != 2)
        {
            throw new UDFArgumentLengthException("decrypt() only takes 2 arguments: cipherColumn<String>,identifier<String>");
        }
        // 1. Check we received the right object types.
        ObjectInspector a = arguments[0];
        ObjectInspector b = arguments[1];

        if (!(a instanceof StringObjectInspector) || !(b instanceof StringObjectInspector))
        {
            throw new UDFArgumentException("first argument must be a string, second argument must be a string");
        }
        this.identifier = (StringObjectInspector) b;
        this.column = (StringObjectInspector) a;
        

        // the return type of our function is a string, so we provide the correct object inspector
        return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    }
    @Override
    public Object evaluate(DeferredObject[] arguments) throws HiveException {

        String cipherText = column.getPrimitiveJavaObject(arguments[0].get());
        String id = identifier.getPrimitiveJavaObject(arguments[1].get());
//        String username= SessionState.get().getUserName();

        if (!isBase64(cipherText)){
            return  cipherText;
        }

        String encKey = this.helper.getKeyFromCache("hive", id);
    log.debug("KMS KEY:  " + encKey);
        if (encKey.contains("Invalid") || encKey.contains("unauthorized")){
            return  encKey;
        }
        return  this.aesEncryption.decrypt(this.algorithm,cipherText,encKey);


    }

    @Override
    public String getDisplayString(String[] strings) {
        return "decrypt(column, identifier)";
    }
}
