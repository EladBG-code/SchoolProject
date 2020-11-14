package com.theproject.schoolproject;

public class Summary {

    //author is the username of the user that created the summary
    private String author;
    private String title;
    private String description;

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

    public Summary(String author, String title, String description) {
        this.author = author;
        this.title = title;
        this.description = description;
    }

    //EDITOR NOTE: IF YOU HAVE TIME ADD AN IMAGE OF THE AUTHOR TO THE SUMMARY PAGE
    public Summary(String author, String title, String description, String subject) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.subject = subject;
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
