package com.apkzube.quizube.service.impl;

import com.apkzube.quizube.service.UserRegistrationService;
import com.apkzube.quizube.util.CommonRestURL;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserRegistrationServiceImpl {

    private static Retrofit retrofit = null;

    public static UserRegistrationService getService(){

        if(retrofit==null){

            retrofit=new Retrofit
                    .Builder()
                    .baseUrl(CommonRestURL.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(UserRegistrationService.class);

    }

}
