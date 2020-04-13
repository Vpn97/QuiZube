package com.apkzube.quizube.service.registration.impl;

import com.apkzube.quizube.service.registration.RegistrationService;
import com.apkzube.quizube.util.CommonRestURL;
import com.apkzube.quizube.util.GsonUtil;
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

        if(retrofit==null){

            retrofit=new Retrofit
                    .Builder()
                    .baseUrl(CommonRestURL.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(GsonUtil.getGson()))
                    .client(GsonUtil.getOkHttpClientBuilder().build())
                    .build();
        }

        return retrofit.create(RegistrationService.class);

    }

}
