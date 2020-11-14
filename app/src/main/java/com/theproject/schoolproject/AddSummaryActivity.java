package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddSummaryActivity extends AppCompatActivity implements View.OnClickListener {

    String subject;
    FloatingActionButton floatingReturnButton;
    Button btnUpload;
    EditText summaryTitle,summaryDescription;
    FirebaseDatabase database;
    DatabaseReference summariesRef;
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

        subject = getIntent().getStringExtra("Subject");
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
                Summary summary = new Summary(GlobalAcross.currentUser.getfName()+" "+GlobalAcross.currentUser.getlName(),summaryTitle.getText().toString(),summaryDescription.getText().toString());
                addSummaryToDB(summary);
                Toast.makeText(this, "העלית את הסיכום בהצלחה", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }

        }
    }

    public void addSummaryToDB(Summary summary) {
        database = FirebaseDatabase.getInstance();
        summariesRef = database.getReference(subject).push();
        summariesRef.setValue(summary);
    }

}