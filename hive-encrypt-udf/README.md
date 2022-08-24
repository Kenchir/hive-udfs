# This UDF helps in encrypting or decrypting a String column type in a hive table.

## Usage
  The encrypt function takes three arguments:
   ``` java
   encrypt hive table column of string data type to encrypt , identifier used in kms server)
   ```

* Copy the jar to hdfs
  ``` bash
  hdfs dfs -copyFromLocal <hive-encrypted-jar> <path in hdfs>
  ```
* user beeline to establish interactive session with hive
  ```bash
  ./beeline -u "jdbc:hive2://<hive zk host port>"
  ```
* Once inside beeline, issue below sql queries.
* First create a function from your udf
  ``` sql
  create temporary function enc as 'com.bigdata.hive.Encrypt' using jar 'hdfs:///<path to your jar in hdfs>';
  ```
* Use the function in your queries.
``` sql
select enc('name','id') from employees2
```

## Sample table
``` bash
0: jdbc:hive2://bigdatadev> select * from employees2;
INFO  : Compiling command(queryId=hive_20220330004713_1041444c-e451-4bf4-bb53-3b883694ef75): select * from employees2
INFO  : Semantic Analysis Completed (retrial = false)
INFO  : Returning Hive schema: Schema(fieldSchemas:[FieldSchema(name:employees2.name, type:string, comment:null), FieldSchema(name:employees2.destination, type:string, comment:null)], properties:null)
INFO  : Completed compiling command(queryId=hive_20220330004713_1041444c-e451-4bf4-bb53-3b883694ef75); Time taken: 1.066 seconds
INFO  : Executing command(queryId=hive_20220330004713_1041444c-e451-4bf4-bb53-3b883694ef75): select * from employees2
INFO  : Completed executing command(queryId=hive_20220330004713_1041444c-e451-4bf4-bb53-3b883694ef75); Time taken: 0.041 seconds
INFO  : OK
+------------------+-------------------------+
| employees2.name  | employees2.destination  |
+------------------+-------------------------+
| Mathew           | Addis                   |
| Stephany         | CapeTown                |
| Susan            | Cairo                   |
| John             | Lagos                   |
+------------------+-------------------------+
4 rows selected (1.849 seconds)

```

## Sample Encrypted name column

``` bash
0: jdbc:hive2://bigdatadev> select enc(name,'id') as enc_name, destination from employees2;
INFO  : Compiling command(queryId=hive_20220330095147_6472db4d-702a-4c0c-ad4c-296bbda2f6be): select  enc(name,'id') as enc_name, destination from employees2
INFO  : Semantic Analysis Completed (retrial = false)
INFO  : Returning Hive schema: Schema(fieldSchemas:[FieldSchema(name:enc_name, type:string, comment:null), FieldSchema(name:destination, type:string, comment:null)], properties:null)
INFO  : Completed compiling command(queryId=hive_20220330095147_6472db4d-702a-4c0c-ad4c-296bbda2f6be); Time taken: 1.251 seconds
INFO  : Executing command(queryId=hive_20220330095147_6472db4d-702a-4c0c-ad4c-296bbda2f6be): select  enc(name,'id') as enc_name, destination from employees2
INFO  : Completed executing command(queryId=hive_20220330095147_6472db4d-702a-4c0c-ad4c-296bbda2f6be); Time taken: 0.042 seconds
INFO  : OK
+---------------------------+--------------+
|         enc_name          | destination  |
+---------------------------+--------------+
| OzasFqcAk5A6wz1ejk4ueA==  | Addis        |
| EGLSWVfL5IvcUQVSWFm/zg==  | CapeTown     |
| t1SG3CQz+iqbVhXqWdewSg==  | Cairo        |
| f5QVzJn+ZYTZgpPRmIha2Q==  | Lagos        |
+---------------------------+--------------+


```
