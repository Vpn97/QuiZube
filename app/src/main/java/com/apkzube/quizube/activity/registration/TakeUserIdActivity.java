package com.apkzube.quizube.activity.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apkzube.quizube.R;
import com.apkzube.quizube.databinding.ActivityTakeUserIdBinding;
import com.apkzube.quizube.events.registration.OnRegistrationEvent;
import com.apkzube.quizube.model.registration.User;
import com.apkzube.quizube.response.registration.Count;
import com.apkzube.quizube.response.registration.RegistrationResponse;
import com.apkzube.quizube.service.registration.RegistrationService;
import com.apkzube.quizube.service.registration.impl.RegistrationServiceImpl;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.Error;
import com.apkzube.quizube.util.ViewUtil;
import com.apkzube.quizube.viewmodel.registration.TakeUserIdViewModel;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TakeUserIdActivity extends AppCompatActivity implements OnRegistrationEvent {


    private ActivityTakeUserIdBinding mBinding;
    private TakeUserIdViewModel model;
    private boolean isGoogleLogin;
    private boolean isFacebookLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_user_id);
        allocation();
        setEvent();
    }

    private void allocation() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_take_user_id);
        model = ViewModelProviders.of(this).get(TakeUserIdViewModel.class);

        Intent intent = getIntent();
        if (null != intent) {
            User user = intent.getParcelableExtra(getString(R.string.user_obj_key));
            isGoogleLogin = user.isGoogleLogin();
            isFacebookLogin = user.isFacebookLogin();
            model.setGoogleLogin(isGoogleLogin);
            model.setFacebookLogin(isFacebookLogin);
            model.setUser(user);
            Log.d(Constants.TAG, "allocation: " + new Gson().toJson(user));
        }
        model.setOnRegistrationEvent(this);
        mBinding.setModel(model);
    }

    private void setEvent() {

        model.getUserId().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String userIdNew) {

                if (userIdNew != null && userIdNew.matches(Constants.USER_ID_REGEX) && userIdNew.length() >= 6 && userIdNew.length() < 30) {

                    RegistrationService registrationService = RegistrationServiceImpl.getService();
                    final Call<Count> countCall = registrationService.getIsValidUser(userIdNew);
                    countCall.enqueue(new Callback<Count>() {
                        @Override
                        public void onResponse(Call<Count> call, Response<Count> response) {
                            Count mCount = response.body();
                            if (null != mCount) {
                                if (mCount.getCount() > 0) {
                                    mBinding.txtUserId.setErrorEnabled(true);
                                    mBinding.txtUserId.setError(getString(R.string.user_id_already));
                                } else if (mCount.getCount() == 0) {
                                    mBinding.txtUserId.setErrorEnabled(false);
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
                        mBinding.txtUserId.setErrorEnabled(true);
                        mBinding.txtUserId.setError(getString(R.string.user_id_length_msg));
                    } else {
                        mBinding.txtUserId.setErrorEnabled(true);
                        mBinding.txtUserId.setError(getString(R.string.not_valid_user_id));
                    }
                }
            }
        });

    }

    @Override
    public void onRegistrationSuccess(RegistrationResponse response) {
        ViewUtil.enableDisableView(mBinding.getRoot(), true);
        setVisibilityProgressbar(false);

        if(response.isStatus()) {
            Intent intent = new Intent();
            if (isGoogleLogin) {
                intent.putExtra(getString(R.string.is_google_login_key), true);
            } else if (isFacebookLogin) {
                intent.putExtra(getString(R.string.is_facebook_login_key), true);
            }
            intent.putExtra(getString(R.string.user_obj_key),response.getUser());
            setResult(Constants.SIGN_UP, intent);
            finish();
        }
    }

    @Override
    public void onRegistrationFail(RegistrationResponse response) {
        ViewUtil.enableDisableView(mBinding.getRoot(), true);
        setVisibilityProgressbar(false);
        //Toast.makeText(this, "Registration Fail", Toast.LENGTH_SHORT).show();
        StringBuilder errorMsg = new StringBuilder();
        if (null != response.getErrors() && response.getErrors().size() > 0) {
            for (Error e : response.getErrors()) {

                if (e.getCode().equalsIgnoreCase(SignUpActivity.ERROR_CODE.REG001.toString())) {
                    mBinding.txtUserId.setErrorEnabled(true);
                    mBinding.txtUserId.setError(e.getMessage());

                } else {
                    errorMsg.append(e.getMessage()).append("\n");
                    //mBinding.txtError.setText(errorMsg);
                    // mBinding.txtError.setVisibility(View.VISIBLE);
                }
            }
        } else {
            errorMsg.append(getString(R.string.server_error));

        }

        if (!TextUtils.isEmpty(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRegistrationStart() {
        ViewUtil.enableDisableView(mBinding.getRoot(), false);
        setVisibilityProgressbar(true);

    }


    public void setVisibilityProgressbar(boolean b) {
        if (b) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressBar.setVisibility(View.GONE);
        }
    }

}
