package com.theproject.schoolproject;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Subject {

    private String subjectName;
    private ArrayList<Summary> summaries;

    /**Builder function for the "Subject" object*/
    public Subject(String subjectName){
       this.subjectName = subjectName;
    }

    /**Second builder function for the "Subject" object*/
    public Subject(String subjectName, ArrayList<Summary> summaries) {
        this.subjectName = subjectName;
        this.summaries = summaries;
    }

    /**Function adds a summary to the summary ArrayList*/
    public void addSummary(Summary summary){
        summaries.add(summary);
    }

    /**Returns the subject's name string*/
    public String getSubjectName() {
        return subjectName;
    }

    /**Sets a new subject name*/
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /**Returns the ArrayList of summaries inside this certain subject*/
    public ArrayList<Summary> getSummaries() {
        return summaries;
    }

    /**Sets the summaries ArrayList to the received one*/
    public void setSummaries(ArrayList<Summary> summaries) {
        this.summaries = summaries;
    }
}
