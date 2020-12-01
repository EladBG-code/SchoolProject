package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class SummariesSubjects extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    Button btnMath,btnHistory,btnHebrew,btnCitizenship,btnBible,btnLiterature,btnEnglish,btnBiology,btnComputerScience,btnChemistry,btnPhysics,btnArts,btnCommunication,btnSocialStudies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summaries_subjects);

        drawerFunction();

        //The function below this line attaches all buttons to their ID's in the XML and sets their onclicklistener function
        attachAndClickListenSubjectButtons();
    }

    public void drawerFunction(){
        drawerLayout = findViewById(R.id.drawer_layout_subjects);
        navigationView = findViewById(R.id.nav_view_subjects);
        toolbar = findViewById(R.id.toolbarSelectSubject);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    public void attachAndClickListenSubjectButtons(){
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

        btnMath.setOnClickListener(this);
        btnHebrew.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        btnCitizenship.setOnClickListener(this);
        btnBible.setOnClickListener(this);
        btnLiterature.setOnClickListener(this);
        btnEnglish.setOnClickListener(this);
        btnBiology.setOnClickListener(this);
        btnComputerScience.setOnClickListener(this);
        btnChemistry.setOnClickListener(this);
        btnPhysics.setOnClickListener(this);
        btnArts.setOnClickListener(this);
        btnCommunication.setOnClickListener(this);
        btnSocialStudies.setOnClickListener(this);
    }

    public void nextPageOfButton(Button subject){
        Intent intent = new Intent(this,ViewSummariesOnSubject.class);
        intent.putExtra("SubjectSelected",subject.getText());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v == btnMath){
            //Math subject
            nextPageOfButton(btnMath);
        }
        if(v == btnHebrew){
            //Hebrew subject
            nextPageOfButton(btnHebrew);
        }
        if(v == btnHistory){
            //History subject
            nextPageOfButton(btnHistory);
        }
        if(v == btnCitizenship){
            //Citizenship subject
            nextPageOfButton(btnCitizenship);
        }
        if(v == btnBible){
            //Bible subject
            nextPageOfButton(btnBible);
        }
        if(v == btnLiterature){
            //Literature subject
            nextPageOfButton(btnLiterature);
        }
        if(v == btnEnglish){
            //English subject
            nextPageOfButton(btnEnglish);
        }
        if(v == btnBiology){
            //Biology subject
            nextPageOfButton(btnBiology);
        }
        if(v == btnComputerScience){
            //Computer Science subject
            nextPageOfButton(btnComputerScience);
        }
        if(v == btnChemistry){
            //Chemistry subject
            nextPageOfButton(btnChemistry);
        }
        if(v == btnPhysics){
            //Physics subject
            nextPageOfButton(btnPhysics);
        }
        if(v == btnArts){
            //Arts subject
            nextPageOfButton(btnArts);
        }
        if(v == btnCommunication){
            //Communication subject
            nextPageOfButton(btnCommunication);
        }
        if(v == btnSocialStudies){
            //Social Studies subject
            nextPageOfButton(btnSocialStudies);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getTitle().equals("התנתקות")){
            //This lines below represent the builder of the dialog of the disconnect
            AlertDialog.Builder builder = new AlertDialog.Builder(SummariesSubjects.this);
            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(SummariesSubjects.this,MainActivity.class);
                            Toast.makeText(SummariesSubjects.this,"התנתקת בהצלחה.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("לא",null);
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        if(item.getTitle().equals("סיכומים")){
            return false;
        }
        if(item.getTitle().equals("הגדרות")){
            Intent intent = new Intent(SummariesSubjects.this, SettingsUser.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("פרופיל")){
            Intent intent = new Intent(SummariesSubjects.this,ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("מסך הבית")){
            Intent intent = new Intent(this,Homepage.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        return false;
    }
}