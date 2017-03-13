package capstone.se491_phm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import capstone.se491_phm.jobs.DailyActivityMonitorJob;
import capstone.se491_phm.jobs.MoodDailyJob;
import capstone.se491_phm.jobs.MoodSurvey;
import capstone.se491_phm.jobs.WeeklyActivityMonitorJob;
import capstone.se491_phm.sensors.ExternalSensorClient;

/**
 * Created by Acer on 1/11/2017.
 */

@RunWith(AndroidJUnit4.class)
public class JobServicesTest {
    private Context context;
    private Context targetContext;

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
        targetContext = InstrumentationRegistry.getTargetContext();
    }

    /**
     * Run daily mood job
     * @throws Exception
     */
    @Test
    public void dailyMoodTest() throws Exception {
        Intent serviceIntent = new Intent(targetContext, MoodDailyJob.class);
        mServiceRule.startService(serviceIntent);
    }

    /**
     * Run activity daily job
     * @throws Exception
     */
    @Test
    public void dailyActivityTest() throws Exception {
        Intent serviceIntent = new Intent(targetContext, DailyActivityMonitorJob.class);
        mServiceRule.startService(serviceIntent);
    }

    /**
     * Run weekly mood job
     * @throws Exception
     */
    @Test
    public void weeklyMoodTest() throws Exception {
        Intent serviceIntent = new Intent(targetContext, MoodSurvey.class);
        mServiceRule.startService(serviceIntent);
    }

    /**
     * Run weekly activity job
     * @throws Exception
     */
    @Test
    public void weeklyActivityTest() throws Exception {
        Intent serviceIntent = new Intent(targetContext, WeeklyActivityMonitorJob.class);
        mServiceRule.startService(serviceIntent);
    }
}
