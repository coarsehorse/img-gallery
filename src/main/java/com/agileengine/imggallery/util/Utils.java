package com.agileengine.imggallery.util;

/**
 * Created by coarse_horse on 21/08/2020
 */
public class Utils {
    
    public static void sleep(Long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
