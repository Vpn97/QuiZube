package com.apkzube.quizube.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.apkzube.quizube.R;
import com.apkzube.quizube.activity.LoginActivity;
import com.apkzube.quizube.events.registration.OnLoginEvent;
import com.apkzube.quizube.response.registration.LoginResponse;
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

public class LoginViewModel extends AndroidViewModel {

    private Application application;
    private MutableLiveData<String> userIdEmail = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private OnLoginEvent loginEvent;


    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public MutableLiveData<String> getUserIdEmail() {
        return userIdEmail;
    }

    public void setUserIdEmail(MutableLiveData<String> userIdEmail) {
        this.userIdEmail = userIdEmail;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }


    public void signIn(View view) {
        loginEvent.onLoginStart();

        ArrayList<Error> errors = validateInput();
        LoginResponse mLoginResponse;

        if (errors.isEmpty()) {

            RegistrationService registrationService = RegistrationServiceImpl.getService();
            HashMap<String, String> mQueryMap = new HashMap<>();
            mQueryMap.put("user_id", userIdEmail.getValue());
            mQueryMap.put("password", password.getValue());

            if (Patterns.EMAIL_ADDRESS.matcher(userIdEmail.getValue()).matches()) {
                mQueryMap.put("is_email", Constants.YES);
            } else {
                mQueryMap.put("is_email", Constants.NO);
            }

            registrationService.loginRequest(mQueryMap).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    Log.d(Constants.TAG, "onResponse: "+(new Gson().toJson(response.body())));
                    if (null != response.body() && response.body().isStatus()) {
                        loginEvent.onLoginSuccess(response.body());
                    } else {
                        loginEvent.onLoginFail(response.body());
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    LoginResponse mResponse = new LoginResponse();
                    mResponse.setStatus(false);
                    Error mError = new Error(LoginActivity.LOGIN_EROR_CODE.REG012.toString(), t.getMessage(), "REG");
                    errors.add(mError);
                    mResponse.setErrors(errors);
                    loginEvent.onLoginFail(mResponse);
                }
            });

        } else {
            mLoginResponse = new LoginResponse();
            mLoginResponse.setStatus(false);
            mLoginResponse.setErrors(errors);
            loginEvent.onLoginFail(mLoginResponse);

        }

    }

    private ArrayList<Error> validateInput() {
        ArrayList<Error> errors = new ArrayList<>();

        if (null == userIdEmail.getValue() || TextUtils.isEmpty(userIdEmail.getValue())) {
            errors.add(new Error(LoginActivity.LOGIN_EROR_CODE.REG001.toString(), application.getString(R.string.insert_user_id_password), "REG"));
        }

        //password validation
        if (null == password.getValue() || TextUtils.isEmpty(password.getValue())) {
            errors.add(new Error(LoginActivity.LOGIN_EROR_CODE.REG002.toString(), application.getString(R.string.enter_password), "REG"));
        }

        return errors;
    }


    public OnLoginEvent getLoginEvent() {
        return loginEvent;
    }

    public void setLoginEvent(OnLoginEvent loginEvent) {
        this.loginEvent = loginEvent;
    }
}
