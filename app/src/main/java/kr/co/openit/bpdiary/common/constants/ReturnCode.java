package kr.co.openit.bpdiary.common.constants;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * 각종 상태에 대한 리턴 코드
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2012. 5. 14.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public enum ReturnCode {
	DOES_NOT_SUPPORT_BLUETOOTH,
	BLUETOOTH_IS_NOT_TURNED_ON,
	BLUETOOTH_SERVICE_START,
	BLUETOOTH_BLE_SERVICE_START,
	NFC_SERVICE_START,
	NFC_SERVICE_UNABLE,
	BLUETOOTH_SERVICE_ALREADY,
	BLUETOOTH_CONNECT_SUCCESS,
	BLUETOOTH_DISCONNECT_SUCCESS,
	NOT_FOUND_BLUETOOTH_DEVICE,
	INVALID_PARAMETA,
	DOES_NOT_BIND_SERVICE,
	DEVICE_CONNECT_SUCCESS,
	DISCONNECT_SUCCESS,
	LISTENER_IS_NOT_NULL,
	MEASURE_FINISHED,
	MEASURE_DEVICE_CONNECT
}
