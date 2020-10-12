package com.mitfinalproject.ceasar.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CompleteOrderData implements Parcelable {

    private List<OrderData> items;
    private String address, date, notes, email, status;
    private int OrderID, total_items;
    private double price;

    public CompleteOrderData() {
    }

    public CompleteOrderData(int orderID,String email,List<OrderData> orderData, String address, String date, String note, double price,int total_items, String status) {
        this.items = orderData;
        this.address = address;
        this.date = date;
        this.notes = note;
        this.email = email;
        this.OrderID = orderID;
        this.total_items = total_items;
        this.price = price;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderData> getOrderData() {
        return items;
    }

    public void setOrderData(List<OrderData> orderData) {
        this.items = orderData;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return notes;
    }

    public void setNote(String note) {
        this.notes = note;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        this.OrderID = orderID;
    }

    public int getTotal_items() {
        return total_items;
    }

    public void setTotal_items(int total_items) {
        this.total_items = total_items;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.items);
        dest.writeString(this.address);
        dest.writeString(this.date);
        dest.writeString(this.notes);
        dest.writeString(this.email);
        dest.writeString(this.status);
        dest.writeInt(this.OrderID);
        dest.writeInt(this.total_items);
        dest.writeDouble(this.price);
    }

    protected CompleteOrderData(Parcel in) {
        this.items = in.createTypedArrayList(OrderData.CREATOR);
        this.address = in.readString();
        this.date = in.readString();
        this.notes = in.readString();
        this.email = in.readString();
        this.status = in.readString();
        this.OrderID = in.readInt();
        this.total_items = in.readInt();
        this.price = in.readDouble();
    }

    public static final Parcelable.Creator<CompleteOrderData> CREATOR = new Parcelable.Creator<CompleteOrderData>() {
        @Override
        public CompleteOrderData createFromParcel(Parcel source) {
            return new CompleteOrderData(source);
        }

        @Override
        public CompleteOrderData[] newArray(int size) {
            return new CompleteOrderData[size];
        }
    };
}