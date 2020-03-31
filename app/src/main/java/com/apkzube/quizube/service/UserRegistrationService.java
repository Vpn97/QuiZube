package com.apkzube.quizube.service;

import com.apkzube.quizube.model.Count;
import com.apkzube.quizube.util.CommonRestURL;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserRegistrationService {

    @POST(CommonRestURL.USER_ID_VALID_URL)
    Call<Count> getIsValidUser(@Query("user_id") String userId);
}
