package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddSummaryActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    String subject;
    FloatingActionButton floatingReturnButton;
    Button btnUpload;
    EditText summaryTitle,summaryDescription;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference summariesRef;
    ShapeableImageView ivAddAttachment;
    RadioButton isAttachment;
    LinearLayout llAttachment;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    SharedPreferences sharedPreferences;
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
        if(description.getText().toString().length()<20){
            Toast.makeText(this, "אנא וודא\\י שיש לפחות 20 תווים בתיאור הסיכום", Toast.LENGTH_LONG).show();
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
            if(checkValid(summaryTitle,summaryDescription)){
                String key = database.getReference(subject).push().getKey();
                Summary summary = new Summary(GlobalAcross.currentUser.getfName()+" "+GlobalAcross.currentUser.getlName(),summaryTitle.getText().toString(),summaryDescription.getText().toString(),getSharedPreferences("index", Context.MODE_PRIVATE));
                summary.setId(key);
                addSummaryToDB(summary);
                summariesRef = database.getReference(subject).push();

                Toast.makeText(this, "העלית את הסיכום בהצלחה", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
        }
        if(v == ivAddAttachment){
            // ADD AN ATTACHMENT (FILE) TO THE SUMMARY

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

    public void addSummaryToDB(Summary summary) {
        database = FirebaseDatabase.getInstance();
        summariesRef = database.getReference(subject).push();
        summariesRef.setValue(summary);
    }

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
            Intent intent = new Intent(AddSummaryActivity.this, SettingsUser.class);
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
            Intent intent = new Intent(this,Homepage.class);
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