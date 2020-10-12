package com.mitfinalproject.ceasar.Data;

import android.os.Parcel;
import android.os.Parcelable;


public class CartDetails implements Parcelable {
    private ItemData itemData;
    private int number;
    private double price;

    public CartDetails(ItemData itemData, int number) {
        this.itemData = itemData;
        this.number = number;
        this.price = itemData.getPrice() * number;
    }


    public ItemData getItemData() {
        return itemData;
    }


    public int getNumber() {
        return number;
    }


    public double getPrice() {
        return price;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.itemData, flags);
        dest.writeInt(this.number);
        dest.writeDouble(this.price);
    }

    protected CartDetails(Parcel in) {
        this.itemData = in.readParcelable(ItemData.class.getClassLoader());
        this.number = in.readInt();
        this.price = in.readDouble();
    }

    public static final Parcelable.Creator<CartDetails> CREATOR = new Parcelable.Creator<CartDetails>() {
        @Override
        public CartDetails createFromParcel(Parcel source) {
            return new CartDetails(source);
        }

        @Override
        public CartDetails[] newArray(int size) {
            return new CartDetails[size];
        }
    };
}
