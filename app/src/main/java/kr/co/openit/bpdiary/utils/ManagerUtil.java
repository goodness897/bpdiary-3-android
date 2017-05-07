package kr.co.openit.bpdiary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.common.vo.apdu.ConfigReport;
import kr.co.openit.bpdiary.common.vo.apdu.DataPrstApdu;
import kr.co.openit.bpdiary.common.vo.apdu.GetMDSPrstApdu;
import kr.co.openit.bpdiary.common.vo.attr.AbsoluteTimeAdjust;
import kr.co.openit.bpdiary.common.vo.attr.AttrValMap;
import kr.co.openit.bpdiary.common.vo.attr.AttrValMapEntry;
import kr.co.openit.bpdiary.common.vo.attr.Attribute;
import kr.co.openit.bpdiary.common.vo.attr.FLOATType;
import kr.co.openit.bpdiary.common.vo.attr.HANDLE;
import kr.co.openit.bpdiary.common.vo.attr.MetricIdList;
import kr.co.openit.bpdiary.common.vo.attr.MetricStructureSmall;
import kr.co.openit.bpdiary.common.vo.attr.OIDType;
import kr.co.openit.bpdiary.common.vo.attr.SFLOATType;
import kr.co.openit.bpdiary.common.vo.attr.SupplementalTypeList;
import kr.co.openit.bpdiary.common.vo.attr.TYPE;
import kr.co.openit.healthup.common.device.BloodPressureMonitorDevice;
import kr.co.openit.healthup.common.device.BodyCompositionAnalyzerDevice;
import kr.co.openit.healthup.common.device.CardiovascularFitnessActivityMonitorDevice;
import kr.co.openit.healthup.common.device.GlucoseMeterDevice;
import kr.co.openit.healthup.common.device.PeakExpiratoryFlowMonitorDevice;
import kr.co.openit.healthup.common.device.PulseOximeterDevice;
import kr.co.openit.healthup.common.device.WeighingScaleDevice;
import kr.co.openit.healthup.common.hl7.HL7Constants;

/**
 * ManagerUtil
 */
public final class ManagerUtil {

    /**
     * debugging
     */
    private static final String TAG = ManagerUtil.class.getSimpleName();

    public static List<Activity> activityManager;

    protected static final int CLICKING_TIME_INTERVAL = 250;

    protected static long mGlobalClickTime;

    /**
     * 여러번 클릭 방지를 위한 체크
     *
     * @return true = 클릭 막힘, false = 클릭 가능
     */
    public static boolean isClicking() {
        long timeGap = System.currentTimeMillis() - mGlobalClickTime;
        if (Math.abs(timeGap) < CLICKING_TIME_INTERVAL) {
            return true;
        }

        mGlobalClickTime = System.currentTimeMillis();
        return false;
    }

    /**
     * 이메일 유효성 검사
     *
     * @return 비밀번호 유효성 검사
     */
    public static boolean isEmail(String email) {
        if (email == null) {
            return false;
        }
        boolean b = Pattern.matches("^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$", email.trim());
        return b;
    }

    /**
     * 비밀번호 유효성 검사
     *
     * @return 비밀번호 유효성 검사
     */
    public static boolean passwordValidate(String hex) {
        //        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z]+)(?=.*[!@#$%^*+=-]|.*[0-9]+).{6,10}$");
        Pattern pattern = Pattern.compile("^(?=.[a-zA-Z0-9!@#$%^*+=-]).{6,10}$");
        Matcher matcher = pattern.matcher(hex);

        return matcher.matches();
    }

    /**
     * 현재 전체날짜조회
     *
     * @return 현재시간조회
     */
    public static String getCurrentDateTime() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

        df.setTimeZone(TimeZone.getDefault());

        return df.format(date);
    }

    /**
     * 현재 날짜조회
     *
     * @return 현재시간조회
     */
    public static String getCurrentDate() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");

        df.setTimeZone(TimeZone.getDefault());

        return df.format(date);
    }

    /**
     * 현재 시간조회
     *
     * @return 현재시간조회
     */
    public static String getCurrentTime() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("HHmm");

        df.setTimeZone(TimeZone.getDefault());

        return df.format(date);
    }

    /**
     * format 형태의 현재 시간 조회
     *
     * @param dateFormat
     * @return
     */
    public static String getCurrentDateTime(String dateFormat) {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat(dateFormat);

        df.setTimeZone(TimeZone.getDefault());

        return df.format(date);
    }

    /**
     * 현재 시작 시간 조회
     *
     * @return 현재시간조회
     */
    public static String getCurrentDateTimeBeforeDay() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd" + "000000");

        df.setTimeZone(TimeZone.getDefault());

        return df.format(date);
    }

    /**
     * 현재 시간 일주일전 시간 조회
     *
     * @return 현재시간조회
     */
    public static String getCurrentDateTimeBeforeWeek() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd" + "000000");

        Date beforeDateWeek = addBeforeWeek(date, -6);

        df.setTimeZone(TimeZone.getDefault());

        return df.format(beforeDateWeek);
    }

    /**
     * 현재 시간 한달전 시간 조회
     *
     * @return 현재시간조회
     */
    public static String getCurrentDateTimeBeforeMonth() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd" + "000000");

        Date beforeDateMonth = addBeforeMonth(date, -1, -1);

        df.setTimeZone(TimeZone.getDefault());

        return df.format(beforeDateMonth);
    }

    /**
     * 현재 시간 일년전 시간 조회
     *
     * @return 현재시간조회
     */
    public static String getCurrentDateTimeBeforeYear() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd" + "000000");

        Date beforeDateYear = addBeforeMonth(date, -12, -1);

        df.setTimeZone(TimeZone.getDefault());

        return df.format(beforeDateYear);
    }

    /**
     * 몇 주전 조회
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addBeforeWeek(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 몇 달전 조회
     *
     * @param date
     * @param months
     * @return
     */
    public static Date addBeforeMonth(Date date, int months, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 현재 TIMEZONE 조회
     *
     * @return 현재시간조회 ex) +09:00
     */
    public static String getCurrentTimeZone() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("Z");

        df.setTimeZone(TimeZone.getDefault());

        String strTimeZone = df.format(date);

        char[] charArray = strTimeZone.toCharArray();

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < charArray.length; i++) {

            sb.append(charArray[i]);

            if (i == 2) {
                sb.append(":");
            }
        }

        return sb.toString();
    }

    /**
     * yyyy-MM-dd 포맷으로 날짜 변환하여 반환
     *
     * @param nNumber
     * @return
     */
    public static String convertSigleNumber(int nNumber) {

        String strNumber = "";

        if (nNumber < 10) {
            strNumber = "0" + nNumber;
        } else {
            strNumber = String.valueOf(nNumber);
        }

        return strNumber;
    }

    /**
     * yyyy-MM-dd 포맷으로 날짜 변환하여 반환
     *
     * @param year 년
     * @param month 월
     * @param day 일
     * @return
     */
    public static String convertDateFormat(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);

        return new SimpleDateFormat("yyyy/MM/dd").format(cal.getTimeInMillis());
    }

    /**
     * yyyy-MM-dd 포맷으로 날짜 변환하여 반환
     *
     * @param date
     * @return
     */
    public static String convertDateFormat(String date) {
        if ((date == null) || (date.length() < 8)) {
            return null;
        }

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        Calendar cal = Calendar.getInstance();

        if (date.length() > 13) {
            int hourOfDay = Integer.parseInt(date.substring(8, 10));
            int minute = Integer.parseInt(date.substring(10, 12));
            int second = Integer.parseInt(date.substring(12, 14));

            cal.set(year, month - 1, day, hourOfDay, minute, second);
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(cal.getTimeInMillis());

        } else {
            cal.set(year, month - 1, day);
            return new SimpleDateFormat("yyyy/MM/dd").format(cal.getTimeInMillis());
        }
    }

    /**
     * yyyy-MM-dd 포맷으로 날짜 변환하여 반환
     *
     * @param date
     * @return
     */
    public static String convertDateFormatToServer(String date) {
        if ((date == null) || (date.length() < 8)) {
            return null;
        }

        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));

        Calendar cal = Calendar.getInstance();

        if (date.length() > 13) {
            int hourOfDay = Integer.parseInt(date.substring(8, 10));
            int minute = Integer.parseInt(date.substring(10, 12));
            int second = Integer.parseInt(date.substring(12, 14));

            cal.set(year, month - 1, day, hourOfDay, minute, second);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTimeInMillis());

        } else {
            cal.set(year, month - 1, day);
            return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTimeInMillis());
        }
    }

    /**
     * 시간을 AM, PM 구분
     *
     * @param time
     * @return
     */
    public static String convertTimeFormat(String time) {
        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(2, 4));

        String dayOrNight = "AM";

        String strHour = "";
        String strMinute = "";

        if (hour < 10) {
            strHour = "0" + hour;
        } else {

            if (hour >= 12) {

                dayOrNight = "PM";

                if (hour == 12) {
                    strHour = String.valueOf(hour);
                } else if (hour == 24) {
                    strHour = "00";
                    dayOrNight = "AM";
                } else {

                    int nHour = hour - 12;

                    if (nHour < 10) {
                        strHour = "0" + nHour;
                    } else {
                        strHour = String.valueOf(nHour);
                    }
                }
            } else {
                strHour = String.valueOf(hour);
            }
        }

        if (minute < 10) {
            strMinute = "0" + minute;
        } else {
            strMinute = String.valueOf(minute);
        }

        String strTime = dayOrNight + " " + strHour + ":" + strMinute;

        return strTime;
    }

    /**
     * NFC Hex2를 String 형식으로 변환하여 반환
     *
     * @param i
     * @return
     */
    public static String toHex2String(int i) {
        if (i >= 0 && i < 0x10) {
            return "0" + Integer.toHexString(i);
        } else {
            return Integer.toHexString(i & 0xff);
        }
    }

    /**
     * NFC HexN를 String 형식으로 변환하여 반환
     *
     * @param i
     * @param width
     * @return
     */
    public static String toHexNString(int i, int width) {
        String s = Integer.toHexString(i);
        while (s.length() < width) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * NFC Hex를 String 형식으로 변환하여 반환
     *
     * @param data
     * @return
     */
    public static String toHexString(byte[] data) {
        String s = "";
        for (int i = 0; i < data.length; i++) {
            s += toHex2String(data[i]);
        }
        return s;
    }

    /**
     * 측정 날짜 확인 헬스기기의 측정 일자가 모바일기기 일자 이후라면 모바일기기 일자를 리턴
     *
     * @param healthDeviceDt
     */
    public static String getValidMeasureDt(String healthDeviceDt) {

        String rtnDt = null;

        String mobileDt = getCurrentDateTime().substring(0, 8);

        String deviceDt = healthDeviceDt.substring(0, 8);

        if (deviceDt.compareTo(mobileDt) > 0) {

            rtnDt = getCurrentDateTime();

        } else {

            rtnDt = healthDeviceDt;

        }

        return rtnDt;

    }

    /**
     * 만보계의 측정 일자가 오늘이라면 측정시간을 계산하여 리턴(기준이 새벽 2시)
     *
     * @param healthDeviceDt
     */
    public static String getValidPedometerMeasureDt(String healthDeviceDt) {

        String rtnSecond = null;

        String mobileDt = getCurrentDateTime().substring(0, 8);

        String deviceDt = healthDeviceDt;

        if (deviceDt.compareTo(mobileDt) == 0) {

            // 현재시간
            Calendar currentCal = Calendar.getInstance();
            long currentSecond = currentCal.getTimeInMillis();

            // 같은날의 새벽 2시
            Calendar defaultCal = Calendar.getInstance();

            int year = Integer.parseInt(mobileDt.substring(0, 4));
            int month = Integer.parseInt(mobileDt.substring(4, 6));
            int day = Integer.parseInt(mobileDt.substring(6, 8));

            defaultCal.set(year, month - 1, day, 02, 00, 00);

            long defaultSecond = defaultCal.getTimeInMillis();

            rtnSecond = String.valueOf((currentSecond - defaultSecond) / 1000);

        } else {

            // 24시간 초로 변환한 값 리턴
            rtnSecond = "86400";

        }

        return rtnSecond;

    }

    /**
     * configPrstApdu 의 정보를 기준으로 Data 를 분석하여 dataPrstApdu 생성
     *
     * @param config
     * @param data
     * @param buffer
     * @return
     */
    public static int parseDataPrstApdu(ConfigReport config, GetMDSPrstApdu mds, DataPrstApdu data, ByteBuffer buffer) {

        buffer.position(0);

        int resultCode = HealthcareConstants.DataParsingResultCode.SUCCESS;

        if (data.getEventType() == Nomenclature.Action.MDC_NOTI_SCAN_REPORT_FIXED) { // 0x0d1d

            // ScanReportInfoFixed
            ByteUtil.short2ushort(buffer.getShort()); // data-req-id
            ByteUtil.short2ushort(buffer.getShort()); // scan-report-no
            int count = ByteUtil.short2ushort(buffer.getShort()); // obs-scan-fixed.count
            int length = ByteUtil.short2ushort(buffer.getShort()); // obj-scan-fixed.length

            byte[] arr = new byte[length];
            buffer = buffer.get(arr, 0, length);

            ByteBuffer obsScanFixedBuf = ByteBuffer.allocate(arr.length).put(arr);
            obsScanFixedBuf.position(0);

            String measureDateTime = null;
            double period = 0.0d;

            for (int a = 0; a < count; a++) {

                int objHandle = ByteUtil.short2ushort(obsScanFixedBuf.getShort()); // obj-handle
                Log.d(TAG, "obj-handle : " + objHandle);

                int obsValDataLength = ByteUtil.short2ushort(obsScanFixedBuf.getShort()); // obs-val-data.length

                byte[] obsValArr = new byte[obsValDataLength];
                obsScanFixedBuf = obsScanFixedBuf.get(obsValArr, 0, obsValDataLength);

                Log.d(TAG, ByteUtil.byte2hexa(obsValArr));

                ByteBuffer obsValBuf = ByteBuffer.allocate(obsValArr.length).put(obsValArr);
                obsValBuf.position(0);

                // obj-handle 에 해당 하는 configuration 추출
                Map<String, Object> configMap = config.getConfigMap(objHandle);

                @SuppressWarnings("unchecked")
                Map<Integer, Attribute> attrMap =
                                                (Map<Integer, Attribute>)configMap.get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                AttrValMap attrValMap = (AttrValMap)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ATTRIBUTE_VAL_MAP);
                List<AttrValMapEntry> entryList = attrValMap.getAttrValMapEntryList();

                for (int b = 0, len = entryList.size(); b < len; b++) {

                    if (obsValBuf.position() < obsValDataLength) {

                        // attribute id
                        int attrId = entryList.get(b).getAttributeId();

                        if ((attrId == Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_BASIC)
                            || (attrId == Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_SIMP)) {

                            TYPE type = (TYPE)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_TYPE);

                            String value = "-";
                            switch (attrId) {
                                case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_BASIC: // SFLOAT-Type

                                    // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                    // (mantissa) × (10**exponent)
                                    short shortValue = obsValBuf.getShort();

                                    if (getValidSFloatTypeValue(shortValue)) {
                                        value = Float.toString(convertShortToSFloat(shortValue));
                                    } else {
                                        resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                    }

                                    break;

                                case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_SIMP: // FLOAT-Type

                                    // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                    // (mantissa) × (10**exponent)
                                    int intValue = obsValBuf.getInt();

                                    if (getValidFloatTypeValue(intValue)) {
                                        value = Double.toString(convertIntToDouble(intValue));
                                    } else {
                                        resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                    }

                                    break;

                                default:
                                    break;
                            }

                            Log.d(TAG, getAttributeName(type.getAttributeId()) + " : " + value);

                            int unitCode = 0;
                            if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)) {
                                unitCode = ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)).getValue();
                            }

                            data.setMeasureData(objHandle, type.getCode(), value, unitCode);

                            int supplementalType = 0;
                            if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES)) {

                                SupplementalTypeList stList =
                                                            (SupplementalTypeList)attrMap.get(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES);

                                for (int c = 0; c < stList.getCount(); c++) {
                                    supplementalType = stList.getTYPE(c).getCode();

                                    data.addSubClass(objHandle,
                                                     Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES,
                                                     Integer.toString(supplementalType));

                                    c = stList.getCount();
                                }
                            }

                            //                            data.setMeasureData(objHandle, type.getCode(), value, unitCode, supplementalType);

                        } else if (attrId == Nomenclature.Attribute.MDC_ATTR_NU_CMPD_VAL_OBS_BASIC) { // SFLOAT-Type

                            ByteUtil.short2ushort(obsValBuf.getShort()); // compound object count
                            ByteUtil.short2ushort(obsValBuf.getShort()); // compound object length

                            MetricIdList metricIdList =
                                                      (MetricIdList)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_PHYSIO_LIST);
                            for (int c = 0; c < metricIdList.getCount(); c++) {

                                // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                // (mantissa) × (10**exponent)
                                short val = obsValBuf.getShort();

                                String value = "-";
                                if (getValidSFloatTypeValue(val)) {
                                    value = Float.toString(convertShortToSFloat(val));
                                } else {
                                    resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                }

                                if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)) {
                                    data.setMeasureData(objHandle,
                                                        metricIdList.getOIDType(c).getAttributeId(),
                                                        value,
                                                        ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)).getValue());
                                } else {
                                    data.setMeasureData(objHandle, metricIdList.getOIDType(c).getAttributeId(), value);
                                }
                            }

                        } else if (attrId == Nomenclature.Attribute.MDC_ATTR_ENUM_OBS_VAL_SIMP_OID) { // OID-Type

                            TYPE type = (TYPE)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_TYPE);

                            String value = Integer.toString(ByteUtil.short2ushort(obsValBuf.getShort()));

                            int unitCode = 0;
                            if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)) {
                                unitCode = ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)).getValue();
                            }

                            data.setMeasureData(objHandle, type.getCode(), value, unitCode);

                            int supplementalType = 0;
                            if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES)) {

                                SupplementalTypeList stList =
                                                            (SupplementalTypeList)attrMap.get(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES);

                                for (int c = 0; c < stList.getCount(); c++) {
                                    supplementalType = stList.getTYPE(c).getCode();

                                    data.addSubClass(objHandle,
                                                     Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES,
                                                     Integer.toString(supplementalType));

                                    c = stList.getCount();
                                }
                            }

                            //                            data.setMeasureData(objHandle, type.getCode(), value, unitCode, supplementalType);

                        } else if (attrId == Nomenclature.Attribute.MDC_ATTR_TIME_STAMP_ABS) {

                            measureDateTime = getMeasureDateTime(obsValBuf);
                            Log.i(TAG, getAttributeName(attrId) + " : " + measureDateTime); // value

                        } else if (attrId == Nomenclature.Attribute.MDC_ATTR_UNIT_CODE) {

                            int unitCode = ByteUtil.short2ushort(obsValBuf.getShort());

                            data.setUnitCode(objHandle, unitCode);

                            OIDType unit = ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE));
                            unit.setValue(unitCode);

                            attrMap.put(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE, unit);
                            configMap.put(HealthcareConstants.ConfigurationKey.ATTRIBUTE, attrMap);

                            config.setConfigMap(objHandle, configMap);

                        } else if (attrId == Nomenclature.Attribute.MDC_ATTR_MSMT_STAT) {

                            int msmtStat = ByteUtil.short2ushort(obsValBuf.getShort());

                            data.addSubClass(objHandle,
                                             Nomenclature.Attribute.MDC_ATTR_MSMT_STAT,
                                             Integer.toString(msmtStat));

                        } else if (attrId == Nomenclature.Attribute.MDC_ATTR_TIME_PD_MSMT_ACTIVE) {

                            //                          period
                            // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                            // (mantissa) × (10**exponent)
                            int intValue = obsValBuf.getInt();

                            if (getValidFloatTypeValue(intValue)) {
                                period = convertIntToDouble(intValue);

                                data.addSubClass(objHandle,
                                                 Nomenclature.Attribute.MDC_ATTR_TIME_PD_MSMT_ACTIVE,
                                                 Double.toString(period));

                                Log.d(TAG, "period : " + period);
                            }

                        } else if (attrId == Nomenclature.Attribute.MDC_ATTR_ENUM_OBS_VAL_BASIC_BIT_STR) {

                            TYPE type = (TYPE)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_TYPE);

                            int intBitValue = ByteUtil.short2ushort(obsValBuf.getShort());
                            Log.d(TAG, getAttributeName(type.getCode()) + " : " + intBitValue);

                            data.setMeasureData(objHandle, type.getCode(), Integer.toString(intBitValue));

                            int supplementalType = 0;
                            if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES)) {

                                SupplementalTypeList stList =
                                                            (SupplementalTypeList)attrMap.get(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES);

                                for (int c = 0; c < stList.getCount(); c++) {
                                    supplementalType = stList.getTYPE(c).getCode();

                                    data.addSubClass(objHandle,
                                                     Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES,
                                                     Integer.toString(supplementalType));

                                    c = stList.getCount();
                                }
                            }
                        }
                    }
                }

                HANDLE sourceHandleRef = (HANDLE)attrMap.get(Nomenclature.Attribute.MDC_ATTR_SOURCE_HANDLE_REF);
                if (sourceHandleRef != null) {
                    data.addSubClass(objHandle,
                                     Nomenclature.Attribute.MDC_ATTR_SOURCE_HANDLE_REF,
                                     Integer.toString(sourceHandleRef.getValue()));
                }

                data.setMeasureDateTime(objHandle, measureDateTime);
            }

        } else if (data.getEventType() == Nomenclature.Action.MDC_NOTI_SCAN_REPORT_VAR) { // 0x0d1e

            // ScanReportInfoVar
            ByteUtil.short2ushort(buffer.getShort()); // data-req-id
            ByteUtil.short2ushort(buffer.getShort()); // scan-report-no
            int count = ByteUtil.short2ushort(buffer.getShort());// obs-scan-var.count
            int length = ByteUtil.short2ushort(buffer.getShort()); // obs-scan-var.length

            byte[] arr = new byte[length];
            buffer = buffer.get(arr, 0, length);

            ByteBuffer obsScanVarBuf = ByteBuffer.allocate(arr.length).put(arr);
            obsScanVarBuf.position(0);

            String measureDateTime = null;

            for (int a = 0; a < count; a++) {

                int objHandle = ByteUtil.short2ushort(obsScanVarBuf.getShort()); // obj-handle
                Log.d(TAG, "obj-handle : " + objHandle);

                int attrCount = ByteUtil.short2ushort(obsScanVarBuf.getShort()); // attribute.count
                int attrLength = ByteUtil.short2ushort(obsScanVarBuf.getShort()); // attribute.length

                byte[] attrArr = new byte[attrLength];
                obsScanVarBuf = obsScanVarBuf.get(attrArr, 0, attrLength);

                ByteBuffer attrBuf = ByteBuffer.allocate(attrArr.length).put(attrArr);
                attrBuf.position(0);

                for (int b = 0; b < attrCount; b++) {

                    int attrId = ByteUtil.short2ushort(attrBuf.getShort()); // attribute-id
                    Log.d(TAG, "attribute-id : " + attrId);

                    int attrValueLength = ByteUtil.short2ushort(attrBuf.getShort()); // attribute-value.length

                    byte[] attrValArr = new byte[attrValueLength];
                    attrBuf = attrBuf.get(attrValArr, 0, attrValueLength);

                    ByteBuffer attrValBuf = ByteBuffer.allocate(attrValArr.length).put(attrValArr);
                    attrValBuf.position(0);

                    if ((attrId == Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_BASIC)
                        || (attrId == Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_SIMP)) {

                        Map<String, Object> configMap = config.getConfigMap(objHandle);

                        @SuppressWarnings("unchecked")
                        Map<Integer, Attribute> attrMap =
                                                        (Map<Integer, Attribute>)configMap.get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                        TYPE type = (TYPE)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_TYPE);

                        String value = "-";
                        switch (attrId) {
                            case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_BASIC:

                                // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                // (mantissa) × (10**exponent)
                                short shortValue = attrValBuf.getShort();

                                if (getValidSFloatTypeValue(shortValue)) {
                                    value = Float.toString(convertShortToSFloat(shortValue));
                                } else {
                                    resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                }

                                break;

                            case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_SIMP:

                                // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                // (mantissa) × (10**exponent)
                                int intValue = attrValBuf.getInt();

                                if (getValidFloatTypeValue(intValue)) {
                                    value = Double.toString(convertIntToDouble(intValue));
                                } else {
                                    resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                }

                                break;

                            default:
                                break;
                        }

                        Log.d(TAG, getAttributeName(type.getCode()) + " : " + value);

                        int unitCode = 0;
                        if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)) {
                            unitCode = ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)).getValue();
                        }

                        data.setMeasureData(objHandle, type.getCode(), value, unitCode);

                        int supplementalType = 0;
                        if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES)) {

                            SupplementalTypeList stList =
                                                        (SupplementalTypeList)attrMap.get(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES);

                            for (int c = 0; c < stList.getCount(); c++) {
                                supplementalType = stList.getTYPE(c).getCode();

                                data.addSubClass(objHandle,
                                                 Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES,
                                                 Integer.toString(supplementalType));

                                c = stList.getCount();
                            }
                        }

                        //                        data.setMeasureData(objHandle, type.getCode(), value, unitCode, supplementalType);

                    } else if (attrId == Nomenclature.Attribute.MDC_ATTR_ID_PHYSIO_LIST) {

                        Map<String, Object> configMap = config.getConfigMap(objHandle);

                        @SuppressWarnings("unchecked")
                        Map<Integer, Attribute> attrMap =
                                                        (Map<Integer, Attribute>)configMap.get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                        int metricIdListCount = ByteUtil.short2ushort(attrValBuf.getShort()); // metricIdList.count
                        int metricIdListLength = ByteUtil.short2ushort(attrValBuf.getShort()); // metricIdList.length

                        MetricIdList metricIdList = (MetricIdList)attrMap.get(attrId);

                        if (metricIdList.getCount() == metricIdListCount) {

                            for (int c = 0; c < metricIdListCount; c++) {
                                metricIdList.getOIDType(c).setAttrId(ByteUtil.short2ushort(attrValBuf.getShort()));
                            }

                            attrMap.put(attrId, metricIdList);

                        } else {

                            metricIdList = new MetricIdList(attrId);
                            metricIdList.setCount(metricIdListCount);
                            metricIdList.setLength(metricIdListLength);

                            for (int c = 0; c < metricIdListCount; c++) {
                                metricIdList.addMetricId(ByteUtil.short2ushort(attrValBuf.getShort()));
                            }

                            attrMap.put(attrId, metricIdList);
                        }

                        configMap.put(HealthcareConstants.ConfigurationKey.ATTRIBUTE, attrMap);

                        config.setConfigMap(objHandle, configMap);

                    } else if (attrId == Nomenclature.Attribute.MDC_ATTR_NU_CMPD_VAL_OBS_BASIC) {

                        Map<String, Object> configMap = config.getConfigMap(objHandle);

                        @SuppressWarnings("unchecked")
                        Map<Integer, Attribute> attrMap =
                                                        (Map<Integer, Attribute>)configMap.get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                        ByteUtil.short2ushort(attrValBuf.getShort()); // compound-object.count
                        ByteUtil.short2ushort(attrValBuf.getShort()); // compound-object.length

                        MetricIdList metricIdList = (MetricIdList)attrMap.get(attrId);

                        Log.d(TAG, metricIdList == null ? "metricIdList null" : "metricIdList not null");

                        if (metricIdList != null) {

                            for (int c = 0; c < metricIdList.getCount(); c++) {

                                short val = attrValBuf.getShort();

                                String value = "-";
                                if (getValidSFloatTypeValue(val)) {
                                    value = Float.toString(convertShortToSFloat(val));
                                } else {
                                    resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                }

                                if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)) {

                                    data.setMeasureData(objHandle,
                                                        metricIdList.getOIDType(c).getAttributeId(),
                                                        value,
                                                        ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)).getValue());
                                } else {
                                    data.setMeasureData(objHandle, metricIdList.getOIDType(c).getAttributeId(), value);
                                }
                            }

                        } else {

                            switch (config.getConfigReportId()) {

                                case HealthcareConstants.StandardDevConfigId.BLOOD_PRESSURE:
                                    //                                case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7200:
                                    //                                case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7201:
                                    //                                case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7202:
                                    //                                case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7203:

                                    metricIdList =
                                                 (MetricIdList)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_PHYSIO_LIST);
                                    for (int c = 0; c < metricIdList.getCount(); c++) {

                                        // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                        // (mantissa) × (10**exponent)
                                        short val = attrValBuf.getShort();

                                        String value = "-";
                                        if (getValidSFloatTypeValue(val)) {
                                            value = Float.toString(convertShortToSFloat(val));
                                        } else {
                                            resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                        }

                                        if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)) {
                                            data.setMeasureData(objHandle,
                                                                metricIdList.getOIDType(c).getAttributeId(),
                                                                value,
                                                                ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)).getValue());
                                        } else {
                                            data.setMeasureData(objHandle,
                                                                metricIdList.getOIDType(c).getAttributeId(),
                                                                value);
                                        }
                                    }

                                    break;
                            }
                        }

                    } else if (attrId == Nomenclature.Attribute.MDC_ATTR_ATTRIBUTE_VAL_MAP) {

                        Map<String, Object> configMap = config.getConfigMap(objHandle);

                        @SuppressWarnings("unchecked")
                        Map<Integer, Attribute> attrMap =
                                                        (Map<Integer, Attribute>)configMap.get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                        int attrValMapCount = ByteUtil.short2ushort(attrValBuf.getShort()); // AttrValMap.count
                        int attrValMapLength = ByteUtil.short2ushort(attrValBuf.getShort()); // AttrValMap.length

                        AttrValMap attrValMap = new AttrValMap(attrId);
                        attrValMap.setCount(attrValMapCount);
                        attrValMap.setLength(attrValMapLength);

                        for (int c = 0; c < attrValMapCount; c++) {
                            attrValMap.addAttrValMapEntry(ByteUtil.short2ushort(attrValBuf.getShort()),
                                                          ByteUtil.short2ushort(attrValBuf.getShort()));
                        }

                        Log.d(TAG, "[[attrValMap : " + attrValMap);

                        attrMap.put(attrId, attrValMap);

                        configMap.put(HealthcareConstants.ConfigurationKey.ATTRIBUTE, attrMap);

                        config.setConfigMap(objHandle, configMap);

                    } else if (attrId == Nomenclature.Attribute.MDC_ATTR_METRIC_STRUCT_SMALL) {

                        Map<String, Object> configMap = config.getConfigMap(objHandle);

                        @SuppressWarnings("unchecked")
                        Map<Integer, Attribute> attrMap =
                                                        (Map<Integer, Attribute>)configMap.get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                        MetricStructureSmall metricStructureSmall = new MetricStructureSmall(attrId);
                        metricStructureSmall.setMsStruct(ByteUtil.byte2uchar(attrValBuf.get()));
                        metricStructureSmall.setMsCompNo(ByteUtil.byte2uchar(attrValBuf.get()));

                        attrMap.put(attrId, metricStructureSmall);

                        configMap.put(HealthcareConstants.ConfigurationKey.ATTRIBUTE, attrMap);

                    } else if (attrId == Nomenclature.Attribute.MDC_ATTR_UNIT_CODE) {

                        int unitCode = ByteUtil.short2ushort(attrValBuf.getShort());

                        Map<String, Object> configMap = config.getConfigMap(objHandle);

                        @SuppressWarnings("unchecked")
                        Map<Integer, Attribute> attrMap =
                                                        (Map<Integer, Attribute>)configMap.get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                        OIDType oidType = (OIDType)attrMap.get(attrId);
                        oidType.setValue(unitCode);

                        attrMap.put(attrId, oidType);

                        configMap.put(HealthcareConstants.ConfigurationKey.ATTRIBUTE, attrMap);

                        config.setConfigMap(objHandle, configMap);

                    } else if (attrId == Nomenclature.Attribute.MDC_ATTR_TIME_STAMP_ABS) {

                        measureDateTime = getMeasureDateTime(attrValBuf);
                        Log.i(TAG, getAttributeName(attrId) + " : " + measureDateTime); // value

                    } else if (attrId == Nomenclature.Attribute.MDC_ATTR_TIME_ABS_ADJUST) {

                        if (objHandle > 0) {

                        } else {

                            AbsoluteTimeAdjust adjust = new AbsoluteTimeAdjust(attrId);

                            StringBuffer sb = new StringBuffer();

                            sb.append(Integer.toHexString(ByteUtil.byte2uchar(attrValBuf.get())));
                            sb.append(Integer.toHexString(ByteUtil.byte2uchar(attrValBuf.get())));
                            sb.append(Integer.toHexString(ByteUtil.byte2uchar(attrValBuf.get())));
                            sb.append(Integer.toHexString(ByteUtil.byte2uchar(attrValBuf.get())));
                            sb.append(Integer.toHexString(ByteUtil.byte2uchar(attrValBuf.get())));
                            sb.append(Integer.toHexString(ByteUtil.byte2uchar(attrValBuf.get())));

                            adjust.setValue(Integer.parseInt(sb.toString()));

                            mds.setAttribute(attrId, adjust);

                            resultCode = HealthcareConstants.DataParsingResultCode.GET_MDS_INFO;
                        }
                    }
                }

                if (measureDateTime != null) {
                    data.setMeasureDateTime(objHandle, measureDateTime);
                }
            }

        } else if (data.getEventType() == Nomenclature.Action.MDC_NOTI_SCAN_REPORT_MP_FIXED) { // 0x0d1f

            // ScanReportInfoMPFixed
            ByteUtil.short2ushort(buffer.getShort()); // data-req-id
            ByteUtil.short2ushort(buffer.getShort()); // scan-report-no
            int personCount = ByteUtil.short2ushort(buffer.getShort()); // scan-per-fixed.count
            int length = ByteUtil.short2ushort(buffer.getShort()); // scan-per-fixed.length

            byte[] arr = new byte[length];
            buffer = buffer.get(arr, 0, length);

            ByteBuffer scanPerFixedBuf = ByteBuffer.allocate(arr.length).put(arr);
            scanPerFixedBuf.position(0);

            for (int a = 0; a < personCount; a++) {

                int personId = ByteUtil.short2ushort(scanPerFixedBuf.getShort());
                Log.d(TAG, "person-id : " + personId);

                int obsScanFixedCount = ByteUtil.short2ushort(scanPerFixedBuf.getShort()); // person-id.obs-scan-fixed.count
                int obsScanFixedLength = ByteUtil.short2ushort(scanPerFixedBuf.getShort()); // person-id.obs-scan-fixed.length

                byte[] obsScanFixedArr = new byte[obsScanFixedLength];
                scanPerFixedBuf = scanPerFixedBuf.get(obsScanFixedArr, 0, obsScanFixedLength);

                ByteBuffer obsScanFixedBuf = ByteBuffer.allocate(obsScanFixedArr.length).put(obsScanFixedArr);
                obsScanFixedBuf.position(0);

                String measureDateTime = null;

                for (int b = 0; b < obsScanFixedCount; b++) {

                    int objHandle = ByteUtil.short2ushort(obsScanFixedBuf.getShort()); // obj-handle
                    Log.d(TAG, "obj-handle : " + objHandle);

                    int obsValDataLength = ByteUtil.short2ushort(obsScanFixedBuf.getShort()); // obs-val-data.length

                    byte[] obsValArr = new byte[obsValDataLength];
                    obsScanFixedBuf = obsScanFixedBuf.get(obsValArr, 0, obsValDataLength);

                    Log.d(TAG, ByteUtil.byte2hexa(obsValArr));

                    ByteBuffer obsValBuf = ByteBuffer.allocate(obsValArr.length).put(obsValArr);
                    obsValBuf.position(0);

                    // obj-handle 에 해당 하는 configuration 추출
                    Map<String, Object> configMap = config.getConfigMap(objHandle);

                    @SuppressWarnings("unchecked")
                    Map<Integer, Attribute> attrMap =
                                                    (Map<Integer, Attribute>)configMap.get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                    AttrValMap attrValMap = (AttrValMap)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ATTRIBUTE_VAL_MAP);
                    List<AttrValMapEntry> entryList = attrValMap.getAttrValMapEntryList();

                    for (int c = 0, len = entryList.size(); c < len; c++) {

                        if (obsValBuf.position() < obsValDataLength) {

                            // attribute id
                            int attrId = entryList.get(c).getAttributeId();

                            if ((attrId == Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_BASIC)
                                || (attrId == Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_SIMP)) {

                                TYPE type = (TYPE)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_TYPE);

                                String value = "-";
                                switch (attrId) {
                                    case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_BASIC:

                                        // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                        // (mantissa) × (10**exponent)
                                        short shortValue = obsValBuf.getShort();

                                        if (getValidSFloatTypeValue(shortValue)) {
                                            value = Float.toString(convertShortToSFloat(shortValue));
                                        } else {
                                            resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                        }

                                        break;

                                    case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_SIMP:

                                        // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                        // (mantissa) × (10**exponent)
                                        int intValue = obsValBuf.getInt();

                                        if (getValidFloatTypeValue(intValue)) {
                                            value = Double.toString(convertIntToDouble(intValue));
                                        } else {
                                            resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                        }

                                        break;

                                    default:
                                        break;
                                }

                                Log.d(TAG, getAttributeName(type.getCode()) + " : " + value);

                                int unitCode = 0;
                                if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)) {
                                    unitCode =
                                             ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)).getValue();
                                }

                                data.setMeasureData(objHandle, type.getCode(), personId, value, unitCode);

                                int supplementalType = 0;
                                if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES)) {

                                    SupplementalTypeList stList =
                                                                (SupplementalTypeList)attrMap.get(Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES);

                                    for (int d = 0; d < stList.getCount(); d++) {
                                        supplementalType = stList.getTYPE(d).getCode();

                                        data.addSubClass(objHandle,
                                                         Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES,
                                                         Integer.toString(supplementalType));

                                        d = stList.getCount();
                                    }
                                }

                                //                                data.setMeasureData(objHandle,
                                //                                                    type.getCode(),
                                //                                                    personId,
                                //                                                    value,
                                //                                                    unitCode,
                                //                                                    supplementalType);

                            } else if (attrId == Nomenclature.Attribute.MDC_ATTR_NU_CMPD_VAL_OBS_BASIC) {

                                ByteUtil.short2ushort(obsValBuf.getShort()); // compound-object.count
                                ByteUtil.short2ushort(obsValBuf.getShort()); // compound-object.length

                                MetricIdList metricIdList =
                                                          (MetricIdList)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_PHYSIO_LIST);
                                for (int d = 0; d < metricIdList.getCount(); d++) {

                                    // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                    // (mantissa) × (10**exponent)
                                    short val = obsValBuf.getShort();

                                    String value = "-";
                                    if (getValidSFloatTypeValue(val)) {
                                        value = Float.toString(convertShortToSFloat(val));
                                    } else {
                                        resultCode = HealthcareConstants.DataParsingResultCode.SPECIAL_VALUES;
                                    }

                                    Log.d(TAG,
                                          getAttributeName(metricIdList.getOIDType(d).getAttributeId()) + " : "
                                               + value);

                                    if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)) {

                                        data.setMeasureData(objHandle,
                                                            metricIdList.getOIDType(d).getAttributeId(),
                                                            personId,
                                                            value,
                                                            ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)).getValue());
                                    } else {
                                        data.setMeasureData(objHandle,
                                                            metricIdList.getOIDType(d).getAttributeId(),
                                                            personId,
                                                            value);
                                    }
                                }

                            } else if (attrId == Nomenclature.Attribute.MDC_ATTR_TIME_STAMP_ABS) {

                                measureDateTime = getMeasureDateTime(obsValBuf);
                                Log.i(TAG, getAttributeName(attrId) + " : " + measureDateTime); // value
                            }
                        }
                    }

                    data.setMeasureDateTime(objHandle, personId, measureDateTime);
                }
            }

        } else if (data.getEventType() == Nomenclature.Action.MDC_NOTI_SCAN_REPORT_MP_VAR) { // 0x0d20

        }

        return resultCode;
    }

    /**
     * ByteBuffer 데이터를 분석하여 측정일을 반환
     *
     * @return
     */
    private static String getMeasureDateTime(ByteBuffer buffer) {

        StringBuffer sb = new StringBuffer();

        sb.append(leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buffer.get())), 2));
        sb.append(leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buffer.get())), 2));
        sb.append(leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buffer.get())), 2));
        sb.append(leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buffer.get())), 2));
        sb.append(leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buffer.get())), 2));
        sb.append(leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buffer.get())), 2));
        sb.append(leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buffer.get())), 2));
        sb.append(leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buffer.get())), 2));

        return sb.toString();
    }

    /**
     * totalLen 만큼 왼쪽에 문자를 채워서 반환
     *
     * @param str
     * @param totalLen
     * @return
     */
    public static String leftPadding(String str, int totalLen) {
        if (str == null || str.length() < 1) {
            return str;
        }

        if (str.length() == totalLen) {
            return str;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0, cnt = totalLen - str.length(); i < cnt; i++) {
            sb.append("0");
        }

        return sb.append(str).toString();
    }

    /**
     * short 형을 sflaot 형으로 변환
     *
     * @param sh
     * @return
     */
    public static float convertShortToSFloat(short sh) {
        short exponent = (short)(sh >> 12);
        short magnitude = (short)(sh & 0x0FFF);

        return Double.valueOf(magnitude * (Math.pow(10, exponent))).floatValue();
    }

    /**
     * int 형을 float 형으로 변환
     *
     * @param i
     * @return
     */
    public static double convertIntToDouble(int i) {
        short exponent = (short)(i >> 24);
        long magnitude = (i & 0x00FFFFFF);

        return Double.valueOf(magnitude * (Math.pow(10, exponent))).doubleValue();
    }

    /**
     * int 형을 long 형으로 변환
     *
     * @param i
     * @return
     */
    public static long convertIntToLong(int i) {
        short exponent = (short)(i >> 24);
        int magnitude = (i & 0x0FFFFF);

        return Double.valueOf(magnitude * (Math.pow(10, exponent))).longValue();
    }

    /**
     * Choice 명 반환
     *
     * @param choice 명
     * @return
     */
    public static String getChoiceName(int choice) {
        switch (choice) {
            case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_EVENT_REPORT:
                return "(Remote Operation Invoke | Event Report)";
            case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_EVENT_REPORT:
                return "(Remote Operation Invoke | Confirmed Event Report)";
            case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_GET:
                return "( Remote Operation Invoke | GET)";
            case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_SET:
                return "(Remote Operation Invoke | SET)";
            case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_SET:
                return "(Remote Operation Invoke | Confirmed SET)";
            case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_ACTION:
                return "(Remote Operation Invoke | Action)";
            case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_ACTION:
                return "(Remote Operation Invoke | Confirmed Action)";
            case HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_EVENT_REPORT:
                return "(Remote Operation Response | Confirmed Event Report)";
            case HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_GET:
                return "(Remote Operation Response | GET)";
            case HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_ACTION:
                return "(Remote Operation Response | Confirmed Action)";
            default:
                return "";
        }
    }

    /**
     * Action 명 반환
     *
     * @param action
     * @return
     */
    public static String getActionName(int action) {
        switch (action) {
            case Nomenclature.Action.MDC_ACT_SEG_GET_INFO:
                return "MDC_ACT_SEG_GET_INFO";
            case Nomenclature.Action.MDC_ACT_SET_TIME:
                return "MDC_ACT_SET_TIME";
            case Nomenclature.Action.MDC_ACT_DATA_REQUEST:
                return "MDC_ACT_DATA_REQUEST";
            case Nomenclature.Action.MDC_ACT_SEG_TRIG_XFER:
                return "MDC_ACT_SEG_TRIG_XFER";
            case Nomenclature.Action.MDC_NOTI_CONFIG:
                return "MDC_NOTI_CONFIG";
            case Nomenclature.Action.MDC_NOTI_SCAN_REPORT_FIXED:
                return "MDC_NOTI_SCAN_REPORT_FIXED";
            case Nomenclature.Action.MDC_NOTI_SCAN_REPORT_VAR:
                return "MDC_NOTI_SCAN_REPORT_VAR";
            case Nomenclature.Action.MDC_NOTI_SCAN_REPORT_MP_FIXED:
                return "MDC_NOTI_SCAN_REPORT_MP_FIXED";
            case Nomenclature.Action.MDC_NOTI_SCAN_REPORT_MP_VAR:
                return "MDC_NOTI_SCAN_REPORT_MP_VAR";
            case Nomenclature.Action.MDC_NOTI_SEGMENT_DATA:
                return "MDC_NOTI_SEGMENT_DATA";
            default:
                return "";
        }
    }

    /**
     * objInfra Id 에 따른 objInfra Name 을 반환
     *
     * @param objInfra
     * @return
     */
    public static String getObjectInfraName(int objInfra) {
        switch (objInfra) {
            case Nomenclature.ObjectInfra.MDC_MOC_VMO_METRIC:
                return "MDC_MOC_VMO_METRIC";
            case Nomenclature.ObjectInfra.MDC_MOC_VMO_METRIC_ENUM:
                return "MDC_MOC_VMO_METRIC_ENUM";
            case Nomenclature.ObjectInfra.MDC_MOC_VMO_METRIC_NU:
                return "MDC_MOC_VMO_METRIC_NU";
            case Nomenclature.ObjectInfra.MDC_MOC_VMO_METRIC_SA_RT:
                return "MDC_MOC_VMO_METRIC_SA_RT";
            case Nomenclature.ObjectInfra.MDC_MOC_SCAN:
                return "MDC_MOC_SCAN";
            case Nomenclature.ObjectInfra.MDC_MOC_SCAN_CFG:
                return "MDC_MOC_SCAN_CFG";
            case Nomenclature.ObjectInfra.MDC_MOC_SCAN_CFG_EPI:
                return "MDC_MOC_SCAN_CFG_EPI";
            case Nomenclature.ObjectInfra.MDC_MOC_SCAN_CFG_PERI:
                return "MDC_MOC_SCAN_CFG_PERI";
            case Nomenclature.ObjectInfra.MDC_MOC_VMS_MDS_SIMP:
                return "MDC_MOC_VMS_MDS_SIMP";
            case Nomenclature.ObjectInfra.MDC_MOC_VMO_PMSTORE:
                return "MDC_MOC_VMO_PMSTORE";
            case Nomenclature.ObjectInfra.MDC_MOC_PM_SEGMENT:
                return "MDC_MOC_PM_SEGMENT";
            default:
                return "";
        }
    }

    /**
     * SFloat Type 의 val 이 유효한 범위의 값인지 확인
     *
     * @param val
     * @return
     */
    public static boolean getValidSFloatTypeValue(short val) {

        switch (val) {
            case SFLOATType.ShortFloatingPointSpecialValue.NAN:
            case SFLOATType.ShortFloatingPointSpecialValue.NRES:
            case SFLOATType.ShortFloatingPointSpecialValue.PLUS_INFINITY:
            case SFLOATType.ShortFloatingPointSpecialValue.MINUS_INFINITY:
            case SFLOATType.ShortFloatingPointSpecialValue.RESERVED_FOR_FUTURE_USE:

                return false;

            default:
                return true;
        }
    }

    /**
     * Float Type 의 val 이 유효한 범위의 값인지 확인
     *
     * @param val
     * @return
     */
    public static boolean getValidFloatTypeValue(int val) {

        switch (val) {
            case FLOATType.FloatingPointSpecialValue.NAN:
            case FLOATType.FloatingPointSpecialValue.NRES:
            case FLOATType.FloatingPointSpecialValue.PLUS_INFINITY:
            case FLOATType.FloatingPointSpecialValue.MINUS_INFINITY:
            case FLOATType.FloatingPointSpecialValue.RESERVED_FOR_FUTURE_USE:

                return false;

            default:
                return true;
        }
    }

    /**
     * attribute Id 에 따른 attribute name 을 반환
     *
     * @param attrId
     * @return
     */
    public static String getAttributeName(int attrId) {
        switch (attrId) {
            case Nomenclature.DataAcqu.MDC_HF_DISTANCE: // 103
                return "MDC_HF_DISTANCE";
            case Nomenclature.DataAcqu.MDC_HF_ENERGY: // 119
                return "MDC_HF_ENERGY";
            case Nomenclature.DataAcqu.MDC_HF_SESSION: // 123
                return "MDC_HF_SESSION";
            case Nomenclature.DataAcqu.MDC_HF_SUBSESSION: // 124
                return "MDC_HF_SUBSESSION";

            case Nomenclature.Attribute.MDC_ATTR_ID_HANDLE: // 2337
                return "MDC_ATTR_ID_HANDLE";
            case Nomenclature.Attribute.MDC_ATTR_ID_MODEL: // 2344
                return "MDC_ATTR_ID_MODEL";
            case Nomenclature.Attribute.MDC_ATTR_ID_PROD_SPECN: // 2349
                return "MDC_ATTR_ID_PROD_SPECN";
            case Nomenclature.Attribute.MDC_ATTR_ID_TYPE: // 2351
                return "MDC_ATTR_ID_TYPE";
            case Nomenclature.Attribute.MDC_ATTR_SYS_ID: // 2436
                return "MDC_ATTR_SYS_ID";
            case Nomenclature.Attribute.MDC_ATTR_TIME_ABS: // 2439
                return "MDC_ATTR_TIME_ABS";
            case Nomenclature.Attribute.MDC_ATTR_TIME_STAMP_ABS: // 2448
                return "MDC_ATTR_TIME_STAMP_ABS";
            case Nomenclature.Attribute.MDC_ATTR_UNIT_CODE: // 2454
                return "MDC_ATTR_UNIT_CODE";
            case Nomenclature.Attribute.MDC_ATTR_TIME_REL_HI_RES: // 2536
                return "MDC_ATTR_TIME_REL_HI_RES";
            case Nomenclature.Attribute.MDC_ATTR_TIME_STAMP_REL_HI_RES: // 2537
                return "MDC_ATTR_TIME_STAMP_REL_HI_RES";
            case Nomenclature.Attribute.MDC_ATTR_DEV_CONFIG_ID: // 2628
                return "MDC_ATTR_DEV_CONFIG_ID";
            case Nomenclature.Attribute.MDC_ATTR_MDS_TIME_INFO: // 2629
                return "MDC_ATTR_MDS_TIME_INFO";
            case Nomenclature.Attribute.MDC_ATTR_METRIC_SPEC_SMALL: // 2630
                return "MDC_ATTR_METRIC_SPEC_SMALL";
            case Nomenclature.Attribute.MDC_ATTR_SOURCE_HANDLE_REF: // 2631
                return "MDC_ATTR_SOURCE_HANDLE_REF";
            case Nomenclature.Attribute.MDC_ATTR_ENUM_OBS_VAL_SIMP_OID: // 2633
                return "MDC_ATTR_ENUM_OBS_VAL_SIMP_OID";
            case Nomenclature.Attribute.MDC_ATTR_REG_CERT_DATA_LIST: // 2635
                return "MDC_ATTR_REG_CERT_DATA_LIST";
            case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_BASIC: // 2636
                return "MDC_ATTR_NU_VAL_OBS_BASIC";
            case Nomenclature.Attribute.MDC_ATTR_ATTRIBUTE_VAL_MAP: // 2645
                return "MDC_ATTR_ATTRIBUTE_VAL_MAP";
            case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_SIMP: // 2646
                return "MDC_ATTR_NU_VAL_OBS_SIMP";
            case Nomenclature.Attribute.MDC_ATTR_TIME_PD_MSMT_ACTIVE: // 2649
                return "MDC_ATTR_TIME_PD_MSMT_ACTIVE";
            case Nomenclature.Attribute.MDC_ATTR_SYS_TYPE_SPEC_LIST: // 2650
                return "MDC_ATTR_SYS_TYPE_SPEC_LIST";
            case Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES: // 2657
                return "MDC_ATTR_SUPPLEMENTAL_TYPES";
            case Nomenclature.Attribute.MDC_ATTR_ENUM_OBS_VAL_SIMP_BIT_STR: // 2661
                return "MDC_ATTR_ENUM_OBS_VAL_SIMP_BIT_STR";
            case Nomenclature.Attribute.MDC_ATTR_ENUM_OBS_VAL_BASIC_BIT_STR: // 2662
                return "MDC_ATTR_ENUM_OBS_VAL_BASIC_BIT_STR";
            case Nomenclature.Attribute.MDC_ATTR_METRIC_STRUCT_SMALL: // 2675
                return "MDC_ATTR_METRIC_STRUCT_SMALL";
            case Nomenclature.Attribute.MDC_ATTR_NU_CMPD_VAL_OBS_BASIC: // 2677
                return "MDC_ATTR_NU_CMPD_VAL_OBS_BASIC";
            case Nomenclature.Attribute.MDC_ATTR_ID_PHYSIO_LIST: // 2678
                return "MDC_ATTR_ID_PHYSIO_LIST";

            case Nomenclature.DataAcqu.MDC_PULS_OXIM_PULS_RATE: // 18458
                return "MDC_PULS_OXIM_PULS_RATE";
            case Nomenclature.DataAcqu.MDC_PULS_RATE_NON_INV: // 18474
                return "MDC_PULS_RATE_NON_INV";
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV: // 18948
                return "MDC_PRESS_BLD_NONINV";
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_SYS: // 18949
                return "MDC_PRESS_BLD_NONINV_SYS";
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_DIA: // 18950
                return "MDC_PRESS_BLD_NONINV_DIA";
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_MEAN: // 18951
                return "MDC_PRESS_BLD_NONINV_MEAN";
            case Nomenclature.DataAcqu.MDC_PULS_OXIM_SAT_O2: // 19384
                return "MDC_PULS_OXIM_SAT_O2";
            case Nomenclature.DataAcqu.MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD: // 29112
                return "MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD";

            case Nomenclature.DataAcqu.MDC_MASS_BODY_ACTUAL: // 57664
                return "MDC_MASS_BODY_ACTUAL";
            case Nomenclature.DataAcqu.MDC_LEN_BODY_ACTUAL: // 57668
                return "MDC_LEN_BODY_ACTUAL";
            case Nomenclature.DataAcqu.MDC_BODY_FAT: // 57676
                return "MDC_BODY_FAT";
            case Nomenclature.DataAcqu.MDC_RATIO_MASS_BODY_LEN_SQ: // 57680
                return "MDC_RATIO_MASS_BODY_LEN_SQ";
            case Nomenclature.DataAcqu.MDC_MASS_BODY_FAT_FREE: // 57684
                return "MDC_MASS_BODY_FAT_FREE";
            case Nomenclature.DataAcqu.MDC_MASS_BODY_SOFT_LEAN: // 57688
                return "MDC_MASS_BODY_SOFT_LEAN";
            case Nomenclature.DataAcqu.MDC_BODY_WATER: // 57692
                return "MDC_BODY_WATER";

            case Nomenclature.DataAcqu.MDC_FLOW_AWAY_EXP_FORCED_PEAK: // 21512
                return "MDC_FLOW_AWAY_EXP_FORCED_PEAK";
            case Nomenclature.DataAcqu.MDC_FLOW_AWAY_EXP_FORCED_PEAK_PB: //21513
                return "MDC_FLOW_AWAY_EXP_FORCED_PEAK_PB";
            case Nomenclature.DataAcqu.MDC_VOL_AWAY_EXP_FORCED_1S: // 21514
                return "MDC_VOL_AWAY_EXP_FORCED_1S";
            case Nomenclature.DataAcqu.MDC_VOL_AWAY_EXP_FORCED_EXP_6S: // 21515
                return "MDC_VOL_AWAY_EXP_FORCED_EXP_6S";

            case Nomenclature.DiseaseMgmt.MDC_PEF_READING_STATUS: // 30720
                return "MDC_PEF_READING_STATUS";

            default:
                return "";
        }
    }

    /**
     * Medical supervisory control and data acquisition (MDC_PART_SCADA) 명 반환
     *
     * @param dataAcquId
     * @return
     */
    public static String getDataAcquName(int dataAcquId) {
        switch (dataAcquId) {
            case Nomenclature.DataAcqu.MDC_HF_ALT_GAIL: // 100
                return "MDC_HF_ALT_GAIL";
            case Nomenclature.DataAcqu.MDC_HF_ALT_LOSS: // 101
                return "MDC_HF_ALT_LOSS";
            case Nomenclature.DataAcqu.MDC_HF_ALT: // 102
                return "MDC_HF_ALT";
            case Nomenclature.DataAcqu.MDC_HF_DISTANCE: // 103
                return "MDC_HF_DISTANCE";
            case Nomenclature.DataAcqu.MDC_HF_ASC_TME_DIST: // 104
                return "MDC_HF_ASC_TME_DIST";
            case Nomenclature.DataAcqu.MDC_HF_DESC_TIME_DIST: // 105
                return "MDC_HF_DESC_TIME_DIST";
            case Nomenclature.DataAcqu.MDC_HF_LATITUDE: // 106
                return "MDC_HF_LATITUDE";
            case Nomenclature.DataAcqu.MDC_HF_LONGITUDE: // 107
                return "MDC_HF_LONGITUDE";
            case Nomenclature.DataAcqu.MDC_HF_PROGRAM_ID: // 108
                return "MDC_HF_PROGRAM_ID";
            case Nomenclature.DataAcqu.MDC_HF_SLOPES: // 109
                return "MDC_HF_SLOPES";
            case Nomenclature.DataAcqu.MDC_HF_SPEED: // 110
                return "MDC_HF_SPEED";
            case Nomenclature.DataAcqu.MDC_HF_CAD: // 111
                return "MDC_HF_CAD";
            case Nomenclature.DataAcqu.MDC_HF_INCLINE: // 112
                return "MDC_HF_INCLINE";
            case Nomenclature.DataAcqu.MDC_HF_HR_MAX_USER: // 113
                return "MDC_HF_HR_MAX_USER";
            case Nomenclature.DataAcqu.MDC_HF_HR: // 114
                return "MDC_HF_HR";
            case Nomenclature.DataAcqu.MDC_HF_POWER: // 115
                return "MDC_HF_POWER";
            case Nomenclature.DataAcqu.MDC_HF_RESIST: // 116
                return "MDC_HF_RESIST";
            case Nomenclature.DataAcqu.MDC_HF_STRIDE: // 117
                return "MDC_HF_STRIDE";
            case Nomenclature.DataAcqu.MDC_HF_ENERGY: // 119
                return "MDC_HF_ENERGY";
            case Nomenclature.DataAcqu.MDC_HF_CAL_INGEST: // 120
                return "MDC_HF_CAL_INGEST";
            case Nomenclature.DataAcqu.MDC_HF_CAL_INGEST_CARB: // 121
                return "MDC_HF_CAL_INGEST_CARB";
            case Nomenclature.DataAcqu.MDC_HF_SUST_PA_THRESHOLD: // 122
                return "MDC_HF_SUST_PA_THRESHOLD";
            case Nomenclature.DataAcqu.MDC_HF_SESSION: // 123
                return "MDC_HF_SESSION";
            case Nomenclature.DataAcqu.MDC_HF_SUBSESSION: // 124
                return "MDC_HF_SUBSESSION";
            case Nomenclature.DataAcqu.MDC_HF_ACTIVITY_TIME: // 125
                return "MDC_HF_ACTIVITY_TIME";
            case Nomenclature.DataAcqu.MDC_HF_AGE: // 126
                return "MDC_HF_AGE";
            case Nomenclature.DataAcqu.MDC_HF_ACTIVITY_INTENSITY: // 127
                return "MDC_HF_ACTIVITY_INTENSITY";

            case Nomenclature.DataAcqu.MDC_HF_ACT_AMB: // 1000
                return "MDC_HF_ACT_AMB";
            case Nomenclature.DataAcqu.MDC_HF_ACT_REST: // 1001
                return "MDC_HF_ACT_REST";
            case Nomenclature.DataAcqu.MDC_HF_ACT_MOTOR: // 1002
                return "MDC_HF_ACT_MOTOR";
            case Nomenclature.DataAcqu.MDC_HF_ACT_LYING: // 1003
                return "MDC_HF_ACT_LYING";
            case Nomenclature.DataAcqu.MDC_HF_ACT_SLEEP: // 1004
                return "MDC_HF_ACT_SLEEP";
            case Nomenclature.DataAcqu.MDC_HF_ACT_PHYS: // 1005
                return "MDC_HF_ACT_PHYS";
            case Nomenclature.DataAcqu.MDC_HF_ACT_SUS_PHYS: // 1006
                return "MDC_HF_ACT_SUS_PHYS";
            case Nomenclature.DataAcqu.MDC_HF_ACT_UNKNOWN: // 1007
                return "MDC_HF_ACT_UNKNOWN";
            case Nomenclature.DataAcqu.MDC_HF_ACT_MULTIPLE: // 1008
                return "MDC_HF_ACT_MULTIPLE";
            case Nomenclature.DataAcqu.MDC_HF_ACT_MONITOR: // 1009
                return "MDC_HF_ACT_MONITOR";
            case Nomenclature.DataAcqu.MDC_HF_ACT_SKI: // 1010
                return "MDC_HF_ACT_SKI";
            case Nomenclature.DataAcqu.MDC_HF_ACT_RUN: // 1011
                return "MDC_HF_ACT_RUN";
            case Nomenclature.DataAcqu.MDC_HF_ACT_BIKE: // 1012
                return "MDC_HF_ACT_BIKE";
            case Nomenclature.DataAcqu.MDC_HF_ACT_STAIR: // 1013
                return "MDC_HF_ACT_STAIR";
            case Nomenclature.DataAcqu.MDC_HF_ACT_ROW: // 1014
                return "MDC_HF_ACT_ROW";
            case Nomenclature.DataAcqu.MDC_HF_ACT_HOME: // 1015
                return "MDC_HF_ACT_HOME";
            case Nomenclature.DataAcqu.MDC_HF_ACT_WORK: // 1016
                return "MDC_HF_ACT_WORK";
            case Nomenclature.DataAcqu.MDC_HF_ACT_WALK: // 1017
                return "MDC_HF_ACT_WALK";

            case Nomenclature.DataAcqu.MDC_PULS_OXIM_PULS_RATE: // 18458
                return "MDC_PULS_OXIM_PULS_RATE";
            case Nomenclature.DataAcqu.MDC_PULS_RATE_NON_INV: // 18474
                return "MDC_PULS_RATE_NON_INV";
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV: // 18948
                return "MDC_PRESS_BLD_NONINV";
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_SYS: // 18949
                return "MDC_PRESS_BLD_NONINV_SYS";
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_DIA: // 18950
                return "MDC_PRESS_BLD_NONINV_DIA";
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_MEAN: // 18951
                return "MDC_PRESS_BLD_NONINV_MEAN";
            case Nomenclature.DataAcqu.MDC_PULS_OXIM_SAT_O2: // 19384
                return "MDC_PULS_OXIM_SAT_O2";
            case Nomenclature.DataAcqu.MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD: // 29112
                return "MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD";
            case Nomenclature.DataAcqu.MDC_MASS_BODY_ACTUAL: // 57664
                return "MDC_MASS_BODY_ACTUAL";

            case Nomenclature.DataAcqu.MDC_FLOW_AWAY_EXP_FORCED_PEAK:
                return "MDC_FLOW_AWAY_EXP_FORCED_PEAK";
            case Nomenclature.DataAcqu.MDC_FLOW_AWAY_EXP_FORCED_PEAK_PB:
                return "MDC_FLOW_AWAY_EXP_FORCED_PEAK_PB";
            case Nomenclature.DataAcqu.MDC_VOL_AWAY_EXP_FORCED_1S:
                return "MDC_VOL_AWAY_EXP_FORCED_1S";
            case Nomenclature.DataAcqu.MDC_VOL_AWAY_EXP_FORCED_EXP_6S:
                return "MDC_VOL_AWAY_EXP_FORCED_EXP_6S";
            case Nomenclature.DataAcqu.MDC_PEF_READING_STATUS:
                return "MDC_PEF_READING_STATUS";

            default:
                return "";

        }
    }

    /**
     * unit code 명 반환
     *
     * @param attrId
     * @return
     */
    public static String getUnitCodeName(int attrId) {
        switch (attrId) {
            case Nomenclature.Dimensions.MDC_DIM_DIMLESS: // 512
                return "MDC_DIM_DIMLESS";
            case Nomenclature.Dimensions.MDC_DIM_PERCENT: // 544
                return "MDC_DIM_PERCENT";
            case Nomenclature.Dimensions.MDC_DIM_ANG_DEG: // 736
                return "MDC_DIM_ANG_DEG";
            case Nomenclature.Dimensions.MDC_DIM_X_M: // 1280
                return "MDC_DIM_X_M";
            case Nomenclature.Dimensions.MDC_DIM_CENTI_M: // 1297
                return "MDC_DIM_CENTI_M";
            case Nomenclature.Dimensions.MDC_DIM_X_FOOT: // 1344
                return "MDC_DIM_X_FOOT";
            case Nomenclature.Dimensions.MDC_DIM_X_INCH: // 1376
                return "MDC_DIM_X_INCH";
            case Nomenclature.Dimensions.MDC_DIM_X_L: // 1600
                return "MDC_DIM_X_L";
            case Nomenclature.Dimensions.MDC_DIM_X_G: // 1728
                return "MDC_DIM_X_G";
            case Nomenclature.Dimensions.MDC_DIM_KILO_G: // 1731
                return "MDC_DIM_KILO_G";
            case Nomenclature.Dimensions.MDC_DIM_LB: // 1760
                return "MDC_DIM_LB";
            case Nomenclature.Dimensions.MDC_DIM_KG_PER_M_SQ: // 1952
                return "MDC_DIM_KG_PER_M_SQ";
            case Nomenclature.Dimensions.MDC_DIM_MILLI_G_PER_DL: // 2130
                return "MDC_DIM_MILLI_G_PER_DL";
            case Nomenclature.Dimensions.MDC_DIM_MIN: // 2208
                return "MDC_DIM_MIN";
            case Nomenclature.Dimensions.MDC_DIM_YR: // 2368
                return "MDC_DIM_YR";
            case Nomenclature.Dimensions.MDC_DIM_BEAT_PER_MIN: // 2720
                return "MDC_DIM_BEAT_PER_MIN";
            case Nomenclature.Dimensions.MDC_DIM_RESP_PER_MIN: // 2784
                return "MDC_DIM_RESP_PER_MIN";
            case Nomenclature.Dimensions.MDC_DIM_X_L_PER_MIN: // 3072
                return "MDC_DIM_X_L_PER_MIN";
            case Nomenclature.Dimensions.MDC_DIM_KILO_PASCAL: // 3843
                return "MDC_DIM_KILO_PASCAL";
            case Nomenclature.Dimensions.MDC_DIM_MMHG: // 3872
                return "MDC_DIM_MMHG";
            case Nomenclature.Dimensions.MDC_DIM_X_JOULES: // 3968
                return "MDC_DIM_X_JOULES";
            case Nomenclature.Dimensions.MDC_DIM_X_WATT: // 4032
                return "MDC_DIM_X_WATT";
            case Nomenclature.Dimensions.MDC_DIM_X_M_PER_MIN: // 6560
                return "MDC_DIM_X_M_PER_MIN";
            case Nomenclature.Dimensions.MDC_DIM_X_STEP: // 6656
                return "MDC_DIM_X_STEP";
            case Nomenclature.Dimensions.MDC_DIM_X_FOOT_PER_MIN: // 6688
                return "MDC_DIM_X_FOOT_PER_MIN";
            case Nomenclature.Dimensions.MDC_DIM_X_INCH_PER_MIN: // 6720
                return "MDC_DIM_X_INCH_PER_MIN";
            case Nomenclature.Dimensions.MDC_DIM_X_STEP_PER_MIN: // 6752
                return "MDC_DIM_X_STEP_PER_MIN";
            case Nomenclature.Dimensions.MDC_DIM_X_CAL: // 6784
                return "MDC_DIM_X_CAL";
            case Nomenclature.Dimensions.MDC_DIM_RPM: // 6816
                return "MDC_DIM_RPM";
            default:
                return "";
        }
    }

    /**
     * unit code 별 단위 표시
     *
     * @param unitCode
     * @return
     */
    public static String displayUnit(int unitCode) {

        switch (unitCode) {
            case Nomenclature.Dimensions.MDC_DIM_PERCENT: // 544
                return " %";
            case Nomenclature.Dimensions.MDC_DIM_X_M: // 1280
                return " m";
            case Nomenclature.Dimensions.MDC_DIM_CENTI_M: // 1297
                return " cm";
            case Nomenclature.Dimensions.MDC_DIM_X_FOOT: // 1344
                return " ft";
            case Nomenclature.Dimensions.MDC_DIM_X_INCH: // 1376
                return " in";
            case Nomenclature.Dimensions.MDC_DIM_KILO_G: // 1731
                return " kg";
            case Nomenclature.Dimensions.MDC_DIM_LB: // 1760
                return " lb";
            case Nomenclature.Dimensions.MDC_DIM_KG_PER_M_SQ: // 1952
                return " kg m-2";
            case Nomenclature.Dimensions.MDC_DIM_MILLI_G_PER_DL: // 2130
                return " mg/dL";
            case Nomenclature.Dimensions.MDC_DIM_SEC: // 2176
                return " sec";
            case Nomenclature.Dimensions.MDC_DIM_MIN: // 2208
                return " min";
            case Nomenclature.Dimensions.MDC_DIM_BEAT_PER_MIN: // 2720
                return " bpm";
            case Nomenclature.Dimensions.MDC_DIM_KILO_PASCAL: // 3843
                return " kPa";
            case Nomenclature.Dimensions.MDC_DIM_MMHG: // 3872
                return " mmHg";
            case Nomenclature.Dimensions.MDC_DIM_X_JOULES: // 3968
                return " J";
            case Nomenclature.Dimensions.MDC_DIM_X_STEP: // 6656
                return " step";
            case Nomenclature.Dimensions.MDC_DIM_X_CAL: // 6784
                return " Cal";
            default:
                return "";
        }
    }

    /**
     * Cardio 에서 activity Name 반환
     *
     * @param activity
     * @return
     */
    public static String displayActivity(int activity) {

        switch (activity) {
            case Nomenclature.DataAcqu.MDC_HF_ACT_AMB:
                return "Amb";
            case Nomenclature.DataAcqu.MDC_HF_ACT_REST:
                return "Rest";
            case Nomenclature.DataAcqu.MDC_HF_ACT_MOTOR:
                return "Motor";
            case Nomenclature.DataAcqu.MDC_HF_ACT_LYING:
                return "Lying";
            case Nomenclature.DataAcqu.MDC_HF_ACT_SLEEP:
                return "Sleep";
            case Nomenclature.DataAcqu.MDC_HF_ACT_PHYS:
                return "Phys";
            case Nomenclature.DataAcqu.MDC_HF_ACT_SUS_PHYS:
                return "SusPhys";
            case Nomenclature.DataAcqu.MDC_HF_ACT_UNKNOWN:
                return "Unknown";
            case Nomenclature.DataAcqu.MDC_HF_ACT_MULTIPLE:
                return "Multiple";
            case Nomenclature.DataAcqu.MDC_HF_ACT_MONITOR:
                return "Monitor";
            case Nomenclature.DataAcqu.MDC_HF_ACT_SKI:
                return "Ski";
            case Nomenclature.DataAcqu.MDC_HF_ACT_RUN:
                return "Run";
            case Nomenclature.DataAcqu.MDC_HF_ACT_BIKE:
                return "Bike";
            case Nomenclature.DataAcqu.MDC_HF_ACT_STAIR:
                return "Stair";
            case Nomenclature.DataAcqu.MDC_HF_ACT_ROW:
                return "Row";
            case Nomenclature.DataAcqu.MDC_HF_ACT_HOME:
                return "Home";
            case Nomenclature.DataAcqu.MDC_HF_ACT_WORK:
                return "Work";
            case Nomenclature.DataAcqu.MDC_HF_ACT_WALK:
                return "Walk";
            default:
                return null;
        }
    }

    /**
     * sub object 명 반환
     *
     * @param attrId
     * @return
     */
    public static String displaySubObjectName(int attrId) {
        switch (attrId) {
            case Nomenclature.DataAcqu.MDC_HF_ALT:
                return "Altitude";
            case Nomenclature.DataAcqu.MDC_HF_DISTANCE:
                return "Distance";
            case Nomenclature.DataAcqu.MDC_HF_ASC_TME_DIST:
                return "Ascent Time and Distance";
            case Nomenclature.DataAcqu.MDC_HF_DESC_TIME_DIST:
                return "Descent Time and Distance";
            case Nomenclature.DataAcqu.MDC_HF_LATITUDE:
                return "Latitude";
            case Nomenclature.DataAcqu.MDC_HF_LONGITUDE:
                return "Longitude";
            case Nomenclature.DataAcqu.MDC_HF_PROGRAM_ID:
                return "Program Identifier";
            case Nomenclature.DataAcqu.MDC_HF_SLOPES:
                return "Slopes";
            case Nomenclature.DataAcqu.MDC_HF_SPEED:
                return "Speed";
            case Nomenclature.DataAcqu.MDC_HF_CAD:
                return "Cadence";
            case Nomenclature.DataAcqu.MDC_HF_INCLINE:
                return "Incline";
            case Nomenclature.DataAcqu.MDC_HF_HR_MAX_USER:
                return "Max User Heart Rate";
            case Nomenclature.DataAcqu.MDC_HF_HR:
                return "Heart Rate";
            case Nomenclature.DataAcqu.MDC_HF_POWER:
                return "Power";
            case Nomenclature.DataAcqu.MDC_HF_RESIST:
                return "Resistance";
            case Nomenclature.DataAcqu.MDC_HF_STRIDE:
                return "Stride Length";
            case Nomenclature.DataAcqu.MDC_HF_ENERGY:
                return "Energy Expended";
            case Nomenclature.DataAcqu.MDC_HF_CAL_INGEST:
                return "Calories Ingested";
            case Nomenclature.DataAcqu.MDC_HF_CAL_INGEST_CARB:
                return "Cardbohydrate Calories Ingested";
            case Nomenclature.DataAcqu.MDC_HF_SUST_PA_THRESHOLD:
                return "Sustained Phys Activity Threshold";
            case Nomenclature.DataAcqu.MDC_HF_SUBSESSION:
                return "Subsession";
            case Nomenclature.DataAcqu.MDC_HF_ACTIVITY_TIME:
                return "Activity Time";
            case Nomenclature.DataAcqu.MDC_HF_AGE:
                return "Age";
            case Nomenclature.DataAcqu.MDC_HF_ACTIVITY_INTENSITY:
                return "Activity Intensity";
            default:
                return null;
        }
    }

    /**
     * Device Spec 에 따른 이름 반환
     *
     * @return
     */
    public static String getDeviceSpecName(int deviceSpec) {
        switch (deviceSpec) {
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_PULS_OXIM:
                return "MDC_DEV_SPEC_PROFILE_PULS_OXIM";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BP:
                return "MDC_DEV_SPEC_PROFILE_BP";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_TEMP:
                return "MDC_DEV_SPEC_PROFILE_TEMP";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_SCALE:
                return "MDC_DEV_SPEC_PROFILE_SCALE";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_GLUCOSE:
                return "MDC_DEV_SPEC_PROFILE_GLUCOSE";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BCA:
                return "MDC_DEV_SPEC_PROFILE_BCA";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_PEFM:
                return "MDC_DEV_SPEC_PROFILE_PEFM";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_HF_CARDIO:
                return "MDC_DEV_SPEC_PROFILE_HF_CARDIO";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_HF_STRENGTH:
                return "MDC_DEV_SPEC_PROFILE_HF_STRENGTH";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_AI_ACTIVITY_HUB:
                return "MDC_DEV_SPEC_PROFILE_AI_ACTIVITY_HUB";
            case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_AI_MED_MINDER:
                return "MDC_DEV_SPEC_PROFILE_AI_MED_MINDER";
            default:
                return "";
        }
    }

    /**
     * supplemental type 명 반환
     *
     * @param supplementalType
     * @return
     */
    public static String getSupplementalTypeName(int supplementalType) {

        switch (supplementalType) {
            case Nomenclature.DataAcqu.MDC_MODALITY_FAST:
                return "MDC_MODALITY_FAST";
            case Nomenclature.DataAcqu.MDC_MODALITY_SLOW:
                return "MDC_MODALITY_SLOW";
            case Nomenclature.DataAcqu.MDC_MODALITY_SPOT:
                return "MDC_MODALITY_SPOT";
            default:
                return "";
        }
    }

    /**
     * health device 의 profile 별 config id 확인
     *
     * @param healthProfile
     * @param devConfigId
     * @param configReportId
     * @return
     */
    public static boolean checkHealthProfileConfigId(int healthProfile, int devConfigId, int configReportId) {

        // devConfigId 와 configReportId 는 같아야 함
        if (devConfigId != configReportId) {
            return false;

        } else {

            // extended
            if ((configReportId >= 0x4000) && (configReportId <= 0x7fff)) {

                switch (healthProfile) {
                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_PULS_OXIM:
                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BP:
                        //                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_TEMP:
                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_SCALE:
                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_GLUCOSE:
                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BCA:
                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_PEFM:
                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_HF_CARDIO:
                        //                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_HF_STRENGTH:
                        //                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_AI_ACTIVITY_HUB:
                        //                    case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_AI_MED_MINDER:
                        //Log.d(TAG, "[checkHealthProfileConfigId] extended accepted config ");
                        return true;

                    default:
                        //Log.d(TAG, "[checkHealthProfileConfigId] extended unsupported config ");
                        return false;
                }

            } else {

                // device 별 standard 와 extended 범위를 체크하여 지원여부에 따른 응답 전송
                switch (configReportId) {

                    // 산소 포화도
                    case HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_400:
                    case HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_401:
                        if (healthProfile != Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_PULS_OXIM) {
                            return false;
                        }

                        return true;

                    // 혈압계
                    case HealthcareConstants.StandardDevConfigId.BLOOD_PRESSURE:
                        if (healthProfile != Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BP) {
                            return false;
                        }

                        return true;

                    // 체온계
                    //                    case HealthcareConstants.StandardDevConfigId.THERMOMETER:
                    //                        if (healthProfile != Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_TEMP) {
                    //                            return false;
                    //                        }
                    //
                    //                        return true;

                    // 체중계
                    case HealthcareConstants.StandardDevConfigId.WEIGHING_SCALE:
                        if (healthProfile != Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_SCALE) {
                            return false;
                        }

                        return true;

                    // 혈당계
                    case HealthcareConstants.StandardDevConfigId.GLUCOSE_METER_1700:
                        //                    case HealthcareConstants.StandardDevConfigId.GLUCOSE_METER_1701:
                        if (healthProfile != Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_GLUCOSE) {
                            return false;
                        }

                        return true;

                    // BCA
                    case HealthcareConstants.StandardDevConfigId.BODY_COMPOSITION_ANALYZER:
                        if (healthProfile != Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BCA) {
                            return false;
                        }

                        return true;

                    // Peak Expiratory Flow Monitor
                    case HealthcareConstants.StandardDevConfigId.PEFM:
                        if (healthProfile != Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_PEFM) {
                            return false;
                        }

                        return true;

                    //                    case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7200:
                    //                    case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7201:
                    //                    case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7202:
                    //                    case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7203:
                    //                        if (healthProfile != Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_AI_MED_MINDER) {
                    //                            return false;
                    //                        }
                    //
                    //                        return true;

                    default:
                        return false;
                }
            }
        }
    }

    /**
     * standard dev config id 여부 반환
     *
     * @param configId
     * @return
     */
    public static boolean isStandardDevConfigId(int configId) {

        switch ((short)configId) {
            case HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_400:
            case HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_401:
            case HealthcareConstants.StandardDevConfigId.BLOOD_PRESSURE:
                //            case HealthcareConstants.StandardDevConfigId.THERMOMETER:
            case HealthcareConstants.StandardDevConfigId.WEIGHING_SCALE:
            case HealthcareConstants.StandardDevConfigId.GLUCOSE_METER_1700:
                //            case HealthcareConstants.StandardDevConfigId.GLUCOSE_METER_1701:
            case HealthcareConstants.StandardDevConfigId.BODY_COMPOSITION_ANALYZER:
            case HealthcareConstants.StandardDevConfigId.PEFM:
                //            case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7200:
                //            case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7201:
                //            case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7202:
                //            case HealthcareConstants.StandardDevConfigId.ADHERENCE_MONITOR_7203:
                return true;

            default:
                return false;
        }
    }

    /**
     * <pre>
     * kg -> lbs(파운드) 로 변환
     * 소수점 다섯번째 자리에서 반올림하여 소수점 네번째 자리까지 표시
     * </pre>
     *
     * @param src lbs 로 변경하고자 하는 kg
     * @return lbs 로 변경된 데이터
     */
    public static String kgToLbs(String src) {
        double d = Double.parseDouble(src);

        double l = d * 2.20462;

//        String weight = String.format("%.1f", new BigDecimal(l).setScale(1, BigDecimal.ROUND_HALF_UP));
        String weight = String.format("%.1f", new BigDecimal(l).setScale(1, BigDecimal.ROUND_HALF_UP));
        if (weight.contains(",")) {
            weight = weight.replace(",", ".");
        }

        return weight;
    }

    /**
     * <pre>
     * lbs(파운드) -> kg 로 변환
     * 소수점 다섯번째 자리에서 반올림하여 소수점 네번째 자리까지 표시
     * </pre>
     *
     * @param src kg 로 변경하고자 하는 lbs
     * @return kb 로 변경된 데이터
     */
    public static String lbsToKg(String src) {
        double d = Double.parseDouble(src);

        double k = d * 0.453592;

        //        String weight = String.format("%.1f", new BigDecimal(k).setScale(1, BigDecimal.ROUND_HALF_UP));
        //        String weight = new BigDecimal(k).setScale(4, BigDecimal.ROUND_HALF_UP).toString();
        //        if (weight.contains(",")) {
        //            weight = weight.replace(",", ".");
        //        }
        //
        //        return weight;

        //        return new BigDecimal(k).setScale(4, BigDecimal.ROUND_HALF_UP).toString();
        //
        return new BigDecimal(k).setScale(4, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * <pre>
     * mg/dL -> mmol/L 로 변환
     * </pre>
     *
     * @param mg mmol로 변경할 mg
     * @return mg로 변경된 데이터
     */

    public static String mgToMmol(String mg) {
        double d = Double.parseDouble(mg);

        double l = d / 18.0;

        String glucose = String.format("%.1f", new BigDecimal(l).setScale(1, BigDecimal.ROUND_HALF_UP));
        if (glucose.contains(",")) {
            glucose = glucose.replace(",", ".");
        }
        return glucose;
    }

    /**
     * <pre>
     * mmol/L -> mg/dL 로 변환
     * </pre>
     *
     * @param mmol mmol로 변경할 mg
     * @return mmol로 변경된 데이터
     */

    public static String mmolToMg(String mmol) {
        double d = Double.parseDouble(mmol);

        double l = d * 18.0;

        String glucose = String.valueOf((int)Math.floor(l));
        if (glucose.contains(",")) {
            glucose = glucose.replace(",", ".");
        }
        return glucose;
    }

    /**
     * 해상도 가로 사이즈 가져오기
     *
     * @param displayMetrics
     * @return
     */
    public static int getDisWidth(DisplayMetrics displayMetrics) {
        return displayMetrics.widthPixels;
    }

    /**
     * 해상도 세로 사이즈 가져오기
     *
     * @param displayMetrics
     * @return
     */
    public static int getDisHeight(DisplayMetrics displayMetrics) {
        return displayMetrics.heightPixels;
    }

    /**
     * 속성에 따른 데이터명 반환
     *
     * @param attrId
     * @return
     */
    public static String convertDataName(int attrId) {

        switch (attrId) {
            case Nomenclature.Attribute.MDC_ATTR_TIME_PD_MSMT_ACTIVE:
                return CardiovascularFitnessActivityMonitorDevice.MDC_ATTR_TIME_PD_MSMT_ACTIVE;

            case Nomenclature.DataAcqu.MDC_HF_DISTANCE: //
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_DISTANCE;
            case Nomenclature.DataAcqu.MDC_HF_ENERGY: //
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ENERGY;
            case Nomenclature.DataAcqu.MDC_HF_SESSION: //
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_SESSION;
            case Nomenclature.DataAcqu.MDC_HF_SUBSESSION: //
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_SUBSESSION;

            case Nomenclature.DataAcqu.MDC_HF_ACT_AMB:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_AMB;
            case Nomenclature.DataAcqu.MDC_HF_ACT_REST:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_REST;
            case Nomenclature.DataAcqu.MDC_HF_ACT_MOTOR:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_MOTOR;
            case Nomenclature.DataAcqu.MDC_HF_ACT_LYING:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_LYING;
            case Nomenclature.DataAcqu.MDC_HF_ACT_SLEEP:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_SLEEP;
            case Nomenclature.DataAcqu.MDC_HF_ACT_PHYS:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_PHYS;
            case Nomenclature.DataAcqu.MDC_HF_ACT_SUS_PHYS:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_SUS_PHYS;
            case Nomenclature.DataAcqu.MDC_HF_ACT_UNKNOWN:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_UNKNOWN;
            case Nomenclature.DataAcqu.MDC_HF_ACT_MULTIPLE:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_MULTIPLE;
            case Nomenclature.DataAcqu.MDC_HF_ACT_MONITOR:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_MONITOR;
            case Nomenclature.DataAcqu.MDC_HF_ACT_SKI:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_SKI;
            case Nomenclature.DataAcqu.MDC_HF_ACT_RUN:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_RUN;
            case Nomenclature.DataAcqu.MDC_HF_ACT_BIKE:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_BIKE;
            case Nomenclature.DataAcqu.MDC_HF_ACT_STAIR:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_STAIR;
            case Nomenclature.DataAcqu.MDC_HF_ACT_ROW:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_ROW;
            case Nomenclature.DataAcqu.MDC_HF_ACT_HOME:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_HOME;
            case Nomenclature.DataAcqu.MDC_HF_ACT_WORK:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_WORK;
            case Nomenclature.DataAcqu.MDC_HF_ACT_WALK:
                return CardiovascularFitnessActivityMonitorDevice.MDC_HF_ACT_WALK;

            case Nomenclature.DataAcqu.MDC_PULS_OXIM_PULS_RATE: // 18458
                return PulseOximeterDevice.MDC_PULS_OXIM_PULS_RATE;
            case Nomenclature.DataAcqu.MDC_PULS_OXIM_SAT_O2: // 19384
                return PulseOximeterDevice.MDC_PULS_OXIM_SAT_O2;

            case Nomenclature.DataAcqu.MDC_PULS_RATE_NON_INV: // 18474
                return BloodPressureMonitorDevice.MDC_PULS_RATE_NON_INV;
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_SYS: // 18949
                return BloodPressureMonitorDevice.MDC_PRESS_BLD_NONINV_SYS;
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_DIA: // 18950
                return BloodPressureMonitorDevice.MDC_PRESS_BLD_NONINV_DIA;
            case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_MEAN: // 18951
                return BloodPressureMonitorDevice.MDC_PRESS_BLD_NONINV_MEAN;

            case Nomenclature.DataAcqu.MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD: // 29112
                return GlucoseMeterDevice.MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD;

            case Nomenclature.DataAcqu.MDC_MASS_BODY_ACTUAL: // 57664
                return WeighingScaleDevice.MDC_MASS_BODY_ACTUAL;

            case Nomenclature.DataAcqu.MDC_LEN_BODY_ACTUAL: // 57668
                return BodyCompositionAnalyzerDevice.MDC_LEN_BODY_ACTUAL;
            case Nomenclature.DataAcqu.MDC_BODY_FAT: // 57676
                return BodyCompositionAnalyzerDevice.MDC_BODY_FAT;
            case Nomenclature.DataAcqu.MDC_RATIO_MASS_BODY_LEN_SQ: // 57680
                return BodyCompositionAnalyzerDevice.MDC_RATIO_MASS_BODY_LEN_SQ;
            case Nomenclature.DataAcqu.MDC_MASS_BODY_FAT_FREE: // 57684
                return BodyCompositionAnalyzerDevice.MDC_MASS_BODY_FAT_FREE;
            case Nomenclature.DataAcqu.MDC_MASS_BODY_SOFT_LEAN: // 57688
                return BodyCompositionAnalyzerDevice.MDC_MASS_BODY_SOFT_LEAN;
            case Nomenclature.DataAcqu.MDC_BODY_WATER: // 57692
                return BodyCompositionAnalyzerDevice.MDC_BODY_WATER;

            case Nomenclature.DataAcqu.MDC_FLOW_AWAY_EXP_FORCED_PEAK: //
                return PeakExpiratoryFlowMonitorDevice.MDC_FLOW_AWAY_EXP_FORCED_PEAK;
            case Nomenclature.DataAcqu.MDC_FLOW_AWAY_EXP_FORCED_PEAK_PB: //
                return PeakExpiratoryFlowMonitorDevice.MDC_FLOW_AWAY_EXP_FORCED_PEAK_PB;
            case Nomenclature.DataAcqu.MDC_VOL_AWAY_EXP_FORCED_1S: //
                return PeakExpiratoryFlowMonitorDevice.MDC_FLOW_AWAY_EXP_FORCED_PEAK_1S;
            case Nomenclature.DataAcqu.MDC_VOL_AWAY_EXP_FORCED_EXP_6S: //
                return PeakExpiratoryFlowMonitorDevice.MDC_FLOW_AWAY_EXP_FORCED_PEAK_6S;
            case Nomenclature.DataAcqu.MDC_PEF_READING_STATUS:
                return PeakExpiratoryFlowMonitorDevice.MDC_ATTR_MSMT_STAT;

            default:
                return "";
        }
    }

    /**
     * unit code 별 단위 반환
     *
     * @param unitCode
     * @return
     */
    public static String convertUnit(int unitCode) {
        switch (unitCode) {
            case Nomenclature.Dimensions.MDC_DIM_DIMLESS: // 512
                return HL7Constants.Unit.MDC_DIM_DIMLESS;
            case Nomenclature.Dimensions.MDC_DIM_PERCENT: // 544
                return HL7Constants.Unit.MDC_DIM_PERCENT;
            case Nomenclature.Dimensions.MDC_DIM_ANG_DEG: // 736
                return HL7Constants.Unit.MDC_DIM_ANG_DEG;
            case Nomenclature.Dimensions.MDC_DIM_X_M: // 1280
                return HL7Constants.Unit.MDC_DIM_M;
            case Nomenclature.Dimensions.MDC_DIM_X_FOOT: // 1344
                return HL7Constants.Unit.MDC_DIM_FOOT;
            case Nomenclature.Dimensions.MDC_DIM_CENTI_M: // 1297
                return HL7Constants.Unit.MDC_DIM_CENTI_M;
            case Nomenclature.Dimensions.MDC_DIM_X_L: // 1600
                return HL7Constants.Unit.MDC_DIM_L;
            case Nomenclature.Dimensions.MDC_DIM_X_G: // 1728
                return HL7Constants.Unit.MDC_DIM_G;
            case Nomenclature.Dimensions.MDC_DIM_KILO_G: // 1731
                return HL7Constants.Unit.MDC_DIM_KILO_G;
            case Nomenclature.Dimensions.MDC_DIM_LB: // 1760
                return HL7Constants.Unit.MDC_DIM_LB;
            case Nomenclature.Dimensions.MDC_DIM_KG_PER_M_SQ: // 1952
                return HL7Constants.Unit.MDC_DIM_KG_PER_M_SQ;
            case Nomenclature.Dimensions.MDC_DIM_MILLI_G_PER_DL: // 2130
                return HL7Constants.Unit.MDC_DIM_MILLI_G_PER_DL;
            case Nomenclature.Dimensions.MDC_DIM_SEC: // 2176
                return HL7Constants.Unit.MDC_DIM_SEC;
            case Nomenclature.Dimensions.MDC_DIM_YR: // 2368
                return HL7Constants.Unit.MDC_DIM_YR;
            case Nomenclature.Dimensions.MDC_DIM_BEAT_PER_MIN: // 2720
                return HL7Constants.Unit.MDC_DIM_BEAT_PER_MIN;
            case Nomenclature.Dimensions.MDC_DIM_RESP_PER_MIN: // 2784
                return HL7Constants.Unit.MDC_DIM_RESP_PER_MIN;
            case Nomenclature.Dimensions.MDC_DIM_X_L_PER_MIN: // 3072
                return HL7Constants.Unit.MDC_DIM_L_PER_MIN;
            case Nomenclature.Dimensions.MDC_DIM_KILO_PASCAL: // 3843
                return HL7Constants.Unit.MDC_DIM_KILO_PASCAL;
            case Nomenclature.Dimensions.MDC_DIM_MMHG: // 3872
                return HL7Constants.Unit.MDC_DIM_MMHG;
            case Nomenclature.Dimensions.MDC_DIM_X_JOULES: // 3968
                return HL7Constants.Unit.MDC_DIM_JOULES;
            case Nomenclature.Dimensions.MDC_DIM_X_WATT: // 4032
                return HL7Constants.Unit.MDC_DIM_WATT;
            case Nomenclature.Dimensions.MDC_DIM_X_M_PER_MIN: // 6560
                return HL7Constants.Unit.MDC_DIM_M_PER_MIN;
            case Nomenclature.Dimensions.MDC_DIM_X_STEP: // 6656
                return HL7Constants.Unit.MDC_DIM_STEP;
            case Nomenclature.Dimensions.MDC_DIM_X_FOOT_PER_MIN: // 6688
                return HL7Constants.Unit.MDC_DIM_FOOT_PER_MIN;
            case Nomenclature.Dimensions.MDC_DIM_X_INCH_PER_MIN: // 6720
                return HL7Constants.Unit.MDC_DIM_INCH_PER_MIN;
            case Nomenclature.Dimensions.MDC_DIM_X_STEP_PER_MIN: // 6752
                return HL7Constants.Unit.MDC_DIM_STEP_PER_MIN;
            case Nomenclature.Dimensions.MDC_DIM_X_CAL: // 6784
                return HL7Constants.Unit.MDC_DIM_CAL;
            case Nomenclature.Dimensions.MDC_DIM_RPM: // 6816
                return HL7Constants.Unit.MDC_DIM_RPM;

            default:
                return "";
        }
    }

    /**
     * <pre>
     * BMI 반환
     * BMI 계산 공식 : weight in kg / (height in m) ^2
     *                 weight in lb / (height in inch) ^2 * k, where k = 703.0696
     * </pre>
     *
     * @param weight kg 단위 몸무게
     * @param height m 단위 키
     * @return
     */
    public static String calBMI(String weight, String height) {

        double wt = Double.parseDouble(weight);
        double ht = Double.parseDouble(height);

        double bmi = (wt / (ht * ht));

        return new BigDecimal(bmi).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * <pre>
     * cm -> inch(인치) 로 변환
     * 소수점 다섯번째 자리에서 반올림하여 소수점 네번째 자리까지 표시
     * </pre>
     *
     * @param src inch 로 변경하고자 하는 cm
     * @return inch 로 변경된 데이터
     */
    public static String cmToInch(String src) {
        double d = Double.parseDouble(src);

        double inch = d * 0.3937;

        return new BigDecimal(inch).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * <pre>
     * feet.inch -> inch 로 변환
     * </pre>
     *
     * @param src inch 로 변경하고자 하는 feet.inch
     * @return inch 로 변경된 데이터
     */
    public static String feetInchToInch(String src) {

        String arr[] = src.split("\\.");

        double d = Double.parseDouble(arr[0]);
        double s = Double.parseDouble(arr[1]);

        if (arr.length == 1) {
            s = 0;
        } else if (arr.length == 2) {
            s = Double.parseDouble(arr[1]);
        } else {
            s = Double.parseDouble(arr[1] + "." + arr[2]);
        }

        double inch = (d * 12) + s;

        return new BigDecimal(inch).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * <pre>
     * inch -> feet.inch 로 변환
     * </pre>
     *
     * @param src feet.inch 로 변경하고자 하는 inch
     * @return feet.inch 로 변경된 데이터
     */
    public static String inchToFeetInch(String src) {
        double d = Double.parseDouble(src);
        double feet = d / 12;
        double inches = d % 12;
        String arr[] = String.valueOf(feet).split("\\.");
        //        String strInch = arr[1];
        //        Double inch = null;
        //        if (strInch.length() > 2) {
        //            if (strInch.length() > 4) {
        //                inch = Double.parseDouble(strInch.substring(0, 4)) * 0.0012;
        //            } else {
        //                inch = Double.parseDouble(strInch) * 0.012;
        //            }
        //        } else {
        //            inch = Double.parseDouble(strInch) * 0.12;
        //        }
        //
        String arr1[] = String.valueOf(inches).split("\\.");
        //
        //        double doubleInch;
        //        if (arr1[1].length() > 1) {
        //            if (Integer.parseInt(arr1[1].substring(1, 2)) >= 5) {
        //                doubleInch = Double.parseDouble(arr1[0] + "." + arr1[1].substring(0, 1)) + 0.1;
        //            } else {
        //                doubleInch = Double.parseDouble(arr1[0] + "." + arr1[1].substring(0, 1));
        //            }
        //        } else {
        //
        //            doubleInch = Double.parseDouble(arr1[0] + "." + arr1[1]);
        //        }
        //
        return arr[0] + "." + arr1[0];
    }

    /**
     * <pre>
     * inch(인치) -> cm 로 변환
     * 소수점 다섯번째 자리에서 반올림하여 소수점 네번째 자리까지 표시
     * </pre>
     *
     * @param src cm 로 변경하고자 하는 inch
     * @return inch 로 변경된 데이터
     */
    public static String inchToCm(String src) {
        double d = Double.parseDouble(src);

        double k = d * 2.54;

        return new BigDecimal(k).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * App 이름 반환
     */
    public static String getAppName(Context context) {
        return context.getResources().getString(R.string.application_name);
    }

    /**
     * App 버전 반환
     */
    public static String getAppVersion(Context context) {
        String version = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * App 버전 코드
     */
    public static int getAppCode(Context context) {
        int code = 0;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        return code;
    }

    /**
     * 언어 설정
     *
     * @param language
     */
    public static void setLocale(String language, Context context) {

        Locale locale;

        if (language.contains("_")) {

            String[] result = language.split("_");

            if (result.length > 1) {
                locale = new Locale(result[0], result[1]);
            } else {
                locale = new Locale(result[0]);
            }

        } else {
            locale = new Locale(language);
        }

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    /**
     * yyyymmdd 포멧으로 날짜 변환하여 반환
     */
    public static String dateFormat(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);

        return new SimpleDateFormat("yyyyMMdd").format(cal.getTimeInMillis());
    }

    /**
     * 현재 TIMEZONE 조회 : 없음
     *
     * @return 현재시간조회 ex) +0900
     */
    public static String getCurrentTimeZoneNotColon() {

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("Z");

        df.setTimeZone(TimeZone.getDefault());

        String strTimeZone = df.format(date);

        char[] charArray = strTimeZone.toCharArray();

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < charArray.length; i++) {

            sb.append(charArray[i]);

        }

        return sb.toString();
    }

    /**
     * 날짜 형태의 보여주는 데이터의 위치 상수 클래스
     *
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @created 2016. 04. 06.
     */
    public static enum ShowFormatPosition {
                                           YEAR, MONTH, DAY, HOUR, MINUTE, SECOND
    }

    /**
     * 지역별 날짜 형태 타입 상수 클래스
     *
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @created 2016. 04. 06.
     */
    public static enum Localization {

                                     /**
                                      * 년/월/일 타입
                                      */
                                     TYPE_01,
                                     /**
                                      * 월/일/년 타입(월은 영문으로 표시됨)
                                      */
                                     TYPE_02,
                                     /**
                                      * 일/월/년 타입
                                      */
                                     TYPE_03
    }

    /**
     * String[] 형태로 들어오는 데이터를 날짜, 시간 형태로 나누여 전달
     *
     * @param localization Localization상수 참조
     * @param showFormatPosition ShowFormatPosition상수 참조
     * @param isDate 데이터에 날짜 값의 존재 여부
     * @param characterDay 날짜 사이에 들어갈 문자열 (예: "-" ==> 2016-04-06), null일경우 ""으로 처리
     * @param characterTime 시간 사이에 들어갈 문자열 (예: ":" ==> 10:45:30), null일경우 ""으로 처리
     * @param formatData data가 String데이터일 경우 데이터의 포멧을 넣어주어야 한다. (예: "20160406" ==> "yyyyMMdd", "2016-04-06" ==>
     * "yyyy-MM-dd"), Long형 또는 현재 시간일 경우 null 허용
     * @param data 날짜데이터(String 또는 Long 데이터), null일 경우 현재 시간을 가져옴
     * @return
     */
    public static String[] getDateCharacter(Localization localization,
                                            ShowFormatPosition showFormatPosition,
                                            boolean isDate,
                                            String characterDay,
                                            String characterTime,
                                            String formatData,
                                            Object data) {
        long dateLong = 0;
        SimpleDateFormat simpleDateFormat = null;
        if (data == null) {
            Date now = new Date();
            simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String measureDt = simpleDateFormat.format(now);
            try {
                dateLong = simpleDateFormat.parse(measureDt).getTime();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            if (formatData != null && !"".equals(formatData)) {
                simpleDateFormat = new SimpleDateFormat(formatData);
                try {
                    Date measureDt = simpleDateFormat.parse((String)data);
                    dateLong = measureDt.getTime();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    String[] dateArr = {"formatData와 data를 확인 필요"};
                    return dateArr;
                }
            } else {
                try {
                    dateLong = (Long)data;
                } catch (Exception e) {
                    // TODO: handle exception
                    String[] dateArr = {"formatData를 확인 필요"};
                    return dateArr;
                }
            }
        }
        String format = getFormat(localization,
                                  showFormatPosition,
                                  isDate,
                                  characterDay == null ? "" : characterDay,
                                  characterTime == null ? "" : characterTime);
        simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        String date = simpleDateFormat.format(dateLong);
        String[] dateArr = date.split("\\^");
        return dateArr;
    }

    public static String[] getBeforAndNextDate(Localization localization,
                                               ShowFormatPosition showFormatPosition,
                                               boolean isDate,
                                               String characterDay,
                                               String characterTime,
                                               int beforDateType,
                                               int beforDate,
                                               String formatData,
                                               Object data) {
        String[] dateArr;
        long dateLong = 0;
        SimpleDateFormat simpleDateFormat = null;
        Calendar calendar = Calendar.getInstance();
        try {
            if (data == null) {
                Date now = new Date();
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String measureDt = simpleDateFormat.format(now);
                try {
                    dateLong = simpleDateFormat.parse(measureDt).getTime();
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                if (formatData != null && !"".equals(formatData)) {
                    simpleDateFormat = new SimpleDateFormat(formatData);
                    try {
                        Date measureDt = simpleDateFormat.parse((String)data);
                        dateLong = measureDt.getTime();
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        dateArr = new String[] {"formatData와 data를 확인 필요"};
                        return dateArr;
                    }
                } else {
                    try {
                        dateLong = (Long)data;
                    } catch (Exception e) {
                        // TODO: handle exception
                        dateArr = new String[] {"formatData를 확인 필요"};
                        return dateArr;
                    }
                }
            }
            calendar.setTimeInMillis(dateLong);
            calendar.add(beforDateType, beforDate);
            String format = getFormat(localization,
                                      showFormatPosition,
                                      true,
                                      characterDay == null ? "" : characterDay,
                                      characterTime == null ? "" : characterTime);
            simpleDateFormat = new SimpleDateFormat(format, Locale.US);
            String date = simpleDateFormat.format(calendar.getTimeInMillis());
            dateArr = date.split("\\^");
        } catch (Exception e) {
            return new String[] {"", ""};
        }
        return dateArr;
    }

    private static String getFormat(Localization localization,
                                    ShowFormatPosition showFormatPosition,
                                    boolean isDate,
                                    String characterDay,
                                    String characterTime) {
        String format = "yyyyMMddHHmmss";
        String format1 = "";
        String format2 = "";
        if (localization == Localization.TYPE_01) {
            switch (showFormatPosition) {
                case YEAR:
                    format = "yyyy";
                    break;
                case MONTH:
                    format = "yyyy" + characterDay + "MM";
                    break;
                case DAY:
                    format = "yyyy" + characterDay + "MM" + characterDay + "dd";
                    break;
                case HOUR:
                    format1 = "yyyy" + characterDay + "MM" + characterDay + "dd" + "^" + "HH";
                    format2 = "HH";
                    format = (isDate == true ? format1 : format2);
                    break;
                case MINUTE:
                    format1 = "yyyy" + characterDay + "MM" + characterDay + "dd" + "^" + "HH" + characterTime + "mm";
                    format2 = "HH" + characterTime + "mm";
                    format = (isDate == true ? format1 : format2);
                    break;
                case SECOND:
                    format1 = "yyyy" + characterDay
                              + "MM"
                              + characterDay
                              + "dd"
                              + "^"
                              + "HH"
                              + characterTime
                              + "mm"
                              + characterTime
                              + "ss";
                    format2 = "HH" + characterTime + "mm" + characterTime + "ss";
                    format = (isDate == true ? format1 : format2);
                    break;
            }
        } else if (localization == Localization.TYPE_02) {
            switch (showFormatPosition) {
                case YEAR:
                    format = "yyyy";
                    break;
                case MONTH:
                    format = "MMM" + characterDay + "yyyy";
                    break;
                case DAY:
                    format = "MMM" + characterDay + "dd" + characterDay + "yyyy";
                    break;
                case HOUR:
                    format1 = "MMM" + characterDay + "dd" + characterDay + "yyyy" + "^" + "HH";
                    format2 = "HH";
                    format = (isDate == true ? format1 : format2);
                    break;
                case MINUTE:
                    format1 = "MMM" + characterDay + "dd" + characterDay + "yyyy" + "^" + "HH" + characterTime + "mm";
                    format2 = "HH" + characterTime + "mm";
                    format = (isDate == true ? format1 : format2);
                    break;
                case SECOND:
                    format1 = "MMM" + characterDay
                              + "dd"
                              + characterDay
                              + "yyyy"
                              + "^"
                              + "HH"
                              + characterTime
                              + "mm"
                              + characterTime
                              + "ss";
                    format2 = "HH" + characterTime + "mm" + characterTime + "ss";
                    format = (isDate == true ? format1 : format2);
                    break;
            }
        } else if (localization == Localization.TYPE_03) {
            switch (showFormatPosition) {
                case YEAR:
                    format = "yyyy";
                    break;
                case MONTH:
                    format = "MM" + characterDay + "yyyy";
                    break;
                case DAY:
                    format = "dd" + characterDay + "MM" + characterDay + "yyyy";
                    break;
                case HOUR:
                    format1 = "dd" + characterDay + "MM" + characterDay + "yyyy" + "^" + "HH";
                    format2 = "HH";
                    format = (isDate == true ? format1 : format2);
                    break;
                case MINUTE:
                    format1 = "dd" + characterDay + "MM" + characterDay + "yyyy" + "^" + "HH" + characterTime + "mm";
                    format2 = "HH" + characterTime + "mm";
                    format = (isDate == true ? format1 : format2);
                    break;
                case SECOND:
                    format1 = "dd" + characterDay
                              + "MM"
                              + characterDay
                              + "yyyy"
                              + "^"
                              + "HH"
                              + characterTime
                              + "mm"
                              + characterTime
                              + "ss";
                    format2 = "HH" + characterTime + "mm" + characterTime + "ss";
                    format = (isDate == true ? format1 : format2);
                    break;
            }
        }

        return format;
    }

    public static ManagerUtil.Localization getLocalizationType(String language) {
        if (language.equals(ManagerConstants.Language.KOR) || language.equals(ManagerConstants.Language.JPN)
            || language.equals(ManagerConstants.Language.CHN)) {
            return Localization.TYPE_01;
        } else if (language.equals(ManagerConstants.Language.ENG)) {
            return Localization.TYPE_02;
        } else {
            return Localization.TYPE_03;
        }
    }

    public static String getMonth(String num) {
        String month = "";
        switch (num) {
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
            case "Jan":
                month = "01";
                break;
            case "Feb":
                month = "02";
                break;
            case "Mar":
                month = "03";
                break;
            case "Apr":
                month = "04";
                break;
            case "May":
                month = "05";
                break;
            case "Jun":
                month = "06";
                break;
            case "Jul":
                month = "07";
                break;
            case "Aug":
                month = "08";
                break;
            case "Sep":
                month = "09";
                break;
            case "Oct":
                month = "10";
                break;
            case "Nov":
                month = "11";
                break;
            case "Dec":
                month = "12";
                break;

            default:
                break;
        }
        return month;
    }

    /**
     * map 데이터 null 체크
     */
    public static boolean mapDataNullCheck(Object data) {
        if (data != null && data.toString() != null
            && data.toString().length() > 0
            && !"null".equals(data.toString())) {
            return true;
        } else {
            return false;
        }
    }

    public static String oneDecimalPlaceDrop(String data) {
        String temp = new BigDecimal(data).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
        if ("0".equals(temp.substring(temp.indexOf(".") + 1, temp.length()))) {
            return temp.substring(0, temp.indexOf("."));
        } else {
            return temp;
        }
    }

}
