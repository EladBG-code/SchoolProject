package com.theproject.schoolproject;

import java.util.ArrayList;

public class GlobalAcross {
    public static ArrayList<User> allUsers; //The ArrayList of all users (used for login - needs to be replaced)
    public static User currentUser; //Current logged in user
    public static int currentUserIndex;

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

    public static void addCreatedSummaryRef(){
        //TEMP NOT CREATED
    }

    public static String[] getAllSubjectsArr(){
        String[] subjects = {"מתמטיקה","היסטוריה","לשון","אזרחות","תנ"+'"'+"ך","ספרות","אנגלית","ביולוגיה","מדעי המחשב","כימיה","פיזיקה","תולדות האומנות","תקשורת","מדעי החברה"};
        return subjects;
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
