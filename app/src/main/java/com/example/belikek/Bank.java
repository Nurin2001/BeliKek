package com.example.belikek;

import com.google.gson.annotations.SerializedName;

public class Bank {
    @SerializedName("CODE")
    private String bankCode;

    @SerializedName("NAME")
    private String bankName;

    public Bank() {}

    public Bank(String bankCode, String bankName) {
        this.bankCode = bankCode;
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        return bankName; // For debugging
    }
}