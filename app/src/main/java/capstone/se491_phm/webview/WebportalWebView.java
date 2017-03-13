package capstone.se491_phm.webview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.UUID;

import capstone.se491_phm.MainActivity;
import capstone.se491_phm.R;
import capstone.se491_phm.common.Constants;

/**
 * Created by Acer on 1/29/2017.
 */

public class WebportalWebView extends Activity {
    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webportal_view);

        webView = (WebView) findViewById(R.id.webportal_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("http://phm.oregonresearchmethod.org/phmTest/loginForm.php?devid="+MainActivity.sharedPreferences.getString(Constants.UNIQUE_ID,""));

        webView.setWebViewClient(new myWebViewClient());
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            //webView.goBack();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else {
            super.onBackPressed();
        }
    }

    public void webportalBackToHome(View view) {
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, MainActivity.class);
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
