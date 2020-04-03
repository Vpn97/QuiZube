package com.apkzube.quizube.response.registration;

import android.os.Parcel;
import android.os.Parcelable;

import com.apkzube.quizube.util.Error;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RegistratoinResponse implements Parcelable {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("errors")
    @Expose
    private ArrayList<Error> errors;


    public RegistratoinResponse(boolean status, ArrayList<Error> errors) {
        this.status = status;
        this.errors = errors;
    }

    public RegistratoinResponse() {
    }


    protected RegistratoinResponse(Parcel in) {
        status = in.readByte() != 0;
        errors = in.createTypedArrayList(Error.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeTypedList(errors);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RegistratoinResponse> CREATOR = new Creator<RegistratoinResponse>() {
        @Override
        public RegistratoinResponse createFromParcel(Parcel in) {
            return new RegistratoinResponse(in);
        }

        @Override
        public RegistratoinResponse[] newArray(int size) {
            return new RegistratoinResponse[size];
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
}
