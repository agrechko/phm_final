package capstone.se491_phm.jobs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import capstone.se491_phm.common.Constants;
import capstone.se491_phm.sensors.StepCounter;

/**
 * Created by Acer on 10/22/2016.
 */

public class WeeklyActivityMonitorJob extends Service {

    /*@Override
    public void onReceive(Context context, Intent intent) {
        Log.i("WeekActivityMonitorJob","weekly alarm fired");
        long totalStep = StepCounter.getActivityDataGroup(Constants.ActivityGroup.TOTAL.toString());

        StepCounter stepCounter = new StepCounter();
        if(!stepCounter.isInitialized()) {
            stepCounter.populateActivityMap(context);
        }
        stepCounter.updateActivityGroup(Constants.ActivityGroup.WEEKLY.toString(),0L);
        stepCounter.saveData(context);
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("WeekActivityMonitorJob","weekly alarm fired");

        StepCounter stepCounter = new StepCounter();
        if(!stepCounter.isInitialized()) {
            stepCounter.populateActivityMap(getBaseContext());
        }
        stepCounter.updateActivityGroup(Constants.ActivityGroup.WEEKLY.toString(),0L);
        stepCounter.saveData(getBaseContext());
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
