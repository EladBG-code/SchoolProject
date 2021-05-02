package com.theproject.schoolproject;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;


public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle;
    TextView tvDescription;
    TextView tvAuthor;
    Button btnViewSummary;
    CheckBox btnHeart;
    View v; //this is the specific view model that the user pressed on
    MaterialTextView mtvLikesNum;
    CardView cvEntireSummary;

    /**
     * Special ViewHolder function that sets all of the cards used in the summaries gridview ahead.
     * @param itemView
     */
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTitle = itemView.findViewById(R.id.summaryTitle);
        tvDescription = itemView.findViewById(R.id.summaryDescription);
        tvAuthor = itemView.findViewById(R.id.summaryAuthor);
        btnViewSummary = itemView.findViewById(R.id.openSummaryBtn);
        btnHeart = itemView.findViewById(R.id.rateBtn);
        mtvLikesNum = itemView.findViewById(R.id.mtvLikesNum);
        cvEntireSummary = itemView.findViewById(R.id.cvEntireSummary);
        v = itemView;

    }
}
