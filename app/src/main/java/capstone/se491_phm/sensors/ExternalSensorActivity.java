package capstone.se491_phm.sensors;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;

import java.net.InetAddress;
import java.net.UnknownHostException;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;
import capstone.se491_phm.webview.GraphsWebView;

/**
 * Created by Acer on 11/4/2016.
 */

public class ExternalSensorActivity extends Activity {
    BroadcastReceiver receiver = null;

    private LocalBroadcastManager broadcaster = null;
    static final public String externalSensorActivityBroadcastIntent = "capstone.se491_phm.sensors.ExternalSensorActivity";
    static final public String externalSensorActivityMessage = "capstone.se491_phm.sensors.ExternalSensorActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getBaseContext();
        broadcaster = LocalBroadcastManager.getInstance(this);
        //cancel notification
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

        setContentView(R.layout.ext_monitoring_session);

        (findViewById(R.id.textViewSuccess)).setVisibility(View.INVISIBLE);

        EditText serverHostIp = ((EditText) findViewById(R.id.serverHostIp));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        serverHostIp.setText(sharedPreferences.getString(Constants.SERVER_IP,"raspberrypi"));

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String broadcastMessage = intent.getStringExtra(ExternalSensorClient.externalSensorClientMessage);
                if(broadcastMessage == null){
                    broadcastMessage = intent.getStringExtra(externalSensorActivityMessage);
                }

                if("startservice".equalsIgnoreCase(broadcastMessage)){
                    //start external sensor client
                    Intent tempintent = new Intent(context, ExternalSensorClient.class);
                    startService(tempintent);
                }else {
                    hideLoading(null);
                }
                if("error".equalsIgnoreCase(broadcastMessage)){
                    connectionIssueNotification();
                } else if("success".equalsIgnoreCase(broadcastMessage)){
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(3);
                    setContentView(R.layout.activity_graphs);
                    Intent intenttemp = new Intent(context, GraphsWebView.class);
                    startActivity(intenttemp);
                    finish();
                } else if("testsuccess".equalsIgnoreCase(broadcastMessage)){
                    (findViewById(R.id.textViewSuccess)).setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void startSensorClient(){
        showLoading(null);
        (findViewById(R.id.connectionErrorMessage)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.btnStart)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.testConnection)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.serverHostIp)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.textViewSuccess)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.openGraphView)).setVisibility(View.INVISIBLE);
        //start external sensor client
        testConnection(true);
    }

    public void showLoading(View view){
        (findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
    }

    public void hideLoading(View view){
        (findViewById(R.id.progressBar1)).setVisibility(View.INVISIBLE);
    }

    public void connectionIssueNotification(){
        (findViewById(R.id.connectionErrorMessage)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnStart)).setVisibility(View.VISIBLE);
        (findViewById(R.id.btnClose)).setVisibility(View.VISIBLE);
        (findViewById(R.id.testConnection)).setVisibility(View.VISIBLE);
        (findViewById(R.id.serverHostIp)).setVisibility(View.VISIBLE);
        (findViewById(R.id.openGraphView)).setVisibility(View.VISIBLE);
    }

    public void backToMain(View view) {
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void openGraphView(View view) {
        setContentView(R.layout.activity_graphs);
        Intent intent = new Intent(this, GraphsWebView.class);
        startActivity(intent);
        finish();
    }

    public void retryConnection(View view) {
        startSensorClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(ExternalSensorClient.externalSensorBroadcastIntent)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    public void testConnection(View view) {
        testConnection(false);
    }

    private void testConnection(final boolean startService){
        if(!"".equals(((EditText) findViewById(R.id.serverHostIp)).getText().toString())) {
            showLoading(null);

            (findViewById(R.id.connectionErrorMessage)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.textViewSuccess)).setVisibility(View.INVISIBLE);
            new Thread() {
                public void run() {
                    String hostname = ((EditText) findViewById(R.id.serverHostIp)).getText().toString();
                    String serverIp = null;
                    boolean foundHost = false;
                    try {
                        InetAddress ipaddress = InetAddress.getByName(hostname);
                        serverIp = ipaddress.getHostAddress();
                        foundHost = true;
                    } catch (UnknownHostException e) {
                        System.out.println("Could not find IP address for: " + hostname);
                    } catch (Exception e) {
                        System.out.println("Issue while testing connection: " + e.getMessage());
                    }
                    if (foundHost) {
                        if(startService){
                            PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                            sharedPreferences.edit().putString(Constants.SERVER_IP,serverIp).commit();
                            broadcast(externalSensorActivityBroadcastIntent, externalSensorActivityMessage, "startservice");
                        } else {
                            broadcast(externalSensorActivityBroadcastIntent, externalSensorActivityMessage, "testsuccess");
                        }
                    } else {
                        broadcast(externalSensorActivityBroadcastIntent, externalSensorActivityMessage, "error");
                    }
                }
            }.start();
        } else {
            hideLoading(null);
            connectionIssueNotification();
        }
    }

    public void broadcast(String broadcastToIntent, String fromMessage, String message) {
        Intent intent = new Intent(broadcastToIntent);
        if(message != null)
            intent.putExtra(fromMessage, message);
        broadcaster.sendBroadcast(intent);
    }
}
