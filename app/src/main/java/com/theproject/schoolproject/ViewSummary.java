package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewSummary extends AppCompatActivity {

    private TextView tvSummaryTitle;
    private TextView tvSummaryAuthor;
    private TextView tvSummaryDescription;
    DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summary);
        tvSummaryAuthor = findViewById(R.id.viewSummaryAuthor);
        tvSummaryTitle = findViewById(R.id.viewSummaryTitle);
        tvSummaryDescription = findViewById(R.id.viewSummaryDescription);
        String summaryKey=getIntent().getStringExtra("summaryKey");
        String subject=getIntent().getStringExtra("subject");
        database= FirebaseDatabase.getInstance().getReference().child(subject);
        database.child(summaryKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            String summaryTitle=snapshot.child("title").getValue().toString();
            String summaryAuthor=snapshot.child("author").getValue().toString();
            String summaryDescription = snapshot.child("description").getValue().toString();
            tvSummaryTitle.setText(summaryTitle);
            tvSummaryAuthor.setText(summaryAuthor);
            tvSummaryDescription.setText(summaryDescription);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}