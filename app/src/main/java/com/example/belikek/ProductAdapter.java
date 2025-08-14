package com.example.belikek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.belikek.R;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface OnClick { void onItem(ProductUi item); }

    private final List<ProductUi> items = new ArrayList<>();
    private final OnClick onClick;

    public ProductAdapter(OnClick onClick) { this.onClick = onClick; }

    public void submit(List<ProductUi> list) {
        items.clear(); if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_product, p, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProductUi it = items.get(position);
        h.name.setText(it.name);
        h.price.setText(String.format("RM %.2f", it.price));

        // Jika imagePathOrUrl bermula http, load terus; jika tidak, anggap Storage path
        String src = it.imagePathOrUrl;
        if (src != null && (src.startsWith("http://") || src.startsWith("https://"))) {
            Glide.with(h.thumb.getContext())
                    .load(src)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(h.thumb);
        } else {
            FirebaseStorage.getInstance()
                    .getReference(src)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> Glide.with(h.thumb.getContext())
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(h.thumb))
                    .addOnFailureListener(e -> h.thumb.setImageResource(R.drawable.placeholder));
        }

        h.itemView.setOnClickListener(v -> { if (onClick != null) onClick.onItem(it); });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb; TextView name, price;
        VH(@NonNull View v) {
            super(v);
            thumb = v.findViewById(R.id.imgThumb);
            name  = v.findViewById(R.id.tvName);
            price = v.findViewById(R.id.tvPrice);
        }
    }
}
