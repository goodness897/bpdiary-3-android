package kr.co.openit.bpdiary.common.controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.common.constants.BleConstants;
import kr.co.openit.bpdiary.common.constants.BlueToothGattServiceConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ReturnCode;
import kr.co.openit.bpdiary.common.device.UA651;
import kr.co.openit.bpdiary.common.device.WeightScale;
import kr.co.openit.bpdiary.utils.DateUtil;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.services.WeighingScaleService;

//import com.samsung.android.sdk.healthdata.HealthDataStore;

/**
 * BLE 디바이스 장치들의 각각의 서비스들을 바인드하고 장치들의 데이터를 받기 위한 클래스
 *
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @created 2012. 5. 14.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BlueToothBLEService {
    private final static String TAG = BlueToothBLEService.class.getSimpleName();
    private Context context = null;
    private boolean isRegister = false;

    /**
     * ========================== 혈압계 ==========================
     */
    /**
     * 혈압계 디바이스 어드레스
     */
    private String mBloodPressureDeviceNameAND = "";
    /**
     * 혈압계 디바이스 어드레스
     */
    private String mBloodPressureDeviceAddressAND = "";
    /**
     * 혈압계 서비스
     */
    private UA651 mBloodPressureAND = null;
    /**
     * 혈압계 서비스와 바인드 여부
     */
    private boolean isBloodPressureServiceBindAND = false;
    /**
     * 각 디바이스 서비스에서 전달받은 데이터를 전달할 리스너
     */
    private BlueToothBPBLEDeviceInterface bpBleDeviceListener = null;
    /**
     * 각 디바이스 서비스에서 전달받은 데이터를 전달할 리스너
     */
    private BlueToothWSBLEDeviceInterface wsBleDeviceListener = null;
    /**
     * BloodPressureService
     */
    private BloodPressureService bpService;
    /**
     * 혈압 측정 Data를 DB에 저장 및 Server로 전송하는 AsyncTask
     */
    private CreateBloodPressureAsync cbpAsync;
    /**
     * ========================== 체중계 ==========================
     */
    /**
     * 체중계 디바이스 어드레스
     */
    private String mWeightScaleDeviceName = "";
    /**
     * 체중계 디바이스 어드레스
     */
    private String mWeightScaleDeviceAddress = "";
    /**
     * 체중계 서비스
     */
    private WeightScale mWeightScale = null;
    /**
     * 체중계 서비스와 바인드 여부
     */
    private boolean isWeightScaleServiceBind = false;
    /**
     * WeighingScaleService
     */
    private WeighingScaleService wsService;
    /**
     * 체중 측정 Data를 DB에 저장 및 Server로 전송하는 AsyncTask
     */
    private CreateWeighingScaleAsync cwsAsync;
    /**
     * 데이터 DB 저장용 Map
     */
    private Map<String, String> requestMap;
    private ArrayList<String> dataList;
    private GoogleApiClient mClient = null;
    private static final String AUTH_PENDING = "auth_state_pending";
    public static final String SESSION_NAME_BLOOD_PRESSURE = "Measure Blood Pressure";
    /**
     * S Health
     */
//    private HealthDataStore mStore;

    private int cnt = 0;

    /**
     * BLE 디바이스 장치들의 각각의 서비스들을 바인드하고 장치들의 데이터를 받기 위한 클래스 생성자
     *
     * @param context
     */
    public BlueToothBLEService(Context context) {
        // TODO Auto-generated constructor stub
        if (isRegister) {
            serviceStop("all");
        }
        this.context = context;
        this.context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        this.isRegister = true;
    }

    public void stopBluetoothBLEService() {
        serviceStop("all");
        if (isRegister) {
            context.unregisterReceiver(mGattUpdateReceiver);
            isRegister = false;
        }
    }

    /**
     * ble 서비스 전체 해제
     */
    public void serviceStop(String device) {
        Log.i("sgim", "serviceStop()");
        if (isRegister) {
            if ("bp".equals(device)) {
                if (isBloodPressureServiceBindAND) {
                    closeBloodPressureDeviceAND();
                }
            } else if ("ws".equals(device)) {
                if (isWeightScaleServiceBind) {
                    closeWeightScaleDevice();
                }
            } else if ("all".equals(device)) {
                if (isBloodPressureServiceBindAND) {
                    closeBloodPressureDeviceAND();
                }
                if (isWeightScaleServiceBind) {
                    closeWeightScaleDevice();
                }
            }
        }
    }

    /**
     * ble 서비스 매니저 서비스의 실행, 중지를 컨트롤
     *
     * @param name    - 서비스 연결 디바이스 이름
     * @param address - 서비스 연결 디바이스 주소
     * @param state   - 실행, 증지 상태 값
     */
    public void serviceBPManager(String name, String address, BlueToothBPBLEDeviceInterface bpListener, String state) {
        serviceStop("bp");
        bpBleDeviceListener = bpListener;
        if (name.startsWith("A&D_UA-651BLE")) {
            if (state.equals("start") && !isBloodPressureServiceBindAND) {
                setBloodPressureDeviceAND(name, address);
            } else if (state.equals("start") && !address.equals(mBloodPressureDeviceAddressAND)) {
                closeBloodPressureDeviceAND();
                setBloodPressureDeviceAND(name, address);
            } else if (state.equals("stop") && isBloodPressureServiceBindAND && name.equals(mBloodPressureDeviceNameAND)) {
                closeBloodPressureDeviceAND();
            }
        }
    }

    /**
     * ble 서비스 매니저 서비스의 실행, 중지를 컨트롤
     *
     * @param name    - 서비스 연결 디바이스 이름
     * @param address - 서비스 연결 디바이스 주소
     * @param state   - 실행, 증지 상태 값
     */
    public void serviceWSManager(String name, String address, BlueToothWSBLEDeviceInterface wsListener, String state) {
        wsBleDeviceListener = wsListener;
        if (name.equals("MI_SCALE")) {
            if (state.equals("start") && !isWeightScaleServiceBind) {
                setWeightScaleDevice(name, address);
            } else if (state.equals("start") && !address.equals(mWeightScaleDeviceAddress)) {
                closeWeightScaleDevice();
                setWeightScaleDevice(name, address);
            } else if (state.equals("stop") && isWeightScaleServiceBind && name.equals(mWeightScaleDeviceName)) {
                closeWeightScaleDevice();
            }
        }
    }

    /**
     * 혈압계서비스 바인드
     *
     * @param bloodPressureDeviceAddress - 혈압계 디바이스 어드레스
     */
    private void setBloodPressureDeviceAND(String bloodPressureDeviceName, String bloodPressureDeviceAddress) {
        this.mBloodPressureDeviceNameAND = bloodPressureDeviceName;
        this.mBloodPressureDeviceAddressAND = bloodPressureDeviceAddress;
        if (mBloodPressureDeviceAddressAND != null && !mBloodPressureDeviceAddressAND.equals("")) {
            Intent gattServiceIntent = new Intent(context, UA651.class);
            isBloodPressureServiceBindAND = context.bindService(gattServiceIntent,
                    mBloodPressureServiceConnectionAND,
                    Context.BIND_AUTO_CREATE);

            if (isBloodPressureServiceBindAND == false) {
                //                bpBleDeviceListener.onFail(BleConstants.MEASUR_DEVICE_TYPE_BLOOD_PRESSURE,
                //                                         ErrorCode.DEVICES_SERVICE_NOT_BIND);
            }
        }
    }

    /**
     * 체중계 서비스 바인드
     *
     * @param weightScaleDeviceAddress - 체중계 디바이스 어드레스
     */
    private void setWeightScaleDevice(String weightScaleDeviceName, String weightScaleDeviceAddress) {
        Log.i("sgim", "setWeightScaleDevice");
        this.mWeightScaleDeviceName = weightScaleDeviceName;
        this.mWeightScaleDeviceAddress = weightScaleDeviceAddress;
        if (mWeightScaleDeviceAddress != null && !mWeightScaleDeviceAddress.equals("")) {
            Intent gattServiceIntent = new Intent(context, WeightScale.class);
            isWeightScaleServiceBind = context.bindService(gattServiceIntent,
                    mWeightScaleServiceConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * ========== 디바이스 서비스 종료 ==========================
     */
    /**
     * 혈압계 디바이스 서비스 종료
     */
    private void closeBloodPressureDeviceAND() {
        if (isBloodPressureServiceBindAND) { // true 일 경우 등록된 상태 이다.
            context.unbindService(mBloodPressureServiceConnectionAND);
            if(mBloodPressureAND != null) {
                mBloodPressureAND.stopSelf();
            }
            mBloodPressureDeviceNameAND = "";
            mBloodPressureDeviceAddressAND = "";
            isBloodPressureServiceBindAND = false;
            mBloodPressureAND = null;
            //            bpBleDeviceListener.onSuccess(BleConstants.MEASUR_DEVICE_TYPE_BLOOD_PRESSURE, ReturnCode.DISCONNECT_SUCCESS);
        }
    }

    /**
     * 체중계 디바이스 서비스 종료
     */
    private void closeWeightScaleDevice() {
        Log.i("sgim", "closeWeightScaleDevice");
        if (isWeightScaleServiceBind && mWeightScale != null) {
            context.unbindService(mWeightScaleServiceConnection);
            if(mWeightScale != null) {
                mWeightScale.stopSelf();
            }
            mWeightScaleDeviceName = "";
            mWeightScaleDeviceAddress = "";
            isWeightScaleServiceBind = false;
            mWeightScale = null;
//            bpBleDeviceListener.onSuccess(HealthUpConstants.MEASUR_DEVICE_TYPE_WEIGHT_SCALE,
//                    ReturnCode.DISCONNECT_SUCCESS);
        }
    }

    private final ServiceConnection mBloodPressureServiceConnectionAND = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBloodPressureAND = ((UA651.LocalBinder) service).getService();

            if (!mBloodPressureAND.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");

                //                if (bpBleDeviceListener != null) {
                //                    bpBleDeviceListener.onFail(BleConstants.MEASUR_DEVICE_TYPE_BLOOD_PRESSURE,
                //                                             ErrorCode.BLUETOOTH_NOT_INITIALIZE);
                //                }
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBloodPressureAND.connect(mBloodPressureDeviceAddressAND);

            if (bpBleDeviceListener != null) {
                bpBleDeviceListener.onSuccess(BleConstants.MEASUR_DEVICE_TYPE_BLOOD_PRESSURE,
                        ReturnCode.BLUETOOTH_CONNECT_SUCCESS);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBloodPressureAND = null;
            //            if (bpBleDeviceListener != null) {
            //                bpBleDeviceListener.onSuccess(BleConstants.MEASUR_DEVICE_TYPE_BLOOD_PRESSURE,
            //                                            ReturnCode.BLUETOOTH_DISCONNECT_SUCCESS);
            //            }
        }
    };
    // Code to manage Service lifecycle.
    private final ServiceConnection mWeightScaleServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mWeightScale = ((WeightScale.LocalBinder) service).getService();

            if (!mWeightScale.initialize()) {
                Log.e(TAG,
                        "Unable to initialize Bluetooth");

//                if (bpBleDeviceListener != null) {
//                    bpBleDeviceListener
//                            .onFail(BleConstants.MEASUR_DEVICE_TYPE_WEIGHT_SCALE,
//                                    ErrorCode.BLUETOOTH_NOT_INITIALIZE);
//                }
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mWeightScale.connect(mWeightScaleDeviceAddress);

            if (wsBleDeviceListener != null) {
                wsBleDeviceListener.onSuccess(BleConstants.MEASUR_DEVICE_TYPE_WEIGHT_SCALE,
                        ReturnCode.BLUETOOTH_CONNECT_SUCCESS);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mWeightScale = null;
//            if (bpBleDeviceListener != null) {
//                bpBleDeviceListener.onSuccess(BleConstants.MEASUR_DEVICE_TYPE_WEIGHT_SCALE,
//                        ReturnCode.BLUETOOTH_DISCONNECT_SUCCESS);
//            }
        }
    };
    /**
     * 각각의 BLE 디바이스로 부터 데이터를 전달 받음
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.i("sgim", action);
            if (BlueToothGattServiceConstants.ACTION_GATT_BLOOD_PRESSURE_DATA_AVAILABLE.equals(action)) {
                /**
                 * blood pressure service
                 */
                bpService = new BloodPressureService(context);

//                String recvString = intent.getStringExtra(BlueToothGattServiceConstants.EXTRA_GATT_DATA);
                dataList = (ArrayList<String>) intent.getSerializableExtra(BlueToothGattServiceConstants.EXTRA_GATT_LIST_DATA);

                // DB에 저장 및 Server로 전송 AsyncTask 실행
                cbpAsync = new CreateBloodPressureAsync();
                cbpAsync.execute();

            } else if (BlueToothGattServiceConstants.ACTION_GATT_WEIGHT_DATA_AVAILABLE.equals(action)) {
                Log.i("sgim", "ACTION_GATT_WEIGHT_DATA_AVAILABLE");
                /**
                 * Weight Scale Service
                 */
                wsService = new WeighingScaleService(context);

                String recvString = intent.getStringExtra(BlueToothGattServiceConstants.EXTRA_GATT_DATA);

                cwsAsync = new CreateWeighingScaleAsync();
                cwsAsync.execute(recvString);

            } else if (BlueToothGattServiceConstants.ACTION_GATT_STATUS_MSG.equals(action)) {
                Log.i("sgim", "ACTION_GATT_STATUS_MSG");
                String recvString = intent.getStringExtra(BlueToothGattServiceConstants.EXTRA_GATT_DATA);
                String device = intent.getStringExtra("device");
                String[] data = recvString.split("===", 5);
                ReturnCode status = null;
                if (data[1].equals("connect")) {
                    status = ReturnCode.MEASURE_DEVICE_CONNECT;
                    if ("bp".equals(device)) {
                        bpBleDeviceListener.onSuccess(data[0], status);
                    } else if ("ws".equals(device)) {
                        wsBleDeviceListener.onSuccess(data[0], status);
                    }
                } else if (data[1].equals("time out")) {
                    bpBleDeviceListener.onFail(data[0], "time out");
                }
            }
        }
    };

    /**
     * 각 디바이스마다 연결, 해제, 데이터 등을 받기 위해 인텐트 필터 등록
     *
     * @return intent를 받기 위해 설정한 IntentFilter
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_BLOOD_PRESSURE_DATA_AVAILABLE);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_GLUCOSE_DATA_AVAILABLE);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_WEIGHT_DATA_AVAILABLE);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_MIBAND_DATA_AVAILABLE);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_BRACELET_DATA_AVAILABLE);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_HEART_RATE_DATA_AVAILABLE);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_GLUCOSE_GLU_NEO_DATA_AVAILABLE);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_TEMPERATURE_DATA_AVAILABLE);
        intentFilter.addAction(BlueToothGattServiceConstants.ACTION_GATT_STATUS_MSG);
        return intentFilter;
    }

    /**
     * 혈압 측정 등록 Async
     */
    private class CreateBloodPressureAsync extends AsyncTask<Void, Void, JSONObject> {
        boolean returnCode = false;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {

                String strDbSeq = null;

                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));

                //DB 저장
                for (int i = 0; i < dataList.size(); i++) {
                    String[] strData = dataList.get(i).split("===", 10);

                    String sys = strData[0];
                    String dia = strData[1];
                    String mean = strData[2];
                    String pulse = strData[3];
                    String unit = strData[4];
                    String deviceSerialNo = strData[5];
                    String deviceModelNm = strData[6];
                    String deviceManufacturer = strData[7];
                    String collectDt = strData[8];

                    if (!"2047".equalsIgnoreCase(sys)) {
                        cnt++;
                        requestMap = new HashMap<String, String>();
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS, sys);
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA, dia);
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN, mean);
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE, pulse);

                        String strType = HealthcareUtil.getBloodPressureType(context, sys, dia);

                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE, strType);
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID, deviceSerialNo);
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL, deviceModelNm);
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, deviceManufacturer);
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, "");
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT, collectDt);
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT, ManagerUtil.getCurrentDateTime());
                        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, "N");

                        int nRow = bpService.createBloodPressureData(requestMap);

                        if (nRow > 0) {

                            strDbSeq = String.valueOf(nRow);

                            //Server 전송
                            data.put(ManagerConstants.RequestParamName.INS_DT, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                            data.put(ManagerConstants.RequestParamName.BP_SYS, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                            data.put(ManagerConstants.RequestParamName.BP_DIA, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                            data.put(ManagerConstants.RequestParamName.BP_MEAN, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN));
                            data.put(ManagerConstants.RequestParamName.BP_PULSE, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
                            data.put(ManagerConstants.RequestParamName.BP_TYPE, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE));
                            data.put(ManagerConstants.RequestParamName.SENSOR_COMPANY,
                                    requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                            data.put(ManagerConstants.RequestParamName.SENSOR_MODEL, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL));
                            data.put(ManagerConstants.RequestParamName.SENSOR_SN, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID));
                            data.put(ManagerConstants.RequestParamName.MESSAGE, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
                            data.put(ManagerConstants.RequestParamName.RECORD_DT,
                                    ManagerUtil.convertDateFormatToServer(requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT)));

                            resultJSON = bpService.sendBloodPressureData(data);
                            returnCode = true;

                            if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                                if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT).toString())) {
                                    //Server 전송 완료

                                    //DB에 Server 전송 Update
                                    bpService.updateSendToServerYN(strDbSeq);

                                }
                            }
                        }
                    } else {
                        if (dataList.size() > 1) {
                            returnCode = true;
                        } else {
                            returnCode = false;
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {
            if (returnCode) {
                bpBleDeviceListener.onSuccess(cnt, "BloodPressure", dataList.get(dataList.size() - 1).split("===", 10));
                returnCode = false;
                cnt = 0;
            }
        }
    }

    /**
     * 체중계 Data 등록 Async
     */
    private class CreateWeighingScaleAsync extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String... recvString) {
            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                // weight/weightUnit/SN/MN/Manufacturer/measureDt
                String[] strData = recvString[0].split("===", 10);

                String nowDate = DateUtil.getDateNow("yyyyMMddHHmmss");
                String height = "";
                String heightUnit = "";
                String weight = strData[0];
                String weightUnit = strData[1];
                String fat = "";
                String deviceSerialNo = strData[2];
                String deviceModelNm = strData[3];
                String deviceManufacturer = strData[4];
                String collectDt = nowDate;

                String strDbSeq = null;

                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));

                //BMI 구하기
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                String strBmiValue = "";
                //BMI 계산을 위해서 키를 cm -> m로 변환
                double dHeight = Double.parseDouble(sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_HEIGHT,
                        "0"));

                if (dHeight > 0) {
                    strBmiValue = ManagerUtil.calBMI(weight, String.valueOf((dHeight / 100.0)));
                } else {
                    dHeight = 0;
                    strBmiValue = "0";
                }
                String strBmiTypeValue = HealthcareUtil.getWeighingScaleBmiType(strBmiValue);

                requestMap = new HashMap<String, String>();
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT, weight);
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT, String.valueOf(dHeight));
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI, strBmiValue);
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE, strBmiTypeValue);
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID, deviceSerialNo);
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL, deviceModelNm);
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, deviceManufacturer);
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, "");
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT, collectDt);
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT, ManagerUtil.getCurrentDateTime());
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, ManagerConstants.ServerSyncYN.SERVER_SYNC_N);

                if (!"0.0".equals(weight)) {
                    //DB 저장
                    int nRow = wsService.createWeighingScaleData(requestMap);

                    if (nRow > 0) {
                        strDbSeq = String.valueOf(nRow);

                        //Server 전송
                        data.put(ManagerConstants.RequestParamName.INS_DT, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                        data.put(ManagerConstants.RequestParamName.WS_WEIGHT, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                        data.put(ManagerConstants.RequestParamName.HEIGHT, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT));
                        data.put(ManagerConstants.RequestParamName.WS_BMI, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));
                        data.put(ManagerConstants.RequestParamName.WS_BMI_TYPE, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE));
                        data.put(ManagerConstants.RequestParamName.SENSOR_COMPANY,
                                requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                        data.put(ManagerConstants.RequestParamName.SENSOR_MODEL, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL));
                        data.put(ManagerConstants.RequestParamName.SENSOR_SN, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID));
                        data.put(ManagerConstants.RequestParamName.MESSAGE, requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
                        data.put(ManagerConstants.RequestParamName.RECORD_DT,
                                ManagerUtil.convertDateFormatToServer(requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT)));

                        resultJSON = wsService.sendWeighingScaleData(data);

                        if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                            if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT).toString())) {
                                //Server 전송 완료

                                //DB에 Server 전송 Update
                                wsService.updateSendToServerYN(strDbSeq);

                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {
            if(requestMap != null) {
                wsBleDeviceListener.onSuccess(1, "Weight", requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
            }
        }
    }
}
