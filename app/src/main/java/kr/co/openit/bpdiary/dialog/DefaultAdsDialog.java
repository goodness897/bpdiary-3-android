package kr.co.openit.bpdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.interfaces.IDefaultAdsDialog;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class DefaultAdsDialog extends Dialog {

    /**
     * 내용
     */
    private WebView wvAds;

    private String strSeq;

    private String strViewTerm;

    private String strType;

    private String strUrl;

    private LinearLayout btnClose;

    private LinearLayout btnCloseDate;

    private TextView tvCloseDate;

    private boolean checkFlag;

    private IDefaultAdsDialog iDefaultDialog;

    private Context context;

    public DefaultAdsDialog(Context context,
                            String strSeq,
                            String strViewTerm,
                            String strType,
                            String strUrl,
                            IDefaultAdsDialog iDefaultDialog) {
        super(context, R.style.MyDialog);

        this.context = context;
        this.strSeq = strSeq;
        this.strViewTerm = strViewTerm;
        this.strType = strType;
        this.strUrl = strUrl;
        this.iDefaultDialog = iDefaultDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.CENTER;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_default_ads);

        // 화면 초기 설정
        setDialogLayout();
    }

    /**
     * 화면 초기 설정
     */
    private void setDialogLayout() {
        wvAds = (WebView)findViewById(R.id.wv_ads);
        btnClose = (LinearLayout)findViewById(R.id.btn_close);
        btnCloseDate = (LinearLayout)findViewById(R.id.btn_close_date);
        tvCloseDate = (TextView)findViewById(R.id.tv_close_date);

        tvCloseDate.setText(context.getString(R.string.dont_show_anytime, strViewTerm));

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tvCloseDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iDefaultDialog.onConfirm(strSeq, strViewTerm);
                dismiss();
            }
        });

        wvAds.getSettings().setJavaScriptEnabled(true);
        wvAds.setWebViewClient(new NewsWebViewClient());
        wvAds.loadUrl("https://dev.bpdiary.net/app/event/" + strSeq);
        //        wvAds.loadData(strContent, "text/html; charset=UTF-8", null);
        wvAds.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wvAds.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        iDefaultDialog.onClick(strType);
                        dismiss();
                        break;
                }
                return false;
            }
        });
    }

    private class NewsWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //            if (url.startsWith("ispmobile")) {
            //                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //                startActivity(intent);
            //            } else if (url.startsWith("market")) {
            //                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //                startActivity(intent);
            //            } else {
            //            }
            view.loadUrl(url);

            return true;
        }
    }
}
