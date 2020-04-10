package com.apkzube.quizube.events.registration;

import com.apkzube.quizube.response.registration.LoginResponse;

public interface OnValidateAuthEvent {

    public void onValidateAuthStart();
    public void onValidateAuthSuccess(LoginResponse response,int LoginCode);
    public void onValidateAuthFail(LoginResponse response);
    public void onUserNotRegistered(LoginResponse response, String email, String name, int loginCode);


}
