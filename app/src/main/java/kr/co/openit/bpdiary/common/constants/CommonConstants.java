package kr.co.openit.bpdiary.common.constants;

/**
 * Created by srpark on 2016-12-12.
 */

public class CommonConstants {

    /**
     * Randing 화면 갯수
     */
    public static final int RANDING_COUNT = 3;

    public static final String EVENT_DATA = "EventData";

    /**
     * RequestCode 정리
     */
    public class RequestCode {

        /**
         * 권한 Request
         */
        public static final int ACTIVITY_REQUEST_CODE_PERMISSIONS = 0;

        /**
         * 로그인 타입 (구글)
         */
        public static final int ACTIVITY_REQUEST_CODE_LOGIN_TYPE_GOOGLE = 1;

        /**
         * 로그인 타입 (페이스북)
         */
        public static final int ACTIVITY_REQUEST_CODE_LOGIN_TYPE_FACEBOOK = 2;

        /**
         * 로그인 타입 (이메일)
         */
        public static final int ACTIVITY_REQUEST_CODE_LOGIN_TYPE_EMAIL = 3;

        /**
         * 로그인 타입 (자동)
         */
        public static final int ACTIVITY_REQUEST_CODE_LOGIN_TYPE_AUTO = 4;

        /**
         * 회원가입에서 프로필 request
         */
        public static final int ACTIVITY_REQUEST_CODE_SIGN_UP = 5;

        /**
         * 레포트 메뉴 request
         */
        public static final int ACTIVITY_REQUEST_REPORT_MENU = 6;

        /**
         * 레포트 변화량에서 혈압 입력
         */
        public static final int ACTIVITY_REQUEST_REPORT_BP_INPUT = 7;

        /**
         * 레포트 변화량에서 체중 입력
         */
        public static final int ACTIVITY_REQUEST_REPORT_WS_INPUT = 8;


        /**
         * 레포트 변화량에서 혈당 입력
         */
        public static final int ACTIVITY_REQUEST_REPORT_GLUCOSE_INPUT = 9;
    }
}
