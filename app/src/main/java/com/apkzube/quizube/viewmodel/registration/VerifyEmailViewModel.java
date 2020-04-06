package com.apkzube.quizube.viewmodel.registration;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class VerifyEmailViewModel extends AndroidViewModel {

    private Application application;


    public VerifyEmailViewModel(@NonNull Application application, Application application1) {
        super(application);
        this.application = application1;
    }
}
