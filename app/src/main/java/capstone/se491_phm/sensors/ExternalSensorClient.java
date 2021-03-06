package capstone.se491_phm.sensors;


/**
 * https://developers.google.com/cloud-messaging/android/client
 */

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.common.Constants;

public class ExternalSensorClient extends IntentService {
    private Socket mSocket = null;
    private OutputStreamWriter osw = null;
    private InputStreamReader isR = null;
    private BufferedReader bfr = null;
    private LocalBroadcastManager broadcaster = null;
    static final public String externalSensorBroadcastIntent = "capstone.se491_phm.sensors.ExternalSensorActivity";
    static final public String externalSensorClientMessage = "capstone.se491_phm.sensors.ExternalSensorClient";

    public ExternalSensorClient() {
        super(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //add to running services so that it can be stopped on demand and not eat memory when not used
        MainActivity.runningServices.put(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME,intent);

        broadcaster = LocalBroadcastManager.getInstance(this);

        String serverResponse = sendToServer("Start Monitoring");
        String uiMessage = "success";
        if(serverResponse == null){
            uiMessage = "error";
            MainActivity.notifyUserNoServerConnection();
        }
        broadcast(externalSensorBroadcastIntent, uiMessage);
    }

    private String sendToServer(String message){
        String messageResponse = null;
        try {
            String serverIp = MainActivity.sharedPreferences.getString(Constants.SERVER_IP,null);
            if(serverIp != null) {
                /*mSocket = new Socket(serverIp, 12345);

                osw = new OutputStreamWriter(mSocket.getOutputStream());
                osw.write(message + "\n");
                Log.i(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME, "Success");
                osw.flush();

                isR = new InputStreamReader(mSocket.getInputStream());
                bfr = new BufferedReader(isR);
                messageResponse = bfr.readLine();*/
                String url = "http://"+serverIp+":3000?action=&id="+MainActivity.sharedPreferences.getString(Constants.USER_ID,"");
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                //add reuqest header
                con.setRequestMethod("POST");

                // Send post request
                con.setDoOutput(true);

                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if(responseCode == 200) {
                    messageResponse = response.toString();
                } else {
                    messageResponse = null;
                }
                Log.i(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME, messageResponse + "\nresponse code: "+responseCode);
            }
        } catch (Exception ex) {
            Log.i(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME, "Could not be sent");
        }
        return messageResponse;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSocket != null){
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.i(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME, "Trying to close socket but it is already closed");
            }
        }
        if (osw != null){
            try {
                osw.close();
            } catch (IOException e) {
                Log.i(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME, "Trying to close resource but it is already closed");
            }
        }
        if (isR != null){
            try {
                isR.close();
            } catch (IOException e) {
                Log.i(Constants.EXTERNAL_SENSOR_CLIENT_SERVICE_NAME, "Trying to close resource but it is already closed");
            }
        }
    }

    public void broadcast(String broadcastIntent, String message) {
        Intent intent = new Intent(broadcastIntent);
        if(message != null)
            intent.putExtra(externalSensorClientMessage, message);
        broadcaster.sendBroadcast(intent);
    }

    public void interruptSession(){

    }
}

