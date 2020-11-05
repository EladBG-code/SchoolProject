package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.core.content.ContextCompat.getSystemService;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    //XML Spinner items insertion below
    int classNum;
    Spinner spinnerClass;
    ArrayAdapter<String> adapter;
    String[] arrClasses = {"י'" ,"י"+'"'+"א", "י"+'"'+"ב"};
    //To here

    Button btnRegister,btnLoginBack;
    EditText etFName,etLName,etUsername,etEmail,etPassword;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setSpinner();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersPlace");

        // Read from the database
    }
    public void setSpinner() {
        //Sets the Spinner and its Items

        spinnerClass = findViewById(R.id.spinnerClass);

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arrClasses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerClass.setAdapter(adapter);
        btnLoginBack = findViewById(R.id.btnLoginBack);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        btnLoginBack.setOnClickListener(this);
        spinnerClass.setOnItemSelectedListener(this);
        etFName = findViewById(R.id.etFName);
        etLName = findViewById(R.id.etLName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmailReg);
        etPassword = findViewById(R.id.etPassword);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //The integer classNum represents the class the user is in: for 10th grade it will be 1, for 11th- 2, for 12th- 3
        if (arrClasses[i].equals("י'")){
            classNum = 1;
        }
        if (arrClasses[i].equals("י"+'"'+"א")){
            classNum = 2;
        }
        if (arrClasses[i].equals("י"+'"'+"ב")){
            classNum = 3;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public boolean strContainArr(String str,char[] arr){
        //This function returns true if one of the chars inside the received String (str) contains any of the chars in the char array that was assigned (arr) - the function returns false if not
        for(int i=0;i<str.length();i++){
            for(int j=0;j<arr.length;j++){
                if(str.charAt(i) == arr[j]){
                    return true;
                }
            }
        }
        return false;
    }
    public boolean validDetails(String fName,String lName,String username,String email,String password){
        //This function validates all of the entered details

        char[] illegalCharacters = {'`', '~', ';', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '+', '=', '/', '[', ']', '"', ':', '<', '>','\\','?'};



        //This section validates the first and last names of the user (fName & lName)
        {
            //This section checks if both of these fields are empty (first check)
            if (fName.isEmpty()) {
                //This section makes the phone vibrate and display a toast telling the user to recheck their details if the field is empty
                vibratePhone(200);
                Toast.makeText(this, "חזור ומלא את הפרטים בשדה השם הפרטי", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!fName.matches("[א-ת]+")) {
                //This if checks if there are any chars in this string who are either numbers or non Hebrew chars (both illegal characters)

                //This section makes the phone vibrate and display a toast telling the user to recheck their details
                vibratePhone(200);
                Toast.makeText(this, "חזור ובדוק את תקינות שדה השם הפרטי", Toast.LENGTH_SHORT).show();
                return false;
            }


            if (lName.isEmpty()) {
                //This section makes the phone vibrate and display a toast telling the user to recheck their details if the field is empty
                vibratePhone(200);
                Toast.makeText(this, "חזור ומלא את הפרטים בשדה שם המשפחה", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!lName.matches("[א-ת]+") || strContainArr(lName, illegalCharacters)) {
                //This if checks if there are any chars in this string who are either numbers or non Hebrew chars (both illegal characters)

                //This section makes the phone vibrate and display a toast telling the user to recheck their details
                vibratePhone(200);
                Toast.makeText(this, "חזור ובדוק את תקינות שדה שם המשפחה", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        //This section validates the username
        {
            /*if(!username.matches(".*[א-ת]+.*") || username.matches(".*[a-z]+.*") || username.matches(".*[A-Z]+.*")){

                //This section makes the phone vibrate and display a toast telling the user to recheck their details
                vibratePhone(200);
                Toast.makeText(this, "שם המשתמש יכול להכיל רק אותיות (באנגלית) ומספרים", Toast.LENGTH_SHORT).show();
                return false;
            }*/
        }







        //If all of the received parameters passed the validation test, the function will return true
        return true;
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

    @Override
    public void onClick(View view) {
        if(view == btnRegister){
            if(validDetails(etFName.getText().toString(),etLName.getText().toString(),etUsername.getText().toString(),etEmail.getText().toString(),etPassword.getText().toString())){
                //If the user entered all of the info truthfully, the user will pass through this 'if'
                // Write a message to the database
                User newUser = new User(etFName.getText().toString(),etLName.getText().toString(),etUsername.getText().toString(),etEmail.getText().toString(),etPassword.getText().toString(),classNum);
                GlobalAcross.allUsers.add(newUser);
                myRef.setValue(GlobalAcross.allUsers);
                GlobalAcross.currentUser = newUser;
                Toast.makeText(this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Homepage.class);
                startActivity(intent);
                finish();
            }
        }
        if(view == btnLoginBack){
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}