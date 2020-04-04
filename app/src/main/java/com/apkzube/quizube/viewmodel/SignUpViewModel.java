package com.apkzube.quizube.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.apkzube.quizube.R;
import com.apkzube.quizube.activity.SignUp;
import com.apkzube.quizube.events.OnRegistrationEvent;
import com.apkzube.quizube.model.registration.RegUserMst;
import com.apkzube.quizube.response.registration.RegistratoinResponse;
import com.apkzube.quizube.service.UserRegistrationService;
import com.apkzube.quizube.service.impl.UserRegistrationServiceImpl;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.Error;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpViewModel extends AndroidViewModel {

    MutableLiveData<String> userId=new MutableLiveData<>();
    MutableLiveData<String>  userName=new MutableLiveData<>();
    MutableLiveData <String> email=new MutableLiveData<>();
    MutableLiveData <String> password=new MutableLiveData<>();
    MutableLiveData <String> confimPassword=new MutableLiveData<>();




    OnRegistrationEvent onRegistrationEvent;
    Application application;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }


    public void registerUser(View view) {

        onRegistrationEvent.onRegistrationStart();
        HashMap<String, String> mQueryMap = new HashMap<>();


        RegUserMst userMst=new RegUserMst(userId.getValue(),userName.getValue(),password.getValue(),confimPassword.getValue(),email.getValue());
        ArrayList<Error> errors = isValidData(userMst);

        if (userMst != null && errors.isEmpty()) {

            mQueryMap.put("user_id", userMst.getUserId());
            mQueryMap.put("user_name", userMst.getUserName());
            mQueryMap.put("email", userMst.getEmailId());
            mQueryMap.put("password", userMst.getPassword());

            UserRegistrationService service = UserRegistrationServiceImpl.getService();
            final Call<RegistratoinResponse> responseCall = service.registerUser(mQueryMap);
            responseCall.enqueue(new Callback<RegistratoinResponse>() {
                @Override
                public void onResponse(Call<RegistratoinResponse> call, Response<RegistratoinResponse> response) {
                    RegistratoinResponse mResponse = response.body();
                    Log.d(Constants.TAG, "onResponse: " + new Gson().toJson(response.body()));

                    if (mResponse.isStatus()) {
                        onRegistrationEvent.onRegistrationSuccess(mResponse);
                    } else {
                        onRegistrationEvent.onRegistrationFail(mResponse);
                    }
                }

                @Override
                public void onFailure(Call<RegistratoinResponse> call, Throwable t) {
                    RegistratoinResponse mResponse = new RegistratoinResponse();
                    mResponse.setStatus(false);
                    Error mError = new Error("REG009", t.getMessage(), "REG");
                    errors.add(mError);
                    mResponse.setErrors(errors);
                    onRegistrationEvent.onRegistrationFail(mResponse);
                }
            });

        } else {
            RegistratoinResponse mResponse = new RegistratoinResponse();
            mResponse.setStatus(false);
            mResponse.setErrors(errors);
            onRegistrationEvent.onRegistrationFail(mResponse);
        }

    }

    private ArrayList<Error> isValidData(RegUserMst userMst) {
        ArrayList<Error> errors = new ArrayList<>();

        if (null == userMst.getUserId() || TextUtils.isEmpty(userMst.getUserId())) {
            errors.add(new Error(SignUp.ERROR_CODE.REG001.toString(), application.getString(R.string.user_id_cannot_be_empty), "REG"));
        } else if (null != userMst.getUserId() && (userMst.getUserId().length() < 6 || userMst.getUserId().length() > 30)) {
            errors.add(new Error(SignUp.ERROR_CODE.REG001.toString(), application.getString(R.string.user_id_length_msg), "REG"));
        }


        if (null == userMst.getUserName() || TextUtils.isEmpty(userMst.getUserName())) {
            errors.add(new Error(SignUp.ERROR_CODE.REG002.toString(), application.getString(R.string.enter_valid_user_name), "REG"));
        }

        if (null == userMst.getEmailId() || TextUtils.isEmpty(userMst.getEmailId()) && !Patterns.EMAIL_ADDRESS.matcher(userMst.getEmailId()).matches()) {
            errors.add(new Error(SignUp.ERROR_CODE.REG003.toString(), application.getString(R.string.enter_email_msg), "REG"));
        }

        //password validation
        if (null == userMst.getPassword() || TextUtils.isEmpty(userMst.getPassword())) {
            errors.add(new Error(SignUp.ERROR_CODE.REG004.toString(), application.getString(R.string.password_can_be), "REG"));

        }

        if (null != userMst.getPassword() && (userMst.getPassword().length() > 6 || userMst.getPassword().length() < 20)) {

            if (null != userMst.getConfPassword() && !TextUtils.isEmpty(userMst.getConfPassword()) && !userMst.getPassword().equals(userMst.getConfPassword())) {
                errors.add(new Error(SignUp.ERROR_CODE.REG005.toString(), application.getString(R.string.pasword_dosenot_match), "REG"));
            }

        } else {
            errors.add(new Error(SignUp.ERROR_CODE.REG004.toString(), application.getString(R.string.password_length_msg), "REG"));
        }

        return errors;
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

    public MutableLiveData<String> getConfimPassword() {
        return confimPassword;
    }

    public void setConfimPassword(MutableLiveData<String> confimPassword) {
        this.confimPassword = confimPassword;
    }

    public OnRegistrationEvent getOnRegistrationEvent() {
        return onRegistrationEvent;
    }

    public void setOnRegistrationEvent(OnRegistrationEvent onRegistrationEvent) {
        this.onRegistrationEvent = onRegistrationEvent;
    }
}
