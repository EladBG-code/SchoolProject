package com.theproject.schoolproject;

import android.graphics.Bitmap;
import android.net.Uri;

public class User {

    private String fName,lName,username,email,password,pfpPath;
    private int grade;

    public User(){

    }


    public User(String fName, String lName, String username, String email, String password,int grade) {
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.grade = grade;
        this.pfpPath = "none";
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getPfpPath() {
        return pfpPath;
    }

    public void setPfpPath(String pfpPath) {
        this.pfpPath = pfpPath;
    }
}
