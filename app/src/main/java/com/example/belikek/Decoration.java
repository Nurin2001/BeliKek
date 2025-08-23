package com.example.belikek;

public class Decoration {
    private String option_name;
    private String option_id; // Note: keeping the typo as it appears in Firestore

    // Required empty constructor for Firestore
    public Decoration() {}

    // Getters and Setters
    public String getOption_name() { return option_name; }
    public void setOption_name(String option_name) { this.option_name = option_name; }

    public void setOption_id(String option_id) { this.option_id = option_id; }
}
