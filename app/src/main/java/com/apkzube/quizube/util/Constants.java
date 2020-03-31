package com.apkzube.quizube.util;


import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String TAG = "ApkZube";
    public static final String USER_ID_REGEX = "^[A-Za-z][\\w.-]+[a-zA-Z0-9]$";

    public static String LOGIN_TYPE="login_type";
    public static int LOGIN_GOOGLE=1;
    public static int LOGIN_FACEBOOK=2;
    public static int LOGIN_SIGN_IN=3;
    public static int SIGN_UP = 4;
    public static int NO_USER_LOGIN=5;

    public static List<String> FB_PERIMISSON_LIST= Arrays.asList("email");


}
