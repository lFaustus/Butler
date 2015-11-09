package com.fluxinatedhotmail.butler.common.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fluxi on 11/9/2015.
 */
public class Card implements Parcelable
{
    Car car;
    private int position;

    public Card(Car car)
    {
        this.car = car;
    }

    protected Card(Parcel in)
    {
        car = in.readParcelable(Car.class.getClassLoader());
        position = in.readInt();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>()
    {
        @Override
        public Card createFromParcel(Parcel in)
        {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size)
        {
            return new Card[size];
        }
    };

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public Car getCar()
    {
        return car;
    }

    public void setCar(Car car)
    {
        this.car = car;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(car, flags);
        dest.writeInt(position);
    }
}
