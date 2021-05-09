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
        myRef = database.getReference("UsersPlace");
        
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
                    findIndexOfUserV2();

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






    public void setCurrentUser(final int index){
        DatabaseReference usersPlaceRef = FirebaseDatabase.getInstance().getReference("UsersPlace").child(index+"");
        usersPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<User> typeIndicator = new GenericTypeIndicator<User>() {};
                GlobalAcross.currentUser = snapshot.getValue(typeIndicator);

                Toast.makeText(MainActivity.this, "התחברת בהצלחה " + GlobalAcross.currentUser.getfName()+ '!', Toast.LENGTH_SHORT).show();
                sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);

                GlobalAcross.currentUserIndex = indexOX;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Index,indexOX);
                editor.putBoolean(Logged,true);
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

    public void isPasswordCorrect(){
        DatabaseReference usersPlaceRef = FirebaseDatabase.getInstance().getReference("UsersPlace").child(indexOX+"").child("password");
        usersPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String realPass = snapshot.getValue().toString();
                if (etPassword.getText().toString().equals(realPass)){
                    //Password and username are correct by this point!


                    setCurrentUser(indexOX);



                }
                else{
                    //Password is incorrect!
                    vibratePhone(200);
                    tvLoginCV.setVisibility(View.VISIBLE);
                    pbLogin.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "יש לבדוק את הסיסמה ולנסות שוב.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void findIndexOfUserV2(){
        DatabaseReference usersPlaceRef = FirebaseDatabase.getInstance().getReference("UsersPlace");

        final Query queryUsername = usersPlaceRef
                .orderByChild("username")
                .equalTo(etUsername.getText().toString());

        queryUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Username exists!
                    indexOX = Integer.valueOf(snapshot.getChildren().iterator().next().getKey());

                    //Toast.makeText(MainActivity.this,indexOX+" is your index",Toast.LENGTH_LONG).show();

                    if (indexOX != -1){
                        //Username exists!

                            //Username and Password are correct and the user exists!

                            //GlobalAcross.currentUser = getUserFromIndex(String.valueOf(indexUser));

                            //Set the current user as the proper one

                            isPasswordCorrect();





                    }
                    else{
                        //Username doesn't exist!
                        vibratePhone(200);
                        Toast.makeText(MainActivity.this, "יש לבדוק את פרטי ההתחברות ולנסות שוב.", Toast.LENGTH_SHORT).show();
                        tvLoginCV.setVisibility(View.VISIBLE);
                        pbLogin.setVisibility(View.INVISIBLE);
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