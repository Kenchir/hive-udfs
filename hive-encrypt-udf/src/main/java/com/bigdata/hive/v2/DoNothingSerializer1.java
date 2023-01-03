package com.bigdata.hive.v2;

import org.apache.hive.com.esotericsoftware.kryo.Kryo;
import org.apache.hive.com.esotericsoftware.kryo.Serializer;
import org.apache.hive.com.esotericsoftware.kryo.io.Input;
import org.apache.hive.com.esotericsoftware.kryo.io.Output;


public class DoNothingSerializer1 extends Serializer<Decrypt> {



    @Override

    public Decrypt read(Kryo arg0, Input arg1, Class<Decrypt> arg2) {

        return new Decrypt();

    }



    @Override

    public void write(Kryo arg0, Output arg1, Decrypt arg2) {


    }



}