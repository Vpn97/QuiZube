package com.apkzube.quizube.events.registration;

import com.apkzube.quizube.response.registration.LoginResponse;

public interface OnLoginEvent {

    public void onLoginSuccess(LoginResponse response);
    public void onLoginFail(LoginResponse response);
    public  void onLoginStart();
}
