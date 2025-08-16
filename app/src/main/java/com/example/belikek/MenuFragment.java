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
    private static final String STATE_TOTAL = "state_total";

    // Keys utk result dari MenuDetails
    public static final String EXTRA_TOTAL_DELTA = "extra_total_delta";
    public static final String EXTRA_CART_TOTAL  = "extra_cart_total";

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
                            updateCheckoutBar();
                        }
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
        updateCheckoutBar();

        checkoutBar.setVisibility(View.GONE);

        btnCheckout.setOnClickListener(view -> {
            Intent i = new Intent(getActivity(), ConfirmOrder.class);
            i.putExtra("total", cartTotal);
            startActivity(i);
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

        // Products grid
        rvProducts = v.findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        menuAdapter = new MenuAdapter((item, position) -> {
            // TODO: buka ProductDetail
        });
        rvProducts.setAdapter(menuAdapter);

        // Muat produk awal
        loadProductsForCategory("four-inch-cake");

        setupCategoryAdapterListener();
        setUpProductAdapterListener();
    }

    private void updateShopStatusUI(boolean isOpen, @NonNull String timeText) {
        chipStatus.setText(isOpen ? "Open" : "Closed");
        chipTime.setText(timeText);
    }

    //Mierza tambah
    private void updateCheckoutBar() {
        if (cartTotal > 0) {
            checkoutBar.setVisibility(View.VISIBLE);
            tvTotal.setText(String.format("RM%.2f", cartTotal));
        } else {
            checkoutBar.setVisibility(View.GONE);
        }
    }
    //?

    private void setTotal(double amt) {
        cartTotal = amt;
        updateCheckoutBar();
    }

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

        // Send multiple objects (ArrayList)
        ArrayList<MenuItem> menuDetailList = new ArrayList<>();
        menuDetailList.add(menuItem);
//        intent.putParcelableArrayListExtra("category_list", menuDetailList);

        startActivity(intent);
    }

    private void loadCategoriesFromDb() {
        db.collection(FIRESTORE_CATEGORIES)
                .orderBy("display_order", Query.Direction.ASCENDING)  // or DESCENDING
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<CategoryUI> category = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firestore", "Document ID: " + document.getId());

                                // Access individual fields
                                Long displayOrder = document.getLong("display_order");
                                String id = document.getString("id");
//                                String imageUrl = document.getString("image_url");
                                Boolean isActive = document.getBoolean("is_active");
                                String name = document.getString("name");

                                category.add(new CategoryUI(id, name));
                                Log.d("Firestore", "display_order: " + displayOrder);
                                Log.d("Firestore", "id: " + id);
//                                Log.d("Firestore", "image_url: " + imageUrl);
                                Log.d("Firestore", "is_active: " + isActive);
                                Log.d("Firestore", "name: " + name);

                                // Alternative: Get all data as a Map
                                Map<String, Object> data = document.getData();
                                Log.d("Firestore", "All data: " + data.toString());
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
        db.collection("products")
                .whereEqualTo("category_id", categoryId)
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
            String name = documentSnapshot.getString("name");
            Double price = documentSnapshot.getDouble("price");
            String imagePath = documentSnapshot.getString("image_url");

            list.add(new MenuItem(id, name, price, imagePath));
        }
        return list;
    }



//    private void bindProductsFromDocument(DocumentSnapshot doc) {
//        if (doc == null || doc.getData() == null) {
//            menuAdapter.submit(new ArrayList<>());
//            return;
//        }
//        String categoryId = doc.getId();
//        List<MenuItem> list = new ArrayList<>();
//
//        // Doc mengandungi fields: key=productId, value=productName
//        for (Map.Entry<String, Object> e : doc.getData().entrySet()) {
//            String productId = e.getKey();
//            String name = String.valueOf(e.getValue());
//
//            // Andaikan harga belum disimpan → guna default / TODO ambil dari koleksi lain
//            double price = 99.0;
//
//            // Path Storage untuk gambar
//            String storagePath = "menu/" + categoryId + "/" + productId + ".jpg";
//
//            list.add(new MenuItem(productId, name, price, storagePath));
//        }
//
//        // Limit ke 6 item
//        if (list.size() > 6) list = list.subList(0, 6);
//
//        menuAdapter.submit(list);
//    }
}
