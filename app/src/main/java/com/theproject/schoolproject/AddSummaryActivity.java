package com.theproject.schoolproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddSummaryActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    String subject;
    FloatingActionButton floatingReturnButton;
    Button btnUpload;
    EditText summaryTitle,summaryDescription;
    String summaryID;
    FirebaseDatabase database = FirebaseDatabase.getInstance(); // is used for storing URLs of uploaded files...
    FirebaseStorage storage = FirebaseStorage.getInstance(); //is used for uploadinf files... Examples: PDF, Word etc
    DatabaseReference summariesRef;
    ShapeableImageView ivAddAttachment;
    RadioButton isAttachment;
    LinearLayout llAttachment;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;
    TextView notification;
    Summary summary;
    Uri pdfUri; //Uri are URLs that are meant for local storage
    boolean checkedRB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_summary);

        drawerFunction();

        floatingReturnButton = findViewById(R.id.floatingReturnButton);
        btnUpload = findViewById(R.id.btnUpload);
        summaryTitle = findViewById(R.id.etSummaryTitle);
        summaryDescription = findViewById(R.id.etSummaryDescription);
        ivAddAttachment = findViewById(R.id.ivAddAttachment);
        isAttachment = findViewById(R.id.rbIsAttachment);
        llAttachment = findViewById(R.id.llAttachments);
        notification = findViewById(R.id.tvFileSelected);
        checkedRB = false;

        floatingReturnButton.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        ivAddAttachment.setOnClickListener(this);

        isAttachment.setOnClickListener(this);
        subject = getIntent().getStringExtra("Subject");
    }

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



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean checkValid(EditText title,EditText description){
        if(title.getText().toString().length()<5){
            Toast.makeText(this, "אנא וודא\\י שיש לפחות 5 תווים בכותרת הסיכום", Toast.LENGTH_LONG).show();
            return false;
        }
        if(title.getText().toString().length()>20) {
            Toast.makeText(this, "אנא וודא\\י שיש עד 15 תווים בכותרת הסיכום", Toast.LENGTH_LONG).show();
            return false;
        }
        if(description.getText().toString().length()>46){
            Toast.makeText(this, "אנא וודא\\י שיש עד 46 תווים בתיאור", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
        //temp
    }

    @Override
    public void onClick(View v) {
        if(v == floatingReturnButton){
            // RETURN TO THE PAGE BEFORE THE CURRENT

            finish();
        }
        if(v == btnUpload){
            // UPLOAD SUMMARY BUTTON

            if (pdfUri != null) {
                if (checkValid(summaryTitle, summaryDescription)) {
                    summary = new Summary(GlobalAcross.currentUser.getfName() + " " + GlobalAcross.currentUser.getlName(), summaryTitle.getText().toString(), summaryDescription.getText().toString(), getSharedPreferences("index", Context.MODE_PRIVATE));
                    summary.setId(database.getReference(subject).push().getKey());
                    summaryID=summary.getId();
//                    addSummaryToDB(summary);
                    summariesRef = database.getReference(subject).push();
                    uploadFile(pdfUri);
//                    Toast.makeText(this, "העלית את הסיכום בהצלחה", Toast.LENGTH_SHORT).show();
                    //super.onBackPressed();
                }
            }
            else{
                Toast.makeText(AddSummaryActivity.this, "בחרו קובץ.", Toast.LENGTH_SHORT).show();
            }
        }
        if(v == ivAddAttachment){
            // ADD AN ATTACHMENT (FILE) TO THE SUMMARY

            if(ContextCompat.checkSelfPermission(AddSummaryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){ //Checks if has the permission to read external storage
                selectPDF();
            }
            else{
                ActivityCompat.requestPermissions(AddSummaryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9); //Asks the user to give it the permission to do so if it doesn't have it and sets the request code to 9
                //onRequestPermissionResult will be the next line to this - all parameters are passed
            }
        }


        if(v == isAttachment){
            // RADIO BUTTON THAT SETS THE ATTACHMENT LINEARLAYOUT TO VISIBLE / GONE FOR THE USER TO SEE IF THERE IS AN ATTACHMENT

            if(checkedRB == false){
                checkedRB=true;
                isAttachment.setChecked(checkedRB);
                llAttachment.setVisibility(View.VISIBLE);
            }
            else{
                checkedRB=false;
                isAttachment.setChecked(checkedRB);
                llAttachment.setVisibility(View.GONE);
            }
        }
    }

    private void uploadFile(Uri pdfUri) {
        //Function that uploads the Uri to the storage cloud
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("מעלים את הקובץ...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String name = UUID.randomUUID().toString();
        StorageReference storageReference = storage.getReference(); //Sets the root path
        storageReference.child("SummariesFiles").child(name).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                        String url = taskSnapshot.getStorage().getDownloadUrl().toString(); //Returns the URL of the file that is being uploaded.
                        //Storing the URL in the realtime database.
                        //progressDialog.show();
                        String namePdfUri = "/SummariesFiles/"+name;
                        summary.setFileRef(namePdfUri);
//                        summariesRef.setValue(summary);

//                        DatabaseReference reference = database.getReference().child(subject).child(summaryID); //Returns the path to the root
                        summariesRef.setValue(summary).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(AddSummaryActivity.this,"הסיכום הועלה בהצלחה.",Toast.LENGTH_SHORT).show();
                                    onBackPressed();
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
                int currentProgress = (int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount()); //Formula to get the progress percentage of bytes transferred over total bytes times 100 casted into int
                progressDialog.setProgress(currentProgress);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ //Checks if the request code is 9 and that the user gave the app the permission in that string array as mentioned before
            selectPDF();
        }
        else{
            Toast.makeText(AddSummaryActivity.this, "אנא אשרו לאפליקציה את לעלות את הקובץ כדי לצרף אותו לסיכום.", Toast.LENGTH_SHORT).show();
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
            notification.setText("הקובץ: " + data.getData().getLastPathSegment() + " נבחר.");
        } else {
            Toast.makeText(AddSummaryActivity.this, "אנא בחרו קובץ", Toast.LENGTH_SHORT).show();
        }
    }

//    public void addSummaryToDB(Summary summary) {
//        // Pushes the summary onto the database
//
//        summariesRef = database.getReference(subject).push();
//        summariesRef.setValue(summary);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getTitle().equals("התנתקות")){
            //This lines below represent the builder of the dialog of the disconnect
            AlertDialog.Builder builder = new AlertDialog.Builder(AddSummaryActivity.this);
            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(AddSummaryActivity.this,MainActivity.class);
                            Toast.makeText(AddSummaryActivity.this,"התנתקת בהצלחה.", Toast.LENGTH_SHORT).show();
                            sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(MainActivity.Index); //Shared preferences - login keeper (key and value)
                            editor.remove(MainActivity.Logged); //Shared preferences - login keeper
                            editor.commit();

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
            Intent intent = new Intent(AddSummaryActivity.this, SummariesSubjects.class);
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
            String info = "שלום "+GlobalAcross.currentUser.getfName()+", שמי אלעד ואני פיתחתי את אפליקציה זו. אשמח שתשלח\\י לי פידבק לאימייל: "+"eladbargal2@gmail.com";
            builder.setMessage(info)
                    .setNegativeButton("הבנתי",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }
}