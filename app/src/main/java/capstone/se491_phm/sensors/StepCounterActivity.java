package capstone.se491_phm.sensors;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;

/**
 * Created by Acer on 1/15/2017.
 */

public class StepCounterActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);

        StepCounter stepCounter = new StepCounter();
        if(!stepCounter.isInitialized()) {
            stepCounter.populateActivityMap(getBaseContext());
        }
        ((TextView)findViewById(R.id.dailyStepCount)).setText(Long.toString(stepCounter.getActivityDataGroup(Constants.ActivityGroup.DAILY.toString())));
        ((TextView)findViewById(R.id.weeklyStepInfo)).setText(Long.toString(stepCounter.getActivityDataGroup(Constants.ActivityGroup.WEEKLY.toString())));
        ((TextView)findViewById(R.id.lifeTimeStepInfo)).setText(Long.toString(stepCounter.getActivityDataGroup(Constants.ActivityGroup.TOTAL.toString())));
    }

    public void closeView(View view) {
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
