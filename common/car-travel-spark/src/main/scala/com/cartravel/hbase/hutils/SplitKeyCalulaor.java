package com.cartravel.hbase.hutils;

/**
 * Created by zxc
 */
public interface SplitKeyCalulaor {
    public byte[][] getSplitKeys(int regionNum);
}
