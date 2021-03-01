package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddSummaryActivity extends AppCompatActivity implements View.OnClickListener {

    String subject;
    FloatingActionButton floatingReturnButton;
    Button btnUpload;
    EditText summaryTitle,summaryDescription;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference summariesRef;
    ShapeableImageView ivAddAttachment;
    RadioButton isAttachment;
    LinearLayout llAttachment;
    boolean checkedRB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_summary);
        floatingReturnButton = findViewById(R.id.floatingReturnButton);
        btnUpload = findViewById(R.id.btnUpload);
        summaryTitle = findViewById(R.id.etSummaryTitle);
        summaryDescription = findViewById(R.id.etSummaryDescription);
        ivAddAttachment = findViewById(R.id.ivAddAttachment);
        isAttachment = findViewById(R.id.rbIsAttachment);
        llAttachment = findViewById(R.id.llAttachments);
        checkedRB = false;

        floatingReturnButton.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        ivAddAttachment.setOnClickListener(this);

        isAttachment.setOnClickListener(this);
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
            // RETURN TO THE PAGE BEFORE THE CURRENT
            finish();
        }
        if(v == btnUpload){
            // UPLOAD SUMMARY BUTTON
            if(checkValid(summaryTitle,summaryDescription)){
                String key = database.getReference(subject).push().getKey();
                Summary summary = new Summary(GlobalAcross.currentUser.getfName()+" "+GlobalAcross.currentUser.getlName(),summaryTitle.getText().toString(),summaryDescription.getText().toString(),getSharedPreferences("index", Context.MODE_PRIVATE));
                summary.setId(key);
                addSummaryToDB(summary);
                summariesRef = database.getReference(subject).push();

                Toast.makeText(this, "העלית את הסיכום בהצלחה", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
        }
        if(v == ivAddAttachment){
            // ADD AN ATTACHMENT (FILE) TO THE SUMMARY

        }


        if(v == isAttachment){
            // RADIO BUTTON THAT SETS THE ATTACHMENT LINEARLAYOUT TO VISIBLE / GONE FOR THE USER TO SEE IF THERE IS AN ATTACHMENT

            if(checkedRB == false){
                checkedRB=true;
                isAttachment.setChecked(checkedRB);
                llAttachment.setVisibility(View.VISIBLE);
            }
            else{
                checkedRB=false;
                isAttachment.setChecked(checkedRB);
                llAttachment.setVisibility(View.GONE);
            }
        }
    }

    public void addSummaryToDB(Summary summary) {
        database = FirebaseDatabase.getInstance();
        summariesRef = database.getReference(subject).push();
        summariesRef.setValue(summary);
    }

}