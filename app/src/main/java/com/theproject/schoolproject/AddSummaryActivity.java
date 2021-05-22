package com.theproject.schoolproject;

import android.Manifest;
import android.animation.Animator;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.transition.Scene;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.internal.FlowLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class AddSummaryActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    String subject;
    FloatingActionButton floatingReturnButton;
    CardView cvPublish,cvAttachment;
    EditText summaryTitle,summaryDescription;
    String summaryID;
    FirebaseDatabase database;
    FirebaseStorage storage;
    DatabaseReference summariesRef;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    TextView notification;
    Summary summary;
    Uri pdfUri; //Uri are URLs that are meant for local storage
    MediaPlayer mp;

    boolean hasFile;

    /**
     * Usual onCreate function that sets all of the required things to their appropriate values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_summary);

        hasFile = false;

        database = FirebaseDatabase.getInstance();// is used for storing URLs of uploaded files...
        storage = FirebaseStorage.getInstance();//is used for uploadinf files... Examples: PDF, Word etc

        mp = MediaPlayer.create(this, R.raw.clip_sound_effect);

        drawerFunction();

        floatingReturnButton = findViewById(R.id.floatingReturnButton);
        cvPublish = findViewById(R.id.cvPublish);
        summaryTitle = findViewById(R.id.etSummaryTitle);
        summaryDescription = findViewById(R.id.etSummaryDescription);

        cvAttachment = findViewById(R.id.cvAttachment);



        notification = findViewById(R.id.tvFileSelected);

        floatingReturnButton.setOnClickListener(this);
        cvPublish.setOnClickListener(this);

        subject = getIntent().getStringExtra("Subject");

        cvAttachment.setOnClickListener(this);
    }

    /**
     * Function starts a sound
     */
    public void startAttachSound(){
        mp.start();
    }

    /**
     * Function stops the sound
     */
    public void stopAttachSound(){
        mp.stop();
    }

    /**
     * Function sets the drawer and toolbar
     */
    public void drawerFunction(){
        drawerLayout = findViewById(R.id.drawer_layout_upload_summary);
        navigationView = findViewById(R.id.nav_view_upload_summary);
        toolbar = findViewById(R.id.toolbarUploadSubject);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }


    /**
     * Function sends onbackpressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Function sends the user back if presses the return button and uploads the PDF summary if it passes the validation and is pressed on publish cardview
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v == floatingReturnButton){
            // RETURN TO THE PAGE BEFORE THE CURRENT
            //finish();
            //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
            
        }
        if(v == cvPublish){
            // UPLOAD SUMMARY BUTTON

            if (pdfUri != null) {
                Cursor returnCursor = getContentResolver().query(pdfUri, null, null, null, null);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();

                if(returnCursor.getLong(sizeIndex) < 6291456) { //Checks if the file size is over 6MB withusing a curser that checks the file's size


                    if (GlobalAcross.checkValid(summaryTitle, summaryDescription, AddSummaryActivity.this)) {
                        summary = new Summary(GlobalAcross.currentUser.getfName() + " " + GlobalAcross.currentUser.getlName(), summaryTitle.getText().toString(), summaryDescription.getText().toString());
                        summary.setId(database.getReference(subject).push().getKey());
                        summaryID = summary.getId();
                        summariesRef = database.getReference(subject).push();
                        uploadFile(pdfUri);
                    }
                }
                else{
                    //Size over 6MB
                    cvAttachment.animate().rotationBy(-360).setDuration(650).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            pdfUri = null;
                            notification.setText("");
                            Toast.makeText(AddSummaryActivity.this,"הקובץ כבד מדי! אנו מרשים רק עד 6 MB.",Toast.LENGTH_LONG).show();
                            cvAttachment.setCardBackgroundColor(cvPublish.getCardBackgroundColor());
                            findViewById(R.id.ivStatusAttachment).setBackgroundResource(R.drawable.attach_file_icon);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();

                }
            }
            else{
                if(pdfUri == null)
                    Toast.makeText(AddSummaryActivity.this, "אנא בחרו קובץ PDF ממכשירכם.", Toast.LENGTH_SHORT).show();
            }
        }

        if(v == cvAttachment){
            if(pdfUri == null){
                // ADD AN ATTACHMENT (FILE) TO THE SUMMARY [WHEN THE PDF ISN'T SELECTED]
                cvAttachment.animate().rotationBy(360).setDuration(550).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        cvAttachment.setClickable(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(ContextCompat.checkSelfPermission(AddSummaryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){ //Checks if has the permission to read external storage
                            cvAttachment.setClickable(true);
                            selectPDF();
                        }
                        else{
                            ActivityCompat.requestPermissions(AddSummaryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9); //Asks the user to give it the permission to do so if it doesn't have it and sets the request code to 9
                            //onRequestPermissionResult will be the next line to this - all parameters are passed
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();


            }
            else{
                //THERE IS A PDF SELECTED
                cvAttachment.animate().rotationBy(360).setDuration(550).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        startAttachSound();
                        cvAttachment.setClickable(false);
                        pdfUri = null;
                        cvAttachment.setCardBackgroundColor(cvPublish.getCardBackgroundColor());
                        findViewById(R.id.ivStatusAttachment).setBackgroundResource(R.drawable.attach_file_icon);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cvAttachment.setClickable(true);
                        notification.setText("");
                        Toast.makeText(AddSummaryActivity.this, "בחירת קובץ ה- PDF הנוכחית הוסרה בהצלחה.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
            }


        }
    }

    /**
     * Function uses FirebaseStorage and uploads the selected PDF to the storage (generates UUID and checks if the file is too heavy and cancels the upload if it is)
     * @param pdfUri
     */
    private void uploadFile(final Uri pdfUri) {
        //Function that uploads the Uri to the storage cloud
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("מעלים את הקובץ...");
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String fileName = UUID.randomUUID().toString();
        StorageReference storageReference = storage.getReference(); //Sets the root path
        final StorageTask uploadTask = storageReference.child("SummariesFiles").child(fileName).putFile(pdfUri);


        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                        String url = taskSnapshot.getStorage().getDownloadUrl().toString(); //Returns the URL of the file that is being uploaded.
                        //Storing the URL in the realtime database.
                        //progressDialog.show();
                        summary.setFileRef("/SummariesFiles/"+fileName);
//                        summariesRef.setValue(summary);

//                        DatabaseReference reference = database.getReference().child(subject).child(summaryID); //Returns the path to the root
                        summariesRef.setValue(summary).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AddSummaryActivity.this,"הסיכום הועלה בהצלחה.",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else{
                                    Toast.makeText(AddSummaryActivity.this,"נתקלנו בבעיה... בדקו את החיבור לאינטרנט - הקובץ לא הועלה.",Toast.LENGTH_LONG).show();
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
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddSummaryActivity.this,"נתקלנו בבעיה... בדקו את חיבורכם לאינטרנט - ההעלאה נכשלה.",Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                //Tracks the progress of our upload task (progressbar)
                //This if checks if the file which is being uploaded is over 6MB and cancels this immediately if it is

                int currentProgress = (int) (100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount()); //Formula to get the progress percentage of bytes transferred over total bytes times 100 casted into int
                progressDialog.setProgress(currentProgress);

            }
        });

    }

    /**
     * Function checks if the app has the proper permission and requests it if it doesn't
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //Checks if the request code is 9 and that the user gave the app the permission in that string array as mentioned before
            selectPDF();
        }
        else{
            Toast.makeText(AddSummaryActivity.this, "אנא אשרו לאפליקציה את לעלות את הקובץ כדי לצרף אותו לסיכום.", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Function creates an action get content type intent that selects PDF only and starts activityForResult with resultCode 86 and the path
     */
    public void selectPDF() {
        // Method for offering the user to select a PDF file using file manager with an intent
        Intent intent = new Intent()
        .setType("application/pdf")
        .setAction(Intent.ACTION_GET_CONTENT) // in order to fetch the files - type of action
                ;
        startActivityForResult(intent, 86);
    }



    /**
     * Function checks if the result code is 86 and and sets the PDF uri to it if it is
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        //This checks if the user has selected a file or not
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {

//            MediaPlayer mediaPlayer = new MediaPlayer();
//            AssetFileDescriptor afd;
//            try {
//                afd = getAssets().openFd("clip_sound_effect.mp3");
//                mediaPlayer.setDataSource(afd.getFileDescriptor());
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Toast.makeText(AddSummaryActivity.this, "Playing audio from Asset directory", Toast.LENGTH_SHORT).show();

            cvAttachment.animate().rotationBy(-360).setDuration(550).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

//                    try {
//                        startSound("clip_sound_effect.mp3");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//

                    startAttachSound();


//                    MediaPlayer mediaPlayer = MediaPlayer.create(AddSummaryActivity.this, );
//                    mediaPlayer.start(); // no need to call prepare(); create() does that for you


                    pdfUri = data.getData(); // This will return the Uri of the selected file
                    cvAttachment.setCardBackgroundColor(Color.RED);
                    findViewById(R.id.ivStatusAttachment).setBackgroundResource(R.drawable.attachment_attached_icon);
                    cvAttachment.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    cvAttachment.setClickable(true);
                    String PDFname = getFileName(pdfUri);
                    notification.setText("הקובץ בשם: "+PDFname+" נבחר.");
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();
        }
        else {
            if (pdfUri == null)
                Toast.makeText(AddSummaryActivity.this, "אנא בחרו קובץ PDF", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function returns the name of the Uri of the path of the file which was selected
     * @param uri
     * @return
     */
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Repetitive function in each acitvity that logs the user out at will
     */
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
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getTitle().equals("התנתקות")){
            //This lines below represent the builder of the dialog of the disconnect
            AlertDialog.Builder builder = new AlertDialog.Builder(AddSummaryActivity.this);
            builder.setMessage(GlobalAcross.logoutMessage)
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           logoutFunction();
                        }
                    })
                    .setNegativeButton("לא",null);
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        if(item.getTitle().equals("סיכומים")){
            Intent intent = new Intent(AddSummaryActivity.this, SummariesSubjectsActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("הגדרות")){
            Intent intent = new Intent(AddSummaryActivity.this, SettingsUserActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("פרופיל")){
            Intent intent = new Intent(AddSummaryActivity.this,ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("מסך הבית")){
            Intent intent = new Intent(this,HomepageActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("אודות")){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AddSummaryActivity.this);
            builder.setMessage(GlobalAcross.infoMessage)
                    .setNegativeButton("הבנתי",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }
}