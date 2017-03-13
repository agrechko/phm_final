package capstone.se491_phm.jobs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.questionnaire.MoodDaily;

/**
 * Created by Acer on 10/15/2016.
 */

public class MoodDailyJob extends Service {
    public static int notificationId = 1;
    private NotificationManager mNM;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle("PHM")
                        .setContentText("Enter your mood for today");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getBaseContext(), MoodDaily.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MoodDaily.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // mId allows you to update the notification later on
        mNM.notify(notificationId, mBuilder.build());

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        //super.onCreate();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
