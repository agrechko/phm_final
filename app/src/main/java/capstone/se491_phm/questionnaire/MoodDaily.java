package capstone.se491_phm.questionnaire;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;

/**
 * Created by Acer on 10/9/2016.
 */

public class MoodDaily extends Activity implements IQuestionnare {
    private SeekBar mSeekBarMood;
    private TextView mSelectedDailyMoodTextView;
    private List<String> availableMoods = new ArrayList<String>();
    //if daily mood average is below 1.5 or greater than 2.5 need to fire mood survey
    public static double dailyMoodAverage = 0;
    public static int numberOfEntries = 0;
    private int mSelectedMood = 2;
    private final String mHistoryPreference = "moodDailyHistory";
    private final int mHistoryDays = 365;
    public static Context targetContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        dailyMoodAverage = sharedPreferences.getFloat(Constants.DAILY_MOOD_AVERAGE,0);
        numberOfEntries = sharedPreferences.getInt(Constants.NUMBER_OF_ENTRIES,0);

        setContentView(R.layout.q_mood_daily);
        prepareQuestion();
        mSeekBarMood = (SeekBar)findViewById(R.id.seekBar_mood_selection);
        mSeekBarMood.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSelectedDailyMoodTextView.setText(availableMoods.get(progress));
                mSelectedMood = progress;
            }
        });
    }

    @Override
    public void prepareQuestion() {
        availableMoods.addAll(Constants.getDailyMood());
        mSelectedDailyMoodTextView = (TextView)findViewById(R.id.selectDailyMood);
        mSelectedDailyMoodTextView.setText(availableMoods.get(Constants.getDailyMoodDefault()));
    }

    @Override
    public JSONArray getQuestionHistory(int limit) {
        SharedPreferences sharedPreferences = null;
        if(getBaseContext() != null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        } else {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(targetContext);
        }
        String history = sharedPreferences.getString(mHistoryPreference,"");
        JSONArray jsonArray = null;
        if(!"".equals(history)){
            try {
                jsonArray = new JSONArray("["+history+"]");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(jsonArray == null){
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }

    @Override
    public boolean cleanUpStorage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(mHistoryPreference,"");
        editor.commit();
        return true;
    }

    public void moodDailyHome(View view) {
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveMoodDaily(View view) {
        numberOfEntries += 1;
        dailyMoodAverage = (dailyMoodAverage + (double) mSelectedMood)/(double) numberOfEntries;
        SharedPreferences sharedPreferences = null;
        if(getBaseContext() != null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        } else {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(targetContext);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Constants.DAILY_MOOD_AVERAGE,(float)dailyMoodAverage);
        editor.putInt(Constants.NUMBER_OF_ENTRIES,numberOfEntries);

        String history = sharedPreferences.getString(mHistoryPreference,"");
        if(!"".equals(history)){
            history = mSelectedMood+","+history;
        }else{
            history = Integer.toString(mSelectedMood);
        }
        String [] list = (history).split(",");
        if(list.length > mHistoryDays){
            history = history.substring(0,history.lastIndexOf(","));
        }

        editor.putString(mHistoryPreference,history);
        editor.commit();

        if(getBaseContext() != null) {
            moodDailyHome(view);
        }
    }
}
