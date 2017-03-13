package capstone.se491_phm;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import capstone.se491_phm.questionnaire.MoodDaily;

import static org.junit.Assert.assertTrue;

/**
 * Created by Acer on 1/14/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MoodDailyTest {
    private Context context;
    private Context targetContext;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
        targetContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void saveDailyMood(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                MoodDaily moodDaily = new MoodDaily();
                moodDaily.targetContext = targetContext;
                int recordsBeforeSave = moodDaily.getQuestionHistory(0).length();
                moodDaily.saveMoodDaily(null);
                assertTrue(recordsBeforeSave+1 == moodDaily.getQuestionHistory(0).length());
            }
        });

    }
}
