package com.theproject.schoolproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressBar progressBar;
    int pbStatus = 0;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersPlace");
        progressBar = findViewById(R.id.pbLoading);
        // Read from the database

        new Thread(new Runnable() { //This makes a thread that runs in the background regardless of the UI loading in here that does both actions
            @Override
            public void run() {
                while(pbStatus < 100){
                    pbStatus++;
                    android.os.SystemClock.sleep(15);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(pbStatus);
                        }
                    });
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingFunc();
                    }
                });
            }
        }).start();

        //loadingFunc();
    }

    public void loadingFunc(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<ArrayList<User>> t = new GenericTypeIndicator<ArrayList<User>>(){};
                GlobalAcross.allUsers = new ArrayList<>();
                if(dataSnapshot.getValue(t) != null){
                    GlobalAcross.allUsers.addAll(dataSnapshot.getValue(t));
                }
                Intent intent = new Intent(LoadingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }
}