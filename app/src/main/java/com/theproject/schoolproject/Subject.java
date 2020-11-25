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


    public Subject(String subjectName){
       this.subjectName = subjectName;
    }
    public Subject(String subjectName, ArrayList<Summary> summaries) {
        this.subjectName = subjectName;
        this.summaries = summaries;
    }


    public void addSummary(Summary summary){
        summaries.add(summary);
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ArrayList<Summary> getSummaries() {
        return summaries;
    }

    public void setSummaries(ArrayList<Summary> summaries) {
        this.summaries = summaries;
    }
}
