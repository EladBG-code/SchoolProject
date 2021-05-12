package com.theproject.schoolproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseError;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.theproject.schoolproject.GlobalAcross.currentUser;
import static com.theproject.schoolproject.GlobalAcross.currentUserIndex;

public class LoadingActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressBar progressBar;
    TextView loadingP;
    int pbStatus = 0;
    private Handler mHandler = new Handler();

    Thread thread;
    SharedPreferences sharedPreferences;

    /**
     * Your usual on create function - except it's used for the loading activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        loadingP = findViewById(R.id.tvLoadingPercentage);
        //startService(new Intent(this,notificationService.class)); //5 like notification service starter

        progressBar = findViewById(R.id.pbLoading);
        // Read from the database
        sharedPreferences = getSharedPreferences("index", Context.MODE_PRIVATE);

        thread = new Thread(new Runnable() { //This makes a thread that runs in the background regardless of the UI loading in here that does both actions
            @Override
            public void run() {
                while (pbStatus < 100) {
                    pbStatus++;
                    loadingP.setText(pbStatus + "%");
                    //SystemClock.sleep(15);
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
        });
        thread.start();



        }


    /**
     * This function is called when the activity starts. The function checks if the user was logged in and if they were - it gets their values from
     * the realtime database and sets the appropriate GlobalAcross variables to them.
     * If they aren't - they are sent to log in (MainActivity).
     * */
        public void loadingFunc() {
            if (!isNetworkAvailable()){
                Toast.makeText(LoadingActivity.this, "אאוץ'! נתקלנו בבעיית אינטרנט.", Toast.LENGTH_SHORT).show();
                try {
                    thread.join();
                } catch (InterruptedException e) { }
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
            }

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("UsersPlace");
             myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                     // This method is called once with the initial value and again
                     // whenever data at this location is updated.

                     GenericTypeIndicator<ArrayList<User>> t = new GenericTypeIndicator<ArrayList<User>>() {
                     };
                     if (sharedPreferences.getBoolean("logged", false)) {
                         //Checks if the user was logged in the device and places the correct path reference for his saved index and pulls out the class out of the arraylist in the firebase database
                         currentUserIndex = sharedPreferences.getInt("index", 0);
                         currentUser = snapshot.getValue(t).get(currentUserIndex);
                         progressBar.setProgress(100);
                         loadingP.setText(100 + "%");
                         Intent intent = new Intent(LoadingActivity.this, HomepageActivity.class);
                         startActivity(intent);
                     } else {
                         //Was not logged in the current device

                         Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                         startActivity(intent);
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError error) {

                 }
             });

        /* myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<ArrayList<User>> t = new GenericTypeIndicator<ArrayList<User>>() {};
                if (sharedPreferences.getBoolean("logged", false)) {
                    //Checks if the user was logged in the device and places the correct path reference for his saved index and pulls out the class out of the arraylist in the firebase database
                    currentUser = dataSnapshot.getValue(t).get(sharedPreferences.getInt("index", 0));
                    Toast.makeText(LoadingActivity.this, "ברוכים השבים " + currentUser.getfName() + '.', Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoadingActivity.this, HomepageActivity.class);
                    startActivity(intent);
                }
                else {
                    //Was not logged in the current device
                    allUsers = new ArrayList<>();
                    if (dataSnapshot.getValue(t) != null) {
                        allUsers.addAll(dataSnapshot.getValue(t));
                    }
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        }); */
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}