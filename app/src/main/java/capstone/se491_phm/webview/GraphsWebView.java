package capstone.se491_phm.webview;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;
import capstone.se491_phm.sensors.ExternalSensorActivity;

/**
 * Created by Acer on 1/18/2017.
 */

public class GraphsWebView extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        WebView webView = (WebView) findViewById(R.id.graphsWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://phm.oregonresearchmethod.org/phmTest/graph-cnt.php?iid="+MainActivity.sharedPreferences.getString(Constants.USER_ID,""));

        webView.setWebViewClient(new myWebViewClient());
    }

    public void stopMonitoringSession(View view) {
        sendStopCommand();
    }

    private void sendStopCommand(){
        new Thread() {
            public void run() {
                String messageResponse = null;
                try {
                    String serverIp = MainActivity.sharedPreferences.getString(Constants.SERVER_IP,null);
                    if(serverIp != null) {
                        /*Socket mSocket = new Socket(serverIp, 12345);

                        OutputStreamWriter osw = new OutputStreamWriter(mSocket.getOutputStream());
                        osw.write("Stop\n");
                        Log.i("GRAPHS WEB VIEW", "Success");
                        osw.flush();

                        InputStreamReader isR = new InputStreamReader(mSocket.getInputStream());
                        BufferedReader bfr = new BufferedReader(isR);
                        messageResponse = bfr.readLine();*/
                        String url = "http://"+serverIp+":3000?action=stop&id="+MainActivity.sharedPreferences.getString(Constants.USER_ID,"");
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
                    }
                } catch (Exception ex) {
                    Log.i("GRAPHS WEB VIEW", "Could not be sent");
                }
            }
        }.start();
    }

    public void backToMain(View view) {
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void showExternalSensorView(View view) {
        setContentView(R.layout.ext_monitoring_session);
        Intent intent = new Intent(this, ExternalSensorActivity.class);
        startActivity(intent);
        finish();
    }

    private class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
