package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.net.URL;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    ShapeableImageView ivProfileIcon;
    TextView tvProfileName,tvProfileClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setToolbarAndDrawer();
        ivProfileIcon = findViewById(R.id.ivProfilePictureIcon);
        tvProfileName = findViewById(R.id.tvProfileEntireName);
        tvProfileClass = findViewById(R.id.tvProfileClass);

        tvProfileName.setText(GlobalAcross.currentUser.getfName()+" "+GlobalAcross.currentUser.getlName());
        tvProfileClass.setText("כיתה: "+getUserClass());
        ivProfileIcon.setOnClickListener(this);

    }

    public String getUserClass(){
        int x = GlobalAcross.currentUser.getGrade();
        if(x == 1){
            return "י'";
        }
        if(x == 2){
            return "י"+'"'+"א";
        }
        return "י"+'"'+"ב";
    }

    public void setToolbarAndDrawer(){
        drawerLayout = findViewById(R.id.drawer_layout_profile);
        navigationView = findViewById(R.id.nav_view_profile);
        toolbar = findViewById(R.id.toolbarProfile);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if (item.getTitle().equals("התנתקות")) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this);

            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            Toast.makeText(ProfileActivity.this, "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("לא", null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;

        }
        if (item.getTitle().equals("סיכומים")) {
            Intent intent = new Intent(ProfileActivity.this, SummariesSubjects.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("הגדרות")) {
            //Will be added in the future
            Toast.makeText(this, "מסך ההגדרות יהיה זמין לשימוש בעתיד.", Toast.LENGTH_LONG - 5000).show();
            return false;
        }
        if (item.getTitle().equals("פרופיל")) {
            return false;
        }
        if (item.getTitle().equals("מסך הבית")) {
            Intent intent = new Intent(ProfileActivity.this, Homepage.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v == ivProfileIcon){
            //Uploading profile picture here - needs to be done

        }
    }
}