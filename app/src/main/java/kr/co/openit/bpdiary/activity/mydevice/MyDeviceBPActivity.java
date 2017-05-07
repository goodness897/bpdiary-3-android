package kr.co.openit.bpdiary.activity.mydevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Set;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PermissionUtils;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class MyDeviceBPActivity extends BaseActivity implements View.OnClickListener {

    private final int MY_DEVICE_GUIDE = 1000;

    private BluetoothAdapter bluetoothAdapter;

    private LinearLayout llEmptyView;

    /**
     * UA-651BLE Layout
     */
    private RelativeLayout rlUa651Ble;

    /**
     * UA-851PBT-C Layout
     */
    private RelativeLayout rlUa851;

    /**
     * UA-767PBT-C Layout
     */
    private RelativeLayout rlUa767;

    /**
     * Fora D40b Layout
     */
    private RelativeLayout rlForaD40b;

    /**
     * HEM-708-IT Layout
     */
    private RelativeLayout rlHem708;

    /**
     * UA-651BLE 블루투스 연결 확인 Layout
     */
    private LinearLayout llUa651Ble;

    /**
     * UA-851PBT-C 블루투스 연결 확인 Layout
     */
    private LinearLayout llUa851;

    /**
     * UA-767PBT-C 블루투스 연결 확인 Layout
     */
    private LinearLayout llUa767;

    /**
     * Fora D40b 블루투스 연결 확인 Layout
     */
    private LinearLayout llForaD40b;

    /**
     * HEM-708-IT 블루투스 연결 확인 Layout
     */
    private LinearLayout llHem708;

    private LinearLayout llAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device_blood_pressure);

        initToolbar(getString(R.string.common_txt_mydevice));

        AnalyticsUtil.sendScene(MyDeviceBPActivity.this, "3_M 기기 혈압계 목록");

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(MyDeviceBPActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        setLayout();

        // 블루투스 활성화
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public void onResume() {
        super.onResume();
        doDiscovery();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }


    private void setLayout() {
        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        rlUa651Ble = (RelativeLayout) findViewById(R.id.rl_ua_651);
        rlUa851 = (RelativeLayout) findViewById(R.id.rl_ua_851);
        rlUa767 = (RelativeLayout) findViewById(R.id.rl_ua_767);
        rlForaD40b = (RelativeLayout) findViewById(R.id.rl_fora_d40b);
        rlHem708 = (RelativeLayout) findViewById(R.id.rl_omron_hem_708);

        llUa651Ble = (LinearLayout) findViewById(R.id.ll_ua_651_connect);
        llUa851 = (LinearLayout) findViewById(R.id.ll_ua_851_connect);
        llUa767 = (LinearLayout) findViewById(R.id.ll_ua_767_connect);
        llForaD40b = (LinearLayout) findViewById(R.id.ll_fora_d40b_connect);
        llHem708 = (LinearLayout) findViewById(R.id.ll_omron_hem_708_connect);

        rlUa651Ble.setOnClickListener(this);
        rlUa851.setOnClickListener(this);
        rlUa767.setOnClickListener(this);
        rlForaD40b.setOnClickListener(this);
        rlHem708.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {

            switch (v.getId()) {
                case R.id.rl_ua_651:
                    if (checkPermission()) {
                        viewPopupBluetooth("rl_ua651");
                    }
                    break;
                case R.id.rl_ua_851:
                    if (checkPermission()) {
                        viewPopupBluetooth("rl_ua851");
                    }
                    break;
                case R.id.rl_ua_767:
                    if (checkPermission()) {
                        viewPopupBluetooth("rl_ua767");
                    }
                    break;
                case R.id.rl_fora_d40b:
                    if (checkPermission()) {
                        viewPopupBluetooth("rl_fora_d40b");
                    }
                    break;
                case R.id.rl_omron_hem_708:
                    if (checkPermission()) {
                        viewPopupBluetooth("rl_omron_hem_708");
                    }
                    break;

            }
        }

    }

    public void viewPopupBluetooth(final String strUrl) {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) { // 현재 블루투스 활성화 여부.
            DefaultDialog bluetoothCustomDialog = new DefaultDialog(MyDeviceBPActivity.this,
                    getString(R.string.bluetooth_dialog_title),
                    getString(R.string.bluetooth_dialog_text),
                    getString(R.string.bluetooth_dialog_button_no),
                    getString(R.string.bluetooth_dialog_button_yes),
                    new IDefaultDialog() {

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onConfirm() {
                            bluetoothAdapter.enable();

                            //                    if (mProgress == null) {
                            //                        mProgress = new CustomDialogProgress(MyDeviceBPActivity.this);
                            //                        mProgress.setCancelable(false);
                            //                        mProgress.show();
                            //                    }

                            final Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    mHandler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            //                                    mProgress.dismiss();
                                            doDiscovery();
                                        }
                                    });

                                    if ("rl_ua651".equals(strUrl)) {
                                        Intent intent =
                                                new Intent(MyDeviceBPActivity.this,
                                                        MyDeviceGuideActivity.class);
                                        intent.putExtra(ManagerConstants.IntentData.DEVICE,
                                                ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE);
                                        startActivityForResult(intent,
                                                MY_DEVICE_GUIDE);
                                    } else if ("rl_ua851".equals(strUrl)) {
                                        Intent intent =
                                                new Intent(MyDeviceBPActivity.this,
                                                        MyDeviceGuideActivity.class);
                                        intent.putExtra(ManagerConstants.IntentData.DEVICE,
                                                ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C);
                                        startActivityForResult(intent,
                                                MY_DEVICE_GUIDE);
                                    } else if ("rl_ua767".equals(strUrl)) {
                                        Intent intent =
                                                new Intent(MyDeviceBPActivity.this,
                                                        MyDeviceGuideActivity.class);
                                        intent.putExtra(ManagerConstants.IntentData.DEVICE,
                                                ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C);
                                        startActivityForResult(intent,
                                                MY_DEVICE_GUIDE);
                                    } else if ("rl_fora_d40b".equals(strUrl)) {
                                        Intent intent =
                                                new Intent(MyDeviceBPActivity.this,
                                                        MyDeviceGuideActivity.class);
                                        intent.putExtra(ManagerConstants.IntentData.DEVICE,
                                                ManagerConstants.BloodPressureDevice.PORA_D40B);
                                        startActivityForResult(intent,
                                                MY_DEVICE_GUIDE);
                                    } else if ("rl_omron_hem_708".equals(strUrl)) {
                                        Intent intent =
                                                new Intent(MyDeviceBPActivity.this,
                                                        MyDeviceGuideActivity.class);
                                        intent.putExtra(ManagerConstants.IntentData.DEVICE,
                                                ManagerConstants.BloodPressureDevice.HEM_7081_IT);
                                        startActivityForResult(intent,
                                                MY_DEVICE_GUIDE);
                                    } else {
                                        //nothing
                                    }

                                }
                            }, 2500);

                        }
                    });

            bluetoothCustomDialog.show();

        } else {

            if ("rl_ua651".equals(strUrl)) {
                Intent intent = new Intent(MyDeviceBPActivity.this, MyDeviceGuideActivity.class);
                intent.putExtra(ManagerConstants.IntentData.DEVICE,
                        ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE);
                startActivityForResult(intent, MY_DEVICE_GUIDE);
            } else if ("rl_ua851".equals(strUrl)) {
                Intent intent = new Intent(MyDeviceBPActivity.this, MyDeviceGuideActivity.class);
                intent.putExtra(ManagerConstants.IntentData.DEVICE,
                        ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C);
                startActivityForResult(intent, MY_DEVICE_GUIDE);
            } else if ("rl_ua767".equals(strUrl)) {
                Intent intent = new Intent(MyDeviceBPActivity.this, MyDeviceGuideActivity.class);
                intent.putExtra(ManagerConstants.IntentData.DEVICE,
                        ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C);
                startActivityForResult(intent, MY_DEVICE_GUIDE);
            } else if ("rl_fora_d40b".equals(strUrl)) {
                Intent intent = new Intent(MyDeviceBPActivity.this, MyDeviceGuideActivity.class);
                intent.putExtra(ManagerConstants.IntentData.DEVICE, ManagerConstants.BloodPressureDevice.PORA_D40B);
                startActivityForResult(intent, MY_DEVICE_GUIDE);
            } else if ("rl_omron_hem_708".equals(strUrl)) {
                Intent intent = new Intent(MyDeviceBPActivity.this, MyDeviceGuideActivity.class);
                intent.putExtra(ManagerConstants.IntentData.DEVICE, ManagerConstants.BloodPressureDevice.HEM_7081_IT);
                startActivityForResult(intent, MY_DEVICE_GUIDE);
            } else {
                //nothing
            }
        }
    }

    /**
     * 권한 확인
     */
    private boolean checkPermission() {

        boolean isPermission = PermissionUtils.checkPermissionAccessCoarseLocation(MyDeviceBPActivity.this);

        if (!isPermission) {
            /*
             * ActivityCompat.requestPermissions(getActivity(), new
             * String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
             * PermissionRequestCode.PERMISSION_REQUEST_CHECK_MY_DEVICE_BP);
             */
        }

        return isPermission;
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        llUa651Ble.setVisibility(View.GONE);
        llUa851.setVisibility(View.GONE);
        llUa767.setVisibility(View.GONE);
        llForaD40b.setVisibility(View.GONE);
        llHem708.setVisibility(View.GONE);

        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {

                for (BluetoothDevice device : pairedDevices) {

                    Log.d(TAG, "device : " + device.getName());

                    if (device.getName() != null) {
                        if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C)) {
                            llUa767.setVisibility(View.VISIBLE);
                        } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C)) {
                            llUa851.setVisibility(View.VISIBLE);
                        } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.PORA_D40B)) {
                            llForaD40b.setVisibility(View.VISIBLE);
                        } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.HEM_7081_IT)) {
                            llHem708.setVisibility(View.VISIBLE);
                        } else if (device.getName()
                                .startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE)) {
                            llUa651Ble.setVisibility(View.VISIBLE);
                        }
                    }
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_DEVICE_GUIDE) {
            if (resultCode == RESULT_OK) {
//                Intent intent = new Intent();
//                intent.putExtra("device", "bp");
//                setResult(RESULT_OK, intent);
//                finish();
            }
        }
    }
}
