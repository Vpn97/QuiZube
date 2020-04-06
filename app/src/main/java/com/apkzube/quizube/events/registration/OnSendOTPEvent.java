package com.apkzube.quizube.events.registration;

import com.apkzube.quizube.response.registration.SendOTPResponse;

public interface OnSendOTPEvent {


    public void onSendOTPSuccess(SendOTPResponse responce);
    public void onSendOTPFail(SendOTPResponse responce);
    public void onSendOTPStart();
}
