package kr.co.openit.bpdiary.common.controller;

import kr.co.openit.bpdiary.common.constants.ReturnCode;

/**
 * BLE 서비스에 관한 상태 값 결과 값을 전달하는 리스너
 * 
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2012. 5. 14.
 */
public interface BlueToothWSBLEDeviceInterface {
	public ReturnCode onSuccess(String measurDeviceType, ReturnCode rc);

	public void onSuccess(int cnt, String measureType, String weight);

	public void onFail(String measurDeviceType, String errorCode);

}
