package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.theproject.schoolproject.GlobalAcross.currentUserIndex;

public class EditSummaryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    Spinner spinnerEditSubject;
    String subject,summaryKey,spinnerSubjectCurrent;
    EditText etEditSummaryName,etEditSummaryDescription;
    ArrayList<String> spinnerArray;
    DatabaseReference database;
    ShapeableImageView sivSaveEditedSummary;
    CardView cvDeleteSummary;
    ProgressDialog progressDialog;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    int deletionProgress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_summary);

        subject = getIntent().getStringExtra("subject");
        summaryKey = getIntent().getStringExtra("key");
        database = FirebaseDatabase.getInstance().getReference().child(subject);
        setToolbarAndDrawer();
        setEveryAttribute();
        spinnerEditSubject.setOnItemSelectedListener(this);



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

            if(subject != spinnerSubjectCurrent){

            }

            database.child(summaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (subject != spinnerEditSubject.toString()){

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        if(v == cvDeleteSummary){
            //Activates if the delete summary cardview is pressed on
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(subject).child(summaryKey);
            //    myRef.child("usersThatLiked").child(String.valueOf(currentUserIndex)).removeValue();
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("מוחקים את הסיכום שלך...");
            progressDialog.setProgress(0);
            progressDialog.show();

            myRef.child("usersThatLiked").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot != null) {
                        //Checks if the summary even has likes to start with the deletion

                        final ArrayList<String> usersThatLikedCertainSummary = new ArrayList<>(); //ArrayList for indexes of users who liked the summary that's about to me deleted
                        for (DataSnapshot child : snapshot.getChildren()) { //Traverses from every node within usersThatLiked within summary and adds that value to usersThatLikedCertainSummary
                            usersThatLikedCertainSummary.add(child.getValue() + "");
                        }
                        for (int i = 0; i < usersThatLikedCertainSummary.size(); i++) {
                            final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UsersPlace/" + usersThatLikedCertainSummary.get(i));
                            usersRef.child("favoriteSummaries").child(summaryKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    deletionProgress += (100 / (usersThatLikedCertainSummary.size()+2));
                                    //System.out.println(deletionProgress);
                                    progressDialog.setProgress(deletionProgress);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                        //The code gets here once it deletes all of the data the users who had the this summary as a favorite

                        //The string file in the reference inside the realtime database is opened and a storageref is opening and deleting the included file
                        FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("fileRef").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                StorageReference storageRef = storage.getInstance().getReference().child(snapshot.getValue().toString());
                                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        deletionProgress += 100/(usersThatLikedCertainSummary.size()+2);
                                        progressDialog.setProgress(deletionProgress);
                                    }
                                });
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                        //Deletes the entire summary from the realtime database
                        FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deletionProgress += 100/(usersThatLikedCertainSummary.size()+2); //The last piece to get 100% on the progressbar after the entire summary is removed from the realtime database
                                progressDialog.setProgress(deletionProgress);
                                progressDialog.dismiss();
                                Intent intent = new Intent(EditSummaryActivity.this,ViewSummariesOnSubjectActivity.class);
                                intent.putExtra("SubjectSelected",subject);
                                Toast.makeText(EditSummaryActivity.this,"הסיכום שלך נמחק בהצלחה!",Toast.LENGTH_SHORT).show();
                                startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
                                finishAffinity();
                            }
                        });

                        //This prints the indexes of the users who liked the summary
//                        for(int i=0;i<usersThatLikedCertainSummary.size();i++) {
//                            System.out.println(usersThatLikedCertainSummary.get(i));
//                        }

                    }
                    finish();

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Sets a string of when a choice on the subjects spinner is selected

        spinnerSubjectCurrent = parent.getItemAtPosition(position).toString();

        if(!spinnerSubjectCurrent.equals(subject)){
            Drawable temp = getResources().getDrawable(R.drawable.info_icon);
            temp.setTint(Color.RED);
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EditSummaryActivity.this);
            builder.setMessage("אתם בטוחים שאתם רוצים לשנות את נושא הסיכום?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            spinnerEditSubject.setSelection(finderSpinnerDefaultSubjectPosition(GlobalAcross.getAllSubjectsArrayList()));
                        }
                    })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    spinnerEditSubject.setSelection(finderSpinnerDefaultSubjectPosition(GlobalAcross.getAllSubjectsArrayList()));
                    dialog.dismiss();
                }
            })
            .setIcon(temp)
            .setTitle("אזהרה")
            ;
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        spinnerSubjectCurrent = subject;
    }
}