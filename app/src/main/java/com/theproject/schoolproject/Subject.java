package com.theproject.schoolproject;

import java.util.ArrayList;

public class Subject {

    private String subjectName;
    private ArrayList<Summary> summaries;

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
