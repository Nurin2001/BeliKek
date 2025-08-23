package com.example.belikek;

public class Fillings {
    private String option_id;
    private String option_name;

    // Required empty constructor for Firestore
    public Fillings() {}

    // Getters and Setters
    public void setOption_id(String option_id) { this.option_id = option_id; }

    public String getOption_name() { return option_name; }
    public void setOption_name(String option_name) { this.option_name = option_name; }
}
