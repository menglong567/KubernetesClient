package com.ml.kubernetes.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * @author luoliudan
 */
public class CommonUtil {
    private static final CommonUtil instance = new CommonUtil();
    private static Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");

    private CommonUtil() {
    }

    public static CommonUtil getInstance() {
        return instance;
    }

    public boolean isInteger(String str) {
        return pattern.matcher(str).matches();
    }

    /**
     * @param str
     * @return
     */
    public boolean isValidLong(String str) {
        try {
            long _v = Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param str
     * @return
     */
    public boolean isValidFloat(String str) {
        try {
            Float _v = Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param str
     * @return
     */
    public boolean isValidDouble(String str) {
        try {
            Double _v = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
