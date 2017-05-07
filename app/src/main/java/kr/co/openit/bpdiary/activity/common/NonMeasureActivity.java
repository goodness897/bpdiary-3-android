package kr.co.openit.bpdiary.activity.common;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.common.measure.bean.MeasureService;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;

/**
 * 블루투스 미연동 Activity
 */
public class NonMeasureActivity extends BaseActivity {

    /**
     * MeasureService 와 메세지를 송수신하는 messenger
     */
    private Messenger healthService;

    private CustomProgressDialog customProgressDialog = null;

    /**
     * Sets up communication with {@link MeasureService}.
     */
    private final Handler doNothingHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            //Log.w(TAG, "doNothingHandler - msg.what : " + msg.what + ", msg arg1 : " + msg.arg1);
        }
    };

    /**
     * MeasureService 와 메세지를 송수신하는 messenger
     */
    private final Messenger doNothingMesseger = new Messenger(doNothingHandler);

    /**
     * Sets up communication with {@link MeasureService}.
     */
    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.i(TAG, "connection.onServiceConnected()");

            Message msg = Message.obtain(null, MeasureService.MSG_REG_CLIENT);
            msg.replyTo = doNothingMesseger;
            msg.obj = TAG;
            healthService = new Messenger(service);

            try {
                healthService.send(msg);
            } catch (RemoteException re) {
                //Log.w(TAG, "connection. Unable to register client to service.");
                re.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            healthService = null;
            //Log.i(TAG, "connection.onServiceDisconnected()");
        }
    };

    /**
     * unbind Service
     */
    protected void unbindDoNothingService() {
        unbindService(connection);
        //Log.i(TAG, "unbindDoNothingService()");
    }

    /**
     * bind Service
     */
    protected void bindDoNothingService() {
        // Starts health service.
        Intent intent = new Intent(this, MeasureService.class);

        bindService(intent, connection, BIND_AUTO_CREATE);

        //Log.i(TAG, "bindDoNothingService()");
    }

    protected void showLodingProgress() {
        if (customProgressDialog == null && !NonMeasureActivity.this.isFinishing()) {
            customProgressDialog = new CustomProgressDialog(NonMeasureActivity.this);
            customProgressDialog.setCancelable(false);
            customProgressDialog.setCanceledOnTouchOutside(false);
        }
        customProgressDialog.show();
    }

    protected void hideLodingProgress() {
        if (customProgressDialog != null && customProgressDialog.isShowing()) {
            try {
                customProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
