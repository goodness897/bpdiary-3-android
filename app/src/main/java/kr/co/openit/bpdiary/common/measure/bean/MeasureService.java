package kr.co.openit.bpdiary.common.measure.bean;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.bluetooth.BluetoothHealthCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.AppConfig;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.common.helper.ConfigurationInitializer;
import kr.co.openit.bpdiary.utils.ByteUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.ParcelableMap;
import kr.co.openit.bpdiary.common.vo.ResultData;
import kr.co.openit.bpdiary.common.vo.apdu.AareApdu;
import kr.co.openit.bpdiary.common.vo.apdu.AarqApdu;
import kr.co.openit.bpdiary.common.vo.apdu.AbrtApdu;
import kr.co.openit.bpdiary.common.vo.apdu.ConfigReport;
import kr.co.openit.bpdiary.common.vo.apdu.DataPrstApdu;
import kr.co.openit.bpdiary.common.vo.apdu.GetMDSPrstApdu;
import kr.co.openit.bpdiary.common.vo.apdu.MeasureData;
import kr.co.openit.bpdiary.common.vo.apdu.RlreApdu;
import kr.co.openit.bpdiary.common.vo.apdu.SegmSelection;
import kr.co.openit.bpdiary.common.vo.apdu.SegmentDataEvent;
import kr.co.openit.bpdiary.common.vo.apdu.SegmentDataResult;
import kr.co.openit.bpdiary.common.vo.apdu.SegmentInfoList;
import kr.co.openit.bpdiary.common.vo.apdu.SetTimePrstApdu;
import kr.co.openit.bpdiary.common.vo.apdu.TrigSegmDataXferReq;
import kr.co.openit.bpdiary.common.vo.apdu.TrigSegmDataXferRsp;
import kr.co.openit.bpdiary.common.vo.attr.Attribute;
import kr.co.openit.bpdiary.common.vo.attr.ConfigId;
import kr.co.openit.bpdiary.common.vo.attr.OCTECSTRING;
import kr.co.openit.bpdiary.common.vo.attr.ProdSpecEntry;
import kr.co.openit.bpdiary.common.vo.attr.ProductionSpec;
import kr.co.openit.bpdiary.common.vo.attr.RegCertDataList;
import kr.co.openit.bpdiary.common.vo.attr.SystemModel;
import kr.co.openit.bpdiary.common.vo.attr.TYPE;
import kr.co.openit.bpdiary.common.vo.attr.TypeVerList;

/**
 * 블루투스 연동을 통해서 Health Device 의 측정데이터를 수신하는 Service
 */
public class MeasureService extends Service {

    /**
     * debugging tag
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Handles events sent by DeviceActivity
     */
    private Messenger client;

    /**
     * bluetoothHealth
     */
    private BluetoothHealth bluetoothHealth;

    /**
     * bluetoothAdapter
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * 블루투스 통신을 하고 있는 device
     */
    private BluetoothDevice device;

    /**
     * 현재 연결되어 있는 health configuration
     */
    private BluetoothHealthAppConfiguration healthAppConfig;

    /**
     * 연결이 가능한 health configuration 리스트
     */
    private final List<BluetoothHealthAppConfiguration> healthAppConfigs = new ArrayList<BluetoothHealthAppConfiguration>();

    /**
     * 현재 연결된 health profile
     */
    private int healthProfile;

    /**
     * 측정 상태
     */
    private int managerState = HealthcareConstants.State.DISCONNECTED;

    /**
     * Agent로 전송하려는 메세지 등록
     */
    private List<String> sendMsgList = new ArrayList<String>();

    /**
     * 블루투스 통신 후 측정 데이터를 주고받는 thread
     */
    private MeasureThread mthread;

    /**
     * success
     */
    public static final int RESULT_OK = 0;

    /**
     * fail
     */
    public static final int RESULT_FAIL = -1;

    // Status codes sent back to the UI client.
    /**
     * Application registration complete.
     */
    public static final int STATUS_HEALTH_APP_REG = 100;

    /**
     * Application unregistration complete.
     */
    public static final int STATUS_HEALTH_APP_UNREG = 101;

    /**
     * Channel creation complete.
     */
    public static final int STATUS_CREATE_CHANNEL = 102;

    /**
     * Channel destroy complete.
     */
    public static final int STATUS_DESTROY_CHANNEL = 103;

    /**
     * Reading data from Bluetooth HDP device.
     */
    public static final int STATUS_READ_DATA = 104;

    /**
     * Save the medical device system data.
     */
    public static final int STATUS_SAVE_MDS_DATA = 105;

    /**
     * Get the medical device system data.
     */
    public static final int STATUS_GET_MDS_DATA = 106;

    /**
     * Done with reading data.
     */
    public static final int STATUS_READ_DATA_DONE = 107;

    // Message codes received from the UI client.
    /**
     * Register client with this service.
     */
    public static final int MSG_REG_CLIENT = 200;

    /**
     * Unregister client from this service.
     */
    public static final int MSG_UNREG_CLIENT = 201;

    /**
     * Register health application.
     */
    public static final int MSG_REG_HEALTH_APP = 300;

    /**
     * Unregister health application.
     */
    public static final int MSG_UNREG_HEALTH_APP = 301;

    /**
     * Connect channel.
     */
    public static final int MSG_CONNECT_CHANNEL = 400;

    /**
     * Disconnect channel.
     */
    public static final int MSG_DISCONNECT_CHANNEL = 401;

    /**
     * mds info 저장 여부
     */
    public static final int MSG_GET_MDS_INFO = 500;

    /**
     * 
     */
    private final Messenger messenger = new Messenger(new IncomingHandler());

    /**
     * Handles events sent by DeviceActivity
     */
    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

            // Register UI client to this service so the client can receive messages.
                case MSG_REG_CLIENT:
                    client = msg.replyTo;
                    String clientName = (String)msg.obj;
                    break;

                // Unregister UI client from this service.
                case MSG_UNREG_CLIENT:
                    client = null;
                    break;

                // Register health application.
                case MSG_REG_HEALTH_APP:
                    break;

                // Unregister health application.
                case MSG_UNREG_HEALTH_APP:
                    break;

                // Connect channel.
                case MSG_CONNECT_CHANNEL:
                    break;

                // Disconnect channel.
                case MSG_DISCONNECT_CHANNEL:
                    break;

                case MSG_GET_MDS_INFO:

                    ParcelableMap prMap = msg.getData().getParcelable("mdsInfo");
                    Map<String, String> rMap = prMap.getStringMap();

                    if (rMap != null) {
                        if (mthread != null) {

                            mthread.setMDSInfo(rMap);
                        }
                    } else {
                        if (mthread != null) {
                            // DB 에 해당 MDS 정보가 없으니, 요청
                            sendMsgList.add(HealthcareConstants.SendMessage.GET_MDS);
                            mthread.setSendToAgent(true);
                        }
                    }

                    break;

                default:
                    super.handleMessage(msg);

            }
        }
    }

    /**
     * Sends an update message to registered UI client.
     * 
     * @param what
     * @param value
     */
    private void sendMessage(int what, int value) {
        if (client == null) {
            return;
        }

        try {
            client.send(Message.obtain(null, what, value, 0));
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    /**
     * activity 로 메세지 전달
     * 
     * @param what
     * @param value
     * @param bundle
     */
    private void sendMessage(int what, int value, Bundle bundle) {
        if (client == null) {
            return;
        }

        try {
            Message msg = Message.obtain(null, what, value, 0);
            if (bundle != null) {
                msg.setData(bundle);
            }

            client.send(msg);

        } catch (RemoteException re) {
            // Unable to reach client.
            re.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Make sure Bluetooth and health profile are available on the Android device.
        // Stop service if they are not available.

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Bluetooth adapter isn't available.  
            // The client of the service is supposed to verify that it is available and activate before invoking this service.
            stopSelf();
            return;
        }

        if (!bluetoothAdapter.getProfileProxy(this, bluetoothServiceListener, BluetoothProfile.HEALTH)) {
            stopSelf();
            return;
        } else {
            ConfigurationInitializer.saveStandardConfigApdu();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mthread != null) {

            try {
                // 측정 thread 정지
                mthread.threadStop();
            } catch (Exception e) {
                //Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Unregister health application through the Bluetooth Health API.
     */
    private void unregisterApp() {
        bluetoothHealth.unregisterAppConfiguration(healthAppConfig);
    }

    /**
     * Register health application through the Bluetooth Health API.
     * 
     * @param dataType
     */
    private void registerApp(int dataType) {

        if (healthProfile != 0 && healthProfile != dataType) {
            unregisterApp();
        }

        if (dataType == Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BP) {
            bluetoothHealth.registerSinkAppConfiguration("blood pressure", dataType, healthCallback);
        } else if (dataType == Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_GLUCOSE) {
            bluetoothHealth.registerSinkAppConfiguration("glucose meter", dataType, healthCallback);
        }
    }

    /**
     * Register health application through the Bluetooth Health API.
     */
    private void registerApp() {

        // 혈압계
        bluetoothHealth.registerSinkAppConfiguration("blood pressure",
                                                     Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BP,
                                                     healthCallback);

        // 혈당계
        bluetoothHealth.registerSinkAppConfiguration("glucose meter",
                                                     Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_GLUCOSE,
                                                     healthCallback);
    }

    /**
     * Callbacks to handle connection set up and disconnection clean up.
     */
    private final BluetoothProfile.ServiceListener bluetoothServiceListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HEALTH) {
                bluetoothHealth = (BluetoothHealth)proxy;

                registerApp();
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEALTH) {
                bluetoothHealth = null;
            }
        }
    };

    /**
     * 
     */
    private final BluetoothHealthCallback healthCallback = new BluetoothHealthCallback() {

        @Override
        public void onHealthAppConfigurationStatusChange(BluetoothHealthAppConfiguration config, int status) {

            // Callback to handle application registration and unregistration events. 
            // The service passes the status back to the UI client.

            // 여러개 register
            if (status == BluetoothHealth.APP_CONFIG_REGISTRATION_FAILURE) {
                boolean isConfig = false;

                for (int i = 0, len = healthAppConfigs.size(); i < len; i++) {

                    BluetoothHealthAppConfiguration conf = healthAppConfigs.get(i);
                    if (config.toString().equals(conf.toString())) {
                        isConfig = true;

                        i = len;
                    }
                }

                if (isConfig) {
                    healthAppConfigs.remove(config);
                }

            } else if (status == BluetoothHealth.APP_CONFIG_REGISTRATION_SUCCESS) {
                boolean isConfig = false;

                for (int i = 0, len = healthAppConfigs.size(); i < len; i++) {

                    BluetoothHealthAppConfiguration conf = healthAppConfigs.get(i);

                    if (config.toString().equals(conf.toString())) {
                        isConfig = true;

                        i = len;
                    }
                }

                if (!isConfig) {
                    healthAppConfigs.add(config);
                }

            } else if (status == BluetoothHealth.APP_CONFIG_UNREGISTRATION_FAILURE || status == BluetoothHealth.APP_CONFIG_UNREGISTRATION_SUCCESS) {

                Log.i(TAG, status == BluetoothHealth.APP_CONFIG_UNREGISTRATION_SUCCESS ? "unregistration ok"
                                                                                      : "unregistration fail");
            }
        }

        @Override
        public void onHealthChannelStateChange(BluetoothHealthAppConfiguration config,
                                               BluetoothDevice btDevice,
                                               int prevState,
                                               int newState,
                                               ParcelFileDescriptor fd,
                                               int channel) {

            // Callback to handle channel connection state changes.
            // Note that the logic of the state machine may need to be modified based on the HDP device.
            // When the HDP device is connected, the received file descriptor is passed to the MeasureThread to read the content.

            if ((prevState == BluetoothHealth.STATE_CHANNEL_DISCONNECTED || prevState == BluetoothHealth.STATE_CHANNEL_CONNECTING) && newState == BluetoothHealth.STATE_CHANNEL_CONNECTED) {

                // 여러개 register
                boolean isConfig = false;

                for (int i = 0, len = healthAppConfigs.size(); i < len; i++) {

                    BluetoothHealthAppConfiguration conf = healthAppConfigs.get(i);

                    if (config.toString().equals(conf.toString())) {

                        if (client != null) {

                            sendMessage(STATUS_CREATE_CHANNEL, RESULT_OK);

                            // health profile 설정
                            healthProfile = config.getDataType();

                            device = btDevice;

                            // mac address 로 mds info 저장여부 조회
                            sendMessageGetMDSInfo(device.getAddress());

                            // 측정 thread 시작
                            mthread = new MeasureThread(fd, device.getAddress());
                            mthread.start();

                            // 측정 상태값 설정
                            managerState = HealthcareConstants.State.CONNECTED;

                            isConfig = true;

                            i = len;

                        } else {

                            if (mthread != null) {

                                try {
                                    // 측정 thread 정지
                                    mthread.threadStop();

                                    fd.close();
                                    fd = null;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                if (!isConfig) {
                    sendMessage(STATUS_CREATE_CHANNEL, RESULT_FAIL);
                }

            } else if (prevState == BluetoothHealth.STATE_CHANNEL_CONNECTING && newState == BluetoothHealth.STATE_CHANNEL_DISCONNECTED) {
                sendMessage(STATUS_CREATE_CHANNEL, RESULT_FAIL);
            } else if (newState == BluetoothHealth.STATE_CHANNEL_DISCONNECTED) {

                // 여러개 register
                boolean isConfig = false;

                for (int i = 0, len = healthAppConfigs.size(); i < len; i++) {

                    BluetoothHealthAppConfiguration conf = healthAppConfigs.get(i);
                    if (config.toString().equals(conf.toString())) {
                        sendMessage(STATUS_DESTROY_CHANNEL, RESULT_OK);

                        isConfig = true;

                        i = len;

                        if (mthread != null) {
                            mthread.threadStop();
                            managerState = HealthcareConstants.State.DISCONNECTED;
                        }
                    }
                }

                if (!isConfig) {
                    sendMessage(STATUS_DESTROY_CHANNEL, RESULT_FAIL);
                }
            }
        }
    };

    /**
     * MDS Info 조회 요청 메시지
     * 
     * @param macAddress
     */
    private void sendMessageGetMDSInfo(String macAddress) {
        Bundle bundle = new Bundle();
        bundle.putString("macAddress", macAddress);

        sendMessage(STATUS_GET_MDS_DATA, 0, bundle);
    }

    /**
     * <pre>
     * Thread to read incoming data received from the HDP device. 
     * This sample application merely reads the raw byte from the incoming file descriptor. 
     * The data should be interpreted using a health manager which implements the IEEE 11073-xxxxx specifications.
     * </pre>
     */
    private class MeasureThread extends Thread {

        /**
         * thread 내 loop 실행 여부
         */
        private boolean threadRunFlag = true;

        /**
         * thread sleep time
         */
        private final long sleepTime = 300;

        /**
         * 최종 메세지 받은 시간
         */
        private long lastReceiveTime = 0;

        /**
         * 최종 메세지 보낸 시간
         */
        private long lastSendTime = 0;

        /**
         * 체크해야 하는 interval time (ms 단위)
         */
        private long checkIntervalTime = 0;

        /**
         * timeout error code
         */
        private short timeoutErrorCode = 0;

        /**
         * 
         */
        private String rtnMsg;

        /**
         * UI로 넘겨줄 데이터를 담을 bundle
         */
        private final Bundle measureResult = new Bundle();

        /**
         * 
         */
        private ParcelFileDescriptor mFd;

        /**
         * output stream
         */
        private OutputStream out;

        /**
         * input stream
         */
        private InputStream in;

        /**
         * 연결된 device name
         */
        private String deviceName;

        /**
         * dev-config-id
         */
        private int devConfigId = 0;

        /**
         * configuration 을 저장할 파일명
         */
        private String fileName;

        /**
         * timeout 체크 여부
         */
        private boolean checkTimeout = true;

        /**
         * configuration 정보를 가진 객체
         */
        private ConfigReport config;

        /**
         * 측정 데이터를 가진 객체
         */
        private DataPrstApdu data;

        /**
         * PM-Store segment 정보를 가진 객체
         */
        private SegmentInfoList getSegInfoRes;

        /**
         * PM-Store 측정 데이터를 가진 객체
         */
        private SegmentDataEvent segmData;

        /**
         * mds 정보를 가진 객체
         */
        private GetMDSPrstApdu getMDS;

        /**
         * 
         */
        private Map<String, String> mdsInfo;

        /**
         * 이전 측정 데이터의 측정일시
         */
        private String lastMeasureDt;

        /**
         * 측정 데이터를 적재할 List
         */
        private final ArrayList<ParcelableMap> rList = new ArrayList<ParcelableMap>();

        /**
         * UI 로 전송할 측정 데이터 존재 여부
         */
        private boolean sendMeasureData = false;

        /**
         * device 로 프로토콜을 전송해도 되는지 여부
         */
        private boolean isSendToAgent = false;

        /**
         * 유효한 configuration 인지
         */
        private boolean isValidConfig = false;

        /**
         * configuration 정보 저장여부
         */
        private boolean isSaveConfig = false;

        /**
         * PM-Store 여부
         */
        private boolean isPMStore = false;

        /**
         * PM-Store 로 정의된 obj-handle
         */
        private int pmStoreObjHandle = 0;

        /**
         * segment instance number
         */
        private int segInstNo = 0;

        /**
         * 생성자
         * 
         * @param fd
         */
        public MeasureThread(ParcelFileDescriptor fd, String macAddress) {
            super();
            mFd = fd;

            sendMsgList = new ArrayList<String>();
        }

        @Override
        public void run() {

            out = new FileOutputStream(mFd.getFileDescriptor());
            in = new BufferedInputStream(new FileInputStream(mFd.getFileDescriptor()));

            managerState = HealthcareConstants.State.UNASSOCIATED;

            try {

                sendMessage(STATUS_READ_DATA, 0);

                while (threadRunFlag) {

                    // connected 상태인지 확인
                    if (managerState == HealthcareConstants.State.UNASSOCIATED) {

                        // agent 와 접속 상태 확인
                        receiveAssociationRequest();
                    }

                    if (managerState >= HealthcareConstants.State.ASSOCIATED) {

                        // 송신 데이터 확인
                        receive();

                        // 수신 데이터 확인
                        send();
                    }

                    // timeout 확인
                    checkTimeout();

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }

                }

            } catch (ClassNotFoundException ce) {
                ce.printStackTrace();
                rtnMsg = ce.getMessage();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                rtnMsg = ioe.getMessage();
            } finally {

                if (out != null) {
                    try {
                        out.close();
                        out = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (in != null) {
                    try {
                        in.close();
                        in = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (mFd != null) {
                    try {
                        mFd.close();
                        mFd = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (!sendMeasureData) {
                    sendMeasureResult();
                }
            }
        }

        /**
         * 측정 thread 정지
         */
        public void threadStop() {
            threadRunFlag = false;
        }

        /**
         * Medical Device System 정보 setter
         * 
         * @param mdsInfo
         */
        public void setMDSInfo(Map<String, String> mdsInfo) {
            this.mdsInfo = mdsInfo;
        }

        /**
         * isSendToAgent 설정
         * 
         * @param isSendToAgent
         */
        public void setSendToAgent(boolean isSendToAgent) {
            this.isSendToAgent = isSendToAgent;
        }

        /**
         * 결과 데이터 추출 및 bundle 에 설정
         * 
         * @return
         */
        private void makeResultData() {

            // 결과 전송
            if (data != null && !data.getDataList().isEmpty()) {

                // 한번에 parsing 한 데이터 묶음이 rMap 에 저장
                ParcelableMap rMap = new ParcelableMap();
                for (int i = 0, len = data.getDataList().size(); i < len; i++) {

                    MeasureData mData = data.getDataList().get(i);

                    // WAN Receiver 로 전송해야 하는 데이터는 정해져 있음
                    if (ManagerUtil.convertDataName(mData.getAttrId()) != null) {

                        ResultData rd = null;

                        // 단위 존재여부에 따라 구분
                        if (mData.getUnitCd() > 0) {
                            rd = new ResultData(mData.getObjHandle(),
                                                mData.getAttrId(),
                                                mData.getValue(),
                                                mData.getUnitCd(),
                                                mData.getDateTime());
                        } else {
                            rd = new ResultData(mData.getObjHandle(),
                                                mData.getAttrId(),
                                                mData.getValue(),
                                                mData.getDateTime());
                        }

                        // subClassList 설정 여부 확인
                        if (!mData.isEmptySubClassList()) {
                            rd.setSubClassList(mData.getSubClassList());
                        }

                        // 바로 전 측정 데이터의 측정일시와 비교
                        if (lastMeasureDt != null && (lastMeasureDt.equals(mData.getDateTime()))) { // 측정일이 같으면 같은 측정 데이터로 간주

                            if (!rList.isEmpty()) {

                                ParcelableMap pMap = rList.get(rList.size() - 1);
                                pMap.put(ManagerUtil.convertDataName(mData.getAttrId()), rd);

                                rList.set(rList.size() - 1, pMap);

                            } else {
                                rMap.put(ManagerUtil.convertDataName(mData.getAttrId()), rd);

                            }

                        } else {
                            rMap.put(ManagerUtil.convertDataName(mData.getAttrId()), rd);

                        }

                        if ((i + 1) == len) {
                            if (mData.getDateTime() != null && !mData.getDateTime().isEmpty()) {
                                lastMeasureDt = mData.getDateTime();
                            }
                        }
                    }
                }

                if (!rMap.getResultDataMap().isEmpty()) {
                    rList.add(rMap);
                }
            }
        }

        /**
         * mds object 데이터 전달을 위해 bundle 에 설정
         * 
         * @param getMDS mds 정보를 가진 객체
         * @return
         */
        private Bundle makeMDSData(GetMDSPrstApdu getMDS) {

            Map<String, String> rMap = new HashMap<String, String>();

            TypeVerList sysType = (TypeVerList)getMDS.getAttribute(Nomenclature.Attribute.MDC_ATTR_SYS_TYPE_SPEC_LIST);
            rMap.put(ManagerConstants.DataBase.COLUMN_NAME_HEALTH_PROFILE,
                     Integer.toString(sysType.getTypeVer().getType()));

            ConfigId devConfigId = (ConfigId)getMDS.getAttribute(Nomenclature.Attribute.MDC_ATTR_DEV_CONFIG_ID);
            rMap.put(ManagerConstants.DataBase.COLUMN_NAME_CONFIG_REPORT_ID, Integer.toString(devConfigId.getValue()));

            SystemModel model = (SystemModel)getMDS.getAttribute(Nomenclature.Attribute.MDC_ATTR_ID_MODEL);
            rMap.put(ManagerConstants.DataBase.COLUMN_NAME_COMPANY, model.getManufacturer());
            rMap.put(ManagerConstants.DataBase.COLUMN_NAME_MODEL, model.getModelNumber());

            rMap.put(ManagerConstants.DataBase.COLUMN_NAME_MAC_ADDRESS, device.getAddress());

            OCTECSTRING sysId = (OCTECSTRING)getMDS.getAttribute(Nomenclature.Attribute.MDC_ATTR_SYS_ID);
            rMap.put(ManagerConstants.DataBase.COLUMN_NAME_SYSTEM_ID, sysId.getValue());

            // 측정 의료기기의 sn 설정
            ProductionSpec productionSpec = (ProductionSpec)getMDS.getAttribute(Nomenclature.Attribute.MDC_ATTR_ID_PROD_SPECN);
            for (int i = 0, cnt = productionSpec.getCount(); i < cnt; i++) {
                ProdSpecEntry prodSpecEntry = productionSpec.entryList.get(i);

                if (prodSpecEntry.getSpecType() == ProdSpecEntry.SpecType.SERIAL_NUMBER) {
                    rMap.put(ManagerConstants.DataBase.COLUMN_NAME_SYSTEM_ID, prodSpecEntry.getProdSpec());
                    i = cnt;
                }
            }

            ParcelableMap map = new ParcelableMap();
            map.setStringMap(rMap);

            Bundle bundle = new Bundle();
            bundle.putParcelable("mdsInfo", map);

            mdsInfo = rMap;

            return bundle;
        }

        /**
         * association request 수신
         * 
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void receiveAssociationRequest() throws IOException, ClassNotFoundException {

            byte[] cmd = new byte[2];

            if ((in.available() > 0) && (in.read(cmd) > -1)) {

                byte[] totalLen = new byte[2];

                int length = 0;

                byte[] body = null;

                // association request receive
                if (ByteUtil.short2ushort(ByteUtil.getshort(cmd)) == HealthcareConstants.ApduChoiceType.ASSOCIATION_REQUEST) {

                    in.read(totalLen);
                    length = ByteUtil.short2ushort(ByteUtil.getshort(totalLen));

                    body = new byte[length];
                    in.read(body);

                    // 받은시간
                    lastReceiveTime = System.currentTimeMillis();
                    checkTimeout = true;

                    Log.i(TAG, ">> Association Request : \n" + ByteUtil.byte2hexa(cmd)
                               + ByteUtil.byte2hexa(totalLen)
                               + ByteUtil.byte2hexa(body));

                    // association request 를 받은 후에 상태 변경
                    managerState = HealthcareConstants.State.ASSOCIATING;

                    AarqApdu aarqApdu = new AarqApdu(body);
                    devConfigId = aarqApdu.getDevConfigId();
                    deviceName = aarqApdu.getSystemId();

                    Log.i(TAG, "\n========== [[ Association Request ]] ==========\n" + aarqApdu.toString()
                               + "\n=============================================\n");

                    // AARQ 객체 내 데이터 점검
                    if (aarqApdu.getAssociationVersion() != HealthcareConstants.Association.ASSOCIATION_VERSION) { // check AarqApdu field (association version)
                        sendAssociationResponse(HealthcareConstants.AssociationResult.REJECTED_UNSUPPORTED_ASSOC_VERSION);
                    } else if (!aarqApdu.isValidDataProtoId()) { // check AarqApdu field (data-proto-id)
                        sendAssociationResponse(HealthcareConstants.AssociationResult.REJECTED_NO_COMMON_PROTOCOL);
                    } else if (!aarqApdu.isValidEncodingRule()) { // check AarqApdu field (encoding rule)
                        sendAssociationResponse(HealthcareConstants.AssociationResult.REJECTED_NO_COMMON_PARAMETER);
                    }

                    if (managerState == HealthcareConstants.State.ASSOCIATING) {

                        // configuration 을 저장한 파일 이름 생성
                        StringBuffer sb = new StringBuffer();

                        switch (devConfigId) {
                            case HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_400:
                            case HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_401:
                            case HealthcareConstants.StandardDevConfigId.BLOOD_PRESSURE:
                            case HealthcareConstants.StandardDevConfigId.WEIGHING_SCALE:
                            case HealthcareConstants.StandardDevConfigId.GLUCOSE_METER_1700:
                            case HealthcareConstants.StandardDevConfigId.BODY_COMPOSITION_ANALYZER:

                                break;

                            default:
                                sb.append(deviceName);
                                sb.append("_");

                                break;
                        }

                        sb.append(Integer.toHexString(devConfigId));
                        sb.append(".hu");

                        fileName = sb.toString();

                        // association response send
                        sendAssociationResponse(aarqApdu.getDevConfigId(),
                                                aarqApdu.getDataProtoId(),
                                                HealthcareConstants.AssociationResult.ACCEPTED);
                    }

                } else {

                    // unassociated 상태인데 association request 가 아닌 command 가 수신
                    in.read(totalLen);
                    length = ByteUtil.short2ushort(ByteUtil.getshort(totalLen));

                    if (length == 0) {
                        length = 4;
                    }

                    body = new byte[length];
                    in.read(body);

                    // 받은시간
                    lastReceiveTime = System.currentTimeMillis();

//                    Log.d(TAG,
//                          ">> Request : \n" + ByteUtil.byte2hexa(cmd)
//                                      + ByteUtil.byte2hexa(totalLen)
//                                      + ByteUtil.byte2hexa(body));

                    AbrtApdu abrt = new AbrtApdu(HealthcareConstants.AbortReason.UNDEFINED);
                    byte[] response = abrt.getBytes();

//                    Log.d(TAG, "<< Association Response (cmd) : \n" + ByteUtil.byte2hexa(response));

                    out.write(response);
                    out.flush();

                    // 보낸시간
                    lastSendTime = System.currentTimeMillis();

                    checkTimeout = false;

                    managerState = HealthcareConstants.State.UNASSOCIATED;
                }
            }
        }

        /**
         * association response 전송
         * 
         * @param associationResult association request 에 대한 result
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private void sendAssociationResponse(short associationResult) throws IOException, ClassNotFoundException {
            sendAssociationResponse((short)0x0000, (short)0x0000, associationResult);
        }

        /**
         * association response 전송
         * 
         * @param devConfigId association request 에서 받은 devConfigId
         * @param dataProtoId association request 에서 받은 dataProtoId
         * @param associationResult association request 에 대한 result
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private    void
                    sendAssociationResponse(int devConfigId, short dataProtoId, short associationResult) throws IOException,
                                                                                                        ClassNotFoundException {

            if (associationResult == HealthcareConstants.AssociationResult.ACCEPTED) {

                // storage 저장 목록에서 해당 파일 검색
                boolean isExist = false;

                File dir = new File(AppConfig.STORAGE_PATH);
                dir.mkdir();

                String[] arr = dir.list();

                for (int i = 0; i < arr.length; i++) {

                    if (arr[i].equals(fileName)) {
                        isExist = true;

                        i = arr.length;
                    }
                }

                AareApdu aareApdu = null;
                if (isExist) {
                    aareApdu = new AareApdu();
                    aareApdu.setDataProtoId(dataProtoId);

                    checkIntervalTime = HealthcareConstants.Timeout.ASSOCIATION;

                    // configuration 이 존재한다면, 바로 operating 상태로 변경
                    managerState = HealthcareConstants.State.OPERATING;

                    // config 를 DB 에서 조회하여 로딩...
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(AppConfig.STORAGE_PATH + fileName));
                    config = (ConfigReport)ois.readObject();
                    ois.close();

                    checkTimeout = false;

                } else {
                    // accepted-unknown-config 를 전송하여 configuration 정보 수신 준비
                    aareApdu = new AareApdu(dataProtoId, HealthcareConstants.AssociationResult.ACCEPTED_UNKNOWN_CONFIG);

                    // timeout 을 체크해야 하기 때문에, 체크 기준시간과 timeout 이 발생했을 때 전송해야하는 error code 설정
                    checkIntervalTime = HealthcareConstants.Timeout.CONFIGURATION;
                    timeoutErrorCode = HealthcareConstants.AbortReason.CONFIGURATION_TIMEOUT;

                    managerState = HealthcareConstants.State.CONFIGURING;

                    checkTimeout = true;

                    // Get MDS 전송
                    sendMsgList.add(HealthcareConstants.SendMessage.GET_MDS);
                }

                byte[] response = aareApdu.getBytes();
//                Log.i(TAG, "<< Association Response : \n" + ByteUtil.byte2hexa(response));

                out.write(response);
                out.flush();

                // 보낸시간
                lastSendTime = System.currentTimeMillis();

                if (managerState == HealthcareConstants.State.OPERATING) {
                    if (sendMsgList.size() > 0) {
                        isSendToAgent = true;
                    }
                }

            } else {
                // 비정상 AARQ 이기 때문에 AARE 를 전송한 후 다시 AARQ 를 수신할 수 있도록 상태 변경
                AareApdu aareApdu = new AareApdu(dataProtoId, associationResult);

                byte[] response = aareApdu.getBytes();
//                Log.i(TAG, "<< Association Response : \n" + ByteUtil.byte2hexa(response));

                out.write(response);
                out.flush();

                // 보낸시간
                lastSendTime = System.currentTimeMillis();

                managerState = HealthcareConstants.State.UNASSOCIATED;

                checkTimeout = false;
            }
        }

        /**
         * receive
         * 
         * @throws IOException
         */
        private void receive() throws IOException {

            if (!isSendToAgent) {

                byte[] cmd = new byte[2];

                // configuring & operating
                if ((in.available() > 0) && (in.read(cmd) > -1)) {

                    byte[] totalLen = new byte[2];

                    int length = 0;

                    byte[] body = null;

                    if (ByteUtil.short2ushort(ByteUtil.getshort(cmd)) == HealthcareConstants.ApduChoiceType.PRESENTATION_APDU) {

                        in.read(totalLen);
                        length = ByteUtil.short2ushort(ByteUtil.getshort(totalLen));

                        body = new byte[length];
                        in.read(body);

                        // 받은시간
                        lastReceiveTime = System.currentTimeMillis();
                        checkTimeout = false;

//                        Log.d(TAG,
//                              ">> Presentation Request : \n" + ByteUtil.byte2hexa(cmd)
//                                          + ByteUtil.byte2hexa(totalLen)
//                                          + ByteUtil.byte2hexa(body));

                        ByteBuffer buffer = ByteBuffer.allocate(body.length).put(body);
                        buffer.position(0);

                        int octecStringLength = ByteUtil.short2ushort(buffer.getShort());
                        int invokeId = ByteUtil.short2ushort(buffer.getShort());

                        int choice = ByteUtil.short2ushort(buffer.getShort());
                        int choiceLength = ByteUtil.short2ushort(buffer.getShort());

                        // GET MDS response
                        if (choice == HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_GET) {

                            receiveGetMDSRequest(buffer, octecStringLength, invokeId, choice, choiceLength);

                        } else {

                            // configuration
                            if (managerState == HealthcareConstants.State.CONFIGURING) {

                                if (choice == HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_EVENT_REPORT) {

                                    receiveConfigReportRequest(buffer,
                                                               octecStringLength,
                                                               invokeId,
                                                               choice,
                                                               choiceLength);

                                } else {

                                    // config state
                                    // choice : roiv-cmip-get, roiv-cmip-set, roiv-cmip-confirmed-set, roiv-cmip-action, roiv-cmip-confirmed-action

                                    switch (choice) {
                                        case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_GET:
                                        case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_SET:
                                        case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_SET:
                                        case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_ACTION:
                                        case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_ACTION:

                                            AbrtApdu abrt = new AbrtApdu(HealthcareConstants.AbortReason.UNDEFINED);
                                            byte[] response = abrt.getBytes();

//                                            Log.d(TAG,
//                                                  "<< Presentation Response (ETC choice / Configurating) : \n" + ByteUtil.byte2hexa(response));

                                            out.write(response);
                                            out.flush();

                                            managerState = HealthcareConstants.State.UNASSOCIATED;

                                            checkTimeout = false;

                                            break;
                                    }
                                }

                            } else if (managerState == HealthcareConstants.State.OPERATING) {

                                // 측정 data 수신
                                if (choice == HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_EVENT_REPORT || choice == HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_EVENT_REPORT) {

                                    receiveEventReport(buffer, octecStringLength, invokeId, choice, choiceLength);

                                } else if (choice == HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_ACTION) {

                                    int objHandle = ByteUtil.short2ushort(buffer.getShort()); // objHandle
                                    int actionType = ByteUtil.short2ushort(buffer.getShort()); // action-type

                                    if (actionType == Nomenclature.Action.MDC_ACT_SEG_GET_INFO) {

                                        getSegInfoRes = new SegmentInfoList(buffer.slice());

                                        getSegInfoRes.setOctecStringLength(octecStringLength);
                                        getSegInfoRes.setChoice(choice);
                                        getSegInfoRes.setChoiceLength(choiceLength);

                                        getSegInfoRes.setObjHandle(objHandle);
                                        getSegInfoRes.setActionType(ByteUtil.ushort2short(actionType));

//                                        Log.d(TAG,
//                                              "========== [[ Segment Get Info ]] =========\n" + getSegInfoRes.toString()
//                                                          + "=======================================\n");

                                        segInstNo = getSegInfoRes.getSegInstNo();

                                        if (sendMsgList.size() > 0) {
                                            isSendToAgent = true;
                                        }

                                    } else if (actionType == Nomenclature.Action.MDC_ACT_SEG_TRIG_XFER) {

                                        TrigSegmDataXferRsp trigSegmDataXferRsp = new TrigSegmDataXferRsp(buffer.slice());

                                        trigSegmDataXferRsp.setOctecStringLength(octecStringLength);
                                        trigSegmDataXferRsp.setChoice(choice);
                                        trigSegmDataXferRsp.setChoiceLength(choiceLength);

                                        trigSegmDataXferRsp.setObjHandle(objHandle);
                                        trigSegmDataXferRsp.setActionType(ByteUtil.ushort2short(actionType));

//                                        Log.d(TAG,
//                                              "========= [[ Segment Triger ]] =========\n" + trigSegmDataXferRsp.toString()
//                                                          + "===========================================\n");

                                        if (sendMsgList.size() > 0) {
                                            isSendToAgent = true;
                                        }
                                    }

                                } else {

                                    // choice : roiv-cmip-get, roiv-cmip-set, roiv-cmip-confirmed-set, roiv-cmip-action, roiv-cmip-confirmed-action

                                    switch (choice) {
                                        case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_GET:
                                        case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_SET:
                                        case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_SET:
                                        case HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_ACTION:

                                            AbrtApdu abrt = new AbrtApdu(HealthcareConstants.AbortReason.UNDEFINED);
                                            byte[] response = abrt.getBytes();

//                                            Log.d(TAG,
//                                                  "<< Presentation Response (ETC choice / SCAN_REPORT_*) : \n" + ByteUtil.byte2hexa(response));

                                            out.write(response);
                                            out.flush();

                                            managerState = HealthcareConstants.State.UNASSOCIATED;

                                            checkTimeout = false;

                                            break;
                                    }
                                }
                            }
                        }

                        // association release request
                    } else if (ByteUtil.short2ushort(ByteUtil.getshort(cmd)) == HealthcareConstants.ApduChoiceType.ASSOCIATION_RELEASE_REQUEST) {

                        in.read(totalLen);
                        length = ByteUtil.short2ushort(ByteUtil.getshort(totalLen));

                        body = new byte[length];
                        in.read(body);

                        // 받은시간
                        lastReceiveTime = System.currentTimeMillis();

//                        Log.d(TAG,
//                              ">> Assocation Release Request : \n" + ByteUtil.byte2hexa(cmd)
//                                          + ByteUtil.byte2hexa(totalLen)
//                                          + ByteUtil.byte2hexa(body));

                        managerState = HealthcareConstants.State.DISASSOCIATING;

                        // send association release response
                        sendAssociationReleaseResponse();

                    } else {

                        in.read(totalLen);
                        length = ByteUtil.short2ushort(ByteUtil.getshort(totalLen));

                        if (length == 0) {
                            length = 4;
                        }

                        body = new byte[length];
                        in.read(body);

                        // 받은시간
                        lastReceiveTime = System.currentTimeMillis();

//                        Log.d(TAG, ">> Request : \n" + ByteUtil.byte2hexa(cmd)
//                                   + ByteUtil.byte2hexa(totalLen)
//                                   + ByteUtil.byte2hexa(body));

                        AbrtApdu abrt = new AbrtApdu(HealthcareConstants.AbortReason.UNDEFINED);
                        byte[] response = abrt.getBytes();

                        // 각 상태별로 받을 수 있는 message 의 형태가 다름. message 에 따라 응답 message 전송
                        switch (ByteUtil.short2ushort(ByteUtil.getshort(cmd))) {
                            case HealthcareConstants.ApduChoiceType.ASSOCIATION_REQUEST:
                            case HealthcareConstants.ApduChoiceType.ASSOCIATION_RESPONSE:
                            case HealthcareConstants.ApduChoiceType.ASSOCIATION_RELEASE_RESPONSE:

//                                Log.d(TAG, "<< Presentation Response (cmd) : \n" + ByteUtil.byte2hexa(response));

                                out.write(response);
                                out.flush();

                                // 보낸시간
                                lastSendTime = System.currentTimeMillis();

                                break;

                            case HealthcareConstants.ApduChoiceType.ASSOCIATION_ABORT:
                                break;

                            default:

                                if (managerState == HealthcareConstants.State.CONFIGURING || managerState == HealthcareConstants.State.OPERATING) {

//                                    Log.d(TAG, "<< Presentation Response (cmd) : \n" + ByteUtil.byte2hexa(response));

                                    out.write(response);
                                    out.flush();

                                    // 보낸시간
                                    lastSendTime = System.currentTimeMillis();

                                    break;

                                }

                                break;
                        }

                        checkTimeout = false;

                        managerState = HealthcareConstants.State.UNASSOCIATED;
                    }
                }
            }
        }

        /**
         * Config Report 분석
         * 
         * @param buffer
         * @param octecStringLength
         * @param invokeId
         * @param choice
         * @param choiceLength
         * @throws IOException
         */
        private void receiveConfigReportRequest(ByteBuffer buffer,
                                                int octecStringLength,
                                                int invokeId,
                                                int choice,
                                                int choiceLength) throws IOException {

            int mdsObject = ByteUtil.short2ushort(buffer.getShort());
            int eventTime = buffer.getInt();

            int eventType = ByteUtil.short2ushort(buffer.getShort());

            // configuration
            if (eventType == Nomenclature.Action.MDC_NOTI_CONFIG) {

                config = new ConfigReport(buffer.slice());

                config.setOctecStringLength(octecStringLength);
                config.setInvokeId(invokeId);
                config.setChoice(choice);
                config.setChoiceLength(choiceLength);

                config.setMDSObject(mdsObject);
                config.setEventTime(eventTime);

//                Log.i(TAG, "\n========== [[ Config ]] ==========\n" + config.toString()
//                           + "\n=============================================\n");

                // Episodic / Scanner 는 지원하지 않음.
                Iterator<Integer> itr = config.getConfigMap().keySet().iterator();
                while (itr.hasNext()) {

                    Integer key = itr.next();

                    Map<String, Object> attrMap = config.getConfigMap().get(key);
                    int objClass = (Integer)attrMap.get(HealthcareConstants.ConfigurationKey.OBJ_CLASS);

                    switch (objClass) {
                        case Nomenclature.ObjectInfra.MDC_MOC_SCAN:
                        case Nomenclature.ObjectInfra.MDC_MOC_SCAN_CFG:
                        case Nomenclature.ObjectInfra.MDC_MOC_SCAN_CFG_EPI:
                        case Nomenclature.ObjectInfra.MDC_MOC_SCAN_CFG_PERI:
                        case Nomenclature.ObjectInfra.MDC_MOC_PM_SEGMENT:

                            // Episodic / Scanner 는 지원하지 않음. 응답 전송
                            ConfigReport configRes = new ConfigReport(config.getInvokeId(),
                                                                      config.getConfigReportId(),
                                                                      HealthcareConstants.ConfigResult.UNSUPPORTED_CONFIG);

                            byte[] response = configRes.getBytes();
//                            Log.d(TAG, "<< Configuration Response : \n" + ByteUtil.byte2hexa(response));

                            out.write(response);
                            out.flush();

                            // 보낸 시간
                            lastSendTime = System.currentTimeMillis();

                            checkTimeout = false;

                            break;

                        case Nomenclature.ObjectInfra.MDC_MOC_VMO_PMSTORE:

                            isPMStore = true;
                            pmStoreObjHandle = (Integer)attrMap.get(HealthcareConstants.ConfigurationKey.OBJ_HANDLE);
                            break;

                        default:
                            break;
                    }
                }

                // standard config id 일 때와 extended config id 를 구분
                switch (config.getConfigReportId()) {
                    case HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_400:
                    case HealthcareConstants.StandardDevConfigId.PULSE_OXIMETER_401:
                    case HealthcareConstants.StandardDevConfigId.BLOOD_PRESSURE:
                    case HealthcareConstants.StandardDevConfigId.WEIGHING_SCALE:
                    case HealthcareConstants.StandardDevConfigId.GLUCOSE_METER_1700:
                    case HealthcareConstants.StandardDevConfigId.BODY_COMPOSITION_ANALYZER:
                    case HealthcareConstants.StandardDevConfigId.PEFM:

                        if ((config.getConfigObjListCount() < 1) && (config.getConfigObjListLength() < 1)) {

                            ConfigReport configRes = new ConfigReport(config.getInvokeId(),
                                                                      config.getConfigReportId(),
                                                                      HealthcareConstants.ConfigResult.STANDARD_CONFIG_UNKNOWN);

                            byte[] response = configRes.getBytes();
//                            Log.d(TAG, "<< Configuration Response : \n" + ByteUtil.byte2hexa(response));

                            out.write(response);
                            out.flush();

                            // 보낸 시간
                            lastSendTime = System.currentTimeMillis();

                            checkTimeout = false;
                        }

                        break;

                    default:

                        // extended config id 인데 config object list 가 0 이면 안됨
                        if ((config.getConfigObjListCount() < 1) && (config.getConfigObjListLength() < 1)) {

                            ConfigReport configRes = new ConfigReport(config.getInvokeId(),
                                                                      config.getConfigReportId(),
                                                                      HealthcareConstants.ConfigResult.UNSUPPORTED_CONFIG);

                            byte[] response = configRes.getBytes();
//                            Log.d(TAG, "<< Configuration Response : \n" + ByteUtil.byte2hexa(response));

                            out.write(response);
                            out.flush();

                            // 보낸 시간
                            lastSendTime = System.currentTimeMillis();

                            checkTimeout = false;
                        }

                        break;
                }

                if ((config.getConfigObjListCount() > 0) && (config.getConfigObjListLength() > 0)) {

                    // profile 과 config 비교

                    switch (healthProfile) {
                        case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_PULS_OXIM:
                        case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BP:
                        case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_TEMP:
                        case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_SCALE:
                        case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_GLUCOSE:

                            itr = config.getConfigMap().keySet().iterator();
                            while (itr.hasNext()) {

                                @SuppressWarnings("unchecked")
                                Map<Integer, Attribute> attrMap = (Map<Integer, Attribute>)config.getConfigMap(itr.next())
                                                                                                 .get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                                // 존재해야 하는 attribute 를 체크
                                if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_ID_TYPE)) {
                                    TYPE typeAttr = (TYPE)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_TYPE);

                                    switch (typeAttr.getCode()) {

                                        // 산소포화도
                                        case Nomenclature.DataAcqu.MDC_PULS_OXIM_SAT_O2:
                                        case Nomenclature.DataAcqu.MDC_PULS_OXIM_PULS_RATE:
                                            isValidConfig = true;
                                            break;

                                        // 혈압계
                                        case Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV:
                                        case Nomenclature.DataAcqu.MDC_PULS_RATE_NON_INV:
                                            isValidConfig = true;
                                            break;

                                        // 체온계
                                        case Nomenclature.DataAcqu.MDC_TEMP_BODY:
                                            isValidConfig = true;
                                            break;

                                        // 체중계
                                        case Nomenclature.DataAcqu.MDC_MASS_BODY_ACTUAL:
                                        case Nomenclature.DataAcqu.MDC_LEN_BODY_ACTUAL:
                                        case Nomenclature.DataAcqu.MDC_RATIO_MASS_BODY_LEN_SQ:
                                            isValidConfig = true;
                                            break;

                                        // 혈당계
                                        case Nomenclature.DataAcqu.MDC_CONC_GLU_GEN:
                                        case Nomenclature.DataAcqu.MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD:
                                        case Nomenclature.DataAcqu.MDC_CONC_GLU_CAPILLARY_PLASMA:
                                        case Nomenclature.DataAcqu.MDC_CONC_GLU_VENOUS_WHOLEBLOOD:
                                        case Nomenclature.DataAcqu.MDC_CONC_GLU_VENOUS_PLASMA:
                                        case Nomenclature.DataAcqu.MDC_CONC_GLU_ARTERIAL_WHOLEBLOOD:
                                        case Nomenclature.DataAcqu.MDC_CONC_GLU_ARTERIAL_PLASMA:
                                        case Nomenclature.DataAcqu.MDC_CONC_GLU_CONTROL:
                                        case Nomenclature.DataAcqu.MDC_CONC_GLU_ISF:
                                            isValidConfig = true;
                                            break;
                                    }
                                }
                            }

                            break;

                        case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_BCA:
                        case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_PEFM:
                        case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_HF_CARDIO:
                        case Nomenclature.DeviceSpec.MDC_DEV_SPEC_PROFILE_AI_MED_MINDER:
                            isValidConfig = true;
                            break;
                    }

                    // 유효하지 않은 config
                    if (!isValidConfig) {

                        sendConfigEventReportResponse(config.getInvokeId(),
                                                      config.getConfigReportId(),
                                                      HealthcareConstants.ConfigResult.UNSUPPORTED_CONFIG);
                    } else {

                        if (getMDS != null) {

                            sendConfigEventReportResponse(config.getInvokeId(),
                                                          config.getConfigReportId(),
                                                          HealthcareConstants.ConfigResult.ACCEPTED_CONFIG);
                        } else {

                            if (!sendMsgList.contains(HealthcareConstants.SendMessage.GET_MDS)) {
                                sendMsgList.add(HealthcareConstants.SendMessage.GET_MDS);
                            }
                        }

                        if (isPMStore) {
                            sendMsgList.add(HealthcareConstants.SendMessage.GET_SEG_INFO);
                        }
                    }

                    if (sendMsgList.size() > 0) {
                        isSendToAgent = true;
                    } else {
                        isSendToAgent = false;
                    }
                }
            }
        }

        /**
         * Get MDS Request 분석
         * 
         * @param buffer
         * @param octecStringLength
         * @param invokeId
         * @param choice
         * @param choiceLength
         * @throws IOException
         */
        private void receiveGetMDSRequest(ByteBuffer buffer,
                                          int octecStringLength,
                                          int invokeId,
                                          int choice,
                                          int choiceLength) throws IOException {

            // 전송받은 mds 정보를 분석
            getMDS = new GetMDSPrstApdu(buffer.slice());
            getMDS.setOctecStringLength(octecStringLength);
            getMDS.setInvokeId(invokeId);
            getMDS.setChoice(choice);
            getMDS.setChoiceLength(choiceLength);

            TypeVerList sysType = (TypeVerList)getMDS.getAttribute(Nomenclature.Attribute.MDC_ATTR_SYS_TYPE_SPEC_LIST);
            RegCertDataList regCert = (RegCertDataList)getMDS.getAttribute(Nomenclature.Attribute.MDC_ATTR_REG_CERT_DATA_LIST);

//            Log.i(TAG, "\n========== [[ Get MDS ]] ==========\n" + getMDS.toString()
//                       + "\n======================================\n");
//
//            Log.d(TAG, sysType == null ? "sysType null" : "sysType not null");
//            Log.d(TAG, regCert == null ? "regCert null" : "regCert not null");

            // Get MDS 응답메세지에는 아래 두 attribute 가 항상 존재해야 함
            if ((sysType == null) || (regCert == null)) {

                if (managerState == HealthcareConstants.State.CONFIGURING) {

                    sendConfigEventReportResponse(config.getInvokeId(),
                                                  config.getConfigReportId(),
                                                  HealthcareConstants.ConfigResult.UNSUPPORTED_CONFIG);

                } else if (managerState == HealthcareConstants.State.OPERATING) {
                    AbrtApdu abrt = new AbrtApdu(HealthcareConstants.AbortReason.UNDEFINED);
                    byte[] response = abrt.getBytes();

//                    Log.d(TAG, "<< Presentation Response (GET MDS / operating) : \n" + ByteUtil.byte2hexa(response));

                    out.write(response);
                    out.flush();

                    managerState = HealthcareConstants.State.UNASSOCIATED;

                    checkTimeout = false;
                }

            } else {

                // healthProfile 갱신
                if (sysType != null) {
                    healthProfile = sysType.getTypeVer().getType();
                }

//                Log.d(TAG, "healthProfile : " + healthProfile);

                //                MdsTimeInfo mdsTimeInfo = (MdsTimeInfo)getMDS.getAttribute(Nomenclature.Attribute.MDC_ATTR_MDS_TIME_INFO);
                //                if (mdsTimeInfo != null) {
                //
                //                    if (mdsTimeInfo.isMdsTimeMgrSetTime()) {
                //                        sendMsgList.add(ManagerConstants.SendMessage.SET_TIME);
                //                    }
                //                }

                if (managerState == HealthcareConstants.State.CONFIGURING) {

                    if (isValidConfig) {
                        sendConfigEventReportResponse(config.getInvokeId(),
                                                      config.getConfigReportId(),
                                                      HealthcareConstants.ConfigResult.ACCEPTED_CONFIG);

                        // configuration
                        if (isSaveConfig) {

                            devConfigId = config.getConfigReportId();

                            // configuration 을 저장한 파일 이름 생성
                            StringBuffer sb = new StringBuffer();

                            if (!ManagerUtil.isStandardDevConfigId(devConfigId)) {
                                sb.append(deviceName);
                                sb.append("_");
                            }
                            sb.append(Integer.toHexString(devConfigId));
                            sb.append(".hu");

                            fileName = sb.toString();

//                            Log.d(TAG, "fileName : " + fileName);
//
//                            Log.i(TAG, "Scanner / Episodic unsupported. devConfigId value changed ...");

                            // configuration 저장
                            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AppConfig.STORAGE_PATH + fileName));
                            oos.writeObject(config);
                            oos.close();

                            // getMDS 저장
                            sendMessage(STATUS_SAVE_MDS_DATA, 0, makeMDSData(getMDS));
                        }

                    } else {
                        sendConfigEventReportResponse(config.getInvokeId(),
                                                      config.getConfigReportId(),
                                                      HealthcareConstants.ConfigResult.UNSUPPORTED_CONFIG);
                    }

//                    Log.d(TAG, "isSaveConfig : " + isSaveConfig + ", isValidConfig : " + isValidConfig);

                } else if (managerState == HealthcareConstants.State.OPERATING) {

                    // getMDS 저장
                    sendMessage(STATUS_SAVE_MDS_DATA, 0, makeMDSData(getMDS));

                    if (isPMStore) {
                        sendMsgList.add(HealthcareConstants.SendMessage.GET_SEG_INFO);
                    }
                }
            }

            if (sendMsgList.size() > 0) {
                isSendToAgent = true;
            } else {
                isSendToAgent = false;
            }
        }

        /**
         * Event Report 분석
         * 
         * @param buffer
         * @param octecStringLength
         * @param invokeId
         * @param choice
         * @param choiceLength
         * @throws IOException
         */
        private void receiveEventReport(ByteBuffer buffer,
                                        int octecStringLength,
                                        int invokeId,
                                        int choice,
                                        int choiceLength) throws IOException {

            int objHandle = ByteUtil.short2ushort(buffer.getShort());
            int eventTime = buffer.getInt();
            int eventType = ByteUtil.short2ushort(buffer.getShort());

            // SegmentDataEvent 와 구분
            if (eventType == Nomenclature.Action.MDC_NOTI_SEGMENT_DATA) {

                segmData = new SegmentDataEvent(config, getSegInfoRes, buffer.slice());

                segmData.setOctecStringLength(octecStringLength);
                segmData.setInvokeId(invokeId);
                segmData.setChoice(choice);
                segmData.setChoiceLength(choiceLength);

                segmData.setObjHandle(objHandle);
                segmData.setEventTime(eventTime);
                segmData.setEventType(eventType);

//                Log.i(TAG, "\n========== [[ Segment Data ]] ===========\n" + segmData.toString()
//                           + "\n=============================================\n");

                makeResultData();

                // send response 
                SegmentDataResult segmentDataResult = new SegmentDataResult(invokeId);
                segmentDataResult.setObjHandle(objHandle);
                segmentDataResult.setEventTime(eventTime);

                segmentDataResult.setSegmInstances(segmData.getSegmInstance());
                segmentDataResult.setSegmEvtEntryIndex(segmData.getSegmEvtEntryIndex());
                segmentDataResult.setSegmEvtEntryCount(segmData.getSegmEvtEntryCount());

                if (segmData.getSegmEvtStatus() == SegmentDataEvent.SegmEvtStatus.SEVTSTA_FIRST_ENTRY) {
                    segmentDataResult.setSegmEvtStatus(SegmentDataEvent.SegmEvtStatus.SEVTSTA_FIRST_ENTRY_MANAGER_CONFIRM);
                } else if (segmData.getSegmEvtStatus() == SegmentDataEvent.SegmEvtStatus.SEVTSTA_LAST_ENTRY) {
                    segmentDataResult.setSegmEvtStatus(SegmentDataEvent.SegmEvtStatus.SEVTSTA_LAST_ENTRY_MANAGER_CONFIRM);
                } else if (segmData.getSegmEvtStatus() == SegmentDataEvent.SegmEvtStatus.SEVTSTA_FIRST_ENTRY_AGENT_ABORT) {
                    segmentDataResult.setSegmEvtStatus(SegmentDataEvent.SegmEvtStatus.SEVTSTA_FIRST_ENTRY_AGENT_ABORT_MANAGER_CONFIRM);
                } else if (segmData.getSegmEvtStatus() == SegmentDataEvent.SegmEvtStatus.SEVTSTA_LAST_ENTRY_AGENT_ABORT) {
                    segmentDataResult.setSegmEvtStatus(SegmentDataEvent.SegmEvtStatus.SEVTSTA_LAST_ENTRY_AGENT_ABORT_MANAGER_CONFIRM);
                }

                byte[] response = segmentDataResult.getBytes();

//                Log.d(TAG, "<< Noti Segment Data Response : \n" + ByteUtil.byte2hexa(response));

                out.write(response);
                out.flush();

            } else {

                data = new DataPrstApdu();

                data.setOctecStringLength(octecStringLength);
                data.setInvokeId(invokeId);
                data.setChoice(choice);
                data.setChoiceLength(choiceLength);

                data.setObjHandle(objHandle);
                data.setEventTime(eventTime);
                data.setEventType(eventType);
                data.setEventInfoLength(ByteUtil.short2ushort(buffer.getShort()));

                ByteBuffer buf = buffer.slice();

                // config 객체를 가지고 data 를 parsing
                int dataParsingResultCode = ManagerUtil.parseDataPrstApdu(config, getMDS, data, buf);

//                if (eventType == Nomenclature.Action.MDC_NOTI_SCAN_REPORT_VAR) {
//                    Log.i(TAG, "========= [[ MDC_NOTI_SCAN_REPORT_VAR - Config ]] =========\n" + config.toString()
//                               + "==================================\n");
//                }

                if (dataParsingResultCode == HealthcareConstants.DataParsingResultCode.SUCCESS) {

                    //                    Log.i(TAG, "========= [[ Data Report ]] =========\n" + data.toString()
                    //                               + "==================================\n");

                    if (choice == HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_EVENT_REPORT) {

                        // data report response 전송
                        sendScanReportResponse(invokeId, eventType);
                    }

                    makeResultData();

                } else if (dataParsingResultCode == HealthcareConstants.DataParsingResultCode.GET_MDS_INFO) {

                    // data report response 전송
                    sendScanReportResponse(invokeId, eventType);

                } else {

                    // data report response 전송
                    sendScanReportResponse(invokeId, eventType);

                    makeResultData();

                    managerState = HealthcareConstants.State.UNASSOCIATED;

                    checkTimeout = false;
                }
            }
        }

        /**
         * configuration response 전송
         * 
         * @param invokeId request message 에 있었던 invoke id
         * @param configReportId config report id
         * @param configResult configuration request 결과
         * @throws IOException
         */
        private    void
                    sendConfigEventReportResponse(int invokeId, int configReportId, int configResult) throws IOException {

            ConfigReport configRes = new ConfigReport(invokeId, configReportId, configResult);

            if (configResult == HealthcareConstants.ConfigResult.ACCEPTED_CONFIG) {

                // association request 에 dev-config-id 와 동일한지 체크
                if (ManagerUtil.checkHealthProfileConfigId(healthProfile, devConfigId, configReportId)) {
                    isSendToAgent = true;
                    isSaveConfig = true;
                } else {
                    configRes.setConfigResult(HealthcareConstants.ConfigResult.UNSUPPORTED_CONFIG);
                }

                byte[] response = configRes.getBytes();
//                Log.d(TAG, "<< Configuration Response : \n" + ByteUtil.byte2hexa(response));

                out.write(response);
                out.flush();

                // 보낸시간
                lastSendTime = System.currentTimeMillis();

                managerState = HealthcareConstants.State.OPERATING;

                checkTimeout = false;

            } else {

                byte[] response = configRes.getBytes();
//                Log.d(TAG, "<< Configuration Response : \n" + ByteUtil.byte2hexa(response));

                out.write(response);
                out.flush();

                // 보낸시간
                lastSendTime = System.currentTimeMillis();

                checkTimeout = false;
            }
        }

        /**
         * SCAN_REPORT_* Response 전송
         * 
         * @param invokeId request message 내 invokeId
         * @param eventType request message 내 eventType 에 맞는 response message eventType
         * @throws IOException
         */
        private void sendScanReportResponse(int invokeId, int eventType) throws IOException {

            // data report response 전송
            DataPrstApdu dataRes = new DataPrstApdu(invokeId, eventType);
            byte[] response = dataRes.getBytes();
//            Log.d(TAG, "<< Data Report Response : \n" + ByteUtil.byte2hexa(response));

            out.write(response);
            out.flush();

            // 보낸시간
            lastSendTime = System.currentTimeMillis();

            checkTimeout = false;
        }

        /**
         * association release response 전송
         * 
         * @throws IOException
         */
        private void sendAssociationReleaseResponse() throws IOException {

            // association release request 를 받은 상태
            if (managerState == HealthcareConstants.State.DISASSOCIATING) {

                RlreApdu rlreApdu = new RlreApdu();

                byte[] response = rlreApdu.getBytes();
//                Log.d(TAG, "<< Assocation Release Response : \n" + ByteUtil.byte2hexa(response));

                out.write(response);
                out.flush();

                // 보낸시간
                lastSendTime = System.currentTimeMillis();

                checkTimeout = false;

                managerState = HealthcareConstants.State.UNASSOCIATED;

                // association ~ association release 사이에 누적된 측정 데이터를 UI 로 전달
                sendMeasureResult();
            }
        }

        /**
         * Association ~ Association Release 사이에 누적된 측정 데이터 및 측정 디바이스 정보를 UI로 전달
         */
        private void sendMeasureResult() {

            if (!rList.isEmpty()) {

                Bundle rtnBundle = new Bundle();

                rtnBundle.putBoolean("isSuccess", true);

                rtnBundle.putInt("healthProfile", healthProfile);

                if (mdsInfo != null) {

                    rtnBundle.putString("deviceCompany", mdsInfo.get(ManagerConstants.DataBase.COLUMN_NAME_COMPANY));

                    rtnBundle.putString("deviceModel", mdsInfo.get(ManagerConstants.DataBase.COLUMN_NAME_MODEL));

                    rtnBundle.putString("deviceId", mdsInfo.get(ManagerConstants.DataBase.COLUMN_NAME_SYSTEM_ID));
                }

//                Log.d(TAG,
//                      "deviceCompany : " + rtnBundle.getString("deviceCompany")
//                                  + ", deviceModel : "
//                                  + rtnBundle.getString("deviceModel")
//                                  + ", deviceId : "
//                                  + rtnBundle.getString("deviceId"));

                measureResult.putParcelableArrayList("data", rList);

                rtnBundle.putBundle("measureResult", measureResult);
                sendMessage(STATUS_READ_DATA_DONE, 0, rtnBundle);
                sendMeasureData = true;
            }
        }

        /**
         * agent 로 보내는 메시지 전송
         * 
         * @throws IOException
         */
        private void send() throws IOException {

            if (isSendToAgent) {
                if (!sendMsgList.isEmpty()) {

                    // get mds message 전송 여부
                    if (HealthcareConstants.SendMessage.GET_MDS.equals(sendMsgList.get(0))) {

                        // get mds message 는 configuring 이나 operating 상태에서 전송 가능
                        if (managerState == HealthcareConstants.State.CONFIGURING || managerState == HealthcareConstants.State.OPERATING) {

                            // GET MDS attribute request 전송 sleep time ...
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException ie) {
                                //Log.e(TAG, ie.getMessage(), ie);
                                ie.printStackTrace();
                            }

                            // GET MDS attribute request 전송
                            GetMDSPrstApdu getMDSReq = new GetMDSPrstApdu();

                            byte[] request = getMDSReq.getBytes();
//                            Log.d(TAG, "<< Get MDS Request : \n" + ByteUtil.byte2hexa(request));

                            out.write(request);
                            out.flush();

                            sendMsgList.remove(0);
                            isSendToAgent = false;

                            checkTimeout = true;

                            lastSendTime = System.currentTimeMillis();

                            // timeout 을 체크해야 하기 때문에, 체크 기준시간과 timeout 이 발생했을 때 전송해야하는 error code 설정
                            checkIntervalTime = HealthcareConstants.Timeout.GET;
                            timeoutErrorCode = HealthcareConstants.AbortReason.RESPONSE_TIMEOUT;
                        }

                    } else if (HealthcareConstants.SendMessage.SET_TIME.equals(sendMsgList.get(0))) {
                        // set time message 전송 여부

                        // set timemessage 는 configurating 이나 operating 상태에서 전송 가능
                        if (managerState == HealthcareConstants.State.CONFIGURING || managerState == HealthcareConstants.State.OPERATING) {

                            // SET Time request 전송 sleep time ...
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException ie) {
                                //Log.e(TAG, ie.getMessage(), ie);
                                ie.printStackTrace();
                            }

                            SetTimePrstApdu setTimeReq = new SetTimePrstApdu();
                            byte[] request = setTimeReq.getBytes();

//                            Log.d(TAG, "<< Set Time Request : \n" + ByteUtil.byte2hexa(request));

                            out.write(request);
                            out.flush();

                            sendMsgList.remove(0);
                            isSendToAgent = false;

                            checkTimeout = true;

                            lastSendTime = System.currentTimeMillis();

                            // timeout 을 체크해야 하기 때문에, 체크 기준시간과 timeout 이 발생했을 때 전송해야하는 error code 설정
                            checkIntervalTime = HealthcareConstants.Timeout.CONFIRM_ACTION;
                            timeoutErrorCode = HealthcareConstants.AbortReason.RESPONSE_TIMEOUT;
                        }

                    } else if (HealthcareConstants.SendMessage.GET_SEG_INFO.equals(sendMsgList.get(0))) {
                        // segment get info message 전송 여부
                        if (managerState == HealthcareConstants.State.OPERATING) {

                            // SET Time request 전송 sleep time ...
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException ie) {
                                //Log.e(TAG, ie.getMessage(), ie);
                                ie.printStackTrace();
                            }

                            SegmSelection getSegReq = new SegmSelection(pmStoreObjHandle);
                            byte[] request = getSegReq.getBytes();

//                            Log.d(TAG, "<< Get Segment Info Request : \n" + ByteUtil.byte2hexa(request));

                            out.write(request);
                            out.flush();

                            sendMsgList.remove(0);
                            isSendToAgent = false;

                            checkTimeout = true;

                            lastSendTime = System.currentTimeMillis();

                            // timeout 을 체크해야 하기 때문에, 체크 기준시간과 timeout 이 발생했을 때 전송해야하는 error code 설정
                            checkIntervalTime = HealthcareConstants.Timeout.CONFIRM_ACTION;
                            timeoutErrorCode = HealthcareConstants.AbortReason.RESPONSE_TIMEOUT;
                        }

                    } else if (HealthcareConstants.SendMessage.SEG_TRIG_XFER.equals(sendMsgList.get(0))) {

                        if (managerState == HealthcareConstants.State.OPERATING) {

                            // SET Time request 전송 sleep time ...
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException ie) {
                                //Log.e(TAG, ie.getMessage(), ie);
                                ie.printStackTrace();
                            }

                            TrigSegmDataXferReq trigSegmReq = new TrigSegmDataXferReq(pmStoreObjHandle, segInstNo);
                            byte[] request = trigSegmReq.getBytes();

//                            Log.d(TAG, "<< Segment Trig Request : \n" + ByteUtil.byte2hexa(request));

                            out.write(request);
                            out.flush();

                            sendMsgList.remove(0);
                            isSendToAgent = false;

                            checkTimeout = true;

                            lastSendTime = System.currentTimeMillis();

                            // timeout 을 체크해야 하기 때문에, 체크 기준시간과 timeout 이 발생했을 때 전송해야하는 error code 설정
                            checkIntervalTime = HealthcareConstants.Timeout.CONFIRM_ACTION;
                            timeoutErrorCode = HealthcareConstants.AbortReason.RESPONSE_TIMEOUT;
                        }
                    }
                }
            }
        }

        /**
         * Timeout 확인
         * 
         * @throws IOException
         */
        private void checkTimeout() throws IOException {

            if (checkTimeout) {

                long interval = System.currentTimeMillis() - lastSendTime;

                //                Log.d(TAG, "interval : " + interval + ", checkIntervalTime : " + checkIntervalTime);

                if (checkTimeout && (lastReceiveTime > 0) && (interval > checkIntervalTime)) {

//                    Log.d(TAG, "interval : " + interval + ", checkIntervalTime : " + checkIntervalTime);

                    // TOconfig 를 초과하여 association abort 전송
                    AbrtApdu abrtApdu = new AbrtApdu(timeoutErrorCode);
                    byte[] response = abrtApdu.getBytes();
//                    Log.d(TAG, "<< AbrtApdu : \n" + ByteUtil.byte2hexa(response));

                    out.write(response);
                    out.flush();

                    // 보낸시간
                    lastSendTime = System.currentTimeMillis();

                    managerState = HealthcareConstants.State.UNASSOCIATED;
                }
            }
        }
    }
}