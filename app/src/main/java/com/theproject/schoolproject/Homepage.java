package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FirebaseStorage storage = FirebaseStorage.getInstance();

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    TextView tvWelcomeMessage,tvToSummaries;
    ImageView ivLibraryIcon;
    ShapeableImageView ivProfilePictureHomepage;
    StorageReference firePfpRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbarHome);
        tvWelcomeMessage = findViewById(R.id.tvHomeWelcome);
        ivLibraryIcon = findViewById(R.id.ivLibraryIcon);
        tvToSummaries = findViewById(R.id.tvToSummaries);
        ivProfilePictureHomepage = findViewById(R.id.ivProfilePictureIconHomepage);

        

        tvWelcomeMessage.setText(tvWelcomeMessage.getText().toString()+" "+GlobalAcross.currentUser.getfName()); /*Tells the user a welcome message with their own name! */

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        ivLibraryIcon.setOnClickListener(this);
        tvToSummaries.setOnClickListener(this);
        ivProfilePictureHomepage.setOnClickListener(this);

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getTitle().equals("התנתקות")){

               androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Homepage.this);

            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(Homepage.this,MainActivity.class);
                            Toast.makeText(Homepage.this,"התנתקת בהצלחה.", Toast.LENGTH_SHORT-5000).show();
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("לא",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;

        }
        if(item.getTitle().equals("סיכומים")){
            Intent intent = new Intent(Homepage.this,SummariesSubjects.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("הגדרות")){
            Intent intent = new Intent(Homepage.this, SettingsUser.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("פרופיל")){
            Intent intent = new Intent(Homepage.this,ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("מסך הבית")){
            drawerLayout.closeDrawers();
            return false;
        }
        if(item.getTitle().equals("אודות")){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Homepage.this);
            String info = "שלום "+GlobalAcross.currentUser.getfName()+", שמי אלעד ואני פיתחתי את אפליקציה זו. אשמח שתשלח\\י לי פידבק לאימייל: "+"eladbargal2@gmail.com";
            builder.setMessage(info)
                    .setNegativeButton("הבנתי",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        //This function makes sure the app doesn't close down if someone is back pressing in their phone
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == ivLibraryIcon || v == tvToSummaries){
            Intent intent = new Intent(this,SummariesSubjects.class);
            startActivity(intent);
            //finish();
        }
        if(v == ivProfilePictureHomepage){
            Intent intent = new Intent(Homepage.this,ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
        }
    }
}