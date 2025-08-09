package com.example.belikek;

public class Decoration {
    private String option_name;
    private String otion_id; // Note: keeping the typo as it appears in Firestore
    private int price_modifier;
    private Object fillings; // Can be null or other types

    // Required empty constructor for Firestore
    public Decoration() {}

    public Decoration(String option_name, String otion_id, int price_modifier, Object fillings) {
        this.option_name = option_name;
        this.otion_id = otion_id;
        this.price_modifier = price_modifier;
        this.fillings = fillings;
    }

    // Getters and Setters
    public String getOption_name() { return option_name; }
    public void setOption_name(String option_name) { this.option_name = option_name; }

    public String getOtion_id() { return otion_id; }
    public void setOtion_id(String otion_id) { this.otion_id = otion_id; }

    public int getPrice_modifier() { return price_modifier; }
    public void setPrice_modifier(int price_modifier) { this.price_modifier = price_modifier; }

    public Object getFillings() { return fillings; }
    public void setFillings(Object fillings) { this.fillings = fillings; }
}
