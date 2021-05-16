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

public class LoadingActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressBar progressBar;
    TextView loadingP;
    int pbStatus = 0;
    private Handler mHandler = new Handler();

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

        Thread thread = new Thread(new Runnable() { //This makes a thread that runs in the background regardless of the UI loading in here that does both actions
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
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                if (sharedPreferences.getBoolean("logged",false)){
                    doesUserExistInDB(sharedPreferences.getString("key",""));
                }
                else{
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }









            //            WORKING CODE BELOW

            /*
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
             });*/


            //            WORKING CODE ABOVE


    }

    /**
     * The following function logs the user in if they exist in the database
     */
    private void logThemIn(){
        if (sharedPreferences.getBoolean("logged",false)){
            //User was logged in before and had their details saved

            GlobalAcross.currentUserKey = sharedPreferences.getString("key",""); //Defines the Global Key of the logged in User

            database = FirebaseDatabase.getInstance();

            myRef = database.getReference("UserHashMap").child(GlobalAcross.currentUserKey);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<User> typeUser = new GenericTypeIndicator<User>() {};
                    currentUser = snapshot.getValue(typeUser);

                    progressBar.setProgress(100);
                    loadingP.setText(100 + "%");

                    Toast.makeText(LoadingActivity.this, "ברוכים השבים "+currentUser.getfName()+"!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoadingActivity.this, HomepageActivity.class);
                    startActivity(intent);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else{
            //User was not logged in before and needs to register / login

            Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * The following function checks if the user's saved key exists in the database (it can not exist in cases where I - the creator delete the user manually) and logs them in if they exist
     * @param sharedPreferenceKey
     */
    private void doesUserExistInDB(String sharedPreferenceKey) {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UserHashMap").child(sharedPreferenceKey);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        //User exists in database so log them in
                        logThemIn();
                    }
                    else{
                        //User doesn't exist in database so direct them to login

                        sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("index"); //Shared preferences - login keeper (key and value)
                        editor.remove("logged"); //Shared preferences - login keeper
                        editor.commit();

                        Toast.makeText(getApplicationContext(),"אאוץ'! נתקלנו בשגיאת מסד נתונים.",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    /**
     * The following function checks if the user has an available internet connection in order to login
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}