package com.example.belikek;

import com.google.firebase.Timestamp;
import java.util.List;

// Main Order class
public class Order {
    private String id;
    private String order_no;
    private String status;
    private int total_amount;
    private String user_id;
    private Timestamp created_at;
    private List<Items> items;

    // Required empty constructor for Firestore
    public Order() {}

    public Order(String id, String order_no, String status, int total_amount,
                 String user_id, Timestamp created_at, List<Items> items) {
        this.id = id;
        this.order_no = order_no;
        this.status = status;
        this.total_amount = total_amount;
        this.user_id = user_id;
        this.created_at = created_at;
        this.items = items;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
