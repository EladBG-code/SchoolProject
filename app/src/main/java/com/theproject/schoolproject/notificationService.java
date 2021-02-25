package com.theproject.schoolproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

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
        final ArrayList<String> keys = new ArrayList<>();
        final SharedPreferences sharedPreferences = getSharedPreferences("index",Context.MODE_PRIVATE);
        for(final String subject : arr){
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(subject);
            ref.orderByChild("amountOfLikes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GenericTypeIndicator<HashMap<String,Summary>> t = new GenericTypeIndicator<HashMap<String,Summary>>(){};
                    if(snapshot.getValue(t) != null){
                        HashMap<String,Summary> summaries = new HashMap<String,Summary>(snapshot.getValue(t));
                        for(Summary summary : summaries.values()){
                            //Send notification to summary creator by index saved - WIP
                            if(summary.getAmountOfLikes()>=5) {
                                if (!summary.isHasNotified()) {
                                    if (summary.getCreatorIndex() == sharedPreferences.getInt("index", 0)) {
                                        summary.setHasNotified(true);
                                        String key = summary.getId();
                                        FirebaseDatabase.getInstance().getReference(summary.getSubject()).child(key).child("hasNotified").setValue(true);



                                        /*String message = "הסיכום שלך בנושא "+subject+' '+"קיבל 5 לייקים או יותר!";
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(notificationService.this)
                                                .setContentTitle("התראה חדשה")
                                                .setSmallIcon(R.drawable.like_icon)
                                                .setContentText(message)
                                                .setAutoCancel(true);

                                        Intent intent = new Intent(notificationService.this,NotificationActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("message",message);

                                        PendingIntent pendingIntent = PendingIntent.getActivity(notificationService.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                        builder.setContentIntent(pendingIntent);

                                        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                        notificationManager.notify(0,builder.build());*/

                                        //Toast.makeText(notificationService.this,"הסיכום שלך בנושא "+subject+" "+"קיבל 5 לייקים או יותר!", Toast.LENGTH_SHORT).show();



                                        int NOTIFICATION_ID = 234;
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                        String CHANNEL_ID = "XXX";

                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                            CharSequence name = "XXX";
                                            String Description = "XXX";
                                            int importance = NotificationManager.IMPORTANCE_HIGH;
                                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                                            mChannel.setDescription(Description);
                                            mChannel.enableLights(true);
                                            mChannel.setLightColor(Color.RED);
                                            mChannel.enableVibration(true);
                                            mChannel.setShowBadge(false);
                                            notificationManager.createNotificationChannel(mChannel);
                                        }

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.ic_launcher)
                                                .setContentTitle("עדכון")
                                                .setContentText("הסיכום שלך בנושא "+subject+" הגיע לחמישה לייקים או יותר!")
                                                .setSmallIcon(R.drawable.like_icon);

                                        Intent resultIntent = new Intent(getApplicationContext(), LoadingActivity.class);
                                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                                        stackBuilder.addParentStack(LoadingActivity.class);
                                        stackBuilder.addNextIntent(resultIntent);
                                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                                        builder.setContentIntent(resultPendingIntent);
                                        notificationManager.notify(NOTIFICATION_ID, builder.build());

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
        //android.os.SystemClock.sleep(300000); //5 minutes thread sleep
        //SystemClock.sleep(30000);
        return super.onStartCommand(intent, flags, startId);
    }
}