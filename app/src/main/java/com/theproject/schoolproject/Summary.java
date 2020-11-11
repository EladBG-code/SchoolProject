package com.theproject.schoolproject;

public class Summary {

    //author is the username of the user that created the summary
    private String author;
    private String title;
    private String description;
    //private int score;

    //EDITOR NOTE: IF YOU HAVE TIME ADD AN IMAGE OF THE AUTHOR TO THE SUMMARY PAGE
    public Summary(String author, String title, String description) {
        this.author = author;
        this.title = title;
        this.description = description;
        //EDITOR NOTE URGENT: ADD IMAGE\FILE UPLOADING SYSTEM
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


}
