package com.mitfinalproject.ceasar.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemData implements Parcelable {

    private String name;
    private String category;
    private String size;
    private double price;
    private String availability;
    private int itemID;
    private String desc;

    public ItemData() {
    }


    public ItemData(String name, String category, String desc, String size, double price, String availability, int itemID) {
        this.name = name;
        this.category = category;
        this.size = size;
        this.price = price;
        this.availability = availability;
        this.itemID = itemID;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.category);
        dest.writeString(this.size);
        dest.writeDouble(this.price);
        dest.writeString(this.availability);
        dest.writeInt(this.itemID);
        dest.writeString(this.desc);
    }

    protected ItemData(Parcel in) {
        this.name = in.readString();
        this.category = in.readString();
        this.size = in.readString();
        this.price = in.readDouble();
        this.availability = in.readString();
        this.itemID = in.readInt();
        this.desc = in.readString();
    }

    public static final Parcelable.Creator<ItemData> CREATOR = new Parcelable.Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(Parcel source) {
            return new ItemData(source);
        }

        @Override
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };
}
