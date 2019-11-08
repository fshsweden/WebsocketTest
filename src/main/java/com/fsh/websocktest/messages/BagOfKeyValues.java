package com.fsh.websocktest.messages;

import java.util.HashMap;
import java.util.Map;

public class BagOfKeyValues {
    private Map<String,String> keyValues = new HashMap<String,String>();

    public void setKeyValue(final String key, final String value) {
        keyValues.put(key, value);
    }

    public String getKeyValue(final String key) {
        return keyValues.get(key) != null ? keyValues.get(key) : "";
    }

    public void debug() {
        System.out.println("------------------BAG-----------------------");
        keyValues.forEach((k,v) -> {
            System.out.println("Key " + k + " has value " + v);
        });

        System.out.println("--------------------------------------------");
    }
}
