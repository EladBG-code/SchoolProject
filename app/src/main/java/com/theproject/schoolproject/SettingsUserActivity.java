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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class SettingsUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    SharedPreferences sharedPreferences;
    CardView cvDeletePFP,cvClassYodAlef,cvClassYodBeit,cvClassYod,cvSaveEmail;
    Bitmap bitmapTemp;
    String tempPath;
    EditText etChangedEmail;

    /**
     * Usual onCreate function
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_user);

        setToolbarAndDrawer();
        setCurrentGradeSelected();
    }

    /**
     * The function highlights the current class to being selected according to the actual class of the user
     * */
    private void setCurrentGradeSelected() {
        switch (GlobalAcross.currentUser.getGrade()){
            case 1:{
                //Yod
                cvClassYod.setCardBackgroundColor(Color.GRAY);

                cvClassYodAlef.setCardBackgroundColor(Color.WHITE);
                cvClassYodBeit.setCardBackgroundColor(Color.WHITE);
                break;
            }
            case 2:{
                //YodAlef
                cvClassYodAlef.setCardBackgroundColor(Color.GRAY);

                cvClassYod.setCardBackgroundColor(Color.WHITE);
                cvClassYodBeit.setCardBackgroundColor(Color.WHITE);
                break;
            }
            case 3:{
                //Yodbeit
                cvClassYodBeit.setCardBackgroundColor(Color.GRAY);

                cvClassYodAlef.setCardBackgroundColor(Color.WHITE);
                cvClassYod.setCardBackgroundColor(Color.WHITE);
                break;
            }
        }
    }

    /**
     * This function sets the toolbar and drawer according to their ID's and activates the gradient background and adds the cardview listeners
     * for each of the three classes.
     * */
    public void setToolbarAndDrawer(){
        drawerLayout = findViewById(R.id.drawer_layout_settings);
        navigationView = findViewById(R.id.nav_view_settings);
        toolbar = findViewById(R.id.toolbarSettings);
        cvDeletePFP = findViewById(R.id.cvDeletePFP);

        cvSaveEmail = findViewById(R.id.cvSaveEmail);
        etChangedEmail = findViewById(R.id.etChangedEmail);

        etChangedEmail.setText(GlobalAcross.currentUser.getEmail());

        cvClassYod = findViewById(R.id.cvClassYod);
        cvClassYodAlef = findViewById(R.id.cvClassYodAlef);
        cvClassYodBeit = findViewById(R.id.cvClassYodBeit);

        GlobalAcross.activateGradientBackground(drawerLayout,4000,4000);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        cvClassYod.setOnClickListener(this);
        cvClassYodAlef.setOnClickListener(this);
        cvClassYodBeit.setOnClickListener(this);

        cvSaveEmail.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(this);
        cvDeletePFP.setOnClickListener(this);
    }

    /**
     * Repeated function that operates the side drawer (inherits navigationView) that navigates to the proper activities in the app and shows 2 dialogs (one for feedback and one for logging out)
     * @param item
     * @return
     */
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

    /**
     * This function spins and highlights the new class when the action is completed and changed in the realtime database successfully
     * @param i
     * @param classCard
     */
    public void changeGradeByGrade(final int i, final CardView classCard){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UsersPlace").child(GlobalAcross.currentUserIndex+"").child("grade");
        myRef.setValue(i).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                classCard.animate().rotationBy(360).setDuration(1200);
                GlobalAcross.currentUser.setGrade(i);
                setCurrentGradeSelected();
                Snackbar.make(findViewById(R.id.linearLayoutSettings),"הכיתה שונתה בהצלחה.",Snackbar.LENGTH_LONG)
                        .setAction("הבנתי", new View.OnClickListener() {
                            @Override
                            public void onClick(View v){
                            }})
                        .show();
            }
        });
    }


    /**
     * This function activates each of the three class's cardviews and deletes the profile picture of the user if the option is selected
     * as well as saves their new email if the option is selected.
     * @param v
     */
    @Override
    public void onClick(View v) {

        if(v == cvClassYod){
            //Selected the CardView to change to class Yod

            if (GlobalAcross.currentUser.getGrade() != 1){
                changeGradeByGrade(1,cvClassYod);
            }
            else{
                Snackbar.make(findViewById(R.id.linearLayoutSettings),"הכיתה כבר מוגדרת ככיתתכם.",Snackbar.LENGTH_SHORT).show();
            }

        }
        if(v == cvClassYodAlef){
            //Selected the CardView to change to class YodAlef

            if (GlobalAcross.currentUser.getGrade() != 2){
                changeGradeByGrade(2,cvClassYodAlef);
            }
            else{
                Snackbar.make(findViewById(R.id.linearLayoutSettings),"הכיתה כבר מוגדרת ככיתתכם.",Snackbar.LENGTH_SHORT).show();
            }

        }
        if(v == cvClassYodBeit){
            //Selected the CardView to change to class YodBeit

            if (GlobalAcross.currentUser.getGrade() != 3){
                changeGradeByGrade(3,cvClassYodBeit);
            }
            else{
                Snackbar.make(findViewById(R.id.linearLayoutSettings),"הכיתה כבר מוגדרת ככיתתכם.",Snackbar.LENGTH_SHORT).show();
            }
        }

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
                                        GlobalAcross.currentUser.setPfpPath("none");
                                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UsersPlace/"+GlobalAcross.currentUserIndex+"/pfpPath");
                                        myRef.setValue("none").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(findViewById(R.id.linearLayoutSettings),"התמונה נמחקה בהצלחה.",Snackbar.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
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

        if(v == cvSaveEmail){


            if(!etChangedEmail.getText().toString().equals(GlobalAcross.currentUser.getEmail())){
                if(validateNewEmail(etChangedEmail)){
                    //Change the email in the database and make a snackbar here //
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UsersPlace").child(GlobalAcross.currentUserIndex+"").child("email");
                    myRef.setValue(etChangedEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            GlobalAcross.currentUser.setEmail(etChangedEmail.getText().toString());
                            Snackbar.make(findViewById(R.id.linearLayoutSettings),"האימייל התעדכן בהצלחה.",Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else{
                Snackbar.make(findViewById(R.id.linearLayoutSettings),"לא שינינו את האימייל.",Snackbar.LENGTH_SHORT).show();
            }
        }



    }

    /**
     * The function returns the amount of illegal characters which are used in a certain string
     * @param string
     * @return
     */
    public int illegalCharacterCount(String string){
        char[] illegalArr = {'!','#','$','%','^','&','*','(',')','-','+','=','/','\\','?',' '};
        char[] charArr = string.toCharArray();
        int count = 0;
        for(int i=0;i<charArr.length;i++){
            for(int k=0;k<illegalArr.length;k++){
                if(charArr[i] == illegalArr[k])
                    count++;
            }
        }
        return count;
    }

    /**
     * Boolean function that return a true if the new Email is validated and false otherwise
     * @param newEmail
     * @return
     */
    public boolean validateNewEmail(EditText newEmail){
        String string = newEmail.getText().toString();
        if(countCharInString(string,'@') == 1 && string.length() > 5 && illegalCharacterCount(string) == 0){
            return true;
        }
        else{
            Snackbar.make(findViewById(R.id.linearLayoutSettings),"אנא בדקו את תקינות האימייל.",Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Function checks how many times a certain char appears inside a string and returns that number
     * @param string
     * @param ch
     * @return
     */
    public int countCharInString(String string,char ch){
        char[] chArray = string.toCharArray();
        int count = 0;
        for(int i=0;i<chArray.length;i++){
            if(chArray[i] == ch)
                count++;
        }
        return count++;
    }

}