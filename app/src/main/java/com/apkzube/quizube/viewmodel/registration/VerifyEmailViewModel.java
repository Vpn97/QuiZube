package com.apkzube.quizube.viewmodel.registration;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.apkzube.quizube.activity.registration.ForgotPasswordActivity;
import com.apkzube.quizube.events.registration.OnOTPVerifyEvent;
import com.apkzube.quizube.events.registration.OnSendOTPEvent;
import com.apkzube.quizube.response.registration.SendOTPResponse;
import com.apkzube.quizube.service.registration.RegistrationService;
import com.apkzube.quizube.service.registration.impl.RegistrationServiceImpl;
import com.apkzube.quizube.util.Error;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailViewModel extends AndroidViewModel {

    private MutableLiveData<String> otp1=new MutableLiveData<>();
    private MutableLiveData<String> otp2=new MutableLiveData<>();
    private MutableLiveData<String> otp3=new MutableLiveData<>();
    private MutableLiveData<String> otp4=new MutableLiveData<>();
    private MutableLiveData<String> otp5=new MutableLiveData<>();
    private MutableLiveData<String> otp6=new MutableLiveData<>();
    private MutableLiveData<String> count=new MutableLiveData<>();
    private SendOTPResponse otpResponse;

    private Application application;

    //events
    private OnSendOTPEvent sendOTPEvent;
    private OnOTPVerifyEvent onOTPVerifyEvent;

    public VerifyEmailViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

    }

    public void verifyOTP(View view){
        onOTPVerifyEvent.onOTPVerifyStart();

        if(null!=otpResponse){
            String inputOTP=String.valueOf(otp1.getValue()+otp2.getValue()+otp3.getValue()+otp4.getValue()+otp5.getValue()+otp6.getValue());
            if(otpResponse.isExpired()){
                //otp expired
                onOTPVerifyEvent.onOTPExpired(otpResponse);
            }else {
                if (otpResponse.getOtp().equalsIgnoreCase(inputOTP)) {
                    onOTPVerifyEvent.onOTPVerifySuccess(otpResponse);
                } else {
                    onOTPVerifyEvent.onOTPNotMatch(otpResponse);
                }
            }
        }
    }


    public void sendOTP(View view){
        //Log.d(Constants.TAG, "sendOTP: "+email.getValue());

        sendOTPEvent.onSendOTPStart();
        ArrayList<Error> errors=new ArrayList<>();
        SendOTPResponse mSendOTPResponse;

        if(errors.isEmpty()){
            RegistrationService service= RegistrationServiceImpl.getService();
            HashMap<String, String> mQueryMap = new HashMap<>();
            mQueryMap.put("email", otpResponse.getEmail());

            service.sendOTP(mQueryMap).enqueue(new Callback<SendOTPResponse>() {
                @Override
                public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                    if(null!=response.body() && response.body().isStatus()){
                        sendOTPEvent.onOTPReceiveSuccess(response.body());
                        otpResponse =response.body();
                    }else{
                        sendOTPEvent.onOTPReceiveFail(response.body());
                    }
                }

                @Override
                public void onFailure(Call<SendOTPResponse> call, Throwable t) {
                    SendOTPResponse mResponse=new SendOTPResponse();
                    mResponse.setStatus(false);
                    Error mError = new Error(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP005.toString(), t.getMessage(), "REG");
                    errors.add(mError);
                    mResponse.setErrors(errors);
                    sendOTPEvent.onOTPReceiveFail(mResponse);
                }
            });

        }else{
            mSendOTPResponse=new SendOTPResponse();
            mSendOTPResponse.setStatus(false);
            mSendOTPResponse.setErrors(errors);
            sendOTPEvent.onOTPReceiveFail(mSendOTPResponse);
        }
    }


    public MutableLiveData<String> getOtp1() {
        return otp1;
    }

    public void setOtp1(MutableLiveData<String> otp1) {
        this.otp1 = otp1;
    }

    public MutableLiveData<String> getOtp2() {
        return otp2;
    }

    public void setOtp2(MutableLiveData<String> otp2) {
        this.otp2 = otp2;
    }

    public MutableLiveData<String> getOtp3() {
        return otp3;
    }

    public void setOtp3(MutableLiveData<String> otp3) {
        this.otp3 = otp3;
    }

    public MutableLiveData<String> getOtp4() {
        return otp4;
    }

    public void setOtp4(MutableLiveData<String> otp4) {
        this.otp4 = otp4;
    }

    public MutableLiveData<String> getOtp5() {
        return otp5;
    }

    public void setOtp5(MutableLiveData<String> otp5) {
        this.otp5 = otp5;
    }

    public MutableLiveData<String> getOtp6() {
        return otp6;
    }

    public void setOtp6(MutableLiveData<String> otp6) {
        this.otp6 = otp6;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public SendOTPResponse getOtpResponse() {
        return otpResponse;
    }

    public OnSendOTPEvent getSendOTPEvent() {
        return sendOTPEvent;
    }

    public void setSendOTPEvent(OnSendOTPEvent sendOTPEvent) {
        this.sendOTPEvent = sendOTPEvent;
    }

    public OnOTPVerifyEvent getOnOTPVerifyEvent() {
        return onOTPVerifyEvent;
    }

    public void setOnOTPVerifyEvent(OnOTPVerifyEvent onOTPVerifyEvent) {
        this.onOTPVerifyEvent = onOTPVerifyEvent;
    }

    public void setOtpResponse(SendOTPResponse otpResponse) {
        this.otpResponse = otpResponse;
    }

    public MutableLiveData<String> getCount() {
        return count;
    }

    public void setCount(MutableLiveData<String> count) {
        this.count = count;
    }
}
