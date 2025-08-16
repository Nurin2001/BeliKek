package com.example.belikek;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CakeOptionDetailAdapter extends RecyclerView.Adapter<CakeOptionDetailAdapter.ViewHolder> {
    private List<CakeOptionDetail> options;
    private OnOptionSelectedListener listener;
    private boolean isMultiSelect;

    public interface OnOptionSelectedListener {
        void onOptionSelected(int position, CakeOptionDetail option);
    }

    public CakeOptionDetailAdapter(List<CakeOptionDetail> options, boolean isMultiSelect, OnOptionSelectedListener listener) {
        this.options = options;
        this.isMultiSelect = isMultiSelect;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_cake_option_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CakeOptionDetail option = options.get(position);

        // Set checkbox text and state
        holder.optionCheckbox.setText(option.getOptionName());
        holder.optionCheckbox.setChecked(option.isSelected());

        // Handle checkbox clicks
        holder.optionCheckbox.setOnCheckedChangeListener(null); // Clear previous listener
        holder.optionCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isMultiSelect) {
                // Multiple selection allowed
                option.setSelected(isChecked);
            } else {
                // Single selection only
                if (isChecked) {
                    // Uncheck all others
                    for (CakeOptionDetail opt : options) {
                        opt.setSelected(false);
                    }
                    option.setSelected(true);
                    notifyDataSetChanged();
                } else {
                    option.setSelected(false);
                }
            }

            if (listener != null) {
                listener.onOptionSelected(position, option);
            }
        });

        // Handle checkbox container clicks (to make the whole area clickable)
        holder.itemView.setOnClickListener(v -> {
            holder.optionCheckbox.performClick();
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox optionCheckbox;

        ViewHolder(View itemView) {
            super(itemView);
            optionCheckbox = itemView.findViewById(R.id.detail_option_btn);
        }
    }
}
