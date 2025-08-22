package com.example.belikek;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.belikek.Constants.*;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ---- Products grid (existing) ----
        RecyclerView rvProducts = view.findViewById(R.id.rvMenu);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        MenuAdapter menuAdapter = new MenuAdapter((item, position) -> {
            // TODO: go to product detail
        });
        rvProducts.setAdapter(menuAdapter);

        // ---- Promotions horizontal ----
        RecyclerView rvPromotion = view.findViewById(R.id.rvPromotion);
        rvPromotion.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        PromotionAdapter promoAdapter = new PromotionAdapter((item, pos) -> {
            // TODO: open deep link if you have (item.deepLink)
        });
        rvPromotion.setAdapter(promoAdapter);

        // Optional: snap one banner per “page” (like ViewPager)
        SnapHelper snap = new PagerSnapHelper();
        snap.attachToRecyclerView(rvPromotion);

        // ---- Firestore ----
        db = FirebaseFirestore.getInstance();

        // Load products (categoryId = "cartoon" per your current code)
        String categoryId = "cartoon";
        db.collection(PRODUCTS_COLLECTION)
                .whereEqualTo(CATEGORY_ID_FIELD, categoryId)
                .get()
                .addOnSuccessListener(qs -> {
                    List<MenuItem> list = mapDocToMenuItems(qs);
                    list.sort((a, b) -> a.name.compareToIgnoreCase(b.name));
                    if (list.size() > 6) list = list.subList(0, 6);
                    menuAdapter.submit(list);
                })
                .addOnFailureListener(e -> Log.w("FirestoreQuery", "products error", e));

        // Load promotions
        db.collection(PROMOTIONS_COLLECTION)              // e.g. "promotions"
                .orderBy(DISPLAY_ORDER_FIELD, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener((OnSuccessListener<QuerySnapshot>) qs -> {
                    List<PromotionAdapter.PromotionItem> banners = new ArrayList<>();
                    for (DocumentSnapshot d : qs) {
                        String id = d.getId();
                        String imageUrl = d.getString(IMAGE_URL_FIELD); // reuse field name
                        String deepLink = d.getString("deep_link");      // optional
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            banners.add(new PromotionAdapter.PromotionItem(id, imageUrl, deepLink));
                        }
                    }
                    promoAdapter.submit(banners);
                })
                .addOnFailureListener(e -> Log.w("FirestoreQuery", "promotions error", e));
    }

    private List<MenuItem> mapDocToMenuItems(@Nullable QuerySnapshot qs) {
        List<MenuItem> list = new ArrayList<>();
        if (qs == null) return list;
        for (DocumentSnapshot d : qs) {
            if (!d.exists()) continue;
            String id = d.getId();
            String name = d.getString(NAME_FIELD);
            Double price = d.getDouble(PRICE_FIELD);
            String imagePath = d.getString(IMAGE_URL_FIELD);
            boolean hasCakeOptions = Boolean.TRUE.equals(d.getBoolean(HAS_CAKE_OPTIONS_FIELD));
            list.add(new MenuItem(id, name, price, imagePath, hasCakeOptions));
        }
        return list;
    }
}
