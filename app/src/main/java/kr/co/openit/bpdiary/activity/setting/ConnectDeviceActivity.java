package kr.co.openit.bpdiary.activity.setting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Set;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.activity.mydevice.MyDeviceBPActivity;
import kr.co.openit.bpdiary.activity.mydevice.MyDeviceWeightScaleActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class ConnectDeviceActivity extends BaseActivity {

    /**
     * BluetoothAdapter
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * 요청 코드
     */
    private final int MY_DEVICE_GUIDE = 2000;

    private LinearLayout llEmptyView;

    /**
     * 혈압계 연결됨 표시 Layout
     */
    private LinearLayout llBPConnect;

    /**
     * 혈압계 Layout
     */
    private RelativeLayout rlBPDevice;

    /**
     * 체중계 TextView
     */
    private TextView tvBPDevice;

    /**
     * 광고 Layout
     */
    private LinearLayout llAds;

    /**
     * 체중계 Layout
     */
    private RelativeLayout rlWsDevice;

    /**
     * 체중계 연결
     */
    private LinearLayout llWsConnect;

    /**
     * 체중계명 TextView
     */
    private TextView tvWsDevice;

    /**
     * 혈압계 (선택 안 됨 표시)
     */
    private TextView tvBPDisconnect;

    /**
     * 체중계 선택안됨 TextView
     */
    private TextView tvWsDisconnect;

    /**
     * 혈당계 선택안됨 TextView
     */
    private TextView tvGlucoseDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);
        initToolbar(getString(R.string.setting_connect_device));

        AnalyticsUtil.sendScene(ConnectDeviceActivity.this, "3_M 기기 리스트");

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                                     .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                                                     .build();
        adView.loadAd(adRequest);

        setLayout();

        if (PreferenceUtil.getIsPayment(ConnectDeviceActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }
        // 혈압계 연결 클릭
        rlBPDevice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(ConnectDeviceActivity.this, MyDeviceBPActivity.class);
                    startActivityForResult(intent, MY_DEVICE_GUIDE);
                }
            }
        });

        // 체중계 연결 클릭
        rlWsDevice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(ConnectDeviceActivity.this, MyDeviceWeightScaleActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        doDiscovery();
    }

    /**
     * Layout 셋팅
     */

    private void setLayout() {
        tvBPDisconnect = (TextView)findViewById(R.id.tv_bp_disconnect);

        tvWsDisconnect = (TextView)findViewById(R.id.tv_weight_disconnect);

        tvGlucoseDisconnect = (TextView)findViewById(R.id.tv_glucose_disconnect);

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);

        llBPConnect = (LinearLayout)findViewById(R.id.ll_bp_connect);

        llWsConnect = (LinearLayout)findViewById(R.id.ll_weight_connect);

        tvBPDevice = (TextView)findViewById(R.id.tv_bp_device);

        tvWsDevice = (TextView)findViewById(R.id.tv_weight_device);

        rlBPDevice = (RelativeLayout)findViewById(R.id.rl_pressure);

        rlWsDevice = (RelativeLayout)findViewById(R.id.rl_weight);

        llAds = (LinearLayout)findViewById(R.id.ll_ads);

    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        llBPConnect.setVisibility(View.GONE);
        tvBPDisconnect.setVisibility(View.VISIBLE);
        llWsConnect.setVisibility(View.GONE);
        tvWsDisconnect.setVisibility(View.VISIBLE);

        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {

                for (BluetoothDevice device : pairedDevices) {

                    Log.d(TAG, "device : " + device.getName());

                    if (device.getName() != null) {
                        if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C)) {
                            setBpConnectVisible(ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C);
                        } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C)) {
                            setBpConnectVisible(ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C);
                        } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.PORA_D40B)) {
                            setBpConnectVisible(ManagerConstants.BloodPressureDevice.PORA_D40B);
                        } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.HEM_7081_IT)) {
                            setBpConnectVisible(ManagerConstants.BloodPressureDevice.HEM_7081_IT);
                        } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE)) {
                            setBpConnectVisible(ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE);
                        }

                        if (device.getName().startsWith(ManagerConstants.WeightScale.MI_SCALE)) {
                            llWsConnect.setVisibility(View.VISIBLE);
                            tvWsDevice.setText(ManagerConstants.WeightScale.MI_SCALE);
                            tvWsDisconnect.setVisibility(View.GONE);
                        }
                    }
                }
            } else {
                llBPConnect.setVisibility(View.GONE);
                tvBPDisconnect.setVisibility(View.VISIBLE);
                llWsConnect.setVisibility(View.GONE);
                tvWsDisconnect.setVisibility(View.VISIBLE);
            }
        }

        //        String strMyDevice = PreferenceUtil.getBPDeviceName(MyDeviceBPActivity.this);
        //
        //        if (strMyDevice != null && !strMyDevice.isEmpty()) {
        //
        //            if (ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C.startsWith(strMyDevice)) {
        //                txt767.setText(R.string.mydevice_pairing_txt_paired);
        //                txt767.setVisibility(View.VISIBLE);
        //                imgBt767.setVisibility(View.VISIBLE);
        //            } else if (ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C.startsWith(strMyDevice)) {
        //                txt851.setText(R.string.mydevice_pairing_txt_paired);
        //                txt851.setVisibility(View.VISIBLE);
        //                imgBt851.setVisibility(View.VISIBLE);
        //            } else if (ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE.startsWith(strMyDevice)) {
        //                txt651.setText(R.string.mydevice_pairing_txt_paired);
        //                txt651.setVisibility(View.VISIBLE);
        //                imgBt651.setVisibility(View.VISIBLE);
        //            } else if (ManagerConstants.BloodPressureDevice.PORA_D40B.startsWith(strMyDevice)) {
        //                txtFora.setText(R.string.mydevice_pairing_txt_paired);
        //                txtFora.setVisibility(View.VISIBLE);
        //                imgBtFora.setVisibility(View.VISIBLE);
        //            } else if (ManagerConstants.BloodPressureDevice.HEM_7081_IT.startsWith(strMyDevice)) {
        //                txtOmron.setText(R.string.mydevice_pairing_txt_paired);
        //                txtOmron.setVisibility(View.VISIBLE);
        //                imgBtOmron.setVisibility(View.VISIBLE);
        //            }
        //        }
    }

    /**
     * 혈압 연결시 Visible 처리
     *
     * @param device 연결된 장비
     */
    private void setBpConnectVisible(String device) {
        llBPConnect.setVisibility(View.VISIBLE);
        tvBPDisconnect.setVisibility(View.GONE);
        tvBPDevice.setText(device);
    }
}
