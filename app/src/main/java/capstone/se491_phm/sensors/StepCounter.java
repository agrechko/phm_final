package capstone.se491_phm.sensors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;
import capstone.se491_phm.common.util.FileManager;

import static android.content.pm.PackageManager.FEATURE_SENSOR_STEP_COUNTER;

/**
 * Created by Acer on 10/1/2016.
 */

public class StepCounter implements SensorEventListener,ISensors {

    private static SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    private static final String mFileName = "activityData.txt";
    private static boolean initialized = false;

    private static Map<String,Long> activityData = new ConcurrentHashMap<>();

    public StepCounter(){

    }

    /**
     * Call to initialize and start the monitoring. After initialization you can use the start and
     * stop monitoring methods
     * @return
     */
    public boolean initialize(Context context){
        if(!initialized || mSensorManager == null) {
            PackageManager pm = context.getPackageManager();
            if (pm.hasSystemFeature(FEATURE_SENSOR_STEP_COUNTER)) {
                populateActivityMap(context);

                mSensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
                mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
                initialized = true;
                //Input parameter is being ignored so null is fine
                startMonitoring(null);
            }
        } else {
            //do not need to unregister before registering because android will check if there is an existing sensor registered
            //mSensorManager.unregisterListener(this, mStepCounterSensor);
            //mSensorManager.unregisterListener(this, mStepDetectorSensor);
            //register because sensors have already been initialized
            mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        return initialized;
    }

    public void populateActivityMap(Context context){
        String content = "";
        try {
            content = FileManager.readFromStorage(context, mFileName);
        } catch (IOException e) {
            Log.e("Read Storage","unable to read activity data from storage");
        }

        JSONObject jsonObject = null;
        try {
            if(!"".equals(content)) {
                jsonObject = new JSONObject(content);
            }
        } catch (JSONException e) {
            Log.e("String to json","unable to create json object of activity data");
        }

        if(jsonObject != null){
            for(Enum group : Constants.ActivityGroup.values()){
                if(jsonObject.has(group.toString())){
                    try {
                        activityData.put(group.toString(),Long.parseLong(jsonObject.get(group.toString()).toString()));
                    } catch (JSONException e) {
                        Log.e("JsonObject","unable to add activity data to json object");
                    }
                }
                if(!activityData.containsKey(group.toString())){
                    activityData.put(group.toString(),0L);
                }
            }
        } else {
            //populate map with all zero since there is no data read in
            for(Enum group : Constants.ActivityGroup.values()){
                activityData.put(group.toString(),0L);
            }
        }
    }

    /**
     * Call to persist data
     */
    public void saveData(Context context){
        if(!activityData.isEmpty()){
            JSONObject jsonObject = new JSONObject();
            for(String group : activityData.keySet()){
                try {
                    jsonObject.put(group,activityData.get(group));
                } catch (JSONException e) {
                    Log.e("JsonObject","unable to add create json object for save of activity data");
                }
            }
            try {
                FileManager.writeFile(context,mFileName,jsonObject.toString());
            } catch (IOException e) {
                Log.e("Save Storage","unable to save activity data");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //total update
            activityData.put(Constants.ActivityGroup.TOTAL.toString(),
                    activityData.get(Constants.ActivityGroup.TOTAL.toString())+1L);
            //daily update
            activityData.put(Constants.ActivityGroup.DAILY.toString(),
                    activityData.get(Constants.ActivityGroup.DAILY.toString())+1L);
            //weekly update
            activityData.put(Constants.ActivityGroup.WEEKLY.toString(),
                    activityData.get(Constants.ActivityGroup.WEEKLY.toString())+1L);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Must call initialize before using this method to restart monitoring after stopping
     * @param view
     */
    public void startMonitoring(View view){
        if(initialized) {
            mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }
    public void stopMonitoring(View view){
        mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }

    /**
     * Get step count for specified group
     * @param activityGroup
     * @return
     */
    public long getActivityDataGroup(String activityGroup){
        if(activityData.containsKey(activityGroup)){
            return activityData.get(activityGroup);
        }
        return 0L;
    }

    /**
     * manual update activity group value
     * @param group
     * @param value
     */
    public void updateActivityGroup(String group, long value){
        activityData.put(group,value);
    }

    public static String getFileName(){
        return mFileName;
    }

    public boolean isInitialized(){
        return initialized;
    }

//    public void goHome(View view) {
//        setContentView(R.layout.activity_main);
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
}