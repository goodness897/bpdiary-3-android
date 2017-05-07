package kr.co.openit.bpdiary.common.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.measure.dao.MeasureDAO;
import kr.co.openit.bpdiary.services.MedicineMeasureAlarmService;

/**
 * DBHelper
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * debugging
     */
    private final String TAG = getClass().getSimpleName();

    /**
     *
     */
    protected SQLiteDatabase db;

    /**
     * 생성자
     *
     * @param context
     */
    public DBHelper(Context context) {
        super(context, ManagerConstants.DataBase.NAME, null, ManagerConstants.DataBase.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    /**
     * 내용 입력
     *
     * @param db
     */
    private void createTables(SQLiteDatabase db) {

        try {
            db.beginTransaction();

            StringBuffer sql = new StringBuffer();

            // Medical Device System Info
            sql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_MDS_INFO).append(" (");
            sql.append(ManagerConstants.DataBase.TABLE_NAME_MDS_INFO).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_HEALTH_PROFILE).append(" INTEGER NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MAC_ADDRESS).append(" TEXT(17) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_SYSTEM_ID).append(" TEXT(32) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_CONFIG_REPORT_ID).append(" TEXT(4) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_COMPANY).append(" TEXT(128) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MODEL).append(" TEXT(128) NOT NULL)");

            db.execSQL(sql.toString());

            // BloodPressure
            sql = new StringBuffer();
            sql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_BP).append(" (");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS).append(" INTEGER NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA).append(" INTEGER NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN).append(" INTEGER NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE).append(" INTEGER NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE).append(" VARCHAR(20) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM).append(" VARCHAR(1), ");

            sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(" VARCHAR(20) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(" VARCHAR(20) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" VARCHAR(1) NOT NULL)");

            db.execSQL(sql.toString());

            // WeighingScale
            sql = new StringBuffer();
            sql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_WS).append(" (");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT).append(" DOUBLE(7,4) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT).append(" DOUBLE(7,4) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI).append(" DOUBLE(7,4) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE).append(" VARCHAR(20) NOT NULL, ");

            sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(" VARCHAR(20) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(" VARCHAR(20) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" VARCHAR(1) NOT NULL)");

            db.execSQL(sql.toString());

            // glucose
            sql = new StringBuffer();
            sql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE).append(" (");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE).append(" DOUBLE(10,4) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(" VARCHAR(1) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(" VARCHAR(2) NOT NULL, ");

            sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(" VARCHAR(20) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(" VARCHAR(20) NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(" VARCHAR(128), ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" VARCHAR(1) NOT NULL)");

            db.execSQL(sql.toString());

            // Medicine/Measure Alarm
            sql = new StringBuffer();
            sql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM).append(" (");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE).append(" TEXT NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS).append(" TEXT NOT NULL, ");
            sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME).append(" TEXT NOT NULL)");

            db.execSQL(sql.toString());

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            db.endTransaction();
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 1) {

            try {

                db.beginTransaction();

                db.execSQL("DROP TABLE IF EXISTS " + ManagerConstants.DataBase.TABLE_NAME_USER_INFO);

                db.setTransactionSuccessful();
                db.endTransaction();

            } catch (Exception e) {
                db.endTransaction();
                e.printStackTrace();
            }

            oldVersion++;
        }

        if (oldVersion == 2) {

            try {

                db.beginTransaction();

                // Blood Pressure 마이그레이션
                // TEMP TABLE 생성
                StringBuffer sql = new StringBuffer();
                sql.append("CREATE TEMP TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_BP_TEMP);
                sql.append(" AS SELECT * FROM ").append(ManagerConstants.DataBase.TABLE_NAME_BP);
                db.execSQL(sql.toString());

                // TABLE 삭제
                sql = new StringBuffer();
                sql.append("DROP TABLE IF EXISTS ").append(ManagerConstants.DataBase.TABLE_NAME_BP);
                db.execSQL(sql.toString());

                // TABLE 생성
                sql = new StringBuffer();
                sql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_BP).append(" (");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS).append(" INTEGER NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA).append(" INTEGER NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN).append(" INTEGER NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE).append(" INTEGER NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE).append(" VARCHAR(20) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM).append(" VARCHAR(1), ");

                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(" VARCHAR(128), ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(" VARCHAR(128), ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(" VARCHAR(128), ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(" VARCHAR(20) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(" VARCHAR(20) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(" VARCHAR(128), ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" VARCHAR(1) NOT NULL)");
                db.execSQL(sql.toString());

                // DATA 마이닝
                sql = new StringBuffer();
                sql.append("INSERT INTO ").append(ManagerConstants.DataBase.TABLE_NAME_BP).append(" (");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(") ");
                sql.append(" SELECT ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE).append(", ");
                sql.append("'',");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN);
                sql.append(" FROM ").append(ManagerConstants.DataBase.TABLE_NAME_BP_TEMP);
                db.execSQL(sql.toString());

                // TEMP TABLE 삭제
                sql = new StringBuffer();
                db.execSQL("DROP TABLE IF EXISTS " + ManagerConstants.DataBase.TABLE_NAME_BP_TEMP);

                // WeightingScale 마이그레이션
                // TEMP TABLE 생성
                sql = new StringBuffer();
                sql.append("CREATE TEMP TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_WS_TEMP);
                sql.append(" AS SELECT * FROM ").append(ManagerConstants.DataBase.TABLE_NAME_WS);
                db.execSQL(sql.toString());

                // TABLE 삭제
                sql = new StringBuffer();
                sql.append("DROP TABLE IF EXISTS ").append(ManagerConstants.DataBase.TABLE_NAME_WS);
                db.execSQL(sql.toString());

                // TABLE 생성
                sql = new StringBuffer();
                sql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_WS).append(" (");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT).append(" DOUBLE(7,4) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT).append(" DOUBLE(7,4) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI).append(" DOUBLE(7,4) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE).append(" VARCHAR(20) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(" VARCHAR(128) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(" VARCHAR(128) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)
                        .append(" VARCHAR(128) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(" VARCHAR(20) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(" VARCHAR(20) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(" VARCHAR(128), ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" VARCHAR(1) NOT NULL)");
                db.execSQL(sql.toString());

                // DATA 마이그레이션
                sql = new StringBuffer();
                sql.append("INSERT INTO ").append(ManagerConstants.DataBase.TABLE_NAME_WS).append(" (");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(") ");
                sql.append(" SELECT ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT).append(", ");
                sql.append("'',");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI).append(", ");
                sql.append("'',");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN);
                sql.append(" FROM ").append(ManagerConstants.DataBase.TABLE_NAME_WS_TEMP);
                db.execSQL(sql.toString());

                // TEMP TABLE 삭제
                sql = new StringBuffer();
                db.execSQL("DROP TABLE IF EXISTS " + ManagerConstants.DataBase.TABLE_NAME_WS_TEMP);

                // glucose 마이그레이션
                // TEMP TABLE 생성
                sql = new StringBuffer();
                sql.append("CREATE TEMP TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE_TEMP);
                sql.append(" AS SELECT * FROM ").append(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE);
                db.execSQL(sql.toString());

                // TABLE 삭제
                sql = new StringBuffer();
                sql.append("DROP TABLE IF EXISTS ").append(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE);
                db.execSQL(sql.toString());

                // TABLE 생성
                sql = new StringBuffer();
                sql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE).append(" (");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE).append(" DOUBLE(10,4) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(" VARCHAR(1) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(" VARCHAR(2) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(" VARCHAR(128) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(" VARCHAR(128) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)
                        .append(" VARCHAR(128) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(" VARCHAR(20) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(" VARCHAR(20) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(" VARCHAR(128), ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(" VARCHAR(1) NOT NULL)");
                db.execSQL(sql.toString());

                // DATA 마이그레이션
                sql = new StringBuffer();
                sql.append("INSERT INTO ").append(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE).append(" (");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE).append(" DOUBLE(10,4) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL).append(" VARCHAR(1) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE).append(" VARCHAR(2) NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN).append(") ");
                sql.append(" SELECT ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_INS_DT).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE).append(", ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN);
                sql.append(" FROM ").append(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE_TEMP);
                db.execSQL(sql.toString());

                // TEMP TABLE 삭제
                sql = new StringBuffer();
                db.execSQL("DROP TABLE IF EXISTS " + ManagerConstants.DataBase.TABLE_NAME_GLUCOSE_TEMP);

                // 알람 TABLE 생성
                // Medicine/Measusre Alarm
                sql = new StringBuffer();
                sql.append("CREATE TABLE ").append(ManagerConstants.DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM).append(" (");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE).append(" TEXT NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS).append(" TEXT NOT NULL, ");
                sql.append(ManagerConstants.DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME).append(" TEXT(10) NOT NULL)");
                db.execSQL(sql.toString());

                db.setTransactionSuccessful();
                db.endTransaction();

            } catch (Exception e) {
                db.endTransaction();
                e.printStackTrace();
            }

            oldVersion++;

        }

        if (oldVersion >= newVersion) {
            return;
        }
    }

    /**
     * DROP Table
     *
     * @param db
     */
    private void dropTables(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + ManagerConstants.DataBase.TABLE_NAME_MDS_INFO);

        db.execSQL("DROP TABLE IF EXISTS " + ManagerConstants.DataBase.TABLE_NAME_BP);

        db.execSQL("DROP TABLE IF EXISTS " + ManagerConstants.DataBase.TABLE_NAME_WS);

        db.execSQL("DROP TABLE IF EXISTS " + ManagerConstants.DataBase.TABLE_NAME_GLUCOSE);

        createTables(db);
    }
}
