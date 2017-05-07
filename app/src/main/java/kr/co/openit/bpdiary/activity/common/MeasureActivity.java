package kr.co.openit.bpdiary.activity.common;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.utils.ParcelableMap;
import kr.co.openit.bpdiary.common.measure.bean.MeasureService;
import kr.co.openit.bpdiary.common.measure.dao.MeasureDAO;

/**
 * 블루투스 연동 Activity
 */
public class MeasureActivity extends BaseActivity {

    /**
     * service 의 bound 여부
     */
    private boolean isBound = false;

    /**
     * MeasureService 와 메세지를 송수신하는 messenger
     */
    private Messenger healthService;

    /**
     * Medical Device System DAO
     */
    protected MeasureDAO measureDAO;

    private CustomProgressDialog customProgressDialog = null;

    /**
     * Sets up communication with {@link MeasureService}.
     */
    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.i(TAG, "onServiceConnected()");

            Message msg = Message.obtain(null, MeasureService.MSG_REG_CLIENT);
            msg.replyTo = messenger;
            msg.obj = TAG;
            healthService = new Messenger(service);

            try {
                healthService.send(msg);
            } catch (RemoteException e) {
                //Log.w(TAG, "Unable to register client to service.");
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            healthService = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        // Starts health service.
        Intent intent = new Intent(this, MeasureService.class);
        isBound = bindService(intent, connection, BIND_AUTO_CREATE);

        //Log.i(TAG, "bind MeasureService");
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isBound) {
            unbindService(connection);
            isBound = false;
            //Log.i(TAG, "unbind MeasureService");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        measureDAO = new MeasureDAO(this);

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    /**
     * MeasureService 와 메세지를 송수신하는 messenger
     */
    private final Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            //Log.d(TAG, "msg.what : " + msg.what + ", msg arg1 : " + msg.arg1);

            switch (msg.what) {

            //Application registration complete.
                case MeasureService.STATUS_HEALTH_APP_REG:
                    switch (msg.arg1) {
                        case MeasureService.RESULT_OK:
                            //Log.i(TAG, "reg app OK.");
                            break;
                        case MeasureService.RESULT_FAIL:
                            //Log.i(TAG, "reg app FAIL.");
                            break;
                    }

                    break;

                // Application unregistration complete.
                case MeasureService.STATUS_HEALTH_APP_UNREG:
                    switch (msg.arg1) {
                        case MeasureService.RESULT_OK:
                            //Log.i(TAG, "unreg app OK.");
                            break;
                        case MeasureService.RESULT_FAIL:
                            //Log.i(TAG, "unreg app FAIL.");
                            break;
                    }

                    break;

                case MeasureService.STATUS_CREATE_CHANNEL:
                    switch (msg.arg1) {
                        case MeasureService.RESULT_OK:
                            //Log.i(TAG, "create channel OK.");
                            break;
                        case MeasureService.RESULT_FAIL:
                            //Log.i(TAG, "create channel FAIL.");
                            break;
                    }

                    break;

                case MeasureService.STATUS_DESTROY_CHANNEL:
                    switch (msg.arg1) {
                        case MeasureService.RESULT_OK:
                            //Log.i(TAG, "destroy channel OK.");
                            break;
                        case MeasureService.RESULT_FAIL:
                            //Log.i(TAG, "destroy channel FAIL.");
                            break;
                    }

                    break;

                // Reading data from HDP device.
                case MeasureService.STATUS_READ_DATA:
                    //Log.i(TAG, "reading data...");

                    startMeasuringAnimation();

                    break;

                case MeasureService.STATUS_GET_MDS_DATA:
                    //Log.i(TAG, "get mds info...");

                    String macAddress = msg.getData().getString("macAddress");

                    Map<String, String> pMap = new HashMap<String, String>();
                    pMap.put("macAddress", macAddress);
                    Map<String, String> rMap = searchMDSInfo(pMap);

                    sendMDSInfo(rMap);

                    break;

                // Save the medical device system.
                case MeasureService.STATUS_SAVE_MDS_DATA:
                    //Log.i(TAG, "save mds info...");

                    ParcelableMap prMap = msg.getData().getParcelable("mdsInfo");
                    Log.d(TAG, "[MDS]\n" + prMap.getStringMap());

                    insertMDSInfo(prMap.getStringMap());

                    break;

                // Finish reading data from HDP device.
                case MeasureService.STATUS_READ_DATA_DONE:
                    //Log.i(TAG, "one with reading data...");

                    stopMeasuringAnimation();

                    boolean isSuccess = msg.getData().getBoolean("isSuccess");
                    String rtnMsg = msg.getData().getString("rtnMsg");

                    int healthProfile = msg.getData().getInt("healthProfile");
                    String model = msg.getData().getString("deviceModel");
                    String company = msg.getData().getString("deviceCompany");

                    Bundle measureResult = msg.getData().getBundle("measureResult");

//                    Log.i(TAG, "isSuccess : " + isSuccess
//                               + ", rtnMsg : "
//                               + rtnMsg
//                               + ", healthProfile : "
//                               + healthProfile
//                               + ", model : "
//                               + model
//                               + ", company : "
//                               + company
//                               + ", measureResult : "
//                               + measureResult);

                    measureResult(msg.getData());

                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    };

    /**
     * 
     */
    private final Messenger messenger = new Messenger(handler);

    /**
     * 측정 데이터 수신하기 시작할 때 호출
     */
    protected void startMeasuringAnimation() {
        //Log.i(TAG, "startMeasuringAnimation");
    }

    /**
     * 측정 데이터 수신 종료 때 호출
     */
    protected void stopMeasuringAnimation() {
        //Log.i(TAG, "stopAnimation");
    }

    /**
     * 측정 데이터 송신하기 시작할 때 호출
     */
    protected void startSendingAnimation() {
        //Log.i(TAG, "startSendingAnimation");
    }

    /**
     * DB 에서 조회한 MDS 정보 전달
     * 
     * @param pMap
     */
    private void sendMDSInfo(Map<String, String> pMap) {
        //Log.d(TAG, "MDS Info : " + pMap);

        ParcelableMap prMap = new ParcelableMap();
        prMap.setStringMap(pMap);

        Bundle bundle = new Bundle();
        bundle.putParcelable("mdsInfo", prMap);

        sendMessage(MeasureService.MSG_GET_MDS_INFO, 0, bundle);
    }

    /**
     * activity 로 메세지 전달
     * 
     * @param what
     * @param value
     * @param bundle
     */
    private void sendMessage(int what, int value, Bundle bundle) {

        if (healthService == null) {
            //Log.w(TAG, "No healthService registered.");
            return;
        }

        try {
            Message msg = Message.obtain(null, what, value, 0);
            if (bundle != null) {
                msg.setData(bundle);
            }

            healthService.send(msg);

        } catch (RemoteException re) {
            // Unable to reach client.
            re.printStackTrace();
        }
    }

    /**
     * 블루투스 측정 데이터를 수신하여 분석
     * 
     * @param measureBundle
     */
    protected void measureResult(Bundle measureBundle) {

        if (measureBundle.getBoolean("isSuccess")) {
            //nothing
        } else {
            //Log.e(TAG, "measure error : " + measureBundle.getString("rtnMsg"));
        }
    }

    /**
     * MDS 정보 조회
     * 
     * @param pMap
     * @return
     */
    protected Map<String, String> searchMDSInfo(Map<String, String> pMap) {
        return measureDAO.selectMedicalDeviceSystem(pMap);
    }

    /**
     * MDS 정보 저장
     * 
     * @param pMap
     */
    protected void insertMDSInfo(Map<String, String> pMap) {
        long row = measureDAO.insertMedicalDeviceSystem(pMap);
        //Log.d(TAG, "row : " + row);
    }

    @Override
    public void onNewIntent(Intent intent) {
        //Log.d(TAG, "onNewIntent()");
        setIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                setResult(RESULT_OK);
                break;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 핸들러
    private static final int MSG_MEASURE_LAYOUT = 0;

    private static final int MSG_MEASURE_SAVE_DATA = 1;

    // 측정중 레이아웃 변화
    protected void requestMeasureLayout() {
        measureHandler.sendMessage(measureHandler.obtainMessage(MSG_MEASURE_LAYOUT));
    }

    // 측정 데이터 전송
    protected void requestMeasureData() {
        measureHandler.sendMessage(measureHandler.obtainMessage(MSG_MEASURE_SAVE_DATA));
    }

    /**
     * NFC 측정 화면 변환 핸들러
     */
    private final Handler measureHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_MEASURE_LAYOUT:
                    // 측정 화면 표시
                    setMeasureLayout();
                    break;

                case MSG_MEASURE_SAVE_DATA:
                    // 측정 데이터 DB 저장

                    measureNfcResult();
                    break;

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    /**
     * 측정 화면 변환
     */
    protected void setMeasureLayout() {
        //nothing
    }

    /**
     * NFC 측정 데이터 저장 메서드
     */
    protected void measureNfcResult() {
        //nothing
    }

    protected void showLodingProgress() {
        if (customProgressDialog == null) {
            customProgressDialog = new CustomProgressDialog(MeasureActivity.this);
            customProgressDialog.setCancelable(false);
            customProgressDialog.setCanceledOnTouchOutside(false);
        }
        customProgressDialog.show();
    }

    protected void hideLodingProgress() {
        if (customProgressDialog != null && customProgressDialog.isShowing()) {
            customProgressDialog.hide();
        }
    }
}
