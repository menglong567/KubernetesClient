package com.ml.kubernetes.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liudan_Luo
 */
public class PlaceHolderUtil {
    private static final PlaceHolderUtil instance = new PlaceHolderUtil();
    private static final Map valuesMap = new HashMap();

    public static PlaceHolderUtil getInstance() {
        return instance;
    }

    private PlaceHolderUtil() {
    }

    public Map getVlauesMap() {
        return valuesMap;
    }

    public void clearValues() {
        valuesMap.clear();
    }

    public void addValues(String key, String value) {
        valuesMap.put(key, value);
    }

    public void delValues(String key) {
        valuesMap.remove(key);
    }

    public void replaceValue(String key, String value) {
        valuesMap.replace(key, value);
    }
}
