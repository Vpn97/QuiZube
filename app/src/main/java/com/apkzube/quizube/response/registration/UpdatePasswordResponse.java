package com.apkzube.quizube.response.registration;

import android.os.Parcel;
import android.os.Parcelable;

import com.apkzube.quizube.util.Error;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UpdatePasswordResponse implements Parcelable {

    @SerializedName("status")
    private boolean status;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("errors")
    @Expose
    private ArrayList<Error> errors;



    public UpdatePasswordResponse(boolean status, String email, ArrayList<Error> errors) {
        this.status = status;
        this.email = email;
        this.errors = errors;
    }

    public UpdatePasswordResponse() {
    }

    protected UpdatePasswordResponse(Parcel in) {
        status = in.readByte() != 0;
        email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UpdatePasswordResponse> CREATOR = new Creator<UpdatePasswordResponse>() {
        @Override
        public UpdatePasswordResponse createFromParcel(Parcel in) {
            return new UpdatePasswordResponse(in);
        }

        @Override
        public UpdatePasswordResponse[] newArray(int size) {
            return new UpdatePasswordResponse[size];
        }
    };

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
}
