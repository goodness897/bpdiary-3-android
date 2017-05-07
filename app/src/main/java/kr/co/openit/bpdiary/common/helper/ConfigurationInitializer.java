package kr.co.openit.bpdiary.common.helper;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.AppConfig;
import kr.co.openit.bpdiary.common.vo.apdu.ConfigReport;

/**
 * configuration 초기화
 */
public final class ConfigurationInitializer {

    /**
     * debugging
     */
    private static final String TAG = ConfigurationInitializer.class.getSimpleName();

    /**
     * standard configuration 객체 저장
     */
    public static void saveStandardConfigApdu() {

        File dir = new File(AppConfig.STORAGE_PATH);

        if (!dir.exists()) {
            dir.mkdir();
        }

        saveBloodPressureStandardConfig();

        saveGlucoseMeterStandardConfig();

        saveWeighingScaleStandardConfig();

        savePulseOximeterStandardConfig();

    }

    /**
     * 혈압계 standard configuration 미리 저장
     */
    private static void saveBloodPressureStandardConfig() {
        ObjectOutputStream oos = null;

        String fileName = Integer.toHexString(HealthcareConstants.StandardDevConfigId.BLOOD_PRESSURE) + ".hu";
        File configFile = new File(AppConfig.STORAGE_PATH, fileName);

        if (configFile.exists()) {

            configFile.delete();
        }

        ConfigReport bpConfig = new ConfigReport();

        ByteBuffer bpBuffer = ByteBuffer.allocate(HealthcareConstants.StandardConfiguration.BLOOD_PRESSURE.length)
                                        .put(HealthcareConstants.StandardConfiguration.BLOOD_PRESSURE);
        bpBuffer.position(0);

        bpConfig.setOctecStringLength(bpBuffer.getShort()); // OCTET STRING.length
        bpConfig.setInvokeId(bpBuffer.getShort()); // invoke-id

        bpConfig.setChoice(bpBuffer.getShort()); // CHOICE (Remote Operation Invoke | Confirmed Event Report)
        bpConfig.setChoiceLength(bpBuffer.getShort()); // CHOICE.length

        bpConfig.setMDSObject(bpBuffer.getShort()); // obj-handle = 0 (MDS object)
        bpConfig.setEventTime(bpBuffer.getInt()); //  event-time

        bpBuffer.getShort(); // event-type

        bpConfig = new ConfigReport(bpBuffer.slice());

        try {
            // configuration 저장
            oos = new ObjectOutputStream(new FileOutputStream(AppConfig.STORAGE_PATH + fileName));
            oos.writeObject(bpConfig);
            oos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * 혈당계 standard configuration 미리 저장
     */
    private static void saveGlucoseMeterStandardConfig() {
        ObjectOutputStream oos = null;

        String fileName = Integer.toHexString(HealthcareConstants.StandardDevConfigId.GLUCOSE_METER_1700) + ".hu";
        File configFile = new File(AppConfig.STORAGE_PATH, fileName);

        if (configFile.exists()) {

            configFile.delete();
        }

        ConfigReport glConfig = new ConfigReport();

        ByteBuffer glBuffer = ByteBuffer.allocate(HealthcareConstants.StandardConfiguration.GLUCOSE_METER.length)
                                        .put(HealthcareConstants.StandardConfiguration.GLUCOSE_METER);
        glBuffer.position(0);

        glConfig.setOctecStringLength(glBuffer.getShort()); // OCTET STRING.length
        glConfig.setInvokeId(glBuffer.getShort()); // invoke-id

        glConfig.setChoice(glBuffer.getShort()); // CHOICE (Remote Operation Invoke | Confirmed Event Report)
        glConfig.setChoiceLength(glBuffer.getShort()); // CHOICE.length

        glConfig.setMDSObject(glBuffer.getShort()); // obj-handle = 0 (MDS object)
        glConfig.setEventTime(glBuffer.getInt()); //  event-time

        glBuffer.getShort(); // event-type

        glConfig = new ConfigReport(glBuffer.slice());

        try {
            // configuration 저장
            oos = new ObjectOutputStream(new FileOutputStream(AppConfig.STORAGE_PATH + fileName));
            oos.writeObject(glConfig);
            oos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * 체중계 standard configuration 미리 저장
     */
    private static void saveWeighingScaleStandardConfig() {
        ObjectOutputStream oos = null;

        String fileName = Integer.toHexString(HealthcareConstants.StandardDevConfigId.WEIGHING_SCALE) + ".hu";
        File configFile = new File(AppConfig.STORAGE_PATH, fileName);

        if (configFile.exists()) {

            configFile.delete();

        }

        ConfigReport wgConfig = new ConfigReport();

        ByteBuffer wgBuffer = ByteBuffer.allocate(HealthcareConstants.StandardConfiguration.WEIGHING_SCALE.length)
                                        .put(HealthcareConstants.StandardConfiguration.WEIGHING_SCALE);
        wgBuffer.position(0);

        wgConfig.setOctecStringLength(wgBuffer.getShort()); // OCTET STRING.length
        wgConfig.setInvokeId(wgBuffer.getShort()); // invoke-id

        wgConfig.setChoice(wgBuffer.getShort()); // CHOICE (Remote Operation Invoke | Confirmed Event Report)
        wgConfig.setChoiceLength(wgBuffer.getShort()); // CHOICE.length

        wgConfig.setMDSObject(wgBuffer.getShort()); // obj-handle = 0 (MDS object)
        wgConfig.setEventTime(wgBuffer.getInt()); //  event-time

        wgBuffer.getShort(); // event-type

        wgConfig = new ConfigReport(wgBuffer.slice());

        try {
            // configuration 저장
            oos = new ObjectOutputStream(new FileOutputStream(AppConfig.STORAGE_PATH + fileName));
            oos.writeObject(wgConfig);
            oos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    /**
     * 산소포화도 standard configuration 미리 저장
     */
    private static void savePulseOximeterStandardConfig() {
        ObjectOutputStream oos = null;
        ConfigReport poConfig = null;

        String fileName = Integer.toHexString(HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_400) + ".hu";
        File configFile = new File(AppConfig.STORAGE_PATH, fileName);

        if (configFile.exists()) {

            configFile.delete();

        }

        poConfig = new ConfigReport();

        ByteBuffer wgBuffer = ByteBuffer.allocate(HealthcareConstants.StandardConfiguration.PULSE_OXIMETER.length)
                                        .put(HealthcareConstants.StandardConfiguration.PULSE_OXIMETER);
        wgBuffer.position(0);

        poConfig.setOctecStringLength(wgBuffer.getShort()); // OCTET STRING.length
        poConfig.setInvokeId(wgBuffer.getShort()); // invoke-id

        poConfig.setChoice(wgBuffer.getShort()); // CHOICE (Remote Operation Invoke | Confirmed Event Report)
        poConfig.setChoiceLength(wgBuffer.getShort()); // CHOICE.length

        poConfig.setMDSObject(wgBuffer.getShort()); // obj-handle = 0 (MDS object)
        poConfig.setEventTime(wgBuffer.getInt()); //  event-time

        wgBuffer.getShort(); // event-type

        poConfig = new ConfigReport(wgBuffer.slice());

        try {
            // configuration 저장
            oos = new ObjectOutputStream(new FileOutputStream(AppConfig.STORAGE_PATH + fileName));
            oos.writeObject(poConfig);
            oos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        fileName = Integer.toHexString(HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_401) + ".hu";
        configFile = new File(AppConfig.STORAGE_PATH, fileName);

        if (configFile.exists()) {

            configFile.delete();

        }

        poConfig = new ConfigReport();

        wgBuffer = ByteBuffer.allocate(HealthcareConstants.StandardConfiguration.PULSE_OXIMETER.length)
                             .put(HealthcareConstants.StandardConfiguration.PULSE_OXIMETER);
        wgBuffer.position(0);

        poConfig.setOctecStringLength(wgBuffer.getShort()); // OCTET STRING.length
        poConfig.setInvokeId(wgBuffer.getShort()); // invoke-id

        poConfig.setChoice(wgBuffer.getShort()); // CHOICE (Remote Operation Invoke | Confirmed Event Report)
        poConfig.setChoiceLength(wgBuffer.getShort()); // CHOICE.length

        poConfig.setMDSObject(wgBuffer.getShort()); // obj-handle = 0 (MDS object)
        poConfig.setEventTime(wgBuffer.getInt()); //  event-time

        wgBuffer.getShort(); // event-type

        poConfig = new ConfigReport(wgBuffer.slice());

        try {
            // configuration 저장
            oos = new ObjectOutputStream(new FileOutputStream(AppConfig.STORAGE_PATH + fileName));
            oos.writeObject(poConfig);
            oos.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
