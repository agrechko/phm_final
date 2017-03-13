/*
Project Name: PHM
Author Name: Advait, Artem, Geoff, Tahani & Yatin.
*/

package capstone.se491_phm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import capstone.se491_phm.common.util.BackgroundWorker;
import capstone.se491_phm.common.Constants;
import capstone.se491_phm.gcm.RegistrationIntentService;
import capstone.se491_phm.jobs.DailyActivityMonitorJob;
import capstone.se491_phm.jobs.MoodDailyJob;
import capstone.se491_phm.jobs.MoodSurvey;
import capstone.se491_phm.jobs.WeeklyActivityMonitorJob;
import capstone.se491_phm.location.GPS_Service;
import capstone.se491_phm.questionnaire.Mood;
import capstone.se491_phm.questionnaire.MoodDaily;
import capstone.se491_phm.sensors.ExternalSensorActivity;
import capstone.se491_phm.sensors.ExternalSensorClient;
import capstone.se491_phm.sensors.FallDetectedActivity;
import capstone.se491_phm.sensors.FallViewSettingActivity;
import capstone.se491_phm.sensors.ISensors;
import capstone.se491_phm.sensors.StepCounter;
import capstone.se491_phm.sensors.StepCounterActivity;
import capstone.se491_phm.webview.TermsConditionsWebView;
import capstone.se491_phm.webview.WebportalWebView;


import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Build;

import android.support.annotation.NonNull;

import android.support.v4.content.ContextCompat;



public class MainActivity extends Activity {
    public static Context mContext;
    static Map<String, ISensors> sensors = new HashMap<>();
    private AlarmManager alarmMgr;
    private Map<String,PendingIntent> mAlarmIntents = new HashMap<String,PendingIntent>();
    public static NotificationManager mNotificationManager;
    public static SharedPreferences sharedPreferences = null;
    public static Map<String, Intent> runningServices = new HashMap<>();
    BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getBaseContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        //cancel all notification created by the app
        mNotificationManager =(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

        //register for gcm
        //Intent intent = new Intent(this, RegistrationIntentService.class);
        //startService(intent);
        //init sensors
        initSensors();
        //create all jobs
        createScheduleJobs();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String broadcastMessage = intent.getStringExtra(Alarm.broadcasterMessage);
                if("fallDetected".equalsIgnoreCase(broadcastMessage)){
                    setContentView(R.layout.activity_fall_detected);
                    Intent tempIntent = new Intent(context, FallDetectedActivity.class);
                    startActivity(tempIntent);
                    finish();
                } else {
                    FallDetectedActivity.setCountDownTextValue(broadcastMessage);
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Alarm.alertBroadcastIntent)
        );

        if(sharedPreferences.getBoolean(Constants.WAITING_FOR_FALL_ACK,false)){
            setContentView(R.layout.activity_fall_detected);
            Intent tempIntent = new Intent(mContext, FallDetectedActivity.class);
            startActivity(tempIntent);
            finish();
        }

        if("".equals(sharedPreferences.getString(Constants.UNIQUE_ID,""))){
            String uniqueID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(Constants.UNIQUE_ID,uniqueID).commit();
        } else {
            if("".equals(sharedPreferences.getString(Constants.USER_ID,""))) {
                BackgroundWorker worker= new BackgroundWorker(this);
                worker.execute("getUserID", sharedPreferences.getString(Constants.UNIQUE_ID,""));
                String userid = worker.getResult();
                if(!"".equals(userid)){
                    sharedPreferences.edit().putString(Constants.USER_ID, userid).commit();
                }
            }
        }

        Switch switch1 = (Switch) findViewById(R.id.activitySwitch);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activitySwitch(null);
            }
        });

        Switch switch2 = (Switch) findViewById(R.id.fallSwitch);
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fallSwitch(null);
            }
        });

        Switch switch3 = (Switch) findViewById(R.id.externalSwitch);
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                externalSwitch(null);
            }
        });

        if(!sharedPreferences.getBoolean(Constants.EULA,false)){
            setContentView(R.layout.activity_terms_conditions);
            Intent intent = new Intent(this, TermsConditionsWebView.class);
            startActivity(intent);
            finish();
        }


    }


    /**
     * Called when app is killed
     */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        //need to save any collected data before exit
        for(ISensors sensor : sensors.values()){
            sensor.saveData(mContext);
        }
    }

    private void initSensors(){
        if(sharedPreferences != null) {
            ((Switch) findViewById(R.id.activitySwitch)).setChecked(sharedPreferences.getBoolean("activitySwitch", true));
            if(sharedPreferences.getBoolean("activitySwitch", true)){
                (findViewById(R.id.openActivityViewBtn)).setVisibility(View.VISIBLE);
                if(sensors.get("stepCounter") == null) {
                    StepCounter stepCounter = new StepCounter();
                    stepCounter.initialize(mContext);
                    //add all initialized sensors
                    sensors.put("stepCounter", stepCounter);
                } else {
                    sensors.get("stepCounter").initialize(mContext);
                }
            }

            ((Switch) findViewById(R.id.fallSwitch)).setChecked(sharedPreferences.getBoolean("fallSwitch", false));
            if(sharedPreferences.getBoolean("fallSwitch", false)) {
                if(!"".equals(sharedPreferences.getString(Constants.EMERGENCY_CONTACT, ""))) {
                    ((Switch) findViewById(R.id.fallSwitch)).setChecked(true);
                }
            }
            //do not need to save reference for fall detector
            Detector.initiate(getContextMain());

            //does not make sense to turn on external monitoring by default since additional setup is required
            ((Switch) findViewById(R.id.externalSwitch)).setChecked(sharedPreferences.getBoolean("externalSwitch", false));
            if(sharedPreferences.getBoolean("externalSwitch", false)){
                (findViewById(R.id.externalSensorViewbtn)).setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * delete all schedules by looping through the list with schedules and canceling them one by one
     * @param view
     */
    public void deleteSchedules(View view) {
        if (alarmMgr!= null) {
            for(PendingIntent intent : mAlarmIntents.values()) {
                alarmMgr.cancel(intent);
            }
        }
    }

    public void showMoodSurvey(View view) {
        setContentView(R.layout.q_mood);
        Intent intent = new Intent(this, Mood.class);
        startActivity(intent);
        finish();
    }

    public void showMoodDaily(View view) {
        setContentView(R.layout.q_mood_daily);
        Intent intent = new Intent(this, MoodDaily.class);
        startActivity(intent);
        finish();
    }

    private void createScheduleJobs(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //daily job to clear daily activity group
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentDailyActivity = new Intent(mContext, DailyActivityMonitorJob.class);
        boolean alarmIntentDailyActivityActive = (PendingIntent.getService(mContext, 0,
                intentDailyActivity, PendingIntent.FLAG_NO_CREATE) != null);
        PendingIntent alarmIntentDailyActivity = PendingIntent.getService(mContext, 0, intentDailyActivity, 0);
        mAlarmIntents.put("dailyActivityMonitorJob",alarmIntentDailyActivity);
        if (!alarmIntentDailyActivityActive)
        {
            alarmMgr.setRepeating(AlarmManager.RTC,
                    calendar.getTimeInMillis(),
                    1000*60*60*24, alarmIntentDailyActivity);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        //weekly job to clear weekly activity group
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentWeeklyActivity = new Intent(mContext, WeeklyActivityMonitorJob.class);
        boolean alarmIntentWeeklyActivityActive = (PendingIntent.getService(mContext, 1,
                intentWeeklyActivity, PendingIntent.FLAG_NO_CREATE) != null);
        PendingIntent alarmIntentWeeklyActivity = PendingIntent.getService(mContext, 1, intentWeeklyActivity, 0);
        mAlarmIntents.put("weeklyActivityMonitorJob",alarmIntentWeeklyActivity);
        if (!alarmIntentWeeklyActivityActive)
        {
            alarmMgr.setRepeating(AlarmManager.RTC,
                    calendar.getTimeInMillis(),
                    1000*60*60*24*7, alarmIntentWeeklyActivity);
        }

        //mood daily job
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentMoodDaily = new Intent(mContext, MoodDailyJob.class);
        boolean alarmIntentMoodDailyActive = (PendingIntent.getService(mContext, 2,
                intentMoodDaily, PendingIntent.FLAG_NO_CREATE) != null);
        PendingIntent alarmIntentMoodDaily = PendingIntent.getService(mContext, 2, intentMoodDaily, 0);
        mAlarmIntents.put("moodDaily",alarmIntentMoodDaily);
        if (!alarmIntentMoodDailyActive)
        {
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    AlarmManager.INTERVAL_DAY,
                    AlarmManager.INTERVAL_DAY, alarmIntentMoodDaily);
        }

        //start time for mood survey
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //start time for mood survey
        //weekly mood survey
        alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intentWeeklySurvey = new Intent(mContext, MoodSurvey.class);
        boolean alarmIntentMoodSurveyActive = (PendingIntent.getService(mContext, 2,
                intentMoodDaily, PendingIntent.FLAG_NO_CREATE) != null);
        PendingIntent alarmIntentMoodSurvey = PendingIntent.getService(mContext, 3, intentWeeklySurvey, 0);
        mAlarmIntents.put("moodSurvey",alarmIntentMoodSurvey);
        if (!alarmIntentMoodSurveyActive)
        {
            alarmMgr.setInexactRepeating(AlarmManager.RTC,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY*7, alarmIntentMoodSurvey);
        }
    }

    public Context getContextMain(){
        if(mContext == null){
            mContext = getBaseContext();
        }
        return mContext;
    }

    public void showWebPortal(View view) {
        setContentView(R.layout.activity_webportal_view);
        Intent intent = new Intent(this, WebportalWebView.class);
        startActivity(intent);
        finish();
    }

    public void showExternalSensorView(View view) {
        setContentView(R.layout.ext_monitoring_session);
        Intent intent = new Intent(this, ExternalSensorActivity.class);
        startActivity(intent);
        finish();
    }

    public void showActivityView(View view) {
        setContentView(R.layout.activity_step_details);
        Intent intent = new Intent(this, StepCounterActivity.class);
        startActivity(intent);
        finish();
    }

    public void activitySwitch(View view) {
        Switch switch1 = (Switch) findViewById(R.id.activitySwitch);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(switch1.isChecked()){
            (findViewById(R.id.openActivityViewBtn)).setVisibility(View.VISIBLE);
            if(sensors.get("stepCounter") == null) {
                StepCounter stepCounter = new StepCounter();
                stepCounter.initialize(mContext);
                //add all initialized sensors
                sensors.put("stepCounter", stepCounter);
            } else {
                sensors.get("stepCounter").initialize(mContext);
            }
            editor.putBoolean("activitySwitch", true);
        } else {
            (findViewById(R.id.openActivityViewBtn)).setVisibility(View.INVISIBLE);
            sensors.get("stepCounter").stopMonitoring(view);
            editor.putBoolean("activitySwitch", false);
        }
        editor.commit();
    }
    public void fallSwitch(View view) {
        Switch switch1 = (Switch) findViewById(R.id.fallSwitch);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(switch1.isChecked()){
            runtime_permissions();

            editor.putBoolean("fallSwitch", true);
            editor.commit();

            setContentView(R.layout.activity_fallview_setting);
            Intent intent = new Intent(this, FallViewSettingActivity.class);
            startActivity(intent);
            finish();
        } else {
            editor.putBoolean("fallSwitch", false);
            editor.commit();
        }

    }
    public void externalSwitch(View view) {
        Switch switch1 = (Switch) findViewById(R.id.externalSwitch);
        if(switch1.isChecked()){
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
                (findViewById(R.id.externalSensorViewbtn)).setVisibility(View.VISIBLE);
                prepareForExternalMonitoring();
            } else {
                (findViewById(R.id.webPortalRequiredMsg)).setVisibility(View.VISIBLE);
                switch1.setChecked(false);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        (findViewById(R.id.webPortalRequiredMsg)).setVisibility(View.INVISIBLE);
                    }
                }, 2000);
            }
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("externalSwitch", false);
            Intent intent = runningServices.get(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME);
            if (intent != null) {
                stopService(intent);
                runningServices.remove(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME);
            }
            (findViewById(R.id.externalSensorViewbtn)).setVisibility(View.INVISIBLE);
            editor.commit();
        }
    }

    private void prepareForExternalMonitoring(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("externalSwitch", true);
            editor.commit();
            showExternalSensorView(null);
    }

    public static void notifyUserNoServerConnection(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle("Connection Issue")
                        .setContentText("Unable to reconnect to external sensors, check server host/ip and please try again");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, ExternalSensorActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ExternalSensorActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // mId allows you to update the notification later on.
        MainActivity.mNotificationManager.notify(3, mBuilder.build());
    }


    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.SEND_SMS},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //enable_buttons(null);
            }else {
                runtime_permissions();
            }
        }
    }


}
