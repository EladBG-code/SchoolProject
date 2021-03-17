package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FirebaseStorage storage = FirebaseStorage.getInstance();

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    ShapeableImageView ivProfileIcon;
    TextView tvProfileName,tvProfileClass;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference firePfpRef;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersPlace/"+GlobalAcross.currentUserIndex+"/pfpPath");

        setToolbarAndDrawer();
        ivProfileIcon = findViewById(R.id.ivProfilePictureIcon);
        tvProfileName = findViewById(R.id.tvProfileEntireName);
        tvProfileClass = findViewById(R.id.tvProfileClass);

        tvProfileName.setText(GlobalAcross.currentUser.getfName()+" "+GlobalAcross.currentUser.getlName());
        tvProfileClass.setText("כיתה: "+GlobalAcross.getCurrentUserGradeString());
        ivProfileIcon.setOnClickListener(this);

        if(!GlobalAcross.currentUser.getPfpPath().equals("none")){
            //The if only goes through if the user does have a profile picture
            String pfpPath = GlobalAcross.currentUser.getPfpPath();
            firePfpRef = storage.getInstance().getReference().child(GlobalAcross.currentUser.getPfpPath());

            try {
                final File localFile = File.createTempFile("profilePicture","png");
                firePfpRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                ShapeableImageView ivPFP = findViewById(R.id.ivProfilePictureIcon);
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
                            sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);

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
            Intent intent = new Intent(ProfileActivity.this, SummariesSubjects.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("הגדרות")) {
            Intent intent = new Intent(ProfileActivity.this, SettingsUserActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("פרופיל")) {
            return false;
        }
        if (item.getTitle().equals("מסך הבית")) {
            Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("אודות")){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this);
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
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v == ivProfileIcon){
            //Uploading profile picture here - needs to be done

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this);
            builder.setMessage("לצלם או למשוך מהגלריה?")
                    .setPositiveButton("מצלמה", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent,0); // open camera
                        }
                    })
                    .setNegativeButton("גלריה", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");

                            startActivityForResult(Intent.createChooser(intent, "בחר\\י בתמונה עבור פרופילך"), 1);
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            // PICK IMAGE FROM FILES (Gallery)
            final ShapeableImageView profilePictureReference = findViewById(R.id.ivProfilePictureIcon);
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());

                final Bitmap bitmapPFP = BitmapFactory.decodeStream(inputStream);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapPFP.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] byteArray = baos.toByteArray();
                final String pfpPath = "profilePictures/" + UUID.randomUUID() + ".png";
                // DELETES OLD PFP
                if(!GlobalAcross.currentUser.getPfpPath().equals("none")){
                    firePfpRef.delete();
                }
                // DELETES OLD PFP
                firePfpRef = storage.getReference(pfpPath);

                UploadTask uploadTask = firePfpRef.putBytes(byteArray);
                uploadTask.addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //This function only activates when the upload of a new pfp is successful
                        profilePictureReference.setScaleType(ImageView.ScaleType.FIT_XY);
                        profilePictureReference.setForeground(null);
                        profilePictureReference.setImageBitmap(bitmapPFP);

                        GlobalAcross.currentUser.setPfpPath(firePfpRef.getPath());
                        myRef.setValue(firePfpRef.getPath());

                        Toast.makeText(ProfileActivity.this, "התמונת פרופיל שונתה בהצלחה!", Toast.LENGTH_LONG - 5000).show();
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 0 && resultCode == RESULT_OK) {
            // TAKE A PHOTO FROM CAMERA
            if (data.getExtras() != null) {
                Bundle extras = data.getExtras();
                final Bitmap bitmapPFP =  (Bitmap) extras.get("data");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapPFP.compress(Bitmap.CompressFormat.PNG, 100, baos);
                InputStream is = new ByteArrayInputStream(baos.toByteArray());
                final String pfpPath = "profilePictures/" + UUID.randomUUID() + ".png";
                // DELETES OLD PFP
                if(!GlobalAcross.currentUser.getPfpPath().equals("none")){
                    firePfpRef.delete();
                }
                // DELETES OLD PFP
                firePfpRef = storage.getReference(pfpPath);
                final ShapeableImageView profilePictureReference = findViewById(R.id.ivProfilePictureIcon);

                UploadTask uploadTask = firePfpRef.putStream(is);
                uploadTask.addOnSuccessListener(ProfileActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //This function only activates when the upload of a new pfp is successful
                        profilePictureReference.setScaleType(ImageView.ScaleType.FIT_XY);
                        profilePictureReference.setForeground(null);
                        profilePictureReference.setImageBitmap(bitmapPFP);

                        GlobalAcross.currentUser.setPfpPath(firePfpRef.getPath());
                        myRef.setValue(firePfpRef.getPath());

                        Toast.makeText(ProfileActivity.this, "התמונת פרופיל שונתה בהצלחה!", Toast.LENGTH_LONG - 5000).show();
                    }
                });
            }
        }
    }

    }