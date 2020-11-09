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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    TextView tvWelcomeMessage,tvToSummaries;
    ImageView ivLibraryIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbarHome);
        tvWelcomeMessage = findViewById(R.id.tvHomeWelcome);
        ivLibraryIcon = findViewById(R.id.ivLibraryIcon);
        tvToSummaries = findViewById(R.id.tvToSummaries);

        

        tvWelcomeMessage.setText(tvWelcomeMessage.getText().toString()+" "+GlobalAcross.currentUser.getfName()); /*Tells the user a welcome message with their own name! */

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        ivLibraryIcon.setOnClickListener(this);
        tvToSummaries.setOnClickListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getTitle().equals("התנתקות")){

               androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Homepage.this);

            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(Homepage.this,MainActivity.class);
                            Toast.makeText(Homepage.this,"התנתקת בהצלחה.", Toast.LENGTH_SHORT-5000).show();
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
            Intent intent = new Intent(Homepage.this,SummariesSubjects.class);
            startActivity(intent);
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
            return false;
        }
        return false;
    }



    @Override
    public void onBackPressed() {
        //This function makes sure the app doesn't close down if someone is back pressing in their phone
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == ivLibraryIcon || v == tvToSummaries){
            Intent intent = new Intent(this,SummariesSubjects.class);
            startActivity(intent);
            //finish();
        }
    }
}