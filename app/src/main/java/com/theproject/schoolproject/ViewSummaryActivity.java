package com.theproject.schoolproject;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class ViewSummaryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private TextView tvSummaryTitle;
    private TextView tvSummaryAuthor;
    private TextView tvSummaryDescription;
    DatabaseReference database;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    SharedPreferences sharedPreferences;
    ImageButton ibEditSummary;
    String summarySubject,summaryKey;
    ProgressBar pbLoadingPDF;

    String summaryCreatorKey;

    TextView tvPagesUpdate;

    int summaryCreatorIndex;
    PDFView pdfView;

    /**
     * Usual onCreate function
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summary);

        setToolbarAndDrawer();

        pbLoadingPDF = findViewById(R.id.pbLoadingPDF);

        tvPagesUpdate = findViewById(R.id.tvPagesUpdate);

        tvSummaryAuthor = findViewById(R.id.viewSummaryAuthor);
        tvSummaryTitle = findViewById(R.id.viewSummaryTitle);
        tvSummaryDescription = findViewById(R.id.viewSummaryDescription);
        pdfView = findViewById(R.id.pdfView);

        summaryKey = getIntent().getStringExtra("summaryKey");
        summarySubject = getIntent().getStringExtra("subject");

        database = FirebaseDatabase.getInstance().getReference().child(summarySubject);
        getIndexKeyAndSubject();
    }

    /**
     * Sets the EditTexts and PDF to their appropriate values in the Firebase realtime database and FirebaseStorage
     */
    public void getIndexKeyAndSubject(){
        ibEditSummary = findViewById(R.id.ibEditSummary);
        ibEditSummary.setOnClickListener(this);

        database = FirebaseDatabase.getInstance().getReference().child(summarySubject);
        database.child(summaryKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String summaryTitle = snapshot.child("title").getValue().toString();
                String summaryAuthor = snapshot.child("author").getValue().toString();
                String summaryDescription = snapshot.child("description").getValue().toString();

                //pdf getDownloadUrl try
                String pdf = snapshot.child("fileRef").getValue().toString();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference(); //Sets the root path
                final long sixMegaBytes = 1024*1024*6;
                storageReference.child(pdf).getBytes(sixMegaBytes).addOnSuccessListener(new OnSuccessListener<byte[]>(){
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Got the download URL for 'users/me/profile.png' in uri
                        System.out.println(bytes.toString());
                        //pdfView.getpag
                        pdfView.fromBytes(bytes).password(null).enableSwipe(true).swipeHorizontal(false).enableDoubletap(true).defaultPage(0).onPageError(new OnPageErrorListener() {
                            @Override
                            public void onPageError(int page, Throwable t) {
                                Toast.makeText(ViewSummaryActivity.this,"שגיאה בקריאת הקובץ",Toast.LENGTH_LONG);
                            }
                        }).onTap(new OnTapListener() {
                            @Override
                            public boolean onTap(MotionEvent e) {
                                return true;
                            }
                        }).onRender(new OnRenderListener() {
                            @Override
                            public void onInitiallyRendered(int nbPages) {
                                pbLoadingPDF.setVisibility(View.VISIBLE);
                                pbLoadingPDF.setProgress(0);
                                pdfView.fitToWidth(0);
                            }
                        }).onPageChange(new OnPageChangeListener() {
                            @Override
                            public void onPageChanged(int page, int pageCount) {
                                tvPagesUpdate.setText(pdfView.getCurrentPage()+1+"/"+pdfView.getPageCount());
                            }
                        }).

                                enableAnnotationRendering(true).load();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewSummaryActivity.this,"שגיאה בקריאת הקובץ",Toast.LENGTH_LONG);
                    }
                }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        pbLoadingPDF.setProgress(100);
                        pbLoadingPDF.setVisibility(View.GONE);
                        pdfView.setVisibility(View.VISIBLE);
                    }
                });

                //summaryCreatorIndex = Integer.valueOf(snapshot.child("creatorIndex").getValue().toString());
                summaryCreatorKey = snapshot.child("creatorKey").getValue().toString();

                if(summaryCreatorKey.equals( GlobalAcross.currentUserKey)){
                    //Enables the edit shapableimage in the toolbar if the currentuserindex is equal to the summary creator's index
                    ibEditSummary.setVisibility(View.VISIBLE);
                }
                tvSummaryTitle.setText(summaryTitle);
                tvSummaryAuthor.setText("סופר\\ת: " + summaryAuthor);
                tvSummaryDescription.setText(summaryDescription);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Sets the Toolbar and Drawer to their appropriate ID's in the XML and listens to clicking the navigation view as well as toggling the drawer layout as usual
     */
    public void setToolbarAndDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout_view_summary);
        navigationView = findViewById(R.id.nav_view_view_summary);
        toolbar = findViewById(R.id.toolbarViewSummary);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    public void logoutFunction(){
        GlobalAcross.currentUser = null;
        Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
        Toast.makeText(getApplicationContext(), "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
        sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("index"); //Shared preferences - login keeper (key and value)
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
        if (item.getTitle().equals("התנתקות")) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ViewSummaryActivity.this);

            builder.setMessage(GlobalAcross.logoutMessage)
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logoutFunction();
                        }
                    })
                    .setNegativeButton("לא", null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;

        }
        if (item.getTitle().equals("סיכומים")) {
            Intent intent = new Intent(ViewSummaryActivity.this, SummariesSubjectsActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("הגדרות")) {
            //Will be added in the future
            Intent intent = new Intent(ViewSummaryActivity.this, SettingsUserActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("פרופיל")) {
            Intent intent = new Intent(ViewSummaryActivity.this, ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("מסך הבית")) {
            Intent intent = new Intent(ViewSummaryActivity.this, HomepageActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("אודות")) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ViewSummaryActivity.this);
            builder.setMessage(GlobalAcross.infoMessage)
                    .setNegativeButton("הבנתי", null);

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

        if (getIntent().getBooleanExtra("openedNotif",false)){
            Intent intent = new Intent(this, HomepageActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else{
            if (super.getClass().equals(ViewSummariesOnSubjectActivity.class)){
                Intent intent = new Intent(this, ViewSummariesOnSubjectActivity.class);
                intent.putExtra("SubjectSelected", summarySubject);

                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        }
        finish();


    }

    /**
     * Function sends the user to edit their own summary with the appropriate values sent with the intent
     * @param v 
     * */
    @Override
    public void onClick(View v) {
        if(v == ibEditSummary){
            //If the editing pencil in the view summary is clicked
            Intent intent = new Intent(ViewSummaryActivity.this,EditSummaryActivity.class);
            intent.putExtra("key",summaryKey);
            intent.putExtra("subject",summarySubject);
            intent.putExtra("SubjectVectorID",getIntent().getIntExtra("SubjectVectorID",0));
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
    }

}