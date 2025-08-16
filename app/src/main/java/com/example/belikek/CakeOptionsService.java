package com.example.belikek;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CakeOptionsService {
    private FirebaseFirestore db;
    private static final String COLLECTION_CAKE_OPTIONS = "cake_options";

    public interface CakeOptionsCallback {
        void onSuccess(List<CakeOption> cakeOptions);
        void onError(Exception e);
    }

    public CakeOptionsService() {
        db = FirebaseFirestore.getInstance();
    }

    public void fetchAllCakeOptions(CakeOptionsCallback callback) {
        db.collection(COLLECTION_CAKE_OPTIONS)
                .orderBy("display_order", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CakeOption> cakeOptions = new ArrayList<>();

                        int i = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Get document fields
                                String docId = document.getId();
                                String name = document.getString("name");
                                Boolean multiSelect = document.getBoolean("multiselect");
                                Boolean required = document.getBoolean("required");

                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(i++);
                                // Get options array
                                List<Map<String, Object>> optionsArray =
                                        (List<Map<String, Object>>) documentSnapshot.get("options");

                                // Convert to CakeOptionDetail list
                                List<CakeOptionDetail> options = new ArrayList<>();

                                if (optionsArray != null) {
                                    for (Map<String, Object> optionMap : optionsArray) {
                                        String optionId = (String) optionMap.get("id");
                                        String optionName = (String) optionMap.get("name");
                                        Log.d("option id", optionId);

                                        // Create CakeOptionDetail (isSelected = false initially)
                                        CakeOptionDetail detail = new CakeOptionDetail(optionName, optionId, docId, false);
                                        options.add(detail);
                                    }
                                }

                                // Create pick text based on multi_select
                                String pickText = (multiSelect != null && multiSelect) ? "Pick at least 1" : "Pick 1";

                                // Create CakeOption
                                CakeOption cakeOption = new CakeOption(name, pickText, options);
                                cakeOptions.add(cakeOption);

                            } catch (Exception e) {
                                Log.e("CakeOptionsService", "Error parsing document: " + document.getId(), e);
                            }
                        }

                        callback.onSuccess(cakeOptions);

                    } else {
                        Log.e("CakeOptionsService", "Error getting documents: ", task.getException());
                        callback.onError(task.getException());
                    }
                });
    }
}
