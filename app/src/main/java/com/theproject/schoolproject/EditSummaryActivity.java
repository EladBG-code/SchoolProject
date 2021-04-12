package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditSummaryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    Spinner spinnerEditSubject;
    String subject,summaryKey;
    EditText etEditSummaryName,etEditSummaryDescription;
    ArrayList<String> spinnerArray;
    DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_summary);

        subject = getIntent().getStringExtra("subject");
        summaryKey = getIntent().getStringExtra("key");
        database = FirebaseDatabase.getInstance().getReference().child(subject);
        setToolbarAndDrawer();
        setEveryAttribute();




    }

    public void setSpinnerEditSubject(){
        spinnerEditSubject = findViewById(R.id.spinnerEditSubject);
        spinnerArray = GlobalAcross.getAllSubjectsArrayList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditSubject.setAdapter(adapter);

    }

    public void setEveryAttribute(){
        spinnerEditSubject.setSelection(finderSpinnerDefaultSubjectPosition(spinnerArray));

        database.child(summaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //String summaryAuthor = snapshot.child("author").getValue().toString();
                etEditSummaryName.setText(snapshot.child("title").getValue().toString());
                etEditSummaryDescription.setText(snapshot.child("description").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public int finderSpinnerDefaultSubjectPosition(ArrayList<String> spinnerArray){
        for(int i=0;i<spinnerArray.size();i++){
            if(spinnerArray.get(i).equals(subject)){
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void setToolbarAndDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout_edit_summary);
        navigationView = findViewById(R.id.nav_view_edit_summary);
        toolbar = findViewById(R.id.toolbarEditSummary);
        etEditSummaryName = findViewById(R.id.etEditSummaryName);
        etEditSummaryDescription = findViewById(R.id.etEditSummaryDescription);

        //etEditSummaryName.setText();

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        setSpinnerEditSubject();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("התנתקות")) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EditSummaryActivity.this);

            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(EditSummaryActivity.this, LoadingActivity.class);
                            Toast.makeText(EditSummaryActivity.this, "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("index", Context.MODE_PRIVATE);
                            GlobalAcross.currentUser = null;

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(MainActivity.Index); //Shared preferences - login keeper (key and value)
                            editor.remove(MainActivity.Logged); //Shared preferences - login keeper
                            editor.commit();

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
            Intent intent = new Intent(EditSummaryActivity.this, SummariesSubjects.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("הגדרות")) {
            //Will be added in the future
            Intent intent = new Intent(EditSummaryActivity.this, SettingsUserActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("פרופיל")) {
            Intent intent = new Intent(EditSummaryActivity.this, ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("מסך הבית")) {
            Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("אודות")) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EditSummaryActivity.this);
            builder.setMessage(GlobalAcross.infoMessage)
                    .setNegativeButton("הבנתי", null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }
}