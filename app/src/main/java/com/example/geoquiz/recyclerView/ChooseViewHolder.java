package com.example.geoquiz.recyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geoquiz.R;

public class ChooseViewHolder extends RecyclerView.ViewHolder {
    // Holds the references to the views within the item layout
    TextView tvTypeGameName;
    ImageView ivCard;

    public ChooseViewHolder(@NonNull View itemView) {
        super(itemView);
        tvTypeGameName = itemView.findViewById(R.id.tv_title);
        ivCard = itemView.findViewById(R.id.iv_Card);
    }
}
