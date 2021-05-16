package com.theproject.schoolproject;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.theproject.schoolproject.GlobalAcross.currentUser;

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
    ProgressBar pbLoadingSummaries;
    SharedPreferences sharedPreferences;
    LinearLayout llNoSummaries;
    ImageView ivSubjectVector;
    MediaPlayer mp;

    /**
     * Usual onCreate function
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summaries_on_subject);
        initComponents();
        initDrawer();


        pbLoadingSummaries = findViewById(R.id.pbLoadingSummaries);
        mp = MediaPlayer.create(this, R.raw.add_summary_sound);

            subject = new Subject(getIntent().getStringExtra("SubjectSelected"));
            tvSubjectName.setText(getIntent().getStringExtra("SubjectSelected")); /*This line sets the name of the subject which was selected as the title of the subject's summary page*/


            tvSubjectName.setPaintFlags(tvSubjectName.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG); //Makes the subject name textview bold

            Drawable drawableVectorIcon = ContextCompat.getDrawable(getApplicationContext(),GlobalAcross.selectedSubjectVectorID);

            ivSubjectVector.setImageDrawable(drawableVectorIcon);

            database = FirebaseDatabase.getInstance();
            summariesRef = database.getReference(subject.getSubjectName());
            loadSummariesListFromDB();



    }


    /**
     * This function updates the number of liked in DB
     * @param selectedKeySummary
     * @param newLikes
     */
    public void updateLikesDB(String selectedKeySummary, long newLikes){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(subject.getSubjectName()).child(selectedKeySummary).child("amountOfLikes");
        myRef.setValue(newLikes);
    }

    /**
     * This function adds to the user the summaries that he liked, and add to the summary's users list the users that liked it
     * @param selectedKeySummary
     * @param add
     */
    public void updateListOfLikes(String selectedKeySummary, boolean add){
        //DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("UsersPlace/"+ currentUserIndex);

        DatabaseReference userHashMapRef = FirebaseDatabase.getInstance().getReference("UserHashMap").child(GlobalAcross.currentUserKey);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(subject.getSubjectName()).child(selectedKeySummary);
        //if add=1 - add the summary to the list of summaries that the user liked
        if(add){

            userHashMapRef.child("favoriteSummaries").child(selectedKeySummary).setValue(subject.getSubjectName());

            myRef.child("usersThatLiked").child(GlobalAcross.currentUserKey).setValue(GlobalAcross.currentUserKey);

        }
        //if add=0 - remove the summary from the list of summaries that the user liked
        else{
            userHashMapRef.child("favoriteSummaries").child(selectedKeySummary).removeValue();

            myRef.child("usersThatLiked").child(GlobalAcross.currentUserKey).removeValue();



        }

    }

    /**
     * This function loads all of the summaries of this current subject from the realtime database withusing a query - uses FirebaseUI (updates LIVE)
     * */
    public void loadSummariesListFromDB() {
    options = new FirebaseRecyclerOptions.Builder<Summary>().setQuery(summariesRef, new SnapshotParser<Summary>() {
        @NonNull
        @Override
        public Summary parseSnapshot(@NonNull DataSnapshot snapshot) {
            //Todo: Add subject here somehow !
            String author="", title="", description="", subject1="" ;
            long amountOfLikes = 0;
            HashMap<String,String> usersThatLikedHash = null;
            for (DataSnapshot child : snapshot.getChildren()) {
                if (child.getKey().equals("usersThatLiked")){
                    usersThatLikedHash = (HashMap<String,String>) child.getValue();
                }
                else if(child.getKey().equals("author")) {
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

            }
            Summary summary = new Summary(author, title, description,subject1,amountOfLikes,usersThatLikedHash);
            return summary;
        }
    }).build();
    adapter = new FirebaseRecyclerAdapter<Summary, MyViewHolder>(options) {

        @Override
        public void onDataChanged() {
            if (adapter.getItemCount() == 0){
                llNoSummaries.setVisibility(View.VISIBLE);
                pbLoadingSummaries.setVisibility(View.INVISIBLE);
            }
            else{
                llNoSummaries.setVisibility(View.GONE);
            }

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
            pbLoadingSummaries.setVisibility(View.INVISIBLE);
        holder.tvTitle.setText(model.getTitle());
        if(model.getDescription().length() > 15){
            holder.tvDescription.setText(model.getDescription().substring(0,15)+"...");
        }
        else{
            holder.tvDescription.setText(model.getDescription());
        }
        holder.tvAuthor.setText(model.getAuthor());

        holder.mtvLikesNum.setText(amountOfLikesFunc(model.getAmountOfLikes()));



        if (model.getUsersThatLikedHash() == null){
            holder.btnHeart.setChecked(false);
        }
        else{
            if (model.getUsersThatLikedHash().containsKey(GlobalAcross.currentUserKey)){
                holder.btnHeart.setChecked(true);
                holder.mtvLikesNum.setTextColor(Color.RED);
            }
            else{
                holder.btnHeart.setChecked(false);
                holder.mtvLikesNum.setTextColor(Color.BLACK);
            }
        }


     /*   if(model.getUsersThatLiked()==null){       //temporarily disabled
            holder.btnHeart.setChecked(false);
        }
       else {
            ArrayList<String> usersThatLikedTemp = model.getUsersThatLiked();
            if(usersThatLikedTemp.contains(Long.valueOf(currentUserIndex))) {
                holder.btnHeart.setChecked(true);
                holder.mtvLikesNum.setTextColor(Color.RED);
            }
            else{
                holder.btnHeart.setChecked(false);
                holder.mtvLikesNum.setTextColor(Color.BLACK);
            }
        }   */


        // this function is adding one like to the summary
        holder.btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedKeySummary = getRef(position).getKey();
                long newLikes = model.getAmountOfLikes();
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
        holder.cvEntireSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    /**
     * Sets all of the components to their ID's in the XML
     */
    public void initComponents() {
        dataList = (RecyclerView)findViewById(R.id.recyclerView);
        drawerLayout = findViewById(R.id.drawer_layout_subject);
        navigationView = findViewById(R.id.nav_view_subject);
        toolbar = findViewById(R.id.toolbarSubjectSelected);
        floatingUploadButton = findViewById(R.id.floatingUploadButton);
        tvSubjectName = findViewById(R.id.tvSubjectName);
        llNoSummaries = findViewById(R.id.llNoSummaries);
        ivSubjectVector = findViewById(R.id.ivSubjectVector);
    }

    /**
     * Sets the drawer and toolbar to be activated (& listeners etc)
     * */
    public void initDrawer(){
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        floatingUploadButton.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Closes the drawer if open and finishes otherwise
     * */
    @Override
    public void onBackPressed() {
         if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            finish();
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
     * Repeated function that operates the side drawer (inherits navigationView) that navigates to the proper activities in the app and shows 2 dialogs (one for feedback and one for logging out)
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals("התנתקות")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ViewSummariesOnSubjectActivity.this);
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

    /**
     * This function sends the user to the AddSummary activity
     * @param v
     */
    @Override
    public void onClick(View v) {
        if(v == floatingUploadButton){
            //Upload floating action button selected
            floatingUploadButton.animate().rotationBy(360).setDuration(550).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    startAddSound();
                    floatingUploadButton.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    floatingUploadButton.setClickable(true);
                    Intent intent = new Intent(ViewSummariesOnSubjectActivity.this,AddSummaryActivity.class);
                    intent.putExtra("Subject",subject.getSubjectName());
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ViewSummariesOnSubjectActivity.this).toBundle());
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

    public void startAddSound(){
        mp.start();
    }



}