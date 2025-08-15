package com.example.belikek;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuDetails extends AppCompatActivity {

    private MenuItem menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_details);

        RecyclerView mainRecyclerView = findViewById(R.id.main_recyclerciew_cake_option);
// Prepare your data
        List<CakeOption> cakeOptions = prepareCakeOptions();

        // Setup main RecyclerView
        LinearLayoutManager mainLayoutManager = new LinearLayoutManager(this);
        mainRecyclerView.setLayoutManager(mainLayoutManager);
        mainRecyclerView.setHasFixedSize(true);

        CakeOptionAdapter mainAdapter = new CakeOptionAdapter(cakeOptions,
                (categoryPosition, optionPosition, option) -> {
                    // Handle option selection
                    Toast.makeText(this, "Selected: " + option.getOptionName() +
                                    " from " + cakeOptions.get(categoryPosition).getCategoryName(),
                            Toast.LENGTH_SHORT).show();

                    // Update your price, enable buttons, etc.
                    updatePriceAndButtons();
                }
        );
        mainRecyclerView.setAdapter(mainAdapter);
    }

    private void setDetails() {
        Intent intent = getIntent();
        if (intent != null) {
            MenuItem item = intent.getParcelableExtra("menu_detail");

            this.menuItems.setId(item.getId());
            this.menuItems.setName(item.getName());
            this.menuItems.setPrice(item.getPrice());
            this.menuItems.setImagePath(item.getImagePath());

            if (item != null) {
                Log.d("cake name", item.getName());
            }
        }
    }

    private MenuItem getMenuItem() {
        return this.menuItems;
    }

    private List<CakeOption> prepareCakeOptions() {
        List<CakeOption> options = new ArrayList<>();

//        // Cake Base options
//        List<CakeOptionDetail> cakeBaseOptions = Arrays.asList(
//                new CakeOptionDetail("Chocolate Moist", false),
//                new CakeOptionDetail("Vanilla Sponge", false),
//                new CakeOptionDetail("Buttercake", false)
//        );
//        options.add(new CakeOption("Cake Base", "Pick 1", cakeBaseOptions));
//
//        // Fillings options
//        List<CakeOptionDetail> fillingsOptions = Arrays.asList(
//                new CakeOptionDetail("Chocolate Fudge", false),
//                new CakeOptionDetail("Strawberry", false),
//                new CakeOptionDetail("Vanilla Cream", false)
//        );
//        options.add(new CakeOption("Fillings", "Pick 1", fillingsOptions));
//
//        // Decoration options
//        List<CakeOptionDetail> decorationOptions = Arrays.asList(
//                new CakeOptionDetail("Toys", false),
//                new CakeOptionDetail("Buttercream Only", false),
//                new CakeOptionDetail("Topper Only", false)
//        );
//        options.add(new CakeOption("Decoration", "Pick 1", decorationOptions));

        return options;
    }

    private void updatePriceAndButtons() {
        // Update your price calculation and enable/disable buttons
        Button buyNowBtn = findViewById(R.id.buy_now_btn);
        Button addToCartBtn = findViewById(R.id.add_to_btn);

        // Check if all required options are selected
        boolean allSelected = checkIfAllOptionsSelected();

        buyNowBtn.setEnabled(allSelected);
        addToCartBtn.setEnabled(allSelected);

        if (allSelected) {
            buyNowBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.enabled_rounded_btn));
            addToCartBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.enabled_rounded_btn));
        }
    }

    private boolean checkIfAllOptionsSelected() {
        // Implement logic to check if all required options are selected
        return true; // placeholder
    }
}