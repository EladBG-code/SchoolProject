package com.theproject.schoolproject;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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
    ShapeableImageView sivEditSummary;
    String summarySubject,summaryKey;
    ProgressBar pbLoadingPDF;

    TextView tvPagesUpdate;

    int summaryCreatorIndex;
    PDFView pdfView;

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

        //tvPagesUpdate.setX(Resources.getSystem().getDisplayMetrics().widthPixels / 2);



        database = FirebaseDatabase.getInstance().getReference().child(summarySubject);
        getIndexKeyAndSubject();

    }

    public void getIndexKeyAndSubject(){
        sivEditSummary = findViewById(R.id.sivEditSummary);
        sivEditSummary.setOnClickListener(this);

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
                                //pbLoadingPDF.setProgress(100);
                                //pbLoadingPDF.setVisibility(View.GONE);
                                //pbLoadingPDF.setVisibility(View.GONE);

                                //pdfView.setVisibility(View.VISIBLE);
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

                summaryCreatorIndex = Integer.valueOf(snapshot.child("creatorIndex").getValue().toString());
                if(summaryCreatorIndex == GlobalAcross.currentUserIndex){
                    //Enables the edit shapableimage in the toolbar if the currentuserindex is equal to the summary creator's index
                    sivEditSummary.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if (item.getTitle().equals("התנתקות")) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ViewSummaryActivity.this);

            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(ViewSummaryActivity.this, LoadingActivity.class);
                            Toast.makeText(ViewSummaryActivity.this, "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
                            sharedPreferences = getSharedPreferences("index", Context.MODE_PRIVATE);
                            GlobalAcross.currentUser = null;

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
            Intent intent = new Intent(ViewSummaryActivity.this, SummariesSubjects.class);
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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v == sivEditSummary){
            //If the editing pencil in the view summary is clicked
            Intent intent = new Intent(ViewSummaryActivity.this,EditSummaryActivity.class);
            intent.putExtra("key",summaryKey);
            intent.putExtra("subject",summarySubject);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
    }

}