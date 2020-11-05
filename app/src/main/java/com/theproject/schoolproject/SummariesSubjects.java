package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SummariesSubjects extends AppCompatActivity {

    Button btnMath,btnHistory,btnHebrew,btnCitizenship,btnBible,btnLiterature,btnEnglish,btnBiology,btnComputerScience,btnChemistry,btnPhysics,btnArts,btnCommunication,btnSocialStudies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summaries_subjects);

        btnMath = findViewById(R.id.btnMath);
        btnHistory = findViewById(R.id.btnHistory);
        btnHebrew = findViewById(R.id.btnHebrew);
        btnCitizenship = findViewById(R.id.btnCitizenship);
        btnBible = findViewById(R.id.btnBible);
        btnLiterature = findViewById(R.id.btnLiterature);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnBiology = findViewById(R.id.btnBiology);
        btnComputerScience = findViewById(R.id.btnComputerScience);
        btnChemistry = findViewById(R.id.btnChemistry);
        btnPhysics = findViewById(R.id.btnPhysics);
        btnArts = findViewById(R.id.btnArts);
        btnCommunication = findViewById(R.id.btnCommunication);
        btnSocialStudies = findViewById(R.id.btnSocialStudies);

    }

    @Override
    public void onBackPressed() {
        /*if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }*/

        //NOTE: Undo the comment above for the if and else once there is a toolbar and a drawer on the summary selecting page
        super.onBackPressed();
    }
}