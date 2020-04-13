package com.apkzube.quizube.util;


import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String TAG = "ApkZube";
    public static final String YES= "Y";
    public static final String NO = "N";
    public static final String USER_ID_REGEX = "^[A-Za-z][\\w.-]+[a-zA-Z0-9]$";

    public static String LOGIN_TYPE="login_type";

    //for result code
    public static int LOGIN_GOOGLE=1;
    public static int LOGIN_FACEBOOK=2;
    public static int LOGIN_SIGN_IN=3;
    public static int SIGN_UP = 4;
    public static int NO_USER_LOGIN=5;
    public static int VERIFY_EMAIL = 6;
    public static int FORGOT_PASSWORD = 8;


    public static List<String> FB_PERMISSIONS_LIST = Arrays.asList("email","user_birthday","user_gender","user_posts");


}
