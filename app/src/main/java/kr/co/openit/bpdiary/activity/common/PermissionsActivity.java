package kr.co.openit.bpdiary.activity.common;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import kr.co.openit.bpdiary.dialog.DialogPermissions;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;

/**
 * Created by srpark on 2016-12-12.
 */
public class PermissionsActivity extends Activity {

    private static String[] PERMISSIONS = {Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final int REQUEST_PERMISSION = 50;

    private DialogPermissions dialogPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogPermissions = new DialogPermissions(PermissionsActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialogPermissions != null && !dialogPermissions.isShowing()) {
            if (verifyStoragePermissions(PermissionsActivity.this, PERMISSIONS)) {
                setResult(RESULT_OK);
                finish();
            }
        } else if (dialogPermissions == null) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    /**
     * 혈압다이어리에 필요한 모든권한 체크
     *
     * @param activity     : context
     * @param grantResults : 권한여부
     * @return
     */
    public static boolean verifyStoragePermissions(Activity activity, String[] grantResults) {
        boolean isResults = true;
        for (String results : grantResults) {
            int permission = ContextCompat.checkSelfPermission(activity, results);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                isResults = false;
            }
        }
        if (!isResults) {
            ActivityCompat.requestPermissions(activity, grantResults, REQUEST_PERMISSION);
        }
        return isResults;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                boolean isPermissionGranted = true;
                String grant = "";
                for (int i = 0; i < grantResults.length; i++) {
                    Log.i("srpark", "grantResults = " + grantResults[i]);
                    Log.i("srpark", "Permission = " + permissions[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isPermissionGranted = false;
                        grant = permissions[i];
                        break;
                    }
                }
                if (isPermissionGranted) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    if (dialogPermissions != null && !dialogPermissions.isShowing()) {
                        dialogPermissions =
                                new DialogPermissions(PermissionsActivity.this, grant, new IDefaultDialog() {

                                    @Override
                                    public void onConfirm() {
                                        Intent intent = null;
                                        try {
                                            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                        } catch (ActivityNotFoundException e) {
                                            e.printStackTrace();
                                            intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                        } finally {
                                            startActivity(intent);
                                            dialogPermissions.cancel();
                                        }
                                    }

                                    @Override
                                    public void onCancel() {
                                        setResult(RESULT_CANCELED);
                                        finish();
                                    }
                                });
                        dialogPermissions.show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
