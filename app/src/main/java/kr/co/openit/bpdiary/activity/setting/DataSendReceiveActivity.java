package kr.co.openit.bpdiary.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class DataSendReceiveActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llEmptyView;

    /**
     * 데이터 보내기 Layout
     */
    private RelativeLayout rlSendData;

    /**
     * 데이터 받기 Layout
     */
    private RelativeLayout rlReceiveData;

    private LinearLayout llAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_send_receive);

        initToolbar(getString(R.string.setting_data_send_receive));

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(DataSendReceiveActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        setLayout();

    }

    private void setLayout() {

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        rlSendData = (RelativeLayout)findViewById(R.id.rl_send_data);
        rlReceiveData = (RelativeLayout)findViewById(R.id.rl_receive_data);

        rlSendData.setOnClickListener(this);
        rlReceiveData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {
            Intent intent;
            switch (v.getId()) {

                case R.id.rl_send_data:
                    intent = new Intent(DataSendReceiveActivity.this, DataSendActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_receive_data:
                    intent = new Intent(DataSendReceiveActivity.this, DataReceiveActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
