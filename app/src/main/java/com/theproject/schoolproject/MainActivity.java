package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etPassword,etUsername;
    TextView tvRegister,tvLoginCV;
    FirebaseDatabase database;
    CardView cvLoginBtn;
    DatabaseReference myRef;
    ProgressBar pbLogin;

    int indexOX;
    String keyOX;

    SharedPreferences sharedPreferences;

    public static final String Index = "index";
    public static final String Logged = "logged";

    /**
     * Usual onCreate function
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indexOX = -1;

        etPassword = findViewById(R.id.etPasswordL);
        etUsername = findViewById(R.id.etUsernameL);
        /*btnAutoFill = findViewById(R.id.btnAutoFill); */ //Remove this line once you're done with testing
        tvRegister = findViewById(R.id.tvRegister);
        tvLoginCV = findViewById(R.id.tvLoginCV);
        cvLoginBtn = findViewById(R.id.cvLoginBtn);
        pbLogin = findViewById(R.id.pbLogin);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserHashMap");
        
        cvLoginBtn.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

        /*btnAutoFill.setOnClickListener(this); */ //Remove this line once you're done with testing

        sharedPreferences = getSharedPreferences(Index,Context.MODE_PRIVATE);




        }


    /**
     * This function sends the user to register if they press the register prompt (textview)
     * Otherwise, is looks their details up in the realtime database if they press the login button after validation of details
     * and sets the appropriate variables in GlobalAcross as well.
     * @param v
     */
    @Override
    public void onClick(View v) {
        /*if(v == btnAutoFill){etPassword.setText("e123456"); etUsername.setText("eladbg");} */ //Remove this line once you're done with testing
        if (v == tvRegister) {
            startActivity(new Intent(this, RegisterActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
        if(v == cvLoginBtn){

            pbLogin.setVisibility(View.VISIBLE);
            tvLoginCV.setVisibility(View.INVISIBLE);
            if (etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                vibratePhone(200);
                pbLogin.setVisibility(View.INVISIBLE);
                tvLoginCV.setVisibility(View.VISIBLE);
                Toast.makeText(this, "אנא בדקו אם מלאתם את שדה הסיסמה ושם המשתמש.", Toast.LENGTH_SHORT).show();
            }
            else{


                {               //Temp testing

//                    findIndexOfUserV2(etUsername.getText().toString());

                    //Fixes memory problems and entirely cloud based as well as it works in sync instead of async now
                    loginFuncV2();

//                    int indexUser = findIndexOfUserV2();



                }               //Temp testing






 /*                            WORKING CODE BELOW                                        */

                /*
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
                */

                /*                            WORKING CODE ABOVE                                        */


            }
        }
            }






    public void setCurrentUser(){
        DatabaseReference userHashMapRef = FirebaseDatabase.getInstance().getReference("UserHashMap").child(keyOX);
        userHashMapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<User> userType = new GenericTypeIndicator<User>() {};
                GlobalAcross.currentUser = snapshot.getValue(userType);

                Toast.makeText(MainActivity.this, "התחברת בהצלחה " + GlobalAcross.currentUser.getfName()+ '!', Toast.LENGTH_SHORT).show();
                sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);

                //GlobalAcross.currentUserIndex = indexOX;
                GlobalAcross.currentUserKey = keyOX;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                //editor.putInt(Index,indexOX);
                editor.putString("key",keyOX);
                editor.putBoolean("logged",true);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    public void isPasswordCorrect(){
//        DatabaseReference usersPlaceRef = FirebaseDatabase.getInstance().getReference("UsersPlace").child(indexOX+"").child("password");
//        usersPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String realPass = snapshot.getValue().toString();
//                if (etPassword.getText().toString().equals(realPass)){
//                    //Password and username are correct by this point!
//
//
//                    setCurrentUser(indexOX);
//
//
//
//                }
//                else{
//                    //Password is incorrect!
//                    vibratePhone(200);
//                    tvLoginCV.setVisibility(View.VISIBLE);
//                    pbLogin.setVisibility(View.INVISIBLE);
//                    Toast.makeText(MainActivity.this, "יש לבדוק את הסיסמה ולנסות שוב.", Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    public void loginFuncV2(){

        DatabaseReference usersPlaceRef = FirebaseDatabase.getInstance().getReference("UserHashMap");

        Query userHashMapRef = FirebaseDatabase.getInstance().getReference("UserHashMap").orderByChild("username").equalTo(etUsername.getText().toString()).limitToFirst(1);
        userHashMapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Username exists in database and is therefore correct

                    if (snapshot.getChildren().iterator().next().child("password").getValue().toString().equals(etPassword.getText().toString())){
                        //Username and password are correct
                        keyOX = snapshot.getChildren().iterator().next().getKey();
                        setCurrentUser();

                        //Comment to show the key of the user
                        //Toast.makeText(MainActivity.this, keyOX, Toast.LENGTH_SHORT).show();

                    }
                    else{
                        //Username is correct but password isn't

                        vibratePhone(200);
                        tvLoginCV.setVisibility(View.VISIBLE);
                        pbLogin.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "יש לבדוק את הסיסמה ולנסות שוב.", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    //Username doesn't exist!
                    vibratePhone(200);
                    Toast.makeText(MainActivity.this, "יש לבדוק את פרטי ההתחברות ולנסות שוב.", Toast.LENGTH_SHORT).show();
                    tvLoginCV.setVisibility(View.VISIBLE);
                    pbLogin.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });














//        final Query queryUsername = usersPlaceRef
//                .orderByChild("username")
//                .equalTo(etUsername.getText().toString());
//
//
//        queryUsername.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists() && !snapshot.getKey().equals(null)) {
//
//                    keyOX = snapshot.getKey();
//
//                    String realPass = snapshot.child("password").getValue().toString();
//
//                    Toast.makeText(MainActivity.this, realPass, Toast.LENGTH_SHORT).show();
//
//                    if (realPass.equals(etPassword.getText().toString())) {
//                        //If the inserted password is correct
//                        setCurrentUser();
//                    } else {
//                        //Username is correct but password isn't
//
//                        vibratePhone(200);
//                        tvLoginCV.setVisibility(View.VISIBLE);
//                        pbLogin.setVisibility(View.INVISIBLE);
//                        Toast.makeText(MainActivity.this, "יש לבדוק את הסיסמה ולנסות שוב.", Toast.LENGTH_SHORT).show();
//                    }
//
//                    //Toast.makeText(MainActivity.this,indexOX+" is your index",Toast.LENGTH_LONG).show();
//                }
//                else {
//                    //Username doesn't exist!
//                    vibratePhone(200);
//                    Toast.makeText(MainActivity.this, "יש לבדוק את פרטי ההתחברות ולנסות שוב.", Toast.LENGTH_SHORT).show();
//                    tvLoginCV.setVisibility(View.VISIBLE);
//                    pbLogin.setVisibility(View.INVISIBLE);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



    }


    /**
     * Usual onBackPressed function
     * */
    @Override
    public void onBackPressed() {
        if(super.getClass() == RegisterActivity.class){
            startActivity(new Intent(this, RegisterActivity.class), ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
        else{
            this.finishAffinity();
        }
    }

    /**
     * If the validation does not come to be - the user's phone is greeted with a quick vibration
     * @param milisecondsToVibrate
     */
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