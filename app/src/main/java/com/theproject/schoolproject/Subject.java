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
    FirebaseDatabase database;
    DatabaseReference myRef;

    public Subject(String subjectName){
        // Write a message to the database
        subjectName = subjectName;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(subjectName);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<ArrayList<Summary>> t = new GenericTypeIndicator<ArrayList<Summary>>() {
                };
                summaries = new ArrayList<Summary>();
                if(snapshot.getValue(t) != null) {
                    summaries.addAll(snapshot.getValue(t));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //myRef.setValue("Hello, World!");
    }
    public void addSummary(Summary summary){
        summaries.add(summary);
        myRef.setValue(summaries);
    }
    public Subject(String subjectName, ArrayList<Summary> summaries) {
        this.subjectName = subjectName;
        this.summaries = summaries;
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
