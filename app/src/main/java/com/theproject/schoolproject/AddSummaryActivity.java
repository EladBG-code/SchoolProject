package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddSummaryActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton floatingReturnButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_summary);

        floatingReturnButton = findViewById(R.id.floatingReturnButton);

        floatingReturnButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v == floatingReturnButton){
            finish();
        }
    }
}