package com.example.belikek;

import java.util.List;

// Items class
public class Items {
    private CakeBase cake_base;
    private Fillings fillings;
    private List<Decoration> decorations;
    private String product_id;
    private String product_name;
    private int quantity;
    private double base_price;
    private double totalEachCake;
    private double subTotal;
    private String imageUrl;

    // Required empty constructor for Firestore
    public Items() {}

    public Items(CakeBase cake_base, Fillings fillings, List<Decoration> decorations, String product_id,
                 String product_name, int quantity, double base_price, String imageUrl) {
        this.cake_base = cake_base;
        this.fillings = fillings;
        this.decorations = decorations;
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.base_price = base_price;
        this.totalEachCake = base_price * quantity;
        this.subTotal = 0;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public CakeBase getCake_base() { return cake_base; }
    public void setCake_base(CakeBase cake_base) { this.cake_base = cake_base; }

    public Fillings getFillings() {
        return fillings;
    }

    public void setFillings(Fillings fillings) {
        this.fillings = fillings;
    }

    public List<Decoration> getDecorations() { return decorations; }
    public void setDecorations(List<Decoration> decorations) { this.decorations = decorations; }

    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }

    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getBase_price() { return base_price; }
    public void setBase_price(double base_price) { this.base_price = base_price; }

    // Helper methods for your layout
    public String getCakeBaseDescription() {
        return cake_base != null ? cake_base.getOption_name() : "";
    }

    public String getFillingsDescription() {
        return fillings != null ? fillings.getOption_name() : "";
    }

    public String getDecorationsDescription() {
        if (decorations == null || decorations.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < decorations.size(); i++) {
            sb.append(decorations.get(i).getOption_name());
            if (i < decorations.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public double getTotalEachCake() {
        return totalEachCake;
    }

    public void setTotalEachCake(double totalEachCake) {
        this.totalEachCake = totalEachCake;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}