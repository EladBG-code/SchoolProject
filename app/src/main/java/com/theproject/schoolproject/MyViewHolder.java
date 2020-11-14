package com.theproject.schoolproject;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView description;
    TextView author;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        title=itemView.findViewById(R.id.summaryTitle);
        description=itemView.findViewById(R.id.summaryDescription);
        author=itemView.findViewById(R.id.summaryAuthor);

    }
}
