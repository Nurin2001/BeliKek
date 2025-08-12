package com.example.belikek;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // <-- layout yang kau paste tu

        // Insets: apply TOP sahaja pada content; jangan tolak bottom (biar bottom nav melekat bawah)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, /* bottom */ 0);
            return insets;
        });

        bottomNav = findViewById(R.id.bottom_nav); // id dalam bottom_navigation_bar.xml

        // Load fragment awal sekali (Home) hanya kali pertama activity dibuat
        if (savedInstanceState == null) {
            replace(new HomeActivity());
            // set selected state pada bottom nav supaya konsisten
            if (bottomNav != null) bottomNav.setSelectedItemId(R.id.home);
        }

        if (bottomNav != null) {
            // Bila user pilih tab
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.home) {
                    replace(new HomeFragment());
                    return true;
                } else if (id == R.id.shorts) { // id "Cake" dalam menu kau
                    replace(new MenuFragment());
                    return true;
                } else if (id == R.id.subscriptions) { // id "Profile" dalam menu kau
                    replace(new ProfileFragment());
                    return true;
                }
                return false;
            });

            // Optional: bila reselect tab yang sama, jangan buat apa-apa
            bottomNav.setOnItemReselectedListener(item -> {
                // no-op
            });
        }
    }

    private void replace(@NonNull HomeActivity fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
