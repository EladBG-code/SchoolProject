package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ViewSummariesOnSubject extends AppCompatActivity {

    TextView tvSubjectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summaries_on_subject);

        tvSubjectName = findViewById(R.id.tvSubjectName);

        tvSubjectName.setText(getIntent().getStringExtra("SubjectSelected"));
    }
}