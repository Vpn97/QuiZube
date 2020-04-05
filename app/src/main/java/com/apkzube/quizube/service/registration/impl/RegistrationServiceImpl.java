package com.apkzube.quizube.service.registration.impl;

import com.apkzube.quizube.service.registration.RegistrationService;
import com.apkzube.quizube.util.CommonRestURL;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.text.DateFormat;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationServiceImpl {

    private static Retrofit retrofit = null;




    public static RegistrationService getService(){

        Gson builder=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().create();
        if(retrofit==null){

            retrofit=new Retrofit
                    .Builder()
                    .baseUrl(CommonRestURL.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(builder))
                    .build();
        }

        return retrofit.create(RegistrationService.class);

    }

}
