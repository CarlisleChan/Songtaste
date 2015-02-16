package com.carlisle.songtaste.modle;

import android.os.Parcel;
import android.os.Parcelable;

import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Created by chengxin on 2/13/15.
 */
@ParcelablePlease
public class TestModle implements Parcelable {
    // test com.hannesdorfmann.parcelableplease

    public int id = -1;
    public String title = "";

    public TestModle() {
        this.id = 1;
        this.title = "title";
    }

    public TestModle(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        TestModleParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<TestModle> CREATOR = new Creator<TestModle>() {
        public TestModle createFromParcel(Parcel source) {
            TestModle target = new TestModle();
            TestModleParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public TestModle[] newArray(int size) {
            return new TestModle[size];
        }
    };
}