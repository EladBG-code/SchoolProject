package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

public class ViewSummariesOnSubject extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    TextView tvSubjectName;
    FloatingActionButton floatingUploadButton;
    Subject subject;
    RecyclerView dataList;
    List<Integer> images;
    List<String> titles;
    FirebaseDatabase database;
    DatabaseReference summariesRef;
    FirebaseRecyclerOptions<Summary> options;
    FirebaseRecyclerAdapter<Summary, MyViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
        initDrawer();
        floatingUploadButton.setOnClickListener(this);
        subject=  new Subject(getIntent().getStringExtra("SubjectSelected"));
        tvSubjectName.setText(getIntent().getStringExtra("SubjectSelected")); /*This line sets the name of the subject which was selected as the title of the subject's summary page*/

        titles=new ArrayList<>();
        images =new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        summariesRef = database.getReference(subject.getSubjectName());
        Log.d("summariesRef", "onCreate: "+summariesRef.toString());
        loadSummariesListFromDB();

    }

    public void loadSummariesListFromDB() {
    options =new FirebaseRecyclerOptions.Builder<Summary>().setQuery(summariesRef, Summary.class).build();
    adapter = new FirebaseRecyclerAdapter<Summary, MyViewHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull Summary model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvDescription.setText(model.getDescription());
        holder.tvAuthor.setText(model.getAuthor());
        holder.btnViewSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewSummariesOnSubject.this,ViewSummary.class);
                intent.putExtra("summaryKey",getRef(position).getKey());
                intent.putExtra("subject",subject.getSubjectName());
                startActivity(intent);
            }
        });
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewSummariesOnSubject.this,ViewSummary.class);
                intent.putExtra("summaryKey",getRef(position).getKey());
                intent.putExtra("subject",subject.getSubjectName());
                startActivity(intent);
            }
        });
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_grid_layout,parent,false);
            return new MyViewHolder(v);
        }
    };
    adapter.startListening();
    GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.VERTICAL, false);
    dataList.setLayoutManager(gridLayoutManager);
    dataList.setAdapter(adapter);

    }



    public void initComponents() {
        setContentView(R.layout.activity_view_summaries_on_subject);
        dataList = (RecyclerView)findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawer_layout_subject);
        navigationView = findViewById(R.id.nav_view_subject);
        toolbar = findViewById(R.id.toolbarSubjectSelected);
        floatingUploadButton = findViewById(R.id.floatingUploadButton);
        tvSubjectName = findViewById(R.id.tvSubjectName);
    }

    public void initDrawer(){
         setSupportActionBar(toolbar);
         navigationView.bringToFront();
         ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close){
             public void onDrawerClosed(View view) {
                 // Visible/Enable the FAB
             }

             public void onDrawerOpened(View drawerView) {
                 // Hide/Disable the FAB
             }

             @Override
             public void onDrawerSlide(View drawerView, float slideOffset) {
                 // invert the slideOffset value
                 //This line fades the floating action button as the user slides their finger across the screen
                 floatingUploadButton.setAlpha(1-slideOffset);
             }
         };
         drawerLayout.addDrawerListener(toggle);
         toggle.syncState();


         navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
         if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("התנתקות")){

            AlertDialog.Builder builder = new AlertDialog.Builder(ViewSummariesOnSubject.this);

            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(ViewSummariesOnSubject.this,MainActivity.class);
                            Toast.makeText(ViewSummariesOnSubject.this,"התנתקת בהצלחה.", Toast.LENGTH_SHORT).show();
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
            super.onBackPressed();
            return false;
        }
        if(item.getTitle().equals("הגדרות")){
            //Will be added in the future
            Toast.makeText(this,"מסך ההגדרות יהיה זמין לשימוש בעתיד.", Toast.LENGTH_LONG-5000).show();
            return false;
        }
        if(item.getTitle().equals("פרופיל")){
            //Will be added in the future
            Toast.makeText(this,"מסך הפרופיל יהיה זמין לשימוש בעתיד.", Toast.LENGTH_LONG-5000).show();
            return false;
        }
        if(item.getTitle().equals("מסך הבית")){
            Intent intent = new Intent(this,Homepage.class);
            startActivity(intent);
            return false;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v == floatingUploadButton){
            //Upload floating action button selected
            Intent intent = new Intent(this,AddSummaryActivity.class);
            intent.putExtra("Subject",subject.getSubjectName());
            startActivity(intent);
        }

    }


}