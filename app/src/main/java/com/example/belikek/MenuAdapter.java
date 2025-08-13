package com.example.belikek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.VH> {

    public interface OnItemClick { void onClick(MenuItem item); }

    private final List<MenuItem> items = new ArrayList<>();
    private final OnItemClick listener;

    FirebaseFirestore db;

    public MenuAdapter(OnItemClick listener) {
        this.listener = listener;
    }

    public void submit(List<MenuItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                // GUNA nama fail yang betul: item_menu.xml
                .inflate(R.layout.menu_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        MenuItem it = items.get(position);
        h.title.setText(it.getName());
        h.price.setText(String.format("RM %.2f", it.getPrice()));

        // Load gambar dari Firebase Storage → imagePath contoh: "menu/cartoon/baby_shark.jpg"
        if (h.image != null) {
            Glide.with(h.itemView.getContext())
                    .load(it.getImagePath())
                    .placeholder(R.drawable.ic_cookie) // Add a placeholder image
                    .error(R.drawable.ic_cartoon) // Add an error image
                    .centerCrop()
                    .into(h.image);
        } else {
            h.image.setImageResource(R.drawable.ic_default_background);
        }

//        h.itemView.setOnClickListener(v -> {
//            if (listener != null) listener.onClick(it);
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price;
        VH(@NonNull View v) {
            super(v);
            // ID ikut item_menu.xml yang kau bagi
            image = v.findViewById(R.id.imageView);
            title = v.findViewById(R.id.menuName);
            price = v.findViewById(R.id.tvPrice);
        }
    }
}
