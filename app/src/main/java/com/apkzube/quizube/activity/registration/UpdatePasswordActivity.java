package com.apkzube.quizube.activity.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apkzube.quizube.R;
import com.apkzube.quizube.databinding.ActivityUpdatePasswordBinding;
import com.apkzube.quizube.events.registration.OnPasswordUpdateEvent;
import com.apkzube.quizube.response.registration.SendOTPResponse;
import com.apkzube.quizube.response.registration.UpdatePasswordResponse;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.viewmodel.registration.UpdatePasswordViewModel;
import com.google.gson.Gson;

public class UpdatePasswordActivity extends AppCompatActivity implements OnPasswordUpdateEvent {

    private ActivityUpdatePasswordBinding mBinding;
    private UpdatePasswordViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        allocation();
        setEvent();

    }

    private void allocation() {

        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_update_password);
        model= ViewModelProviders.of(this).get(UpdatePasswordViewModel.class);
        mBinding.setModel(model);

        Intent intent = getIntent();
        if (null != intent) {
            SendOTPResponse otpResponse = intent.getParcelableExtra(getString(R.string.send_email_response_obj));
            Log.d(Constants.TAG, "allocation: " + new Gson().toJson(otpResponse));
            if (null != otpResponse && null != otpResponse.getOtp()) {
                model.setSendOTPResponse(otpResponse);/**/
            }
        }
    }

    private void setEvent() {

    }

    @Override
    public void onUpdatePasswordStart() {
        setVisibilityProgressbar(true);
    }

    @Override
    public void onUpdatePasswordSuccessful(UpdatePasswordResponse response) {
        setVisibilityProgressbar(false);

        if(response.isStatus()){
            Toast.makeText(this, "Password updated Successful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdatePasswordFail(UpdatePasswordResponse response) {

        setVisibilityProgressbar(false);

    }

    @Override
    public void onSamePassword(UpdatePasswordResponse response) {

    }

    public static enum ERROR_CODE {
        PASS001,//password cannot be empty
        PASS002,//email cannot be empty
        PASS003,//server error please try again
        PASS004, // dont use old password

        //local
        PASS005,// password dont match
        PASS006,// password length error
        PASS007,// password null
        PASS008,// email null
        PASS09 //response fail

    }



    public void setVisibilityProgressbar(boolean b) {
        if (b) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressBar.setVisibility(View.GONE);
        }
    }
}
