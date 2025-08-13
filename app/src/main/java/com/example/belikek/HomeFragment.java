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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

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

        // Firestore: ambil dokumen "menu/cartoon" (semua field)
        FirebaseFirestore.getInstance()
                .collection("menu")
                .document("cartoon")
                .get()
                .addOnSuccessListener(doc -> {
                    List<MenuItem> list = mapDocToMenuItems(doc);
                    // Susun ikut nama dan LIMIT 6
                    list.sort((a, b) -> a.name.compareToIgnoreCase(b.name));
                    if (list.size() > 6) list = list.subList(0, 6);
                    adapter.submit(list);
                });

        // "See More" → tukar ke tab Menu (jika btnSeeMore wujud dalam layout)
//        if (btnSeeMore != null) {
//            btnSeeMore.setOnClickListener(v -> {
//                BottomNavigationView bottom = requireActivity().findViewById(R.id.bottom_nav);
//                if (bottom != null) bottom.setSelectedItemId(R.id.shorts); // tukar id jika berbeza
//            });
//        }
    }

    private List<MenuItem> mapDocToMenuItems(@Nullable DocumentSnapshot doc) {
        List<MenuItem> list = new ArrayList<>();
        if (doc == null || !doc.exists() || doc.getData() == null) return list;

        for (Map.Entry<String, Object> e : doc.getData().entrySet()) {
            String id = e.getKey();                     // cth: "baby_shark"
            String name = String.valueOf(e.getValue()); // cth: "Baby Shark"

            // Default price (boleh sambung dari koleksi lain nanti)
            double price = 99.00;

            // **Path gambar** ikut konvensyen Storage anda
            String imagePath = "menu/cartoon/" + id + ".jpg";

            list.add(new MenuItem(id, name, price, imagePath));
        }
        return list;
    }
}
