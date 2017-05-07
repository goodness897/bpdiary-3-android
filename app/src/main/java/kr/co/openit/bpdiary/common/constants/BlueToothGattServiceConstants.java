package kr.co.openit.bpdiary.common.constants;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.HashMap;

/**
 * BLE 통신을 위한 상수 값과 Gatt의 UUID 정의 클래스
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2012. 5. 14.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BlueToothGattServiceConstants {
	
	/**
	 * Bluetooth Gatt UUID
	 */
	private static HashMap<String, String> attributes = new HashMap();
	
	/**
	 * Bluetooth Gatt Connected Success
	 */
	public static String ACTION_GATT_CONNECTED = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_CONNECTED";

	/**
	 * Bluetooth Gatt Disconnected Success
	 */
    public static String ACTION_GATT_DISCONNECTED = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_DISCONNECTED";

	/**
	 * Bluetooth Gatt Discovered Success
	 */
    public static String ACTION_GATT_SERVICES_DISCOVERED = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_SERVICES_DISCOVERED";

    /**
     * 혈압 데이터 Broadcast 상수
     */
    public static String ACTION_GATT_BLOOD_PRESSURE_DATA_AVAILABLE = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_BLOOD_PRESSURE_DATA_AVAILABLE";
    
    /**
     * 혈당 데이터 Broadcast 상수
     */
    public static String ACTION_GATT_GLUCOSE_DATA_AVAILABLE = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_GLUCOSE_DATA_AVAILABLE";
    
    /**
     * 혈당 데이터 Broadcast 상수
     */
    public static String ACTION_GATT_GLUCOSE_GLU_NEO_DATA_AVAILABLE = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_GLUCOSE_GLU_NEO_DATA_AVAILABLE";
    
    /**
     * 체중 데이터 Broadcast 상수
     */
    public static String ACTION_GATT_WEIGHT_DATA_AVAILABLE = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_WEIGHT_DATA_AVAILABLE";
    
    /**
     * 활동계(샤오미) 데이터 Broadcast 상수
     */
    public static String ACTION_GATT_MIBAND_DATA_AVAILABLE = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_MIBAND_DATA_AVAILABLE";
   
    /**
     * 활동계 데이터 Broadcast 상수
     */
    public static String ACTION_GATT_BRACELET_DATA_AVAILABLE = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_BRACELET_DATA_AVAILABLE";
   
    /**
     * 심박계 데이터 Broadcast 상수
     */
    public static String ACTION_GATT_HEART_RATE_DATA_AVAILABLE = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_HEART_RATE_DATA_AVAILABLE";
    
    /**
     * 체온계 데이터 Broadcast 상수
     */
    public static String ACTION_GATT_TEMPERATURE_DATA_AVAILABLE = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_TEMPERATURE_DATA_AVAILABLE";
    
    /**
     * BLE status Broadcast 상수
     */
    public static String ACTION_GATT_STATUS_MSG = "kr.co.openit.healthup.sdk.ble.ACTION_GATT_STATUS_MSG";

    /**
     * 데이터 전달 Extra Name
     */
    public static String EXTRA_GATT_DATA = "kr.co.openit.healthup.sdk.ble.EXTRA_GATT_DATA";

    /**
     * 데이터 전달 Extra Name
     */
    public static String EXTRA_GATT_LIST_DATA = "kr.co.openit.healthup.sdk.ble.EXTRA_GATT_LIST_DATA";

    /**
     * 데이터 전달 Extra Name
     */
    public static String EXTRA_GATT_DATA_DEVICE = "kr.co.openit.healthup.sdk.ble.EXTRA_GATT_DATA_DEVICE";
    
    /**
     * GATT_CLIENT_CHARACTERISTIC_CONFIG  UUID 
     */
    public static String GATT_CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    /***************************** 심박계 *****************************/
    
    /**
     * GATT_HEART_RATE_SERVICE UUID 
     */
    public static String GATT_HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
    
    /**
     * GATT_HEART_RATE_MEASUREMENT UUID 
     */
    public static String GATT_HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    
    /**
     * GATT_BODY_SENSOR_LOCATION
     */
    public static String GATT_BODY_SENSOR_LOCATION = "00002a38-0000-1000-8000-00805f9b34fb";
    
    /**
     * GATT_HEART_RATE_CONTROL_POINT
     */
    public static String  GATT_HEART_RATE_CONTROL_POINT= "00002a39-0000-1000-8000-00805f9b34fb";
    
    /***************************** 속도계 *****************************/
    
    /**
     * GATT_CSC_MEASUREMENT_UUID 
     */
    public static String GATT_CSC_MEASUREMENT_UUID = "00002a5b-0000-1000-8000-00805f9b34fb";
    
    /***************************** 혈압계 *****************************/
    
    /**
     * GATT_BLOOD_PRESSURE_SERVICE  UUID 
     */
    public static String GATT_BLOOD_PRESSURE_SERVICE = "00001810-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_BLOOD_PRESSURE_MEASUREMENT  UUID 
     */
    public static String GATT_BLOOD_PRESSURE_MEASUREMENT = "00002a35-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_BLOOD_PRESSURE_FEATURE  UUID 
     */
    public static String GATT_BLOOD_PRESSURE_FEATURE = "00002a49-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_BLOOD_PRESSURE_DATE  UUID 
     */
    public static String GATT_BLOOD_PRESSURE_DATE = "00002a08-0000-1000-8000-00805f9b34fb";

    /***************************** 혈당계 *****************************/
    
    /**
     * GATT_GLUCOSE_SERVICE  UUID 
     */
    public static String GATT_GLUCOSE_SERVICE = "00001808-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_GLUCOSE_MEASUREMENT  UUID 
     */
    public static String GATT_GLUCOSE_MEASUREMENT = "00002a18-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_GLUCOSE_FEATURE UUID 
     */
    public static String GATT_GLUCOSE_FEATURE = "00002a51-0000-1000-8000-00805f9b34fb";
    
    /**
     * GATT_GLUCOSE_MEASUREMENT_CONTEXT UUID 
     */
    public static String GATT_GLUCOSE_MEASUREMENT_CONTEXT = "00002a34-0000-1000-8000-00805f9b34fb";
    
    /**
     * GATT_RECORD  UUID 
     */
    public static String GATT_RECORD = "00002a52-0000-1000-8000-00805f9b34fb";

    /************************** 체중계 ***************************/
    
    /**
     * GATT_WEIGHT_SERVICE  UUID 
     */
    public static String GATT_WEIGHT_SERVICE = "0000181d-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_WEIGHT_SCALE_MEASUREMENT  UUID 
     */
    public static String GATT_WEIGHT_SCALE_MEASUREMENT = "00002a9d-0000-1000-8000-00805f9b34fb";

    /************************** 활동계(샤오미) ***************************/
    
    /**
     * GATT_MI_BAND_SERVICE  UUID 
     */
    public static String GATT_MI_BAND_SERVICE = "0000fee0-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_MI_BAND_STEP  UUID 
     */
    public static String GATT_MI_BAND_STEP = "0000ff06-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_MI_BAND_WRITE  UUID 
     */
    public static String GATT_MI_BAND_WRITE = "0000ff05-0000-1000-8000-00805f9b34fb";
    
    /************************** 활동계 ***************************/
    /**
     * BRACELET_BLE_TX
     */
    public static String BRACELET_BLE_TX = "D44BC439-ABFD-45A2-B575-925416129600";
    
    /**
     * BRACELET_BLE_RX
     */
    public static String BRACELET_BLE_RX = "D44BC439-ABFD-45A2-B575-925416129601";
    
    /**
     * BRACELET_BLE_SERVICE
     */
    public static String BRACELET_BLE_SERVICE = "0000FEE9-0000-1000-8000-00805F9B34FB";
    
    /************************** 체온계 *****************************/
    
    /**
     * GATT_HEALTH_THERMOMETER_SERVICE
     */
    public static String GATT_HEALTH_THERMOMETER_SERVICE = "00001809-0000-1000-8000-00805f9b34fb";
    
    /**
     * GATT_TEMPERATURE
     */
    public static String GATT_TEMPERATURE = "00002a6e-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_TEMPERATURE_MEASUREMENT
     */
    public static String GATT_TEMPERATURE_MEASUREMENT = "00002a1c-0000-1000-8000-00805f9b34fb";
    
    /**
     * GATT_TEMPERATURE_TYPE
     */
    public static String GATT_TEMPERATURE_TYPE = "00002a1d-0000-1000-8000-00805f9b34fb";

    /************************** 디바이스 공통 ***************************/
    
    /**
     * GATT_DEVICE_INFORMATION_DEVICE_NAME  UUID 
     */
    public static String GATT_DEVICE_INFORMATION_DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    
    /**
     * GATT_DEVICE_INFORMATION_SERVICE  UUID 
     */
    public static String GATT_DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_DEVICE_INFORMATION_SERIAL_NUMBER  UUID 
     */
    public static String GATT_DEVICE_INFORMATION_SERIAL_NUMBER = "00002a25-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_DEVICE_INFORMATION_MODEL_NUMBER  UUID 
     */
    public static String GATT_DEVICE_INFORMATION_MODEL_NUMBER = "00002a24-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_DEVICE_INFORMATION_MANUFACTURER  UUID 
     */
    public static String GATT_DEVICE_INFORMATION_MANUFACTURER = "00002a29-0000-1000-8000-00805f9b34fb";

    /**
     * GATT_DEVICE_INFORMATION_SYSTEM_ID  UUID 
     */
    public static String GATT_DEVICE_INFORMATION_SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb";
    
    /**
     * GATT_RECORD_ACCESS_CONTROL_POINT UUID
     */
    public static String GATT_RECORD_ACCESS_CONTROL_POINT = "00002a52-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "GAP Protocol");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "GATT Protocol");
        attributes.put("00001810-0000-1000-8000-00805f9b34fb", "BloodPressure Service");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "battery");
        attributes.put(GATT_GLUCOSE_SERVICE, "Glucose Service");
        // Sample Characteristics.
        attributes.put(GATT_HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(GATT_DEVICE_INFORMATION_MANUFACTURER, "Manufacturer Name String");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a49-0000-1000-8000-00805f9b34fb", "Blood Pressure Feature");
        attributes.put(GATT_BLOOD_PRESSURE_MEASUREMENT, "Blood Pressure Measurement");
        attributes.put("00002a2b-0000-1000-8000-00805f9b34fb", "Current Time");
        attributes.put("00002a08-0000-1000-8000-00805f9b34fb", "Date Time");
        attributes.put("00002a0a-0000-1000-8000-00805f9b34fb", "Day Date Time");
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a37-0000-1000-8000-00805f9b34fb", "Heart Rate Measurement");
        attributes.put("00002a43-0000-1000-8000-00805f9b34fb", "Alert Category ID");
        attributes.put("00002a42-0000-1000-8000-00805f9b34fb", "Alert Category ID Bit Mask");
        attributes.put("00002a06-0000-1000-8000-00805f9b34fb", "Alert Level");
        attributes.put("00002a44-0000-1000-8000-00805f9b34fb", "Alert Notification Control Point");
        attributes.put("00002a3f-0000-1000-8000-00805f9b34fb", "Alert Status");
        attributes.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level");
        attributes.put("00002a22-0000-1000-8000-00805f9b34fb", "Boot Keyboard Input Report");
        attributes.put("00002a32-0000-1000-8000-00805f9b34fb", "Boot Keyboard Output Report");
        attributes.put("00002a33-0000-1000-8000-00805f9b34fb", "Boot Mouse Input Report");
        attributes.put("00002a5c-0000-1000-8000-00805f9b34fb", "CSC Feature");
        attributes.put("00002a5b-0000-1000-8000-00805f9b34fb", "CSC Measurement");
        attributes.put("00002a66-0000-1000-8000-00805f9b34fb", "Cycling Power Control Point");
        attributes.put("00002a65-0000-1000-8000-00805f9b34fb", "Cycling Power Feature");
        attributes.put("00002a09-0000-1000-8000-00805f9b34fb", "Day of Week");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        attributes.put(GATT_GLUCOSE_FEATURE, "Glucose Feature");
        attributes.put(GATT_GLUCOSE_MEASUREMENT, "Glucose Measurement");
        attributes.put(GATT_GLUCOSE_MEASUREMENT_CONTEXT, "Glucose Measurement Context");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        attributes.put("00002a4c-0000-1000-8000-00805f9b34fb", "HID Control Point");
        attributes.put("00002a4a-0000-1000-8000-00805f9b34fb", "HID Information");
        attributes.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE 11073-20601 Regulatory Certification Data List");
        attributes.put("00002a36-0000-1000-8000-00805f9b34fb", "Intermediate Cuff Pressure");
        attributes.put("00002a1e-0000-1000-8000-00805f9b34fb", "Intermediate Temperature");
        attributes.put("00002a6b-0000-1000-8000-00805f9b34fb", "LN Control Point");
        attributes.put("00002a6a-0000-1000-8000-00805f9b34fb", "LN Feature");
        attributes.put("00002a0f-0000-1000-8000-00805f9b34fb", "Local Time Information");
        attributes.put("00002a67-0000-1000-8000-00805f9b34fb", "Location and Speed");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002a21-0000-1000-8000-00805f9b34fb", "Measurement Interval");
        attributes.put(GATT_DEVICE_INFORMATION_MODEL_NUMBER, "Model Number String");
        attributes.put("00002a68-0000-1000-8000-00805f9b34fb", "Navigation");
        attributes.put("00002a46-0000-1000-8000-00805f9b34fb", "New Alert");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
        attributes.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag");
        attributes.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID");
        attributes.put("00002a69-0000-1000-8000-00805f9b34fb", "Position Quality");
        attributes.put("00002a4e-0000-1000-8000-00805f9b34fb", "Protocol Mode");
        attributes.put("00002a03-0000-1000-8000-00805f9b34fb", "Reconnection Address");
        attributes.put(GATT_RECORD_ACCESS_CONTROL_POINT, "Record Access Control Point");
        attributes.put("00002a14-0000-1000-8000-00805f9b34fb", "Reference Time Information");
        attributes.put("00002a4d-0000-1000-8000-00805f9b34fb", "Report");
        attributes.put("00002a4b-0000-1000-8000-00805f9b34fb", "Report Map");
        attributes.put("00002a40-0000-1000-8000-00805f9b34fb", "Ringer Control Point");
        attributes.put("00002a41-0000-1000-8000-00805f9b34fb", "Ringer Setting");
        attributes.put("00002a54-0000-1000-8000-00805f9b34fb", "RSC Feature");
        attributes.put("00002a53-0000-1000-8000-00805f9b34fb", "RSC Measurement");
        attributes.put("00002a55-0000-1000-8000-00805f9b34fb", "SC Control Point");
        attributes.put("00002a4f-0000-1000-8000-00805f9b34fb", "Scan Interval Window");
        attributes.put("00002a31-0000-1000-8000-00805f9b34fb", "Scan Refresh");
        attributes.put("00002a5d-0000-1000-8000-00805f9b34fb", "Sensor Location");
        attributes.put(GATT_DEVICE_INFORMATION_SERIAL_NUMBER, "Serial Number String");
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        attributes.put("00002a47-0000-1000-8000-00805f9b34fb", "Supported New Alert Category");
        attributes.put("00002a48-0000-1000-8000-00805f9b34fb", "Supported Unread Alert Category");
        attributes.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID");
        attributes.put("00002a1c-0000-1000-8000-00805f9b34fb", "Temperature Measurement");
        attributes.put("00002a1d-0000-1000-8000-00805f9b34fb", "Temperature Type");
        attributes.put("00002a12-0000-1000-8000-00805f9b34fb", "Time Accuracy");
        attributes.put("00002a13-0000-1000-8000-00805f9b34fb", "Time Source");
        attributes.put("00002a16-0000-1000-8000-00805f9b34fb", "Time Update Control Point");
        attributes.put("00002a17-0000-1000-8000-00805f9b34fb", "Time Update State");
        attributes.put("00002a11-0000-1000-8000-00805f9b34fb", "Time with DST");
        attributes.put("00002a0e-0000-1000-8000-00805f9b34fb", "Time Zone");
        attributes.put("00002a07-0000-1000-8000-00805f9b34fb", "Tx Power Level");
        attributes.put(GATT_WEIGHT_SERVICE, "Weight Scale Service");
        attributes.put("00002a9e-0000-1000-8000-00805f9b34fb", "Weight Scale Feature");
        attributes.put("00002a9d-0000-1000-8000-00805f9b34fb", "Weight Measurement");
        attributes.put("00002a9f-0000-1000-8000-00805f9b34fb", "User Control Point");
        attributes.put(GATT_MI_BAND_SERVICE, "MI Band Service");
        attributes.put(GATT_MI_BAND_STEP, "MI Band Real Step");
        attributes.put(GATT_MI_BAND_WRITE, "MI Band Control Point");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
