package com.apkzube.quizube.viewmodel.registration;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.apkzube.quizube.R;
import com.apkzube.quizube.activity.registration.UpdatePasswordActivity;
import com.apkzube.quizube.events.registration.OnPasswordUpdateEvent;
import com.apkzube.quizube.response.registration.SendOTPResponse;
import com.apkzube.quizube.response.registration.UpdatePasswordResponse;
import com.apkzube.quizube.service.registration.RegistrationService;
import com.apkzube.quizube.service.registration.impl.RegistrationServiceImpl;
import com.apkzube.quizube.util.Error;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePasswordViewModel extends AndroidViewModel {

    private Application application;
    private MutableLiveData<String> password=new MutableLiveData<>();
    private MutableLiveData<String> confirmPassword=new MutableLiveData<>();

    private SendOTPResponse sendOTPResponse;
    private OnPasswordUpdateEvent passwordUpdateEvent;


    public UpdatePasswordViewModel(@NonNull Application application) {
        super(application);
        this.application=application;
    }

    public void updatePassword(View view){

        passwordUpdateEvent.onUpdatePasswordStart();
        ArrayList<Error> errors=validateInput();
        if(errors.isEmpty()){
            if(null!=sendOTPResponse.getEmail()){
                HashMap<String,String> mQueryMap=new HashMap<>();
                mQueryMap.put("email",sendOTPResponse.getEmail());
                mQueryMap.put("password",password.getValue());
                RegistrationService service= RegistrationServiceImpl.getService();

                service.updatePassword(mQueryMap).enqueue(new Callback<UpdatePasswordResponse>() {
                    @Override
                    public void onResponse(Call<UpdatePasswordResponse> call, Response<UpdatePasswordResponse> response) {
                        if(null!=response.body() && response.body().isStatus()){
                            passwordUpdateEvent.onUpdatePasswordSuccessful(response.body());
                        }else{
                            passwordUpdateEvent.onUpdatePasswordFail(response.body());

                        }
                    }

                    @Override
                    public void onFailure(Call<UpdatePasswordResponse> call, Throwable t) {
                        ArrayList<Error> errors=new ArrayList<>();
                        errors.add(new Error(UpdatePasswordActivity.ERROR_CODE.PASS008.toString(), t.getMessage(), "REG"));
                        UpdatePasswordResponse response=new UpdatePasswordResponse();
                        response.setStatus(false);
                        response.setErrors(errors);
                        passwordUpdateEvent.onUpdatePasswordFail(response);
                    }
                });


            }else{
                errors=new ArrayList<>();
                errors.add(new Error(UpdatePasswordActivity.ERROR_CODE.PASS008.toString(), application.getString(R.string.server_error), "REG"));
                UpdatePasswordResponse response=new UpdatePasswordResponse();
                response.setStatus(false);
                response.setErrors(errors);
                passwordUpdateEvent.onUpdatePasswordFail(response);
            }
        }else{
            UpdatePasswordResponse response=new UpdatePasswordResponse();
            response.setStatus(false);
            response.setErrors(errors);
            passwordUpdateEvent.onUpdatePasswordFail(response);
        }


    }


    public ArrayList<Error> validateInput(){
        ArrayList<Error> errors=new ArrayList<>();
        //password validation
        if (null == password.getValue() || TextUtils.isEmpty(password.getValue())) {
            errors.add(new Error(UpdatePasswordActivity.ERROR_CODE.PASS001.toString(), application.getString(R.string.password_can_be), "REG"));

        }

        if (null == confirmPassword.getValue() || TextUtils.isEmpty(confirmPassword.getValue()) ) {
            errors.add(new Error(UpdatePasswordActivity.ERROR_CODE.PASS005.toString(), application.getString(R.string.pasword_dosenot_match), "REG"));
        }else if(!password.getValue().equals(confirmPassword.getValue())){
            errors.add(new Error(UpdatePasswordActivity.ERROR_CODE.PASS005.toString(), application.getString(R.string.pasword_dosenot_match), "REG"));
        }


        if (null != password.getValue() && (password.getValue().length() > 6 || password.getValue().length() < 20)) {

            if (null != confirmPassword.getValue() &&  !password.getValue().equals(confirmPassword.getValue())) {
                errors.add(new Error(UpdatePasswordActivity.ERROR_CODE.PASS005.toString(), application.getString(R.string.pasword_dosenot_match), "REG"));
            }

        } else {
            errors.add(new Error(UpdatePasswordActivity.ERROR_CODE.PASS006.toString(), application.getString(R.string.password_length_msg), "REG"));
        }


        return errors;
    }


    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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

    public SendOTPResponse getSendOTPResponse() {
        return sendOTPResponse;
    }

    public void setSendOTPResponse(SendOTPResponse sendOTPResponse) {
        this.sendOTPResponse = sendOTPResponse;
    }

    public OnPasswordUpdateEvent getPasswordUpdateEvent() {
        return passwordUpdateEvent;
    }

    public void setPasswordUpdateEvent(OnPasswordUpdateEvent passwordUpdateEvent) {
        this.passwordUpdateEvent = passwordUpdateEvent;
    }
}
