package com.theproject.schoolproject;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    ImageButton image;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        title=itemView.findViewById(R.id.summaryTitle);
        image=itemView.findViewById(R.id.summaryImage);

    }
}
