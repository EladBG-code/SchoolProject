package com.theproject.schoolproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SummariesAdapter extends RecyclerView.Adapter<SummariesAdapter.ViewHolder> {
List<String> titles;
List <Integer> images;
LayoutInflater inflater;

    public SummariesAdapter (Context ctx, List<String> titles, List <Integer> images){
        this.titles=titles;
        this.images=images;
        this.inflater=LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public SummariesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull SummariesAdapter.ViewHolder holder, int position) {
        holder.title.setText(titles.get(position));
        holder.image.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageButton image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.summaryTitle);
            image=itemView.findViewById(R.id.summaryImage);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),"clicked"+getAdapterPosition(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}

