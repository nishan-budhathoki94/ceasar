package com.mitfinalproject.ceasar.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderData implements Parcelable {

    private int ID;
    private String Name;
    private int Quantity;
    private double Price;

    public OrderData(int ID, String name, double price, int quantity) {
        this.ID = ID;
        Name = name;
        Quantity = quantity;
        Price = price;
    }

    public OrderData() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.ID);
        dest.writeString(this.Name);
        dest.writeInt(this.Quantity);
        dest.writeDouble(this.Price);
    }

    protected OrderData(Parcel in) {
        this.ID = in.readInt();
        this.Name = in.readString();
        this.Quantity = in.readInt();
        this.Price = in.readDouble();
    }

    public static final Parcelable.Creator<OrderData> CREATOR = new Parcelable.Creator<OrderData>() {
        @Override
        public OrderData createFromParcel(Parcel source) {
            return new OrderData(source);
        }

        @Override
        public OrderData[] newArray(int size) {
            return new OrderData[size];
        }
    };
}
