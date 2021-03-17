package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etPassword,etUsername;
    TextView tvRegister;
    FirebaseDatabase database;
    CardView cvLoginBtn;
    DatabaseReference myRef;

    SharedPreferences sharedPreferences;

    public static final String Index = "index";
    public static final String Logged = "logged";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPassword = findViewById(R.id.etPasswordL);
        etUsername = findViewById(R.id.etUsernameL);
        /*btnAutoFill = findViewById(R.id.btnAutoFill); */ //Remove this line once you're done with testing
        tvRegister = findViewById(R.id.tvRegister);
        cvLoginBtn = findViewById(R.id.cvLoginBtn);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersPlace");

        cvLoginBtn.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        /*btnAutoFill.setOnClickListener(this); */ //Remove this line once you're done with testing



        sharedPreferences = getSharedPreferences(Index,Context.MODE_PRIVATE);


        }

    @Override
    public void onClick(View v) {
        /*if(v == btnAutoFill){etPassword.setText("e123456"); etUsername.setText("eladbg");} */ //Remove this line once you're done with testing
        if (v == tvRegister) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
        if(v == cvLoginBtn){
            if (etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                vibratePhone(200);
                Toast.makeText(this, "אנא בדקו אם מלאתם את שדה הסיסמה ושם המשתמש.", Toast.LENGTH_SHORT).show();
            }
            else{
                //loginV2(etUsername.getText().toString(),etPassword.getText().toString());   ----> W.I.P - Very important function!

                int index = findIndexOfUser(etUsername.getText().toString());
                if (index != -1) {
                    if (GlobalAcross.allUsers.get(index).getPassword().equals(etPassword.getText().toString())) {
                        GlobalAcross.currentUser = GlobalAcross.allUsers.get(index);
                        //Create an intent for the homepage and redirect the user to there - SUCCESSFUL LOGIN
                        Toast.makeText(this, "התחברת בהצלחה " + GlobalAcross.currentUser.getfName() + '!', Toast.LENGTH_SHORT).show();
                        sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Index,index);
                        editor.putBoolean(Logged,true);
                        editor.commit();

                        Intent intent = new Intent(this, HomepageActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //Incorrect password
                        vibratePhone(200);
                        Toast.makeText(this, "הסיסמה שגויה. תקן ונסה שוב.", Toast.LENGTH_SHORT).show();
                    }
                    //Login successful
                } else {
                    //User doesn't exist
                    vibratePhone(200);
                    Toast.makeText(this, "המשתמש אינו קיים במערכת כלל.", Toast.LENGTH_SHORT).show();
                }
            }
        }
            }

    public static int findIndexOfUser(String username)
    {
        int i = 0;
        for (User user:GlobalAcross.allUsers)
        {
            if (user.getUsername().equals(username))
            {
                GlobalAcross.currentUserIndex = i;
                return i;
            }
            i++;
        }
        return -1;
    }

    public void loginV2(String username,String password){

        //FUNCTION IS A W.I.P | Status - Unfinished

        database = FirebaseDatabase.getInstance();
        DatabaseReference dbUsername = database.getReference("UsersPlace");
        DatabaseReference dbPassword;
        final String[] userPass = new String[2];
        final long[] limit = new long[1];
        dbUsername.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Get the amount of users on the app
                limit[0] = snapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Failure case
            }
        });

        for(int i=0;i<limit[0];i++){
            dbUsername = database.getReference("UsersPlace/"+i+"/username");
            dbPassword = database.getReference("UsersPlace/"+i+"/password");

            dbUsername.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userPass[0] = snapshot.getValue(new GenericTypeIndicator<String>(){});
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            dbPassword.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userPass[1] = snapshot.getValue(new GenericTypeIndicator<String>(){});
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if(userPass[0].equals(username) && userPass[1].equals(password)){
                dbUsername = database.getReference("UsersPlace/"+i);
                dbUsername.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue(new GenericTypeIndicator<User>() {}) != null) {
                            GlobalAcross.currentUser = snapshot.getValue(new GenericTypeIndicator<User>() {});
                            Toast.makeText(MainActivity.this, "sucess!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(super.getClass() == RegisterActivity.class){
            super.onBackPressed();
        }
        else{
            this.finishAffinity();
        }
    }

    public void vibratePhone(int milisecondsToVibrate){
        //This function makes the phone vibrate for [secondsToVibrate] seconds.
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(milisecondsToVibrate, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else {
            v.vibrate(milisecondsToVibrate);
        }
    }
}