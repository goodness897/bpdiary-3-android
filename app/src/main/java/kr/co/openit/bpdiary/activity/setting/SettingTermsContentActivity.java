package kr.co.openit.bpdiary.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class SettingTermsContentActivity extends NonMeasureActivity {

    private LinearLayout llEmptyView;

    private WebView webView;

    private CustomProgressDialog mProgress;

    private Activity settingTermsContentActivity;

    private LinearLayout llIsNetworkFalse;

    private LinearLayout llAds;

    private LinearLayout llRetry;

    private String url = "";

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_terms_content);

        settingTermsContentActivity = SettingTermsContentActivity.this;
        AnalyticsUtil.sendScene(settingTermsContentActivity, "SettingTermsContentActivity");

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                                     .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                                                     .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(SettingTermsContentActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        String seq = intent.getStringExtra("webViewSeq");

        if ("1".equals(seq)) {
            initToolbar(getString(R.string.setting_terms_of_use));
            url = "https://help.bpdiary.net/legal/terms";
            AnalyticsUtil.sendScene(SettingTermsContentActivity.this, "3_M 법적내용 약관");
        } else if ("2".equals(seq)) {
            initToolbar(getString(R.string.setting_privacy_treatment_policy));
            url = "https://help.bpdiary.net/legal/privacy";
            AnalyticsUtil.sendScene(SettingTermsContentActivity.this, "3_M 법적내용 개인정보");
        } else if ("3".equals(seq)) {
            initToolbar(getString(R.string.setting_opensource_license));
            url = "https://help.bpdiary.net/legal/license";
            AnalyticsUtil.sendScene(SettingTermsContentActivity.this, "3_M 법적내용 오픈소스");
        }

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        webView = (WebView)findViewById(R.id.market_webview_openmall);
        llIsNetworkFalse = (LinearLayout)findViewById(R.id.ll_is_network_false);
        llRetry = (LinearLayout)findViewById(R.id.ll_network_try_again);

        if (BPDiaryApplication.isNetworkState(SettingTermsContentActivity.this)) {
            webView.setVisibility(View.VISIBLE);
            llIsNetworkFalse.setVisibility(View.GONE);
            webView.getSettings().setJavaScriptEnabled(true);
            Map<String, String> additionalHttpHaders = new HashMap<String, String>();
            additionalHttpHaders.put(ManagerConstants.HTTPHeader.ACCEPT_LANGUAGE,
                                     PreferenceUtil.getLanguage(settingTermsContentActivity));
            additionalHttpHaders.put(ManagerConstants.HTTPHeader.ACCEPT_ENCODING, "gzip");
            webView.loadUrl(url, additionalHttpHaders);
            webView.setWebViewClient(new TermsWebViewClient());
        } else {
            llIsNetworkFalse.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }

        llRetry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!ManagerUtil.isClicking()) {

                    showLodingProgress();

                    if (BPDiaryApplication.isNetworkState(SettingTermsContentActivity.this)) {

                        hideLodingProgress();
                        webView.setVisibility(View.VISIBLE);
                        llIsNetworkFalse.setVisibility(View.GONE);
                        webView.getSettings().setJavaScriptEnabled(true);
                        Map<String, String> additionalHttpHaders = new HashMap<String, String>();
                        additionalHttpHaders.put(ManagerConstants.HTTPHeader.ACCEPT_LANGUAGE,
                                PreferenceUtil.getLanguage(settingTermsContentActivity));
                        additionalHttpHaders.put(ManagerConstants.HTTPHeader.ACCEPT_ENCODING, "gzip");
                        webView.loadUrl(url, additionalHttpHaders);
                        webView.setWebViewClient(new TermsWebViewClient());
                    } else {
                        llIsNetworkFalse.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);
                        mHandler.postDelayed(mMyTask, 1500); // 3초후에 실행
                    }
                }
            }
        });

    }

    private Runnable mMyTask = new Runnable() {

        @Override
        public void run() {
            hideLodingProgress();
        }
    };

    private class TermsWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mProgress == null) {
                mProgress = new CustomProgressDialog(settingTermsContentActivity);
                mProgress.setCancelable(false);
            }
            if (!isFinishing()) {
                mProgress.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.hide();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.hide();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("ispmobile")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else if (url.startsWith("market")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else {
                view.loadUrl(url);
            }

            return true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

}
