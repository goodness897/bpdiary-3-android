package kr.co.openit.bpdiary.activity.mydevice;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.adapter.common.CommonListAdapter;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.CountDownTimerUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class MyDeviceSearchWSDeviceActivity extends NonMeasureActivity {

    /**
     * 생성자
     */
    public MyDeviceSearchWSDeviceActivity() {
        // default constructor
    }

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private List<Map<String, String>> listResultListData;

    private String strAddress;

    private int nPosition;

    private boolean isConnecting = false;

    private DeviceListAdapter arrAdapterDevices;

    private ImageButton iBtnBack;

    private CustomProgressDialog mProgress;

    private LinearLayout llEmptyView;

    /**
     * 다시 검색하기
     */
    private Button btnReSearch;

    /**
     * 블루투스 목록 리스트뷰
     */
    private ListView listDevices;

    /**
     * BluetoothAdapter
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * 내 기기
     */
    private String myDevice;

    /**
     * 페어링 해제
     */
    private boolean isRemovePairing = false;

    public static MyDeviceSearchWSDeviceActivity myDeviceSearchWSDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        myDevice = intent.getStringExtra(ManagerConstants.IntentData.DEVICE);

        setContentView(R.layout.activity_my_device_search_wsdevice);

        AnalyticsUtil.sendScene(MyDeviceSearchWSDeviceActivity.this, "MyDeviceSearchWSDeviceActivity");
        myDeviceSearchWSDevice = MyDeviceSearchWSDeviceActivity.this;

        initToolbar(getString(R.string.setting_connect_weight));
        /**
         * 화면 초기 설정
         */
        setActivityLayout();

        /**
         * IntentFilter 설정
         */
        setIntentFilter();

        /**
         * Bluetooth adapter 설정
         */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        doDiscovery();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        timerUtil.cancel();

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }

    /**
     * 화면 초기 설정 메서드
     */
    protected void setActivityLayout() {

        listResultListData = new ArrayList<Map<String, String>>();
        arrAdapterDevices = new DeviceListAdapter(MyDeviceSearchWSDeviceActivity.this, listResultListData);

        // Find and set up the ListView for newly discovered devices
        listDevices = (ListView) findViewById(R.id.lv_search_device);
        listDevices.setAdapter(arrAdapterDevices);
        listDevices.setOnItemClickListener(mDeviceClickListener);

        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);

        btnReSearch = (Button) findViewById(R.id.btn_re_search);
        btnReSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (mProgress == null) {
                        mProgress = new CustomProgressDialog(MyDeviceSearchWSDeviceActivity.this);
                        mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                timerUtil.cancel();
                                finish();
                            }
                        });
                    }
                    mProgress.show();
                    timerUtil.start();
                    bluetoothAdapter.startDiscovery();
                }

            }
        });

    }

    /**
     * IntentFilter 설정 메서드
     */
    protected void setIntentFilter() {

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);

    }

    /**
     * Activity 뒤로가기 메서드
     */
    protected void doBackActivity() {

        PreferenceUtil.setMacAddr(MyDeviceSearchWSDeviceActivity.this, strAddress);

        if (listResultListData.size() > nPosition) {
            listResultListData.get(nPosition).put("state", getString(R.string.mydevice_pairing_txt_paired));

            arrAdapterDevices.notifyDataSetChanged();
        }

        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (mProgress != null && mProgress.isShowing()) {
                            mProgress.hide();
                        }
                        setResult(RESULT_OK);
                        finish();

                    }
                });
            }
        }, 1300);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Toast.makeText(MyDeviceSearchWSDeviceActivity.this,
                getResources().getString(R.string.device_disconnect_and_connect_again),
                Toast.LENGTH_SHORT)
                .show();
        timerUtil.start();
        if (mProgress == null) {
            mProgress = new CustomProgressDialog(MyDeviceSearchWSDeviceActivity.this);
            mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    timerUtil.cancel();
                    finish();
                }
            });
        }

        mProgress.show();

        listResultListData = new ArrayList<Map<String, String>>();

        Set<BluetoothDevice> pairedDevices = null;

        if (bluetoothAdapter != null) {
            pairedDevices = bluetoothAdapter.getBondedDevices();
        }

        if (null != pairedDevices) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName() != null) {
                    if (!device.getName().startsWith(myDevice)) {
                        isRemovePairing = true;
                        if (ManagerConstants.WeightScale.MI_SCALE.startsWith(myDevice)) {
                        } else {
                            if (device.getName().startsWith(ManagerConstants.WeightScale.MI_SCALE)) {
                                unpairDevice(device);
                            }
                        }
                    }
                }
            }
        }

        if (bluetoothAdapter != null) {

            // If we're already discovering, stop it
            if (bluetoothAdapter.isDiscovering()) {

                bluetoothAdapter.cancelDiscovery();
            }

            if (isRemovePairing) {
                // nothing
            } else {
                // Request discover from BluetoothAdapter
                bluetoothAdapter.startDiscovery();
            }
        }
    }

    private void pairDevice(BluetoothDevice device) {
        try {

            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {

            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 리스트뷰 아답터
     */
    private class DeviceListAdapter extends CommonListAdapter {

        public DeviceListAdapter(Context context, List<Map<String, String>> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Map<String, String> data = getItem(position);

            ViewHolder holder;

            if (convertView == null) {

                convertView = getLayoutInflater().inflate(R.layout.layout_my_device_search_list, parent, false);

                holder = new MyDeviceSearchWSDeviceActivity.DeviceListAdapter.ViewHolder();
                holder.llState = (LinearLayout) convertView.findViewById(R.id.ll_ble_state);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_mydevice_name);
                holder.tvState = (TextView) convertView.findViewById(R.id.tv_state);

                convertView.setTag(holder);
            } else {

                holder = (MyDeviceSearchWSDeviceActivity.DeviceListAdapter.ViewHolder) convertView.getTag();
            }

            holder.tvName.setText(data.get("name"));
            if (data.get("state").equals(getResources().getString(R.string.mydevice_pairing_txt_pairing))) {
                holder.tvState.setText(data.get("state"));
                holder.llState.setVisibility(View.VISIBLE);
            } else {
                holder.llState.setVisibility(View.GONE);
            }

            return convertView;
        }

        class ViewHolder {

            TextView tvName;

            TextView tvState;

            LinearLayout llState;

        }
    }

    // The on-click listener for all devices in the ListViews
    private final AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            isConnecting = true;

            if (mProgress == null) {
                mProgress = new CustomProgressDialog(MyDeviceSearchWSDeviceActivity.this);
                mProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
            }

            mProgress.show();

            final Map pMap = (Map) parent.getItemAtPosition(position);

            strAddress = pMap.get("address").toString();

            nPosition = position;

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strAddress);

            if ((getResources().getString(R.string.mydevice_pairing_txt_paired)).equals(pMap.get("state").toString())) {
                // nothing
            } else {

                ((Map) parent.getItemAtPosition(position)).put("state",
                        getResources().getString(R.string.mydevice_pairing_txt_pairing));

                arrAdapterDevices.notifyDataSetChanged();

                pairDevice(device);
            }

            bluetoothAdapter.cancelDiscovery();

        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                timerUtil.cancel();
                //                pbSearching.setVisibility(View.VISIBLE);
                //                txtSearching.setVisibility(View.VISIBLE);

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                    if (device.getName() != null) {

                        if (device.getName().startsWith(myDevice)) {
                            Log.i("1234", "1234 : " + device.getName());
                            Map<String, String> pMap = new HashMap<String, String>();
                            pMap.put("name", device.getName());
                            pMap.put("address", device.getAddress());
                            pMap.put("state", getResources().getString(R.string.mydevice_pairing_txt_pairing));

                            listResultListData.add(pMap);

                            arrAdapterDevices.notifyDataSetChanged();

                            bluetoothAdapter.cancelDiscovery();

                            pairDevice(device);

                        }
                    }
                }

                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                timerUtil.cancel();
                if (arrAdapterDevices.getCount() == 0) {

                    if (!isConnecting) {
                        if (mProgress != null && mProgress.isShowing()) {
                            mProgress.hide();
                        }
                    }

                    DefaultDialog defaultDialog = new DefaultDialog(MyDeviceSearchWSDeviceActivity.this,
                            getString(R.string.setting_connect_weight_fail),
                            getString(R.string.mydevice_pairing_txt_ws_nodevices),
                            "",
                            getString(R.string.common_txt_confirm),
                            new IDefaultDialog() {

                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onConfirm() {

                                }
                            });
                    defaultDialog.show();
                }

            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                timerUtil.cancel();
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                        BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {

                    PreferenceUtil.setWSDeviceName(context, myDevice);

                    doBackActivity();

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {

                    if (isRemovePairing) {
                        timerUtil.start();
                        bluetoothAdapter.startDiscovery();
                    }

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDING) {
                    timerUtil.cancel();
                    Toast.makeText(context, R.string.mydevice_pairing_txt_fail, Toast.LENGTH_SHORT).show();
                    PreferenceUtil.setWSDeviceName(context, "");

                    if (listResultListData != null && listResultListData.size() > 0) {
                        Map<String, String> pMap = listResultListData.get(0);
                        pMap.put("state", "");
                        arrAdapterDevices.notifyDataSetChanged();
                    }

                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.hide();
                    }

                } else if (state == BluetoothDevice.ERROR) {
                    timerUtil.cancel();
                    if (mProgress != null && mProgress.isShowing()) {
                        mProgress.hide();
                    }

                } else {
                    timerUtil.cancel();
                    //nothing
                }
            }
        }
    };

    private CountDownTimerUtil timerUtil = new CountDownTimerUtil(20000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }

            if (android.os.Build.VERSION.SDK_INT >= 23) {
                DefaultDialog defaultDialog = new DefaultDialog(MyDeviceSearchWSDeviceActivity.this,
                        getString(R.string.setting_connect_weight_fail),
                        getString(R.string.device_pairing_connect_fail),
                        "",
                        getString(R.string.common_txt_confirm),
                        new IDefaultDialog() {

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onConfirm() {

                            }
                        });
                if (!((Activity) MyDeviceSearchWSDeviceActivity.this).isFinishing()) {
                    defaultDialog.show();
                }
            } else {
                DefaultDialog defaultDialog = new DefaultDialog(MyDeviceSearchWSDeviceActivity.this,
                        getString(R.string.setting_connect_weight_fail),
                        getString(R.string.mydevice_pairing_txt_ws_nodevices),
                        "",
                        getString(R.string.common_txt_confirm),
                        new IDefaultDialog() {

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onConfirm() {

                            }
                        });
                if (!((Activity) MyDeviceSearchWSDeviceActivity.this).isFinishing()) {
                    defaultDialog.show();
                }
            }
        }
    };
}
