package com.apkzube.quizube.viewmodel.registration;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.apkzube.quizube.R;
import com.apkzube.quizube.activity.registration.ForgotPasswordActivity;
import com.apkzube.quizube.events.registration.OnSendOTPEvent;
import com.apkzube.quizube.response.registration.SendOTPResponse;
import com.apkzube.quizube.service.registration.RegistrationService;
import com.apkzube.quizube.service.registration.impl.RegistrationServiceImpl;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.Error;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends AndroidViewModel {
    private Application application;
    private OnSendOTPEvent sendOTPEvent;
    private MutableLiveData<String> email=new MutableLiveData<>();

    public ForgotPasswordViewModel(@NonNull Application application) {
        super(application);
        this.application=application;
    }



    public void sendOTP(View view){
        Log.d(Constants.TAG, "sendOTP: "+email.getValue());

        sendOTPEvent.onSendOTPStart();
        ArrayList<Error> errors=validateInput();
        SendOTPResponse mSendOTPResponse;

        if(errors.isEmpty()){
            RegistrationService service= RegistrationServiceImpl.getService();
            HashMap<String, String> mQueryMap = new HashMap<>();
            mQueryMap.put("email", email.getValue());

            service.sendOTP(mQueryMap).enqueue(new Callback<SendOTPResponse>() {
                @Override
                public void onResponse(Call<SendOTPResponse> call, Response<SendOTPResponse> response) {
                    if(null!=response.body() && response.body().isStatus()){
                        sendOTPEvent.onOTPReceiveSuccess(response.body());
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

    private ArrayList<Error> validateInput() {
        ArrayList<Error> errors=new ArrayList<>();

        if (null == email.getValue() || TextUtils.isEmpty(email.getValue())) {
            errors.add(new Error(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP001.toString(), application.getString(R.string.insert_email), "REG"));
        }else if (null!=email.getValue() && !Patterns.EMAIL_ADDRESS.matcher(email.getValue()).matches()){
            errors.add(new Error(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP004.toString(), application.getString(R.string.enter_email_msg), "REG"));
        }

        return errors;

    }


    public OnSendOTPEvent getSendOTPEvent() {
        return sendOTPEvent;
    }

    public void setSendOTPEvent(OnSendOTPEvent sendOTPEvent) {
        this.sendOTPEvent = sendOTPEvent;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(MutableLiveData<String> email) {
        this.email = email;
    }
}
