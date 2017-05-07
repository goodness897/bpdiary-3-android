package kr.co.openit.bpdiary.common.measure.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.DataBase;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.DeviceType;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.PeriodType;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.ServerSyncYN;
import kr.co.openit.bpdiary.common.helper.DBHelper;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

;

/**
 * 측정 관련 DAO
 */
public class MeasureDAO {

    /**
     * debugging
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * context
     */
    private final Context context;

    /**
     * dbHelper
     */
    private final DBHelper dbHelper;

    /**
     * 생성자
     *
     * @param context
     */
    public MeasureDAO(Context context) {
        this.context = context;
        dbHelper = new DBHelper(this.context);
    }

    /**
     * MDS 정보 조회
     *
     * @param pMap
     * @return
     */
    public Map<String, String> selectMedicalDeviceSystem(Map<String, String> pMap) {

        String[] strSelectionArgs = {pMap.get("macAddress")};

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        Map<String, String> rMap = null;

        try {

            StringBuffer sbSql = new StringBuffer();

            sbSql.append("SELECT ");
            sbSql.append(DataBase.COLUMN_NAME_HEALTH_PROFILE).append(", ");
            sbSql.append(DataBase.COLUMN_NAME_MAC_ADDRESS).append(", ");
            sbSql.append(DataBase.COLUMN_NAME_SYSTEM_ID).append(", ");
            sbSql.append(DataBase.COLUMN_NAME_CONFIG_REPORT_ID).append(", ");
            sbSql.append(DataBase.COLUMN_NAME_COMPANY).append(", ");
            sbSql.append(DataBase.COLUMN_NAME_MODEL);

            sbSql.append(" FROM ").append(DataBase.TABLE_NAME_MDS_INFO);
            sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MAC_ADDRESS).append(" = ?");

            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            if (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_HEALTH_PROFILE,
                         Integer.toString(cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_NAME_HEALTH_PROFILE))));
                rMap.put(DataBase.COLUMN_NAME_MAC_ADDRESS,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MAC_ADDRESS)));
                rMap.put(DataBase.COLUMN_NAME_SYSTEM_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SYSTEM_ID)));
                rMap.put(DataBase.COLUMN_NAME_CONFIG_REPORT_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_CONFIG_REPORT_ID)));
                rMap.put(DataBase.COLUMN_NAME_COMPANY,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_COMPANY)));
                rMap.put(DataBase.COLUMN_NAME_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MODEL)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {

                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }

            if (db != null) {

                if (db.isOpen()) {
                    db.close();
                }
            }
        }

        return rMap;
    }

    /**
     * MDS 정보 저장
     *
     * @param pMap
     */
    public long insertMedicalDeviceSystem(Map<String, String> pMap) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long lRow = -1;

        try {

            ContentValues values = new ContentValues();

            values.put(DataBase.COLUMN_NAME_HEALTH_PROFILE, pMap.get(DataBase.COLUMN_NAME_HEALTH_PROFILE));
            values.put(DataBase.COLUMN_NAME_MAC_ADDRESS, pMap.get(DataBase.COLUMN_NAME_MAC_ADDRESS));
            values.put(DataBase.COLUMN_NAME_SYSTEM_ID, pMap.get(DataBase.COLUMN_NAME_SYSTEM_ID));
            values.put(DataBase.COLUMN_NAME_CONFIG_REPORT_ID, pMap.get(DataBase.COLUMN_NAME_CONFIG_REPORT_ID));
            values.put(DataBase.COLUMN_NAME_COMPANY, pMap.get(DataBase.COLUMN_NAME_COMPANY));
            values.put(DataBase.COLUMN_NAME_MODEL, pMap.get(DataBase.COLUMN_NAME_MODEL));

            lRow = db.insert(DataBase.TABLE_NAME_MDS_INFO, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return lRow;
    }

    /**
     * 측정 데이터의 총 개수 조회
     *
     * @param strTableName
     * @param strColumnName
     * @return
     */
    public int selectTotalCount(String strTableName, String strColumnName) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT COUNT(").append(strColumnName).append(") AS TOTAL_COUNT ");
        sbSql.append("FROM ").append(strTableName);

        Cursor cursor = null;
        int nTotalCnt = -1;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            while (cursor.moveToNext()) {
                nTotalCnt = cursor.getInt(cursor.getColumnIndex("TOTAL_COUNT"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return nTotalCnt;
    }

    /**
     * 측정 데이터의 서버 전송 여부 수정
     *
     * @param strTableName
     * @param strColumnName
     * @param strSeq
     * @return
     */
    public int updateSendToServerYN(String strTableName, String strColumnName, String strSeq) {

        // SET
        ContentValues values = new ContentValues();
        values.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, ServerSyncYN.SERVER_SYNC_Y);

        // WHERE
        String strWhere = strColumnName + " = ?";
        String[] strWhereArgs = {strSeq};

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long lRow = -1;

        try {

            lRow = db.update(strTableName, values, strWhere, strWhereArgs);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * 혈압 측정 데이터의 Message 입력
     *
     * @param strTableName
     * @param strColumnName
     * @param strSeq
     * @param strMessage
     * @return
     */
    public int updateMessage(String strTableName,
                             String strColumnName,
                             String strSeq,
                             String strMessage,
                             String strArmType) {

        // SET
        ContentValues values = new ContentValues();
        values.put(DataBase.COLUMN_NAME_MESSAGE, strMessage);
        values.put(DataBase.COLUMN_NAME_BP_ARM, strArmType);

        // WHERE
        String strWhere = strColumnName + " = ?";
        String[] strWhereArgs = {strSeq};

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long lRow = -1;

        try {
            lRow = db.update(strTableName, values, strWhere, strWhereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * 측정 데이터의 Message 입력
     *
     * @param strTableName
     * @param strColumnName
     * @param strSeq
     * @param strMessage
     * @return
     */
    public int updateMessage(String strTableName, String strColumnName, String strSeq, String strMessage) {

        // SET
        ContentValues values = new ContentValues();
        values.put(DataBase.COLUMN_NAME_MESSAGE, strMessage);

        // WHERE
        String strWhere = strColumnName + " = ?";
        String[] strWhereArgs = {strSeq};

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long lRow = -1;

        try {
            lRow = db.update(strTableName, values, strWhere, strWhereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * 측정 데이터의 식전/식후 변경
     *
     * @param strTableName
     * @param strColumnName
     * @param strSeq
     * @param strMeal
     * @return
     */
    public int updateMeal(String strTableName, String strColumnName, String strSeq, String strMeal, String strTypeValue) {

        // SET
        ContentValues values = new ContentValues();
        values.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, strMeal);
        values.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, strTypeValue);

        // WHERE
        String strWhere = strColumnName + " = ?";
        String[] strWhereArgs = {strSeq};

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long lRow = -1;

        try {
            lRow = db.update(strTableName, values, strWhere, strWhereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * 선택 측정 데이터 삭제
     *
     * @param strTableName
     * @param strColumnName
     * @param strSeq
     */
    public void deleteMeasureData(String strTableName, String strColumnName, String strSeq) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long lRow = -1;

        try {

            StringBuffer sbSql = new StringBuffer();
            sbSql.append("DELETE FROM ");
            sbSql.append(strTableName);
            sbSql.append(" WHERE ");
            sbSql.append(strColumnName).append(" = ").append(strSeq);

            db.execSQL(sbSql.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 측정 데이터 전체 삭제
     *
     * @param strUserSeq
     */
    public void deleteMeasureAllData(String strUserSeq) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {

            db.beginTransaction();

            // 혈압계
            StringBuffer sbSql = new StringBuffer();

            sbSql.append("DELETE FROM ");
            sbSql.append(DataBase.TABLE_NAME_BP);

            db.execSQL(sbSql.toString());

            // 체중계
            sbSql = new StringBuffer();

            sbSql.append("DELETE FROM ");
            sbSql.append(DataBase.TABLE_NAME_WS);
            sbSql.append(strUserSeq);

            db.execSQL(sbSql.toString());

            db.endTransaction();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 회원 정보 & 측정 데이터 전체 삭제
     */
    public void deleteDBAllData() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {

            // 혈압계
            StringBuffer sbSql = new StringBuffer();

            sbSql.append("DELETE FROM ");
            sbSql.append(DataBase.TABLE_NAME_BP);

            db.execSQL(sbSql.toString());

            // 체중계
            sbSql = new StringBuffer();

            sbSql.append("DELETE FROM ");
            sbSql.append(DataBase.TABLE_NAME_WS);

            db.execSQL(sbSql.toString());

            // 혈당

            sbSql = new StringBuffer();

            sbSql.append("DELETE FROM ");
            sbSql.append(DataBase.TABLE_NAME_GLUCOSE);

            db.execSQL(sbSql.toString());

            // 알람

            sbSql = new StringBuffer();

            sbSql.append("DELETE FROM ");
            sbSql.append(DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM);

            db.execSQL(sbSql.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * 1일 데이터 가공
     *
     * @param arrList
     * @param strType
     * @return
     */
    public ArrayList<Map<String, String>> makeDayDataList(ArrayList<Map<String, String>> arrList, String strType) {

        ArrayList<Map<String, String>> arrMakeList = new ArrayList<Map<String, String>>();

        String df = "yyyyMMdd";
        String strDate = ManagerUtil.getCurrentDateTime(df);
        String strHour = "";
        boolean isData = false;

        for (int i = 0; i < 24; i++) {

            if (i < 10) {
                strHour = "0" + String.valueOf(i);
            } else {
                strHour = String.valueOf(i);
            }

            for (int j = 0; j < arrList.size(); j++) {

                if ((strDate + strHour).equals(arrList.get(j).get(DataBase.COLUMN_NAME_MEASURE_DT))) {

                    arrMakeList.add(arrList.get(j));

                    isData = true;

                    break;
                }
            }

            if (!isData) {

                if (DeviceType.BLOOD_PRESSURE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_BP_SYS, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_TYPE, "");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate + strHour);

                    arrMakeList.add(rMap);

                } else if (DeviceType.WEIGHING_SCALE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate + strHour);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_BEFORE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate + strHour);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_AFTER.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate + strHour);

                    arrMakeList.add(rMap);

                }
            }

            isData = false;
        }

        return arrMakeList;
    }

    /**
     * 1주일 데이터 가공
     *
     * @param arrList
     * @param strType
     * @return
     */
    public ArrayList<Map<String, String>> makeWeekDataList(ArrayList<Map<String, String>> arrList, String strType) {

        ArrayList<Map<String, String>> arrMakeList = new ArrayList<Map<String, String>>();

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(TimeZone.getDefault());

        String strDate = "";
        boolean isData = false;

        for (int i = 0; i < 7; i++) {

            Date beforeDateWeek = ManagerUtil.addBeforeWeek(date, i - 6);

            df.setTimeZone(TimeZone.getDefault());

            strDate = df.format(beforeDateWeek);

            for (int j = 0; j < arrList.size(); j++) {

                if (strDate.equals(arrList.get(j).get(DataBase.COLUMN_NAME_MEASURE_DT))) {

                    arrMakeList.add(arrList.get(j));

                    isData = true;

                    break;
                }
            }

            if (!isData) {

                if (DeviceType.BLOOD_PRESSURE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_BP_SYS, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_TYPE, "");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                } else if (DeviceType.WEIGHING_SCALE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_BEFORE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_AFTER.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                }
            }

            isData = false;
        }

        return arrMakeList;
    }

    /**
     * 월 데이터 가공
     *
     * @param arrList
     * @param strType
     * @return
     */
    public ArrayList<Map<String, String>> makeMonthDataList(ArrayList<Map<String, String>> arrList, String strType) {

        ArrayList<Map<String, String>> arrMakeList = new ArrayList<Map<String, String>>();

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        df.setTimeZone(TimeZone.getDefault());

        String strDate = "";
        boolean isData = false;

        for (int i = 0; i < 31; i++) {

            Date beforeDateMonth = ManagerUtil.addBeforeMonth(date, 0, i - 30);

            df.setTimeZone(TimeZone.getDefault());

            strDate = df.format(beforeDateMonth);

            for (int j = 0; j < arrList.size(); j++) {

                if (strDate.equals(arrList.get(j).get(DataBase.COLUMN_NAME_MEASURE_DT))) {

                    arrMakeList.add(arrList.get(j));

                    isData = true;

                    break;
                }
            }

            if (!isData) {

                if (DeviceType.BLOOD_PRESSURE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_BP_SYS, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_TYPE, "");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                } else if (DeviceType.WEIGHING_SCALE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_BEFORE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_AFTER.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                }
            }

            isData = false;
        }

        return arrMakeList;
    }

    /**
     * 년 데이터 가공
     *
     * @param arrList
     * @param strType
     * @return
     */
    public ArrayList<Map<String, String>> makeYearDataList(ArrayList<Map<String, String>> arrList, String strType) {

        ArrayList<Map<String, String>> arrMakeList = new ArrayList<Map<String, String>>();

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMM");
        df.setTimeZone(TimeZone.getDefault());

        String strDate = "";
        boolean isData = false;

        for (int i = 0; i < 12; i++) {

            Date beforeDateYear = ManagerUtil.addBeforeMonth(date, i - 11, 0);

            strDate = df.format(beforeDateYear);

            for (int j = 0; j < arrList.size(); j++) {

                if (strDate.equals(arrList.get(j).get(DataBase.COLUMN_NAME_MEASURE_DT))) {

                    arrMakeList.add(arrList.get(j));

                    isData = true;

                    break;
                }
            }

            if (!isData) {

                if (DeviceType.BLOOD_PRESSURE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_BP_SYS, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_TYPE, "");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                } else if (DeviceType.WEIGHING_SCALE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_BEFORE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_AFTER.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, strDate);

                    arrMakeList.add(rMap);

                }
            }

            isData = false;
        }

        return arrMakeList;
    }

    /**
     * 1일 데이터 가공
     *
     * @param arrList
     * @param strType
     * @return
     */
    public ArrayList<Map<String, String>> makeDayDataList(ArrayList<Map<String, String>> arrList,
                                                          String strType,
                                                          String strPeriodStart,
                                                          String strPeriodEnd) {

        ArrayList<Map<String, String>> arrMakeList = new ArrayList<Map<String, String>>();

        boolean isData = false;

        int year = Integer.parseInt(strPeriodStart.substring(0, 4));
        int month = Integer.parseInt(strPeriodStart.substring(4, 6)) - 1;
        int day = Integer.parseInt(strPeriodStart.substring(6, 8));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0);
        for (int i = 0; i < 24; i++) {
            year = calendar.get(Calendar.YEAR);
            month = (calendar.get(Calendar.MONTH) + 1);
            day = calendar.get(Calendar.DATE);
            String startDay = String.format("%04d%02d%02d%02d",
                                            calendar.get(Calendar.YEAR),
                                            (calendar.get(Calendar.MONTH) + 1),
                                            calendar.get(Calendar.DATE),
                                            calendar.get(Calendar.HOUR_OF_DAY));

            for (int j = 0; j < arrList.size(); j++) {
                String measureDtMonth = arrList.get(j).get(DataBase.COLUMN_NAME_MEASURE_DT);
                if (startDay.equals(measureDtMonth)) {

                    arrMakeList.add(arrList.get(j));

                    isData = true;

                    break;
                }
            }

            if (!isData) {

                if (DeviceType.BLOOD_PRESSURE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_BP_SYS, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_TYPE, "");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.WEIGHING_SCALE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_BEFORE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "B");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_AFTER.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "A");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                }
            }

            isData = false;
            calendar.add(Calendar.HOUR_OF_DAY, 1);
        }

        return arrMakeList;
    }

    /**
     * 1주일 데이터 가공
     *
     * @param arrList
     * @param strType
     * @return
     */
    public ArrayList<Map<String, String>> makeWeekDataList(ArrayList<Map<String, String>> arrList,
                                                           String strType,
                                                           String strPeriodStart,
                                                           String strPeriodEnd) {

        ArrayList<Map<String, String>> arrMakeList = new ArrayList<Map<String, String>>();

        boolean isData = false;

        int endDay = Integer.parseInt(strPeriodEnd.substring(6, 8));

        int year = Integer.parseInt(strPeriodStart.substring(0, 4));
        int month = Integer.parseInt(strPeriodStart.substring(4, 6)) - 1;
        int day = Integer.parseInt(strPeriodStart.substring(6, 8));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        for (int i = 0; i < 7; i++) {
            year = calendar.get(Calendar.YEAR);
            month = (calendar.get(Calendar.MONTH) + 1);
            day = calendar.get(Calendar.DATE);
            String startDay = String.format("%04d%02d%02d",
                                            calendar.get(Calendar.YEAR),
                                            (calendar.get(Calendar.MONTH) + 1),
                                            calendar.get(Calendar.DATE));
            for (int j = 0; j < arrList.size(); j++) {
                String measureDtMonth = arrList.get(j).get(DataBase.COLUMN_NAME_MEASURE_DT);
                if (startDay.equals(measureDtMonth)) {

                    arrMakeList.add(arrList.get(j));

                    isData = true;

                    break;
                }
            }

            if (!isData) {

                if (DeviceType.BLOOD_PRESSURE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_BP_SYS, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_TYPE, "");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.WEIGHING_SCALE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_BEFORE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_AFTER.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                }
            }

            isData = false;
            calendar.add(Calendar.DATE, 1);
        }

        return arrMakeList;
    }

    /**
     * 월 데이터 가공
     *
     * @param arrList
     * @param strType
     * @return
     */
    public ArrayList<Map<String, String>> makeMonthDataList(ArrayList<Map<String, String>> arrList,
                                                            String strType,
                                                            String strPeriodStart,
                                                            String strPeriodEnd) {

        ArrayList<Map<String, String>> arrMakeList = new ArrayList<Map<String, String>>();

        boolean isData = false;

        int year = Integer.parseInt(strPeriodStart.substring(0, 4));
        int month = Integer.parseInt(strPeriodStart.substring(4, 6)) - 1;
        int day = Integer.parseInt(strPeriodStart.substring(6, 8));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int max = calendar.getActualMaximum(Calendar.DATE);
        for (int i = 0; i < max; i++) {
            String startDay = String.format("%04d%02d%02d",
                                            calendar.get(Calendar.YEAR),
                                            (calendar.get(Calendar.MONTH) + 1),
                                            calendar.get(Calendar.DATE));
            for (int j = 0; j < arrList.size(); j++) {
                String measureDtMonth = arrList.get(j).get(DataBase.COLUMN_NAME_MEASURE_DT);
                if (startDay.equals(measureDtMonth)) {

                    arrMakeList.add(arrList.get(j));

                    isData = true;

                    break;
                }
            }

            if (!isData) {

                if (DeviceType.BLOOD_PRESSURE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_BP_SYS, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_TYPE, "");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_BEFORE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_AFTER.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                }
            }

            isData = false;
            calendar.add(Calendar.DATE, 1);
        }

        return arrMakeList;
    }

    /**
     * 년 데이터 가공
     *
     * @param arrList
     * @param strType
     * @return
     */
    public ArrayList<Map<String, String>> makeYearDataList(ArrayList<Map<String, String>> arrList,
                                                           String strType,
                                                           String strPeriodStart,
                                                           String strPeriodEnd) {

        ArrayList<Map<String, String>> arrMakeList = new ArrayList<Map<String, String>>();

        boolean isData = false;

        int endDay = Integer.parseInt(strPeriodEnd.substring(4, 6));

        int year = Integer.parseInt(strPeriodStart.substring(0, 4));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, 0);

        for (int i = 0; i < 12; i++) {
            String startDay =
                            String.format("%04d%02d", calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) + 1));
            for (int j = 0; j < arrList.size(); j++) {
                if (startDay.equals(arrList.get(j).get(DataBase.COLUMN_NAME_MEASURE_DT))) {

                    arrMakeList.add(arrList.get(j));

                    isData = true;

                    break;
                }
            }

            if (!isData) {

                if (DeviceType.BLOOD_PRESSURE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_BP_SYS, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE, "0");
                    rMap.put(DataBase.COLUMN_NAME_BP_TYPE, "");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.WEIGHING_SCALE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI, "0");
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_BEFORE.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                } else if (DeviceType.GLUCOSE_AFTER.equals(strType)) {

                    Map<String, String> rMap = new HashMap<String, String>();

                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, "0");
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, "0");
                    rMap.put(DataBase.COLUMN_NAME_MEASURE_DT, startDay);

                    arrMakeList.add(rMap);

                }
            }

            isData = false;
            calendar.add(Calendar.MONTH, 1);
        }

        return arrMakeList;
    }

    /**
     * 혈압 측정 마지막 데이터 조회(최근 측정 데이터)
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectBPLastMeasureData() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_DIA).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_MEAN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_PULSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_ARM).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");
        sbSql.append("LIMIT 1");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_BP_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)).replaceAll(",",
                                                                                                              "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)).replaceAll(",",
                                                                                                               "."));
                }

                rMap.put(DataBase.COLUMN_NAME_BP_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_BP_ARM,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_ARM)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }

            if (dbHelper != null) {
                dbHelper.close();
            }
        }

        return arrList;
    }

    /**
     * 혈압 측정 마지막 데이터 조회(최근 입력 데이터)
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectBPLastInputData() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_DIA).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_MEAN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_PULSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_ARM).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" DESC ");
        sbSql.append("LIMIT 1");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_BP_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)).replaceAll(",",
                                                                                                              "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)).replaceAll(",",
                                                                                                               "."));
                }

                rMap.put(DataBase.COLUMN_NAME_BP_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_BP_ARM,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_ARM)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }

            if (dbHelper != null) {
                dbHelper.close();
            }
        }

        return arrList;
    }

    /**
     * 혈압 측정 데이터 리스트 조회
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectBPDataList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_DIA).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_MEAN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_PULSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_ARM).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_BP_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)).replaceAll(",",
                                                                                                              "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)).replaceAll(",",
                                                                                                               "."));
                }

                rMap.put(DataBase.COLUMN_NAME_BP_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_BP_ARM,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_ARM)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * 혈압 측정 서버 미전송 데이터 리스트 조회
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectBPNotSendDataList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {ServerSyncYN.SERVER_SYNC_N};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_DIA).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_MEAN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_PULSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_ARM).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" = ? ");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_BP_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)).replaceAll(",",
                                                                                                              "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)).replaceAll(",",
                                                                                                               "."));
                }

                rMap.put(DataBase.COLUMN_NAME_BP_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_BP_ARM,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_ARM)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * 혈압 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectBPDataListPeriodGraph(Map<String, String> pMap,
                                                                      int nPeriodType,
                                                                      String strDateLength,
                                                                      String strPeriodStart,
                                                                      String strPeriodEnd) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();

        sbSql.append("SELECT ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_BP_SYS).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_BP_DIA).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_BP_DIA).append(", ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_BP_MEAN).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_BP_MEAN).append(", ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_BP_PULSE).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_BP_PULSE).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" GROUP BY ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");
        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)).replaceAll(",",
                                                                                                              "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)).replaceAll(",",
                                                                                                               "."));
                }

                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                Log.i("sgim",
                      "COLUMN_NAME_MEASURE_DT = "
                              + cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }

            if (dbHelper != null) {
                dbHelper.close();
            }
        }

        if (PeriodType.PERIOD_DAY == nPeriodType) {
            return makeDayDataList(arrList, DeviceType.BLOOD_PRESSURE, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_WEEK == nPeriodType) {
            return makeWeekDataList(arrList, DeviceType.BLOOD_PRESSURE, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_MONTH == nPeriodType) {
            return makeMonthDataList(arrList, DeviceType.BLOOD_PRESSURE, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_YEAR == nPeriodType) {
            return makeYearDataList(arrList, DeviceType.BLOOD_PRESSURE, strPeriodStart, strPeriodEnd);
        } else {
            return makeDayDataList(arrList, DeviceType.BLOOD_PRESSURE, strPeriodStart, strPeriodEnd);
        }
    }

    /**
     * 혈압 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public Map<String, String> selectBpAvg(Map<String, String> pMap,
                                           int nPeriodType,
                                           String strDateLength,
                                           String strPeriodStart,
                                           String strPeriodEnd) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();

        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_DIA).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_MEAN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_PULSE).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");
        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;
        Map<String, String> data = null;
        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)).replaceAll(",",
                                                                                                               "."));
                }
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                arrList.add(rMap);
            }
            data = new HashMap<String, String>();
            int avgSys = 0;
            int avgDia = 0;
            int avgPulse = 0;
            for (int i = 0; i < arrList.size(); i++) {
                avgSys += Integer.parseInt(arrList.get(i).get(DataBase.COLUMN_NAME_BP_SYS));
                avgDia += Integer.parseInt(arrList.get(i).get(DataBase.COLUMN_NAME_BP_DIA));
                avgPulse += Integer.parseInt(arrList.get(i).get(DataBase.COLUMN_NAME_BP_PULSE));
            }
            avgSys = avgSys / (arrList.size());
            avgDia = avgDia / (arrList.size());
            avgPulse = avgPulse / (arrList.size());

            data.put("sys", avgSys + "");
            data.put("dia", avgDia + "");
            data.put("pulse", avgPulse + "");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }

            if (dbHelper != null) {
                dbHelper.close();
            }
        }
        return data;
    }

    /**
     * 혈압 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectBPDataListPeriodGrapth(Map<String, String> pMap, int nPeriodType) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String strPeriodStart = "";
        String strDateLength = "";

        String strPeriodEnd = ManagerUtil.getCurrentDateTime();

        if (PeriodType.PERIOD_DAY == nPeriodType) {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeDay();
            strDateLength = "10";

        } else if (PeriodType.PERIOD_WEEK == nPeriodType) {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeWeek();
            strDateLength = "8";

        } else if (PeriodType.PERIOD_MONTH == nPeriodType) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int max = calendar.getMaximum(Calendar.DATE);
            int min = calendar.getMinimum(Calendar.DATE);

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeMonth();
            strDateLength = "8";

        } else if (PeriodType.PERIOD_YEAR == nPeriodType) {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeYear();
            strDateLength = "6";

        } else if (PeriodType.PERIOD_ALL == nPeriodType) {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeYear();
            strDateLength = "14";

        } else {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeDay();
            strDateLength = "10";

        }
        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();

        sbSql.append("SELECT ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_BP_SYS).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_BP_DIA).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_BP_DIA).append(", ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_BP_MEAN).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_BP_MEAN).append(", ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_BP_PULSE).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_BP_PULSE).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" GROUP BY ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)).replaceAll(",",
                                                                                                              "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)).replaceAll(",",
                                                                                                               "."));
                }

                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }

            if (dbHelper != null) {
                dbHelper.close();
            }
        }

        if (PeriodType.PERIOD_DAY == nPeriodType) {
            return makeDayDataList(arrList, DeviceType.BLOOD_PRESSURE);
        } else if (PeriodType.PERIOD_WEEK == nPeriodType) {
            return makeWeekDataList(arrList, DeviceType.BLOOD_PRESSURE);
        } else if (PeriodType.PERIOD_MONTH == nPeriodType) {
            return makeMonthDataList(arrList, DeviceType.BLOOD_PRESSURE);
        } else if (PeriodType.PERIOD_YEAR == nPeriodType) {
            return makeYearDataList(arrList, DeviceType.BLOOD_PRESSURE);
        } else {
            return makeDayDataList(arrList, DeviceType.BLOOD_PRESSURE);
        }
    }

    /**
     * 혈압 측정 데이터 리스트 기간별 조회(리스트)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectBPDataListPeriodGraphAll(Map<String, String> pMap, int nPeriodType) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();

        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_DIA).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_MEAN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_PULSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_ARM).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");
        sbSql.append("LIMIT 50");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;
        ArrayList<Map<String, String>> arrList1 = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_BP_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)).replaceAll(",",
                                                                                                              "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)).replaceAll(",",
                                                                                                               "."));
                }

                rMap.put(DataBase.COLUMN_NAME_BP_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_BP_ARM,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_ARM)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));
                rMap.put("isOpen", "close");

                arrList.add(rMap);
            }

            arrList1 = new ArrayList<Map<String, String>>();
            for (int i = arrList.size() - 1; i >= 0; i--) {
                Map<String, String> rMap1 = new HashMap<String, String>();
                rMap1 = arrList.get(i);
                arrList1.add(rMap1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList1;
    }

    /**
     * 혈압 측정 데이터 리스트 기간별 조회(리스트)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectBPDataListPeriodList(Map<String, String> pMap, int nPeriodType) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();

        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_DIA).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_MEAN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_PULSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_BP_ARM).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_BP_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_SYS,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_DIA,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA)).replaceAll(",", "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_MEAN,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_MEAN)).replaceAll(",",
                                                                                                              "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE))) {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_BP_PULSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_PULSE)).replaceAll(",",
                                                                                                               "."));
                }

                rMap.put(DataBase.COLUMN_NAME_BP_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_BP_ARM,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_ARM)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));
                rMap.put("isOpen", "close");

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * 혈압 측정 데이터 등록
     *
     * @param pMap
     * @return
     */
    public int insertBPData(Map<String, String> pMap) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DataBase.COLUMN_NAME_BP_SYS, pMap.get(DataBase.COLUMN_NAME_BP_SYS).replaceAll(",", ""));
        values.put(DataBase.COLUMN_NAME_BP_DIA, pMap.get(DataBase.COLUMN_NAME_BP_DIA).replaceAll(",", ""));
        values.put(DataBase.COLUMN_NAME_BP_MEAN, pMap.get(DataBase.COLUMN_NAME_BP_MEAN).replaceAll(",", ""));
        values.put(DataBase.COLUMN_NAME_BP_PULSE, pMap.get(DataBase.COLUMN_NAME_BP_PULSE).replaceAll(",", ""));
        values.put(DataBase.COLUMN_NAME_BP_TYPE, pMap.get(DataBase.COLUMN_NAME_BP_TYPE));
        values.put(DataBase.COLUMN_NAME_BP_ARM, pMap.get(DataBase.COLUMN_NAME_BP_ARM));
        values.put(DataBase.COLUMN_NAME_DEVICE_ID, pMap.get(DataBase.COLUMN_NAME_DEVICE_ID));
        values.put(DataBase.COLUMN_NAME_DEVICE_MODEL, pMap.get(DataBase.COLUMN_NAME_DEVICE_MODEL));
        values.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, pMap.get(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
        values.put(DataBase.COLUMN_NAME_MEASURE_DT, pMap.get(DataBase.COLUMN_NAME_MEASURE_DT));
        values.put(DataBase.COLUMN_NAME_MESSAGE, pMap.get(DataBase.COLUMN_NAME_MESSAGE));
        values.put(DataBase.COLUMN_NAME_INS_DT, pMap.get(DataBase.COLUMN_NAME_INS_DT));
        values.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, pMap.get(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));

        long lRow = -1;

        try {

            lRow = db.insert(DataBase.TABLE_NAME_BP, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * 체중 측정 마지막 데이터 조회(최근 측정 데이터)
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectWSLastMeasureData() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_WS_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_HEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");
        sbSql.append("LIMIT 1");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_WS_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)).replaceAll(",", "."));
                }

                rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * 체중 측정 마지막 데이터 조회(최근 입력 데이터)
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectWSLastInputData() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_WS_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_HEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" DESC ");
        sbSql.append("LIMIT 1");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_WS_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)).replaceAll(",", "."));
                }

                rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * 체중 측정 서버 미전송 데이터 리스트 조회
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectWSNotSendDataList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {ServerSyncYN.SERVER_SYNC_N};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_WS_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_HEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" = ? ");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" DESC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_WS_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)).replaceAll(",", "."));
                }

                rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * 체중 측정 데이터 리스트 조회
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectWSDataList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_WS_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_HEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" DESC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_WS_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)).replaceAll(",", "."));
                }

                rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * 혈압 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectWSDataListPeriodGrapth(Map<String, String> pMap,
                                                                       int nPeriodType,
                                                                       String strDateLength,
                                                                       String strPeriodStart,
                                                                       String strPeriodEnd) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_WS_WEIGHT).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_WS_BMI).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_WS_BMI).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" GROUP BY ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)).replaceAll(",", "."));
                }

                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        if (PeriodType.PERIOD_DAY == nPeriodType) {
            return makeDayDataList(arrList, DeviceType.WEIGHING_SCALE, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_WEEK == nPeriodType) {
            return makeWeekDataList(arrList, DeviceType.WEIGHING_SCALE, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_MONTH == nPeriodType) {
            return makeMonthDataList(arrList, DeviceType.WEIGHING_SCALE, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_YEAR == nPeriodType) {
            return makeYearDataList(arrList, DeviceType.WEIGHING_SCALE, strPeriodStart, strPeriodEnd);
        } else {
            return makeDayDataList(arrList, DeviceType.WEIGHING_SCALE, strPeriodStart, strPeriodEnd);
        }
    }

    public ArrayList<Map<String, String>>
           selectWSDataListPeriodGrapthForYear(Map<String, String> pMap,
                                               String strDateLength,
                                               String strPeriodStart,
                                               String strPeriodEnd) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(String.format(" SUM(CASE WHEN IFNULL(%s,'-') <> '-' AND %s <> ''  THEN %s ELSE 0 END) / SUM(CASE WHEN IFNULL(%s,'-') <> '-' AND %s <> '' THEN 1 ELSE 0 END) ",
                                   DataBase.COLUMN_NAME_WS_WEIGHT,
                                   DataBase.COLUMN_NAME_WS_WEIGHT,
                                   DataBase.COLUMN_NAME_WS_WEIGHT,
                                   DataBase.COLUMN_NAME_WS_WEIGHT,
                                   DataBase.COLUMN_NAME_WS_WEIGHT));
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
        sbSql.append(String.format(" SUM(CASE WHEN IFNULL(%s,'-') <> '-' AND %s <> '' THEN %s ELSE 0 END) / SUM(CASE WHEN IFNULL(%s,'-') <> '-' AND %s <> '' THEN 1 ELSE 0 END ) ",
                                   DataBase.COLUMN_NAME_WS_BMI,
                                   DataBase.COLUMN_NAME_WS_BMI,
                                   DataBase.COLUMN_NAME_WS_BMI,
                                   DataBase.COLUMN_NAME_WS_BMI,
                                   DataBase.COLUMN_NAME_WS_BMI));
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_WS_BMI).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" GROUP BY ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)).replaceAll(",", "."));
                }

                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return makeYearDataList(arrList, DeviceType.WEIGHING_SCALE, strPeriodStart, strPeriodEnd);
    }

    /**
     * 체중 측정 데이터 리스트 기간별 평균 조회
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public Map<String, String> selectWSAvg(Map<String, String> pMap,
                                           int nPeriodType,
                                           String strDateLength,
                                           String strPeriodStart,
                                           String strPeriodEnd) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;
        Map<String, String> data = null;
        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)).replaceAll(",", "."));
                }
                arrList.add(rMap);
            }

            data = new HashMap<String, String>();
            float avgWeight = 0;
            float avgBmi = 0;
            for (int i = 0; i < arrList.size(); i++) {
                avgWeight += Float.parseFloat(arrList.get(i).get(DataBase.COLUMN_NAME_WS_WEIGHT));
                avgBmi += Float.parseFloat(arrList.get(i).get(DataBase.COLUMN_NAME_WS_BMI));
            }
            avgWeight = avgWeight / (arrList.size());
            avgBmi = avgBmi / (arrList.size());

            data.put("avgWeight", avgWeight + "");
            data.put("avgBmi", avgBmi + "");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return data;
    }

    /**
     * 체중 측정 데이터 리스트 기간별 조회(리스트)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectWSDataListPeriodGraphAll(Map<String, String> pMap, int nPeriodType) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();

        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_WS_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_HEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");
        sbSql.append("LIMIT 50");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;
        ArrayList<Map<String, String>> arrList1 = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_WS_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)).replaceAll(",", "."));
                }

                rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));
                rMap.put("isOpen", "close");

                arrList.add(rMap);
            }
            arrList1 = new ArrayList<Map<String, String>>();
            for (int i = arrList.size() - 1; i >= 0; i--) {
                Map<String, String> rMap1 = new HashMap<String, String>();
                rMap1 = arrList.get(i);
                arrList1.add(rMap1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList1;
    }

    /**
     * 체중 측정 데이터 리스트 기간별 조회(리스트)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectWSDataListPeriodList(Map<String, String> pMap, int nPeriodType) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();

        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_WS_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_HEIGHT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_WS_BMI_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_WS_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_HEIGHT,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_HEIGHT)).replaceAll(",",
                                                                                                                "."));
                }

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI))) {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_WS_BMI,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI)).replaceAll(",", "."));
                }

                rMap.put(DataBase.COLUMN_NAME_WS_BMI_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));
                rMap.put("isOpen", "close");

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * 체중 측정 데이터 등록
     *
     * @param pMap
     * @return
     */
    public int insertWSData(Map<String, String> pMap) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DataBase.COLUMN_NAME_WS_WEIGHT,
                   pMap.get(DataBase.COLUMN_NAME_WS_WEIGHT).toString().replaceAll(",", "."));
        values.put(DataBase.COLUMN_NAME_WS_HEIGHT, pMap.get(DataBase.COLUMN_NAME_WS_HEIGHT).toString());
        values.put(DataBase.COLUMN_NAME_WS_BMI, pMap.get(DataBase.COLUMN_NAME_WS_BMI).toString().replaceAll(",", "."));
        values.put(DataBase.COLUMN_NAME_WS_BMI_TYPE, pMap.get(DataBase.COLUMN_NAME_WS_BMI_TYPE));
        values.put(DataBase.COLUMN_NAME_DEVICE_ID, pMap.get(DataBase.COLUMN_NAME_DEVICE_ID));
        values.put(DataBase.COLUMN_NAME_DEVICE_MODEL, pMap.get(DataBase.COLUMN_NAME_DEVICE_MODEL));
        values.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, pMap.get(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
        values.put(DataBase.COLUMN_NAME_MESSAGE, pMap.get(DataBase.COLUMN_NAME_MESSAGE));
        values.put(DataBase.COLUMN_NAME_MEASURE_DT, pMap.get(DataBase.COLUMN_NAME_MEASURE_DT));
        values.put(DataBase.COLUMN_NAME_INS_DT, pMap.get(DataBase.COLUMN_NAME_INS_DT));
        values.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, pMap.get(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));

        long lRow = -1;

        try {

            lRow = db.insert(DataBase.TABLE_NAME_WS, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * Glucose 측정 마지막 데이터 조회(최근 측정 데이터)
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectGlucoseLastMeasureData() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");
        sbSql.append("LIMIT 1");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                              "."));
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));
                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * Glucose 측정 마지막 데이터 조회(최근 입력 데이터)
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectGlucoseLastInputData() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" DESC ");
        sbSql.append("LIMIT 1");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                              "."));
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));
                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * Glucose 측정 서버 미전송 데이터 리스트 조회
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectGlucoseNotSendDataList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {ServerSyncYN.SERVER_SYNC_N};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" = ? ");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" DESC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                              "."));
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));
                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * Glucose 측정 데이터 리스트 조회
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectGlucoseDataList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" DESC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_SEQ)));

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                              "."));
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));
                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * Glucose 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectGlucoseDataListPeriodGraph(Map<String, String> pMap,
                                                                           int nPeriodType,
                                                                           String strDateLength,
                                                                           String strPeriodStart,
                                                                           String strPeriodEnd) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_GLUCOSE).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" AND ").append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(" = 'B' ");
        sbSql.append(" GROUP BY ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                                  "."));
                    }
                } else {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))
                                                            .replaceAll(",", ".")));
                    }
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));

                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        ArrayList<Map<String, String>> allList = null;
        ArrayList<Map<String, String>> afterList = null;
        afterList = selectGlucoseDataListAfterPeriodGraph(nPeriodType, strDateLength, strPeriodStart, strPeriodEnd);

        if (PeriodType.PERIOD_DAY == nPeriodType) {
            allList = makeDayDataList(arrList, DeviceType.GLUCOSE_BEFORE, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_WEEK == nPeriodType) {
            allList = makeWeekDataList(arrList, DeviceType.GLUCOSE_BEFORE, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_MONTH == nPeriodType) {
            allList = makeMonthDataList(arrList, DeviceType.GLUCOSE_BEFORE, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_YEAR == nPeriodType) {
            allList = makeYearDataList(arrList, DeviceType.GLUCOSE_BEFORE, strPeriodStart, strPeriodEnd);
        } else {
            allList = makeDayDataList(arrList, DeviceType.GLUCOSE_BEFORE, strPeriodStart, strPeriodEnd);
        }

        for (int i = 0; i < afterList.size(); i++) {
            Map<String, String> temp = new HashMap<String, String>();
            allList.get(i).put(DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                               afterList.get(i).get(DataBase.COLUMN_NAME_GLUCOSE_AFTER));
            if (Double.parseDouble(allList.get(i).get(DataBase.COLUMN_NAME_GLUCOSE_BEFORE)) > 0) {
                allList.get(i).put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, ManagerConstants.EatType.GLUCOSE_ALL);
            } else {
                allList.get(i).put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, ManagerConstants.EatType.GLUCOSE_AFTER);
            }
        }

        return allList;
    }

    /**
     * Glucose 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>>
           selectGlucoseDataListAfterPeriodGraph(int nPeriodType,
                                                 String strDateLength,
                                                 String strPeriodStart,
                                                 String strPeriodEnd) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_GLUCOSE).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" AND ").append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(" = 'A' ");
        sbSql.append(" GROUP BY ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                                  "."));
                    }
                } else {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_AFTER,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))
                                                            .replaceAll(",", ".")));
                    }
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));

                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        ArrayList<Map<String, String>> afterList = null;

        if (PeriodType.PERIOD_DAY == nPeriodType) {
            afterList = makeDayDataList(arrList, DeviceType.GLUCOSE_AFTER, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_WEEK == nPeriodType) {
            afterList = makeWeekDataList(arrList, DeviceType.GLUCOSE_AFTER, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_MONTH == nPeriodType) {
            afterList = makeMonthDataList(arrList, DeviceType.GLUCOSE_AFTER, strPeriodStart, strPeriodEnd);
        } else if (PeriodType.PERIOD_YEAR == nPeriodType) {
            afterList = makeYearDataList(arrList, DeviceType.GLUCOSE_AFTER, strPeriodStart, strPeriodEnd);
        } else {
            afterList = makeDayDataList(arrList, DeviceType.GLUCOSE_AFTER, strPeriodStart, strPeriodEnd);
        }

        return afterList;
    }

    /**
     * Glucose 식전 평균
     *
     * @return
     */
    public Map<String, String> searchGlucoseBeforeAvg(String strDateLength,
                                                      String strPeriodStart,
                                                      String strPeriodEnd) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" AND ").append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(" = 'B' ");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;
        Map<String, String> data = null;
        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                                  "."));
                    }
                } else {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))
                                                            .replaceAll(",", ".")));
                    }
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));

                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));

                arrList.add(rMap);
            }

            data = new HashMap<String, String>();
            double avgGlucoseBefore = 0;
            double avgGlucoseAfter = 0;
            double glucoseBeforeCount = 0;

            for (int i = 0; i < arrList.size(); i++) {
                if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(arrList.get(i).get(DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                    avgGlucoseBefore += Double.parseDouble(arrList.get(i).get(DataBase.COLUMN_NAME_GLUCOSE));
                    glucoseBeforeCount++;
                }
            }

            if (avgGlucoseBefore > 0) {
                avgGlucoseBefore = avgGlucoseBefore / glucoseBeforeCount;
            } else {
                avgGlucoseBefore = 0;
            }

            avgGlucoseAfter = searchGlucoseAfterAvg(strDateLength, strPeriodStart, strPeriodEnd);

            data.put("glucoseBefore", avgGlucoseBefore + "");
            data.put("glucoseAfter", avgGlucoseAfter + "");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return data;
    }

    /**
     * Glucose 식후 평균
     *
     * @return
     */
    public double searchGlucoseAfterAvg(String strDateLength, String strPeriodStart, String strPeriodEnd) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" AND ").append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(" = 'A' ");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;
        double avgGlucoseAfter = 0;
        double glucoseAfterCount = 0;
        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                                  "."));
                    }
                } else {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))
                                                            .replaceAll(",", ".")));
                    }
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));

                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));

                arrList.add(rMap);
            }

            for (int i = 0; i < arrList.size(); i++) {
                if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(arrList.get(i)
                                                                         .get(DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                    avgGlucoseAfter += Double.parseDouble(arrList.get(i).get(DataBase.COLUMN_NAME_GLUCOSE));
                    glucoseAfterCount++;
                }
            }

            if (avgGlucoseAfter > 0) {
                avgGlucoseAfter = avgGlucoseAfter / glucoseAfterCount;
            } else {
                avgGlucoseAfter = 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return avgGlucoseAfter;
    }

    /**
     * Glucose 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectGlucoseDataListPeriodGraph(Map<String, String> pMap, int nPeriodType) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String strPeriodStart = "";
        String strDateLength = "";

        String strPeriodEnd = ManagerUtil.getCurrentDateTime();

        if (PeriodType.PERIOD_DAY == nPeriodType) {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeDay();
            strDateLength = "10";

        } else if (PeriodType.PERIOD_WEEK == nPeriodType) {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeWeek();
            strDateLength = "8";

        } else if (PeriodType.PERIOD_MONTH == nPeriodType) {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeMonth();
            strDateLength = "8";

        } else if (PeriodType.PERIOD_YEAR == nPeriodType) {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeYear();
            strDateLength = "6";

        } else {

            strPeriodStart = ManagerUtil.getCurrentDateTimeBeforeDay();
            strDateLength = "10";

        }

        String[] strSelectionArgs = {strPeriodStart, strPeriodEnd};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(" AVG( ").append(DataBase.COLUMN_NAME_GLUCOSE).append(" ) ");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" AS ").append(DataBase.COLUMN_NAME_MEASURE_DT);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEASURE_DT);
        sbSql.append(" BETWEEN ").append(" ? ");
        sbSql.append(" AND ").append(" ? ");
        sbSql.append(" GROUP BY ");
        sbSql.append(" SUBSTR(").append(DataBase.COLUMN_NAME_MEASURE_DT).append(", 1, ");
        sbSql.append(strDateLength).append(")");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_INS_DT).append(" ASC ");
        sbSql.append(", ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" ASC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                } else {
                    rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                             cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                              "."));
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));

                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        if (PeriodType.PERIOD_DAY == nPeriodType) {
            return makeDayDataList(arrList, DeviceType.WEIGHING_SCALE);
        } else if (PeriodType.PERIOD_WEEK == nPeriodType) {
            return makeWeekDataList(arrList, DeviceType.WEIGHING_SCALE);
        } else if (PeriodType.PERIOD_MONTH == nPeriodType) {
            return makeMonthDataList(arrList, DeviceType.WEIGHING_SCALE);
        } else if (PeriodType.PERIOD_YEAR == nPeriodType) {
            return makeYearDataList(arrList, DeviceType.WEIGHING_SCALE);
        } else {
            return makeDayDataList(arrList, DeviceType.WEIGHING_SCALE);
        }
    }

    /**
     * Glucose 측정 데이터 리스트 기간별 조회(리스트)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectGlucoseDataListPeriodGraphAll(Map<String, String> pMap,
                                                                              int nPeriodType) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();

        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");
        sbSql.append("LIMIT 50");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;
        ArrayList<Map<String, String>> arrList1 = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_SEQ)));

                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                                  "."));
                    }
                } else {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE_BEFORE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))
                                                            .replaceAll(",", ".")));
                    }
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));
                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));
                rMap.put("isOpen", "close");

                arrList.add(rMap);
            }
            arrList1 = new ArrayList<Map<String, String>>();
            for (int i = arrList.size() - 1; i >= 0; i--) {
                Map<String, String> rMap1 = new HashMap<String, String>();
                rMap1 = arrList.get(i);
                arrList1.add(rMap1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList1;
    }

    /**
     * Glucose 측정 데이터 리스트 기간별 조회(리스트)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public ArrayList<Map<String, String>> selectGlucoseDataListPeriodList(Map<String, String> pMap, int nPeriodType) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        StringBuffer sbSql = new StringBuffer();

        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_INS_DT).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MESSAGE);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEASURE_DT).append(" DESC ");

        Cursor cursor = null;
        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), null);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_SEQ)));

                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE)).replaceAll(",",
                                                                                                                  "."));
                    }
                } else {
                    if (cursor.isNull(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))) {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))));
                    } else {
                        rMap.put(DataBase.COLUMN_NAME_GLUCOSE,
                                 ManagerUtil.mgToMmol(cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE))
                                                            .replaceAll(",", ".")));
                    }
                }

                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL)));
                rMap.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_TYPE)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_ID,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_ID)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MODEL,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MODEL)));
                rMap.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)));
                rMap.put(DataBase.COLUMN_NAME_MEASURE_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEASURE_DT)));
                rMap.put(DataBase.COLUMN_NAME_INS_DT,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_INS_DT)));
                rMap.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)));
                rMap.put(DataBase.COLUMN_NAME_MESSAGE,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MESSAGE)));
                rMap.put("isOpen", "close");

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return arrList;
    }

    /**
     * Glucose 측정 데이터 등록
     *
     * @param pMap
     * @return
     */
    public int insertGlucoseData(Map<String, String> pMap) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DataBase.COLUMN_NAME_GLUCOSE,
                   pMap.get(DataBase.COLUMN_NAME_GLUCOSE).toString().replaceAll(",", "."));
        values.put(DataBase.COLUMN_NAME_GLUCOSE_MEAL, pMap.get(DataBase.COLUMN_NAME_GLUCOSE_MEAL).toString());
        values.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, pMap.get(DataBase.COLUMN_NAME_GLUCOSE_TYPE));
        values.put(DataBase.COLUMN_NAME_DEVICE_ID, pMap.get(DataBase.COLUMN_NAME_DEVICE_ID));
        values.put(DataBase.COLUMN_NAME_DEVICE_MODEL, pMap.get(DataBase.COLUMN_NAME_DEVICE_MODEL));
        values.put(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, pMap.get(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
        values.put(DataBase.COLUMN_NAME_MESSAGE, pMap.get(DataBase.COLUMN_NAME_MESSAGE));
        values.put(DataBase.COLUMN_NAME_MEASURE_DT, pMap.get(DataBase.COLUMN_NAME_MEASURE_DT));
        values.put(DataBase.COLUMN_NAME_INS_DT, pMap.get(DataBase.COLUMN_NAME_INS_DT));
        values.put(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, pMap.get(DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));

        long lRow = -1;

        try {

            lRow = db.insert(DataBase.TABLE_NAME_GLUCOSE, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * 혈압&체중&Glucose 데이터 마이그레이션
     *
     * @return
     */
    public int migrationMeasureData() {

        long lRow = -1;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;

        try {
            StringBuffer sbSql = new StringBuffer();
            sbSql.append("ALTER TABLE ").append(DataBase.TABLE_NAME_BP);
            sbSql.append(" ADD ").append(DataBase.COLUMN_NAME_BP_ARM).append(" VARCHAR(1)");
            db.execSQL(sbSql.toString());

            // glucose 테이블 생성
            sbSql = new StringBuffer();
            sbSql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE).append(" (");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE).append(" DOUBLE(10,4) NOT NULL, ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(" VARCHAR(1) NOT NULL, ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(" VARCHAR(2) NOT NULL, ");

            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(" VARCHAR(128), ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(" VARCHAR(128), ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(" VARCHAR(128), ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(" VARCHAR(20) NOT NULL, ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(" VARCHAR(20) NOT NULL, ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(" VARCHAR(128), ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" VARCHAR(1) NOT NULL)");

            db.execSQL(sbSql.toString());

            //혈압 마이그레이션 실행
            sbSql = new StringBuffer();
            sbSql.append("SELECT ");
            sbSql.append(DataBase.COLUMN_NAME_BP_SEQ).append(", ");
            sbSql.append(DataBase.COLUMN_NAME_BP_SYS).append(", ");
            sbSql.append(DataBase.COLUMN_NAME_BP_DIA);

            sbSql.append(" FROM ").append(DataBase.TABLE_NAME_BP);

            cursor = db.rawQuery(sbSql.toString(), null);

            while (cursor.moveToNext()) {

                String strSysValue = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SYS));
                String strDiaValue = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_DIA));
                String strTypeValue = HealthcareUtil.getBloodPressureType(context, strSysValue, strDiaValue);

                // SET
                ContentValues values = new ContentValues();
                values.put(DataBase.COLUMN_NAME_BP_TYPE, strTypeValue);

                // WHERE
                String strWhere = DataBase.COLUMN_NAME_BP_SEQ + " = ?";
                String[] strWhereArgs = {cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_BP_SEQ))};

                db = dbHelper.getWritableDatabase();

                lRow = db.update(DataBase.TABLE_NAME_BP, values, strWhere, strWhereArgs);

            }

            //체중 마이그레이션 실행
            sbSql = new StringBuffer();
            sbSql.append("SELECT ");
            sbSql.append(DataBase.COLUMN_NAME_WS_SEQ).append(", ");
            sbSql.append(DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
            sbSql.append(DataBase.COLUMN_NAME_WS_BMI);

            sbSql.append(" FROM ").append(DataBase.TABLE_NAME_WS);

            cursor = db.rawQuery(sbSql.toString(), null);

            while (cursor.moveToNext()) {

                String strWeightValue = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_WEIGHT));
                String strBmiValue = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_BMI));
                String strTypeValue = HealthcareUtil.getWeighingScaleBmiType(strBmiValue);

                double wt = Double.parseDouble(strWeightValue);
                double bmi = Double.parseDouble(strBmiValue);

                String strHeightValue = String.valueOf((int)(Math.sqrt(wt / bmi) * 100.0));

                // SET
                ContentValues values = new ContentValues();
                values.put(DataBase.COLUMN_NAME_WS_BMI_TYPE, strTypeValue);
                values.put(DataBase.COLUMN_NAME_WS_HEIGHT, strHeightValue);

                // WHERE
                String strWhere = DataBase.COLUMN_NAME_WS_SEQ + " = ?";
                String[] strWhereArgs = {cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_WS_SEQ))};

                db = dbHelper.getWritableDatabase();

                lRow = db.update(DataBase.TABLE_NAME_WS, values, strWhere, strWhereArgs);

            }

            // Medicine/Measure Alarm
            sbSql = new StringBuffer();
            sbSql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM).append(" (");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE).append(" TEXT NOT NULL, ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS).append(" TEXT NOT NULL, ");
            sbSql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME).append(" TEXT NOT NULL)");

            db.execSQL(sbSql.toString());

            //Glucose 마이그레이션 실행
            //            sbSql = new StringBuffer();
            //            sbSql.append("SELECT ");
            //            sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(", ");
            //            sbSql.append(DataBase.COLUMN_NAME_GLUCOSE);
            //            sbSql.append(DataBase.COLUMN_NAME_GLUCOSE_MEAL);
            //            sbSql.append(" FROM ").append(DataBase.TABLE_NAME_GLUCOSE);
            //
            //            cursor = db.rawQuery(sbSql.toString(), null);
            //
            //            while (cursor.moveToNext()) {
            //                String strGlucoseValue = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE));
            //                String strMealValue = cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_MEAL));
            //                String strTypeValue = HealthcareUtil.getWeightType(strGlucoseValue, strMealValue);
            //
            //                // SET
            //                ContentValues values = new ContentValues();
            //                values.put(DataBase.COLUMN_NAME_GLUCOSE_TYPE, strTypeValue);
            //
            //                // WHERE
            //                String strWhere = DataBase.COLUMN_NAME_GLUCOSE_SEQ + " = ?";
            //                String[] strWhereArgs = {cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_GLUCOSE_SEQ))};
            //
            //                db = dbHelper.getWritableDatabase();
            //
            //                lRow = db.update(DataBase.TABLE_NAME_GLUCOSE, values, strWhere, strWhereArgs);
            //
            //            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * 복약/측정 알림 데이터 등록
     *
     * @param pMap
     * @return
     */
    public int insertMedicineMeasureAlarmData(Map<String, String> pMap) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                   pMap.get(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE));
        values.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                   pMap.get(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS));
        values.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME,
                   pMap.get(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME));

        long lRow = -1;

        try {

            lRow = db.insert(DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM, null, values);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        Log.d("TEST", "LL" + lRow + "" + " , " + values.get(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE));
        return (int)lRow;
    }

    /**
     * 복약/측정 노티SEQ 조회 (가장 최근의)
     *
     * @return
     */
    public int selectMedicineMeasureData(String alarmFlag) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {alarmFlag};

        StringBuffer sbsql = new StringBuffer();
        sbsql.append("SELECT ");
        sbsql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ).append(", ");
        sbsql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE).append(", ");
        sbsql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS).append(", ");
        sbsql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME);

        sbsql.append(" FROM ").append(DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM);
        sbsql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE).append(" = ?");
        sbsql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ).append(" DESC ");
        sbsql.append("LIMIT 1");

        Cursor cursor = null;

        int result = 0;

        try {
            cursor = db.rawQuery(sbsql.toString(), strSelectionArgs);

            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    result = cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (db != null) {
                db.close();
            }

            return result;
        }
    }

    /**
     * 복약 데이터 리스트 조회
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectMedicineDataList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE).append(" = ?");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME).append(" ASC ");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ,
                         Integer.toString(cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ))));
                rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                         Integer.toString(cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE))));

                rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)));

                rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {

                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }

            if (db != null) {

                if (db.isOpen()) {
                    db.close();
                }
            }
        }

        return arrList;
    }

    /**
     * 측정 데이터 리스트 조회
     *
     * @return
     */
    public ArrayList<Map<String, String>> selectMeasureDataList() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] strSelectionArgs = {ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y};

        StringBuffer sbSql = new StringBuffer();
        sbSql.append("SELECT ");
        sbSql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS).append(", ");
        sbSql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME);

        sbSql.append(" FROM ").append(DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM);
        sbSql.append(" WHERE ").append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE).append(" = ?");
        sbSql.append(" ORDER BY ").append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME).append(" ASC ");

        Cursor cursor = null;

        ArrayList<Map<String, String>> arrList = null;

        try {

            // 쿼리 실행
            cursor = db.rawQuery(sbSql.toString(), strSelectionArgs);

            arrList = new ArrayList<Map<String, String>>();

            Map<String, String> rMap = null;

            while (cursor.moveToNext()) {

                rMap = new HashMap<String, String>();

                rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ,
                         Integer.toString(cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ))));
                rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE,
                         Integer.toString(cursor.getInt(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE))));

                rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS)));

                rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME,
                         cursor.getString(cursor.getColumnIndex(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME)));

                arrList.add(rMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {

                if (!cursor.isClosed()) {
                    cursor.close();
                }
            }

            if (db != null) {

                if (db.isOpen()) {
                    db.close();
                }
            }
        }

        return arrList;
    }

    /**
     * 복약/측정 데이터 삭제
     *
     * @param strTableName
     * @param strSeq
     * @param strSeq2
     */
    public int deleteMedicineMeasureData(String strTableName, String strSeq, int strSeq2) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long lRow = -1;

        try {

            StringBuffer sbSql = new StringBuffer();
            sbSql.append("DELETE FROM ");
            sbSql.append(strTableName);
            sbSql.append(" WHERE ")
                 .append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE)
                 .append(" = ")
                 .append("'" + strSeq + "'");// 복약/측정 플래그
            sbSql.append(" AND ");
            sbSql.append(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ).append(" = ").append(strSeq2);// 노티 SEQ

            db.execSQL(sbSql.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * 복약/측정 데이터 전체 삭제
     *
     * @param strTableName
     */
    public int deleteMedicineMeasureWholeData(String strTableName) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long lRow = -1;

        try {

            StringBuffer sbSql = new StringBuffer();
            sbSql.append("DELETE FROM ");
            sbSql.append(strTableName);

            db.execSQL(sbSql.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }

    /**
     * 복약/측정 알림 토글상태 갱신
     *
     * @param strSeq
     * @param strSeq2
     * @param toggleStatus
     * @return
     */
    public int updateMedicineMeasureToggleState(String strSeq, String strSeq2, String toggleStatus) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // SET
        ContentValues values = new ContentValues();
        values.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS, toggleStatus);

        // WHERE
        String strWhere = DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE + " = ?"
                          + " AND "
                          + DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ
                          + " = ?";
        String[] strWhereArgs = {strSeq, strSeq2};

        long lRow = -1;

        try {
            lRow = db.update(DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM, values, strWhere, strWhereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
            }
        }

        return (int)lRow;
    }
}
