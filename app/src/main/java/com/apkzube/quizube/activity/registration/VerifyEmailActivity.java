package com.apkzube.quizube.activity.registration;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.apkzube.quizube.R;
import com.apkzube.quizube.databinding.ActivityVerifyEmailBinding;
import com.apkzube.quizube.events.registration.OnOTPVerifyEvent;
import com.apkzube.quizube.events.registration.OnSendOTPEvent;
import com.apkzube.quizube.model.registration.User;
import com.apkzube.quizube.response.registration.SendOTPResponse;
import com.apkzube.quizube.response.registration.VerifyOTPResponse;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.Error;
import com.apkzube.quizube.util.ViewUtil;
import com.apkzube.quizube.viewmodel.registration.VerifyEmailViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Objects;

public class VerifyEmailActivity extends AppCompatActivity implements OnSendOTPEvent, OnOTPVerifyEvent {

    private ActivityVerifyEmailBinding mBinding;
    private VerifyEmailViewModel model;
    private EditText[] editTexts;
    private Snackbar snackbar;
    private CountDownTimer countDownTimer;
    private boolean isForPasswordUpdate;
    private boolean isFromSignUp;
    private boolean isForVerifyEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        allocation();
        setEvent();
    }

    private void allocation() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_verify_email);
        model = ViewModelProviders.of(this).get(VerifyEmailViewModel.class);
        mBinding.setModel(model);
        model.setSendOTPEvent(this);
        model.setOnOTPVerifyEvent(this);


        Intent intent = getIntent();
        if (null != intent) {
            isFromSignUp=intent.getBooleanExtra(getString(R.string.from_sign_up_key),false);
            isForPasswordUpdate=intent.getBooleanExtra(getString(R.string.is_password_update_key),false);
            isForVerifyEmail=intent.getBooleanExtra(getString(R.string.is_for_verify_email),false);
            model.setFromUpdatePassword(isForPasswordUpdate);

            if(isFromSignUp){
                String email=intent.getStringExtra(getString(R.string.email_id_key));
                model.setOtpResponse(new SendOTPResponse());
                model.getOtpResponse().setEmail(email);
                mBinding.txt.setText(getString(R.string.verify_email_msg)+" "+email);
                mBinding.txtResend.setEnabled(true);
                mBinding.txtResend.setClickable(true);
                model.sendOTP(mBinding.txtResend);

            }else if(isForPasswordUpdate){
                SendOTPResponse otpResponse = intent.getParcelableExtra(getString(R.string.send_email_response_obj));
                Log.d(Constants.TAG, "allocation: " + new Gson().toJson(otpResponse));
                if (null != otpResponse) {
                    model.setOtpResponse(otpResponse);/**/
                    mBinding.txt.setText(getString(R.string.verify_email_msg)+" "+otpResponse.getEmail());
                }
            } else if(isForVerifyEmail){
                User user=intent.getParcelableExtra(getString(R.string.user_obj_key));
                if(null!=user){
                    model.setOtpResponse(new SendOTPResponse());
                    model.getOtpResponse().setEmail(user.getEmail());
                    mBinding.txt.setText(getString(R.string.verify_email_msg)+" "+user.getEmail());
                    mBinding.txtResend.setEnabled(true);
                    mBinding.txtResend.setClickable(true);
                    model.sendOTP(mBinding.txtResend);
                }else{
                    finish();
                }

            }
        }

        mBinding.otp1.requestFocus();
        mBinding.otp1.setShowSoftInputOnFocus(true);
        snackbar=Snackbar.make(mBinding.getRoot(), "", Snackbar.LENGTH_LONG);
    }

    private void setEvent() {

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                //model.getCount().setValue(String.valueOf();
                mBinding.txtResendCountDown.setText(String.valueOf(millisUntilFinished / 1000)+":00");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                mBinding.txtResendCountDown.setText("");
                //model.getCount().setValue("");
                model.getOtpResponse().setExpired(true);
                mBinding.txtResend.setEnabled(true);
            }

        };

        mBinding.btnConfirm.setEnabled(false);
        mBinding.txtResend.setEnabled(false);

        if(!isFromSignUp) {
            countDownTimer.start();
        }

        editTexts = new EditText[]{mBinding.otp1, mBinding.otp2, mBinding.otp3, mBinding.otp4, mBinding.otp5, mBinding.otp6};

        mBinding.otp1.addTextChangedListener(new PinTextWatcher(0));
        mBinding.otp2.addTextChangedListener(new PinTextWatcher(1));
        mBinding.otp3.addTextChangedListener(new PinTextWatcher(2));
        mBinding.otp4.addTextChangedListener(new PinTextWatcher(3));
        mBinding.otp5.addTextChangedListener(new PinTextWatcher(4));
        mBinding.otp6.addTextChangedListener(new PinTextWatcher(5));

        mBinding.otp1.setOnKeyListener(new PinOnKeyListener(0));
        mBinding.otp2.setOnKeyListener(new PinOnKeyListener(1));
        mBinding.otp3.setOnKeyListener(new PinOnKeyListener(2));
        mBinding.otp4.setOnKeyListener(new PinOnKeyListener(3));
        mBinding.otp5.setOnKeyListener(new PinOnKeyListener(4));
        mBinding.otp6.setOnKeyListener(new PinOnKeyListener(5));


    }


    public void setSnackBar(String msg,String errorCode){

        mBinding.btnConfirm.setEnabled(false);

        snackbar.setText(msg);
        if(errorCode.equalsIgnoreCase(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP002.toString())){
            //not reg user
            snackbar.setAction(R.string.sign_up,view ->  {
                startActivity(new Intent(VerifyEmailActivity.this,SignUpActivity.class));
                finish();
            });
        }



        if(TextUtils.isEmpty(errorCode)|| errorCode.equalsIgnoreCase(VerifyEmailActivity.ERROR_CODE.OTP008.toString()) || errorCode.equalsIgnoreCase(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP003.toString()) || errorCode.equalsIgnoreCase(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP005.toString()) ){
            //responce fail
            //otp expired OTP008
            snackbar.setAction(R.string.resend,view -> {mBinding.txtResend.performClick();
            snackbar.dismiss();});
        }

        snackbar.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.FORGOT_PASSWORD && resultCode==RESULT_OK){
            if(null!=data){
                setResult(RESULT_OK,data);
                finish();
            }
        }
    }

    // OTP verify event

    @Override
    public void onOTPVerifySuccess(VerifyOTPResponse response) {

        if(isFromSignUp){
            Intent intent=new Intent();
            setResult(RESULT_OK,intent);
            finish();
        }else if(isForPasswordUpdate){
            setVisibilityProgressbar(false);
            Intent intent = new Intent(this, UpdatePasswordActivity.class);
            intent.putExtra(getString(R.string.verify_otp_response_obj), response);
            intent.putExtra(getString(R.string.is_password_update_key),Boolean.TRUE);
            startActivityForResult(intent,Constants.FORGOT_PASSWORD);
        }else if(isForVerifyEmail){
            Intent intent=new Intent();
            intent.putExtra(getString(R.string.user_obj_key),response.getUser());
            setResult(RESULT_OK,intent);
            finish();
        }

    }

    @Override
    public void onOTPVerifyFail(VerifyOTPResponse response) {
        setVisibilityProgressbar(false);
        if(!response.getErrors().isEmpty()){
            for (Error error:response.getErrors()) {
                switch (error.toIntValue()){
                    case 1:
                    case 2:
                        Toast.makeText(this, response.getErrors().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        setSnackBar(error.getMessage(),"");
                        break;
                }
            }
        }
    }

    @Override
    public void onOTPVerifyStart() {
        setVisibilityProgressbar(true);
    }

    @Override
    public void onOTPExpired(VerifyOTPResponse response) {
        //Toast.makeText(this, getString(R.string.otp_expired), Toast.LENGTH_SHORT).show();
        setVisibilityProgressbar(false);
        setSnackBar(getString(R.string.otp_expired),ERROR_CODE.OTP008.toString());
    }

    @Override
    public void onOTPNotMatch(VerifyOTPResponse response) {
        Toast.makeText(this, getString(R.string.otp_does_not_match), Toast.LENGTH_SHORT).show();
        setVisibilityProgressbar(false);
    }

    //otp resend event

    @Override
    public void onOTPReceiveSuccess(SendOTPResponse responce) {
        ViewUtil.enableDisableView(mBinding.getRoot(),true);
        if(null!=responce && responce.isStatus()){
            model.setOtpResponse(responce);
            countDownTimer.start();
            mBinding.btnConfirm.setEnabled(false);
            mBinding.txtResend.setEnabled(false);
            clearAllOTP();
        }
        setVisibilityProgressbar(false);
    }

    @Override
    public void onOTPReceiveFail(SendOTPResponse response) {
         ViewUtil.enableDisableView(mBinding.getRoot(),true);
        //  Log.d(Constants.TAG, "onOTPReceiveFail: "+new Gson().toJson(response));

        if(null!=response && !response.isStatus()){
            StringBuffer errorBuffer=new StringBuffer();
            if(null!=response.getErrors() && !response.getErrors().isEmpty()) {
                for (Error error : response.getErrors()) {
                   if(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP005.toString().equalsIgnoreCase(error.getCode())){
                        setSnackBar(error.getMessage(),error.getCode());
                    }else if(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP002.toString().equalsIgnoreCase(error.getCode())){
                        //user not register
                        setSnackBar(error.getMessage(),error.getCode());
                    } else if(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP006.toString().equalsIgnoreCase(error.getCode())){
                        //email is not verified
                        //setSnackBar(error.getMessage(),error.getCode());
                    }else if(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP003.toString().equalsIgnoreCase(error.getCode())){
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
        mBinding.txtResend.setEnabled(true);
        setVisibilityProgressbar(false);
    }

    @Override
    public void onSendOTPStart() {

        ViewUtil.enableDisableView(mBinding.getRoot(),true);
        setVisibilityProgressbar(true);
        mBinding.txtResend.setEnabled(false);

    }

    //text watcher and key watcher
    public class PinTextWatcher implements TextWatcher {

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = newTypedString;

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0));

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        private void moveToNext() {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled()) { // isLast is optional
                hideKeyboard();
                mBinding.btnConfirm.setEnabled(true);
                editTexts[currentIndex].clearFocus();
            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }
        }

    }

    public class PinOnKeyListener implements View.OnKeyListener {

        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_UP) {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }

    }

    public void setVisibilityProgressbar(boolean b) {
        if (b) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            mBinding.progressBar.setVisibility(View.GONE);
        }
    }

    public void clearAllOTP(){
        mBinding.otp1.setText("");
        mBinding.otp2.setText("");
        mBinding.otp3.setText("");
        mBinding.otp4.setText("");
        mBinding.otp5.setText("");
        mBinding.otp6.setText("");
        mBinding.otp1.requestFocus();
    }



    public static enum ERROR_CODE{
        OTP001,//enter email id
        OTP002,//enter otp
        OTP006,// otp does not match
        OTP007,// server error
        OTP008 //OTP Expired
    }

}
