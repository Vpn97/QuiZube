package com.apkzube.quizube.service;

import com.apkzube.quizube.response.registration.Count;
import com.apkzube.quizube.response.registration.RegistratoinResponse;
import com.apkzube.quizube.util.CommonRestURL;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface UserRegistrationService {

    @POST(CommonRestURL.USER_ID_VALID_URL)
    Call<Count> getIsValidUser(@Query("user_id") String userId);

    @POST(CommonRestURL.REGISTER_USER_URL)
    Call<RegistratoinResponse> registerUser(@QueryMap HashMap<String,String> mQueryMap);

}
