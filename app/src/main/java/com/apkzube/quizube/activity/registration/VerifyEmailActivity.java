package com.apkzube.quizube.activity.registration;

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
import com.apkzube.quizube.response.registration.RegistratoinResponse;
import com.apkzube.quizube.response.registration.SendOTPResponse;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.Error;
import com.apkzube.quizube.util.ViewUtil;
import com.apkzube.quizube.viewmodel.registration.VerifyEmailViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

public class VerifyEmailActivity extends AppCompatActivity implements OnSendOTPEvent, OnOTPVerifyEvent {

    private ActivityVerifyEmailBinding mBinding;
    private VerifyEmailViewModel model;
    private EditText[] editTexts;
    private OnOTPVerifyEvent onOTPVerifyEvent;
    private OnSendOTPEvent sendOTPEvent;
    private Snackbar snackbar;
    private CountDownTimer countDownTimer;

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
            SendOTPResponse otpResponse = intent.getParcelableExtra("response");
            Log.d(Constants.TAG, "allocation: " + new Gson().toJson(otpResponse));
            if (null != otpResponse && null != otpResponse.getOtp()) {
                model.setOtpResponse(otpResponse);/**/
            }
        }

        mBinding.otp1.requestFocus();
        mBinding.otp1.setShowSoftInputOnFocus(true);


    }

    private void setEvent() {


        mBinding.btnConfirm.setEnabled(false);
        mBinding.txtResend.setEnabled(false);

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                model.getCount().setValue(String.valueOf(millisUntilFinished / 1000)+":00");
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                //mBinding.txtResendCountDown.setText("done!");
                model.getCount().setValue("");
                model.getOtpResponse().setExpired(true);
                mBinding.txtResend.setEnabled(true);
            }

        };

        countDownTimer.start();


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
            snackbar.setAction(R.string.sign_up,view -> {
                startActivity(new Intent(VerifyEmailActivity.this,SignUpActivity.class));
                finish();
            });
        }


        if(TextUtils.isEmpty(errorCode)|| errorCode.equalsIgnoreCase(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP003.toString()) || errorCode.equalsIgnoreCase(ForgotPasswordActivity.SEND_OTP_ERROR_CODE.OTP005.toString()) ){
            //responce fail
            snackbar.setAction(R.string.re_try,view -> {
                mBinding.txtResend.performClick();
            });
        }

        snackbar.show();
    }

    // OTp verify event

    @Override
    public void onOTPVerifySuccess(SendOTPResponse sendOTPResponse) {

        Toast.makeText(this, R.string.otp_verify_sucessfully, Toast.LENGTH_SHORT).show();
        setVisibilityProgressbar(false);

    }

    @Override
    public void onOTPVerifyFail(SendOTPResponse sendOTPResponse) {

        setVisibilityProgressbar(false);
    }

    @Override
    public void onOTPVerifyStart() {
        setVisibilityProgressbar(true);
    }

    @Override
    public void onOTPExpired(SendOTPResponse sendOTPResponse) {

        Toast.makeText(this, getString(R.string.otp_expired), Toast.LENGTH_SHORT).show();

        setVisibilityProgressbar(false);
    }

    @Override
    public void onOTPNotMatch(SendOTPResponse sendOTPResponse) {
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
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

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

}
