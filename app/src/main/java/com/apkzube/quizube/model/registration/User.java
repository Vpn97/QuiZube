package com.apkzube.quizube.model.registration;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class User implements Parcelable {

    @SerializedName("uid")
    @Expose
    private String uid;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("user_name")
    @Expose
    private String userName;

    @SerializedName("email_id")
    @Expose
    private String email;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("phone_no")
    @Expose
    private String phoneNo;


    @SerializedName("is_verify_email")
    @Expose
    private boolean isVerifyEmail;


    @SerializedName("is_verify_phone")
    @Expose
    private boolean isVerifyPhone;

    @SerializedName("birth_date")
    @Expose
    private Date birthDate;


    @SerializedName("is_google_login")
    @Expose
    private boolean isGoogleLogin;

    @SerializedName("is_facebook_login")
    @Expose
    private boolean isFacebookLogin;

    @SerializedName("reg_date")
    @Expose
    private Date regDate;


    @SerializedName("pwd_update_date")
    @Expose
    private Date pwsUpdateDate;


    public User(String uid, String userId, String userName, String email, String password, String phoneNo, boolean isVerifyEmail, boolean isVerifyPhone, Date birthDate, boolean isGoogleLogin, boolean isFacebookLogin, Date regDate, Date pwsUpdateDate) {
        this.uid = uid;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phoneNo = phoneNo;
        this.isVerifyEmail = isVerifyEmail;
        this.isVerifyPhone = isVerifyPhone;
        this.birthDate = birthDate;
        this.isGoogleLogin = isGoogleLogin;
        this.isFacebookLogin = isFacebookLogin;
        this.regDate = regDate;
        this.pwsUpdateDate = pwsUpdateDate;
    }

    public User() {
    }

    protected User(Parcel in) {
        uid = in.readString();
        userId = in.readString();
        userName = in.readString();
        email = in.readString();
        password = in.readString();
        phoneNo = in.readString();
        isVerifyEmail = in.readByte() != 0;
        isVerifyPhone = in.readByte() != 0;
        isGoogleLogin = in.readByte() != 0;
        isFacebookLogin = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(phoneNo);
        dest.writeByte((byte) (isVerifyEmail ? 1 : 0));
        dest.writeByte((byte) (isVerifyPhone ? 1 : 0));
        dest.writeByte((byte) (isGoogleLogin ? 1 : 0));
        dest.writeByte((byte) (isFacebookLogin ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public boolean isVerifyEmail() {
        return isVerifyEmail;
    }

    public void setVerifyEmail(boolean verifyEmail) {
        isVerifyEmail = verifyEmail;
    }

    public boolean isVerifyPhone() {
        return isVerifyPhone;
    }

    public void setVerifyPhone(boolean verifyPhone) {
        isVerifyPhone = verifyPhone;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isGoogleLogin() {
        return isGoogleLogin;
    }

    public void setGoogleLogin(boolean googleLogin) {
        isGoogleLogin = googleLogin;
    }

    public boolean isFacebookLogin() {
        return isFacebookLogin;
    }

    public void setFacebookLogin(boolean facebookLogin) {
        isFacebookLogin = facebookLogin;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getPwsUpdateDate() {
        return pwsUpdateDate;
    }

    public void setPwsUpdateDate(Date pwsUpdateDate) {
        this.pwsUpdateDate = pwsUpdateDate;
    }
}
