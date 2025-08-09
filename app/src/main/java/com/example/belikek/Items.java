package com.example.belikek;

import java.util.List;

// Items class
public class Items {
    private CakeBase cake_base;
    private List<Decoration> decorations;
    private int customizatoin_total; // Note: keeping the typo as it appears in Firestore
    private int final_price;
    private String product_id;
    private String product_name;
    private int quantity;
    private long base_price;
    private long subtotal;
    private long totalEachCake;
    private List customizations;

    // Required empty constructor for Firestore
    public Items() {}

    public Items(CakeBase cake_base, List<Decoration> decorations,
                 int customizatoin_total, int final_price, String product_id,
                 String product_name, int quantity, long base_price, long subtotal) {
        this.cake_base = cake_base;
        this.decorations = decorations;
        this.customizatoin_total = customizatoin_total;
        this.final_price = final_price;
        this.product_id = product_id;
        this.product_name = product_name;
        this.quantity = quantity;
        this.base_price = base_price;
        this.subtotal = subtotal;
        this.totalEachCake = base_price * quantity;
    }

    // Getters and Setters
    public List getCustomizations() { return customizations; }
    public void setCustomizations(List customizations) { this.customizations = customizations; }

    public CakeBase getCake_base() { return cake_base; }
    public void setCake_base(CakeBase cake_base) { this.cake_base = cake_base; }

    public List<Decoration> getDecorations() { return decorations; }
    public void setDecorations(List<Decoration> decorations) { this.decorations = decorations; }

    public int getCustomizatoin_total() { return customizatoin_total; }
    public void setCustomizatoin_total(int customizatoin_total) { this.customizatoin_total = customizatoin_total; }

    public int getFinal_price() { return final_price; }
    public void setFinal_price(int final_price) { this.final_price = final_price; }

    public String getProduct_id() { return product_id; }
    public void setProduct_id(String product_id) { this.product_id = product_id; }

    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public long getSubtotal() { return subtotal; }
    public void setSubtotal(long subtotal) { this.subtotal = subtotal; }

    public long getBase_price() { return base_price; }
    public void setBase_price(long base_price) { this.base_price = base_price; }

    // Helper methods for your layout
    public String getFormattedPrice() {
        return "RM " + base_price + ".00";
    }

    public String getCakeBaseDescription() {
        return cake_base != null ? cake_base.getOption_name() : "";
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

    public long getTotalEachCake() {
        return totalEachCake;
    }

    public void setTotalEachCake(long totalEachCake) {
        this.totalEachCake = totalEachCake;
    }
}