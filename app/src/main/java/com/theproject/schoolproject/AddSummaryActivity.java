package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddSummaryActivity extends AppCompatActivity implements View.OnClickListener {

    String subjectSelected;
    FloatingActionButton floatingReturnButton;
    Button btnUpload;
    EditText summaryTitle,summaryDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_summary);

        floatingReturnButton = findViewById(R.id.floatingReturnButton);
        btnUpload = findViewById(R.id.btnUpload);
        summaryTitle = findViewById(R.id.etSummaryTitle);
        summaryDescription = findViewById(R.id.etSummaryDescription);

        floatingReturnButton.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean checkValid(){
        return true;
        //temp
    }

    @Override
    public void onClick(View v) {
        if(v == floatingReturnButton){
            finish();
        }
        if(v == btnUpload){
            if(checkValid()){
                String[] summaryArr = {GlobalAcross.currentUser.getfName(),summaryTitle.getText().toString(),summaryDescription.getText().toString()};
                Intent intent = new Intent();
                intent.putExtra("Summary",summaryArr);
                setResult(RESULT_OK,intent);
                finish();
            }
        }
    }
}