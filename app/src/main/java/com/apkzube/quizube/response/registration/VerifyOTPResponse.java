package com.apkzube.quizube.response.registration;

import android.os.Parcel;
import android.os.Parcelable;

import com.apkzube.quizube.model.registration.User;
import com.apkzube.quizube.util.Error;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VerifyOTPResponse implements Parcelable {


    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("errors")
    @Expose
    private ArrayList<Error> errors;

    @SerializedName("is_expired")
    @Expose
    private boolean isExpired;


    public VerifyOTPResponse() {
    }


    public VerifyOTPResponse(boolean status, User user, String email, ArrayList<Error> errors, boolean isExpired) {
        this.status = status;
        this.user = user;
        this.email = email;
        this.errors = errors;
        this.isExpired = isExpired;
    }

    protected VerifyOTPResponse(Parcel in) {
        status = in.readByte() != 0;
        user = in.readParcelable(User.class.getClassLoader());
        email = in.readString();
        errors = in.createTypedArrayList(Error.CREATOR);
        isExpired = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeParcelable(user, flags);
        dest.writeString(email);
        dest.writeTypedList(errors);
        dest.writeByte((byte) (isExpired ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VerifyOTPResponse> CREATOR = new Creator<VerifyOTPResponse>() {
        @Override
        public VerifyOTPResponse createFromParcel(Parcel in) {
            return new VerifyOTPResponse(in);
        }

        @Override
        public VerifyOTPResponse[] newArray(int size) {
            return new VerifyOTPResponse[size];
        }
    };

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Error> errors) {
        this.errors = errors;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }
}
