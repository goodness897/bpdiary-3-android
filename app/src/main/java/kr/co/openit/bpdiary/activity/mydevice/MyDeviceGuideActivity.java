package kr.co.openit.bpdiary.activity.mydevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

public class MyDeviceGuideActivity extends BaseActivity {

    /**
     * Device 정보
     */
    private String device = ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C;

    private LinearLayout llEmptyView;

    /**
     * OMRON-HEM-708-IT
     */
    private LinearLayout llOmron708;

    /**
     * A&D UA767PBT-C
     */
    private LinearLayout llUa767;

    /**
     * A&D UA851PBT-C
     */
    private LinearLayout llUa851;

    /**
     * A&D UA651BLE
     */
    private LinearLayout llUa651;

    /**
     * Foracare Fora D40b
     */
    private LinearLayout llForaD40b;

    /**
     * 다음 Button
     */
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device_guide);

        Intent intent = getIntent();
        device = intent.getStringExtra(ManagerConstants.IntentData.DEVICE);

        initToolbar(device);

        setLayout();

        if (ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C.equals(device)) {
            AnalyticsUtil.sendScene(MyDeviceGuideActivity.this, "3_M 기기 A&D767");
            llUa851.setVisibility(View.GONE);
            llUa651.setVisibility(View.GONE);
            llUa767.setVisibility(View.VISIBLE);
            llForaD40b.setVisibility(View.GONE);
            llOmron708.setVisibility(View.GONE);
        } else if (ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C.equals(device)) {
            AnalyticsUtil.sendScene(MyDeviceGuideActivity.this, "3_M 기기 A&D851");
            llUa851.setVisibility(View.VISIBLE);
            llUa651.setVisibility(View.GONE);
            llUa767.setVisibility(View.GONE);
            llForaD40b.setVisibility(View.GONE);
            llOmron708.setVisibility(View.GONE);
        } else if (ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE.equals(device)) {
            AnalyticsUtil.sendScene(MyDeviceGuideActivity.this, "3_M 기기 A&D651");
            llUa851.setVisibility(View.GONE);
            llUa651.setVisibility(View.VISIBLE);
            llUa767.setVisibility(View.GONE);
            llForaD40b.setVisibility(View.GONE);
            llOmron708.setVisibility(View.GONE);
        } else if (ManagerConstants.BloodPressureDevice.HEM_7081_IT.equals(device)) {
            AnalyticsUtil.sendScene(MyDeviceGuideActivity.this, "3_M 기기 Omron");
            llUa851.setVisibility(View.GONE);
            llUa651.setVisibility(View.GONE);
            llUa767.setVisibility(View.GONE);
            llForaD40b.setVisibility(View.GONE);
            llOmron708.setVisibility(View.VISIBLE);
        } else if (ManagerConstants.BloodPressureDevice.PORA_D40B.equals(device)) {
            AnalyticsUtil.sendScene(MyDeviceGuideActivity.this, "3_M 기기 FORAD40");
            llUa851.setVisibility(View.GONE);
            llUa651.setVisibility(View.GONE);
            llUa767.setVisibility(View.GONE);
            llForaD40b.setVisibility(View.VISIBLE);
            llOmron708.setVisibility(View.GONE);
        }

    }

    private void setLayout() {

        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        llOmron708 = (LinearLayout) findViewById(R.id.ll_omron_hem_708);
        llUa651 = (LinearLayout) findViewById(R.id.ll_ua_651_ble);
        llUa767 = (LinearLayout) findViewById(R.id.ll_ua_767);
        llUa851 = (LinearLayout) findViewById(R.id.ll_ua_851);
        llForaD40b = (LinearLayout) findViewById(R.id.ll_fora_d40b);
        btnNext = (Button) findViewById(R.id.btn_next);

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(MyDeviceGuideActivity.this, MyDeviceSearchBPDeviceActivity.class);
                    intent.putExtra(ManagerConstants.IntentData.DEVICE, device);
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.MY_DEVICE_SETUP);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ManagerConstants.ActivityResultCode.MY_DEVICE_SETUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
