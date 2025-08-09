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

    public String getOrder_no() { return order_no; }
    public void setOrder_no(String order_no) { this.order_no = order_no; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getTotal_amount() { return total_amount; }
    public void setTotal_amount(int total_amount) { this.total_amount = total_amount; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public Timestamp getCreated_at() { return created_at; }
    public void setCreated_at(Timestamp created_at) { this.created_at = created_at; }

    public List<Items> getItems() { return items; }
    public void setItems(List<Items> items) { this.items = items; }
}
