package com.apkzube.quizube.activity.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.apkzube.quizube.R;
import com.apkzube.quizube.databinding.ActivityUpdatePasswordBinding;
import com.apkzube.quizube.events.registration.OnPasswordUpdateEvent;
import com.apkzube.quizube.response.registration.UpdatePasswordResponse;
import com.apkzube.quizube.response.registration.VerifyOTPResponse;
import com.apkzube.quizube.util.Error;
import com.apkzube.quizube.viewmodel.registration.UpdatePasswordViewModel;
import com.google.android.material.snackbar.Snackbar;

public class UpdatePasswordActivity extends AppCompatActivity implements OnPasswordUpdateEvent {

    private ActivityUpdatePasswordBinding mBinding;
    private UpdatePasswordViewModel model;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        allocation();
        setEvent();

    }

    private void allocation() {



        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_password);
        model = ViewModelProviders.of(this).get(UpdatePasswordViewModel.class);
        model.setPasswordUpdateEvent(this);
        Intent intent = getIntent();
        if (null != intent) {
            VerifyOTPResponse verifyOTPResponse = intent.getParcelableExtra(getString(R.string.verify_otp_response_obj));
            if (null != verifyOTPResponse && verifyOTPResponse.getEmail() !=null ) {
                model.setVerifyOTPResponse(verifyOTPResponse);/**/
            }else{
                Toast.makeText(this, getString(R.string.otp_expired), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        mBinding.setModel(model);




        snackbar=Snackbar.make(mBinding.getRoot(), "", Snackbar.LENGTH_LONG);


        mBinding.txtBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setEvent() {
        model.getPassword().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String password) {
                if (TextUtils.isEmpty(password)) {
                    mBinding.txtPassword.setErrorEnabled(true);
                    mBinding.txtPassword.setError(getString(R.string.password_can_be));
                } else if ((password.length() < 6 || password.length() > 20)) {
                    mBinding.txtPassword.setErrorEnabled(true);
                    mBinding.txtPassword.setError(getString(R.string.password_length_msg));
                } else {
                    mBinding.txtPassword.setErrorEnabled(false);
                    mBinding.txtPassword.setError("");
                }
            }
        });

        model.getConfirmPassword().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String password) {
                if (!password.equals(mBinding.txtPassword.getEditText().getText().toString())) {
                    mBinding.txtConfirmPassword.setErrorEnabled(true);
                    mBinding.txtConfirmPassword.setError(getString(R.string.password_does_not_match));
                } else {
                    mBinding.txtConfirmPassword.setErrorEnabled(false);
                    mBinding.txtConfirmPassword.setError("");
                }
            }
        });
    }

    @Override
    public void onUpdatePasswordStart() {
        setVisibilityProgressbar(true);
    }

    @Override
    public void onUpdatePasswordSuccessful(UpdatePasswordResponse response) {
        setVisibilityProgressbar(false);

        if (response.isStatus()) {
            Toast.makeText(this, getString(R.string.password_update_successfully), Toast.LENGTH_SHORT).show();
            Intent intent=new Intent();
            intent.putExtra(getString(R.string.user_id_key),response.getEmail());
            setResult(RESULT_OK,intent);
            finish();
        }else{
            setSnackBar(getString(R.string.server_error), ERROR_CODE.PASS002.toString());
        }
    }

    @Override
    public void onUpdatePasswordFail(UpdatePasswordResponse response) {

        setVisibilityProgressbar(false);

        //  Log.d(Constants.TAG, "onOTPReceiveFail: "+new Gson().toJson(response));

        if (null != response && !response.isStatus()) {
            StringBuffer errorBuffer = new StringBuffer();
            if (null != response.getErrors() && !response.getErrors().isEmpty()) {
                for (Error error : response.getErrors()) {

                    switch (error.toIntValue()) {

                        case 1:
                        case 6:
                        case 7:
                            mBinding.txtPassword.setErrorEnabled(true);
                            mBinding.txtPassword.setError(error.getMessage());

                            break;

                        case 2:
                        case 3:
                        case 9:
                        case 8:
                            setSnackBar(error.getMessage(),ERROR_CODE.PASS002.toString());
                            break;

                        case 5:
                            mBinding.txtConfirmPassword.setErrorEnabled(true);
                            mBinding.txtConfirmPassword.setError(error.getMessage());
                            break;

                        default:
                            errorBuffer.append(error.getMessage());
                            break;
                    }
                }

                if (!TextUtils.isEmpty(errorBuffer)) {
                    setSnackBar(errorBuffer.toString(), "");
                }
            }


        }

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
        PASS009;//response fail
    }


    public void setVisibilityProgressbar(boolean b) {
        if (b) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressBar.setVisibility(View.GONE);
        }
    }


    public void setSnackBar(String msg,String errorCode){

        snackbar.setText(msg);

        if(errorCode.equalsIgnoreCase(ERROR_CODE.PASS002.toString())){
            //responce fail
            snackbar.setAction(R.string.re_try,view -> {
                mBinding.btnUpdate.performClick();
            });
        }

        snackbar.show();
    }
}
