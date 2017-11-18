package com.test;

import java.util.Date;

public class Service1 {


    public void sleep(Long millis) {
        try {
            Thread.sleep(millis.longValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Date getCurrentDate() {
        return new Date();
    }


}