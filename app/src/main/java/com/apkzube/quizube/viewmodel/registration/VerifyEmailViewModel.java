package com.apkzube.quizube.viewmodel.registration;

import android.app.Application;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.apkzube.quizube.R;
import com.apkzube.quizube.response.registration.SendOTPResponse;

public class VerifyEmailViewModel extends AndroidViewModel {

    private MutableLiveData<String> otp1=new MutableLiveData<>();
    private MutableLiveData<String> otp2=new MutableLiveData<>();
    private MutableLiveData<String> otp3=new MutableLiveData<>();
    private MutableLiveData<String> otp4=new MutableLiveData<>();
    private MutableLiveData<String> otp5=new MutableLiveData<>();
    private MutableLiveData<String> otp6=new MutableLiveData<>();
    private SendOTPResponse otpResponse;

    private Application application;


    public VerifyEmailViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

    }

    public void verifyOTP(View view){
        if(null!=otpResponse){
            String inputOTP=String.valueOf(otp1.getValue()+otp2.getValue()+otp3.getValue()+otp4.getValue()+otp5.getValue()+otp6.getValue());
            if(otpResponse.getOtp().equalsIgnoreCase(inputOTP)){
                Toast.makeText(application, application.getString(R.string.otp_verify_sucessfully), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(application, application.getString(R.string.otp_does_not_match), Toast.LENGTH_SHORT).show();
            }
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

    public void setOtpResponse(SendOTPResponse otpResponse) {
        this.otpResponse = otpResponse;
    }
}
