package kr.co.openit.bpdiary.activity.bp;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthConstants.SessionMeasurement;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthDeviceManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionKey;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionResult;
import com.samsung.android.sdk.healthdata.HealthPermissionManager.PermissionType;
import com.samsung.android.sdk.healthdata.HealthResultHolder;
import com.samsung.android.sdk.healthdata.HealthResultHolder.BaseResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.ActivityResultCode;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.DataBase;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * 혈압계 수동 입력 Activity
 */
public class BPGFitSHealthActivity extends NonMeasureActivity {

    /**
     * debugging
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * Google Fit
     */
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;

    private static final String AUTH_PENDING = "auth_state_pending";

    public static final String SESSION_NAME_BLOOD_PRESSURE = "Measure Blood Pressure";

    /**
     * S Health
     */
    private HealthDataStore mStore;

    private PermissionKey mPermissionKey;

    /**
     * 데이터 DB 저장용 Map
     */
    private Map<String, String> requestMap;

    private String strSys = null;

    private String strDia = null;

    private String strPulse = null;

    private String strMean = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bp_ws_for_gfit_shealth);

        Intent intent = getIntent();

        strSys = intent.getStringExtra("sys");
        strDia = intent.getStringExtra("dia");
        strPulse = intent.getStringExtra("pulse");
        strMean = intent.getStringExtra("mean");
        if (strMean.isEmpty()) {
            strMean = String.valueOf(Double.parseDouble(strDia)
                    + ((Double.parseDouble(strSys) - Double.parseDouble(strPulse)) / 3));
        }

        if (strPulse.contains(".")) {
            strPulse = strPulse.substring(0, strPulse.indexOf("."));
        }

        requestMap = new HashMap<String, String>();
        requestMap.put(DataBase.COLUMN_NAME_BP_SYS, strSys);
        requestMap.put(DataBase.COLUMN_NAME_BP_DIA, strDia);
        requestMap.put(DataBase.COLUMN_NAME_BP_MEAN, strMean);
        requestMap.put(DataBase.COLUMN_NAME_BP_PULSE, strPulse);

        /**
         * Google Fit
         */
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        buildFitnessClient();

        /**
         * S 헬스
         */
        mPermissionKey = new PermissionKey(HealthConstants.BloodPressure.HEALTH_DATA_TYPE, PermissionType.WRITE);

        HealthDataService healthDataService = new HealthDataService();

        try {
            healthDataService.initialize(BPGFitSHealthActivity.this);
        } catch (Exception e) {
            // Handles exception
        }

        mStore = new HealthDataStore(BPGFitSHealthActivity.this, mConnectionListener);

        onConnectGFitSHealth();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        if (mClient.isConnected()) {
            mClient.disconnect();
        }

        if (mStore != null) {
            mStore.disconnectService();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onConnectGFitSHealth() {
        try {
            if (PreferenceUtil.getGoogleFit(BPGFitSHealthActivity.this)) {
                //구글 피트니스
                mClient.connect();
            } else {
                //S 헬스
                if (PreferenceUtil.getSHealthBP(BPGFitSHealthActivity.this)) {
                    mStore.connectService();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == ActivityResultCode.GOOGLE_FIT_REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    }

    /**
     * 삼성 S Health 확인
     *
     * @return
     */
    private boolean isSHealth() {
        boolean isSHealth = false;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningServiceInfo> info;
        info = am.getRunningServices(Integer.MAX_VALUE);
        for (Iterator<RunningServiceInfo> i = info.iterator(); i.hasNext(); ) {
            RunningServiceInfo runningServiceInfo = i.next();

            if ("com.sec.android.service.health.sensor.HealthService".equals(runningServiceInfo.service.getClassName())) {
                isSHealth = true;
                break;
            }
        }
        return isSHealth;
    }

    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this).addApi(Fitness.SENSORS_API)
                .addApi(Fitness.CONFIG_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                //                .addApi(Fitness.API)
                //                                                   .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                //                                                   .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(new ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle bundle) {
                        // Now you can make calls to the Fitness APIs.  What to do?
                        // Play with some sessions!!

                        new InsertAndVerifySessionTask().execute();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        // If your connection to the sensor gets lost at some point,
                        // you'll be able to determine the reason and react to it here.
                        if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                            Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                        } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                            Log.i(TAG,
                                    "Connection lost.  Reason: Service Disconnected");
                        }

                        if (PreferenceUtil.getSHealthBP(BPGFitSHealthActivity.this)) {
                            mStore.connectService();
                        } else {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                    // Called whenever the API client fails to connect.
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {

                        if (PreferenceUtil.getSHealthBP(BPGFitSHealthActivity.this)) {
                            mStore.connectService();
                        } else {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                })
                .build();

    }

    /**
     * S 헬스
     */
    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            // The connection is successful.
            // Acquires the required permission
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

            try {

                // Check whether the required permission is acquired
                Set<PermissionKey> keySet = new HashSet<PermissionKey>();
                keySet.add(mPermissionKey);
                Map<PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(keySet);

                if (resultMap.get(mPermissionKey) == Boolean.TRUE) {
                    // The permission has been acquired already

                    insertSHealthData();

                } else {
                    // Requests permission to read the count of steps

                    pmsManager.requestPermissions(keySet).setResultListener(mPermissionListener);
                }
            } catch (Exception e) {
                // Error handling
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            // Resolve error if the connection fails
            error.resolve(BPGFitSHealthActivity.this);

            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onDisconnected() {
            // The connection is disconnected
        }
    };

    private final HealthResultHolder.ResultListener<PermissionResult> mPermissionListener =
            new HealthResultHolder.ResultListener<PermissionResult>() {

                @Override
                public void
                onResult(PermissionResult result) {

                    Map<PermissionKey, Boolean> resultMap =
                            result.getResultMap();

                    if (resultMap.get(mPermissionKey) == Boolean.FALSE) {
                        // The requested permission is not acquired

                        mStore.disconnectService();
                    } else {
                        // The requested permission is acquired.
                    }
                }
            };

    private class InsertAndVerifySessionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //First, create a new session and an insertion request.
            SessionInsertRequest insertRequest = insertFitnessSession();

            // [START insert_session]
            // Then, invoke the Sessions API to insert the session and await the result,
            // which is possible here because of the AsyncTask. Always include a timeout when
            // calling await() to avoid hanging that can occur from the service being shutdown
            // because of low memory or other conditions.
            Log.i(TAG, "Inserting the session in the History API");
            com.google.android.gms.common.api.Status insertStatus = Fitness.SessionsApi
                    .insertSession(mClient,
                            insertRequest)
                    .await(1, TimeUnit.MINUTES);

            // Before querying the session, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                Log.i(TAG, "There was a problem inserting the session: " + insertStatus.getStatusMessage());
                return null;
            }

            // At this point, the session has been inserted and can be read.
            Log.i(TAG, "Session insert was successful!");
            // [END insert_session]

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try {

                if (PreferenceUtil.getSHealthBP(BPGFitSHealthActivity.this)) {
                    mStore.connectService();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();

                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private SessionInsertRequest insertFitnessSession() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -1);
        long startTime = cal.getTimeInMillis();

        // 맥박
        DataSource pulseDataSource = new DataSource.Builder().setAppPackageName(this.getPackageName())
                .setDataType(DataType.TYPE_HEART_RATE_BPM)
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set of the run speeds to include in the session.
        DataSet pulseDataSet = DataSet.create(pulseDataSource);

        DataPoint pulseDataPoint = pulseDataSet.createDataPoint().setTimestamp(endTime, TimeUnit.MILLISECONDS);

        pulseDataPoint.getValue(Field.FIELD_BPM).setFloat(Float.valueOf(strPulse));
        pulseDataSet.add(pulseDataPoint);

        // [START build_insert_session_request_with_activity_segments]
        // Create a second DataSet of ActivitySegments to indicate the runner took a 10-minute walk
        // in the middle of the run.
        DataSource activitySegmentDataSource = new DataSource.Builder().setAppPackageName(this.getPackageName())
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet activitySegments = DataSet.create(activitySegmentDataSource);

        DataPoint measureDataPoint = activitySegments.createDataPoint().setTimeInterval(startTime,
                endTime,
                TimeUnit.MILLISECONDS);
        measureDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.OTHER);
        activitySegments.add(measureDataPoint);

        // [START build_insert_session_request]
        // Create a session with metadata about the activity.
        Session session = new Session.Builder().setName(SESSION_NAME_BLOOD_PRESSURE)
                .setDescription(SESSION_NAME_BLOOD_PRESSURE)
                .setIdentifier(getApplicationInfo().packageName)
                .setActivity(FitnessActivities.OTHER)
                .setStartTime(startTime, TimeUnit.MILLISECONDS)
                .setEndTime(endTime, TimeUnit.MILLISECONDS)
                .build();

        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder().setSession(session)
                .addDataSet(pulseDataSet)
                .addDataSet(activitySegments)
                .build();
        // [END build_insert_session_request]
        // [END build_insert_session_request_with_activity_segments]

        return insertRequest;
    }

    private void insertSHealthData() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, -1);
        long startTime = cal.getTimeInMillis();

        // 타임존
        TimeZone tz = TimeZone.getDefault();
        int offsetFromUtc = tz.getOffset(now.getTime());

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        HealthData data = new HealthData();

        data.putLong(SessionMeasurement.START_TIME, startTime);
        data.putLong(SessionMeasurement.TIME_OFFSET, offsetFromUtc);
        data.putFloat(HealthConstants.BloodPressure.DIASTOLIC,
                Float.parseFloat(requestMap.get(DataBase.COLUMN_NAME_BP_DIA)));
        data.putFloat(HealthConstants.BloodPressure.MEAN,
                Float.parseFloat(requestMap.get(DataBase.COLUMN_NAME_BP_MEAN)));
        data.putInt(HealthConstants.BloodPressure.PULSE,
                Integer.parseInt(requestMap.get(DataBase.COLUMN_NAME_BP_PULSE)));
        data.putFloat(HealthConstants.BloodPressure.SYSTOLIC,
                Float.parseFloat(requestMap.get(DataBase.COLUMN_NAME_BP_SYS)));
        data.putString(HealthConstants.BloodPressure.COMMENT, requestMap.get(DataBase.COLUMN_NAME_MESSAGE));

        HealthDataResolver.InsertRequest request =
                new HealthDataResolver.InsertRequest.Builder().setDataType(HealthConstants.BloodPressure.HEALTH_DATA_TYPE)
                        .build();

        try {
            // register the local device with the data if it is not registered
            data.setSourceDevice(new HealthDeviceManager(mStore).getLocalDevice().getUuid());
            request.addHealthData(data);
            resolver.insert(request).setResultListener(mResultListener);
        } catch (Exception e) {
            // Error handling
            e.printStackTrace();

            setResult(RESULT_OK);
            finish();
        }
    }

    private final HealthResultHolder.ResultListener<BaseResult> mResultListener =
            new HealthResultHolder.ResultListener<BaseResult>() {

                @Override
                public void
                onResult(BaseResult result) {
                    //Check the result
                    setResult(RESULT_OK);
                    finish();
                }
            };
}
