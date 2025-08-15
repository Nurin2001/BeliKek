package com.example.belikek;

public class Fillings {
    private String option_id;
    private String option_name;
    private int price_modifier;

    // Required empty constructor for Firestore
    public Fillings() {}

    public Fillings(String option_id, String option_name, int price_modifier) {
        this.option_id = option_id;
        this.option_name = option_name;
        this.price_modifier = price_modifier;
    }

    // Getters and Setters
    public String getOption_id() { return option_id; }
    public void setOption_id(String option_id) { this.option_id = option_id; }

    public String getOption_name() { return option_name; }
    public void setOption_name(String option_name) { this.option_name = option_name; }

    public int getPrice_modifier() { return price_modifier; }
    public void setPrice_modifier(int price_modifier) { this.price_modifier = price_modifier; }
}
