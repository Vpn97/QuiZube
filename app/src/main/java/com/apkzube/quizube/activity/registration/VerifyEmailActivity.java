package com.apkzube.quizube.activity.registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.apkzube.quizube.R;
import com.apkzube.quizube.databinding.ActivityVerifyEmailBinding;
import com.apkzube.quizube.response.registration.SendOTPResponse;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.viewmodel.registration.VerifyEmailViewModel;
import com.google.gson.Gson;

public class VerifyEmailActivity extends AppCompatActivity {

    private ActivityVerifyEmailBinding mBinding;
    private VerifyEmailViewModel model;
    private EditText[] editTexts;

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

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus();
                hideKeyboard();
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

}
