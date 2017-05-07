package kr.co.openit.bpdiary.activity.setting;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Browser;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class OnlineShopActivity extends NonMeasureActivity {

    //    private ViewPager vpIntro;
    //
    //    private LinearLayout llPagerIndicator;
    //
    //    private int dotsCount;
    //
    //    private ImageView[] dots;
    //
    //    private OnlineShopPagerAdapter mAdapter;

    private LinearLayout llEmptyView;

    private WebView webView;

    private CustomProgressDialog mProgress;

    private LinearLayout llIsNetworkFalse;

    private LinearLayout llRetry;

    private Handler mHandler = new Handler();



    //    private int[] mImageResources = {R.drawable.ic_ex_menu_set_target,
    //                                     R.drawable.ic_ex_menu_set_target,
    //                                     R.drawable.ic_ex_menu_set_target};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_shop);
        initToolbar(getString(R.string.setting_online_shop));

        AnalyticsUtil.sendScene(OnlineShopActivity.this, "3_M 온라인샵");

        context = OnlineShopActivity.this;

        //        vpIntro = (ViewPager)findViewById(R.id.vp_introduction);
        //        llPagerIndicator = (LinearLayout)findViewById(R.id.ll_dot);
        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        webView = (WebView)findViewById(R.id.market_webview_openmall);
        llIsNetworkFalse = (LinearLayout)findViewById(R.id.ll_is_network_false);
        llRetry = (LinearLayout)findViewById(R.id.ll_network_try_again);

        if (BPDiaryApplication.isNetworkState(context)) {
            llIsNetworkFalse.setVisibility(View.GONE);
            setPropertyWebView();

        } else {
            llIsNetworkFalse.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
        }

        llRetry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    showLodingProgress();

                    if (BPDiaryApplication.isNetworkState(context)) {
                        hideLodingProgress();
                        llIsNetworkFalse.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        setPropertyWebView();

                    } else {
                        llIsNetworkFalse.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);
                        mHandler.postDelayed(mMyTask, 1500);

                    }
                }
            }
        });

        //        mAdapter = new OnlineShopPagerAdapter(OnlineShopActivity.this, mImageResources);
        //        vpIntro.setAdapter(mAdapter);
        //        vpIntro.setCurrentItem(0);
        //        vpIntro.setOnPageChangeListener(this);
        //        setUiPageViewController();

    }

    private Runnable mMyTask = new Runnable() {

        @Override
        public void run() {
            hideLodingProgress();
        }
    };


    /**
     * WebView 관련 셋팅
     */
    private void setPropertyWebView() {

        String url = "https://store.cufit.net";

        Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
        additionalHttpHeaders.put("Accept-Language", "ko");
        additionalHttpHeaders.put("appid", "bpdiary");
        additionalHttpHeaders.put("uuid", PreferenceUtil.getEncEmail(context).toLowerCase());

        webView.setWebViewClient(new MarketWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(false);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.loadUrl(url, additionalHttpHeaders);

    }

    //    private void setUiPageViewController() {
    //
    //        dotsCount = mAdapter.getCount();
    //        dots = new ImageView[dotsCount];
    //
    //        for (int i = 0; i < dotsCount; i++) {
    //            dots[i] = new ImageView(this);
    //            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
    //
    //            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
    //                                                                             LinearLayout.LayoutParams.WRAP_CONTENT);
    //
    //            params.setMargins(16, 0, 16, 0);
    //
    //            llPagerIndicator.addView(dots[i], params);
    //        }
    //
    //        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    //
    //    }
    //
    //    @Override
    //    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    //
    //    }
    //
    //    @Override
    //    public void onPageSelected(int position) {
    //        for (int i = 0; i < dotsCount; i++) {
    //            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
    //        }
    //
    //        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    //    }
    //
    //    @Override
    //    public void onPageScrollStateChanged(int state) {
    //
    //    }
    //
    //    @Override
    //    public void onClick(View view) {
    //
    //    }

    class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int progress) {
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

            DefaultDialog defaultDialog = new DefaultDialog(context, "", message, "CANCEL", "OK", new IDefaultDialog() {

                @Override
                public void onCancel() {

                }

                @Override
                public void onConfirm() {
                    result.confirm();
                }
            });
            defaultDialog.show();
            //            new AlertDialog.Builder(.setTitle("")
            //                                                  .setMessage(message)
            //                                                  .setPositiveButton(android.R.string.ok,
            //                                                                     new AlertDialog.OnClickListener() {
            //
            //                                                                         public void onClick(DialogInterface dialog,
            //                                                                                             int which) {
            //                                                                             result.confirm();
            //                                                                         }
            //                                                                     })
            //                                                  .setCancelable(false)
            //                                                  .create()
            //                                                  .show();

            //            new AlertDialog.Builder(context).setTitle("")
            //                    .setMessage(message)
            //                    .setPositiveButton(android.R.string.ok,
            //                            new AlertDialog.OnClickListener() {
            //
            //                                @Override
            //                                public void onClick(DialogInterface dialog,
            //                                                    int which) {
            //                                    result.confirm();
            //                                }
            //                            })
            //                    .setCancelable(false)
            //                    .create()
            //                    .show();

            // showToast(message);
            // result.confirm();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            DefaultDialog defaultDialog = new DefaultDialog(context, "", message, "CANCEL", "OK", new IDefaultDialog() {

                @Override
                public void onCancel() {
                    result.cancel();
                }

                @Override
                public void onConfirm() {
                    result.confirm();
                }
            });
            defaultDialog.show();

            return true;
        }
    }

    public void showAlert(String message,
                          String positiveButton,
                          DialogInterface.OnClickListener positiveListener,
                          String negativeButton,
                          DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message);
        alert.setPositiveButton(positiveButton, positiveListener);
        alert.setNegativeButton(negativeButton, negativeListener);
        alert.show();
    }

    class MarketWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            Log.d("================url::", url);
            if ((url.startsWith("http://") || url.startsWith("https://")) && url.endsWith(".apk")) {
                downloadFile(url);
                return super.shouldOverrideUrlLoading(view, url);
            } else if ((url.startsWith("http://") || url.startsWith("https://"))
                       && (url.contains("market.android.com") || url.contains("m.ahnlab.com/kr/site/download"))) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(intent);
                    return true;
                } catch (ActivityNotFoundException e) {
                    return false;
                }
            } else if (url.startsWith("http://") || url.startsWith("https://")) {
                view.loadUrl(url);
                return true;
            } else if (url != null && (url.contains("vguard") || url.contains("droidxantivirus")
                                       || url.contains("smhyundaiansimclick://")
                                       || url.contains("smshinhanansimclick://")
                                       || url.contains("smshinhancardusim://")
                                       || url.contains("smartwall://")
                                       || url.contains("appfree://")
                                       || url.contains("v3mobile")
                                       || url.endsWith(".apk")
                                       || url.contains("market://")
                                       || url.contains("ansimclick")
                                       || url.contains("market://details?id=com.shcard.smartpay")
                                       || url.contains("shinhan-sr-ansimclick://"))) {

                return callApp(url);
            } else if (url.startsWith("smartxpay-transfer://")) {
                boolean isatallFlag = isPackageInstalled(getApplicationContext(), "kr.co.uplus.ecredit");
                if (isatallFlag) {
                    boolean override = false;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());

                    try {
                        startActivity(intent);
                        override = true;
                    } catch (ActivityNotFoundException ex) {
                    }
                    return override;
                } else {
                    showAlert("확인버튼을 누르시면 구글플레이로 이동합니다.", "확인", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                                       Uri.parse(("market://details?id=kr.co.uplus.ecredit")));
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    }, "취소", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    return true;
                }
            } else if (url.startsWith("ispmobile://")) {
                boolean isatallFlag = isPackageInstalled(getApplicationContext(), "kvp.jjy.MispAndroid320");
                if (isatallFlag) {
                    boolean override = false;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());

                    try {
                        startActivity(intent);
                        override = true;
                    } catch (ActivityNotFoundException ex) {
                    }
                    return override;
                } else {
                    showAlert("확인버튼을 누르시면 구글플레이로 이동합니다.", "확인", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            view.loadUrl("http://mobile.vpay.co.kr/jsp/MISP/andown.jsp");
                        }
                    }, "취소", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    return true;
                }
            } else if (url.startsWith("paypin://")) {

                boolean isatallFlag = isPackageInstalled(getApplicationContext(), "com.skp.android.paypin");
                if (isatallFlag) {
                    boolean override = false;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());

                    try {
                        startActivity(intent);
                        override = true;
                    } catch (ActivityNotFoundException ex) {
                    }
                    return override;
                } else {
                    Intent intent =
                                  new Intent(Intent.ACTION_VIEW,
                                             Uri.parse(("market://details?id=com.skp.android.paypin&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5za3AuYW5kcm9pZC5wYXlwaW4iXQ..")));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
            } else if (url.startsWith("lguthepay://")) {

                boolean isatallFlag = isPackageInstalled(getApplicationContext(), "com.lguplus.paynow");
                if (isatallFlag) {
                    boolean override = false;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());

                    try {
                        startActivity(intent);
                        override = true;
                    } catch (ActivityNotFoundException ex) {
                    }
                    return override;
                } else {
                    Intent intent =
                                  new Intent(Intent.ACTION_VIEW, Uri.parse(("market://details?id=com.lguplus.paynow")));
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }
            } else {

                return callApp(url);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (mProgress == null) {
                mProgress = new CustomProgressDialog(context);
                mProgress.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
        }

        // 외부 앱 호출
        public boolean callApp(String url) {

            Intent intent = null;
            // 인텐트 정합성 체크 : 2014 .01추가
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException ex) {
                Log.e("Browser", "Bad URI " + url + ":" + ex.getMessage());
                return false;
            }

            try {
                boolean retval = true;
                //chrome 버젼 방식 : 2014.01 추가
                if (url.startsWith("intent")) { // chrome 버젼 방식
                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null) {

                        String packagename = intent.getPackage();

                        if (packagename != null) {

                            Uri uri = Uri.parse("market://search?q=pname:" + packagename);
                            intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            retval = true;
                        }
                    } else {

                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setComponent(null);
                        try {

                            if (startActivityIfNeeded(intent, -1)) {
                                retval = true;
                            }
                        } catch (ActivityNotFoundException ex) {
                            retval = false;
                        }
                    }
                } else { // 구 방식
                    Uri uri = Uri.parse(url);
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    retval = true;
                }

                return retval;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            int keyCode = event.getKeyCode();
            if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT) && webView.canGoBack()) {
                webView.goBack();
                return true;
            } else if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) && webView.canGoForward()) {
                webView.goForward();
                return true;
            }
            return false;
        }

        private void downloadFile(String mUrl) {
            new DownloadFileTask().execute(mUrl);
        }

        // AsyncTask<Params,Progress,Result>
        private class DownloadFileTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... urls) {
                URL myFileUrl = null;
                try {
                    myFileUrl = new URL(urls[0]);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();

                    // 다운 받는 파일의 경로는 sdcard/ 에 저장되며 sdcard에 접근하려면 uses-permission에
                    // android.permission.WRITE_EXTERNAL_STORAGE을 추가해야만 가능.
                    String mPath = "sdcard/v3mobile.apk";
                    FileOutputStream fos;
                    File f = new File(mPath);
                    if (f.createNewFile()) {
                        fos = new FileOutputStream(mPath);
                        int read;
                        while ((read = is.read()) != -1) {
                            fos.write(read);
                        }
                        fos.close();
                    }

                    return "v3mobile.apk";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "";
                }
            }

            @Override
            protected void onPostExecute(String filename) {
                if (!"".equals(filename)) {
                    Toast.makeText(context, "download complete", Toast.LENGTH_SHORT).show();

                    // 안드로이드 패키지 매니저를 사용한 어플리케이션 설치.
                    File apkFile = new File(Environment.getExternalStorageDirectory() + "/" + filename);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    startActivity(intent);
                }
            }
        }

    }

    //    private class MarketWebViewClient extends WebViewClient {
    //
    //        @Override
    //        public void onPageStarted(WebView view, String url, Bitmap favicon) {
    //            super.onPageStarted(view, url, favicon);
    //            if (mProgress == null) {
    //                mProgress = new CustomDialogProgress(getActivity());
    //                mProgress.setCancelable(false);
    //            }
    //            mProgress.show();
    //        }
    //
    //        @Override
    //        public void onPageFinished(WebView view, String url) {
    //            super.onPageFinished(view, url);
    //            if (mProgress != null && mProgress.isShowing()) {
    //                mProgress.hide();
    //            }
    //        }
    //
    //        @Override
    //        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    //            super.onReceivedError(view, errorCode, description, failingUrl);
    //            if (mProgress != null && mProgress.isShowing()) {
    //                mProgress.hide();
    //            }
    //        }
    //
    //        @Override
    //        public boolean shouldOverrideUrlLoading(WebView view, String url) {
    //            if (url.startsWith("ispmobile")) {
    //                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    //                getActivity().startActivity(intent);
    //            } else if (url.startsWith("market")) {
    //                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    //                getActivity().startActivity(intent);
    //            } else {
    //                view.loadUrl(url);
    //            }
    //
    //            return true;
    //        }
    //    }

    @Override
    public void onStop() {
        super.onStop();
        //        if (mProgress != null) {
        //            mProgress.dismiss();
        //            mProgress = null;
        //        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    public static boolean isPackageInstalled(Context ctx, String pkgName) {

        try {
            ctx.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void onNewIntent(Intent intent) {
        Log.e("===============", "onNewIntent!!");
        if (intent != null) {
            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                Log.e("================uri", uri.toString());
                if (String.valueOf(uri).startsWith("ISP 커스텀스키마를 넣어주세요")) { // ISP 커스텀스키마를 넣어주세요
                    String result = uri.getQueryParameter("result");
                    if ("success".equals(result)) {
                        webView.loadUrl("javascript:doPostProcess();");
                    } else if ("cancel".equals(result)) {
                        webView.loadUrl("javascript:doCancelProcess();");
                    } else {
                        webView.loadUrl("javascript:doNoteProcess();");
                    }
                } else if (String.valueOf(uri).startsWith("계좌이체 커스텀스키마를 넣어주세요")) { // 계좌이체 커스텀스키마를 넣어주세요
                    // 계좌이체는 WebView가 아무일을 하지 않아도 됨
                } else if (String.valueOf(uri).startsWith("paypin 커스텀스키마를 넣어주세요")) { // paypin 커스텀스키마를 넣어주세요
                    webView.loadUrl("javascript:doPostProcess();");
                } else if (String.valueOf(uri).startsWith("paynow 커스텀스키마를 넣어주세요")) { // paynow 커스텀스키마를 넣어주세요
                    // paynow는 WebView가 아무일을 하지 않아도 됨
                }
            }
        }
    }

    public boolean backWebView() {

        boolean isBack = false;

        if (webView != null) {
            isBack = webView.canGoBack();
            webView.goBack();
        }

        return isBack;
    }
}
