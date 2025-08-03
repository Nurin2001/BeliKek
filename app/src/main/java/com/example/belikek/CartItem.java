package com.example.belikek;

public class CartItem {
    private String productName;
    private String price;
    private String description1;
    private String description2;
    private String description3;
    private int imageResourceId;
    private int quantity;

    public CartItem(String name, String price, String description1, String description2, String description3, int imageResourceId, int quantity) {
        this.productName = name;
        this.price = price;
        this.description1 = description1;
        this.description2 = description2;
        this.description3 = description3;
        this.imageResourceId = imageResourceId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getName() { return productName; }
    public void setName(String name) { this.productName = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getDescription(int type) {
        switch(type) {
            case 1: return description1;
            case 2: return description2;
            case 3: return description3;
        }
        return "";
    }
    public void setDescription(int type, String description) {
        switch(type) {
            case 1: this.description1 = description; break;
            case 2: this.description2 = description; break;
            case 3: this.description3 = description; break;
        }
    }

    public int getImageResourceId() { return imageResourceId; }
    public void setImageResourceId(int imageResourceId) { this.imageResourceId = imageResourceId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
