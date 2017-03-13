package capstone.se491_phm.common.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import capstone.se491_phm.webview.WebViewActivity;


/**
 * Created by Tahani on 10/22/16.
 */

public class BackgroundWorker extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;

    private static final String TAG = "BackgroundWorker";
    public BackgroundWorker(Context ctx){
        context= ctx;
    }


    public String getResult()
    {
        try {
            return this.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String data = null;
    private TestTaskItf mInter = null;// for tests only

    public BackgroundWorker(Context context, TestTaskItf inter) {
        super();
        this.context = context;
        this.mInter = inter;
    }


    @Override
    protected String doInBackground(String... params) {
        String type=params[0];

        String login_URL= "http://phm.oregonresearchmethod.org/login.php";
        String register_URL= "http://phm.oregonresearchmethod.org/register.php";
        String save_URL= "http://phm.oregonresearchmethod.org/gcm.php";

        String getUserID_URL= "http://phm.oregonresearchmethod.org/phmTest/retriveUserID.php";

        String getToken_URL= "http://phm.oregonresearchmethod.org/getToken.php";
        String getTokenByID_URL= "http://phm.oregonresearchmethod.org/getTokenByID.php";

        String addAbnormality_URL= "http://phm.oregonresearchmethod.org/phmTest/addAbnormality.php";
        String getActivityConfig_URL="http://phm.oregonresearchmethod.org/phmTest/getActivityConfig.php";

        if (type.equals("login"))
        {
            try{
                String email=params[1];
                String psw=params[2];

                String post_data= URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                        +URLEncoder.encode("psw","UTF-8")+"="+URLEncoder.encode(psw,"UTF-8");

                return postData(login_URL, post_data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(type.equals("register"))
        {
             try{
                String email=params[1];
                String psw=params[2];
                String name=params[3];

                String post_data= URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                        +URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                        +URLEncoder.encode("psw","UTF-8")+"="+URLEncoder.encode(psw,"UTF-8");

                return postData(register_URL, post_data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (type.equals("save") || type.equals("update"))
        {
            try{
                String deviceid=params[1];
                String regToken=params[2];

                String post_data= URLEncoder.encode("deviceid","UTF-8")+"="+URLEncoder.encode(deviceid,"UTF-8")+"&"
                        +URLEncoder.encode("regToken","UTF-8")+"="+URLEncoder.encode(regToken,"UTF-8")+"&"
                        +URLEncoder.encode("type","UTF-8")+"="+URLEncoder.encode(type,"UTF-8");

                return postData(save_URL, post_data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("getToken"))
        {
            try{
                String deviceid=params[1];

                String post_data= URLEncoder.encode("deviceid","UTF-8")+"="+URLEncoder.encode(deviceid,"UTF-8");

                return postData(getToken_URL, post_data);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (type.equals("getUserID"))
        {
            try{
                String deviceid=params[1];

                String post_data= URLEncoder.encode("deviceid","UTF-8")+"="+URLEncoder.encode(deviceid,"UTF-8");

                return postData(getUserID_URL, post_data);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (type.equals("getTokenByID"))
        {
            try{
                String id=params[1];

                String post_data= URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");

                return postData(getTokenByID_URL, post_data);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (type.equals("addAbnormality"))
        {
            try{
                String id=params[1];
                String vital=params[2];
                String description=params[3];

                String post_data= URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8")+"&"+
                        URLEncoder.encode("vital","UTF-8")+"="+URLEncoder.encode(vital,"UTF-8")+"&"+
                        URLEncoder.encode("description","UTF-8")+"="+URLEncoder.encode(description,"UTF-8");

                return postData(addAbnormality_URL, post_data);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if (type.equals("getActivityConfig"))
        {
            try{
                String id=params[1];

                String post_data= URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");

                return postData(getActivityConfig_URL, post_data);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private String postData(String postUrl, String post_data){
        try{
            URL url= new URL(postUrl);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream=httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            String line="";

            while((line=bufferedReader.readLine())!=null)
            {
                result+=line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPreExecute(){

    }



    @Override
    protected void onPostExecute(String result) {

        data=result;
        if(this.mInter != null){ // aka test mode
            this.mInter.onDone(data); // tell to unitest that we finished
        }

        /*if (result.equals("login success") || result.equals("registration successful")) {
            alertDialog.setMessage(result);

            alertDialog.show();

            alertDialog.getCurrentFocus();

            if (result.equals("login success")) {
                Intent i = new Intent(context.getApplicationContext(),
                        WebViewActivity.class);
                context.startActivity(i);
            }
            if (result.equals("registration successful")) {
                Intent r = new Intent(context.getApplicationContext(),
                        LoginActivity.class);
                context.startActivity(r);
            }


        } else {
            if (result.equals("save successful"))
                Log.i(TAG, "GCM registration token saved successfully.");
            else if (result.equals("get data failed"))
                Log.i(TAG, "Error occurred getting the GCM registration info. "+ result);
            else if (result.equals("The email account is already exist")) {
                alertDialog.setMessage(result);
                alertDialog.show();
                alertDialog.getCurrentFocus();
            }
            else
                Log.i(TAG, result);

        }*/
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }


}
