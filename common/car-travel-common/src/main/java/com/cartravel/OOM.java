package com.cartravel;

import java.util.ArrayList;
import java.util.List;

/**
 * -XX:+ PrintGCDetails
 */
public class OOM {
    private static final int _1kb = 1024 ;

    byte[] data = new byte[_1kb];
}

class Tests{
    public static void main(String[] args) {
        List<OOM> list = new ArrayList<OOM>();
        while (true){
            list.add(new OOM());
        }
    }
}
