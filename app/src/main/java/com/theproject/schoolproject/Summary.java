package com.theproject.schoolproject;

import android.content.SharedPreferences;

import java.util.ArrayList;

public class Summary {

    //author is the username of the user that created the summary
    private String author;
    private String title;
    private String description;
    private String id;
    private String fileRef;
    private long amountOfLikes;
    private boolean hasNotified;
    private int creatorIndex;
    private ArrayList<String> usersThatLiked;

    /**Returns string subject of the current summary*/
    public String getSubject() {
        return subject;
    }

    /**Sets the subject of the current summary to the received subject string*/
    public void setSubject(String subject) {
        this.subject = subject;
    }

    private String subject;
    //private int score;

    /**(First) Empty builder function for the "Summary" object*/
    public Summary(){

    }

    /**Second builder function of the "Summary" object*/
    public Summary(String author, String title, String description,SharedPreferences sharedPreferences) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.amountOfLikes = 0;
        this.hasNotified = false;
        this.id = "";
        this.fileRef = "none";
        this.creatorIndex = sharedPreferences.getInt("index",0);
    }

    /**Third builder function of the "Summary" object -Not in use-*/
    //EDITOR NOTE: IF YOU HAVE TIME ADD AN IMAGE OF THE AUTHOR TO THE SUMMARY PAGE
    public Summary(String author, String title, String description, String subject) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.subject = subject;
        //EDITOR NOTE URGENT: ADD IMAGE\FILE UPLOADING SYSTEM
    }

    /**Fourth builder function of the "Summary" object*/
    public Summary(String author, String title, String description, String subject, Long amountOfLikes, ArrayList<String> usersThatLiked) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.amountOfLikes=amountOfLikes;
        this.usersThatLiked = usersThatLiked;
        //EDITOR NOTE URGENT: ADD IMAGE\FILE UPLOADING SYSTEM
    }

    /**Fifth builder function of the "Summary" object -Not in use-*/
    public Summary(String author, String title, String description, String subject, Long amountOfLikes) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.amountOfLikes=amountOfLikes;
        this.usersThatLiked = null;
        //EDITOR NOTE URGENT: ADD IMAGE\FILE UPLOADING SYSTEM
    }

    /**Returns the ArrayList of indexes of the users that liked this certain summary*/
    public ArrayList<String> getUsersThatLiked() {
        return usersThatLiked;
    }

    /**Sets the usersThatLiked ArrayList to the received one*/
    public void setUsersThatLiked(ArrayList<String> usersThatLiked) {
        this.usersThatLiked = usersThatLiked;
    }

    /**Returns the author's string value*/
    public String getAuthor() {
        return author;
    }

    /**Sets the author's string value to the received one*/
    public void setAuthor(String author) {
        this.author = author;
    }

    /**Returns the string value of the summary's title*/
    public String getTitle() {
        return title;
    }

    /**Sets the string value of the title to the reeived one*/
    public void setTitle(String title) {
        this.title = title;
    }

    /**Returns the string value of the description of this summary*/
    public String getDescription() {
        return description;
    }

    /**Sets the description string value of this summary onto the received one*/
    public void setDescription(String description) {
        this.description = description;
    }

    /**Returns the amount of likes of this summary*/
    public long getAmountOfLikes() {
        return amountOfLikes;
    }

    /**Sets the amount of likes onto the received long variable*/
    public void setAmountOfLikes(long amountOfLikes) {
        this.amountOfLikes = amountOfLikes;
    }

    /**Returns the boolean value of weather or not, the creator has received 5 or more likes*/
    public boolean isHasNotified() {
        return hasNotified;
    }

    /**Sets the boolean value of weather or not the the creator of the summary has been notified*/
    public void setHasNotified(boolean hasNotified) {
        this.hasNotified = hasNotified;
    }

    /**Returns the creator's index*/
    public int getCreatorIndex() {
        return creatorIndex;
    }

    /**Sets the creator's index*/
    public void setCreatorIndex(int creatorIndex) {
        this.creatorIndex = creatorIndex;
    }

    /**Returns a string value which is this summary's ID*/
    public String getId() {
        return id;
    }

    /**Sets the ID of this summary onto the received one*/
    public void setId(String id) {
        this.id = id;
    }

    /**Returns a string which is the file reference of this summary*/
    public String getFileRef() {
        return fileRef;
    }

    /**Sets the file reference of this summary to the received one*/
    public void setFileRef(String fileRef) {
        this.fileRef = fileRef;
    }
}
