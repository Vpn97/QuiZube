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
import com.apkzube.quizube.databinding.ActivityForgotPasswordBinding;
import com.apkzube.quizube.events.registration.OnSendOTPEvent;
import com.apkzube.quizube.response.registration.SendOTPResponse;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.Error;
import com.apkzube.quizube.util.ViewUtil;
import com.apkzube.quizube.viewmodel.registration.ForgotPasswordViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class ForgotPasswordActivity extends AppCompatActivity implements OnSendOTPEvent {


    ActivityForgotPasswordBinding mBinding;
    ForgotPasswordViewModel model;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        allocation();
        setEvent();
    }

    private void allocation() {
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_forgot_password);
        model= ViewModelProviders.of(this).get(ForgotPasswordViewModel.class);
        model.setSendOTPEvent(this);
        mBinding.setModel(model);
        snackbar=Snackbar.make(mBinding.getRoot(), "", Snackbar.LENGTH_LONG);
        mBinding.progressBar.setVisibility(View.GONE);
    }

    private void setEvent() {
        model.getEmail().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });

        mBinding.txtBackToLogin.setOnClickListener(view -> {
            finish();
        });
    }


    public void setSnackBar(String msg,String errorCode){

        snackbar.setText(msg);
        if(errorCode.equalsIgnoreCase(SEND_OTP_ERROR_CODE.OTP002.toString())){
            //not reg user
            snackbar.setAction(R.string.sign_up,view -> {
                startActivity(new Intent(ForgotPasswordActivity.this,SignUpActivity.class));
                finish();
            });
        }


        if(TextUtils.isEmpty(errorCode)|| errorCode.equalsIgnoreCase(SEND_OTP_ERROR_CODE.OTP003.toString()) || errorCode.equalsIgnoreCase(SEND_OTP_ERROR_CODE.OTP005.toString()) ){
            //responce fail
            snackbar.setAction(R.string.re_try,view -> {
                mBinding.btnSendMail.performClick();
            });
        }

        snackbar.show();
    }


    @Override
    public void onOTPReceiveSuccess(SendOTPResponse response) {
        mBinding.progressBar.setVisibility(View.GONE);
        ViewUtil.enableDisableView(mBinding.getRoot(),true);

        if(response.isStatus()){
            //Toast.makeText(this, response.getUid()+" : "+response.getOtp()+" : "+response.getEmail(), Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,VerifyEmailActivity.class);
            intent.putExtra(getString(R.string.send_email_response_obj),response);
            startActivity(intent);
        }

    }

    @Override
    public void onOTPReceiveFail(SendOTPResponse response) {

        mBinding.progressBar.setVisibility(View.GONE);
        ViewUtil.enableDisableView(mBinding.getRoot(),true);
      //  Log.d(Constants.TAG, "onOTPReceiveFail: "+new Gson().toJson(response));

        if(null!=response && !response.isStatus()){
            StringBuffer errorBuffer=new StringBuffer();
            if(null!=response.getErrors() && !response.getErrors().isEmpty()) {
                for (Error error : response.getErrors()) {
                    if(SEND_OTP_ERROR_CODE.OTP001.toString().equalsIgnoreCase(error.getCode()) || SEND_OTP_ERROR_CODE.OTP004.toString().equalsIgnoreCase(error.getCode())){
                        mBinding.txtEmail.setErrorEnabled(true);
                        mBinding.txtEmail.setError(error.getMessage());
                    }else if(SEND_OTP_ERROR_CODE.OTP005.toString().equalsIgnoreCase(error.getCode())){
                        setSnackBar(error.getMessage(),error.getCode());
                    }else if(SEND_OTP_ERROR_CODE.OTP002.toString().equalsIgnoreCase(error.getCode())){
                        //user not register
                        setSnackBar(error.getMessage(),error.getCode());
                    } else if(SEND_OTP_ERROR_CODE.OTP006.toString().equalsIgnoreCase(error.getCode())){
                        //email is not verified
                        //setSnackBar(error.getMessage(),error.getCode());
                    }else if(SEND_OTP_ERROR_CODE.OTP003.toString().equalsIgnoreCase(error.getCode())){
                        //email is not verified
                        setSnackBar(error.getMessage(),error.getCode());
                    }else{
                        errorBuffer.append(error.getMessage()).append("\n");
                    }
                }
                if(!TextUtils.isEmpty(errorBuffer)) {
                    setSnackBar(errorBuffer.toString(),"");
                }
            }

        }

    }

    @Override
    public void onSendOTPStart() {
        Log.d(Constants.TAG, "onSendOTPStart: ");
        mBinding.progressBar.setVisibility(View.VISIBLE);
        ViewUtil.enableDisableView(mBinding.getRoot(),false);
    }


    public static enum SEND_OTP_ERROR_CODE {
        OTP001,//enter user email id
        OTP002,//not reg user
        OTP003,//unable to send email please try again
        OTP004,//enter valid email
        OTP005,//fail response
        OTP006//email is not verified

    }
}
