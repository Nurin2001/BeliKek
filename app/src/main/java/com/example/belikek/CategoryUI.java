package com.example.belikek;

public class CategoryUI {
    public final String id;
    public final String label;
    public String imageUrl;

    public CategoryUI(String id, String label, String imageUrl) {
        this.id = id; this.label = label; this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
