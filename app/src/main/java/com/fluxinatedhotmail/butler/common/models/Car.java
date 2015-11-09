package com.fluxinatedhotmail.butler.common.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fluxi on 11/9/2015.
 */
public class Car implements Parcelable
{
    private String name;
    private String status;
    private String rate;

    public Car()
    {
    }

    protected Car(Parcel in)
    {
        name = in.readString();
        status = in.readString();
        rate = in.readString();
    }

    public static final Creator<Car> CREATOR = new Creator<Car>()
    {
        @Override
        public Car createFromParcel(Parcel in)
        {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size)
        {
            return new Car[size];
        }
    };

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRate()
    {
        return rate;
    }

    public void setRate(String rate)
    {
        this.rate = rate;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeString(status);
        dest.writeString(rate);
    }
}
