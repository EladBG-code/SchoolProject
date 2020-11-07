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

        drawerLayout = findViewById(R.id.drawer_layout_subjects);
        navigationView = findViewById(R.id.nav_view_subjects);
        toolbar = findViewById(R.id.toolbarSelectSubject);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        //The function below this line attaches all buttons to their ID's in the XML and sets their onclicklistener function
        attachAndClickListenSubjectButtons();
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
    @Override
    public void onClick(View v) {
        if(v == btnMath){
            //Math subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnMath.getText());
            startActivity(intent);
        }
        if(v == btnHebrew){
            //Hebrew subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnHebrew.getText());
            startActivity(intent);

        }
        if(v == btnHistory){
            //History subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnHistory.getText());
            startActivity(intent);
        }
        if(v == btnCitizenship){
            //Citizenship subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnCitizenship.getText());
            startActivity(intent);

        }
        if(v == btnBible){
            //Bible subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnBible.getText());
            startActivity(intent);

        }
        if(v == btnLiterature){
            //Literature subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnLiterature.getText());
            startActivity(intent);

        }
        if(v == btnEnglish){
            //English subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnEnglish.getText());
            startActivity(intent);

        }
        if(v == btnBiology){
            //Biology subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnBiology.getText());
            startActivity(intent);

        }
        if(v == btnComputerScience){
            //Computer Science subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnComputerScience.getText());
            startActivity(intent);

        }
        if(v == btnChemistry){
            //Chemistry subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnChemistry.getText());
            startActivity(intent);

        }
        if(v == btnPhysics){
            //Physics subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnPhysics.getText());
            startActivity(intent);

        }
        if(v == btnArts){
            //Arts subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnArts.getText());
            startActivity(intent);

        }
        if(v == btnCommunication){
            //Communication subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnCommunication.getText());
            startActivity(intent);

        }
        if(v == btnSocialStudies){
            //Social Studies subject
            Intent intent = new Intent(this,ViewSummariesOnSubject.class);
            intent.putExtra("SubjectSelected",btnSocialStudies.getText());
            startActivity(intent);

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

            AlertDialog.Builder builder = new AlertDialog.Builder(SummariesSubjects.this);

            builder.setMessage("את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
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
            //Will be added in the future
            Toast.makeText(this,"מסך ההגדרות יהיה זמין לשימוש בעתיד.", Toast.LENGTH_LONG-5000).show();
            return false;
        }
        if(item.getTitle().equals("פרופיל")){
            //Will be added in the future
            Toast.makeText(this,"מסך הפרופיל יהיה זמין לשימוש בעתיד.", Toast.LENGTH_LONG-5000).show();
            return false;
        }
        if(item.getTitle().equals("מסך הבית")){
            Intent intent = new Intent(this,Homepage.class);
            startActivity(intent);
            return false;
        }
        return false;
    }
}