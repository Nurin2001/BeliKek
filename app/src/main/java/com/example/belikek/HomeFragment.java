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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import static com.example.belikek.Constants.*;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;

    public HomeFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Pastikan fail ini wujud & betul
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rvMenu);

        // Grid 2 kolum
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        MenuAdapter adapter = new MenuAdapter((item, position) -> {
            // TODO: buka detail item (optional)
        });
        rv.setAdapter(adapter);

        // initialize firestore
        db = FirebaseFirestore.getInstance();

        String categoryId = "cartoon";
        db.collection(PRODUCTS_COLLECTION)
                .whereEqualTo(CATEGORY_ID_FIELD, categoryId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<MenuItem> list = mapDocToMenuItems(queryDocumentSnapshots);
                            // Susun ikut nama dan LIMIT 6
                            list.sort((a, b) -> a.name.compareToIgnoreCase(b.name));
                            if (list.size() > 6) list = list.subList(0, 6);
                            adapter.submit(list);

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
}
