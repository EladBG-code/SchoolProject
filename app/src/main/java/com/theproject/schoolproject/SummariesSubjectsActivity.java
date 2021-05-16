package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.net.IDN;

public class SummariesSubjectsActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    CardView cvMathematics,cvHistory,cvHebrew,cvCitizenship,cvBible,cvLiterature,cvEnglish,cvBiology,cvComputerScience,cvChemistry,cvPhysics,cvHistoryOfArt,cvCommunication,cvSociology;
    SharedPreferences sharedPreferences;

    /**
     * Usual onCreate function
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summaries_subjects);

        drawerFunction();

        //The function below this line attaches all buttons to their ID's in the XML and sets their onclicklistener function
        attachAndClickListenSubjectCardViews();

    }

    /**
     * Function sets the drawer to its appropriate values and activates the gradient background for the activity
     * */
    public void drawerFunction(){
        drawerLayout = findViewById(R.id.drawer_layout_subjects);
        navigationView = findViewById(R.id.nav_view_subjects);
        toolbar = findViewById(R.id.toolbarSelectSubject);

        GlobalAcross.activateGradientBackground(drawerLayout,4000,4000);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * The function assigns all subject cardviews in the gridview to their XML ID's appropriately and adds onClickListener to each of them
     * */
    public void attachAndClickListenSubjectCardViews(){
        cvMathematics = findViewById(R.id.cvMathematics);
        cvHistory = findViewById(R.id.cvHistory);
        cvHebrew = findViewById(R.id.cvHebrew);
        cvCitizenship = findViewById(R.id.cvCitizenship);
        cvBible = findViewById(R.id.cvBible);
        cvLiterature = findViewById(R.id.cvLiterature);
        cvEnglish = findViewById(R.id.cvEnglish);
        cvBiology = findViewById(R.id.cvBiology);
        cvComputerScience = findViewById(R.id.cvComputerScience);
        cvChemistry = findViewById(R.id.cvChemistry);
        cvPhysics = findViewById(R.id.cvPhysics);
        cvHistoryOfArt = findViewById(R.id.cvHistoryOfArt);
        cvCommunication = findViewById(R.id.cvCommunication);
        cvSociology = findViewById(R.id.cvSociology);

        cvMathematics.setOnClickListener(this);
        cvHebrew.setOnClickListener(this);
        cvHistory.setOnClickListener(this);
        cvCitizenship.setOnClickListener(this);
        cvBible.setOnClickListener(this);
        cvLiterature.setOnClickListener(this);
        cvEnglish.setOnClickListener(this);
        cvBiology.setOnClickListener(this);
        cvComputerScience.setOnClickListener(this);
        cvChemistry.setOnClickListener(this);
        cvPhysics.setOnClickListener(this);
        cvHistoryOfArt.setOnClickListener(this);
        cvCommunication.setOnClickListener(this);
        cvSociology.setOnClickListener(this);
    }

    /**
     * The function receives the string of the selected subject & its ID in the drawable folder of the vector asset image of the subject.
     * the function sends the user into the Activity that shows the summaries of the selected subject with the vector's ID image on top.
     * @param subjectSelected
     * @param vectorID
     */
    public void nextPageOfCardView(String subjectSelected,int vectorID){
        Intent intent = new Intent(this,ViewSummariesOnSubjectActivity.class);
        intent.putExtra("SubjectSelected",subjectSelected);
        GlobalAcross.selectedSubjectVectorID = vectorID;
        startActivity(intent);
    }

    /**
     * The onClick function sends the user to the appropriately selected button's subject (uses the nextPageOfCardView function)
     * @param v
     * */
    @Override
    public void onClick(View v) {

        if(v == cvMathematics){
            //Math subject
            nextPageOfCardView("מתמטיקה",R.drawable.calculating_mathematics_vector_icon);
        }
        if(v == cvHebrew){
            //Hebrew subject
            nextPageOfCardView("לשון",R.drawable.essay_lashon_hebrew_vector_icon);
        }
        if(v == cvHistory){
            //History subject
            nextPageOfCardView("היסטוריה",R.drawable.pillar_history_vector_icon);
        }
        if(v == cvCitizenship){
            //Citizenship subject
            nextPageOfCardView("אזרחות",R.drawable.israeli_flag_citizenship_vector_icon);
        }
        if(v == cvBible){
            //Bible subject
            nextPageOfCardView("תנ"+'"'+"ך",R.drawable.hebrew_bible_bible_vector_icon);
        }
        if(v == cvLiterature){
            //Literature subject
            nextPageOfCardView("ספרות",R.drawable.book_literature_vector_icon);
        }
        if(v == cvEnglish){
            //English subject
            nextPageOfCardView("אנגלית",R.drawable.abc_blocks_english_vector_icon);
        }
        if(v == cvBiology){
            //Biology subject
            nextPageOfCardView("ביולוגיה",R.drawable.dna_biology_vector_icon);
        }
        if(v == cvComputerScience){
            //Computer Science subject
            nextPageOfCardView("מדעי המחשב",R.drawable.web_programming_computer_science_vector_icon);
        }
        if(v == cvChemistry){
            //Chemistry subject
            nextPageOfCardView("כימיה",R.drawable.chemical_jar_chemistry_vector_icon);
        }
        if(v == cvPhysics){
            //Physics subject
            nextPageOfCardView("פיזיקה",R.drawable.formula_physics_vector_icon);
        }
        if(v == cvHistoryOfArt){
            //Arts subject
            nextPageOfCardView("תולדות האומנות",R.drawable.hieroglyph_history_of_art_vector_icon);
        }
        if(v == cvCommunication){
            //Communication subject
            nextPageOfCardView("תקשורת",R.drawable.communication_communication_vector_icon);
        }
        if(v == cvSociology){
            //Social Studies subject
            nextPageOfCardView("מדעי החברה",R.drawable.social_network_sociology_vector_icon);
        }
    }

    /**
     * Closes drawer if it's open, otherwise finishes
     * */
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    public void logoutFunction(){
        GlobalAcross.currentUser = null;
        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
        Toast.makeText(getApplicationContext(), "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
        sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("key");
        editor.remove("logged"); //Shared preferences - login keeper
        editor.commit();

        startActivity(intent);
        finish();
    }

    /**
     * Repeated function that operates the side drawer (inherits navigationView) that navigates to the proper activities
     * in the app and shows 2 dialogs (one for feedback and one for logging out)
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getTitle().equals("התנתקות")){
            //This lines below represent the builder of the dialog of the disconnect
            AlertDialog.Builder builder = new AlertDialog.Builder(SummariesSubjectsActivity.this);
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
            return false;
        }
        if(item.getTitle().equals("הגדרות")){
            Intent intent = new Intent(SummariesSubjectsActivity.this, SettingsUserActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("פרופיל")){
            Intent intent = new Intent(SummariesSubjectsActivity.this,ProfileActivity.class);
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
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(SummariesSubjectsActivity.this);
            builder.setMessage(GlobalAcross.infoMessage)
                    .setNegativeButton("הבנתי",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }
}