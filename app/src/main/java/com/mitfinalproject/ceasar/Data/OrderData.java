package com.mitfinalproject.ceasar.Customer;

public class OrderData {

    private int ID;
    private String Name;
    private int Quantity;
    private double Price;

    public OrderData(int ID, String name, int quantity, double price) {
        this.ID = ID;
        Name = name;
        Quantity = quantity;
        Price = price;
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
}
