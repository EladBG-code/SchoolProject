package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    CardView cvDeleteSummary,cvReplaceFile;
    ProgressDialog progressDialog;
    Uri pdfUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    int deletionProgress = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_summary);

        subject = getIntent().getStringExtra("subject");
        summaryKey = getIntent().getStringExtra("key");
        database = FirebaseDatabase.getInstance().getReference().child(subject);
        pdfUri = null;
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
        cvReplaceFile = findViewById(R.id.cvReplaceFile);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        cvDeleteSummary.setOnClickListener(this);
        cvReplaceFile.setOnClickListener(this);
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
        if (v == sivSaveEditedSummary) {
            //Save shapable image view in clicker in the toolbar

            //First of all we check if the user even changed anything
            if (GlobalAcross.checkValid(etEditSummaryName, etEditSummaryDescription,EditSummaryActivity.this)) {


                progressDialog = new ProgressDialog(EditSummaryActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("שומרים את השינויים שלך...");
                progressDialog.setProgress(0);
                //Editor's note - add an app check for the new values lengths entered to the edittexts while changing

                database.child(summaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GlobalAcross.editingTemp = 0;
                        if (!subject.equals(spinnerSubjectCurrent)) {
                            GlobalAcross.editingTemp++;
                        }
                        if (!snapshot.child("title").getValue().toString().equals(etEditSummaryName.getText().toString())) {
                            GlobalAcross.editingTemp++;
                        }
                        if (!snapshot.child("description").getValue().toString().equals(etEditSummaryDescription.getText().toString())) {
                            GlobalAcross.editingTemp++;
                        }
                        if(pdfUri != null){
                            GlobalAcross.editingTemp += 2;   //adds 2 : one for deletion & one for uploading the new PDF
                        }

                        //FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("amountOfItemsToEdit").setValue(GlobalAcross.editingTemp); /*experiment*/
                        //Toast.makeText(EditSummaryActivity.this,temp,Toast.LENGTH_SHORT).show(); /*experiment*/


                        if(GlobalAcross.editingTemp != 0){
                            //Checks if any changes have even been made
                            progressDialog.show();

                            if (!snapshot.child("title").getValue().toString().equals(etEditSummaryName.getText().toString())) { //This function checks if the new title is different than the existing one and sets the on the realtime database as the new one
                                FirebaseDatabase.getInstance().getReference().child(subject).child(summaryKey).child("title").setValue(etEditSummaryName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                                        if(progressDialog.getProgress() == 100){
                                            progressDialog.dismiss();
                                            finish();
                                        }

                                    }
                                });
                            }


                            if (!snapshot.child("description").getValue().toString().equals(etEditSummaryDescription.getText().toString())) { //This function checks if the new description is different than the existing one and sets the on the realtime database as the new one
                                FirebaseDatabase.getInstance().getReference().child(subject).child(summaryKey).child("description").setValue(etEditSummaryDescription.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                                        if(progressDialog.getProgress() == 100){
                                            progressDialog.dismiss();
                                            Toast.makeText(EditSummaryActivity.this,"השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }
                                });
                            }

                            if(pdfUri != null){
                                FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("fileRef").addListenerForSingleValueEvent(new ValueEventListener() { //This section deletes the old PDF if a new one is selected
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        final StorageReference storageRef = storage.getInstance().getReference().child(snapshot.getValue().toString());
                                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Successfully deleted old summary on the first try


                                                    //Will upload the new File here
                                                    final String newFileName = UUID.randomUUID().toString();
                                                storage.getReference().child("SummariesFiles").child(newFileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                                                        if(progressDialog.getProgress() == 100){
                                                            progressDialog.dismiss();
                                                        }
                                                        FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("fileRef").setValue("/SummariesFiles/"+newFileName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //Success - changed the file reference of the summary in the realtime database to its actual new reference
                                                                Toast.makeText(EditSummaryActivity.this,"השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                                                                finish();
                                                            }
                                                        });
                                                    }
                                                });
                                                }

                                        }).addOnFailureListener(new OnFailureListener() { //This onfailurelistener tries to delete the file a second time inside of it
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                storageRef.delete().addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //Fails to delete the old summary file on the storage for the second time

                                                        Toast.makeText(EditSummaryActivity.this,"אאוץ'! נתקלנו בשגיאה - לא הצלחנו למחוק את הקובץ PDF הישן.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //Successfully deleted old summary on the second try

                                                        progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                                                        if(progressDialog.getProgress() == 100) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(EditSummaryActivity.this, "השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                                                            finish();
                                                        }
                                                        //Will upload the new File here
                                                            final String newFileName = UUID.randomUUID().toString();
                                                            storage.getReference().child("SummariesFiles").child(newFileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                                                                    if(progressDialog.getProgress() == 100){
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(EditSummaryActivity.this,"השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                                                                    }
                                                                    FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("fileRef").setValue("/SummariesFiles/"+newFileName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            //Success - changed the file reference of the summary in the realtime database to its actual new reference
                                                                            Toast.makeText(EditSummaryActivity.this,"השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                                                                            finish();
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                    }
                                                });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(EditSummaryActivity.this,"אאוץ'! נתקלנו בשגיאה - נסו שוב מאוחר יותר", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                            }

                        }

                        else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"לא נעשו שינויים בסיכום.",Toast.LENGTH_SHORT).show();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditSummaryActivity.this,"אאוץ'! נתקלנו בשגיאה - נסו שוב מאוחר יותר", Toast.LENGTH_SHORT).show();
                    }
                });


//                if (subject != spinnerSubjectCurrent) {
//                    //Toast.makeText(EditSummaryActivity.this,spinnerSubjectCurrent,Toast.LENGTH_SHORT).show();
//
//                }

//            database.child(summaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (subject != spinnerEditSubject.toString()){
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });


            }


        }

        if (v == cvReplaceFile) {
            //Activates if the replace PDF cardview is selected

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { //Checks if has the permission to read external storage
                selectPDF();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9); //Asks the user to give it the permission to do so if it doesn't have it and sets the request code to 9
                //onRequestPermissionResult will be the next line to this - all parameters are passed
            }
        }

        if (v == cvDeleteSummary) {
            //Activates if the delete summary cardview is pressed on

            Drawable temp = getResources().getDrawable(R.drawable.info_icon);
            temp.setTint(Color.RED);
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EditSummaryActivity.this);
            builder.setMessage("אתם בטוחים שאתם רוצים למחוק את הסיכום?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(subject).child(summaryKey);
                            //    myRef.child("usersThatLiked").child(String.valueOf(currentUserIndex)).removeValue();
                            progressDialog = new ProgressDialog(EditSummaryActivity.this);
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
                                                    deletionProgress += (100 / (usersThatLikedCertainSummary.size() + 2));
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
                                                        deletionProgress += 100 / (usersThatLikedCertainSummary.size() + 2);
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
                                                deletionProgress += 100 / (usersThatLikedCertainSummary.size() + 2); //The last piece to get 100% on the progressbar after the entire summary is removed from the realtime database
                                                progressDialog.setProgress(deletionProgress);
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(EditSummaryActivity.this, ViewSummariesOnSubjectActivity.class);
                                                intent.putExtra("SubjectSelected", subject);
                                                Toast.makeText(EditSummaryActivity.this, "הסיכום שלך נמחק בהצלחה!", Toast.LENGTH_SHORT).show();
                                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
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
                                    Toast.makeText(EditSummaryActivity.this,"אאוץ'! נתקלנו בשגיאה.", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    })
                    .setNegativeButton("לא", null)
                    .setIcon(temp)
                    .setTitle("אזהרה");
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void selectPDF() {
        // Method for offering the user to select a PDF file using file manager with an intent
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT); // in order to fetch the files - type of action
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //This checks if the user has selected a file or not
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData(); // This will return the Uri of the selected file
            Toast.makeText(EditSummaryActivity.this,"הקובץ: "+data.getData().getLastPathSegment()+" נבחר.",Toast.LENGTH_LONG);
        } else {
            Toast.makeText(EditSummaryActivity.this, "אנא בחרו קובץ", Toast.LENGTH_SHORT).show();
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