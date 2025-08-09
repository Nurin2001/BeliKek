package com.example.belikek;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateBillResponse {
    @SerializedName("BillCode")
    private String billCode;

    @SerializedName("status")
    private String status;

    @SerializedName("msg")
    private String message;

    // Getters and setters
    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
