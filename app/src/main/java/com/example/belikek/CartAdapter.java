package com.example.belikek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemClickListener listener;

    public interface OnCartItemClickListener {
        void onDeleteClick(int position);
        void onIncreaseClick(int position);
        void onDecreaseClick(int position);
    }

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public void setOnCartItemClickListener(OnCartItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(item.getPrice());
        holder.itemDescription1.setText(item.getDescription(1));
        holder.itemDescription2.setText(item.getDescription(2));
        holder.itemDescription3.setText(item.getDescription(3));
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        holder.itemImage.setImageResource(item.getImageResourceId());

        // Set click listeners
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });

        holder.increaseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncreaseClick(position);
            }
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecreaseClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void removeItem(int position) {
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());
    }

    public void updateQuantity(int position, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(position);
        } else {
            cartItems.get(position).setQuantity(newQuantity);
            notifyItemChanged(position);
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice, itemDescription1, itemDescription2, itemDescription3, itemQuantity;
        ImageButton deleteButton, increaseButton, decreaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.cart_item_image);
            itemName = itemView.findViewById(R.id.cart_item_name);
            itemPrice = itemView.findViewById(R.id.cart_item_price);
            itemDescription1 = itemView.findViewById(R.id.cart_item_description1);
            itemDescription2 = itemView.findViewById(R.id.cart_item_description2);
            itemDescription3 = itemView.findViewById(R.id.cart_item_description3);
            itemQuantity = itemView.findViewById(R.id.cart_item_quantity);
            deleteButton = itemView.findViewById(R.id.cart_item_delete);
            increaseButton = itemView.findViewById(R.id.cart_item_increase);
            decreaseButton = itemView.findViewById(R.id.cart_item_decrease);
        }
    }
}
