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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class EditSummaryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    Spinner spinnerEditSubject;
    String subject,summaryKey,spinnerSubjectCurrent;
    EditText etEditSummaryName,etEditSummaryDescription;
    ArrayList<String> spinnerArray;
    DatabaseReference database;
    CardView cvDeleteSummary,cvReplaceFile;
    ProgressDialog progressDialog;
    Uri pdfUri;
    FloatingActionButton floatingSaveButton;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Boolean subjectChanged;
    int deletionProgress = 0;
    SharedPreferences sharedPreferences;

    /**
     * Usual onCreate function that sets the proper needs to their proper values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_summary);

        subject = getIntent().getStringExtra("subject");
        summaryKey = getIntent().getStringExtra("key");

        subjectChanged = false;
        pdfUri = null;
        setToolbarAndDrawer();
        setEveryAttribute();
        spinnerEditSubject.setOnItemSelectedListener(this);



    }

    /**
     * This function sets the spinner adapter of the subject changing option to its ArrayList of all subjects from the GlobalAcross function
     */
    public void setSpinnerEditSubject(){
        spinnerEditSubject = findViewById(R.id.spinnerEditSubject);
        spinnerArray = GlobalAcross.getAllSubjectsArrayList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditSubject.setAdapter(adapter);
    }

    /**
     * This function sets the text of the two EditTexts of the title and description to their actual values from the realtime database
     * */
    public void setEveryAttribute(){
        spinnerEditSubject.setSelection(finderSpinnerDefaultSubjectPosition(spinnerArray)); //sets the default value on the spinner as the current subject
        database = FirebaseDatabase.getInstance().getReference(subject);
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

    /**
     * This function sets the subject picking in order to change the spinner to its default value (which is the current subject its at)
     * @param spinnerArray
     * @return
     */
    public int finderSpinnerDefaultSubjectPosition(ArrayList<String> spinnerArray){
        for(int i=0;i<spinnerArray.size();i++){
            if(spinnerArray.get(i).equals(subject)){
                return i;
            }
        }
        return 0;
    }

    /**
     * Usual onBackPressed function that sends to former acitivity
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * This function sets the Toolbar as well as the drawer (which inherits from navigationView) and sets the onclicklisteners of all needed buttons and cardviews
     */
    public void setToolbarAndDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout_edit_summary);
        navigationView = findViewById(R.id.nav_view_edit_summary);
        toolbar = findViewById(R.id.toolbarEditSummary);
        etEditSummaryName = findViewById(R.id.etEditSummaryName);
        floatingSaveButton = findViewById(R.id.floatingSaveButton);
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
        floatingSaveButton.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        setSpinnerEditSubject();

    }

    public void logoutFunction(){
        GlobalAcross.currentUser = null;
        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
        Toast.makeText(getApplicationContext(), "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
        sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("key");
        editor.remove("logged"); //Shared preferences - login keeper
        editor.commit();

        stopService(new Intent(getApplicationContext(),NotificationService.class));
        startActivity(intent);
        finish();
    }


    /**
     * Repeated function that operates the side drawer (inherits navigationView) that navigates to the proper activities in the app and shows 2 dialogs (one for feedback and one for logging out)
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("התנתקות")) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EditSummaryActivity.this);

            builder.setMessage(GlobalAcross.logoutMessage)
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           logoutFunction();
                        }
                    })
                    .setNegativeButton("לא", null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;

        }
        if (item.getTitle().equals("סיכומים")) {
            Intent intent = new Intent(EditSummaryActivity.this, SummariesSubjectsActivity.class);
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

    /**
     * */

    /**
     * This function splits into three parts:
     *
     *    The first part is when the onClick - responds to the press of the floatingSaveButton
     *    The second part is when the onClick - responds to the press of the file change cardview
     *    The third part is when the onClick - responds to the press of the delete summary cardview
     *
     *    The first part saves all changes
     *    The second part selects a new PDF file to the PDF uri from the user's phone
     *    The third part uses the ID of the current summary and deletes it on the realtime database
     *
     *    After completing one of the three, the app finishes the activity
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v == floatingSaveButton) {
            //Save shapable image view in clicker in the toolbar
            //First of all we check if the user even changed anything
            if (GlobalAcross.checkValid(etEditSummaryName, etEditSummaryDescription,EditSummaryActivity.this)) {


                progressDialog = new ProgressDialog(EditSummaryActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("שומרים את השינויים שלך...");
                progressDialog.setProgress(0);
                progressDialog.setCancelable(false);
                progressDialog.show();
                //Editor's note - add an app check for the new values lengths entered to the edittexts while changing

                FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GlobalAcross.editingTemp = 0;
//                        if (!subject.equals(spinnerSubjectCurrent)) {
//                            GlobalAcross.editingTemp++;
//                        }
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

                        database = FirebaseDatabase.getInstance().getReference().child(subject);
                        if(GlobalAcross.editingTemp != 0) {
                            //Checks if any changes have even been made
                            progressDialog.show();

                            if (!snapshot.child("title").getValue().toString().equals(etEditSummaryName.getText().toString())) { //This function checks if the new title is different than the existing one and sets the on the realtime database as the new one
                                FirebaseDatabase.getInstance().getReference().child(subject).child(summaryKey).child("title").setValue(etEditSummaryName.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                                        if (progressDialog.getProgress() == 100) {
                                            progressDialog.dismiss();
                                            Toast.makeText(EditSummaryActivity.this, "השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
                                            intent.putExtra("SubjectSelected", subject);
                                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
                                            finishAffinity();
                                        }

                                    }
                                });
                            }


                            if (!snapshot.child("description").getValue().toString().equals(etEditSummaryDescription.getText().toString())) { //This function checks if the new description is different than the existing one and sets the on the realtime database as the new one
                                FirebaseDatabase.getInstance().getReference().child(subject).child(summaryKey).child("description").setValue(etEditSummaryDescription.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                                        if (progressDialog.getProgress() == 100) {
                                            progressDialog.dismiss();
                                            Toast.makeText(EditSummaryActivity.this, "השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
                                            intent.putExtra("SubjectSelected", subject);
                                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
                                            finishAffinity();
                                        }
                                    }
                                });
                            }

                            if (pdfUri != null){
                                //This function deletes the usersThatLiked the summary because the creator decided to change the PDF file & resets likes
                                DatabaseReference deleteUsersRef = FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("usersThatLiked");
                                deleteUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.equals(null)){

                                            final ArrayList<String> usersThatLikedCertainSummary = new ArrayList<>(); //ArrayList for indexes of users who liked the summary that's about to me deleted
                                            for (DataSnapshot child : snapshot.getChildren()) { //Traverses from every node within usersThatLiked within summary and adds that value to usersThatLikedCertainSummary
                                                usersThatLikedCertainSummary.add(child.getValue().toString());
                                            }
                                            FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("amountOfLikes").setValue(0); //Resets amount of likes
                                            for (int i = 0; i < usersThatLikedCertainSummary.size(); i++) {
                                                //This onSuccess listener deletes the summary reference from the favoriteSummaries of every user who liked that certain summary
                                                FirebaseDatabase.getInstance().getReference("UsersPlace/" + usersThatLikedCertainSummary.get(i)).child("favoriteSummaries").child(summaryKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        deletionProgress += (100 / (usersThatLikedCertainSummary.size() + 2));
                                                        //System.out.println(deletionProgress);
                                                        progressDialog.setProgress(deletionProgress);
                                                    }
                                                });
                                            }
                                            FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("usersThatLiked").removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            aSyncPDFchange();

                        }
                        else if(subjectChanged){
                            Toast.makeText(EditSummaryActivity.this, "השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
                            intent.putExtra("SubjectSelected", subject);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
                            finishAffinity();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(EditSummaryActivity.this,"לא נעשו שינויים בסיכום.",Toast.LENGTH_SHORT).show();
                            //EditSummaryActivity.super.onBackPressed();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditSummaryActivity.this,"אאוץ'! נתקלנו בשגיאה - נסו שוב מאוחר יותר", Toast.LENGTH_SHORT).show();
                    }
                });
                if (progressDialog.getProgress() >= 99 || subjectChanged) {
                    progressDialog.dismiss();
                    Toast.makeText(EditSummaryActivity.this, "השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
                    intent.putExtra("SubjectSelected", subject);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
                    finishAffinity();
                }

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
            Drawable temp = getResources().getDrawable(R.drawable.info_icon);
            temp.setTint(Color.RED);
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EditSummaryActivity.this);
            builder.setMessage("אתם בטוחים שאתם רוצים לשנות את קובץ הPDF? שינוי קובץ הסיכום יגרום למחיקת כל הלייקים שנצברו עבור סיכום זה עד כה.")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (ContextCompat.checkSelfPermission(EditSummaryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) { //Checks if has the permission to read external storage
                                selectPDF();
                            } else {
                                ActivityCompat.requestPermissions(EditSummaryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9); //Asks the user to give it the permission to do so if it doesn't have it and sets the request code to 9
                                //onRequestPermissionResult will be the next line to this - all parameters are passed
                            }
                        }
                    })
                    .setNegativeButton("לא",null)
                    .setIcon(temp)
                    .setTitle("אזהרה")
            ;
            AlertDialog alert = builder.create();
            alert.show();


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

                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("usersThatLiked");
                            //    myRef.child("usersThatLiked").child(String.valueOf(currentUserIndex)).removeValue();
                            progressDialog = new ProgressDialog(EditSummaryActivity.this);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setTitle("מוחקים את הסיכום שלך...");
                            progressDialog.setProgress(0);
                            progressDialog.show();

                            deleteChildrenFromRefMap(myRef);

                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                    if (snapshot != null) {
                                        //Checks if the summary even has likes to start with the deletion

                                        //HashMap<String,String> usersThatLikedCertainSummaryMap = new HashMap<>(snapshot.getValue(new GenericTypeIndicator<HashMap<String,String>>() {}));

                                        //final ArrayList<String> usersThatLikedCertainSummary = new ArrayList<>(); //ArrayList for indexes of users who liked the summary that's about to me deleted

                                        final Long childrenCount = snapshot.getChildrenCount();

//                                        for (DataSnapshot child : snapshot.getChildren()) { //Traverses from every node within usersThatLiked within summary and adds that value to usersThatLikedCertainSummary
//                                            FirebaseDatabase.getInstance().getReference("UserHashMap").child(child.getKey()).child("favoriteSummaries").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    deletionProgress += (100 / (childrenCount/100 + 2));
//                                                    //System.out.println(deletionProgress);
//                                                    progressDialog.setProgress(deletionProgress);
//                                                }
//                                            });
//
//
//                                            //usersThatLikedCertainSummary.add(child.getValue().toString());
//                                        }


//                                        for (int i = 0; i < usersThatLikedCertainSummary.size(); i++) {
//                                            final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UsersPlace/" + usersThatLikedCertainSummary.get(i));
//                                            usersRef.child("favoriteSummaries").child(summaryKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    deletionProgress += (100 / (usersThatLikedCertainSummary.size() + 2));
//                                                    //System.out.println(deletionProgress);
//                                                    progressDialog.setProgress(deletionProgress);
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//
//                                                }
//                                            });
//                                        }


                                        //The code gets here once it deletes all of the data the users who had the this summary as a favorite

                                        //The string file in the reference inside the realtime database is opened and a storageref is opening and deleting the included file
                                        FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("fileRef").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                StorageReference storageRef = storage.getInstance().getReference().child(snapshot.getValue().toString());
                                                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        deletionProgress += 100 / (childrenCount + 2);
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
                                             //The last piece to get 100% on the progressbar after the entire summary is removed from the realtime database

                                                progressDialog.setProgress(deletionProgress);
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
                                                intent.putExtra("SubjectSelected", subject);
                                                Toast.makeText(EditSummaryActivity.this, "הסיכום שלך נמחק בהצלחה!", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
                                                finishAffinity();
                                            }
                                        });

                                        //This prints the indexes of the users who liked the summary
//                        for(int i=0;i<usersThatLikedCertainSummary.size();i++) {
//                            System.out.println(usersThatLikedCertainSummary.get(i));
//                        }
                                    }
                                    //finishAffinity();
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

    public void deleteChildrenFromRefMap(DatabaseReference myRef){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null){
                    final Long childrenCount = snapshot.getChildrenCount();

                    for (DataSnapshot child : snapshot.getChildren()) { //Traverses from every node within usersThatLiked within summary and adds that value to usersThatLikedCertainSummary
                        FirebaseDatabase.getInstance().getReference("UserHashMap").child(child.getKey()).child("favoriteSummaries").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                deletionProgress += (100 / (childrenCount / 100 + 2));
                                //System.out.println(deletionProgress);
                                progressDialog.setProgress(deletionProgress);
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * This function was created in order to prevent aSynchronization issues - because the changing of the PDF
     * Awaits all former functions to finish so when it is called - it is called only when the rest are done with
     * The function uploads the new PDF file which was selected to the FirebaseStorage and updates the reference to it in the realtime database as well
     */
    public void aSyncPDFchange(){
        if(pdfUri != null){

            FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("fileRef").addListenerForSingleValueEvent(new ValueEventListener() { //This section deletes the old PDF if a new one is selected
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final StorageReference storageRef = storage.getInstance().getReference().child(snapshot.getValue().toString());
                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Successfully deleted old summary on the first try
                            progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                            //Will upload the new File here
                            final String newFileName = UUID.randomUUID().toString();

                            final UploadTask newUploadTask = storage.getReference().child("SummariesFiles").child(newFileName).putFile(pdfUri);
                            newUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);

                                    FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("fileRef").setValue("/SummariesFiles/" + newFileName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Success - changed the file reference of the summary in the realtime database to its actual new reference
                                            if (progressDialog.getProgress() == 100) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditSummaryActivity.this, "השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
                                                intent.putExtra("SubjectSelected", subject);
                                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
                                                finishAffinity();
                                            }
                                            //aSyncSubjectIf();
                                        }
                                    });
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    //Tracks the progress of our upload task (progressbar)
                                    if (snapshot.getTotalByteCount() > 6291456) { //This if checks if the file which is being uploaded is over 6MB and cancels this immediately if it is
                                        newUploadTask.cancel();
                                        Toast.makeText(EditSummaryActivity.this,"הקובץ כבד מדי! אנו מרשים רק עד 6 MB.",Toast.LENGTH_LONG).show();
                                        pdfUri = null;
                                        progressDialog.dismiss();
                                    }
                                }
                            })
                            ;

                        }
                    }).addOnFailureListener(new OnFailureListener() { //This onfailurelistener tries to delete the file a second time inside of it
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            storageRef.delete().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Fails to delete the old summary file on the storage for the second time

                                    Toast.makeText(EditSummaryActivity.this,"אאוץ'! נתקלנו בשגיאה - לא הצלחנו למחוק את הקובץ PDF הישן.", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
                                    intent.putExtra("SubjectSelected", subject);
                                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
                                    finishAffinity();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Successfully deleted old summary on the second try

                                    progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                                    //Will upload the new File here
                                    final String newFileName = UUID.randomUUID().toString();
                                    storage.getReference().child("SummariesFiles").child(newFileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);

                                            FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("fileRef").setValue("/SummariesFiles/"+newFileName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //Success - changed the file reference of the summary in the realtime database to its actual new reference
                                                    if(progressDialog.getProgress() == 100){
                                                        progressDialog.dismiss();
                                                        Toast.makeText(EditSummaryActivity.this,"השינויים נשמרו בהצלחה!", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
                                                        intent.putExtra("SubjectSelected", subject);
                                                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
                                                        finishAffinity();

                                                    }
                                                    //aSyncSubjectIf();
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
                    finishAffinity();
                }
            });

        }

    }

    /**
     * This function was created in order to prevent aSynchronization issues (very much like the latter) and it changes the subject of the summary
     * only when the other changes are done (peformed with OnSuccess or OnComplete listeners (according to the needs)
     * */
    public void aSyncSubjectIf(){
        //if(!subject.equals(spinnerSubjectCurrent)){
            //Enters if the subject was changed

            FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final Summary tempCopy = new Summary();
                    tempCopy.setAmountOfLikes(Long.valueOf(snapshot.child("amountOfLikes").getValue().toString()));
                    tempCopy.setAuthor(snapshot.child("author").getValue().toString());
                    tempCopy.setCreatorKey(snapshot.child("creatorKey").getValue().toString());
                    tempCopy.setDescription(snapshot.child("description").getValue().toString());
                    tempCopy.setFileRef(snapshot.child("fileRef").getValue().toString());
                    if(snapshot.child("hasNotified").getValue().equals(true)){
                        tempCopy.setHasNotified(true);
                    }
                    else{
                        tempCopy.setHasNotified(false);
                    }

                    deleteChildrenFromRefMap(FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("usersThatLiked"));

                    if(!snapshot.child("usersThatLiked").equals(null)){
                        //If thee summary has likes it'll copy the value of it and set it onto the new summary class

                        FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("usersThatLiked").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                                for (DataSnapshot child : snapshot.getChildren()) { //Traverses from every node within usersThatLiked within summary and adds that value to usersThatLikedCertainSummary
//                                    FirebaseDatabase.getInstance().getReference("UsersPlace").child(child.getValue().toString()).child("favoriteSummaries").child(summaryKey).removeValue(); //Removes all of the users who liked that certain summary on the database
//                                }
                                deleteChildrenFromRefMap(FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).child("usersThatLiked"));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                    tempCopy.setTitle(snapshot.child("title").getValue().toString());
                    FirebaseDatabase.getInstance().getReference(subject).child(summaryKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Successfully deleted the summary details from the realtime database on the former subject and will now push it to the new subject

                            //Reserves a new ID for the summary in the new subject and sets it to it in the next line
                            tempCopy.setId(FirebaseDatabase.getInstance().getReference(spinnerSubjectCurrent).push().getKey());
                            tempCopy.setUsersThatLiked(null);
                            tempCopy.setAmountOfLikes(0);
                            FirebaseDatabase.getInstance().getReference(spinnerSubjectCurrent).child(tempCopy.getId()).setValue(tempCopy).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Enters when the new summary has been successfully pushed onto the database under the new subject
                                    summaryKey = tempCopy.getId();

                                    deleteChildrenFromRefMap(FirebaseDatabase.getInstance().getReference(spinnerSubjectCurrent).child(tempCopy.getId()).child("usersThatLiked"));

                                    FirebaseDatabase.getInstance().getReference(spinnerSubjectCurrent).child(tempCopy.getId()).child("usersThatLiked").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            //Checks if the summary even has likes and if it does it changes the subject in it for each user onto the new subject
//                                            if(!snapshot.equals(null)) {
//
//                                                final ArrayList<String> usersThatLikedCertainSummary = new ArrayList<>(); //ArrayList for indexes of users who liked the summary that's about to be changed onto the new subject
//                                                for (DataSnapshot child : snapshot.getChildren()) { //Traverses from every node within usersThatLiked within summary and adds that value to usersThatLikedCertainSummary
//                                                    usersThatLikedCertainSummary.add(child.getValue().toString());
//                                                }
//                                                for (int i = 0; i < usersThatLikedCertainSummary.size(); i++) {
//                                                    //FirebaseDatabase.getInstance().getReference(spinnerSubjectCurrent).child(tempCopy.getId()).child("usersThatLiked").child(String.valueOf(i)).setValue(i);
//                                                    //final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UsersPlace/" + usersThatLikedCertainSummary.get(i));
//                                                    FirebaseDatabase.getInstance().getReference("UsersPlace/"+ usersThatLikedCertainSummary.get(i)).child("favoriteSummaries").child(summaryKey).removeValue(); //Removes the old favorite summaries for the old key and summary for the former summary
//                                                    //Temporarily disabled
//                                                    //FirebaseDatabase.getInstance().getReference("UsersPlace/"+ usersThatLikedCertainSummary.get(i)).child("favoriteSummaries").child(tempCopy.getId()).setValue(spinnerSubjectCurrent); //Sets the new summary and summary id as favorite for each liker of the summary
//                                                }
//                                            }
                                            subjectChanged = true;
                                            progressDialog.setProgress(progressDialog.getProgress() + 100 / GlobalAcross.editingTemp);
                                            if (progressDialog.getProgress() == 100){
                                                progressDialog.dismiss();
                                                subject = spinnerSubjectCurrent;
                                                Toast.makeText(EditSummaryActivity.this,"שיניתם נושא בהצלחה.", Toast.LENGTH_LONG).show();
                                            }
//                                            if(progressDialog.getProgress() == 100){
//                                                progressDialog.dismiss();
//                                                Toast.makeText(EditSummaryActivity.this,"שיניתם נושא בהצלחה.", Toast.LENGTH_LONG).show();
//                                                Intent intent = new Intent(EditSummaryActivity.this, HomepageActivity.class);
//                                                intent.putExtra("SubjectSelected", subject);
//                                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(EditSummaryActivity.this).toBundle());
//                                                finishAffinity();
//                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    //Does this when it finishes changing the liked references in everyone's accounts



                                }
                            });
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //Cancellation of the selection of a new subject
                }
            });


        //}
    }

    /**
     * Function creates an action get content type intent that selects PDF only and starts activityForResult with resultCode 86 and the path
     * */
    public void selectPDF() {
        // Method for offering the user to select a PDF file using file manager with an intent
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT); // in order to fetch the files - type of action
        startActivityForResult(intent, 86);
    }

    /**
     * Function checks if the result code is 86 and and sets the PDF uri to it if it is
     * @param requestCode
     * @param resultCode
     * @param data
     */
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

    /**
     * This function activates when the spinner is set (selected) on a new subject.
     * The function calls the void function: aSyncSubjectIf and performs the change (it also creates a new ID for every subject change since
     * it uses the .push function to the firebase realtime database.
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Sets a string of when a choice on the subjects spinner is selected

        spinnerSubjectCurrent = parent.getItemAtPosition(position).toString();

        if(!spinnerSubjectCurrent.equals(subject)){
            Drawable temp = getResources().getDrawable(R.drawable.info_icon);
            temp.setTint(Color.RED);
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(EditSummaryActivity.this);
            builder.setMessage("אתם בטוחים שאתם רוצים לשנות נושא? שינוי נושא הסיכום יגרום למחיקת כל הלייקים שנצברו עבור סיכום זה עד כה.")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog = new ProgressDialog(EditSummaryActivity.this);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setTitle("משנים את נושא הסיכום...");
                            progressDialog.setProgress(0);
                            progressDialog.show();
                            GlobalAcross.editingTemp = 1;
                            aSyncSubjectIf();
                            //GlobalAcross.editingTemp = 0;
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

    /**
     * When no subject is selected this function sets the subject selecting in order to change spinner - to its default value which is the current subject
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        spinnerSubjectCurrent = subject;
    }
}