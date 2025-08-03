package com.example.belikek;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmOrder extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItems;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

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
            testFirestore();
        } else {
            Log.e("Network", "No internet connection");
        }

        // Create sample data
        createSampleData();

        // Set up adapter
        adapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(adapter);

//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if (documentSnapshot.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
//                    }
//                    else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });

        // Set click listeners
//        adapter.setOnCartItemClickListener(new OrdersAdapter.OnCartItemClickListener() {
//            @Override
//            public void onDeleteClick(int position) {
//                adapter.removeItem(position);
//            }
//
//            @Override
//            public void onIncreaseClick(int position) {
//                CartItem item = cartItems.get(position);
//                int newQuantity = item.getQuantity() + 1;
//                adapter.updateQuantity(position, newQuantity);
//            }
//
//            @Override
//            public void onDecreaseClick(int position) {
//                CartItem item = cartItems.get(position);
//                int newQuantity = item.getQuantity() - 1;
//                cartAdapter.updateQuantity(position, newQuantity);
//            }
//        });
    }

    private void testFirestore() {
        //        get a document
        DocumentReference docRef = db.collection("test").document("test");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", "Error connecting to Firestore: " + e.getMessage());
            }
        });
        // Test with a simple read operation first
//        db.collection("test")
//                .limit(1)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        Log.d("Firestore", "Successfully connected to Firestore!");
//                        Log.d("Firestore", "Retrieved " + queryDocumentSnapshots.size() + " documents");
//
//                        // Now try writing data
//                        addTestDocument();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(Exception e) {
//                        Log.e("Firestore", "Error connecting to Firestore: " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                });
    }

    private void addTestDocument() {
        // Create a test document
        Map<String, Object> testData = new HashMap<>();
        testData.put("message", "Hello from Java!");
        testData.put("timestamp", System.currentTimeMillis());

        db.collection("test")
                .document("testDoc")
                .set(testData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Document written successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Firestore", "Error writing document: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

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

    private void createSampleData() {
        cartItems = new ArrayList<>();

        cartItems.add(new CartItem(
                "Baby Shark",
                "RM 99.00",
                "Chocolate Moist",
                "Chocolate Fudge",
                "Toys",
                R.drawable.baby_shark, // Replace with your actual drawable
                2
        ));

        cartItems.add(new CartItem(
                "Fruit Tart",
                "RM 70.00",
                "Remarks",
                "",
                "",
                R.drawable.fruit_tart, // Replace with your actual drawable
                1
        ));
        cartItems.add(new CartItem(
                "Fruit Tart",
                "RM 70.00",
                "Remarks",
                "",
                "",
                R.drawable.fruit_tart, // Replace with your actual drawable
                1
        ));

        // Add more items as needed
    }
}