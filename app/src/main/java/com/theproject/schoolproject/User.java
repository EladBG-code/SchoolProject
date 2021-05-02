package com.theproject.schoolproject;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class User {

    private String fName,lName,username,email,password,pfpPath;
    private int grade;
    private ArrayList<String>[] userLikedSummariesIDs;

    /**
     * First and empty builder of the "User" object
     * */
    public User(){

    }

    /**
     * Second builder of the "User" object
     * @param fName
     * @param lName
     * @param username
     * @param email
     * @param password
     * @param grade
     */
    public User(String fName, String lName, String username, String email, String password,int grade) {
        this.fName = fName;
        this.lName = lName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.grade = grade;
        this.pfpPath = "none";
        this.userLikedSummariesIDs = new ArrayList[14];
    }

    /**
     * Returns this user's current first name
     * @return
     */
    public String getfName() {
        return fName;
    }

    /**
     * Sets the first name of the current user from the received string value
     * @param fName
     * */
    public void setfName(String fName) {
        this.fName = fName;
    }

    /**
     * Returns this user's current last name
     * @return
     */
    public String getlName() {
        return lName;
    }

    /**
     * Sets the last name of the current user from the received string value
     * @param lName
     */
    public void setlName(String lName) {
        this.lName = lName;
    }

    /**
     * Returns the username of this user
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets this user's username from the received string value
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the string email of this user
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets this user's email to the received string value
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the password string value of this user
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password string value of this user from the received string value
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns grade of this user
     * @return
     */
    public int getGrade() {
        return grade;
    }

    /**
     * Sets this user's grade from the received int value
     * @param grade
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    /**
     * Returns the string profile path value of this user
     * @return
     */
    public String getPfpPath() {
        return pfpPath;
    }

    /**
     * Sets this user's profile picture from the received string value
     * @param pfpPath
     */
    public void setPfpPath(String pfpPath) {
        this.pfpPath = pfpPath;
    }

}
