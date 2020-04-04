package com.apkzube.quizube.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.apkzube.quizube.R;
import com.apkzube.quizube.databinding.ActivitySignUpBinding;
import com.apkzube.quizube.events.OnRegistrationEvent;
import com.apkzube.quizube.response.registration.Count;
import com.apkzube.quizube.response.registration.RegistratoinResponse;
import com.apkzube.quizube.service.UserRegistrationService;
import com.apkzube.quizube.service.impl.UserRegistrationServiceImpl;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.Error;
import com.apkzube.quizube.viewmodel.SignUpViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements OnRegistrationEvent {

    private ActivitySignUpBinding signUpBinding;
    private SignUpViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        allocation();
        setEvent();

    }

    private void allocation() {
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        model = ViewModelProviders.of(this).get(SignUpViewModel.class);
        model.setOnRegistrationEvent(this);
        signUpBinding.setModel(model);
    }

    private void setEvent() {

        model.getUserId().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String userIdNew) {

                if (userIdNew != null && userIdNew.matches(Constants.USER_ID_REGEX) && userIdNew.length() >= 6 && userIdNew.length() < 30) {

                    UserRegistrationService registrationService = UserRegistrationServiceImpl.getService();
                    final Call<Count> countCall = registrationService.getIsValidUser(userIdNew);
                    countCall.enqueue(new Callback<Count>() {
                        @Override
                        public void onResponse(Call<Count> call, Response<Count> response) {
                            Count mCount = response.body();
                            if (null != mCount) {
                                if (mCount.getCount() > 0) {
                                    signUpBinding.txtUserId.setErrorEnabled(true);
                                    signUpBinding.txtUserId.setError(getString(R.string.user_id_already));
                                } else if (mCount.getCount() == 0) {
                                    signUpBinding.txtUserId.setErrorEnabled(false);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Count> call, Throwable t) {
                            Log.d(Constants.TAG, "onFailure: onFocusChange" + t.getMessage());
                        }
                    });

                } else {
                    Log.d(Constants.TAG, "onTextChanged: not valid");
                    if (userIdNew.matches(Constants.USER_ID_REGEX)) {
                        signUpBinding.txtUserId.setErrorEnabled(true);
                        signUpBinding.txtUserId.setError(getString(R.string.user_id_length_msg));
                    } else {
                        signUpBinding.txtUserId.setErrorEnabled(true);
                        signUpBinding.txtUserId.setError(getString(R.string.not_valid_user_id));
                    }
                }
            }
        });

        model.getUserName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String name) {
                if (TextUtils.isEmpty(name)) {
                    signUpBinding.txtUserName.setErrorEnabled(true);
                    signUpBinding.txtUserName.setError(getString(R.string.enter_valid_user_name));
                } else {
                    signUpBinding.txtUserName.setErrorEnabled(false);
                    signUpBinding.txtUserName.setError("");
                }
            }
        });

        model.getEmail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String email) {
                if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    signUpBinding.txtEmailId.setErrorEnabled(true);
                    signUpBinding.txtEmailId.setError(getString(R.string.enter_email_msg));
                } else {
                    signUpBinding.txtEmailId.setErrorEnabled(false);
                    signUpBinding.txtEmailId.setError("");
                }
            }
        });

        model.getPassword().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String password) {
                if (TextUtils.isEmpty(password)) {
                    signUpBinding.txtPassword.setErrorEnabled(true);
                    signUpBinding.txtPassword.setError(getString(R.string.password_can_be));
                } else if ((password.length() < 6 || password.length() > 20)) {
                    signUpBinding.txtPassword.setErrorEnabled(true);
                    signUpBinding.txtPassword.setError(getString(R.string.password_length_msg));
                } else {
                    signUpBinding.txtPassword.setErrorEnabled(false);
                    signUpBinding.txtPassword.setError("");
                }
            }
        });

        model.getConfirmPassword().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String password) {
                if (!password.equals(signUpBinding.txtPassword.getEditText().getText().toString())) {
                    signUpBinding.txtConfPassword.setErrorEnabled(true);
                    signUpBinding.txtConfPassword.setError(getString(R.string.pasword_dosenot_match));
                } else {
                    signUpBinding.txtConfPassword.setErrorEnabled(false);
                    signUpBinding.txtConfPassword.setError("");
                }
            }
        });

    }

    @Override
    public void onRegistrationSuccess(RegistratoinResponse response) {
        Toast.makeText(this, "Registration Success", Toast.LENGTH_SHORT).show();
        if (response.isStatus()) {
            finish();
        } else {
            StringBuilder errorMsg = new StringBuilder();
            if (null != response.getErrors()) {
                for (Error e : response.getErrors()) {
                    if (e.getCode().equalsIgnoreCase("REG005")) {
                        signUpBinding.txtEmailId.setErrorEnabled(true);
                        signUpBinding.txtEmailId.setError(e.getMessage());
                    } else {
                        errorMsg.append(e.getMessage()).append("\n");
                    }
                }
            } else {
                errorMsg.append(getString(R.string.server_error));
            }
            signUpBinding.txtError.setText(errorMsg);
            signUpBinding.txtError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRegistrationFail(RegistratoinResponse response) {
        Toast.makeText(this, "Registration Fail", Toast.LENGTH_SHORT).show();
        StringBuilder errorMsg = new StringBuilder();
        if (null != response.getErrors() && response.getErrors().size() > 0) {
            for (Error e : response.getErrors()) {

                if (e.getCode().equalsIgnoreCase(ERROR_CODE.REG001.toString())) {
                    signUpBinding.txtUserId.setErrorEnabled(true);
                    signUpBinding.txtUserId.setError(e.getMessage());

                } else if (e.getCode().equalsIgnoreCase(ERROR_CODE.REG002.toString())) {
                    signUpBinding.txtUserName.setErrorEnabled(true);
                    signUpBinding.txtUserName.setError(e.getMessage());

                } else if (e.getCode().equalsIgnoreCase(ERROR_CODE.REG003.toString())) {
                    signUpBinding.txtEmailId.setErrorEnabled(true);
                    signUpBinding.txtEmailId.setError(e.getMessage());

                } else if (e.getCode().equalsIgnoreCase(ERROR_CODE.REG004.toString())) {
                    signUpBinding.txtPassword.setErrorEnabled(true);
                    signUpBinding.txtPassword.setError(e.getMessage());

                } else if (e.getCode().equalsIgnoreCase(ERROR_CODE.REG005.toString())) {
                    signUpBinding.txtConfPassword.setErrorEnabled(true);
                    signUpBinding.txtConfPassword.setError(e.getMessage());

                } else if (e.getCode().equalsIgnoreCase(ERROR_CODE.REG006.toString())) {

                    if (signUpBinding.txtUserId.isErrorEnabled() && signUpBinding.txtUserId.getError().toString().equalsIgnoreCase(getString(R.string.user_id_already))) {
                        signUpBinding.txtUserId.setErrorEnabled(true);
                        signUpBinding.txtUserId.setError(getString(R.string.user_id_already));
                    } else {
                        signUpBinding.txtEmailId.setErrorEnabled(true);
                        signUpBinding.txtEmailId.setError(e.getMessage());
                    }

                } else {
                    errorMsg.append(e.getMessage()).append("\n");
                    signUpBinding.txtError.setText(errorMsg);
                    signUpBinding.txtError.setVisibility(View.VISIBLE);
                }
            }
        } else {
            errorMsg.append(getString(R.string.server_error));
            signUpBinding.txtError.setText(errorMsg);
            signUpBinding.txtError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRegistrationStart() {
        signUpBinding.txtError.setVisibility(View.GONE);
    }

    public static enum ERROR_CODE {
        REG001,//user id
        REG002,//user name
        REG003,//emial
        REG004,//password
        REG005,//confirm password
        REG006 //email exist
    }

}
