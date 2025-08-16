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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

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

import static com.example.belikek.Constants.*;

public class ConfirmOrder extends AppCompatActivity {
    private static final int REQUEST_CODE_B = 100;

    private CartAdapter adapter;

    private List<Items> cartItems;
    DecimalFormat df = new DecimalFormat("0.00");
    Double subtotal = 0.00;
    Double finalPrice = 0.00;
    Double transactionFee = 0.00;
    int totalQuantity = 0;
    String bankCode;
    String billCode;
    String orderDocument;
    FirebaseFirestore db;

    private RecyclerView recyclerView;
    ConstraintLayout choosePaymethodLayout;
    TextView finalQuantityTv, finalPriceTv, grandPriceTv, subtotalTv;
    Button confirmOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        finalQuantityTv = findViewById(R.id.final_quantity_tv);
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
                makePayment(bankCode, 1);
            }
        });

        ImageButton backBtn = findViewById(R.id.confirmorder_back_btn);
        backBtn.setOnClickListener(v -> finish());
    }

    private ActivityResultLauncher<Intent> paymentGatewayLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String value = data.getStringExtra("status");

                        //status done return from payment gateway page when back btn is pressed
                        if (value.equals("done")) {
                            readBillCodeFromDb();
                        }
                    }
                }
            }
    );

//    get value from payment method page
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        payment method
        if (requestCode == REQUEST_CODE_B && resultCode == RESULT_OK) {
            if (data != null) {
                //tukar warna button order now
                confirmOrderBtn.setBackgroundResource(R.drawable.enabled_rounded_btn);
                confirmOrderBtn.setEnabled(true);

                // set text payment method
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

                // set text transaction fee
                TextView transactionFeeTv = findViewById(R.id.transaction_fee_tv);
                setTransactionFee(data.getDoubleExtra("transaction_fee", 1.00));
                transactionFeeTv.setText("RM" + df.format(getTransactionFee()));

                //subtotal
                Log.d("subtotal after payment method:", String.valueOf(getSubtotal()));
                // update the final/grand total price
                updateGrandTotal(finalPriceTv, grandPriceTv, getFinalPrice() + getTransactionFee());
            }
        }
    }

    //    adapter listener for cart
    private void setupAdapterListeners() {
        adapter.setOnCartItemClickListener(new CartAdapter.OnCartItemClickListener() {
            @Override
            public void onIncreaseClick(int position) {
                Items item = cartItems.get(position);
                setTotalQuantity(getTotalQuantity() + 1);
                int newQuantity = item.getQuantity() + 1;
                adapter.updateQuantity(position, newQuantity);

                // set the final price text
                setFinalPrice(getFinalPrice() + item.getBase_price());
                updateGrandTotal(finalPriceTv, grandPriceTv, getFinalPrice());
                updateQuantityFirestore(position, newQuantity);

                // set the amount text
                setSubtotal(getSubtotal() + item.getBase_price());
                subtotalTv.setText("RM" + df.format(getSubtotal()));
            }

            @Override
            public void onDecreaseClick(int position) {
                Items item = cartItems.get(position);
                // set the final price text
                setFinalPrice(getFinalPrice() - item.getBase_price());
                updateGrandTotal(finalPriceTv, grandPriceTv, getFinalPrice());

                setTotalQuantity(getTotalQuantity() - 1);
                if (item.getQuantity() - 1 == 0) {
                    adapter.removeItem(position);
                    String orderId = "order_0001";
                    removeItemFromDb(item.getProduct_id(), orderId);
                    return;
                }
                int newQuantity = item.getQuantity() - 1;
                adapter.updateQuantity(position, newQuantity);


                updateQuantityFirestore(position, newQuantity);

                //set the amount text
                setSubtotal(getSubtotal() - item.getBase_price());
                subtotalTv.setText("RM" + df.format(getSubtotal()));
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

    //    show items in cart
    private void loadOrderData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = "user_123";

        db.collection(ORDERS_COLLECTION)
                .whereEqualTo(USER_ID_FIELD, userId)
                .whereEqualTo(PAYMENT_STATUS_FIELD, 4)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Read the items array manually
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) documentSnapshot.get(ITEMS_FIELD);
                            List<Items> itemsList = new ArrayList<>();

                            if (itemsData != null) {
                                for (Map<String, Object> singleItem : itemsData) {
                                    Items item = new Items();

                                    // Read base_price
                                    if (singleItem.get(BASE_PRICE_FIELD) != null) {
                                        Double basePrice = ((Number) singleItem.get(BASE_PRICE_FIELD)).doubleValue();
                                        item.setBase_price(basePrice);
                                    }

                                    // Read nested customizations object
                                    Map<String, Object> customizations = (Map<String, Object>) singleItem.get(CUSTOMIZATIONS_FIELD);

                                    if (customizations != null) {
                                        // Read cake_base object
                                        Map<String, Object> cakeBaseData = (Map<String, Object>) customizations.get(CAKE_BASE_FIELD);
                                        if (cakeBaseData != null) {
                                            CakeBase cakeBase = new CakeBase();
                                            cakeBase.setOption_id((String) cakeBaseData.get(OPTION_ID_FIELD));
                                            cakeBase.setOption_name((String) cakeBaseData.get(OPTION_NAME_FIELD));
                                            item.setCake_base(cakeBase);
                                        }

                                        // Read fillings object
                                        Map<String, Object> fillingsData = (Map<String, Object>) customizations.get(FILLINGS_FIELD);
                                        if (fillingsData != null) {
                                            Fillings filling = new Fillings();
                                            filling.setOption_id((String) fillingsData.get(OPTION_ID_FIELD));
                                            filling.setOption_name((String) fillingsData.get(OPTION_NAME_FIELD));
                                            item.setFillings(filling);
                                        }

                                        // Read decorations array
                                        List<Map<String, Object>> decorationsData = (List<Map<String, Object>>) customizations.get(DECORATIONS_FIELD);
                                        if (decorationsData != null) {
                                            List<Decoration> decorations = new ArrayList<>();
                                            for (Map<String, Object> decorationData : decorationsData) {
                                                Decoration decoration = new Decoration();
                                                decoration.setOption_name((String) decorationData.get(OPTION_NAME_FIELD));
                                                decoration.setOption_id((String) decorationData.get(OPTION_ID_FIELD));
                                                decorations.add(decoration);
                                            }
                                            item.setDecorations(decorations);
                                        }
                                    }

                                    // Add other fields if they exist in your Firestore
                                    // read product name
                                    if (singleItem.get(PRODUCT_NAME_FIELD) != null) {
                                        item.setProduct_name((String) singleItem.get(PRODUCT_NAME_FIELD));
                                    }

                                    // get product_id
                                    if (singleItem.get(PRODUCT_ID_FIELD) != null) {
                                        item.setProduct_id((String) singleItem.get(PRODUCT_ID_FIELD));
                                    }

                                    // read quantity
                                    if (singleItem.get(QUANTITY_FIELD) != null) {
                                        item.setQuantity(((Number) singleItem.get(QUANTITY_FIELD)).intValue());
                                        item.setTotalEachCake(item.getQuantity() * item.getBase_price());
                                        setTotalQuantity(getTotalQuantity() + item.getQuantity());
                                    }

                                    // Read image_url and load image
                                    if (singleItem.get(IMAGE_URL_FIELD) != null) {
                                        String imageUrl = (String) singleItem.get(IMAGE_URL_FIELD);
                                        item.setImageUrl(imageUrl); // Make sure your Items class has this setter
                                    }

                                    itemsList.add(item);

                                    // set subTotal - subTotal is the price shown next to the Amount label
                                    setSubtotal(getSubtotal() + (item.getBase_price() * item.getQuantity()));
                                    setFinalPrice(getSubtotal());
                                }
                            }

                            subtotalTv.setText("RM" + df.format(getSubtotal()));
                            setFinalPrice(getFinalPrice() + 5);
                            updateGrandTotal(finalPriceTv, grandPriceTv, getFinalPrice());

                            // Clear existing data and add new
                            cartItems.clear();
                            cartItems.addAll(itemsList);

                            // Notify adapter of data change
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e(TAG, "Error getting pending order: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading data", e);
                    Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // remove item from db
    private void removeItemFromDb(String productId, String orderId) {
        db.collection(ORDERS_COLLECTION)
                .whereEqualTo(ID_FIELD, orderId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get(ITEMS_FIELD);

                        if (items != null) {
                            // Remove items with matching product_id
                            items.removeIf(item -> productId.equals(item.get(PRODUCT_ID_FIELD)));

                            // Update the document
                            documentSnapshot.getReference().update(ITEMS_FIELD, items)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("FIRESTORE", "Item removed successfully");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FIRESTORE", "Error removing item", e);
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FirestoreQuery", "Error getting documents", e);
                });

    }

    //    make the payment
    private void makePayment(String bankCode, double totalAmount) {
        String custName = "Test Customer", billEmail = "abc123@gmail.com", billPhone = "0123456789";

        // CHANGE WHEN HAND OVER
        String userSecretKey = TOYYIBPAY_USER_SECRET_CODE; // Get from ToyyibPay dashboard
        String categoryCode = TOYYIBPAY_CATEGORY_CODE; // Get from ToyyibPay dashboard

        ToyyibPayAPI apiService = ToyyibPayClient.getApiService();
        Call<ResponseBody> call = apiService.createBillRaw(
                userSecretKey,
                categoryCode,
                "Payment for Order",
                "Payment for your order",
                "1", // billPriceSetting (1 = Fixed Price)
                "1", // billPayorInfo (1 = Required)
                df.format(totalAmount*100),
                generateReferenceNo(),
                custName,
                billEmail,
                billPhone,
                "0", // billSplitPayment (0 = No)
                "", // billSplitPaymentArgs (empty if no split)
                "0" // billPaymentChannel (0 = FPX)
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseString = response.body().string();

                    // Parse as JSONArray since the response is an array
                    JSONArray jsonArray = new JSONArray(responseString);
                    if (jsonArray.length() > 0) {
                        JSONObject firstObject = jsonArray.getJSONObject(0);
                        String billCode = firstObject.getString("BillCode");

                        String paymentUrl = TOYYIBPAY_URL + billCode;
                        addBillCodeField(billCode);
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

    private void updateGrandTotal(TextView finalPriceTv, TextView  grandPriceTv, double finalPrice) {
        finalPriceTv.setText("RM" + df.format(finalPrice));
        grandPriceTv.setText("RM" + df.format(finalPrice));
    }

    private void addBillCodeField(String billCode) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(BILL_CODE_FIELD, billCode);
        updates.put(UPDATED_AT_FIELD, FieldValue.serverTimestamp());

        String userId = "user_123";

        db.collection(ORDERS_COLLECTION)
                .whereEqualTo(USER_ID_FIELD, userId)
                .whereEqualTo(PAYMENT_STATUS_FIELD, 4)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Document exists - add item to existing document
                            DocumentSnapshot existingDoc = queryDocumentSnapshots.getDocuments().get(0); // Get first matching document
                            String existingDocId = existingDoc.getId();
                            setDocumentId(existingDocId);

                            db.collection(ORDERS_COLLECTION)
                                    .document(existingDocId)
                                    .update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "Bill code added successfully!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error adding bill code", e);
                                        }
                                    });
                        }
                    }
                });
    }

    private void setDocumentId(String documentId) {
        this.orderDocument = documentId;
    }

    private String getDocumentId() {
        return this.orderDocument;
    }

    private void readBillCodeFromDb() {
        String userId = "user_123";

        db.collection(ORDERS_COLLECTION)
                .document(getDocumentId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            String billCode = document.getString(BILL_CODE_FIELD);
                            if (billCode != null) {
                                Log.d("Firestore", "Bill Code: " + billCode);
                                setBillCode(billCode);
                                checkPaymentStatus(billCode);
                                // Use the billCode value here
                            } else {
                                Log.d("Firestore", "Bill code field not found");
                            }
                        } else {
                            Log.d("Firestore", "Document not found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error reading bill code", e);
                    }
                });
    }

    private void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    private String getBillCode() {
        return billCode;
    }

    private void updatePaymentStatusInDb(int paymentStatus) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(PAYMENT_STATUS_FIELD, paymentStatus);
        updates.put(UPDATED_AT_FIELD, FieldValue.serverTimestamp());

        String userId = "user_123";

        db.collection(ORDERS_COLLECTION)
                .whereEqualTo(USER_ID_FIELD, userId)
                .whereEqualTo(PAYMENT_STATUS_FIELD, 4)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            documentSnapshot.getReference().update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Payment status updated successfully!");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error updating payment status", e);
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FirestoreQuery", "Error getting documents", e);
                });
    }

    private String generateReferenceNo() {
        return "REF" + System.currentTimeMillis();
    }

    // open payment gateway
    private void openPaymentPage(String paymentUrl) {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PAYMENT_URL_FIELD, paymentUrl);
        paymentGatewayLauncher.launch(intent);
    }

    private void checkPaymentStatus(String billCode) {
        ToyyibPayAPI apiService = ToyyibPayClient.getApiService();
        Call<ResponseBody> call = apiService.getBillStatus(billCode);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String responseString = response.body().string();

                    JSONArray jsonArray = new JSONArray(responseString);
                    if (jsonArray.length() > 0) {
                        JSONObject transaction = jsonArray.getJSONObject(0);
                        String billPaymentStatus = transaction.getString(TOYYIBPAY_BILL_PAYMENT_STATUS);

                        runOnUiThread(() -> {
                            updatePaymentStatusInDb(Integer.parseInt(billPaymentStatus));
                            if ("1".equals(billPaymentStatus)) {
                                showPaymentResult("success", "Payment completed successfully!");
                            } else {
                                showPaymentResult("pending", "Payment is being processed...");
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            showPaymentResult("unknown", "Unable to verify payment status");
                        });
                    }

                } catch (Exception e) {
                    Log.e("PAYMENT_STATUS_ERROR", "Error: " + e.getMessage());
                    runOnUiThread(() -> {
                        showPaymentResult("error", "Error checking payment status");
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("PAYMENT_STATUS_ERROR", "Network error: " + t.getMessage());
                runOnUiThread(() -> {
                    showPaymentResult("error", "Network error checking payment");
                });
            }
        });
    }

    // will show alert for payment status
    private void showPaymentResult(String status, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (status) {
            case "success":
                builder.setTitle("Payment Successful")
                        .setMessage(message)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("OK", (dialog, which) -> {
                            setResult(RESULT_OK);

                            // go back to home activity once payment is success
                            Intent intent = new Intent(ConfirmOrder.this, MainActivity2.class);
                            startActivity(intent);
                            finish();
                        });
                break;

            case "pending":
                builder.setTitle("Payment Processing")
                        .setMessage(message)
                        .setPositiveButton("Check Again", (dialog, which) -> {
                            checkPaymentStatus(getBillCode());
                        })
                        .setNegativeButton("Close", (dialog, which) -> {
                            setResult(RESULT_CANCELED);
                            finish();
                        });
                break;

            default:
                builder.setTitle("Payment Status")
                        .setMessage(message)
                        .setPositiveButton("Close", (dialog, which) -> {
                            setResult(RESULT_CANCELED);
                            finish();
                        });
        }

        builder.setCancelable(false).show();
    }

    private void updateQuantityFirestore(int position, int newQuantityValue) {
        // Parameters
        String userId = "user_123";

        db.collection(ORDERS_COLLECTION)
                .whereEqualTo(USER_ID_FIELD, userId)
                .whereEqualTo(PAYMENT_STATUS_FIELD, 4)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Document exists - add item to existing document
                        DocumentSnapshot existingDoc = queryDocumentSnapshots.getDocuments().get(0); // Get first matching document
                        String existingDocId = existingDoc.getId();

                        List<Map<String, Object>> items = (List<Map<String, Object>>) existingDoc.get(ITEMS_FIELD);
//
                            if (items != null && position < items.size()) {
                                // Update only the quantity
                                Map<String, Object> item = items.get(position);
                                item.put("quantity", newQuantityValue);

                                // Update the document
                                Map<String, Object> updates = new HashMap<>();
                                updates.put(TOTAL_AMOUNT_FIELD, getSubtotal());
                                updates.put(ITEMS_FIELD, items);
                                updates.put(UPDATED_AT_FIELD, FieldValue.serverTimestamp());

                                db.collection(ORDERS_COLLECTION)
                                        .document(existingDocId)
                                        .update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Firestore", "Quantity updated successfully!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Firestore", "Error updating quantity", e);
                                            }
                                        });
                            } else {
                            Log.d("Firestore", "Order not found");
                            }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error getting order", e);
                    }
                });
    }

    // set the amount price text
    private void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    // get the amount price text
    private Double getSubtotal() {
        return this.subtotal;
    }

    // set the final price text
    private void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    // get the final price text
    private Double getFinalPrice() {
        return this.finalPrice;
    }

    private void setTransactionFee(Double fee) { this.transactionFee = fee; }

    private Double getTransactionFee() { return this.transactionFee; }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
        finalQuantityTv.setText(String.valueOf(totalQuantity) + " " + (totalQuantity > 1 ? "items" : "item"));
    }
}