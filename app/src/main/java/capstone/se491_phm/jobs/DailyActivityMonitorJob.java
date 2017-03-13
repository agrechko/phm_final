package capstone.se491_phm.jobs;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import capstone.se491_phm.common.Constants;
import capstone.se491_phm.common.util.BackgroundWorker;
import capstone.se491_phm.common.util.FileManager;
import capstone.se491_phm.sensors.StepCounter;

/**
 * Created by Acer on 10/21/2016.
 */

public class DailyActivityMonitorJob extends Service {

    /*@Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DailyActivityMonitorJob","daily alarm fired");
        long totalStep = StepCounter.getActivityDataGroup(Constants.ActivityGroup.TOTAL.toString());

        StepCounter stepCounter = new StepCounter();
        if(!stepCounter.isInitialized()) {
            stepCounter.populateActivityMap(context);
        }
        stepCounter.updateActivityGroup(Constants.ActivityGroup.DAILY.toString(),0L);
        stepCounter.saveData(context);
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DailyActivityMonitorJob","daily alarm fired");

        StepCounter stepCounter = new StepCounter();
        if(!stepCounter.isInitialized()) {
            stepCounter.populateActivityMap(getBaseContext());
        }

        long dailySteps = stepCounter.getActivityDataGroup(Constants.ActivityGroup.DAILY.toString());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String userid = sharedPreferences.getString(Constants.USER_ID,"");
        if("".equals(userid)) {
            BackgroundWorker worker= new BackgroundWorker(this);
            worker.execute("getUserID", sharedPreferences.getString(Constants.UNIQUE_ID,""));
            userid = worker.getResult();
            if(!"".equals(userid)){
                sharedPreferences.edit().putString(Constants.USER_ID, userid).commit();
            }
        }

        if(!"".equals(userid)){
            BackgroundWorker worker= new BackgroundWorker(this);
            worker.execute("getActivityConfig", userid);
            String[] activityConfig = worker.getResult().split("@");
            if(activityConfig.length == 2){
                if(dailySteps < Integer.parseInt(activityConfig[0]) || dailySteps > Integer.parseInt(activityConfig[1])){
                    worker= new BackgroundWorker(this);
                    worker.execute("addAbnormality", userid, "activity_monitor", "abnormality triggered with reading of "+dailySteps);
                }
            }
        }
        stepCounter.updateActivityGroup(Constants.ActivityGroup.DAILY.toString(),0L);
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
