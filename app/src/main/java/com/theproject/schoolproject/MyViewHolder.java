package com.theproject.schoolproject;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;


public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle;
    TextView tvDescription;
    TextView tvAuthor;
    Button btnViewSummary;
    ShapeableImageView btnHeart;
    View v; //this is the specific view model that the user pressed on

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.summaryTitle);
        tvDescription = itemView.findViewById(R.id.summaryDescription);
        tvAuthor = itemView.findViewById(R.id.summaryAuthor);
        btnViewSummary = itemView.findViewById(R.id.openSummaryBtn);
        btnHeart = itemView.findViewById(R.id.rateBtn);
        v = itemView;

    }
}
