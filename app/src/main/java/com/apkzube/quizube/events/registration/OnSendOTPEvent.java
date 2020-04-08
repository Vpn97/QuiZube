package com.apkzube.quizube.events.registration;

import com.apkzube.quizube.response.registration.SendOTPResponse;

public interface OnSendOTPEvent {


    public void onOTPReceiveSuccess(SendOTPResponse responce);
    public void onOTPReceiveFail(SendOTPResponse responce);
    public void onSendOTPStart();
}
