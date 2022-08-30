package com.bigdata.hive;

import org.apache.hive.com.esotericsoftware.kryo.Kryo;

import org.apache.hive.com.esotericsoftware.kryo.Serializer;

import org.apache.hive.com.esotericsoftware.kryo.io.Input;

import org.apache.hive.com.esotericsoftware.kryo.io.Output;



public class DoNothingSerializer extends Serializer<Encrypt> {



    @Override

    public Encrypt read(Kryo arg0, Input arg1, Class<Encrypt> arg2) {

// TODO Auto-generated method stub

        return new Encrypt();

    }



    @Override

    public void write(Kryo arg0, Output arg1, Encrypt arg2) {

// TODO Auto-generated method stub



    }



}