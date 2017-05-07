package kr.co.openit.bpdiary.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.IntentData;

public class WebViewActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        webView = (WebView)findViewById(R.id.webview);

        Intent intent = getIntent();
        String url = intent.getStringExtra(IntentData.URL);

        webView.setWebViewClient(new WebViewClient());
        WebSettings set = webView.getSettings();
        set.setJavaScriptEnabled(true);
        set.setBuiltInZoomControls(true);
        webView.loadUrl(url);
    }
}
