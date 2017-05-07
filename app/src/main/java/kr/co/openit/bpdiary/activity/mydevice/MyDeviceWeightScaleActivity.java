package kr.co.openit.bpdiary.activity.mydevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.lang.reflect.Method;
import java.util.Set;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureReverseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.IntentData;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PermissionUtils;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class MyDeviceWeightScaleActivity extends NonMeasureReverseActivity {

    /**
     * Intent 요청 코드
     */
    private final int MY_DEVICE_GUIDE = 1000;

    private LinearLayout llEmptyView;

    /**
     * MiScale Layout
     */

    private RelativeLayout rlMiScale;

    /**
     * 연결 표시 Layout
     */
    private LinearLayout llMiScaleConnect;

    /**
     * 블루투스 Adapter
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * 페어링 해제
     */
    private boolean isRemovePairing = false;

    private CustomProgressDialog mProgress;

    private LinearLayout llAds;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_scale_select);

        AnalyticsUtil.sendScene(MyDeviceWeightScaleActivity.this, "3_M 기기 MIScale");

        /**
         * 광고
         */
        llAds = (LinearLayout)findViewById(R.id.ll_ads);
        AdView adView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                                     .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                                                     .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(MyDeviceWeightScaleActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        initToolbar(getString(R.string.common_txt_mydevice_weight));

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        rlMiScale = (RelativeLayout)findViewById(R.id.rl_xiaomi_mi_scale);
        llMiScaleConnect = (LinearLayout)findViewById(R.id.ll_xiaomi_mi_scale_connect);

        rlMiScale.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    if (checkPermission()) {
                        viewPopupBluetooth("lLayoutMiScale");
                    }
                }
            }
        });

        // 블루투스 활성화
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        PreferenceUtil.getAuthKey(MyDeviceWeightScaleActivity.this);
    }

    public void viewPopupBluetooth(final String strUrl) {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) { // 현재 블루투스 활성화 여부.
            DefaultDialog bluetoothCustomDialog = new DefaultDialog(MyDeviceWeightScaleActivity.this,
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

                                                                            if (mProgress == null) {
                                                                                mProgress =
                                                                                          new CustomProgressDialog(MyDeviceWeightScaleActivity.this);
                                                                                mProgress.setCancelable(false);
                                                                                mProgress.show();
                                                                            }

                                                                            final Handler mHandler = new Handler();
                                                                            mHandler.postDelayed(new Runnable() {

                                                                                @Override
                                                                                public void run() {
                                                                                    mHandler.post(new Runnable() {

                                                                                        @Override
                                                                                        public void run() {
                                                                                            mProgress.dismiss();
                                                                                            doDiscovery();
                                                                                        }
                                                                                    });

                                                                                    if ("lLayoutMiScale".equals(strUrl)) {
                                                                                        Intent intent =
                                                                                                      new Intent(MyDeviceWeightScaleActivity.this,
                                                                                                                 MyDeviceSearchWSDeviceActivity.class);
                                                                                        intent.putExtra(IntentData.DEVICE,
                                                                                                        ManagerConstants.WeightScale.MI_SCALE);
                                                                                        startActivityForResult(intent,
                                                                                                               MY_DEVICE_GUIDE);
                                                                                    } else {

                                                                                    }

                                                                                }
                                                                            }, 2500);

                                                                        }
                                                                    });

            bluetoothCustomDialog.show();

        } else {

            if ("lLayoutMiScale".equals(strUrl)) {
                Intent intent = new Intent(MyDeviceWeightScaleActivity.this, MyDeviceSearchWSDeviceActivity.class);
                intent.putExtra(IntentData.DEVICE, ManagerConstants.WeightScale.MI_SCALE);
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

        boolean isPermission = PermissionUtils.checkPermissionAccessCoarseLocation(MyDeviceWeightScaleActivity.this);

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

        llMiScaleConnect.setVisibility(View.GONE);

        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {

                for (BluetoothDevice device : pairedDevices) {

                    if (device.getName() != null) {
                        if (device.getName().startsWith(ManagerConstants.WeightScale.MI_SCALE)) {
                            llMiScaleConnect.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }

        //        Set<BluetoothDevice> pairedDevices = null;
        //
        //        if (bluetoothAdapter != null) {
        //            pairedDevices = bluetoothAdapter.getBondedDevices();
        //        }
        //
        //        if (null != pairedDevices) {
        //            for (BluetoothDevice device : pairedDevices) {
        //                if (device.getName() != null) {
        //                    isRemovePairing = true;
        //                    if (!device.getName().startsWith(ManagerConstants.WeightScale.MI_SCALE)) {
        //                    } else {
        //                        unpairDevice(device);
        //                    }
        //                }
        //            }
        //        }

        //        String strMyDevice = PreferenceUtil.getWSDeviceName(MyDeviceWeightScaleActivity.this);
        //
        //        if (strMyDevice != null && !strMyDevice.isEmpty()) {
        //            if (ManagerConstants.WeightScale.MI_SCALE.startsWith(strMyDevice)) {
        //                tvMiScale.setText(R.string.mydevice_pairing_txt_paired);
        //                llMiScaleConnect.setVisibility(View.VISIBLE);
        //            }
        //        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {

            Method m = device.getClass().getMethod("removeBond", (Class[])null);
            m.invoke(device, (Object[])null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_DEVICE_GUIDE) {
            if (resultCode == RESULT_OK) {
                //                Intent intent = new Intent();
                //                intent.putExtra("device", "ws");
                //                setResult(RESULT_OK, intent);
                //                finish();
            }
        }
    }
}
