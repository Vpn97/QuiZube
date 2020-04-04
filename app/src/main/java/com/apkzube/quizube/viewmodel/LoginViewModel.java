package com.apkzube.quizube.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends AndroidViewModel {

    private Application application;
    private MutableLiveData<String> userIdEmail=new MutableLiveData<>();
    private MutableLiveData<String> password=new MutableLiveData<>();


    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.application=application;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public MutableLiveData<String> getUserIdEmail() {
        return userIdEmail;
    }

    public void setUserIdEmail(MutableLiveData<String> userIdEmail) {
        this.userIdEmail = userIdEmail;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }
}
