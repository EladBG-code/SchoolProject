package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.theproject.schoolproject.GlobalAcross.currentUserIndex;

public class EditSummaryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    Spinner spinnerEditSubject;
    String subject,summaryKey;
    EditText etEditSummaryName,etEditSummaryDescription;
    ArrayList<String> spinnerArray;
    DatabaseReference database;
    ShapeableImageView sivSaveEditedSummary;
    CardView cvDeleteSummary;
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
        spinnerEditSubject.setSelection(finderSpinnerDefaultSubjectPosition(spinnerArray)); //sets the default value on the spinner as the current subject

        database.child(summaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
        finish();
    }

    public void setToolbarAndDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout_edit_summary);
        navigationView = findViewById(R.id.nav_view_edit_summary);
        toolbar = findViewById(R.id.toolbarEditSummary);
        etEditSummaryName = findViewById(R.id.etEditSummaryName);
        sivSaveEditedSummary = findViewById(R.id.sivSaveEditedSummary);
        etEditSummaryDescription = findViewById(R.id.etEditSummaryDescription);
        cvDeleteSummary = findViewById(R.id.cvDeleteSummary);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        cvDeleteSummary.setOnClickListener(this);
        sivSaveEditedSummary.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if(v == sivSaveEditedSummary){
            //Save shapable image view in clicker in the toolbar

            //First of all we check if the user even changed anything
            database.child(summaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        if(v == cvDeleteSummary){
            //Activates if the delete summary cardview is pressed on
//            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UsersPlace/"+ currentUserIndex);
//            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(subject).child(summaryKey);
//
//
//
//            myRef.child("usersThatLiked").child(String.valueOf(currentUserIndex)).removeValue();
//
//
//            usersRef.child("favoriteSummaries").child(selectedKeySummary).removeValue();
//

        }
    }
}