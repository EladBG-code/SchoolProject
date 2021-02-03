package com.theproject.schoolproject;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

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
    List<String> titles;
    FirebaseDatabase database;
    DatabaseReference summariesRef;
    FirebaseRecyclerOptions<Summary> options;
    FirebaseRecyclerAdapter<Summary, MyViewHolder> adapter;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summaries_on_subject);
        initComponents();
        initDrawer();
        floatingUploadButton.setOnClickListener(this);
        subject = new Subject(getIntent().getStringExtra("SubjectSelected"));
        tvSubjectName.setText(getIntent().getStringExtra("SubjectSelected")); /*This line sets the name of the subject which was selected as the title of the subject's summary page*/
        titles = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        summariesRef = database.getReference(subject.getSubjectName());
        loadSummariesListFromDB();
    }

    public void loadSummariesListFromDB() {
    options = new FirebaseRecyclerOptions.Builder<Summary>().setQuery(summariesRef, Summary.class).build();
    adapter = new FirebaseRecyclerAdapter<Summary, MyViewHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull final MyViewHolder holder, final int position, @NonNull final Summary model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvDescription.setText(model.getDescription());
        holder.tvAuthor.setText(model.getAuthor());
//            icon.setOnCheckedChangeListener { checkBox, isChecked ->
//                // Respond to icon toggle
//            }

        // this function is adding one like to the summary

        holder.btnHeart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String selectedKeySummary = getRef(position).getKey();
            int newLikes;
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(subject.getSubjectName()).child(selectedKeySummary).child("amountOfLikes");
            if(!compoundButton.isChecked()){
                newLikes= model.getAmountOfLikes()+1;
                myRef.setValue(newLikes);
                b=true;
            }
            else{
                newLikes= model.getAmountOfLikes()-1;
                myRef.setValue(newLikes);
                b=false;
            }

            }
        });


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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
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
            super.onBackPressed();
            drawerLayout.closeDrawers();
            return false;
        }
        if(item.getTitle().equals("הגדרות")){
            Intent intent = new Intent(ViewSummariesOnSubject.this, SettingsUser.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("פרופיל")){
            Intent intent = new Intent(ViewSummariesOnSubject.this,ProfileActivity.class);
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
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ViewSummariesOnSubject.this);
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
    public void onClick(View v) {
        if(v == floatingUploadButton){
            //Upload floating action button selected
            Intent intent = new Intent(this,AddSummaryActivity.class);
            intent.putExtra("Subject",subject.getSubjectName());
            startActivity(intent);
        }

    }


}