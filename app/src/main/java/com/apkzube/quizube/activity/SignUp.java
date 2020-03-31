package com.apkzube.quizube.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apkzube.quizube.R;
import com.apkzube.quizube.databinding.ActivitySignUpBinding;
import com.apkzube.quizube.model.Count;
import com.apkzube.quizube.model.RegUserMst;
import com.apkzube.quizube.service.UserRegistrationService;
import com.apkzube.quizube.service.impl.UserRegistrationServiceImpl;
import com.apkzube.quizube.util.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding signUpBinding;
    private RegUserMst user;
    private Count mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        allocation();
        setEvent();

    }

    private void allocation() {
        user=new RegUserMst();
        signUpBinding= DataBindingUtil.setContentView(this,R.layout.activity_sign_up);
        signUpBinding.setUserMst(user);
    }

    private void setEvent() {

        signUpBinding.txtUserId.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                user.setUserId(charSequence.toString());
                Log.d(Constants.TAG, "onTextChanged: "+charSequence.toString()+" user : "+user.getUserId());
                if(user.getUserId()!=null && user.getUserId().matches(Constants.USER_ID_REGEX) && user.getUserId().length()>=6){

                    Log.d(Constants.TAG, "onTextChanged: valid");
                    UserRegistrationService registrationService= UserRegistrationServiceImpl.getService();
                    final Call<Count> countCall=registrationService.getIsValidUser(user.getUserId());
                    countCall.enqueue(new Callback<Count>() {
                        @Override
                        public void onResponse(Call<Count> call, Response<Count> response) {
                            Count mCount=response.body();
                            if(null!=mCount){
                                if(mCount.getCount()>0){
                                    signUpBinding.txtUserId.setErrorEnabled(true);
                                    signUpBinding.txtUserId.setError(getString(R.string.user_id_already));
                                    Log.d(Constants.TAG, "onResponse: "+mCount.getCount());

                                }else if(mCount.getCount()==0){
                                    signUpBinding.txtUserId.setErrorEnabled(false);
                                    Log.d(Constants.TAG, "onResponse: "+mCount.getCount());
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Count> call, Throwable t) {
                            Log.d(Constants.TAG, "onFailure: onFocusChange"+t.getMessage());
                            Toast.makeText(getApplicationContext(), "onFailure: onFocusChange"+t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Log.d(Constants.TAG, "onTextChanged: not valid");
                    signUpBinding.txtUserId.setErrorEnabled(true);
                    signUpBinding.txtUserId.setError(getString(R.string.not_valid_user_id));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
