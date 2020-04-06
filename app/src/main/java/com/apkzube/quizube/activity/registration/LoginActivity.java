package com.apkzube.quizube.activity.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.apkzube.quizube.R;
import com.apkzube.quizube.databinding.ActivityLoginBinding;
import com.apkzube.quizube.databinding.DialogSignInLayoutBinding;
import com.apkzube.quizube.events.registration.OnLoginEvent;
import com.apkzube.quizube.response.registration.LoginResponse;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.DataStorage;
import com.apkzube.quizube.util.Error;
import com.apkzube.quizube.viewmodel.registration.LoginViewModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;


public class LoginActivity extends AppCompatActivity implements OnLoginEvent {

    ActivityLoginBinding loginBinding;
    DataStorage storage;
    Dialog dialog;
    LoginViewModel loginViewModel;

    //binding
    DialogSignInLayoutBinding mBinding;
    OnLoginEvent loginEvent;


    //google sign in
    LoginActivityClickListener listener;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;


    //facebook login
    CallbackManager mCallbackManager;
    LoginButton mFacbookLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        //LoginViewModel model= ViewModelProviders.of(this).get(LoginViewModel.class);

        allocation();
        setEvent();
    }

    private void allocation() {
        storage = new DataStorage(this, getString(R.string.user_data));
        loginEvent = this;
        listener = new LoginActivityClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mFacbookLoginButton = loginBinding.btnFbLogin;
        mFacbookLoginButton.setLoginBehavior(LoginBehavior.DIALOG_ONLY);
        mCallbackManager = CallbackManager.Factory.create();
        mFacbookLoginButton.setPermissions(Constants.FB_PERIMISSON_LIST);
        //set dialog for login
        setLoginDialog();
    }

    private void setEvent() {
        loginBinding.setClickListener(listener);
        mFacbookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Login By FaceBook " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                    storage.write(Constants.LOGIN_TYPE, Constants.LOGIN_FACEBOOK);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed Facebook.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Facebook Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Constants.TAG, "onError: " + error.getMessage());
                Toast.makeText(LoginActivity.this, "Facebook Login Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoginDialog() {

        dialog = new Dialog(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        mBinding = DialogSignInLayoutBinding.inflate(LayoutInflater.from(new ContextThemeWrapper(this, R.style.DialogTheme)));
        dialog.setContentView(mBinding.getRoot());


        loginViewModel = ViewModelProviders.of(LoginActivity.this).get(LoginViewModel.class);
        loginViewModel.setLoginEvent(loginEvent);
        mBinding.setModel(loginViewModel);

        mBinding.btnDialogSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginViewModel.signIn(view);
            }
        });

        mBinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mBinding.txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBinding.btnSignUp.performClick();
                dialog.dismiss();
            }
        });


        mBinding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        //Checked user sign in or not
        switch ((int) storage.read(Constants.LOGIN_TYPE, DataStorage.INTEGER)) {
            case 1:
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                Toast.makeText(this, "Login By Google : " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
                break;
            case 2:
                FirebaseUser user = mAuth.getCurrentUser();
                Toast.makeText(this, "Login By Facebook : " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(this, "NO User Login", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onLoginSuccess(LoginResponse response) {
        Log.d(Constants.TAG, "Login Success: " + new Gson().toJson(response));
        if (null != response && null != response.getUser() && response.isStatus()) {
            Toast.makeText(this, response.getUser().getUserName(), Toast.LENGTH_SHORT).show();
        } else {
            mBinding.txtError.setVisibility(View.VISIBLE);
            mBinding.txtError.setText(getString(R.string.server_error));
        }
    }

    @Override
    public void onLoginFail(LoginResponse response) {
        Log.d(Constants.TAG, "onLoginFail: " + new Gson().toJson(response));
        if (null != response && !response.isStatus()) {
            if (null != response.getErrors() && !response.getErrors().isEmpty()) {
                StringBuffer errorBuffer = new StringBuffer();
                for (Error error : response.getErrors()) {

                    if (error.getCode().equalsIgnoreCase(LOGIN_EROR_CODE.REG001.toString())) {
                        mBinding.txtUserId.setErrorEnabled(true);
                        mBinding.txtUserId.setError(error.getMessage());
                    } else if (error.getCode().equalsIgnoreCase(LOGIN_EROR_CODE.REG002.toString())) {
                        mBinding.txtPassword.setErrorEnabled(true);
                        mBinding.txtPassword.setError(error.getMessage());
                    } else {
                        errorBuffer.append(error.getMessage()).append("\n");
                    }
                }
                if (!TextUtils.isEmpty(errorBuffer)) {
                    mBinding.txtError.setVisibility(View.VISIBLE);
                    mBinding.txtError.setText(errorBuffer);
                }
            } else {
                mBinding.txtError.setVisibility(View.VISIBLE);
                mBinding.txtError.setText(getString(R.string.server_error));
            }
        }
    }

    @Override
    public void onLoginStart() {
        mBinding.txtError.setVisibility(View.GONE);
        mBinding.txtError.setText("");
    }

    public class LoginActivityClickListener {
        Context context;

        public LoginActivityClickListener(Context context) {
            this.context = context;
        }

        public void signInWithGoogle(View view) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, Constants.LOGIN_GOOGLE);
        }

        public void signInWithFacebook(View view) {
            mFacbookLoginButton.performClick();
        }

        public void signIn(View view) {
            dialog.show();
        }

        public void signUp(View view) {
            startActivityForResult(new Intent(context, SignUpActivity.class), Constants.SIGN_UP);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.LOGIN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                storage.write(Constants.LOGIN_TYPE, Constants.LOGIN_GOOGLE);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(Constants.TAG, "Google sign in failed", e);
                // ...
            }
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            //login by FaceBook
            storage.write(Constants.LOGIN_TYPE, Constants.LOGIN_GOOGLE);
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        } else if (requestCode == Constants.SIGN_UP) {

            if (null != data) {
                String userID = data.getStringExtra("user_id");
                if (null != userID && !TextUtils.isEmpty(userID)) {
                    dialog.show();
                    loginViewModel.getUserIdEmail().setValue(userID);
                    mBinding.txtPassword.setFocusable(true);
                    mBinding.txtPassword.requestFocus();
                }
            }

        }
    }


    public static enum LOGIN_EROR_CODE {
        REG001,//user id or email
        REG002,// password
        REG012// error
    }

}
