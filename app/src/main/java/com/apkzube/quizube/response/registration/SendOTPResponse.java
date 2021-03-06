package com.apkzube.quizube.response.registration;

import android.os.Parcel;
import android.os.Parcelable;

import com.apkzube.quizube.util.Error;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SendOTPResponse implements Parcelable {

    @SerializedName("status")
    @Expose
    private boolean status;

    @SerializedName("uid")
    @Expose
    private String uid;


    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("otp")
    @Expose
    private String otp;

    @SerializedName("errors")
    @Expose
    private ArrayList<Error> errors;

    @SerializedName("is_expired")
    @Expose
    private boolean isExpired;


    @SerializedName("opt_id")
    @Expose
    private int otpId;


    public SendOTPResponse() {
    }


    public SendOTPResponse(boolean status, String uid, String email, String otp, ArrayList<Error> errors, boolean isExpired, int otpId) {
        this.status = status;
        this.uid = uid;
        this.email = email;
        this.otp = otp;
        this.errors = errors;
        this.isExpired = isExpired;
        this.otpId = otpId;
    }

    protected SendOTPResponse(Parcel in) {
        status = in.readByte() != 0;
        uid = in.readString();
        email = in.readString();
        otp = in.readString();
        errors = in.createTypedArrayList(Error.CREATOR);
        isExpired = in.readByte() != 0;
        otpId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(uid);
        dest.writeString(email);
        dest.writeString(otp);
        dest.writeTypedList(errors);
        dest.writeByte((byte) (isExpired ? 1 : 0));
        dest.writeInt(otpId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SendOTPResponse> CREATOR = new Creator<SendOTPResponse>() {
        @Override
        public SendOTPResponse createFromParcel(Parcel in) {
            return new SendOTPResponse(in);
        }

        @Override
        public SendOTPResponse[] newArray(int size) {
            return new SendOTPResponse[size];
        }
    };

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
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

    public int getOtpId() {
        return otpId;
    }

    public void setOtpId(int otpId) {
        this.otpId = otpId;
    }
}
