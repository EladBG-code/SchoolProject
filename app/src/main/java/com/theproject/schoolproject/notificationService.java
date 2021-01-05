package com.theproject.schoolproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class notificationService extends Service {
    public notificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String[] arr = {"מתמטיקה","היסטוריה","לשון","אזרחות","תנ"+'"'+"ך","ספרות","אנגלית","ביולוגיה","מדעי המחשב","כימיה","פיזיקה","תולדות האומנות","תקשורת","מדעי החברה"};
        //14 subjects
        final SharedPreferences sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);
        for(String subject : arr){
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(subject);
            ref.orderByChild("amountOfLikes").equalTo(5).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<ArrayList<Summary>> t = new GenericTypeIndicator<ArrayList<Summary>>(){};
                    if(snapshot.getValue(t) != null){
                        ArrayList<Summary> summaries = new ArrayList<>(snapshot.getValue(t));
                        for(Summary summary : summaries){
                            //Send notification to summary creator by index saved - WIP
                            if(summary.getAmountOfLikes()>=5) {
                                if (!summary.isHasNotified()) {
                                    if (summary.getCreatorIndex() == sharedPreferences.getInt("index", 0)) {
                                        summary.setHasNotified(true);
                                    }
                                }
                            }
                        }
                        ref.setValue(summaries);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //android.os.SystemClock.sleep(300000); //5 minutes thread sleep
        startService(new Intent(this,notificationService.class));
        return super.onStartCommand(intent, flags, startId);
    }
}