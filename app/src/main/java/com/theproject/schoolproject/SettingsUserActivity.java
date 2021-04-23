package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class SettingsUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    SharedPreferences sharedPreferences;
    CardView cvDeletePFP;
    Bitmap bitmapTemp;
    String tempPath;
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
        cvDeletePFP = findViewById(R.id.cvDeletePFP);

        GlobalAcross.activateGradientBackground(drawerLayout,4000,4000);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        cvDeletePFP.setOnClickListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("התנתקות")) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SettingsUserActivity.this);
            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(SettingsUserActivity.this, LoadingActivity.class);
                            Toast.makeText(SettingsUserActivity.this, "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
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
                    .setNegativeButton("לא", null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;

        }
        if (item.getTitle().equals("סיכומים")) {
            Intent intent = new Intent(SettingsUserActivity.this, SummariesSubjects.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("הגדרות")) {
            return false;
        }
        if (item.getTitle().equals("פרופיל")) {
            Intent intent = new Intent(SettingsUserActivity.this, ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("מסך הבית")) {
            Intent intent = new Intent(SettingsUserActivity.this, HomepageActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("אודות")){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SettingsUserActivity.this);
            builder.setMessage(GlobalAcross.infoMessage)
                    .setNegativeButton("הבנתי",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }

    @Override
    public void onClick(View v) {


        if(v == cvDeletePFP){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SettingsUserActivity.this);
            builder.setMessage("אתם בטוחים שאתם רוצים למחוק את התמונת פרופיל?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!GlobalAcross.currentUser.getPfpPath().equals("none")){
                                //Has a profile picture

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference firePfpRef = storage.getInstance().getReference().child(GlobalAcross.currentUser.getPfpPath());

                                try {
                                    final File localFile = File.createTempFile("profilePicture","png");
                                    firePfpRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            bitmapTemp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                firePfpRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        tempPath = GlobalAcross.currentUser.getPfpPath();
                                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UsersPlace/"+GlobalAcross.currentUserIndex+"/pfpPath");
                                        myRef.setValue("none").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                tempPath = GlobalAcross.currentUser.getPfpPath();
                                                GlobalAcross.currentUser.setPfpPath("none");
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    final Snackbar snackbar = null;
                                                    snackbar.make(findViewById(R.id.linearLayoutSettings),"התמונה נמחקה בהצלחה.",Snackbar.LENGTH_LONG)
                                                            .setAction("ביטול", new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    final ProgressDialog progressDialog = new ProgressDialog(SettingsUserActivity.this);
                                                                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                                    progressDialog.setCancelable(false);
                                                                    progressDialog.setTitle("מחזירים את התמונה...");
                                                                    progressDialog.setProgress(0);

                                                                    final StorageReference tempRef = FirebaseStorage.getInstance().getReference("profilePictures/" + UUID.randomUUID() + ".png");
                                                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                                    bitmapTemp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                                                    InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
                                                                    UploadTask uploadTask = tempRef.putStream(inputStream);
                                                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                            GlobalAcross.currentUser.setPfpPath(tempRef.getPath());
                                                                            FirebaseDatabase.getInstance().getReference("UsersPlace").child(GlobalAcross.currentUserIndex+"").child("pfpPath").setValue(tempRef.getPath()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    progressDialog.dismiss();
                                                                                    snackbar.make(findViewById(R.id.linearLayoutSettings),"התמונה שוחזרה בהצלחה.",Snackbar.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                        }
                                                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                                            progressDialog.show();
                                                                            int currentProgress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount()); //Formula to get the progress percentage of bytes transferred over total bytes times 100 casted into int
                                                                            progressDialog.setProgress(currentProgress);
                                                                        }
                                                                    });
                                                                }
                                                            }).show();
                                                }
                                            }})


                                                .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SettingsUserActivity.this, "אאוץ'! נתקלנו בשגיאה. נסו שוב.", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }
                                });


                            }
                            else{
                                //Doesn't have a profile picture
                                Snackbar.make(findViewById(R.id.linearLayoutSettings),"אין לכם תמונת פרופיל.",Snackbar.LENGTH_LONG)
                                        .setAction("הבנתי", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v){}}).show();
                            }
                        }
                    })
                    .setNegativeButton("לא",null);
            AlertDialog alert = builder.create();
            alert.show();
        }



    }
}