package kr.co.openit.bpdiary.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class DataReceiveActivity extends BaseActivity {

    private LinearLayout llEmptyView;

    private Button btnReceive;

    private LinearLayout llAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_receive);
        initToolbar(getString(R.string.setting_receive_data));

        AnalyticsUtil.sendScene(DataReceiveActivity.this, "3_M 데이터 받기");

        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(DataReceiveActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        btnReceive = (Button)findViewById(R.id.btn_receive);
        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(DataReceiveActivity.this, CsvOtherAppsInputListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
