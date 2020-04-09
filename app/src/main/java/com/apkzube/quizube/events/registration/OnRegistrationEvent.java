package com.apkzube.quizube.events.registration;

import com.apkzube.quizube.response.registration.RegistrationResponse;

public interface OnRegistrationEvent {

    public void onRegistrationSuccess(RegistrationResponse responce);
    public void onRegistrationFail(RegistrationResponse responce);
    public  void onRegistrationStart();
}
