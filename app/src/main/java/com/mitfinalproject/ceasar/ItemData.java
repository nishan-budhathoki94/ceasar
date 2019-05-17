package com.mitfinalproject.ceasar;

public class ItemData {

    private String name;
    private String category;
    private String size;
    private String price;
    private String availability;
    private int itemID;
    private String desc;

    public ItemData() {
    }


    public ItemData(String name, String category, String desc, String size, String price, String availability, int itemID) {
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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



}
