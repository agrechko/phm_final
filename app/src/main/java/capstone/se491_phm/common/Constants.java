package capstone.se491_phm.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Acer on 10/21/2016.
 */

public class Constants {
    public static enum ActivityGroup{
        DAILY,
        WEEKLY,
        TOTAL;
    }

    private static List<String> dailyMoods = new ArrayList<>();
    public static List<String> getDailyMood() {
        if (dailyMoods.isEmpty()) {
            dailyMoods.add("Depressed");//0
            dailyMoods.add("Irritated");//1
            dailyMoods.add("Normal");//2
            dailyMoods.add("Hyper");//3
            dailyMoods.add("Mania");//4
        }
        return new ArrayList<>(dailyMoods);
    }

    /**
     * Returs the default value to display for daily mood slider
     * @return
     */
    public static int getDailyMoodDefault() {
        return 2;
    }

    public static final String SERVER_IP = "serverIp";
    public static final String EXTERNAL_SENSOR_CLIENT_SERVICE_NAME = "ExternalSensorClient";
    public static final String REGISTRATION_TOKEN = "registrationToken";
    public static final String EMERGENCY_CONTACT = "emergencyContact";
    public static final String WAITING_FOR_FALL_ACK = "waitingForFallAck";
    public static final String DAILY_MOOD_AVERAGE = "dailyMoodAverage";
    public static final String NUMBER_OF_ENTRIES = "numberOfEntries";
    public static final String USER_ID = "userid";
    public static final String UNIQUE_ID = "uniqueId";
    public static final String EULA = "eula";
}
