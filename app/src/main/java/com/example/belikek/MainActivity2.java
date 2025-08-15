package com.example.belikek;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // pastikan ini layout host yang ada fragment_container + include bottom nav

        // 1) Inset untuk CONTENT (atas sahaja)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, 0);
            return insets;
        });

        // 2) Inset untuk BOTTOM NAV (tambah padding bawah)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_nav), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bars.bottom);
            return insets;
        });

        // First get the include container, then find the BottomNavigationView inside it
//        View bottomNavContainer = findViewById(R.id.bottom_nav_container);
//        bottomNav = bottomNavContainer.findViewById(R.id.bottom_nav);
        bottomNav = findViewById(R.id.bottom_nav); // id dalam bottom_navigation_bar.xml

        // 3) Fragment awal
        if (savedInstanceState == null) {
            replace(new HomeFragment());
            if (bottomNav != null) bottomNav.setSelectedItemId(R.id.home);
        }
Log.d("bottomnav isExist", String.valueOf(bottomNav != null));
        // 4) Handler tab bottom nav
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.home) {// tab "Cake"
                    replace(new HomeFragment());
                    return true;
                } else if (id == R.id.shorts) {
                    Log.d("menu tab", "menu tab");
                    replace(new MenuFragment());           // tukar ke fragment sebenar nanti
                    return true;
                } else if (id == R.id.subscriptions) {
                    Log.d("profile tab", "profile tab");// tab "Cake"
                    replace(new HomeFragment());        // tukar ke fragment sebenar nanti
                    return true;
                }
                return false;
            });

            bottomNav.setOnItemReselectedListener(item -> {
                // optional: scroll to top/refresh
            });
        }
    }

    // >>> Terima FRAGMENT, bukan HomeFragment sahaja
    private void replace(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
