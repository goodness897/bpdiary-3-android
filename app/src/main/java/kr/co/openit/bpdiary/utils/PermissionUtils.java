package kr.co.openit.bpdiary.utils;

import android.Manifest;
import android.app.Activity;

public final class PermissionUtils {

    /**
     * 생성자
     */
    private PermissionUtils() {
        // Default Constructor
    }

    /**
     * Permission 확인
     *
     * @param activity
     * @param strPermissions
     * @return
     */
    public static boolean checkPermission(Activity activity, String[] strPermissions) {

        boolean isPermission = true;

        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            for (int i = 0; i < strPermissions.length; i++ ) {

                if (ContextCompat.checkSelfPermission(activity, strPermissions[i])
                        != PackageManager.PERMISSION_GRANTED) {

                    isPermission = false;
                    break;
                }
            }
        }
        */

        return isPermission;

    }

    /**
     * 권한 확인(GET_ACCOUNT)
     *
     * @param activity
     * @return
     */
    public static boolean checkPermissionAccounts(Activity activity) {
        return PermissionUtils.checkPermission(activity, new String[]{Manifest.permission.GET_ACCOUNTS});
    }

    /**
     * 권한 확인(WRITE_EXTERNAL_STORAGE)
     *
     * @param activity
     * @return
     */
    public static boolean checkPermissionStorage(Activity activity) {
        return PermissionUtils.checkPermission(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    /**
     * 권한 확인(ACCESS_COARSE_LOCATION)
     *
     * @param activity
     * @return
     */
    public static boolean checkPermissionAccessCoarseLocation(Activity activity) {
        //return PermissionUtils.checkPermission(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION});
        return true;
    }
}
