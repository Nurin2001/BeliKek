package com.example.belikek;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.belikek.Constants.*;

public class MenuFragment extends Fragment {

    private RecyclerView rvCategories, rvProducts;
    private CategoryAdapter categoryAdapter;
    private MenuAdapter menuAdapter;
    private View checkoutBar;
    private TextView chipStatus, chipTime, tvTotal;
    private Button btnCheckout;

    private FirebaseFirestore db;

    private static String FIRESTORE_CATEGORIES = "categories";
    private List<MenuItem>  menuItems;

    private double cartTotal = 0.0;

    // Keys utk result dari MenuDetails
    public static final String EXTRA_TOTAL_DELTA = "extra_total_delta";
    public static final String EXTRA_CART_TOTAL  = "extra_cart_total";
    public static final String ORDERS_COLLECTION  = "orders";

    private final ActivityResultLauncher<Intent> detailsLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();

                            // Cara biasa: tambah delta
                            double delta = data.getDoubleExtra(EXTRA_TOTAL_DELTA, 0.0);
                            if (delta != 0) cartTotal += delta;

                            // (Opsyen) override jumlah penuh
                            if (data.hasExtra(EXTRA_CART_TOTAL)) {
                                cartTotal = data.getDoubleExtra(EXTRA_CART_TOTAL, cartTotal);
                            }
                            updateCheckoutBar(cartTotal);
                        }
                        getOrderFromDb();
                    });

    public MenuFragment() {}

    public static MenuFragment newInstance(String p1, String p2) {
        MenuFragment f = new MenuFragment();
        Bundle b = new Bundle();
        b.putString("param1", p1);
        b.putString("param2", p2);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // initialize firestore
        db = FirebaseFirestore.getInstance();

        // Header
        chipStatus = v.findViewById(R.id.chipStatus);
        chipTime   = v.findViewById(R.id.chipTime);

        // Checkout include

        checkoutBar = v.findViewById(R.id.checkoutBar);          // ROOT include
        tvTotal     = checkoutBar.findViewById(R.id.tvTotal);
        btnCheckout = checkoutBar.findViewById(R.id.btnCheckout);

        // Initially: show/hide ikut cartTotal
        updateCheckoutBar(cartTotal);

        checkoutBar.setVisibility(View.GONE);

        btnCheckout.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), ConfirmOrder.class);
//            i.putExtra("total", cartTotal);

            detailsLauncher.launch(i);
        });
        updateShopStatusUI(false, "00:00 P.M");

        // Sidebar categories (static list untuk UI; boleh fetch Firestore kalau ada koleksi 'categories')
        rvCategories = v.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        categoryAdapter = new CategoryAdapter(getContext(), (item, pos) -> {
            categoryAdapter.setSelected(pos);
            loadProductsForCategory(item.id); // refresh kanan
        });
        rvCategories.setAdapter(categoryAdapter);

        // load category from db on side bar
        loadCategoriesFromDb();
        categoryAdapter.setSelected(0);
        // check has order or not, if got, show checkout bar
        getOrderFromDb();

        // Products grid
        rvProducts = v.findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        menuAdapter = new MenuAdapter((item, position) -> {
            // TODO: buka ProductDetail
        });
        rvProducts.setAdapter(menuAdapter);

        // Muat produk awal
        loadProductsForCategory("four_inch_cake");

        setupCategoryAdapterListener();
        setUpProductAdapterListener();
    }

    private void updateShopStatusUI(boolean isOpen, @NonNull String timeText) {
        chipStatus.setText(isOpen ? "Open" : "Closed");
        chipTime.setText(timeText);
    }

    // how or hide checkout bar
    private void updateCheckoutBar(double totalAmount) {
        if (totalAmount > 0) {
            checkoutBar.setVisibility(View.VISIBLE);
            tvTotal.setText(String.format("RM%.2f", totalAmount));
        } else {
            checkoutBar.setVisibility(View.GONE);
        }
    }
    //?

    private void setupCategoryAdapterListener() {
        categoryAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onItemClick(CategoryUI item, int position) {
                loadProductsForCategory(item.getId());
            }
        });
    }

    private void setUpProductAdapterListener() {
        menuAdapter.setOnMenuClickListener(new MenuAdapter.OnItemClick() {
            @Override
            public void onClick(MenuItem item, int position) {
                // TODO: send product details to menu detail activity
                Log.d("tekan product", "yes");
                navigateToMenuDetailsActivity(position);
            }
        });
    }

    private void navigateToMenuDetailsActivity(int position) {
        // Create your object
        MenuItem menuItem = menuItems.get(position);

        // Send single object
        Intent intent = new Intent(getActivity(), MenuDetails.class);
        intent.putExtra("menu_detail", menuItem);

        detailsLauncher.launch(intent);
    }

    private void loadCategoriesFromDb() {
        db.collection(CATEGORIES_COLLECTION)
                .orderBy(DISPLAY_ORDER_FIELD, Query.Direction.ASCENDING)  // or DESCENDING
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<CategoryUI> category = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firestore", "Document ID: " + document.getId());

                                // Access individual fields
                                Long displayOrder = document.getLong(DISPLAY_ORDER_FIELD);
                                String id = document.getString(ID_FIELD);
                                Boolean isActive = document.getBoolean(IS_ACTIVE_FIELD);
                                String name = document.getString(NAME_FIELD);

                                category.add(new CategoryUI(id, name));

                                // Alternative: Get all data as a Map
                                Map<String, Object> data = document.getData();
                            }
                            categoryAdapter.submit(category);
                            categoryAdapter.setSelected(1); // default pilih "4 INCH"
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void loadProductsForCategory(@NonNull String categoryId) {
        db.collection(PRODUCTS_COLLECTION)
                .whereEqualTo(CATEGORY_ID_FIELD, categoryId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        menuAdapter.clearData();
                        menuItems = mapDocToMenuItems(queryDocumentSnapshots);
                        // Susun ikut nama dan LIMIT 6
                        menuItems.sort((a, b) -> a.name.compareToIgnoreCase(b.name));
                        if (menuItems.size() > 6) menuItems = menuItems.subList(0, 6);
                        menuAdapter.submit(menuItems);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FirestoreQuery", "Error getting documents", e);
                });
    }

    private List<MenuItem> mapDocToMenuItems(@Nullable QuerySnapshot queryDocumentSnapshots) {
        List<MenuItem> list = new ArrayList<>();
        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            if (documentSnapshot == null || !documentSnapshot.exists() || documentSnapshot.getData() == null) return list;

            String id = documentSnapshot.getId();
            String name = documentSnapshot.getString(NAME_FIELD);
            Double price = documentSnapshot.getDouble(PRICE_FIELD);
            String imagePath = documentSnapshot.getString(IMAGE_URL_FIELD);
            boolean hasCakeOptions = documentSnapshot.getBoolean(HAS_CAKE_OPTIONS_FIELD);

            list.add(new MenuItem(id, name, price, imagePath, hasCakeOptions));
        }
        return list;
    }

    private void getOrderFromDb () {
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

                            double totalAmount = (double) existingDoc.get(TOTAL_AMOUNT_FIELD);
                            cartTotal = totalAmount;
                            updateCheckoutBar(totalAmount);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
