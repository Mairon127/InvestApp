package com.example.invest_app.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.invest_app.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView txtIndex;
    public TextView txtPreviousHash;
    public TextView txtTimestamp;
    public TextView txtData;
    public TextView txtHash;

    public RecyclerViewHolder(@NonNull final View itemView) {
        super(itemView);

        txtIndex = itemView.findViewById(R.id.txt_index);
        txtPreviousHash = itemView.findViewById(R.id.txt_previous_hash);
        txtTimestamp = itemView.findViewById(R.id.txt_timestamp);
        txtData = itemView.findViewById(R.id.txt_amount);
        txtHash = itemView.findViewById(R.id.txt_hash);
    }
}
