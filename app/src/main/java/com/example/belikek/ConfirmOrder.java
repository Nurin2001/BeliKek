package com.example.belikek;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOrder extends AppCompatActivity {

    private static final int REQUEST_CODE_B = 100;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<Items> cartItems;
    FirebaseFirestore db;
    DecimalFormat df = new DecimalFormat("0.00");
    long subtotal = 0;
    String bankCode;

    ConstraintLayout choosePaymethodLayout;
    TextView finalPriceTv, grandPriceTv, subtotalTv;
    Button confirmOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        finalPriceTv = findViewById(R.id.grand_total_tv);
        grandPriceTv = findViewById(R.id.final_total_tv);
        subtotalTv = findViewById(R.id.amount_tv);

        choosePaymethodLayout = findViewById(R.id.choose_pay_method_btn);

        choosePaymethodLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmOrder.this, PaymentMethod.class);
                startActivityForResult(intent, REQUEST_CODE_B);
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        // Check network first
        if (isNetworkAvailable(this)) {
            Log.d("Network", "Internet is available");
            loadOrderData();
        } else {
            Log.e("Network", "No internet connection");
        }

        cartItems = new ArrayList<>();
        // Set up adapter
        adapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(adapter);

        setupAdapterListeners();

        confirmOrderBtn  = findViewById(R.id.order_now_btn);
        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment(bankCode, subtotal);
            }
        });
    }

//    get value from payment method page
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        payment method
        if (requestCode == REQUEST_CODE_B && resultCode == RESULT_OK) {
            if (data != null) {
                confirmOrderBtn.setBackgroundResource(R.drawable.enabled_rounded_btn);
                TextView choosePaymethodTv = findViewById(R.id.choose_pay_method_tv);
                LinearLayout payMethodLayout = findViewById(R.id.cart_pay_method_layout);
                choosePaymethodTv.setVisibility(View.GONE);
                payMethodLayout.setVisibility(View.VISIBLE);
                String payMethod = data.getStringExtra("pay_method");
                String bankName = data.getStringExtra("bank_name");
                bankCode = data.getStringExtra("bank_code");
                TextView paymethodTv = findViewById(R.id.cart_pay_method_tv);
                TextView bankNameTv = findViewById(R.id.cart_bank_name_tv);
                paymethodTv.setText(payMethod);
                bankNameTv.setText(bankName);

                TextView transactionFeeTv = findViewById(R.id.transaction_fee_tv);
                transactionFeeTv.setText("RM1.00");
                subtotal = subtotal + 1;
                updateGrandTotal(finalPriceTv, grandPriceTv, subtotal);
            }
        }

//        payment result
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK && data != null) {
                String paymentStatus = data.getStringExtra("payment_status");
                String transactionId = data.getStringExtra("transaction_id");

                switch (paymentStatus) {
                    case "success":
                        // Handle successful payment
                        Log.d("PAYMENT", "Payment successful: " + transactionId);
                        // Update your UI, save transaction, etc.
                        break;
                    case "pending":
                        // Handle pending payment
                        Log.d("PAYMENT", "Payment pending");
                        break;
                    case "failed":
                        // Handle failed payment
                        Log.d("PAYMENT", "Payment failed");
                        break;
                }
            }
        }
    }

//    show items in cart
    private void loadOrderData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("orders").document("order_0001")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Read the items array manually
                        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) documentSnapshot.get("items");
                        List<Items> itemsList = new ArrayList<>();

                        subtotal = 0;
                        if (itemsData != null) {
                            for (Map<String, Object> singleItem : itemsData) {
                                Items item = new Items();

                                // Read base_price
                                if (singleItem.get("base_price") != null) {
                                    long basePriceNum = (long) singleItem.get("base_price");
                                    item.setBase_price(basePriceNum);
                                    Log.d(TAG, "base amount: " + basePriceNum);
                                    subtotal = subtotal + basePriceNum;
                                    item.setSubtotal(subtotal);
                                }

                                // Read nested customizations object
                                Map<String, Object> customizations = (Map<String, Object>) singleItem.get("customizations");

                                if (customizations != null) {
                                    // Read cake_base object
                                    Map<String, Object> cakeBaseData = (Map<String, Object>) customizations.get("cake_base");
                                    if (cakeBaseData != null) {
                                        CakeBase cakeBase = new CakeBase();
                                        cakeBase.setOption_id((String) cakeBaseData.get("option_id"));
                                        cakeBase.setOption_name((String) cakeBaseData.get("option_name"));
                                        if (cakeBaseData.get("price_modifier") != null) {
                                            cakeBase.setPrice_modifier(((Number) cakeBaseData.get("price_modifier")).intValue());
                                        }
                                        item.setCake_base(cakeBase);
                                    }

                                    // Read decorations array
                                    List<Map<String, Object>> decorationsData = (List<Map<String, Object>>) customizations.get("decorations");
                                    if (decorationsData != null) {
                                        List<Decoration> decorations = new ArrayList<>();
                                        for (Map<String, Object> decorationData : decorationsData) {
                                            Decoration decoration = new Decoration();
                                            decoration.setOption_name((String) decorationData.get("option_name"));
                                            decoration.setOtion_id((String) decorationData.get("option_id"));
                                            if (decorationData.get("price_modifier") != null) {
                                                decoration.setPrice_modifier(((Number) decorationData.get("price_modifier")).intValue());
                                            }
                                            decorations.add(decoration);
                                        }
                                        item.setDecorations(decorations);
                                    }
                                }

                                // Add other fields if they exist in your Firestore
                                if (singleItem.get("product_name") != null) {
                                    item.setProduct_name((String) singleItem.get("product_name"));
                                }
                                if (singleItem.get("quantity") != null) {
                                    item.setQuantity(((Number) singleItem.get("quantity")).intValue());
                                    item.setTotalEachCake(item.getQuantity() * item.getBase_price());
                                }
                                if (singleItem.get("final_price") != null) {
                                    item.setFinal_price(((Number) singleItem.get("final_price")).intValue());
                                }

                                itemsList.add(item);
                            }
                            subtotalTv.setText("RM" + df.format(subtotal));
                        }

                        subtotal = subtotal + 5;
                        updateGrandTotal(finalPriceTv, grandPriceTv, subtotal);

                        Log.d(TAG, "Items loaded: " + itemsList.size());

                        // Clear existing data and add new
                        cartItems.clear();
                        cartItems.addAll(itemsList);

                        // Notify adapter of data change
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading data", e);
                    Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateGrandTotal(TextView finalPriceTv, TextView  grandPriceTv, long finalPrice) {
        finalPriceTv.setText("RM" + df.format(finalPrice));
        grandPriceTv.setText("RM" + df.format(finalPrice));
    }

//    adapter listener for cart
    private void setupAdapterListeners() {
        adapter.setOnCartItemClickListener(new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onEditClick(int position) {
//                adapter.removeItem(position);
            }

            @Override
            public void onIncreaseClick(int position) {
                Items item = cartItems.get(position);
                int newQuantity = item.getQuantity() + 1;
                adapter.updateQuantity(position, newQuantity);
//                adapter.updateCakePrice(position, newQuantity * item.getBase_price());
                long newFinalPrice = item.getTotalEachCake() + subtotal;
                updateGrandTotal(finalPriceTv, grandPriceTv, newFinalPrice);
            }

            @Override
            public void onDecreaseClick(int position) {
                Items item = cartItems.get(position);

                if (item.getQuantity() - 1 == 0) {
                    adapter.removeItem(position);
                    return;
                }
                int newQuantity = item.getQuantity() - 1;
                adapter.updateQuantity(position, newQuantity);
//                adapter.updateCakePrice(position, newQuantity * item.getBase_price());
            }
        });
    }

//    check got internet or not
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities activeNetwork = connectivityManager.getNetworkCapabilities(network);
        if (activeNetwork == null) return false;

        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
    }

//    make the payment
    private void makePayment(String bankCode, long totalAmount) {
        String userSecretKey = "sh02eahr-60s5-z9hj-trxt-3hw4ntisy07h"; // Get from ToyyibPay dashboard
        String categoryCode = "uxi1zyfd"; // Get from ToyyibPay dashboard

        ToyyibPayAPI apiService = ToyyibPayClient.getApiService();
        Call<ResponseBody> call = apiService.createBillRaw(
                userSecretKey,
                categoryCode,
                "Payment for Order",
                "Payment for your order",
                "1", // billPriceSetting (1 = Fixed Price)
                "1", // billPayorInfo (1 = Required)
                df.format(totalAmount*100),
//                "https://yourapp.com/return",
//                "https://yourapp.com/callback",
                generateReferenceNo(),
                "Customer Name",
                "maisarahnurin38@gmail.com",
                "0123456789",
                "0", // billSplitPayment (0 = No)
                "", // billSplitPaymentArgs (empty if no split)
                "0" // billPaymentChannel (0 = FPX)
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseString = response.body().string();
                    Log.d("CREATE_BILL_RAW", "Raw response: " + responseString);

                    // Parse as JSONArray since the response is an array
                    JSONArray jsonArray = new JSONArray(responseString);
                    if (jsonArray.length() > 0) {
                        JSONObject firstObject = jsonArray.getJSONObject(0);
                        String billCode = firstObject.getString("BillCode");

                        Log.d("CREATE_BILL_SUCCESS", "BillCode: " + billCode);

                        String paymentUrl = "https://toyyibpay.com/" + billCode + "?bank_code=" + bankCode;
                        openPaymentPage(paymentUrl);
                    } else {
                        Log.e("CREATE_BILL_ERROR", "Empty response array");
                    }

                } catch (Exception e) {
                    Log.e("CREATE_BILL_ERROR", "Error parsing response: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("CREATE_BILL_ERROR", "Network error: " + t.getMessage());
                Toast.makeText(ConfirmOrder.this, "Payment failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generateReferenceNo() {
        return "REF" + System.currentTimeMillis();
    }

    private void openPaymentPage(String paymentUrl) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("payment_url", paymentUrl);
        startActivityForResult(intent, 1001);
    }

}