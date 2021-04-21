package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class SummariesSubjects extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    CardView cvMathematics,cvHistory,cvHebrew,cvCitizenship,cvBible,cvLiterature,cvEnglish,cvBiology,cvComputerScience,cvChemistry,cvPhysics,cvHistoryOfArt,cvCommunication,cvSociology;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summaries_subjects);

        drawerFunction();

        //The function below this line attaches all buttons to their ID's in the XML and sets their onclicklistener function
        attachAndClickListenSubjectCardViews();

        GlobalAcross.activateGradientBackground(drawerLayout);
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

    public void attachAndClickListenSubjectCardViews(){
        cvMathematics = findViewById(R.id.cvMathematics);
        cvHistory = findViewById(R.id.cvHistory);
        cvHebrew = findViewById(R.id.cvHebrew);
        cvCitizenship = findViewById(R.id.cvCitizenship);
        cvBible = findViewById(R.id.cvBible);
        cvLiterature = findViewById(R.id.cvLiterature);
        cvEnglish = findViewById(R.id.cvEnglish);
        cvBiology = findViewById(R.id.cvBiology);
        cvComputerScience = findViewById(R.id.cvComputerScience);
        cvChemistry = findViewById(R.id.cvChemistry);
        cvPhysics = findViewById(R.id.cvPhysics);
        cvHistoryOfArt = findViewById(R.id.cvHistoryOfArt);
        cvCommunication = findViewById(R.id.cvCommunication);
        cvSociology = findViewById(R.id.cvSociology);

        cvMathematics.setOnClickListener(this);
        cvHebrew.setOnClickListener(this);
        cvHistory.setOnClickListener(this);
        cvCitizenship.setOnClickListener(this);
        cvBible.setOnClickListener(this);
        cvLiterature.setOnClickListener(this);
        cvEnglish.setOnClickListener(this);
        cvBiology.setOnClickListener(this);
        cvComputerScience.setOnClickListener(this);
        cvChemistry.setOnClickListener(this);
        cvPhysics.setOnClickListener(this);
        cvHistoryOfArt.setOnClickListener(this);
        cvCommunication.setOnClickListener(this);
        cvSociology.setOnClickListener(this);
    }

    public void nextPageOfCardView(String subjectSelected){
        Intent intent = new Intent(this,ViewSummariesOnSubjectActivity.class);
        intent.putExtra("SubjectSelected",subjectSelected);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v == cvMathematics){
            //Math subject
            nextPageOfCardView("מתמטיקה");
        }
        if(v == cvHebrew){
            //Hebrew subject
            nextPageOfCardView("לשון");
        }
        if(v == cvHistory){
            //History subject
            nextPageOfCardView("היסטוריה");
        }
        if(v == cvCitizenship){
            //Citizenship subject
            nextPageOfCardView("אזרחות");
        }
        if(v == cvBible){
            //Bible subject
            nextPageOfCardView("תנ"+'"'+"ך");
        }
        if(v == cvLiterature){
            //Literature subject
            nextPageOfCardView("ספרות");
        }
        if(v == cvEnglish){
            //English subject
            nextPageOfCardView("אנגלית");
        }
        if(v == cvBiology){
            //Biology subject
            nextPageOfCardView("ביולוגיה");
        }
        if(v == cvComputerScience){
            //Computer Science subject
            nextPageOfCardView("מדעי המחשב");
        }
        if(v == cvChemistry){
            //Chemistry subject
            nextPageOfCardView("כימיה");
        }
        if(v == cvPhysics){
            //Physics subject
            nextPageOfCardView("פיזיקה");
        }
        if(v == cvHistoryOfArt){
            //Arts subject
            nextPageOfCardView("תולדות האומנות");
        }
        if(v == cvCommunication){
            //Communication subject
            nextPageOfCardView("תקשורת");
        }
        if(v == cvSociology){
            //Social Studies subject
            nextPageOfCardView("מדעי החברה");
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
                            Intent intent = new Intent(SummariesSubjects.this,LoadingActivity.class);
                            Toast.makeText(SummariesSubjects.this,"התנתקת בהצלחה.", Toast.LENGTH_SHORT).show();
                            sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);
                            GlobalAcross.currentUser = null;

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(MainActivity.Index); //Shared preferences - login keeper (key and value)
                            editor.remove(MainActivity.Logged); //Shared preferences - login keeper
                            editor.commit();

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
            Intent intent = new Intent(SummariesSubjects.this, SettingsUserActivity.class);
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
            Intent intent = new Intent(this,HomepageActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("אודות")){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SummariesSubjects.this);
            builder.setMessage(GlobalAcross.infoMessage)
                    .setNegativeButton("הבנתי",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }
}