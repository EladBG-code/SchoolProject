package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.Set;

public class SettingsUser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_user);

        setToolbarAndDrawer();

    }

    public void setToolbarAndDrawer(){
        drawerLayout = findViewById(R.id.drawer_layout_settings);
        navigationView = findViewById(R.id.nav_view_settings);
        toolbar = findViewById(R.id.toolbarSettings);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("התנתקות")) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SettingsUser.this);
            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(SettingsUser.this, MainActivity.class);
                            Toast.makeText(SettingsUser.this, "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
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
            Intent intent = new Intent(SettingsUser.this, SummariesSubjects.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("הגדרות")) {
            return false;
        }
        if (item.getTitle().equals("פרופיל")) {
            Intent intent = new Intent(SettingsUser.this, ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("מסך הבית")) {
            Intent intent = new Intent(SettingsUser.this, Homepage.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        return false;
    }
}