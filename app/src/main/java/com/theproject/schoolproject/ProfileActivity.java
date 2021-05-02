package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
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
    ProgressDialog progressDialog;

    /**
     * Usual onCreate function that just checks if the user has a profile picture and if they do it sets the appropriate imageview to it.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersPlace").child(GlobalAcross.currentUserIndex+"").child("pfpPath");

        setToolbarAndDrawer();
        ivProfileIcon = findViewById(R.id.ivProfilePictureIcon);
        tvProfileName = findViewById(R.id.tvProfileEntireName);
        tvProfileClass = findViewById(R.id.tvProfileClass);

        tvProfileName.setText("שם: "+GlobalAcross.currentUser.getfName()+" "+GlobalAcross.currentUser.getlName());
        tvProfileClass.setText("כיתה: "+GlobalAcross.getCurrentUserGradeString());
        ivProfileIcon.setOnClickListener(this);


        if(!GlobalAcross.currentUser.getPfpPath().equals("none")){
            //The if only goes through if the user does have a profile picture
            firePfpRef = storage.getInstance().getReference().child(GlobalAcross.currentUser.getPfpPath());
            ShapeableImageView ivPFP = findViewById(R.id.ivProfilePictureIcon);

            try {
                final File localFile = File.createTempFile("profilePicture","png");
                firePfpRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                ShapeableImageView ivPFP = findViewById(R.id.ivProfilePictureIcon);
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                ivPFP.setScaleType(ImageView.ScaleType.FIT_XY);
                                ivPFP.setForeground(null);
                                ivPFP.setImageBitmap(bitmap);
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * The function sets all the appropriate toolbar and drawer of the activity to their ID's
     * */
    public void setToolbarAndDrawer(){
        drawerLayout = findViewById(R.id.drawer_layout_profile);
        navigationView = findViewById(R.id.nav_view_profile);
        toolbar = findViewById(R.id.toolbarProfile);

        GlobalAcross.activateGradientBackground(drawerLayout,4000,4000);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Repeated function that operates the side drawer (inherits navigationView) that navigates to the proper activities in the app and shows 2 dialogs (one for feedback and one for logging out)
     * @param item
     * @return
     */
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
                            Intent intent = new Intent(ProfileActivity.this, LoadingActivity.class);
                            Toast.makeText(ProfileActivity.this, "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
                            GlobalAcross.currentUser = null;
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
            builder.setMessage(GlobalAcross.infoMessage)
                    .setNegativeButton("הבנתי",null);
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }

    /**
     * Usual onBackPressed function
     * */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**This function checks if the user pressed the profileicon imageview and if they did - it prompts them with a dialog which asks if to pull the image from
     * the camera or from the memory and starts the activityForResult with the appropriate selection resultCode.
     * @param v
     * */
    @Override
    public void onClick(View v) {
        if(v == ivProfileIcon){
            //Uploading profile picture here - needs to be done
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this);
            builder.setMessage("לצלם או למשוך מהזיכרון?")
                    .setPositiveButton("מצלמה", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent,0); // open camera
                        }
                    })
                    .setNegativeButton("זיכרון", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");

                            startActivityForResult(Intent.createChooser(intent, "בחר/י בתמונה עבור פרופילך"), 1);
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * The function uploads the image from the selected source to the FirebaseStorage and updates its reference in the realtime database for the
     * current user accordingly.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            // PICK IMAGE FROM FILES (Gallery)
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("מעלים את התמונה...");
            progressDialog.setProgress(0);

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
                        myRef.setValue(firePfpRef.getPath()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ProfileActivity.this,"התמונה הועלתה בהצלחה.",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(ProfileActivity.this,"נתקלנו בבעיה... בדקו את החיבור לאינטרנט - התמונה לא הועלתה.",Toast.LENGTH_LONG).show();
                                }
                                try {
                                    if ((progressDialog != null) && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                } catch (final IllegalArgumentException e) {
                                    // Handle or log or ignore
                                } catch (final Exception e) {
                                    // Handle or log or ignore
                                } finally {
                                    progressDialog = null;
                                }
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        int currentProgress = (int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount()); //Formula to get the progress percentage of bytes transferred over total bytes times 100 casted into int
                        progressDialog.setProgress(currentProgress);
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 0 && resultCode == RESULT_OK) {
            // TAKE A PHOTO FROM CAMERA
            if (data.getExtras() != null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("מעלים את התמונה...");
                progressDialog.setProgress(0);

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
                        myRef.setValue(firePfpRef.getPath()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ProfileActivity.this,"התמונה הועלתה בהצלחה.",Toast.LENGTH_SHORT).show();

                                }
                                else{
                                    Toast.makeText(ProfileActivity.this,"נתקלנו בבעיה... בדקו את החיבור לאינטרנט - התמונה לא הועלתה.",Toast.LENGTH_LONG).show();
                                }
                                try {
                                    if ((progressDialog != null) && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                } catch (final IllegalArgumentException e) {
                                    // Handle or log or ignore
                                } catch (final Exception e) {
                                    // Handle or log or ignore
                                } finally {
                                    progressDialog = null;
                                }
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        int currentProgress = (int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount()); //Formula to get the progress percentage of bytes transferred over total bytes times 100 casted into int
                        progressDialog.setProgress(currentProgress);
                    }
                });
            }
        }
    }

    }