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
    Button btnLogin;
    /*Button btnAutoFill; */ //Remove this line once you're done with testing
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
        /*btnAutoFill = findViewById(R.id.btnAutoFill); */ //Remove this line once you're done with testing
        tvRegister = findViewById(R.id.tvRegister);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersPlace");

        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        /*btnAutoFill.setOnClickListener(this); */ //Remove this line once you're done with testing

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
        /*if(v == btnAutoFill){etPassword.setText("e123456"); etUsername.setText("eladbg");} */ //Remove this line once you're done with testing
        if (v == tvRegister) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
        if (v == btnLogin) {
            if (etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                vibratePhone(200);
                Toast.makeText(this, "אנא בדקו אם מלאתם את שדה הסיסמה ושם המשתמש.", Toast.LENGTH_SHORT).show();
            }
            else{
            int index = findIndexOfUser(etUsername.getText().toString());
            if (index != -1) {
                if (GlobalAcross.allUsers.get(index).getPassword().equals(etPassword.getText().toString())) {
                    GlobalAcross.currentUser = GlobalAcross.allUsers.get(index);
                    //Create an intent for the homepage and redirect the user to there
                    GlobalAcross.currentUser = GlobalAcross.allUsers.get(index);
                    Toast.makeText(this, "התחברת בהצלחה " + GlobalAcross.currentUser.getfName() + '!', Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, Homepage.class);
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

    @Override
    public void onBackPressed() {
        if(super.getClass() == RegisterActivity.class){
            super.onBackPressed();
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