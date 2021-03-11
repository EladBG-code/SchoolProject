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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    private String subject;
    //private int score;

    public Summary(){

    }

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

    //EDITOR NOTE: IF YOU HAVE TIME ADD AN IMAGE OF THE AUTHOR TO THE SUMMARY PAGE
    public Summary(String author, String title, String description, String subject) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.subject = subject;
        //EDITOR NOTE URGENT: ADD IMAGE\FILE UPLOADING SYSTEM
    }
    public Summary(String author, String title, String description, String subject, Long amountOfLikes, ArrayList<String> usersThatLiked) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.amountOfLikes=amountOfLikes;
        this.usersThatLiked=usersThatLiked;
        //EDITOR NOTE URGENT: ADD IMAGE\FILE UPLOADING SYSTEM
    }

    public ArrayList<String> getUsersThatLiked() {
        return usersThatLiked;
    }

    public void setUsersThatLiked(ArrayList<String> usersThatLiked) {
        this.usersThatLiked = usersThatLiked;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public long getAmountOfLikes() {
        return amountOfLikes;
    }


    public void setAmountOfLikes(long amountOfLikes) {
        this.amountOfLikes = amountOfLikes;
    }

    public boolean isHasNotified() {
        return hasNotified;
    }

    public void setHasNotified(boolean hasNotified) {
        this.hasNotified = hasNotified;
    }

    public int getCreatorIndex() {
        return creatorIndex;
    }

    public void setCreatorIndex(int creatorIndex) {
        this.creatorIndex = creatorIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileRef() {
        return fileRef;
    }

    public void setFileUri(String fileRef) {
        this.fileRef = fileRef;
    }
}
