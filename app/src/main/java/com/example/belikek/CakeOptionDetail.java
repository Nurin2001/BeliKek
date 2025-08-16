package com.example.belikek;

public class CakeOptionDetail {
    private String optionName;
    private boolean isSelected;
    private String categoryId;
    private String optionId;

    public CakeOptionDetail(String optionName, String optionId,String categoryid, boolean isSelected) {
        this.optionName = optionName;
        this.optionId = optionId;
        this.isSelected = isSelected;
        this.categoryId = categoryid;
    }

    // Getters and setters
    public String getOptionName() { return optionName; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    public String getCategoryId() {
        return categoryId;
    }

    public String getOptionId() {
        return optionId;
    }
}
