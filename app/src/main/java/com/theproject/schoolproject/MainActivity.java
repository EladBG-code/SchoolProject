package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
    Button btnLogin,btnTest;
    TextView tvRegister;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPassword = findViewById(R.id.etPasswordL);
        etUsername = findViewById(R.id.etUsernameL);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersPlace");

        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                GenericTypeIndicator<ArrayList<User>> t = new GenericTypeIndicator<ArrayList<User>>() {
                };
                GlobalAcross.allUsers = new ArrayList<>();
                if(dataSnapshot.getValue(t) != null){
                GlobalAcross.allUsers.addAll(dataSnapshot.getValue(t));
                }
            }
                @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        }

    @Override
    public void onClick(View v) {
        if(v == tvRegister){
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        if(v == btnLogin){
            int index = findIndexOfUser(etUsername.getText().toString());
            if(index != -1){
                if(GlobalAcross.allUsers.get(index).getPassword().equals(etPassword.getText().toString())){
                    GlobalAcross.currentUser = GlobalAcross.allUsers.get(index);
                    //Create an intent for the homepage and redirect the user to there
                    GlobalAcross.currentUser = GlobalAcross.allUsers.get(index);
                    Toast.makeText(this, "התחברת בהצלחה "+GlobalAcross.currentUser.getfName()+'!', Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this,Homepage.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //Incorrect password
                    vibratePhone(200);
                    Toast.makeText(this, "הסיסמה שגויה. תקן ונסה שוב.", Toast.LENGTH_SHORT).show();
                }
                //Login successful
            }
            else{
                //User doesn't exist
                vibratePhone(200);
                Toast.makeText(this, "המשתמש אינו קיים במערכת כלל.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static int findIndexOfUser(String username)
    {
        int i=0;
        for (User user:GlobalAcross.allUsers)
        {
            if (user.getUsername().equals(username))
            {
                return i;
            }
            i++;
        }
        return -1;
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