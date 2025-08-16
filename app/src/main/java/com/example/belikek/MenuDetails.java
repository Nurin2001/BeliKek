package com.example.belikek;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.belikek.Constants.*;

public class MenuDetails extends AppCompatActivity {

    private FirebaseFirestore db;

    private CakeOptionsService cakeOptionsService;
    private RecyclerView mainRecyclerView;
    private CakeOptionAdapter mainAdapter;
    private List<CakeOption> cakeOptions;
    private MenuItem menuitem;
    private int quantity = 1;
    private StringBuilder stringCakeOptions;
    private List<CakeOptionDetail> selectedCakeOptionDetails;

    public static final String EXTRA_TOTAL_DELTA = "extra_total_delta";
    public static final String EXTRA_CART_TOTAL  = "extra_cart_total";

    DecimalFormat df = new DecimalFormat("0.00");

    TextView cakeNameTv, priceTv, quantityTv;
    ImageView cakeImage;
    ImageButton minusBtn, addBtn;
    Button addToCartBtn, buyNowBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_details);

        db = FirebaseFirestore.getInstance();
        selectedCakeOptionDetails = new ArrayList<>();

        cakeNameTv = findViewById(R.id.detail_cake_name_tv);
        priceTv = findViewById(R.id.detail_subtotal_tv);
        cakeImage = findViewById(R.id.detail_cake_img);
        quantityTv = findViewById(R.id.detail_item_quantity);
        minusBtn = findViewById(R.id.detail_item_decrease);
        addBtn = findViewById(R.id.detail_item_increase);
        buyNowBtn = findViewById(R.id.buy_now_btn);
        addToCartBtn = findViewById(R.id.add_to_btn);

        mainRecyclerView = findViewById(R.id.main_recyclerciew_cake_option);
        cakeOptionsService = new CakeOptionsService();

        setupRecyclerView();
        setDetails();

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity == 1) return;
                updateQuantityAndSubtotal(--quantity);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantityAndSubtotal(++quantity);
            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToDb();
            }
        });

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToDb();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void setDetails() {
        Intent intent = getIntent();
        menuitem = intent.getParcelableExtra("menu_detail");

        cakeNameTv.setText(menuitem.getName());
        priceTv.setText("RM" + df.format(menuitem.getPrice()));

        if (menuitem.getImagePath() != null) {
            Glide.with(cakeImage.getContext())
                    .load(menuitem.getImagePath())
                    .placeholder(R.drawable.ic_cookies) // Add a placeholder image
                    .error(R.drawable.ic_cartoon) // Add an error image
                    .centerCrop()
                    .into(cakeImage);
        } else {
            cakeImage.setImageResource(R.drawable.ic_default_background);
        }

        if (menuitem.isHasCakeOptions()) {
            fetchCakeOptions();
        }
        else {
            buyNowBtn.setEnabled(true);
            addToCartBtn.setEnabled(true);

            buyNowBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_btn));
            addToCartBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.enabled_rounded_btn));
            buyNowBtn.setTextColor(R.color.buyNowBtnText);
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager mainLayoutManager = new LinearLayoutManager(this);
        mainRecyclerView.setLayoutManager(mainLayoutManager);
        mainRecyclerView.setHasFixedSize(true);
    }

    private void fetchCakeOptions() {
        cakeOptionsService.fetchAllCakeOptions(new CakeOptionsService.CakeOptionsCallback() {
            @Override
            public void onSuccess(List<CakeOption> fetchedOptions) {
                cakeOptions = fetchedOptions;

                // Use your existing adapter directly!
                mainAdapter = new CakeOptionAdapter(cakeOptions,
                        (categoryPosition, optionPosition, selectedOption) -> {
                            // Handle option selection
                            CakeOption category = cakeOptions.get(categoryPosition);

                            // Update UI
                            updateSelectedOptionsDisplay();
                            updateBuyingBtn();
                        }
                );
                mainRecyclerView.setAdapter(mainAdapter);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MenuDetails.this,
                        "Error loading options: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateSelectedOptionsDisplay() {
        TextView selectedOptionsTextView = findViewById(R.id.detail_cake_options_tv);

        stringCakeOptions = new StringBuilder();

        selectedCakeOptionDetails = new ArrayList<>();
        for (CakeOption category : cakeOptions) {
            for (CakeOptionDetail option : category.getOptions()) {
                List<CakeOptionDetail> detail = new ArrayList<>();
                if (option.isSelected()) {
                    if (stringCakeOptions.length() > 0) {
                        stringCakeOptions.append(", ");
                    }
                    stringCakeOptions.append(option.getOptionName());
                    selectedCakeOptionDetails.add(new CakeOptionDetail(option.getOptionName(), option.getOptionId(), option.getCategoryId(), option.isSelected()));
                }
            }
        }

        selectedOptionsTextView.setText(stringCakeOptions.toString());
    }


    @SuppressLint("ResourceAsColor")
    private void updateBuyingBtn() {
        // Check if all required options are selected
        boolean allRequiredSelected  = checkIfAllRequiredOptionsSelected();

        buyNowBtn.setEnabled(allRequiredSelected );
        addToCartBtn.setEnabled(allRequiredSelected );

        if (allRequiredSelected) {
            buyNowBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_outline_btn));
            addToCartBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.enabled_rounded_btn));
            buyNowBtn.setTextColor(R.color.buyNowBtnText);
        } else {
            buyNowBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.disabled_rounded_btn));
            addToCartBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.disabled_rounded_btn));
        }
    }

    private void updateQuantityAndSubtotal(int newQuantity) {
        quantityTv.setText(String.valueOf(newQuantity));
        priceTv.setText("RM" + df.format(menuitem.getPrice() * newQuantity));
    }

    private boolean checkIfAllRequiredOptionsSelected() {
        if (cakeOptions == null) return false;

        // Check if all required options are selected
        for (CakeOption category : cakeOptions) {
            boolean hasSelection = false;
            for (CakeOptionDetail option : category.getOptions()) {
                if (option.isSelected()) {
                    hasSelection = true;
                    break;
                }
            }
            if (!hasSelection) {
                return false;
            }
        }
        return true;
    }

    private void addItemToDb() {
        String userId = "user_123";
        Map<String, Object> newItem = newItem();
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

                            // Get current items array
                            List<Map<String, Object>> items = (List<Map<String, Object>>) existingDoc.get(ITEMS_FIELD);
                            if (items == null) {
                                items = new ArrayList<>();
                            }

                            // Check if item with same product_id already exists
                            boolean itemExists = false;
                            String newProductId = (String) newItem.get(PRODUCT_ID_FIELD);

                            double newTotal = 0.0;
                            Log.d("item size", String.valueOf(items.size()));

                            for (int i = 0; i < items.size(); i++) {
                                Map<String, Object> existingItem = items.get(i);
                                String existingProductId = (String) existingItem.get("product_id");

                                double basePrice = (double) newItem.get("base_price");
                                int quantity = (int) newItem.get("quantity");
                                double existingBase_price = (double) existingItem.get("base_price");
                                int existingQuantity = ((Long) existingItem.get("quantity")).intValue();

                                // if new added item already in order, update that item
                                if (newProductId.equals(existingProductId)) {
                                    // Item exists - check and merge decorations
                                    itemExists = true;
                                    Log.d("Firestore", "Item already exists. Checking decorations...");

                                    // Completely replace customizations with new ones
                                    Map<String, Object> newCustomizations = (Map<String, Object>) newItem.get("customizations");

                                    if (newCustomizations != null) {
                                        // Replace entire customizations with new data
                                        existingItem.put("customizations", newCustomizations);
                                        Log.d("Firestore", "Completely replaced all customizations with new selection");
                                    } else {
                                        // User deselected everything - remove customizations
                                        existingItem.put("customizations", null);
                                        Log.d("Firestore", "Removed all customizations (user deselected everything)");
                                    }

                                    // Update other item fields (quantity, prices, etc.)
                                    existingItem.put("quantity", newItem.get("quantity"));

                                    // Update the item in the array
                                    items.set(i, existingItem);
                                    newTotal -= (existingBase_price * existingQuantity);
                                }
//                                  Calculate new total by summing all items
                                newTotal += (Double) (basePrice * quantity) + (existingBase_price * existingQuantity);

                                Log.d("existing price", String.valueOf(existingBase_price));
                                Log.d("new item price", String.valueOf(existingBase_price));
                                Log.d("existing qty", String.valueOf(existingBase_price));
                                Log.d("new item qty", String.valueOf(existingBase_price));

                            }

                            // If item doesn't exist, add it as new item
                            if (!itemExists) {
                                items.add(newItem);

                                Log.d("Firestore", "Item doesn't exist. Adding as new item.");
                            }

                            // Update existing document
                            Map<String, Object> updates = new HashMap<>();

//                            updates.put("final_price", newTotal);
                            updates.put(ITEMS_FIELD, items);
                            updates.put(TOTAL_AMOUNT_FIELD, newTotal);
                            updates.put(UPDATED_AT_FIELD, FieldValue.serverTimestamp());

                            boolean finalItemExists = itemExists;
                            double finalNewTotal = newTotal;
                            db.collection(ORDERS_COLLECTION)
                                    .document(existingDocId)
                                    .update(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MenuDetails.this,"Item is added!", Toast.LENGTH_LONG).show();
                                            returnToMenuFragment(finalNewTotal);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error adding item to existing order", e);
                                        }
                                    });
                        }
                        else {
                            // Document doesn't exist - create new document
                            Log.d("Firestore", "No existing document found. Creating new order...");

                            // Create new order data with randomized document ID
                            Map<String, Object> newOrderData = new HashMap<>();

                            // Generate random order number and ID
                            String randomOrderId = UUID.randomUUID().toString();
                            String orderNumber = "ORD-2025-" + System.currentTimeMillis();

                            newOrderData.put("id", randomOrderId);
                            newOrderData.put("user_id", userId);
                            newOrderData.put("order_number", orderNumber);
                            newOrderData.put("status", "pending");
                            newOrderData.put("payment_status", 4);

                            // Create items array with the new item
                            List<Map<String, Object>> newItems = new ArrayList<>();
                            newItems.add(newItem);
                            newOrderData.put("items", newItems);

                            double basePrice = (double) newItem.get("base_price");
                            int qty = (int) newItem.get("quantity");

                            newOrderData.put("total_amount", basePrice * qty);

                            newOrderData.put("created_at", FieldValue.serverTimestamp());
                            newOrderData.put("updated_at", FieldValue.serverTimestamp());

                            // Create document with random ID
                            db.collection("orders")
                                    .document(randomOrderId)
                                    .set(newOrderData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MenuDetails.this,"Item is added!", Toast.LENGTH_LONG).show();
                                            Log.d("Firestore", "New order created successfully with ID: " + randomOrderId);

                                            double basePrice = (double) newItem.get("base_price");
                                            int quantity = (int) newItem.get("quantity");
                                            double finalNewTotal = (double) basePrice * quantity;
                                            returnToMenuFragment(finalNewTotal);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error creating new order", e);
                                        }
                                    });
                        }
                    }
                });
    }

    private void returnToMenuFragment(double finalNewTotal) {
        Intent resultIntent = new Intent();

        // Add the data you want to send back
        resultIntent.putExtra(EXTRA_CART_TOTAL, finalNewTotal);  // Example total
        resultIntent.putExtra(EXTRA_CART_TOTAL, finalNewTotal);  // Example total

        // Set result and finish
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private Map<String, Object> newItem() {
        // Create the new item to add
        Map<String, Object> newItem = new HashMap<>();
        newItem.put(PRODUCT_ID_FIELD, menuitem.getId());
        newItem.put(PRODUCT_NAME_FIELD, menuitem.getName());
        newItem.put(BASE_PRICE_FIELD, menuitem.getPrice());
        newItem.put(QUANTITY_FIELD, this.quantity);
        newItem.put(IMAGE_URL_FIELD, menuitem.getImagePath());

        if (menuitem.isHasCakeOptions()) {
            Map<String, Object> customizations = new HashMap<>();

            List<Map> decorations = new ArrayList<>();
            for (int i=0; i<selectedCakeOptionDetails.size(); i++) {
                CakeOptionDetail cakeoptionDetail = selectedCakeOptionDetails.get(i);

                Map<String, Object> detail = new HashMap<>();
                detail.put(OPTION_ID_FIELD, cakeoptionDetail.getOptionId());
                detail.put(OPTION_NAME_FIELD, cakeoptionDetail.getOptionName());

                if(cakeoptionDetail.getCategoryId().equals(DECORATIONS_FIELD)) {
                    decorations.add(detail);
                    customizations.put(cakeoptionDetail.getCategoryId(), decorations);
                }
                else customizations.put(cakeoptionDetail.getCategoryId(), detail);

            }
            newItem.put(CUSTOMIZATIONS_FIELD, customizations);
        }

        return newItem;
    }
}