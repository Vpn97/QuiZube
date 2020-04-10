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
import com.apkzube.quizube.model.registration.RegUserMst;
import com.apkzube.quizube.model.registration.User;
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

public class TakeUserIdViewModel extends AndroidViewModel {
    private Application application;

    private boolean isGoogleLogin;
    private boolean isFacebookLogin;
    private User user;
    private OnRegistrationEvent onRegistrationEvent;

    private MutableLiveData<String> userId=new MutableLiveData<>();
    public TakeUserIdViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }





    public void registerUser(View view){

        onRegistrationEvent.onRegistrationStart();
        HashMap<String, String> mQueryMap = new HashMap<>();


        RegUserMst userMst=new RegUserMst(userId.getValue(),user.getUserName(),"","",user.getEmail());
        ArrayList<Error> errors = isValidData(userMst);

        if (errors.isEmpty()) {

            mQueryMap.put("user_id", userMst.getUserId());
            mQueryMap.put("user_name", userMst.getUserName());
            mQueryMap.put("email", userMst.getEmailId());
            mQueryMap.put("password", userMst.getPassword());
            mQueryMap.put("is_facebook_login", String.valueOf(isFacebookLogin?1:0));
            mQueryMap.put("is_google_login", String.valueOf(isGoogleLogin?1:0));

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

        return errors;
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

    public boolean isGoogleLogin() {
        return isGoogleLogin;
    }

    public void setGoogleLogin(boolean googleLogin) {
        isGoogleLogin = googleLogin;
    }

    public boolean isFacebookLogin() {
        return isFacebookLogin;
    }

    public void setFacebookLogin(boolean facebookLogin) {
        isFacebookLogin = facebookLogin;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OnRegistrationEvent getOnRegistrationEvent() {
        return onRegistrationEvent;
    }

    public void setOnRegistrationEvent(OnRegistrationEvent onRegistrationEvent) {
        this.onRegistrationEvent = onRegistrationEvent;
    }
}
