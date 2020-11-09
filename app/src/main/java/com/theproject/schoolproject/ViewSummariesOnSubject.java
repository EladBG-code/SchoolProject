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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class ViewSummariesOnSubject extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    TextView tvSubjectName;
    FloatingActionButton floatingUploadButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summaries_on_subject);

        drawerLayout = findViewById(R.id.drawer_layout_subject);
        navigationView = findViewById(R.id.nav_view_subject);
        toolbar = findViewById(R.id.toolbarSubjectSelected);
        floatingUploadButton = findViewById(R.id.floatingUploadButton);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                // Visible/Enable the FAB
            }

            public void onDrawerOpened(View drawerView) {
                // Hide/Disable the FAB
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // invert the slideOffset value
                //This line fades the floating action button as the user slides their finger across the screen
                floatingUploadButton.setAlpha(1-slideOffset);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        floatingUploadButton.setOnClickListener(this);
        tvSubjectName = findViewById(R.id.tvSubjectName);
        tvSubjectName.setText(getIntent().getStringExtra("SubjectSelected")); /*This line sets the name of the subject which was selected as the title of the subject's summary page*/
    }

    @Override
    public void onBackPressed() {
         if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("התנתקות")){

            AlertDialog.Builder builder = new AlertDialog.Builder(ViewSummariesOnSubject.this);

            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(ViewSummariesOnSubject.this,MainActivity.class);
                            Toast.makeText(ViewSummariesOnSubject.this,"התנתקת בהצלחה.", Toast.LENGTH_SHORT).show();
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
            super.onBackPressed();
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

    @Override
    public void onClick(View v) {
        if(v == floatingUploadButton){
            //Upload floating action button selected
            Intent intent = new Intent(this,AddSummaryActivity.class);
            startActivity(intent);
        }

    }


}