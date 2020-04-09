package com.apkzube.quizube.events.registration;

import com.apkzube.quizube.response.registration.UpdatePasswordResponse;

public interface OnPasswordUpdateEvent {

    public void onUpdatePasswordStart();
    public void onUpdatePasswordSuccessful(UpdatePasswordResponse response);
    public void onUpdatePasswordFail(UpdatePasswordResponse response);
    public void onSamePassword(UpdatePasswordResponse response);

}
