package com.theproject.schoolproject;

import java.util.ArrayList;

public class GlobalAcross {
    public static ArrayList<User> allUsers; //The arraylist of all users (used for login - needs to be replaced)
    public static User currentUser; //Current logged in user
    public static int indexInDatabase;

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


    //
}
