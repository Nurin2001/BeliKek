package com.example.belikek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Items> cartItems;
    private OnCartItemClickListener listener;
    private static final String TAG = "BeliKek";

    public interface OnCartItemClickListener {
        void onIncreaseClick(int position);
        void onDecreaseClick(int position);
        void onEditClick(int position);
    }

    public CartAdapter(List<Items> cartItems) {
        this.cartItems = cartItems;
    }

    public void setOnCartItemClickListener(OnCartItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Items item = cartItems.get(position);

        DecimalFormat df = new DecimalFormat("0.00");

        holder.itemName.setText(item.getProduct_name() != null ? item.getProduct_name() : "Product");
        holder.itemPrice.setText("RM" + df.format(item.getTotalEachCake()));
        holder.itemDescription1.setText(item.getCakeBaseDescription());
        holder.itemDescription2.setText(item.getDecorationsDescription());
        holder.itemDescription3.setText("");
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        holder.itemImage.setImageResource(R.drawable.ic_default_background);

        // Set click listeners
        holder.itemEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(position);
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
            updateCakePrice(position, newQuantity * cartItems.get(position).getBase_price());
            notifyItemChanged(position);
        }
    }

    private void updateCakePrice(int position, long newPrice) {
        cartItems.get(position).setTotalEachCake(newPrice);
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemEdit, itemPrice, itemDescription1, itemDescription2, itemDescription3, itemQuantity;
        ImageButton increaseButton, decreaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.product_name_tv);
            itemPrice = itemView.findViewById(R.id.product_price_tv);
            itemDescription1 = itemView.findViewById(R.id.base_tv);
            itemDescription2 = itemView.findViewById(R.id.fill_tv);
            itemDescription3 = itemView.findViewById(R.id.deco_tv);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemEdit = itemView.findViewById(R.id.item_edit);
            increaseButton = itemView.findViewById(R.id.item_increase);
            decreaseButton = itemView.findViewById(R.id.item_decrease);
        }
    }
}
