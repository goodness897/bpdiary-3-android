package kr.co.openit.bpdiary.dialog;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.adapter.common.CommonListAdapter;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.interfaces.PairingListInterface;

public class PairingListCustomDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener  {

    /**
     * 리스트 뷰
     */
    private ListView listView;

    private ListViewAdapter listViewAdapter;

    /**
     * 확인
     */
    private Button btnConfirm;

    /**
     * 인터페이스
     */
    private final PairingListInterface iDefaultDialog;

    private final Context context;

    private final ArrayList<Map<String, String>> listData = new ArrayList<Map<String, String>>();

    private int prePosition = -1;

    public static PairingListCustomDialog pairingListCustomDialog;

    private Set<BluetoothDevice> pairedDevices;

    /**
     * BluetoothAdapter
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * 페어링 해제
     */
    private boolean isRemovePairing = false;

    /**
     * 생성자
     *
     * @param context
     */
    public PairingListCustomDialog(Context context, Set<BluetoothDevice> pairedDevices, PairingListInterface defaultConfig) {
        super(context, R.style.MyDialog);

        this.iDefaultDialog = defaultConfig;
        this.context = context;
        this.pairedDevices = pairedDevices;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.CENTER;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog_pairing_list);

        pairingListCustomDialog = PairingListCustomDialog.this;

        listView = (ListView)findViewById(R.id.lv_pairing_list);
        btnConfirm = (Button)findViewById(R.id.common_default_dialog_txt_confirm);

        listView.setOnItemClickListener(PairingListCustomDialog.this);
        btnConfirm.setOnClickListener(PairingListCustomDialog.this);

        /**
         * Bluetooth adapter 설정
         */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        for (BluetoothDevice device : pairedDevices) {
            if(device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("deviceName", device.getName());
                listData.add(map);
            } else if(device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("deviceName", device.getName());
                listData.add(map);
            } else if(device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("deviceName", device.getName());
                listData.add(map);
            } else if(device.getName().startsWith(ManagerConstants.BloodPressureDevice.HEM_7081_IT)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("deviceName", device.getName());
                listData.add(map);
            } else if(device.getName().startsWith(ManagerConstants.BloodPressureDevice.PORA_D40B)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("deviceName", device.getName());
                listData.add(map);
            }
        }

        listViewAdapter = new ListViewAdapter(context, listData);
        listView.setAdapter(listViewAdapter);

        if(listData.size() > 0) {
            listData.get(0).put("check","true");
            prePosition = 0;
            listViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {

        dismiss();

        switch (v.getId()) {
            case R.id.common_default_dialog_txt_confirm:
                String strPairing = null;
                if(prePosition != -1){
                    strPairing = listData.get(prePosition).get("deviceName");
                    doDiscovery(strPairing);
                }
                iDefaultDialog.onConfirm(strPairing);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(prePosition != position){

            if(prePosition != -1){
                listData.get(prePosition).put("check","false");
            }

            listData.get(position).put("check","true");
            prePosition = position;
            listViewAdapter.notifyDataSetChanged();
        }
    }


    private class ListViewAdapter extends CommonListAdapter {
        public ListViewAdapter(Context context, ArrayList<Map<String, String>> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_pairing_list, null);

                holder = new ViewHolder();

                holder.txtPairing = (TextView)convertView.findViewById(R.id.row_pairing_txt);

                holder.rBtnPairing = (RadioButton)convertView.findViewById(R.id.row_pairing_rbtn);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder)convertView.getTag();

            }

            String strPairing = listData.get(position).get("deviceName");

            holder.txtPairing.setText(strPairing);

            String strCheck = listData.get(position).get("check");
            if("true".equals(strCheck)){
                holder.rBtnPairing.setChecked(true);
            }else{
                holder.rBtnPairing.setChecked(false);
            }

            return convertView;

        }

        class ViewHolder {

            TextView txtPairing;

            RadioButton rBtnPairing;
        }
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery(String myDevice) {

        Set<BluetoothDevice> pairedDevices = null;

        if (bluetoothAdapter != null) {
            pairedDevices = bluetoothAdapter.getBondedDevices();
        }

        if (null != pairedDevices) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName() != null) {
                    if (!device.getName().startsWith(myDevice)) {
                        isRemovePairing = true;
                        if(ManagerConstants.WeightScale.MI_SCALE.startsWith(myDevice)) {
                        } else {
                            if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE)) {
                                unpairDevice(device);
                            } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C)) {
                                unpairDevice(device);
                            } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C)) {
                                unpairDevice(device);
                            } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.PORA_D40B)) {
                                unpairDevice(device);
                            } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.HEM_7081_IT)) {
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

    private void unpairDevice(BluetoothDevice device) {
        try {

            Method m = device.getClass().getMethod("removeBond", (Class[])null);
            m.invoke(device, (Object[])null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
