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
import com.apkzube.quizube.activity.registration.SignUpActivity;
import com.apkzube.quizube.events.registration.OnRegistrationEvent;
import com.apkzube.quizube.events.registration.OnSendOTPEvent;
import com.apkzube.quizube.model.registration.RegUserMst;
import com.apkzube.quizube.response.registration.RegistrationResponse;
import com.apkzube.quizube.service.registration.RegistrationService;
import com.apkzube.quizube.service.registration.impl.RegistrationServiceImpl;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.Error;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpViewModel extends AndroidViewModel {

    private MutableLiveData<String> userId=new MutableLiveData<>();
    private MutableLiveData<String>  userName=new MutableLiveData<>();
    private MutableLiveData <String> email=new MutableLiveData<>();
    private  MutableLiveData <String> password=new MutableLiveData<>();
    private  MutableLiveData <String> confirmPassword =new MutableLiveData<>();

    private OnSendOTPEvent sendOTPEvent;


    private OnRegistrationEvent onRegistrationEvent;
    private  Application application;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }


    public void registerUser(View view) {

        onRegistrationEvent.onRegistrationStart();
        HashMap<String, String> mQueryMap = new HashMap<>();


        RegUserMst userMst=new RegUserMst(userId.getValue(),userName.getValue(),password.getValue(), confirmPassword.getValue(),email.getValue());
        ArrayList<Error> errors = isValidData(userMst);

        if (userMst != null && errors.isEmpty()) {

            mQueryMap.put("user_id", userMst.getUserId());
            mQueryMap.put("user_name", userMst.getUserName());
            mQueryMap.put("email", userMst.getEmailId());
            mQueryMap.put("password", userMst.getPassword());
            mQueryMap.put("is_facebook_login", "0");
            mQueryMap.put("is_google_login", "0");

            RegistrationService service = RegistrationServiceImpl.getService();
            final Call<RegistrationResponse> responseCall = service.registerUser(mQueryMap);
            responseCall.enqueue(new Callback<RegistrationResponse>() {
                @Override
                public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                    RegistrationResponse mResponse = response.body();
                    Log.d(Constants.TAG, "onResponse: " + new Gson().toJson(response.body()));

                    if (mResponse.isStatus()) {
                        onRegistrationEvent.onRegistrationSuccess(mResponse);
                    } else {
                        onRegistrationEvent.onRegistrationFail(mResponse);
                    }
                }

                @Override
                public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                    RegistrationResponse mResponse = new RegistrationResponse();
                    mResponse.setStatus(false);
                    Error mError = new Error("REG009", t.getMessage(), "REG");
                    errors.add(mError);
                    mResponse.setErrors(errors);
                    onRegistrationEvent.onRegistrationFail(mResponse);
                }
            });

        } else {
            RegistrationResponse mResponse = new RegistrationResponse();
            mResponse.setStatus(false);
            mResponse.setErrors(errors);
            onRegistrationEvent.onRegistrationFail(mResponse);
        }

    }


    private ArrayList<Error> isValidData(RegUserMst userMst) {
        ArrayList<Error> errors = new ArrayList<>();

        if (null == userMst.getUserId() || TextUtils.isEmpty(userMst.getUserId())) {
            errors.add(new Error(SignUpActivity.ERROR_CODE.REG001.toString(), application.getString(R.string.user_id_cannot_be_empty), "REG"));
        } else if (null != userMst.getUserId() && (userMst.getUserId().length() < 6 || userMst.getUserId().length() > 30)) {
            errors.add(new Error(SignUpActivity.ERROR_CODE.REG001.toString(), application.getString(R.string.user_id_length_msg), "REG"));
        }


        if (null == userMst.getUserName() || TextUtils.isEmpty(userMst.getUserName())) {
            errors.add(new Error(SignUpActivity.ERROR_CODE.REG002.toString(), application.getString(R.string.enter_valid_user_name), "REG"));
        }

        if (null == userMst.getEmailId() || TextUtils.isEmpty(userMst.getEmailId()) && !Patterns.EMAIL_ADDRESS.matcher(userMst.getEmailId()).matches()) {
            errors.add(new Error(SignUpActivity.ERROR_CODE.REG003.toString(), application.getString(R.string.enter_email_msg), "REG"));
        }

        //password validation
        if (null == userMst.getPassword() || TextUtils.isEmpty(userMst.getPassword())) {
            errors.add(new Error(SignUpActivity.ERROR_CODE.REG004.toString(), application.getString(R.string.password_can_be), "REG"));

        }

        if (null == userMst.getConfPassword() || TextUtils.isEmpty(userMst.getConfPassword())) {
            errors.add(new Error(SignUpActivity.ERROR_CODE.REG005.toString(), application.getString(R.string.pasword_dosenot_match), "REG"));
        }else if(!userMst.getPassword().equals(userMst.getConfPassword())){
            errors.add(new Error(SignUpActivity.ERROR_CODE.REG005.toString(), application.getString(R.string.pasword_dosenot_match), "REG"));
        }


        if (null != userMst.getPassword() && (userMst.getPassword().length() > 6 || userMst.getPassword().length() < 20)) {

            if (null != userMst.getConfPassword() && !TextUtils.isEmpty(userMst.getConfPassword()) && !userMst.getPassword().equals(userMst.getConfPassword())) {
                errors.add(new Error(SignUpActivity.ERROR_CODE.REG005.toString(), application.getString(R.string.pasword_dosenot_match), "REG"));
            }

        } else {
            errors.add(new Error(SignUpActivity.ERROR_CODE.REG004.toString(), application.getString(R.string.password_length_msg), "REG"));
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

    public MutableLiveData<String> getUserId() {
        return userId;
    }

    public void setUserId(MutableLiveData<String> userId) {
        this.userId = userId;
    }

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public void setUserName(MutableLiveData<String> userName) {
        this.userName = userName;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(MutableLiveData<String> email) {
        this.email = email;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MutableLiveData<String> getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(MutableLiveData<String> confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public OnRegistrationEvent getOnRegistrationEvent() {
        return onRegistrationEvent;
    }

    public void setOnRegistrationEvent(OnRegistrationEvent onRegistrationEvent) {
        this.onRegistrationEvent = onRegistrationEvent;
    }
}
