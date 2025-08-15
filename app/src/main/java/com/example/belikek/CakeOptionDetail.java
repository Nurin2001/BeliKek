package com.example.belikek;

public class CakeOptionDetail {
    private String optionName;
    private boolean isSelected;

    public CakeOptionDetail(String optionName, boolean isSelected) {
        this.optionName = optionName;
        this.isSelected = isSelected;
    }

    // Getters and setters
    public String getOptionName() { return optionName; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
