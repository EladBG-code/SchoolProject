package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class HomepageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FirebaseStorage storage = FirebaseStorage.getInstance();

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    TextView tvWelcomeMessage;
    ShapeableImageView ivProfilePictureHomepage;
    CardView cvToSummaries;
    SharedPreferences sharedPreferences;

    //public static final String fileName = "login";
    //public static final String Username = "username";

    /**Regular onCreate function but this one requires a certain API requirement and it has to be met - the requirement is vital in order for the service to work*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbarHome);
        tvWelcomeMessage = findViewById(R.id.tvHomeWelcome);
        cvToSummaries = findViewById(R.id.cvToSummaries);
        ivProfilePictureHomepage = findViewById(R.id.ivProfilePictureIconHomepage);
        //tvWelcomeMessage.setText("ברוכים השבים "+GlobalAcross.currentUser.getfName()); /*Tells the user a welcome message with their own name! */

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        ivProfilePictureHomepage.setOnClickListener(this);
        cvToSummaries.setOnClickListener(this);

        tryCatchPFP();

        globalAcrossActions();

        startForegroundService(new Intent(HomepageActivity.this,notificationService.class)); //5 like notification service starter - TEMPORARILY DISABLED

        if(getIntent().getBooleanExtra("openedNotif",false)){
            GlobalAcross.updateCurrentUserData();
            Intent intent = new Intent(HomepageActivity.this,ViewSummaryActivity.class);
            intent.putExtra("summaryKey",getIntent().getStringExtra("summaryKey"));
            intent.putExtra("subject",getIntent().getStringExtra("subject"));
            startActivity(intent);
        }

    }

    /**This function checks if the user has a profile picture and if they do - it sets the ShapableImageView in the homepage to it withusing the reference
     * in the realtime database for the FirebaseStorage load.
     * */
    public void tryCatchPFP(){
        //This function checks if the user has a pfp path in the database and creates a temporary file and downloads it to it if they have a pfp
        if(!GlobalAcross.currentUser.getPfpPath().equals("none")){
            //Gets the image of the user and displays it on the homepage top right corner
            StorageReference firePfpRef;
            String pfpPath = GlobalAcross.currentUser.getPfpPath();
            firePfpRef = storage.getInstance().getReference().child(GlobalAcross.currentUser.getPfpPath());

            try {
                final File localFile = File.createTempFile("profilePicture","png");
                firePfpRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                ShapeableImageView ivPFP = findViewById(R.id.ivProfilePictureIconHomepage);
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                ivPFP.setScaleType(ImageView.ScaleType.FIT_XY);
                                ivPFP.setForeground(null);
                                ivPFP.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**This function checks the user's device's current hour and sets the welcoming hourly message accordingly to the real time in real life*/
    public void setEnterMessage(){
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour >= 0 && currentHour <= 8){
            tvWelcomeMessage.setText("בוקר טוב "+GlobalAcross.currentUser.getfName());
        }
        else if (currentHour >= 8 && currentHour <= 16){
            tvWelcomeMessage.setText("צהריים טובים "+GlobalAcross.currentUser.getfName());
        }
        else if(currentHour >= 16 && currentHour <= 20){
            tvWelcomeMessage.setText("אחר הצהריים טובים "+GlobalAcross.currentUser.getfName());
        }
        else if (currentHour >= 20 && currentHour <= 22){
            tvWelcomeMessage.setText("ערב טוב "+GlobalAcross.currentUser.getfName());
        }
        else{
            tvWelcomeMessage.setText("לילה טוב "+GlobalAcross.currentUser.getfName());
        }
    }

    /**This function performs GlobalAcross (global class) functions and actions*/
    public void globalAcrossActions(){
        setEnterMessage();
        if(GlobalAcross.firstLoginSuggestion){
            GlobalAcross.firstLoginSuggestion = false;
            //First login --> show the user the ad to use the pdf scanner here
            tvWelcomeMessage.setText("ברוכים הבאים "+GlobalAcross.currentUser.getfName()+"!");
        }
        GlobalAcross.infoMessage ="שלום "+GlobalAcross.currentUser.getfName()+" שמי אלעד ואני פיתחתי את אפליקציה זו. אשמח לקבל פידבק ל E-mail שלי: "+"eladbgbusiness@gmail.com";

        //GlobalAcross.activateGradientBackground(drawerLayout);
    }

    /**Repeated function that operates the side drawer (inherits navigationView) that navigates to the proper activities in the app and shows 2 dialogs (one for feedback and one for logging out)*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getTitle().equals("התנתקות")){

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(HomepageActivity.this);
            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null; /*Temp disable*/
                            Intent intent = new Intent(HomepageActivity.this,LoadingActivity.class);
                            Toast.makeText(HomepageActivity.this,"התנתקת בהצלחה.", Toast.LENGTH_SHORT-5000).show();
                            sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(MainActivity.Index); //Shared preferences - login keeper (key and value)
                            editor.remove(MainActivity.Logged); //Shared preferences - login keeper
                            editor.commit();

                            startActivity(intent);
                            finishAfterTransition();
                        }
                    })
                    .setNegativeButton("לא",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;

        }
        if(item.getTitle().equals("סיכומים")){
            Intent intent = new Intent(HomepageActivity.this,SummariesSubjects.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("הגדרות")){
            Intent intent = new Intent(HomepageActivity.this, SettingsUserActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("פרופיל")){
            Intent intent = new Intent(HomepageActivity.this,ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("מסך הבית")){
            drawerLayout.closeDrawers();
            return false;
        }
        if(item.getTitle().equals("אודות")){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(HomepageActivity.this);
            builder.setMessage(GlobalAcross.infoMessage)
                    .setNegativeButton("הבנתי",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }

    /**This function checks if the user edited a summary before entering this activity and if it does it finishes.
     * Otherwise, this function checks if the drawer is open and closes it if it is. If it isn't open - it finishes.
     * */
    @Override
    public void onBackPressed() {
        //This function makes sure the app doesn't close down if someone is back pressing in their phone
        if(super.getClass() == EditSummaryActivity.class){
            finish();
        }
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
           finishAffinity();
        }
        else{
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    /**This function checks if the toSummaries cardview is selected and if it does - it sends the user to it.
     * Otherwise, it checks if the minimized ImageView of the homepage picture is clicked and if it does it sends the user to the profile activity.
     * */
    @Override
    public void onClick(View v) {
        if(v == cvToSummaries){
            Intent intent = new Intent(this,SummariesSubjects.class);
            startActivity(intent);
            //finish();
        }
        if(v == ivProfilePictureHomepage){
            Intent intent = new Intent(HomepageActivity.this,ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
        }
    }
}