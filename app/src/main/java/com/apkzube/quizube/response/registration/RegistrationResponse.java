package com.apkzube.quizube.response.registration;

import android.os.Parcel;
import android.os.Parcelable;

import com.apkzube.quizube.model.registration.User;
import com.apkzube.quizube.util.Error;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RegistrationResponse implements Parcelable {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("errors")
    @Expose
    private ArrayList<Error> errors;

    @SerializedName("user")
    @Expose
    private User user;


    public RegistrationResponse(boolean status, ArrayList<Error> errors) {
        this.status = status;
        this.errors = errors;
    }

    public RegistrationResponse() {
    }


    public RegistrationResponse(boolean status, ArrayList<Error> errors, User user) {
        this.status = status;
        this.errors = errors;
        this.user = user;
    }


    protected RegistrationResponse(Parcel in) {
        status = in.readByte() != 0;
        errors = in.createTypedArrayList(Error.CREATOR);
        user = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeTypedList(errors);
        dest.writeParcelable(user, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RegistrationResponse> CREATOR = new Creator<RegistrationResponse>() {
        @Override
        public RegistrationResponse createFromParcel(Parcel in) {
            return new RegistrationResponse(in);
        }

        @Override
        public RegistrationResponse[] newArray(int size) {
            return new RegistrationResponse[size];
        }
    };

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Error> errors) {
        this.errors = errors;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
