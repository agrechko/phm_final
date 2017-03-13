package capstone.se491_phm.webview;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;

/**
 * Created by Acer on 2/2/2017.
 */

public class TermsConditionsWebView extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        WebView webView = (WebView) findViewById(R.id.eula);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/legal.html");
    }

    public void acceptTerms(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        sharedPreferences.edit().putBoolean(Constants.EULA, true).commit();
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void declineTerms(View view) {
        this.finishAffinity();
    }
}
