package kr.co.openit.bpdiary.common.device;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import kr.co.openit.bpdiary.common.constants.BleConstants;
import kr.co.openit.bpdiary.common.constants.BlueToothGattServiceConstants;
import kr.co.openit.bpdiary.utils.CountDownTimerUtil;
import kr.co.openit.bpdiary.utils.DateUtil;

/**
 * Ble 혈압계 서비스
 *
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @created 2012. 5. 14.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class UA651 extends Service {
    private final static String TAG = UA651.class.getSimpleName();
    /**
     * 안드로이드 제공 블루투스 매니저
     */
    private BluetoothManager mBluetoothManager;
    /**
     * 안드로이드 제공 블루투스 기본 어뎁터
     */
    private BluetoothAdapter mBluetoothAdapter;
    /**
     * 연결할 혈압계 BLE 디바이스의 Address
     */
    private String mDeviceAddress;
    /**
     * 혈압 데이터 리스트
     */
    private ArrayList<String> dataList = null;
    /**
     * 혈압계 제품의 시리얼 번호
     */
    private String mSerialNumber = "";
    /**
     * 혈압계 제품의 모델 번호
     */
    private String mModelName = BleConstants.UA_651BLE;
    /**
     * 혈압계 제품의 제조 회사
     */
    private String mManufacturer = "A&D Medical";
    /**
     * 안드로이드 제공 블루투스 GATT 프로파일
     */
    private static BluetoothGatt mBluetoothGatt;
    private static BluetoothDevice device = null;
    private BluetoothGattService bloodPressureService = null;
    private String sendStatusMsg = "";
    private boolean isTimeOut = false;
    // Implements callback methods for GATT events that the app cares about. For
    // example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            Log.i("sgim",
                    "onConnectionStateChange status: " + status);
            Log.i("sgim",
                    "onConnectionStateChange newState: " + status);
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                intentAction = BlueToothGattServiceConstants.ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction);

                // Attempts to discover services after successful connection.
                boolean isDiscover = gatt.discoverServices();
                Log.i("sgim", "isdiscover = " + isDiscover);
                Log.i(TAG,
                        "Attempting to start service discovery:" + isDiscover);
                dataList = new ArrayList<String>();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("sgim", "BluetoothProfile.STATE_DISCONNECTED");
                intentAction = BlueToothGattServiceConstants.ACTION_GATT_DISCONNECTED;
                broadcastUpdate(intentAction);
                if (isTimeOut) {
                    close();
                    sendData.removeMessages(2);
                    sendData.sendEmptyMessageDelayed(2, 10000);
                }
            }
//            Log.i("sgim", "status = " + status);
//            if (status == 133) {
//                // 133 연결이 끊어졌을 경우 gatt를 종료하고 재연결을 시도한다.
////				gatt.close();
////				mBluetoothGatt = null;
////				connect(mBluetoothDeviceAddress);
//                if (device != null) {
//                    connectToDevice(device);
//                }
//            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i("sgim",
                    "onServicesDiscovered received: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                sendStatusMsg = String.format("%s===%s===", BleConstants.MEASUR_DEVICE_TYPE_BLOOD_PRESSURE, "connect");
                sendData.sendEmptyMessage(1);
                List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();

                if (gattServices == null) {
                    return;
                }

                for (BluetoothGattService gattService : gattServices) {
                    if (gattService.getUuid().toString()
                            .equals(BlueToothGattServiceConstants.GATT_BLOOD_PRESSURE_SERVICE)) {
                        bloodPressureService = mBluetoothGatt
                                .getService(UUID
                                        .fromString(BlueToothGattServiceConstants.GATT_BLOOD_PRESSURE_SERVICE));
                    }
                }
//                writeDataCharacteristic();
                sendData.sendEmptyMessageDelayed(3, 1000);
            } else {
                Log.w(TAG,
                        "onServicesDiscovered received: " + status);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            Log.w("sgim", "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(BlueToothGattServiceConstants.ACTION_GATT_BLOOD_PRESSURE_DATA_AVAILABLE,
                        characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
            Log.w("sgim", "onCharacteristicWrite");
            BloodPressureMeasurement();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.w("sgim", "onCharacteristicChanged");
            broadcastUpdate(BlueToothGattServiceConstants.ACTION_GATT_BLOOD_PRESSURE_DATA_AVAILABLE,
                    characteristic);
        }
    };

    public void writeDataCharacteristic() {
        Log.w(TAG, "write data characteristic");
        Log.w("sgim", "write data characteristic");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG,
                    "enableKeyInputNotification BluetoothAdapter not initialized");
            return;
        }

        BluetoothGattCharacteristic sendDataCharacteristicr = null;
        if(bloodPressureService != null) {
            sendDataCharacteristicr = bloodPressureService.getCharacteristic(UUID.fromString(BlueToothGattServiceConstants.GATT_BLOOD_PRESSURE_DATE));
        }
        if (sendDataCharacteristicr == null) {
            Log.i(TAG,
                    "gatt blood pressure date charateristic not found!");
            return;
        }

        sendDataCharacteristicr = DateUtil.setDateAND(sendDataCharacteristicr, Calendar.getInstance());
        mBluetoothGatt.writeCharacteristic(sendDataCharacteristicr);
    }

    public void BloodPressureMeasurement() {
        Log.w("sgim", "BloodPressureMeasurement");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG,
                    "enableKeyInputNotification BluetoothAdapter not initialized");
            return;
        }

        BluetoothGattCharacteristic characteristic = bloodPressureService.getCharacteristic(UUID
                .fromString(BlueToothGattServiceConstants.GATT_BLOOD_PRESSURE_MEASUREMENT));
        if (characteristic == null) {
            Log.i(TAG,
                    "gatt blood pressure measurement not found!");
            return;
        }

        mBluetoothGatt.setCharacteristicNotification(characteristic,
                true);

        final int charaProp = characteristic.getProperties();

        String noti = "";

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID
                .fromString(BlueToothGattServiceConstants.GATT_CLIENT_CHARACTERISTIC_CONFIG));

        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            noti = "ENABLE_NOTIFICATION_VALUE";
        } else if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
            noti = "ENABLE_INDICATION_VALUE";
        }

        Log.i(TAG,
                "BloodPressure Measurement = " + noti);
        Log.w("sgim", "BloodPressureMeasurement = " + noti);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    private void broadcastUpdate(final String action) {
        // final Intent intent = new Intent(action);
        // sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        timerUtil.cancel();
        // This is special handling for the Heart Rate Measurement profile. Data
        // parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (BlueToothGattServiceConstants.GATT_BLOOD_PRESSURE_MEASUREMENT.equals(characteristic
                .getUuid().toString())) {
            sendData.removeMessages(0);
            /*
             * BlueToothGattServiceConstants.GATT_BLOOD_PRESSURE_MEASUREMENT 혈압
			 * 데이터가 들어왔을 때 데이터를 나눠 각각의 데이터로 변경
			 */
            int offset = 0;
            /**
             * flags : 데이터 중 첫번째 바이트 8bit 0bit : Blood Pressure Units Flag -> 0
             * : mmHg 1 : kPa
             *
             * 1bit : Time Stamp Flag -> 0 : Time Stamp not present 1 : Time
             * Stamp present
             *
             * 2bit : Pulse Rate Flag -> 0 : Pulse Rate not present 1 : Pulse
             * Rate present
             *
             * 3bit : User ID Flag -> 0 : User ID not present 1 : User ID
             * present
             *
             * 4bit : Measurement Status Flag -> 0 : Measurement Status not
             * present 1 : Measurement Status present
             *
             * 5bit ~ 7bit : Reserved for future use
             */
            int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8,
                    offset);
            offset = offset + 1;
            String unit = "mmHg";
            if ((flags & 0x01) > 0) {
                unit = "kPa";
            }
            boolean isTimeStamp = false;
            if ((flags & 0x02) > 0) {
                isTimeStamp = true;
            }
            boolean isPulse = false;
            if ((flags & 0x04) > 0) {
                isPulse = true;
            }
            boolean isUserId = false;
            if ((flags & 0x08) > 0) {
                isUserId = true;
            }

            float sys = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,
                    offset);
            offset += 2;
            float dia = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,
                    offset);
            offset += 2;
            float mean = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,
                    offset);
            offset += 2;
            String measureDt = "";
            if (isTimeStamp) {
                int year = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                offset += 2;
                measureDt += year;
                int mon = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 1;
                if (mon < 10) {
                    measureDt = measureDt + "0" + mon;
                } else {
                    measureDt += mon;
                }
                int day = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 1;
                if (day < 10) {
                    measureDt = measureDt + "0" + day;
                } else {
                    measureDt += day;
                }
                int hour = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 1;
                if (hour < 10) {
                    measureDt = measureDt + "0" + hour;
                } else {
                    measureDt += hour;
                }
                int min = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 1;
                if (min < 10) {
                    measureDt = measureDt + "0" + min;
                } else {
                    measureDt += min;
                }
                int seconds = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
                offset += 1;
                if (seconds < 10) {
                    measureDt = measureDt + "0" + seconds;
                } else {
                    measureDt += seconds;
                }
            }
            float pulse = 0;
            if (isPulse) {
                pulse = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT,
                        offset);
                offset += 2;
            }
            Log.i("sgim",
                    "sys = " +
                            (int) sys + "\ndia = " + (int) dia + "\nmean = " + (int) mean +
                            "\npulse = " + (int) pulse + "\nunit = " + unit + "\nmeasureDt = " +
                            measureDt);

            String data = String.format("%d===%d===%d===%d===%s===%s===%s===%s===%s===",
                    (int) sys,
                    (int) dia,
                    (int) mean,
                    (int) pulse,
                    unit,
                    mSerialNumber,
                    mModelName,
                    mManufacturer,
                    measureDt);

            dataList.add(data);

            sendData.sendEmptyMessageDelayed(0, 6000);
        }
    }

    /**
     * 컴포넌트에 반환해줄 mBinder를 위한 클래스
     *
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @created 2012. 5. 14.
     */
    public class LocalBinder extends Binder {
        public UA651 getService() {
            return UA651.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that
        // BluetoothGatt.close() is called
        // such that resources are cleaned up properly. In this particular
        // example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    /**
     * 컴포넌트에 반환되는 mBinder
     */
    private final IBinder mBinder = new LocalBinder();

    /**
     * 블루투스 장치 초기화
     *
     * @return 블루투스 정상적으로 초기화 or 블루투스 어뎁터를 가져왔을 때 true를 리턴한다.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG,
                        "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG,
                    "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * 혈압계 BLE 디바이스와 연결을 시도한다.
     *
     * @param address - 연결할 디바이스 어드레스
     * @return 정상적으로 연결되었을 경우 true를 리턴한다.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device. Try to reconnect.
        if (mDeviceAddress != null &&
                address.equals(mDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG,
                    "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        if (device == null) {
            device = mBluetoothAdapter.getRemoteDevice(address);
        }

        if (device == null) {
            Log.w(TAG,
                    "Device not found.  Unable to connect.");
            return false;
        }

        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this,
                true,
                mGattCallback);
        mDeviceAddress = address;
        return true;
    }

//    public void connectToDevice(BluetoothDevice device) {
//        try {
//            Method m = device.getClass().getDeclaredMethod("connectGatt", Context.class, boolean.class, BluetoothGattCallback.class, int.class);
//            int transport = device.getClass().getDeclaredField("TRANSPORT_LE").getInt(null);
//            mBluetoothGatt = (BluetoothGatt) m.invoke(device, this, true, mGattCallback, transport);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 디바이스와의 연결을 해제한다.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 블루투스 gatt 를 close하고 초기화한다.
     */
    public void close() {

        // tReadCharacteristic stop
        if (mBluetoothGatt == null) {
            return;
        }

        device = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    private Handler sendData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (dataList != null && dataList.size() > 0) {
                        Intent intent = new Intent(BlueToothGattServiceConstants.ACTION_GATT_BLOOD_PRESSURE_DATA_AVAILABLE);
//                        intent.putExtra(BlueToothGattServiceConstants.EXTRA_GATT_DATA,
//                                dataList.get(dataList.size() - 1));
                        intent.putExtra(BlueToothGattServiceConstants.EXTRA_GATT_LIST_DATA,
                                dataList);
                        sendBroadcast(intent);
                        dataList.clear();
                    } else {
                        Log.i("sgim", "sendStatusMsg = 데이터가 없음");
                    }
                    break;
                case 1:
                    Log.i("sgim", "sendStatusMsg = " + sendStatusMsg);
                    timerUtil.start();
                    Intent intent = new Intent(BlueToothGattServiceConstants.ACTION_GATT_STATUS_MSG);
                    intent.putExtra(BlueToothGattServiceConstants.EXTRA_GATT_DATA, sendStatusMsg);
                    intent.putExtra("device", "bp");
                    sendBroadcast(intent);
                    break;
                case 2:
                    Log.i("sgim", "sendStatusMsg = 재연결");
                    connect(mDeviceAddress);
                    break;
                case 3:
                    writeDataCharacteristic();
                    break;
            }
        }
    };
    private CountDownTimerUtil timerUtil = new CountDownTimerUtil(15000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            isTimeOut = true;
            disconnect();
            close();
            Log.i("sgim", "sendStatusMsg = 연결 해제");
            sendStatusMsg = String.format("%s===%s===", BleConstants.MEASUR_DEVICE_TYPE_BLOOD_PRESSURE, "time out");
            Intent intent = new Intent(BlueToothGattServiceConstants.ACTION_GATT_STATUS_MSG);
            intent.putExtra(BlueToothGattServiceConstants.EXTRA_GATT_DATA, sendStatusMsg);
            sendBroadcast(intent);
            sendData.sendEmptyMessageDelayed(2, 10000);
        }
    };
}
