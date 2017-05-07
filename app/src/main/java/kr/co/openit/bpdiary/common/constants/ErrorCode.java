package kr.co.openit.bpdiary.common.constants;

/**
 * HTTP, JSON, Bluetooth, Thread, Process, Server 등 각각의 Error 코드를 정의 하는 클래스
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2012. 5. 14.
 */

import android.annotation.TargetApi;
import android.os.Build;

/**
 *
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2012. 5. 14.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ErrorCode {	
	
	// HTTP
	/**
	 * http 연결 실패
	 */
	public static String HTTP_CONNECTION_EXCEPTION = "E1000";
	
	/**
	 * 인터넷 연결 상태 확인 필요
	 */
	public static String NETWORK_ERROR = "E1001";
	
	
	// JSON
	/**
	 *  JSON에서 발생하는 에러
	 */
	public static String JSON_EXCEPTION = "E2000";
	
	/**
	 * JSON에서 데이터를 분석 하는 중 발생하는 에러
	 */
	public static String JSON_PARSE_EXCEPTION = "E2001";
	
	
	// BLUETOOTH
	/**
	 * 안드로이드 블루투스 서비스 초기화 실패 에러
	 */
	public static String BLUETOOTH_NOT_INITIALIZE = "E3000";
	
	/**
	 * 안드로이드 블루투스 타임아웃 에러
	 */
	public static String BLUETOOTH_CONNECT_TIMEOUT = "E3001";
	
	/**
	 * 블루투스 소켓 장비 전송 데이터 에러
	 */
	public static String BLUETOOTH_DATA_ERROR = "E3002";
	
	
	// THREAD	
	/**
	 * 쓰레드에서 조정할 수 있는 환경을 넘어선 환경에 의해서 발생되는 에러
	 */
	public static String THREAD_RUNTIME_EXCEPTION = "E4000";
	
	/**
	 * 쓰레드가 중지되어 발생되는 에러
	 */
	public static String THREAD_INTERRUPTED_EXCEPTION = "E4001";
	
	
	// PROCESS
	/**
	 * 프로세스에서 토큰값 갱신을 위해 재로그인 시도를 실패 했을 경우 발생하는 에러
	 */
	public static String PROCESS_NOT_LOGIN = "E5000";
	
	/**
	 * 이미 동일한 프로세스가 실행 중일 때 중복된 프로세스가 실행 되지 않게 하기 위해 발생하는 에러
	 */
	public static String PROCESS_DUPLICATE_REQUESTS = "E5001";
	
	/**
	 * SDK 내부에서 처리 할 수 있는 프로세스의 숫자가 넘어 갔을때 발생하는 에러
	 */
	public static String PROCESS_MANY_PROGRESS_TASK = "E5002";
	
	/**
	 * 프로세스 초기화 실패 에러
	 */
	public static String PROCESS_INITIALIZE_ERROR = "E5003";
	
	/**
	 * 실행 해야될 프로세스가 로그인이 안 되어 있을 때 로그인이 필요한 경우 발행하는 에러
	 */
	public static String PROCESS_NEED_LOGIN = "E5004";
	
	/**
	 * 실행 해야될 프로세스의 파라메타가 정상적이지 않을 때 발생하는 에러
	 */
	public static String PROCESS_INVALID_PARAMETA = "E5005";
	
	/**
	 * 프로세스가 이미 실행되어 있을 때 발생하는 에러
	 */
	public static String PROCESS_ALREADY_RUNNING = "E5006";
	
	//DEVICE
	/**
	 * 사용가능한 디바이스 중 디바이스가 중복으로 페어링 되어 있을 때 발생하는 에러
	 */
	public static String DEVICES_DUPLICATE = "E6000";
	
	public static String DEVICES_SERVICE_NOT_BIND = "E6001";
	
	// SERVER
	/**
	 * 서버 요청 성공
	 */
	public static String SERVER_SUCCESS = "200";//	요청 성공
	
	/**
	 * 토큰 값이 만료 되었을 때
	 */
	public static String SERVER_AUTH_EXPIRED = "300";//	만료된 토큰
	
	/**
	 * 서버 인증 실패 
	 */
	public static String SERVER_AUTH_FAIL = "301";//	인증 실패	
	
	/**
	 * 요청 권한이 없음
	 */
	public static String SERVER_NO_PERMISSION= "302";//	권한 없음	
	
	/**
	 * 요청 실패
	 */
	public static String SERVER_REQUEST_FAILED= "400";//	요청 실패	
	
	/**
	 * 잘못된 입력 값
	 */
	public static String SERVER_INPUT_PARAM_ERROR = "401";//	잘못된 입력값	
	
	/**
	 * 시스템 에러
	 */
	public static String SERVER_SYSTEM_ERROR = "410";//	시스템 에러	
	
	/**
	 * 정보가 이미 가입되어 있을 때
	 */
	public static String SERVER_ALREADY_SUBSCRIPTION = "500";//	이미가입	
	
	/**
	 * 가입되어 있지 않을 때
	 */
	public static String SERVER_NON_PARTICIPATION= "501";//	미가입	
	
	/**
	 * 알수 없는 에러
	 */
	public static String UNKNOWN_EXCEPTION = "9999";
}
