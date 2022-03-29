# This UDF helps in encrypting or decrypting a String column type in a hive table.

## Usage

* Copy the jar to hdfs
  ``` bash
  hdfs dfs -copyFromLocal <hive-encrypted-jar> <path in hdfs>
  ```
* user beelin etoestablish interactive sesion with hive
  ```bash
  ./beeline -u "jdbc:hive2://<hive zk host port>"
  ```
* Once inside beeline, issue below sql quiries.
* First create a function fro your udf
  ``` sql
  create temporary function enc3 as 'com.bigdata.hive.Encrypt' using jar 'hdfs:///<path to your jar in hdfs>';
  ```
* Use the function in your queries.
``` sql
select enc('n9Tp9+69gxNdUg9F632u1cCRuqcOuGmN','AES/CBC/PKCS5Padding',name) from employees2
```

## Sample output
``` bash
0: jdbc:hive2://bigdatadev> select enc('n9Tp9+69gxNdUg9F632u1cCRuqcOuGmN','AES/CBC/PKCS5Padding','name') from employees2;
INFO  : Compiling command(queryId=hive_20220330011358_cb85e288-0a1c-4edb-9b0f-5ba0341af39a): select enc('n9Tp9+69gxNdUg9F632u1cCRuqcOuGmN','AES/CBC/PKCS5Padding','name') from employees2
INFO  : Semantic Analysis Completed (retrial = false)
INFO  : Returning Hive schema: Schema(fieldSchemas:[FieldSchema(name:_c0, type:string, comment:null)], properties:null)
INFO  : Completed compiling command(queryId=hive_20220330011358_cb85e288-0a1c-4edb-9b0f-5ba0341af39a); Time taken: 1.073 seconds
INFO  : Executing command(queryId=hive_20220330011358_cb85e288-0a1c-4edb-9b0f-5ba0341af39a): select enc('n9Tp9+69gxNdUg9F632u1cCRuqcOuGmN','AES/CBC/PKCS5Padding','name') from employees2
INFO  : Completed executing command(queryId=hive_20220330011358_cb85e288-0a1c-4edb-9b0f-5ba0341af39a); Time taken: 0.057 seconds
INFO  : OK
+---------------------------+
|            _c0            |
+---------------------------+
| wEDd9Ex8lD367bDPxIQoGQ==  |
| wEDd9Ex8lD367bDPxIQoGQ==  |
| wEDd9Ex8lD367bDPxIQoGQ==  |
| wEDd9Ex8lD367bDPxIQoGQ==  |
+---------------------------+
4 rows selected (1.863 seconds)

```
