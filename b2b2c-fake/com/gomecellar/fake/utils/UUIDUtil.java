package com.gomecellar.fake.utils;

import java.util.UUID;

public class UUIDUtil {
    
    public static String generateUUID(){
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }
    
}
