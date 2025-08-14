package com.example.belikek;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.belikek.ui.CategoryAdapter;
import com.example.belikek.ui.CategoryUi;
import com.example.belikek.ui.ProductAdapter;
import com.example.belikek.ui.ProductUi;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment {

    private RecyclerView rvCategories, rvProducts;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    private TextView chipStatus, chipTime, tvTotal;
    private MaterialButton btnCheckout;

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

        // Header
        chipStatus = v.findViewById(R.id.chipStatus);
        chipTime   = v.findViewById(R.id.chipTime);

        // Checkout include
        View checkout = v.findViewById(R.id.includeCheckout);
        if (checkout != null) {
            tvTotal = checkout.findViewById(R.id.tvTotal);
            btnCheckout = checkout.findViewById(R.id.btnCheckout);
            if (btnCheckout != null) {
                btnCheckout.setOnClickListener(view -> {
                    // TODO: buka Checkout screen
                });
            }
        }
        setTotal(99.00); // contoh total
        updateShopStatusUI(false, "00:00 P.M");

        // Sidebar categories (static list untuk UI; boleh fetch Firestore kalau ada koleksi 'categories')
        rvCategories = v.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        categoryAdapter = new CategoryAdapter((item, pos) -> {
            categoryAdapter.setSelected(pos);
            loadProductsForCategory(item.id); // refresh kanan
        });
        rvCategories.setAdapter(categoryAdapter);

        // Senarai kategori asas (boleh ubah ikut keperluan)
        List<CategoryUi> cats = Arrays.asList(
                new CategoryUi("4in", "4In", R.drawable.ic_category_placeholder),
                new CategoryUi("6in", "6In", R.drawable.ic_category_placeholder),
                new CategoryUi("8in", "8In", R.drawable.ic_category_placeholder),
                new CategoryUi("10in","10In",R.drawable.ic_category_placeholder),
                new CategoryUi("cartoon","Cartoon",R.drawable.ic_category_placeholder),
                new CategoryUi("pastry","Pastry",R.drawable.ic_category_placeholder),
                new CategoryUi("bread","Single Bread",R.drawable.ic_category_placeholder),
                new CategoryUi("cookie","Cookie",R.drawable.ic_category_placeholder)
        );
        categoryAdapter.submit(cats);
        categoryAdapter.setSelected(4); // default pilih "Cartoon"

        // Products grid
        rvProducts = v.findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(item -> {
            // TODO: buka ProductDetail
        });
        rvProducts.setAdapter(productAdapter);

        // Muat produk awal
        loadProductsForCategory("cartoon");
    }

    private void updateShopStatusUI(boolean isOpen, @NonNull String timeText) {
        chipStatus.setText(isOpen ? "Open" : "Closed");
        chipTime.setText(timeText);
    }

    private void setTotal(double amt) {
        if (tvTotal != null) tvTotal.setText(String.format("RM%.2f", amt));
    }

    /**
     * Fetch produk dari Firestore ikut struktur:
     * Collection: menu
     * Document: <categoryId>
     * Fields: <productId>: <productName>
     *
     * Gambar di Firebase Storage: menu/<categoryId>/<productId>.jpg
     */
    private void loadProductsForCategory(@NonNull String categoryId) {
        FirebaseFirestore.getInstance()
                .collection("menu")
                .document(categoryId)
                .get()
                .addOnSuccessListener(this::bindProductsFromDocument)
                .addOnFailureListener(e -> {
                    // TODO: show error
                    productAdapter.submit(new ArrayList<>());
                });
    }

    private void bindProductsFromDocument(DocumentSnapshot doc) {
        if (doc == null || doc.getData() == null) {
            productAdapter.submit(new ArrayList<>());
            return;
        }
        String categoryId = doc.getId();
        List<ProductUi> list = new ArrayList<>();

        // Doc mengandungi fields: key=productId, value=productName
        for (Map.Entry<String, Object> e : doc.getData().entrySet()) {
            String productId = e.getKey();
            String name = String.valueOf(e.getValue());

            // Andaikan harga belum disimpan → guna default / TODO ambil dari koleksi lain
            double price = 99.0;

            // Path Storage untuk gambar
            String storagePath = "menu/" + categoryId + "/" + productId + ".jpg";

            list.add(new ProductUi(productId, name, storagePath, price));
        }

        // Limit ke 6 item
        if (list.size() > 6) list = list.subList(0, 6);

        productAdapter.submit(list);
    }
}
