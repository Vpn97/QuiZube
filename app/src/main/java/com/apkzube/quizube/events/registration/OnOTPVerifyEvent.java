package com.apkzube.quizube.events.registration;

import com.apkzube.quizube.response.registration.VerifyOTPResponse;

public interface OnOTPVerifyEvent {

    public void onOTPVerifySuccess(VerifyOTPResponse response);
    public void onOTPVerifyFail(VerifyOTPResponse response);
    public void onOTPVerifyStart();
    public void onOTPExpired(VerifyOTPResponse response);
    public void onOTPNotMatch(VerifyOTPResponse response);

}
