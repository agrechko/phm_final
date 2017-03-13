package capstone.se491_phm.jobs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;
import capstone.se491_phm.questionnaire.Mood;
import capstone.se491_phm.questionnaire.MoodDaily;

/**
 * Created by Acer on 10/15/2016.
 */

public class MoodSurvey extends Service {
    private final double MOOD_LOW = 1.5;
    private final double MOOD_HIGH = 2.5;
    public static int notificationId = 2;
    private NotificationManager mNM;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        double dailyMoodAverage = sharedPreferences.getFloat(Constants.DAILY_MOOD_AVERAGE,0);
        int numberOfEntries = sharedPreferences.getInt(Constants.NUMBER_OF_ENTRIES,0);
        if((dailyMoodAverage < MOOD_LOW || dailyMoodAverage > MOOD_HIGH) &&
                numberOfEntries > 0){
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getBaseContext())
                            .setSmallIcon(R.drawable.notification)
                            .setContentTitle("PHM")
                            .setContentText("Please complete the mood survey");
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(getBaseContext(), Mood.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(Mood.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            // notificationId allows you to update the notification later on.
            mNM.notify(notificationId, mBuilder.build());
        }
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
