package com.apkzube.quizube.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Count implements Parcelable {

    @SerializedName("count")
    @Expose
    private int count;

    public Count(int count) {
        this.count = count;
    }

    protected Count(Parcel in) {
        count = in.readInt();
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Count> CREATOR = new Creator<Count>() {
        @Override
        public Count createFromParcel(Parcel in) {
            return new Count(in);
        }

        @Override
        public Count[] newArray(int size) {
            return new Count[size];
        }
    };
}
