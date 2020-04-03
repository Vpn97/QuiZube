package com.apkzube.quizube.model.registration;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.apkzube.quizube.BR;
import com.google.gson.annotations.SerializedName;

public class RegUserMst extends BaseObservable {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("user_name")
    private  String userName;

    @SerializedName("password")
    private String password;

    @SerializedName("password")
    private String confPassword;

    @SerializedName("email_id")
    private String emailId;


    public RegUserMst(String userId, String userName, String password, String confPassword, String emailId) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.confPassword = confPassword;
        this.emailId = emailId;
    }

    public RegUserMst() {
    }

    @Bindable
    public String getUserId() {
        return userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
        notifyPropertyChanged(BR.emailId);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getConfPassword() {
        return confPassword;
    }

    public void setConfPassword(String confPassword) {
        this.confPassword = confPassword;
        notifyPropertyChanged(BR.confPassword);
    }
}
