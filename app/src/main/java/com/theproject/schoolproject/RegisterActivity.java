package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{
    //XML Spinner items insertion below
    int classNum;
    Spinner spinnerClass;
    ArrayAdapter<String> adapter;
    String[] arrClasses = {"י'" ,"י"+'"'+"א", "י"+'"'+"ב"};
    //To here

    EditText etFName,etLName,etUsername,etEmail,etPassword;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView tvLoginBack,tvCharCountUsername,tvCharCountPassword,tvCharCountEmail;
    CardView cvRegisterBtn;
    ColorFilter cfTemp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etLName = findViewById(R.id.etLName);
        etFName = findViewById(R.id.etFName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmailReg);
        etPassword = findViewById(R.id.etPassword);

        cfTemp = etLName.getBackground().getColorFilter(); //Gets the color filter before it changes to red

        tvCharCountUsername = findViewById(R.id.tvCharCountUsername);
        tvCharCountEmail = findViewById(R.id.tvCharCountEmail);
        tvCharCountPassword = findViewById(R.id.tvCharCountPassword);

        setSpinner();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersPlace");

        // Read from the database
    }
    public void setSpinner() {
        //Sets the Spinner and its Items

        spinnerClass = findViewById(R.id.spinnerClass);
        tvLoginBack = findViewById(R.id.tvLoginBack);
        cvRegisterBtn = findViewById(R.id.cvRegisterBtn);

        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,arrClasses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerClass.setAdapter(adapter);

        tvLoginBack.setOnClickListener(this);
        cvRegisterBtn.setOnClickListener(this);
        spinnerClass.setOnItemSelectedListener(this);

        etTextChangeListeners(etUsername,tvCharCountUsername,14);
        etTextChangeListeners(etEmail,tvCharCountEmail,55);
        etTextChangeListeners(etPassword,tvCharCountPassword,30);


    }

    public void etTextChangeListeners(final EditText et , final TextView tvCharCountET, final int maxCount){
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if(et.getText().toString().charAt(et.getText().length()-1) == '\n')
                {
                    et.setText(et.getText().toString().substring(0,et.getText().length()-2));
                }*/
                count = et.getText().toString().length();
                if(count == 0) {
                    tvCharCountET.setVisibility(View.GONE);
                    et.getBackground().mutate().setColorFilter(cfTemp); //sets the color filter back to default of the edittext
                }
                    else{
                    tvCharCountET.setVisibility(View.VISIBLE);
                    tvCharCountET.setText(count+"/"+maxCount);

                    if(count - 1 == maxCount){
                        vibratePhone(200);
                    }
                    if(count > maxCount || (count < 4 && count > 0)) {
                        tvCharCountET.setTextColor(Color.RED);
                        et.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP); //sets the color filter to red of (the edittext because it's too many chars)

                    }
                    else{
                        et.getBackground().mutate().setColorFilter(cfTemp); //sets the color filter back to default of the edittext
                        tvCharCountET.setTextColor(Color.BLACK);
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                Toast.makeText(this, "חזרו ומלאו את הפרטים בשדה השם הפרטי", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!fName.matches("[א-ת]+")) {
                //This if checks if there are any chars in this string who are either numbers or non Hebrew chars (both illegal characters)

                //This section makes the phone vibrate and display a toast telling the user to recheck their details
                vibratePhone(200);
                Toast.makeText(this, "חזרו ובדקו את תקינות שדה השם הפרטי", Toast.LENGTH_SHORT).show();
                return false;
            }


            if (lName.isEmpty()) {
                //This section makes the phone vibrate and display a toast telling the user to recheck their details if the field is empty
                vibratePhone(200);
                Toast.makeText(this, "חזרו ומלאו את הפרטים בשדה שם המשפחה", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!lName.matches("[א-ת]+") || strContainArr(lName, illegalCharacters)) {
                //This if checks if there are any chars in this string who are either numbers or non Hebrew chars (both illegal characters)

                //This section makes the phone vibrate and display a toast telling the user to recheck their details
                vibratePhone(200);
                Toast.makeText(this, "חזורו ובדקו את תקינות שדה שם המשפחה", Toast.LENGTH_SHORT).show();
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
    public void onClick(View v) {
        if(v == cvRegisterBtn){
            if(validDetails(etFName.getText().toString(),etLName.getText().toString(),etUsername.getText().toString(),etEmail.getText().toString(),etPassword.getText().toString())){
                User newUser = new User(etFName.getText().toString(),etLName.getText().toString(),etUsername.getText().toString(),etEmail.getText().toString(),etPassword.getText().toString(),classNum);
                GlobalAcross.allUsers.add(newUser);
                //GlobalAcross.setUserIndex(GlobalAcross.allUsers.size());
                //Toast.makeText(this, GlobalAcross.getUserIndex(), Toast.LENGTH_SHORT).show();
                myRef.setValue(GlobalAcross.allUsers);
                GlobalAcross.currentUser = newUser;
                Toast.makeText(this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, HomepageActivity.class);
                startActivity(intent);
                finish();
            }
        }
        if(v == tvLoginBack){
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class),ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

}