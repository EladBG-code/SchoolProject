package com.theproject.schoolproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.text.Layout;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class GlobalAcross {
    //public static ArrayList<User> allUsers; //The ArrayList of all users (used for login - needs to be replaced)
    public static User currentUser; //Current logged in user
//    public static int currentUserIndex; //Needs to be replaced by currentUserKey
    public static boolean firstLoginSuggestion = false;
    public static String infoMessage;
    public static int editingTemp;
    public static int selectedSubjectVectorID;
    public static String logoutMessage = "האם אתם בטוחים שאתם רוצים להתנתק?";

    public static String currentUserKey;

    //Useful global functions across the application : for the current user
    /**
     * This function gets the user's current grade in its number and returns the string nickname of it in hebrew (10th grade = 1, 11th grade = 2, 12th grade = 3)
     * @return
     */
    public static String getCurrentUserGradeString(){
        int x = currentUser.getGrade();
        if(x == 1){
            return "י'";
        }
        if(x == 2){
            return "י"+'"'+"א";
        }
        return "י"+'"'+"ב";
    }



    /**
     * This function sets the gradient backgrounds to several activities such as the profile activity etc.
     *      The function gets the drawerLayout of the activity and the EnterFadeDuration as well
     *      as the ExitFadeDuration in milliseconds and activaites it.
     * @param layout
     * @param millisecondsEnterFadeDuration
     * @param millisecondsExitFadeDuration
     */
    public static void activateGradientBackground(DrawerLayout layout,int millisecondsEnterFadeDuration, int millisecondsExitFadeDuration){
        //Global function for animating the gradient background of the app which some activities use
        AnimationDrawable animationDrawable = (AnimationDrawable)layout.getBackground();
        animationDrawable.setEnterFadeDuration(millisecondsEnterFadeDuration);
        animationDrawable.setExitFadeDuration(millisecondsExitFadeDuration);
        animationDrawable.start();
    }

    /**
     * This function returns the string subject array of all of the subjects of high school which are used in the app
     * @return
     */
    public static String[] getAllSubjectsArr(){
        return new String[]{"מתמטיקה","היסטוריה","לשון","אזרחות","תנ"+'"'+"ך","ספרות","אנגלית","ביולוגיה","מדעי המחשב","כימיה","פיזיקה","תולדות האומנות","תקשורת","מדעי החברה"};
    }

    /**
     * Identical to what the function above of this one does but instead - it returns an ArrayList of strings instead of normal string Array
     * @return
     */
    public static ArrayList<String> getAllSubjectsArrayList(){
        String[] subjectsArr = getAllSubjectsArr();
        ArrayList<String> subjectsArrayList = new ArrayList<>();
        for(int i=0;i<getAllSubjectsArr().length;i++){
            subjectsArrayList.add(subjectsArr[i]);
        }
        return subjectsArrayList;
    }

    /**
     * This function validates the details of the user when they upload a summary (checks title & description)
     * @param title
     * @param description
     * @param context
     * @return
     */
    public static boolean checkValid(EditText title, EditText description, Context context){
        //This function checks if the entered parameters in the description and title of the summary withhold and are valid
        if(title.getText().toString().length() < 5){
            Toast.makeText(context, "אנא וודא\\י שיש לפחות 5 תווים בכותרת הסיכום", Toast.LENGTH_LONG).show();
            return false;
        }
        if(title.getText().toString().length() > 15) {
            Toast.makeText(context, "אנא וודא\\י שיש עד 15 תווים בכותרת הסיכום", Toast.LENGTH_LONG).show();
            return false;
        }
        if(description.getText().toString().length() > 150){
            Toast.makeText(context, "אנא וודא\\י שיש עד 150 תווים בתיאור", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * This function re-updates all of the user's values from the realtime database and sets them to the local variables
     * */
    public static void updateCurrentUserData(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UserHashMap").child(currentUserKey);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser.setEmail(snapshot.child("email").getValue().toString());
                currentUser.setfName(snapshot.child("fName").getValue().toString());
                currentUser.setlName(snapshot.child("lName").getValue().toString());
                currentUser.setGrade(Integer.valueOf(snapshot.child("grade").getValue().toString()));
                currentUser.setPassword(snapshot.child("password").getValue().toString());
                currentUser.setPfpPath(snapshot.child("pfpPath").getValue().toString());
                currentUser.setUsername(snapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
