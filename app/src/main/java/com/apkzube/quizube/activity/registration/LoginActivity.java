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
import com.apkzube.quizube.events.registration.OnValidateAuthEvent;
import com.apkzube.quizube.model.registration.User;
import com.apkzube.quizube.response.registration.LoginResponse;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.DataStorage;
import com.apkzube.quizube.util.Error;
import com.apkzube.quizube.util.ViewUtil;
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

import java.util.Objects;


public class LoginActivity extends AppCompatActivity implements OnLoginEvent, OnValidateAuthEvent {

    private ActivityLoginBinding loginBinding;
    private DataStorage storage;
    private Dialog dialog;
    private LoginViewModel model;

    //binding
    private DialogSignInLayoutBinding mBinding;


    //google sign in
    private LoginActivityClickListener listener;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;


    //facebook login
    private CallbackManager mCallbackManager;
    private LoginButton mFacebookLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        allocation();
        setEvent();
    }

    private void allocation() {
        storage = new DataStorage(this, getString(R.string.user_data));
        listener = new LoginActivityClickListener(this);

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginBinding.setClickListener(listener);
        model = ViewModelProviders.of(this).get(LoginViewModel.class);
        model.setAuthEvent(this);
        model.setLoginEvent(this);

        mAuth = FirebaseAuth.getInstance();
        mFacebookLoginButton = loginBinding.btnFbLogin;
        mFacebookLoginButton.setLoginBehavior(LoginBehavior.DIALOG_ONLY);
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginButton.setPermissions(Constants.FB_PERMISSIONS_LIST);
        //set dialog for login
        setLoginDialog();
    }

    private void setEvent() {
        mFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                model.validateAuthUserLogin(Objects.requireNonNull(user).getEmail(), user.getDisplayName(), Constants.LOGIN_FACEBOOK);
                                //Toast.makeText(LoginActivity.this, "Login By FaceBook " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, getString(R.string.facrbook_auth_fail), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, getString(R.string.facrbook_auth_canceled), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(Constants.TAG, "onError: " + error.getMessage());
                Toast.makeText(LoginActivity.this, getString(R.string.facrbook_auth_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoginDialog() {

        dialog = new Dialog(this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        mBinding = DialogSignInLayoutBinding.inflate(LayoutInflater.from(new ContextThemeWrapper(this, R.style.DialogTheme)));
        dialog.setContentView(mBinding.getRoot());
        mBinding.setModel(model);

        mBinding.btnDialogSignIn.setOnClickListener(view -> model.signIn(view));
        mBinding.btnClose.setOnClickListener(view -> dialog.dismiss());
        mBinding.txtSignUp.setOnClickListener(view -> {
            loginBinding.btnSignUp.performClick();
            dialog.dismiss();
        });
        mBinding.txtForgotPassword.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }


    @Override
    protected void onStart() {
        super.onStart();

        //Checked user sign in or not
        /*
 switch ((int) storage.read(Constants.LOGIN_TYPE, DataStorage.INTEGER)) {
            case 1:
                //google login
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                //Toast.makeText(this, "Login By Google : " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
                if (account != null) {
                    model.validateAuthUserLogin(account.getEmail(), account.getDisplayName(), Constants.LOGIN_GOOGLE);
                }
                break;
            case 2:
                //facebook login
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    model.validateAuthUserLogin(user.getEmail(), user.getDisplayName(), Constants.LOGIN_FACEBOOK);
                }
                //Toast.makeText(this, "Login By Facebook : " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                break;

            case 3:
                //sign in by email or user id
                try {
                    User mUser = new Gson().
                            fromJson(String.valueOf(storage.read(getString(R.string.user_obj_key), DataStorage.STRING))
                                    , User.class);
                    Toast.makeText(this, mUser.getUserName(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(this, getString(R.string.no_user_login), Toast.LENGTH_SHORT).show();
                }


            default:
                Toast.makeText(this, getString(R.string.no_user_login), Toast.LENGTH_SHORT).show();

        }*/

        try {
            User mUser = new Gson().fromJson(String.valueOf(storage.read(getString(R.string.user_obj_key), DataStorage.STRING)), User.class);

            if (null != mUser) {
                startDashboardIntent(mUser);
            } else {
                Toast.makeText(this, getString(R.string.no_user_login), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.no_user_login), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoginSuccess(LoginResponse response) {
        Log.d(Constants.TAG, "Login Success: " + new Gson().toJson(response));
        if (null != response && null != response.getUser() && response.isStatus()) {
            storage.write(getString(R.string.user_obj_key), new Gson().toJson(response.getUser()));
            storage.write(Constants.LOGIN_TYPE, Constants.LOGIN_SIGN_IN);
            startDashboardIntent(response.getUser());
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

                    if (error.getCode().equalsIgnoreCase(LOGIN_ERROR_CODE.REG001.toString())) {
                        mBinding.txtUserId.setErrorEnabled(true);
                        mBinding.txtUserId.setError(error.getMessage());
                    } else if (error.getCode().equalsIgnoreCase(LOGIN_ERROR_CODE.REG002.toString())) {
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
            mFacebookLoginButton.performClick();
        }

        public void signIn(View view) {
            dialog.show();
        }

        public void signUp(View view) {
            startActivityForResult(new Intent(context, SignUpActivity.class), Constants.SIGN_UP);
        }
    }


    // validate google and facebook auth user
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.LOGIN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (null != account.getEmail()) {
                    model.validateAuthUserLogin(account.getEmail(), account.getDisplayName(), Constants.LOGIN_GOOGLE);
                } else {
                    Toast.makeText(this, getString(R.string.google_login_fail_msg), Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(Constants.TAG, getString(R.string.google_login_fail_msg), e);
                Toast.makeText(this, getString(R.string.google_login_fail_msg), Toast.LENGTH_SHORT).show();
                // ...
            }
        } else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            //login by FaceBook
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        } else if (requestCode == Constants.SIGN_UP) {

            if (null != data) {

                if (data.getBooleanExtra(getString(R.string.is_google_login_key), Boolean.FALSE)) {
                    //TODO
                    storage.write(Constants.LOGIN_TYPE, Constants.LOGIN_GOOGLE);
                    User user = data.getParcelableExtra(getString(R.string.user_obj_key));
                    if(null!=user){
                        storage.write(getString(R.string.user_obj_key), new Gson().toJson(user));
                    }
                    startDashboardIntent(user);

                } else if (data.getBooleanExtra(getString(R.string.is_facebook_login_key), Boolean.FALSE)) {
                    //TODO
                    storage.write(Constants.LOGIN_TYPE, Constants.LOGIN_FACEBOOK);
                    User user = data.getParcelableExtra(getString(R.string.user_obj_key));
                    if(null!=user){
                        storage.write(getString(R.string.user_obj_key), new Gson().toJson(user));
                        startDashboardIntent(user);
                    }

                } else {

                    String userID = data.getStringExtra(getString(R.string.user_id_key));
                    if (null != userID && !TextUtils.isEmpty(userID)) {
                        dialog.show();
                        model.getUserIdEmail().setValue(userID);
                        mBinding.txtPassword.setFocusable(true);
                        mBinding.txtPassword.requestFocus();
                    }
                }
            }

        }
    }


    @Override
    public void onValidateAuthStart() {
        ViewUtil.enableDisableView(loginBinding.getRoot(), false);
        setVisibilityProgressbar(true);
    }

    @Override
    public void onValidateAuthSuccess(LoginResponse response, int loginCode) {
        ViewUtil.enableDisableView(loginBinding.getRoot(), true);
        setVisibilityProgressbar(false);

        if (null != response && response.isStatus() && null != response.getUser()) {
            if (loginCode == Constants.LOGIN_GOOGLE) {
                storage.write(Constants.LOGIN_TYPE, Constants.LOGIN_GOOGLE);
            } else if (loginCode == Constants.LOGIN_FACEBOOK) {
                storage.write(Constants.LOGIN_TYPE, Constants.LOGIN_FACEBOOK);
            }
            storage.write(getString(R.string.user_obj_key), new Gson().toJson(response.getUser()));
            startDashboardIntent(response.getUser());
        }

    }


    @Override
    public void onUserNotRegistered(LoginResponse response, String email, String name, int loginCode) {
        ViewUtil.enableDisableView(loginBinding.getRoot(), true);
        setVisibilityProgressbar(false);

        if (!response.isStatus()) {
            Intent intent = new Intent(this, TakeUserIdActivity.class);
            User user = new User();
            user.setEmail(email);
            user.setUserName(name);
            intent.putExtra(getString(R.string.user_obj_key), user);
            if (loginCode == Constants.LOGIN_FACEBOOK) {
                user.setGoogleLogin(false);
                user.setFacebookLogin(true);
            } else if (loginCode == Constants.LOGIN_GOOGLE) {
                user.setGoogleLogin(true);
                user.setFacebookLogin(false);
            }
            startActivityForResult(intent, Constants.SIGN_UP);
        }

    }

    @Override
    public void onValidateAuthFail(LoginResponse response) {
        ViewUtil.enableDisableView(loginBinding.getRoot(), true);
        setVisibilityProgressbar(false);

    }

    public static enum LOGIN_ERROR_CODE {
        REG001,//user id or email
        REG002,// password
        REG012,// error

        LOGIN001 //email null
    }

    public void setVisibilityProgressbar(boolean b) {
        if (b) {
            loginBinding.progressBar.setVisibility(View.VISIBLE);
        } else {
            loginBinding.progressBar.setVisibility(View.GONE);
        }
    }

    public void startDashboardIntent(User user){
        //TODO create main dashboard intent
    }

}
