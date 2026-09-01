package com.temp.demo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {
    public static final String API_PATH = "/api";
    public static final String PUBLIC_PATH = "/public";
    public static final String IMAGE_PATH = "/image";
    public static final String AUTHENTICATE_PATH = "/authenticate";
    public static final String FORGET_PASSWORD_PATH = "/forget_password";
    public static final String REDIRECT_PATH = "/redirect";
    public static final String RESET_PASSWORD_PATH = "/reset_password";
    public static final String STAFF_PATH = "/staff";
    public static final String PRODUCT_PATH = "/product";
    public static final String REGISTER_PATH = "/register";
    public static final String CHANGE_PASSWORD_PATH = "/change_password";
    public static final String CHANGE_PROFILE_PATH = "/change_profile";
    public static final String CREATE_PATH = "/create";
    public static final String DELETE_PATH = "/delete";
    public static final String UPDATE_PATH = "/update";
    public static final String FILE = "/file";
    public static final String IMAGE = "/image";
    public static final String COUNTRY = "/country";
    public static final String CITY = "/city";
    public static final String ADDRESS = "/address";
    public static final String GET_PATH = "/get";

    public static final SimpleDateFormat FORMAT_1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static String getCurrentTimestamp(SimpleDateFormat format) {
        return format.format(new Date());
    }

    public static String getTimestamp(SimpleDateFormat format, Long timestamp, boolean inMillis) {
        return inMillis ? format.format(new Date(timestamp)): format.format(new Date(timestamp * 1000));
    }

    public static long getTimestamp(boolean inMillis) {
        long timestamp = new Date().getTime();
        return inMillis ? timestamp : timestamp / 1000L;
    }
}
