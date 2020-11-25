package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    ShapeableImageView ivProfileIcon;
    TextView tvProfileName,tvProfileClass;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersPlace");

        setToolbarAndDrawer();
        ivProfileIcon = findViewById(R.id.ivProfilePictureIcon);
        tvProfileName = findViewById(R.id.tvProfileEntireName);
        tvProfileClass = findViewById(R.id.tvProfileClass);

        tvProfileName.setText(GlobalAcross.currentUser.getfName()+" "+GlobalAcross.currentUser.getlName());
        tvProfileClass.setText("כיתה: "+GlobalAcross.getCurrentUserGradeString());
        ivProfileIcon.setOnClickListener(this);

    }


    public void setToolbarAndDrawer(){
        drawerLayout = findViewById(R.id.drawer_layout_profile);
        navigationView = findViewById(R.id.nav_view_profile);
        toolbar = findViewById(R.id.toolbarProfile);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if (item.getTitle().equals("התנתקות")) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this);

            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            Toast.makeText(ProfileActivity.this, "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
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
            Intent intent = new Intent(ProfileActivity.this, SummariesSubjects.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("הגדרות")) {
            //Will be added in the future
            Toast.makeText(this, "מסך ההגדרות יהיה זמין לשימוש בעתיד.", Toast.LENGTH_LONG - 5000).show();
            return false;
        }
        if (item.getTitle().equals("פרופיל")) {
            return false;
        }
        if (item.getTitle().equals("מסך הבית")) {
            Intent intent = new Intent(ProfileActivity.this, Homepage.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v == ivProfileIcon){
            //Uploading profile picture here - needs to be done

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            startActivityForResult(Intent.createChooser(intent, "בחר\\י בתמונה עבור פרופילך"), 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode == 1){
            ShapeableImageView profilePictureReference = findViewById(R.id.ivProfilePictureIcon);
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());

                profilePictureReference.setScaleType(ImageView.ScaleType.FIT_XY);
                Bitmap bitmapPFP = BitmapFactory.decodeStream(inputStream);
                profilePictureReference.setForeground(null);
                profilePictureReference.setImageBitmap(bitmapPFP);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    }