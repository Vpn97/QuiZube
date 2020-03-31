package com.apkzube.quizube.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apkzube.quizube.R;
import com.apkzube.quizube.databinding.ActivityLoginBinding;
import com.apkzube.quizube.util.Constants;
import com.apkzube.quizube.util.DataStorage;
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


public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding loginBinding;
    DataStorage storage;


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
        allocation();
        setEvent();
    }

    private void allocation() {
        storage = new DataStorage(this, getString(R.string.user_data));
        listener = new LoginActivityClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mFacbookLoginButton = loginBinding.btnFbLogin;
        mFacbookLoginButton.setLoginBehavior(LoginBehavior.DIALOG_ONLY);
        mCallbackManager = CallbackManager.Factory.create();
        mFacbookLoginButton.setPermissions(Constants.FB_PERIMISSON_LIST);
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
                                    storage.write(Constants.LOGIN_TYPE,Constants.LOGIN_FACEBOOK);
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
                Toast.makeText(this, "Login By Facebook : "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(this, "NO User Login", Toast.LENGTH_SHORT).show();

        }
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

        }

        public void signUp(View view) {
            startActivityForResult(new Intent(context,SignUp.class),Constants.SIGN_UP);
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
        }
    }


}
