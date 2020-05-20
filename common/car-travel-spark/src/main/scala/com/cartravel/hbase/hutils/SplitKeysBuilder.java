package com.cartravel.hbase.hutils;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by zxc
 */
public class SplitKeysBuilder implements SplitKeyCalulaor {
    @Override
    public byte[][] getSplitKeys(int regionNum) {
        //1、构建前缀
        String[] keys = new String[regionNum] ;
        for(int i=0 ; i< regionNum;i++){
            String  pre = StringUtils.leftPad(String.valueOf(i) , 4 ,  "0") + "|";
            keys[i] = pre ;
        }

        //2构建 一个返回的数组
        byte[][] splitKeys = new byte[keys.length][];


        //使用一个有序的集合 ， 封装前缀
        TreeSet<byte[]> row = new TreeSet<byte[]>(Bytes.BYTES_COMPARATOR);
//        TreeSet<byte[]> row = new TreeSet<byte[]>();
        for(int i=0 ; i<keys.length;i++){
            row.add(Bytes.toBytes(keys[i]));
        }

        //迭代TreeSet ， 把值赋值给splitKeys
        final Iterator<byte[]> iterator = row.iterator();
        int i = 0 ;
        while (iterator.hasNext()){
            final byte[] tempRow = iterator.next();
            iterator.remove();
            splitKeys[i] = tempRow ;
            i++ ;
        }

        row.clear();
        row = null ;
        return splitKeys ;


    }
}
