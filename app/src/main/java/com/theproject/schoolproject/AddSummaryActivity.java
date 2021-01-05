package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

    public boolean checkValid(EditText title,EditText description){
        if(title.getText().toString().length()<5){
            Toast.makeText(this, "אנא וודא\\י שיש לפחות 5 תווים בכותרת הסיכום", Toast.LENGTH_LONG).show();
            return false;
        }
        if(description.getText().toString().length()<20){
            Toast.makeText(this, "אנא וודא\\י שיש לפחות 20 תווים בתיאור הסיכום", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
        //temp
    }

    @Override
    public void onClick(View v) {
        if(v == floatingReturnButton){
            finish();
        }
        if(v == btnUpload){
            if(checkValid(summaryTitle,summaryDescription)){
                Summary summary = new Summary(GlobalAcross.currentUser.getfName()+" "+GlobalAcross.currentUser.getlName(),summaryTitle.getText().toString(),summaryDescription.getText().toString(),getSharedPreferences("index", Context.MODE_PRIVATE));
                addSummaryToDB(summary);
                summariesRef = database.getReference(subject).push();
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