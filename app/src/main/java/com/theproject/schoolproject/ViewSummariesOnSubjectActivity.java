package com.theproject.schoolproject;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.theproject.schoolproject.GlobalAcross.currentUser;
import static com.theproject.schoolproject.GlobalAcross.currentUserIndex;

public class ViewSummariesOnSubjectActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    TextView tvSubjectName;
    FloatingActionButton floatingUploadButton;
    Subject subject;
    RecyclerView dataList;
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
        tvSubjectName.setPaintFlags(tvSubjectName.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG); //Makes the subject name textview bold
        database = FirebaseDatabase.getInstance();
        summariesRef = database.getReference(subject.getSubjectName());
        loadSummariesListFromDB();
    }

    //this function updates the number of liked in DB
    public void updateLikesDB(String selectedKeySummary, long newLikes){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(subject.getSubjectName()).child(selectedKeySummary).child("amountOfLikes");
        myRef.setValue(newLikes);
    }

    //this function adds to the user the summaries that he liked, and add to the summary's users list the users that liked it
    public void updateListOfLikes(String selectedKeySummary, boolean add){
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UsersPlace/"+ currentUserIndex);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(subject.getSubjectName()).child(selectedKeySummary);
        //if add=1 - add the summary to the list of summaries that the user liked
        if(add){
            usersRef.child("favoriteSummaries").child(selectedKeySummary).setValue(subject.getSubjectName());
            myRef.child("usersThatLiked").child(String.valueOf(currentUserIndex)).setValue(currentUserIndex);
        }
        //if add=0 - remove the summary from the list of summaries that the user liked
        else{
            usersRef.child("favoriteSummaries").child(selectedKeySummary).removeValue();
            myRef.child("usersThatLiked").child(String.valueOf(currentUserIndex)).setValue("none");

        }

    }


    public void loadSummariesListFromDB() {
    options = new FirebaseRecyclerOptions.Builder<Summary>().setQuery(summariesRef, new SnapshotParser<Summary>() {
        @NonNull
        @Override
        public Summary parseSnapshot(@NonNull DataSnapshot snapshot) {
            //Todo: Add subject here somehow !
            String author="", title="", description="", subject1="" ;
            long amountOfLikes = 0;
            ArrayList<String> usersThatLiked = null;
            for (DataSnapshot child : snapshot.getChildren()) {
                if(child.getKey().equals("author")) {
                    author= (String) child.getValue();
                }
                else if(child.getKey().equals("title")) {
                    title= (String) child.getValue();
                }
                else if(child.getKey().equals("description")) {
                    description= (String) child.getValue();
                }
                else if(child.getKey().equals("amountOfLikes")){
                    amountOfLikes= (Long) child.getValue();
                }
                else if(child.getKey().equals("usersThatLiked")) {
                    if(!child.hasChildren()){
                        usersThatLiked=null;
                    }
                    //else if(child.getChildrenCount() != 1){
                    else
                        usersThatLiked = (ArrayList<String>) child.getValue();
                    //}
                }
            }
            Summary summary = new Summary(author, title, description,subject1,amountOfLikes,usersThatLiked);
            return summary;
        }
    }).build();
    adapter = new FirebaseRecyclerAdapter<Summary, MyViewHolder>(options) {

        @Override
        public void onDataChanged() {
            super.onDataChanged();
            notifyDataSetChanged();
        }

        public String amountOfLikesFunc(long amountOfLikes) {

            if (amountOfLikes > 5 && amountOfLikes < 10){
                return "5+";
            }
            if(amountOfLikes > 10 && amountOfLikes < 20){
                return "10+";
            }
            if(amountOfLikes > 20 && amountOfLikes < 40){
                return "20+";
            }
            if(amountOfLikes > 40 && amountOfLikes < 60){
                return "40+";
            }
            if(amountOfLikes > 60 && amountOfLikes < 80){
                return "60+";
            }
            if (amountOfLikes > 80 && amountOfLikes < 100){
                return "80+";
            }
            if(amountOfLikes > 100){
                return "100+";
            }
            return amountOfLikes+"";
        }

        @Override
        protected void onBindViewHolder(@NonNull final MyViewHolder holder, final int position, @NonNull final Summary model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvDescription.setText(model.getDescription());
        holder.tvAuthor.setText(model.getAuthor());

        holder.mtvLikesNum.setText(amountOfLikesFunc(model.getAmountOfLikes()));

        if(model.getUsersThatLiked()==null){
            holder.btnHeart.setChecked(false);
        }
       else {
            ArrayList<String> usersThatLikedTemp= model.getUsersThatLiked();
            if(usersThatLikedTemp.contains(Long.valueOf(currentUserIndex))) {
                holder.btnHeart.setChecked(true);
                holder.mtvLikesNum.setTextColor(Color.RED);
            }
            else{
                holder.btnHeart.setChecked(false);
                holder.mtvLikesNum.setTextColor(Color.BLACK);
            }
        }
        // this function is adding one like to the summary
        holder.btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedKeySummary = getRef(position).getKey();
                long newLikes=model.getAmountOfLikes();
                if(holder.btnHeart.isChecked()){
                   newLikes += 1;
                   updateLikesDB(selectedKeySummary, newLikes);
                   updateListOfLikes(selectedKeySummary, true);
                   Toast.makeText(ViewSummariesOnSubjectActivity.this, "הסיכום נשמר בסיכומים שלי", Toast.LENGTH_SHORT).show();
                   holder.btnHeart.setChecked(true);
                }
                if(!holder.btnHeart.isChecked()){
                    newLikes -= 1;
                    updateLikesDB(selectedKeySummary, newLikes);
                    updateListOfLikes(selectedKeySummary, false);
                    Toast.makeText(ViewSummariesOnSubjectActivity.this, "הסיכום הוסר מהסיכומים שלי", Toast.LENGTH_SHORT).show();
                    holder.btnHeart.setChecked(false);
                    holder.mtvLikesNum.setTextColor(Color.BLACK);

                }

            }

        });

        holder.btnViewSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewSummariesOnSubjectActivity.this,ViewSummaryActivity.class);
                intent.putExtra("summaryKey",getRef(position).getKey());
                intent.putExtra("subject",subject.getSubjectName());
                startActivity(intent);
            }
        });

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewSummariesOnSubjectActivity.this,ViewSummaryActivity.class);
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
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals("התנתקות")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewSummariesOnSubjectActivity.this);
            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            currentUser = null;
                            Intent intent = new Intent(ViewSummariesOnSubjectActivity.this, LoadingActivity.class);
                            Toast.makeText(ViewSummariesOnSubjectActivity.this,"התנתקת בהצלחה.", Toast.LENGTH_SHORT).show();
                            sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);
                            GlobalAcross.currentUser = null;
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
            Intent intent = new Intent(ViewSummariesOnSubjectActivity.this, SettingsUserActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("פרופיל")){
            Intent intent = new Intent(ViewSummariesOnSubjectActivity.this,ProfileActivity.class);
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
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ViewSummariesOnSubjectActivity.this);
            builder.setMessage(GlobalAcross.infoMessage)
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
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }

    }



}