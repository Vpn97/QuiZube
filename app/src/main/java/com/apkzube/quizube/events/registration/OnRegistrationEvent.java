package com.apkzube.quizube.events.registration;

import com.apkzube.quizube.response.registration.RegistratoinResponse;

public interface OnRegistrationEvent {

    public void onRegistrationSuccess(RegistratoinResponse responce);
    public void onRegistrationFail(RegistratoinResponse responce);
    public  void onRegistrationStart();
}
