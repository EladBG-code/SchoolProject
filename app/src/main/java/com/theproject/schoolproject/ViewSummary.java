package com.theproject.schoolproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewSummary extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvSummaryTitle;
    private TextView tvSummaryAuthor;
    private TextView tvSummaryDescription;
    DatabaseReference database;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_summary);

        setToolbarAndDrawer();

        tvSummaryAuthor = findViewById(R.id.viewSummaryAuthor);
        tvSummaryTitle = findViewById(R.id.viewSummaryTitle);
        tvSummaryDescription = findViewById(R.id.viewSummaryDescription);

        String summaryKey = getIntent().getStringExtra("summaryKey");
        String subject = getIntent().getStringExtra("subject");

        database = FirebaseDatabase.getInstance().getReference().child(subject);
        database.child(summaryKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            String summaryTitle = snapshot.child("title").getValue().toString();
            String summaryAuthor = snapshot.child("author").getValue().toString();
            String summaryDescription = snapshot.child("description").getValue().toString();
            tvSummaryTitle.setText(summaryTitle);
            tvSummaryAuthor.setText("סופר\\ת: "+summaryAuthor);
            tvSummaryDescription.setText(summaryDescription);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void setToolbarAndDrawer(){
        drawerLayout = findViewById(R.id.drawer_layout_view_summary);
        navigationView = findViewById(R.id.nav_view_view_summary);
        toolbar = findViewById(R.id.toolbarViewSummary);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        if (item.getTitle().equals("התנתקות")) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ViewSummary.this);

            builder.setMessage("האם את\\ה בטוח\\ה שאת\\ה רוצה להתנתק?")
                    .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GlobalAcross.currentUser = null;
                            Intent intent = new Intent(ViewSummary.this, MainActivity.class);
                            Toast.makeText(ViewSummary.this, "התנתקת בהצלחה.", Toast.LENGTH_SHORT - 5000).show();
                            sharedPreferences = getSharedPreferences(MainActivity.fileName, Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(MainActivity.Username); //Shared preferences - login keeper (key and value)
                            editor.remove(MainActivity.Password); //Shared preferences - login keeper
                            editor.commit();

                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("לא", null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;

        }
        if (item.getTitle().equals("סיכומים")) {
            Intent intent = new Intent(ViewSummary.this, SummariesSubjects.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("הגדרות")) {
            //Will be added in the future
            Toast.makeText(this, "מסך ההגדרות יהיה זמין לשימוש בעתיד.", Toast.LENGTH_LONG - 5000).show();
            return false;
        }
        if (item.getTitle().equals("פרופיל")) {
            Intent intent = new Intent(ViewSummary.this, ProfileActivity.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if (item.getTitle().equals("מסך הבית")) {
            Intent intent = new Intent(ViewSummary.this, Homepage.class);
            drawerLayout.closeDrawers();
            startActivity(intent);
            return false;
        }
        if(item.getTitle().equals("אודות")){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ViewSummary.this);
            String info = "שלום "+GlobalAcross.currentUser.getfName()+", שמי אלעד ואני פיתחתי את אפליקציה זו. אשמח שתשלח\\י לי פידבק לאימייל: "+"eladbargal2@gmail.com";
            builder.setMessage(info)
                    .setNegativeButton("הבנתי",null);

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return false;
    }
}