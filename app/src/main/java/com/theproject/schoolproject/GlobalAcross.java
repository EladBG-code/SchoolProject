package com.theproject.schoolproject;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.Layout;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;

public class GlobalAcross {
    public static ArrayList<User> allUsers; //The ArrayList of all users (used for login - needs to be replaced)
    public static User currentUser; //Current logged in user
    public static int currentUserIndex;
    public static boolean firstLoginSuggestion = false;
    public static String infoMessage;
    public static int editingTemp;
    public static int selectedSubjectVectorID;

    //Useful global functions across the application : for the current user
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



    public static void activateGradientBackground(DrawerLayout layout,int millisecondsEnterFadeDuration, int millisecondsExitFadeDuration){
        //Global function for animating the gradient background of the app which some activities use
        AnimationDrawable animationDrawable = (AnimationDrawable)layout.getBackground();
        animationDrawable.setEnterFadeDuration(millisecondsEnterFadeDuration);
        animationDrawable.setExitFadeDuration(millisecondsExitFadeDuration);
        animationDrawable.start();
    }

    public static String[] getAllSubjectsArr(){
        String[] subjects = {"מתמטיקה","היסטוריה","לשון","אזרחות","תנ"+'"'+"ך","ספרות","אנגלית","ביולוגיה","מדעי המחשב","כימיה","פיזיקה","תולדות האומנות","תקשורת","מדעי החברה"};
        return subjects;
    }

    public static ArrayList<String> getAllSubjectsArrayList(){
        String[] subjectsArr = getAllSubjectsArr();
        ArrayList<String> subjectsArrayList = new ArrayList<>();
        for(int i=0;i<getAllSubjectsArr().length;i++){
            subjectsArrayList.add(subjectsArr[i]);
        }
        return subjectsArrayList;
    }

    public static void logoutFunction(){
        allUsers = null;
        currentUser = null;
        //currentUserIndex = -1;
    }

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

    /*public static void logoutResetUser(){
        currentUser.setEmail("");
        currentUser.setfName("");
        currentUser.setGrade(1);
        currentUser.setlName("");
        currentUser.setPassword("");
        currentUser.setUsername("");
        currentUser.setPfpPath("");
    }*/

    //
}
