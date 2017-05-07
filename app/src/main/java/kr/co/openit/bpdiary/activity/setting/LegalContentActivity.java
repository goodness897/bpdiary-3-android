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
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class LegalContentActivity extends BaseActivity {

    private LinearLayout llEmptyView;

    private RelativeLayout rlSettingListBpDiary;

    private RelativeLayout rlSettingListPersonInfo;

    private RelativeLayout rlSettingListOpensourse;

    private LinearLayout llAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_content);

        initToolbar(getResources().getString(R.string.setting_legal_content));

        AnalyticsUtil.sendScene(LegalContentActivity.this, "3_M 법적내용");

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(LegalContentActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        rlSettingListBpDiary = (RelativeLayout)findViewById(R.id.llayout_setting_list_bpdiary);
        rlSettingListBpDiary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(LegalContentActivity.this, SettingTermsContentActivity.class);
                    intent.putExtra("webViewSeq", "1");
                    startActivity(intent);
                }
            }
        });
        rlSettingListPersonInfo = (RelativeLayout)findViewById(R.id.llayout_setting_list_person_info);
        rlSettingListPersonInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(LegalContentActivity.this, SettingTermsContentActivity.class);
                    intent.putExtra("webViewSeq", "2");
                    startActivity(intent);
                }

            }
        });
        rlSettingListOpensourse = (RelativeLayout)findViewById(R.id.llayout_setting_list_opensourse);
        rlSettingListOpensourse.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(LegalContentActivity.this, SettingTermsContentActivity.class);
                    intent.putExtra("webViewSeq", "3");
                    startActivity(intent);

                }

            }
        });

    }
}
