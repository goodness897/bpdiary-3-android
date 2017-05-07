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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import kr.co.openit.bpdiary.common.constants.BleConstants;
import kr.co.openit.bpdiary.common.constants.BlueToothGattServiceConstants;

/**
 * Ble 체중계 서비스
 *
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @created 2012. 5. 14.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class WeightScale extends Service {
    private final static String TAG = WeightScale.class.getSimpleName();
    /**
     * 안드로이드 제공 블루투스 매니저
     */
    private BluetoothManager mBluetoothManager;
    /**
     * 안드로이드 제공 블루투스 기본 어뎁터
     */
    private BluetoothAdapter mBluetoothAdapter;
    /**
     * 연결할 체중계 BLE 디바이스의 Address
     */
    private String mBluetoothDeviceAddress;
    /**
     * 체중계 제품의 시리얼 번호
     */
    private String mWeightScaleSerialNumber = "";
    /**
     * 체중계 제품의 시리얼 번호
     */
    private String mWeightScaleModelNuber = "";
    /**
     * 체중계 제품의 시리얼 번호
     */
    private String mWeightScaleManufacturer = "";
    /**
     * 체중계 제품의 시리얼 번호
     */
    private int beforeWeight = 0;
    /**
     * 체중계 제품의 시리얼 번호
     */
    private int weightCount = 0;
    /**
     * 안드로이드 제공 블루투스 GATT 프로파일
     */
    private static BluetoothGatt mBluetoothGatt;
    /**
     * Thread를 빠져 나오기 위한 변수
     */
    private static boolean deviceInfomationCheck = false;
    /**
     * 디바이스에서 사용가능한 각각의 Characteristic을 찾아 read하기 위한 Thread
     */
    private Thread deviceInfomationReadCharacteristic = null;
    private BluetoothGattService weightService = null;
    private BluetoothGattService deviceInfomationService = null;
    private ArrayList<String> dataList = null;
    private String sendStatusMsg = "";
    private boolean isConnectStatus = false;
    /**
     * 디바이스에서 찾은 Characteristic들을 저장하기위한 Queue
     */
    private static LinkedBlockingQueue<BluetoothGattCharacteristic> characteristicReadQueue = new LinkedBlockingQueue<BluetoothGattCharacteristic>();

    /**
     * 블루투스 디바이스에서 찾은 Gatt정보가 몰리는 것을 방지 하기 위해 500ms 마다 순차적으로 실행
     *
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @created 2012. 5. 14.
     */
    private class DeviceInfomationReadLoop implements Runnable {
        public void run() {

            try {
                while (true) {
                    if (deviceInfomationCheck) {
                        break;
                    }

                    if (mBluetoothGatt == null) {
                        continue;
                    }

                    if (!characteristicReadQueue.isEmpty()) {
                        final BluetoothGattCharacteristic read = characteristicReadQueue.poll();
                        mBluetoothGatt.readCharacteristic(read);
                    } else {
                        deviceInfomationCheck = true;
                        if (weightService != null) {
                            WeightScaleMeasurement();
                        }
                    }

                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Log.e(TAG,
                        e.getMessage());
            }
        }
    }

    // Implements callback methods for GATT events that the app cares about. For
    // example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = BlueToothGattServiceConstants.ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction);

                deviceInfomationCheck = false;
                // Attempts to discover services after successful connection.
                Log.i(TAG,
                        "Attempting to start service discovery:" + gatt.discoverServices());

                dataList = new ArrayList<String>();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = BlueToothGattServiceConstants.ACTION_GATT_DISCONNECTED;
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();

                if (gattServices == null) {
                    return;
                }

                for (BluetoothGattService gattService : gattServices) {
                    if (gattService.getUuid().toString()
                            .equals(BlueToothGattServiceConstants.GATT_WEIGHT_SERVICE)) {
                        weightService = mBluetoothGatt.getService(UUID
                                .fromString(BlueToothGattServiceConstants.GATT_WEIGHT_SERVICE));
                    }
                    if (gattService.getUuid().toString()
                            .equals(BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_SERVICE)) {
                        deviceInfomationService = mBluetoothGatt
                                .getService(UUID
                                        .fromString(BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_SERVICE));
                        for (BluetoothGattCharacteristic gattCharacteristic : deviceInfomationService
                                .getCharacteristics()) {
                            if (gattCharacteristic
                                    .getUuid()
                                    .toString()
                                    .equals(BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_MANUFACTURER)) {
                                characteristicReadQueue.add(gattCharacteristic);
                            }

                            if (gattCharacteristic
                                    .getUuid()
                                    .toString()
                                    .equals(BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_MODEL_NUMBER)) {
                                characteristicReadQueue.add(gattCharacteristic);
                            }

                            if (gattCharacteristic
                                    .getUuid()
                                    .toString()
                                    .equals(BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_SERIAL_NUMBER)) {
                                characteristicReadQueue.add(gattCharacteristic);
                            }
                        }
                    }
                }

                if (deviceInfomationService != null && deviceInfomationReadCharacteristic == null) {
                    deviceInfomationReadCharacteristic = new Thread(new DeviceInfomationReadLoop());
                    deviceInfomationReadCharacteristic.start();
                } else if (weightService != null) {
                    WeightScaleMeasurement();
                }
            } else {
                Log.w(TAG,
                        "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(BlueToothGattServiceConstants.ACTION_GATT_WEIGHT_DATA_AVAILABLE,
                        characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(BlueToothGattServiceConstants.ACTION_GATT_WEIGHT_DATA_AVAILABLE,
                    characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        // final Intent intent = new Intent(action);
        // sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        if (BlueToothGattServiceConstants.GATT_WEIGHT_SCALE_MEASUREMENT.equals(characteristic
                .getUuid().toString())) {
            sendData.removeMessages(0);
            if (!isConnectStatus) {
                isConnectStatus = true;
                sendStatusMsg = String.format("%s===%s===", BleConstants.MEASUR_DEVICE_TYPE_WEIGHT_SCALE, "connect");
                sendData.sendEmptyMessage(1);
            }
            /*
			 * BlueToothGattServiceConstants.GATT_WEIGHT_SCALE_MEASUREMENT 체중
			 * 데이터가 들어왔을 때 데이터를 나눠 각각의 데이터로 변경
			 */
            int offset = 0;
            int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8,
                    offset);
            offset = offset + 1;
            String measurementUnits = "kg";
            if ((flags & 0x01) > 0) {
                measurementUnits = "lb";
            }

            boolean isTimeOffset = false;
            if ((flags & 0x02) > 0) {
                isTimeOffset = true;
            }

            boolean isUserIdOffset = false;
            if ((flags & 0x04) > 0) {
                isUserIdOffset = true;
            }

            boolean isBMI = false;
            if ((flags & 0x08) > 0) {
                isBMI = true;
            }

            int weight = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,
                    offset);
            offset += 2;

            String date = "";
            if (isTimeOffset) {
                int year = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,
                        offset);
                offset += 2;
                int month = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,
                        offset);
                offset += 1;
                int day = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,
                        offset);
                offset += 1;
                int hour = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,
                        offset);
                offset += 1;
                int minute = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,
                        offset);
                offset += 1;
                int second = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,
                        offset);
                offset += 1;

                // String nowDate = DateUtil.getDateNow("yyyyMMddHHmmss");

                date = String.format("%d%d%d%d%d%d",
                        year,
                        month,
                        day,
                        hour,
                        minute,
                        second);
                // date = nowDate;
            }

            int userId = 0;
            if (isUserIdOffset) {
                userId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,
                        offset);
                offset += 1;
            }

            int BMI = 0;
            if (isBMI) {
                BMI = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,
                        offset);
                offset += 2;
            }

            // if(beforeWeight != weight) {
            // beforeWeight = weight;
            // } else {
            // weightCount++;
            //
            // if(weightCount == 2) {
            // weightCount = 0;

            double dweigth = 0;
            if (measurementUnits.equals("kg")) {
                dweigth = weight * 0.005;
            } else {
                dweigth = weight * 0.01;
            }
            Log.i("sgim",
                    "weight = " +
                            dweigth + "\nmeasurementUnits = " + measurementUnits + "\ndate = " +
                            date);

            // weight/measurementUnits/SN/MN/Manufacturer/measureDt
            String data = String.format("%.1f===%s===%s===%s===%s===%s",
                    dweigth,
                    measurementUnits,
                    mWeightScaleSerialNumber,
                    mWeightScaleModelNuber,
                    mWeightScaleManufacturer,
                    date);
            dataList.add(data);

            sendData.sendEmptyMessageDelayed(0, 6000);
        } else if (BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_MANUFACTURER
                .equals(characteristic.getUuid().toString())) {
//			LogUtil.byteData(	"GATT_DEVICE_INFORMATION_MANUFACTURER",
//								characteristic.getValue());
			/*
			 * BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_MANUFACTURER
			 * 제품의 제조회사 데이터
			 */
            final byte[] data = characteristic.getValue();
            mWeightScaleManufacturer = new String(data);
        } else if (BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_MODEL_NUMBER
                .equals(characteristic.getUuid().toString())) {
//			LogUtil.byteData(	"GATT_DEVICE_INFORMATION_MODEL_NUMBER",
//								characteristic.getValue());
			/*
			 * BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_MODEL_NUMBER
			 * 제품의 모델 번호
			 */
            final byte[] data = characteristic.getValue();
            mWeightScaleModelNuber = new String(data);
        } else if (BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_SERIAL_NUMBER
                .equals(characteristic.getUuid().toString())) {
//			LogUtil.byteData(	"GATT_DEVICE_INFORMATION_SERIAL_NUMBER",
//								characteristic.getValue());
			/*
			 * BlueToothGattServiceConstants.GATT_DEVICE_INFORMATION_SERIAL_NUMBER
			 * 제품의 시리얼 번호
			 */
            final byte[] data = characteristic.getValue();
            mWeightScaleSerialNumber = new String(data);
        }
    }

    /**
     * 컴포넌트에 반환해줄 mBinder를 위한 클래스
     *
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @created 2012. 5. 14.
     */
    public class LocalBinder extends Binder {
        public WeightScale getService() {
            return WeightScale.this;
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
     * 체중계 BLE 디바이스와 연결을 시도한다.
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
        Log.i("1234", "1234 w mBluetoothDeviceAddress : " + mBluetoothDeviceAddress);
        Log.i("1234", "1234 w mBluetoothGatt : " + mBluetoothGatt);
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            Log.d(TAG,
                    "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
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
        mBluetoothDeviceAddress = address;

        return true;
    }

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
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void WeightScaleMeasurement() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG,
                    "enableKeyInputNotification BluetoothAdapter not initialized");
            return;
        }

        BluetoothGattCharacteristic characteristic = weightService.getCharacteristic(UUID
                .fromString(BlueToothGattServiceConstants.GATT_WEIGHT_SCALE_MEASUREMENT));
        if (characteristic == null) {
            Log.i(TAG,
                    "control rx charateristic not found!");
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
                "Weight Scale Measurement = " + noti);

        mBluetoothGatt.writeDescriptor(descriptor);
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

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized");
            return;
        }

        if (enabled) {
            // This is specific to Heart Rate Measurement.
            if (BlueToothGattServiceConstants.GATT_WEIGHT_SCALE_MEASUREMENT.equals(characteristic
                    .getUuid().toString())) {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(UUID
                                .fromString(BlueToothGattServiceConstants.GATT_CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        } else {
            if (BlueToothGattServiceConstants.GATT_WEIGHT_SCALE_MEASUREMENT.equals(characteristic
                    .getUuid().toString())) {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptor(UUID
                                .fromString(BlueToothGattServiceConstants.GATT_CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     *                       If true, enable notification. False otherwise.
     */
    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG,
                    "BluetoothAdapter not initialized");
            return;
        }

        try {
            if (characteristic.getUuid().toString()
                    .equals(BlueToothGattServiceConstants.GATT_WEIGHT_SCALE_MEASUREMENT)) {
                BluetoothGattCharacteristic writeRACPchar = mBluetoothGatt
                        .getService(UUID.fromString(BlueToothGattServiceConstants.GATT_WEIGHT_SERVICE))
                        .getCharacteristic(UUID.fromString(BlueToothGattServiceConstants.GATT_WEIGHT_SCALE_MEASUREMENT));
                byte[] data = new byte[2];
                data[0] = (byte) 0x01;
                data[1] = (byte) 0x01;
                writeRACPchar.setValue(data);
                writeRACPchar.setValue(1,
                        BluetoothGattCharacteristic.FORMAT_UINT8,
                        0);
                writeRACPchar.setValue(1,
                        BluetoothGattCharacteristic.FORMAT_UINT8,
                        1);
            } else {
                byte[] data = new byte[2];
                characteristic.setValue(data);
                characteristic.setValue(data);
                characteristic.setValue(1,
                        BluetoothGattCharacteristic.FORMAT_UINT8,
                        0);
                characteristic.setValue(1,
                        BluetoothGattCharacteristic.FORMAT_UINT8,
                        1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 디바이스가 제공하는 gatt 서비스 목록을 가져온다.
     *
     * @return 디바이스가 제공하는 gatt 서비스 리스트
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }

        return mBluetoothGatt.getServices();
    }

    private Handler sendData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            deviceInfomationCheck = true;

            switch (msg.what) {
                case 0:
                    isConnectStatus = false;
                    if (dataList != null && dataList.size() > 0) {
                        Intent intent = new Intent(BlueToothGattServiceConstants.ACTION_GATT_WEIGHT_DATA_AVAILABLE);
                        intent.putExtra(BlueToothGattServiceConstants.EXTRA_GATT_DATA,
                                dataList.get(dataList.size() - 1));
                        sendBroadcast(intent);
                        dataList.clear();
                    }
                    break;
                case 1:
                    Intent intent = new Intent(BlueToothGattServiceConstants.ACTION_GATT_STATUS_MSG);
                    intent.putExtra(BlueToothGattServiceConstants.EXTRA_GATT_DATA, sendStatusMsg);
                    intent.putExtra("device", "ws");
                    sendBroadcast(intent);
                    break;
            }
        }
    };
}