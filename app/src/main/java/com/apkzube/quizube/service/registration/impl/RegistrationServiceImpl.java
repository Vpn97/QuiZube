package com.apkzube.quizube.service.registration.impl;

import com.apkzube.quizube.service.registration.RegistrationService;
import com.apkzube.quizube.util.CommonRestURL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationServiceImpl {

    private static Retrofit retrofit = null;




    public static RegistrationService getService(){

        Gson builder=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();

        OkHttpClient.Builder okHttpClient=new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        if(retrofit==null){

            retrofit=new Retrofit
                    .Builder()
                    .baseUrl(CommonRestURL.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(builder))
                    .client(okHttpClient.build())
                    .build();
        }

        return retrofit.create(RegistrationService.class);

    }

}
