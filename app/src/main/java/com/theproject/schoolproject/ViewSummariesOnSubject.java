package com.theproject.schoolproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ViewSummariesOnSubject extends AppCompatActivity {

    TextView tvSubjectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summaries_on_subject);

        tvSubjectName = findViewById(R.id.tvSubjectName);

        tvSubjectName.setText(getIntent().getStringExtra("SubjectSelected")); /*This line sets the name of the subject which was selected as the title of the subject's summary page*/
    }

    @Override
    public void onBackPressed() {
         /*if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }*/

        //NOTE: Undo the comment above on the if and else once there is a toolbar and a drawer on the summary selecting page
        super.onBackPressed();
    }
}