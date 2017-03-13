package capstone.se491_phm;

import android.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import capstone.se491_phm.common.Constants;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

/**
 * Created by Acer on 1/14/2017.
 */

@RunWith(AndroidJUnit4.class)
public class AlarmTest {
    private Context context;
    private Context targetContext;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
        targetContext = InstrumentationRegistry.getTargetContext();
    }

    /**
     * Test to see if siren will play twice
     */
    @Test
    public void sirenTest(){
        Alarm.siren(targetContext);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            fail();
        }
        Alarm.siren(targetContext);
    }

    /**
     * Test sending sms with location coordinates.
     * *Important* - change the phone number to be the number of the device
     */
    @Test
    public void sendSms(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(targetContext).edit();
        editor.putString(Constants.EMERGENCY_CONTACT, "PHONE_NUMBER");
        editor.commit();
        Alarm.sendSMS(targetContext);

    }
}
