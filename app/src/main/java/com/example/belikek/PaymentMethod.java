package com.example.belikek;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethod extends AppCompatActivity {

    private CardView cardOnlineBanking;
    private CheckBox checkboxOnlineBanking;
    private List<Bank> bankList;
    private Bank selectedBank;
    BottomSheetDialog bottomSheetDialog;
    int paymentMethod; // 1 = online banking
    Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_method);

        confirmBtn = findViewById(R.id.confirm_pay_method_btn);
        initViews();
        setupClickListeners();
        fetchBankList();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent with result data
                Intent resultIntent = new Intent();
                switch (paymentMethod) {
                    case 1:
                        resultIntent.putExtra("pay_method", "Online Banking");
                        resultIntent.putExtra("bank_name", selectedBank.getBankName());
                        resultIntent.putExtra("bank_code", selectedBank.getBankCode());
                        resultIntent.putExtra("transaction_fee", 1.00);
                        break;
                }

                // Set result and finish
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void initViews() {
        cardOnlineBanking = findViewById(R.id.card_online_banking);
        checkboxOnlineBanking = findViewById(R.id.checkbox_online_banking);
    }

    private void setupClickListeners() {
        cardOnlineBanking.setOnClickListener(v -> {
            if (bankList != null && !bankList.isEmpty()) {
                showBankSelectionBottomSheet();
            } else {
                Toast.makeText(this, "Loading banks...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBankSelectionBottomSheet() {
        BankSelectionBottomSheet bottomSheet = BankSelectionBottomSheet.newInstance(bankList);
        bottomSheet.setOnBankSelectedListener(bank -> {
            paymentMethod = 1;
            selectedBank = bank;
            checkboxOnlineBanking.setChecked(true);
            Toast.makeText(this, "Selected: " + bank.getBankName(), Toast.LENGTH_SHORT).show();
            TextView bankNameTv = findViewById(R.id.bank_name_tv);
            bankNameTv.setText(bank.getBankName());
            bankNameTv.setVisibility(View.VISIBLE);
            confirmBtn.setBackgroundResource(R.drawable.enabled_rounded_btn);
            confirmBtn.setEnabled(true);
        });
        bottomSheet.show(getSupportFragmentManager(), "BankSelectionBottomSheet");
    }

    private void fetchBankList() {
        ToyyibPayAPI apiService = ToyyibPayClient.getApiService();
        Call<List<Bank>> call = apiService.getBankList();

        call.enqueue(new Callback<List<Bank>>() {
            @Override
            public void onResponse(Call<List<Bank>> call, Response<List<Bank>> response) {
                Log.d("API_RESPONSE", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    bankList = response.body();
                    Log.d("API_RESPONSE", "Loaded " + bankList.size() + " banks from API");

                    // Log each bank for debugging
                    for (Bank bank : bankList) {
                        Log.d("API_RESPONSE", "Bank: " + bank.getBankName() + " (" + bank.getBankCode() + ")");
                    }

                    Toast.makeText(PaymentMethod.this,
                            "Loaded " + bankList.size() + " banks",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Log.e("API_ERROR", "Response not successful: " + response.code());
                    Toast.makeText(PaymentMethod.this,
                            "Failed to load banks",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Bank>> call, Throwable t) {
                Log.e("API_ERROR", "Network error: " + t.getMessage());
                Toast.makeText(PaymentMethod.this,
                        "Failed to load banks: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}