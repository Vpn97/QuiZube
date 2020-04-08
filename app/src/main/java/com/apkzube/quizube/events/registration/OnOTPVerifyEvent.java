package com.apkzube.quizube.events.registration;

import com.apkzube.quizube.response.registration.RegistratoinResponse;
import com.apkzube.quizube.response.registration.SendOTPResponse;

public interface OnOTPVerifyEvent {

    public void onOTPVerifySuccess(SendOTPResponse sendOTPResponse);
    public void onOTPVerifyFail(SendOTPResponse sendOTPResponse);
    public void onOTPVerifyStart();
    public void onOTPExpired(SendOTPResponse sendOTPResponse);
    public void onOTPNotMatch(SendOTPResponse sendOTPResponse);

}
