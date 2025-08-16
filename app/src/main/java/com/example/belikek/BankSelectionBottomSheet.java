package com.example.belikek;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.List;

public class BankSelectionBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerBanks;
    private ImageView btnClose;
    private List<Bank> bankList;
    private OnBankSelectedListener listener;

    public interface OnBankSelectedListener {
        void onBankSelected(Bank bank);
    }

    public static BankSelectionBottomSheet newInstance(List<Bank> bankList) {
        BankSelectionBottomSheet fragment = new BankSelectionBottomSheet();
        fragment.bankList = bankList;
        return fragment;
    }

    public void setOnBankSelectedListener(OnBankSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            // Make the background transparent
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Make it full width
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // Set custom style
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                bottomSheet.setBackgroundResource(R.drawable.bottom_sheet_background);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bank_selection_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
    }

    private void initViews(View view) {
        recyclerBanks = view.findViewById(R.id.recycler_banks);
        btnClose = view.findViewById(R.id.btn_close);
    }

    private void setupRecyclerView() {
        if (bankList != null) {
            BankAdapter adapter = new BankAdapter(bankList, bank -> {
                if (listener != null) {
                    listener.onBankSelected(bank);
                }
                dismiss();
            });

            recyclerBanks.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerBanks.setAdapter(adapter);
        }
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> dismiss());
    }
}