package com.example.belikek;

import java.util.List;

public class CakeOption {
    private String categoryName;
    private String pickText; // e.g., "Pick 1"
    private List<CakeOptionDetail> options;

    public CakeOption(String categoryName, String pickText, List<CakeOptionDetail> options) {
        this.categoryName = categoryName;
        this.pickText = pickText;
        this.options = options;
    }

    // Getters and setters
    public String getCategoryName() { return categoryName; }
    public String getPickText() { return pickText; }
    public List<CakeOptionDetail> getOptions() { return options; }
}