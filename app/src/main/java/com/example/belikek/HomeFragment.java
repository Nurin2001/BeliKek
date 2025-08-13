package com.example.belikek;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
//        TextView btnSeeMore = view.findViewById(R.id.btnSeeMore); // optional jika ada

        // Grid 2 kolum
        rv.setLayoutManager(new GridLayoutManager(getContext(), 2));

        MenuAdapter adapter = new MenuAdapter(item -> {
            // TODO: buka detail item (optional)
        });
        rv.setAdapter(adapter);

        // initialize firestore
        db = FirebaseFirestore.getInstance();

        String categoryId = "cartoon";
        db.collection("products")
                .whereEqualTo("category_id", categoryId)
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

        // "See More" → tukar ke tab Menu (jika btnSeeMore wujud dalam layout)
//        if (btnSeeMore != null) {
//            btnSeeMore.setOnClickListener(v -> {
//                BottomNavigationView bottom = requireActivity().findViewById(R.id.bottom_nav);
//                if (bottom != null) bottom.setSelectedItemId(R.id.shorts); // tukar id jika berbeza
//            });
//        }
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
}
