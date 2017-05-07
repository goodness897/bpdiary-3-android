package kr.co.openit.bpdiary.common.constants;

import android.os.Environment;

import java.io.File;

/**
 * BloodPressure Note 상수 정의
 */
public final class ManagerConstants {

    /**
     * 앱 설정
     */
    public final static class AppConfig {

        //상용 서버 버전 체크 URL
        //        public static final String SERVER_URL = "https://api.bpdiary.net";

        //오픈잇 새로운 상용 서버 버전 체크 URL
        public static final String SERVER_URL = "https://api.bpdiary.net";

        //테스트 서버 버전 체크 URL
//        public static final String SERVER_URL = "https://dev.bpdiary.net";

        //GCM senderId
        public static final String GCM_SENDER_ID = "946890761011";

        //기본 저장 위치
        public static final String STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator
                                                  + "bpdiary"
                                                  + File.separator;

        //현재 App Store Name
        public static final String MARTKET_CD = AppStoreName.APP_STORE_GOOGLE_PLAY;

        //현재 Mobile OS
        public static final String MOBILE_OS = MobileOs.MOBILE_OS_ANDROID;
    }

    /**
     * RESTfulURI 클래스
     */
    public static final class RESTfulURI {

        //APP 버전 확인
        public static final String APP_VERSION_CHECK = "/app/version/check";

        //로그인
        public static final String LOGIN = "/app/customer/login";

        //공지사항
        public static final String NOTICE_LIST = "/app/news";

        //서비스 가입
        public static final String JOIN = "/app/customer/join";

        //서비스 해지
        public static final String LEAVE = "/app/leave";

        //프로필 갱신
        public static final String PROFILE_UPDATE = "/app/customer/profile";

        //인앱 결제
        public static final String PAY = "/app/pay";

        //가입 여부
        public static final String CHECK_JOIN = "/app/customer/checkJoin";

        //비밀번호 찾기
        public static final String FIND_PASSWORD = "/app/customer/findPassword";

        //비밀번호 변경
        public static final String CHANGE_PASSWORD = "/app/customer/changePassword";

        //혈압 전송
        public static final String BP_SEND = "/app/bp/record";

        //체중 전송
        public static final String WS_SEND = "/app/weight/record";

        //혈당 전송
        public static final String GLUCOSE_SEND = "/app/bg/record";

        //혈압 수정
        public static final String BP_UPDATE = "/app/bp/record/update";

        //체중 수정
        public static final String WS_UPDATE = "/app/weight/record/update";

        //혈당 수정
        public static final String GLUCOSE_UPDATE = "/app/bg/record/update";

        //혈압 삭제
        public static final String BP_REMOVE = "/app/bp/record/remove";

        //체중 삭제
        public static final String WS_REMOVE = "/app/weight/record/remove";

        //혈당 삭제
        public static final String GLUCOSE_REMOVE = "/app/bg/record/remove";

        //혈압 동기화 리스트 조회
        public static final String BP_SYNC = "/app/bp/record/sync";

        //체중 동기화 리스트 조회
        public static final String WS_SYNC = "/app/weight/record/sync";

        //혈당 동기화 리스트 조회
        public static final String GLUCOSE_SYNC = "/app/bg/record/sync";

        //목표 설정
        public static final String MOD_GOAL = "/app/customer/goal";

        //광고 조회
        public static final String ADS_EVENT = "/api/event/";

        //레포트 조회
        public static final String SERACH_REPORT = "/app/report";

        //레포트 공유
        public static final String SHARE_REPORT = "/app/report/email";

    }

    /**
     * HTTP HEADER 클래스
     */
    public static final class HTTPHeader {

        public static final String ACCEPT_LANGUAGE = "accept-language";

        public static final String ACCEPT_ENCODING = "accept-encoding";

        public static final String CONTENT_TYPE = "Content-Type";

    }

    /**
     * 메인 메뉴 코드
     */
    public final class MainMenuCode {

        //메인 메뉴 코드 키
        public static final String MAIN_MENU_CODE = "mainMenuCode";

        //혈압 메인
        public static final int MAIN_BP_MAIN = 0;

        //체중 메인
        public static final int MAIN_WS_MAIN = 1;

        //마켓
        public static final int MAIN_MARKET = 2;

        //불러오기
        public static final int MAIN_DATA_IMPORT = 3;

        //내보내기
        public static final int MAIN_DATA_EXPORT = 4;

        //공지사항
        public static final int MAIN_NOTICE = 5;

        //설정
        public static final int MAIN_DATA_SETTING = 6;

        //사용법
        public static final int MAIN_GUIDE = 7;

        //프로필 설정
        public static final int MAIN_PROFILE = 8;

        //기기 연결 설정
        public static final int MAIN_DEVICE_CONNECT = 9;

        //내 기기
        public static final int MAIN_MY_DEVICE = 10;

    }

    /**
     * Activity Result Code
     */
    public final class ActivityResultCode {

        //혈압 수동 입력
        public static final int BP_INPUT_MEASURE = 0;

        //혈압 메모 입력
        public static final int BP_INPUT_MEMO = 1;

        //혈압 그래프
        public static final int BP_VIEW_GRAPH = 2;

        //체중 수동 입력
        public static final int WS_INPUT_MEASURE = 3;

        //체중 메모 입력
        public static final int WS_INPUT_MEMO = 4;

        //체중 그래프
        public static final int WS_VIEW_GRAPH = 5;

        //내기기 셋업
        public static final int MY_DEVICE_SETUP = 6;

        //내 기기(혈압계, 체중계) 선택
        public static final int MY_DEVICE_SELECT = 7;

        //혈압계 연결
        public static final int MY_DEVICE_SEARCH = 8;

        //Google Fit
        public static final int GOOGLE_FIT_REQUEST_OAUTH = 9;

        //profile
        public static final int SETTING_PROFILE = 10;

        //LANGUAGE
        public static final int LANGUAGE = 11;

        //s Health
        public static final int SHEALTH_SETTING_DETAIL = 12;

        //혈당 수동 입력
        public static final int GLUCOSE_INPUT_MEASURE = 20;

        //혈당 메모 입력
        public static final int GLUCOSE_INPUT_MEMO = 21;

        //혈당 그래프
        public static final int GLUCOSE_VIEW_GRAPH = 22;

        //기타(61~70)
        //권한(메인 화면)
        public static final int PERMISSION_SETTING_MAIN = 65;

        //권한(서브 화면)
        public static final int PERMISSION_SETTING_SUB = 66;
    }

    /**
     * Permission RequestCode Permission RequestCode ActivityRequestCode와 겹치면 안된다.
     */
    public static final class PermissionRequestCode {

        public static final int PERMISSION_REQUEST_CHECK = 100;

        public static final int PERMISSION_REQUEST_CHECK_GOOGLE_PLUS = 101;

        public static final int PERMISSION_REQUEST_CHECK_MY_DEVICE_BP = 102;

        public static final int PERMISSION_REQUEST_CHECK_CSV_INPUT = 103;

        public static final int PERMISSION_REQUEST_CHECK_CSV_OUTPUT = 104;

    }

    /**
     * Intent Data 이동 스트링값
     */
    public final class IntentData {

        //URL
        public static final String URL = "url";

        //혈압계
        public static final String DEVICE = "device";

        public static final String IS_MY_DEVICE = "isMyDevice";

        //혈압계
        public static final String BLE = "ble";

        // 알람구분 플래그
        public static final String ALARM_FLAG = "alarmFlag";

        // 알림 시간
        public static final String ALARM_TIME = "time";

        // 알림 SEQ
        public static final String ALARM_SEQ = "seq";

        // 알림 HOUR
        public static final String ALARM_HOUR = "hour";

        // 알림 MINUTE
        public static final String ALARM_MINUTE = "minute";

    }

    /**
     * 로컬 데이터베이스 관련 정의
     */
    public final class DataBase {

        //HealthUp DB
        public static final String NAME = "bpdiary.db";

        //HealthUp DB version
        public static final int VERSION = 3;

        //Medical Device System Table 명
        public static final String TABLE_NAME_MDS_INFO = "BPD_MDS_INFO";

        //mac address
        public static final String COLUMN_NAME_MAC_ADDRESS = "MAC_ADDRESS";

        //device model
        public static final String COLUMN_NAME_MODEL = "MODEL";

        //device manufacture
        public static final String COLUMN_NAME_COMPANY = "COMPANY";

        //device system id
        public static final String COLUMN_NAME_SYSTEM_ID = "SYSTEM_ID";

        //config report id = dev config id
        public static final String COLUMN_NAME_CONFIG_REPORT_ID = "CONFIG_REPORT_ID";

        //health profile
        public static final String COLUMN_NAME_HEALTH_PROFILE = "HEALTH_PROFILE";

        //사용자 정보
        public static final String TABLE_NAME_USER_INFO = "BPD_USER_INFO";

        /**********************
         * 측정 데이터 테이블의 공통 컬럼 정의
         ***************************/

        //측정 데이터 테이블 : 디바이스 아이디
        public static final String COLUMN_NAME_DEVICE_ID = "DEVICE_ID";

        //측정 데이터 테이블 : 디바이스 모델
        public static final String COLUMN_NAME_DEVICE_MODEL = "DEVICE_MODEL";

        //측정 데이터 테이블 : 디바이스 제조사
        public static final String COLUMN_NAME_DEVICE_MANUFACTURER = "DEVICE_MANUFACTURER";

        //측정 데이터 테이블 : 측정시간
        public static final String COLUMN_NAME_MEASURE_DT = "MEASURE_DT";

        //측정 데이터 테이블 : 서버저장 YN
        public static final String COLUMN_NAME_SEND_TO_SERVER_YN = "SEND_YN";

        //측정 데이터 테이블 : 입력시간
        public static final String COLUMN_NAME_INS_DT = "INS_DT";

        //측정 데이터 테이블 : 메모
        public static final String COLUMN_NAME_MESSAGE = "MESSAGE";

        /*****************************
         * Blood Pressure
         *****************************/

        //혈압측정 테이블 이름
        public static final String TABLE_NAME_BP = "BPD_BP";

        //혈압측정 TEMP 테이블 이름
        public static final String TABLE_NAME_BP_TEMP = "BPD_BP_TEMP";

        //혈압측정 테이블 : 시퀀스
        public static final String COLUMN_NAME_BP_SEQ = "BP_SEQ";

        //혈압측정 테이블 : sys
        public static final String COLUMN_NAME_BP_SYS = "SYS";

        //혈압측정 테이블 : dia
        public static final String COLUMN_NAME_BP_DIA = "DIA";

        //혈압측정 테이블 : mean
        public static final String COLUMN_NAME_BP_MEAN = "MEAN";

        //혈압측정 테이블 : pulse
        public static final String COLUMN_NAME_BP_PULSE = "PULSE";

        //혈압측정 테이블 : state
        public static final String COLUMN_NAME_BP_TYPE = "TYPE";

        //혈압측정 테이블 : 왼팔, 오른팔(L, R)
        public static final String COLUMN_NAME_BP_ARM = "ARM";

        /**************************
         * Weighing Scale
         ***************************/

        //체중계 측정 테이블 이름
        public static final String TABLE_NAME_WS = "BPD_WS";

        //체중계 측정 TEMP 테이블 이름
        public static final String TABLE_NAME_WS_TEMP = "BPD_WS_TEMP";

        //체중계 측정 테이블 : 시퀀스
        public static final String COLUMN_NAME_WS_SEQ = "WS_SEQ";

        //체중계 측정 테이블 : WEIGHT
        public static final String COLUMN_NAME_WS_WEIGHT = "WEIGHT";

        //체중계 측정 테이블 : HEIGHT
        public static final String COLUMN_NAME_WS_HEIGHT = "HEIGHT";

        //체중계 측정 테이블 : BMI
        public static final String COLUMN_NAME_WS_BMI = "BMI";

        //체중계 측정 테이블 : BMI
        public static final String COLUMN_NAME_WS_BMI_TYPE = "TYPE";

        /**************************
         * glucose
         ***************************/

        //혈당 측정 테이블 이름
        public static final String TABLE_NAME_GLUCOSE = "BPD_GLUCOSE";

        //혈당 측정 TEMP 테이블 이름
        public static final String TABLE_NAME_GLUCOSE_TEMP = "BPD_GLUCOSES_TEMP";

        //혈당 측정 테이블 : GLUCOSE_SEQ
        public static final String COLUMN_NAME_GLUCOSE_SEQ = "GLUCOSE_SEQ";

        //혈당 측정 테이블 : GLUCOSE
        public static final String COLUMN_NAME_GLUCOSE = "GLUCOSE";

        //혈당 측정 테이블 : GLUCOSE
        public static final String COLUMN_NAME_GLUCOSE_BEFORE = "GLUCOSE_BEFORE";

        //혈당 측정 테이블 : GLUCOSE
        public static final String COLUMN_NAME_GLUCOSE_AFTER = "GLUCOSE_AFTER";

        //혈당 측정 테이블 : MEAL
        public static final String COLUMN_NAME_GLUCOSE_MEAL = "MEAL";

        //혈당 측정 테이블 : TYPE
        public static final String COLUMN_NAME_GLUCOSE_TYPE = "TYPE";

        /**************************
         * Medicine/Measure Alarm
         ***************************/

        // 복약/측정 알림 테이블 이름
        public static final String TABLE_NAME_MEDICINE_MEASURE_ALARM = "BPD_MEDICINE_MEASURE_ALARM";

        // 복약/측정 알림 TEMP 테이블 이름
        public static final String TABLE_NAME_MEDICINE_MEASURE_ALARM_TEMP = "BPD_MEDICINE_MEASURE_ALARM_TEMP";

        // 복약/측정 알림 테이블 : 알림 아이디
        public static final String COLUMN_NAME_MEDICINE_MEASURE_ALARM_SEQ = "SEQ";

        // 복약/측정 알림 테이블 : 복약/측정 구분 플래그
        public static final String COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE = "TYPE";

        // 복약/측정 알림 테이블 : 토글버튼 ON/OFF
        public static final String COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS = "STATUS";

        // 복약/측정 알림 테이블 : 알림 시간
        public static final String COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME = "NOTIFY_TIME";
    }

    /**
     * SharedPreferencesName 상수 정의
     */
    public final class SharedPreferencesName {

        public static final int PREF_VERSION = 1;

        //SharedPreference버전
        public static final String SHARED_VERSION = "version";

        //Config 파일 초기화용 버전 코드(앱 VersionCode이용)
        public static final String SHARED_PRE_VERSION_CODE = "preVersionCode";

        //ID
        public static final String SHARED_ID = "id";

        //email
        public static final String SHARED_EMAIL = "email";

        //성
        public static final String SHARED_LAST_NAME = "lastname";

        //이름
        public static final String SHARED_FIRST_NAME = "firstname";

        //성별
        public static final String SHARED_GENDER = "gender";

        //생년월일
        public static final String SHARED_BIRTH = "birth";

        //키
        public static final String SHARED_HEIGHT = "height";

        //단위
        public static final String SHARED_HEIGHT_UNIT = "heightUnit";

        public static final String SHARED_USING_BLOODGLUCOSE = "UsingBloodGlucose";

        //몸무게
        public static final String SHARED_WEIGHT = "weight";

        //몸무게 단위
        public static final String SHARED_WEIGHT_UNIT = "weightUnit";

        //몸무게 단위
        public static final String SHARED_GLUCOSE_UNIT = "glucoseUnit";

        //언어
        public static final String SHARED_LANGUAGE = "language";

        //사용자 최초 생성
        public static final String SHARED_USER_CREATE = "userCreate";

        //Auth Key
        public static final String SHARED_AUTH_KEY = "authKey";

        //Serial Number
        public static final String SHARED_SN = "serialNumber";

        //언어 변경 상태값
        public static final String SHARED_LANGUAGE_STATE = "languageSate";

        //약관 동의 여부
        public static final String SHARED_IS_TERMS = "isTerms";

        //블루투스 연동 기기 MAC 주소
        public static final String SHARED_PAIRED_MAC_ADDRESS = "pairedMacAddress";

        //블루투스 BP 연동 기기 이름
        public static final String SHARED_PAIRED_BP_DEIVCE_NAME = "pairedBPDeviceName";

        //블루투스 WS 연동 기기 이름
        public static final String SHARED_PAIRED_WS_DEIVCE_NAME = "pairedWSDeviceName";

        //통합센터 로그인 유무
        public static final String SHARED_IS_LOGIN = "isLogin";

        //GCM 토큰 값
        public static final String SHARED_GCM_TOKEN = "gcmToken";

        //Noti 수신
        public static final String SHARED_IS_NOTIFICATION = "isNoti";

        //Google Fit 연동
        public static final String SHARED_IS_GOOGLE_FIT = "isGoogleFit";

        //Google 피트니스 안내 SKIP 상태값 no Skip false, Skip true
        public static final String SHARED_IS_GOOGLE_FIT_SKP = "IS_GOOGLE_FIT_SKIP";

        //S Health 연동
        public static final String SHARED_IS_S_HEALTH = "isSHealth";

        //S Health BP 연동
        public static final String SHARED_IS_S_HEALTH_BP = "isSHealthBP";

        //S Health WS 연동
        public static final String SHARED_IS_S_HEALTH_WS = "isSHealthWS";

        //혈압계 유무 표시
        public static final String SHARED_BRING_BP = "bringBp";

        //결제 완료 구분
        public static final String SHARED_PAYMENT_CHECK = "paymentCheck";

        //결제 키
        public static final String SHARED_PAYMENT_SIGNATURE = "paymentSignature";

        //공지사항 최종 번호
        public static final String SHARED_NEW_BOARD_SEQ = "NewBoardSeq";

        //공지사항 현재 번호
        public static final String SHARED_CURRENT_BOARD_SEQ = "CurrentBoardSeq";

        //이벤트 광고 1
        public static final String EVENT_ADS_ONE_SEQ = "eventAdsOneSeq";

        //이벤트 광고 1
        public static final String EVENT_ADS_ONE_TIME = "eventAdsOneTime";

        //이벤트 광고 2
        public static final String EVENT_ADS_TWO_SEQ = "eventAdsTwoSeq";

        //이벤트 광고 2
        public static final String EVENT_ADS_TWO_TIME = "eventAdsTwoTime";

        //이벤트 광고 3
        public static final String EVENT_ADS_THREE_SEQ = "eventAdsThreeSeq";

        //이벤트 광고 3
        public static final String EVENT_ADS_THREE_TIME = "eventAdsThreeTime";

        //이벤트 광고 4
        public static final String EVENT_ADS_FOUR_SEQ = "eventAdsFourSeq";

        //이벤트 광고 4
        public static final String EVENT_ADS_FOUR_TIME = "eventAdsFourTime";

        //이벤트 광고 5
        public static final String EVENT_ADS_FIVE_SEQ = "eventAdsFiveSeq";

        //이벤트 광고 5
        public static final String EVENT_ADS_FIVE_TIME = "eventAdsFiveTime";

        //DB 버전 2 -> 3으로 데이터 마이그레이션 확인용
        public static final String SHARED_IS_PRE_MIGRATION = "isPreMigration";

        //DB 버전 2 -> 3으로 데이터 마이그레이션 확인용
        public static final String SHARED_IS_DB_MIGRATION = "isDBMigration5";

        // 복약/측정 알람 전체 플래그
        public static final String SHARED_ALARM_WHOLE_TOGGLE = "isOn";

        //혈압 수축기 최소값
        public static final String SHARED_BP_MIN_SYSTOLE = "bpMinSystole";

        //혈압 수축기 최대값
        public static final String SHARED_BP_MAX_SYSTOLE = "bpMaxSystole";

        //혈압 이완기 최소값
        public static final String SHARED_BP_MIN_DIASTOLE = "bpMinDiastole";

        //혈압 이완기 최대값
        public static final String SHARED_BP_MAX_DIASTOLE = "bpMaxDiastole";

        //식전(공복) 최소값
        public static final String SHARED_GLUCOSE_MIN_BEFORE_MEAL = "glucoseMinBeforeMeal";

        //식전(공복) 최대값
        public static final String SHARED_GLUCOSE_MAX_BEFORE_MEAL = "glucoseMaxBeforeMeal";

        //식후 최소값
        public static final String SHARED_GLUCOSE_MIN_AFTER_MEAL = "glucoseMinAfterMeal";

        //식후 최대값
        public static final String SHARED_GLUCOSE_MAX_AFTER_MEAL = "glucoseMaxAfterMeal";

    }

    /**
     * Language 정의
     */
    public final class Language {

        /**
         * 영문
         */
        public static final String ENG = "en";

        /**
         * 한글
         */
        public static final String KOR = "ko";

        /**
         * 스페인어
         */
        public static final String SPN = "es";

        /**
         * 일어
         */
        public static final String JPN = "ja";

        /**
         * 독일어
         */
        public static final String GER = "de";

        /**
         * 러시아어
         */
        public static final String RUS = "ru";

        /**
         * 프랑스어
         */
        public static final String FRN = "fr";

        /**
         * 체코어
         */
        public static final String CZE = "cs";

        /**
         * 포르투갈어
         */
        public static final String POR = "pt";

        /**
         * 이탈리아어
         */
        public static final String ITA = "it";

        /**
         * 인도어
         */
        public static final String IND = "hi";

        /**
         * 중국어 간체
         */
        public static final String TPE = "zh_TW";

        /**
         * 중국어 번체
         */
        public static final String CHN = "zh_CN";

    }

    /**
     * 성별 정의
     */
    public final class Gender {

        //남자
        public static final String MALE = "M";

        //여자
        public static final String FEMALE = "F";

    }

    /**
     * 광고 결제
     */
    public final class PaymentAdYN {

        //광고 제거 결제
        public static final String PAYMENT_AD_Y = "Y";

        //광고 제거 미결제
        public static final String PAYMENT_AD_N = "N";

    }

    /**
     * RequestParamName Key 정의
     */
    public final class RequestParamName {

        //신규
        //App 버전
        public static final String APP_VERSION = "version";

        //App 마켓
        public static final String APP_MARKET = "market";

        //UUID(email - encoding)
        public static final String UUID = "uuid";

        //email(decoding)
        public static final String EMAIL = "email";

        //PASSWORD
        public static final String PASSWORD = "password";

        //자동 로그인 여부
        public static final String AUTO_YN = "autoYn";

        //UUID(E mail)
        public static final String LOGIN_TYPE = "loginType";

        //Mobile OS
        public static final String MOBILE_OS = "mobileOS";

        //Mobile Token
        public static final String MOBILE_TOKEN = "mobileToken";

        //Notification
        public static final String NOTIFICATION = "noti";

        //Google Fit
        public static final String GOOGLE_FIT = "googleFit";

        //S 헬스
        public static final String S_HEALTH = "sHealth";

        //페어링 기기 이름
        public static final String BP_MONITOR_NAME = "bpmName";

        //단말 제조사명
        public static final String DEVICE_COMPANY = "deviceCompany";

        //이름
        public static final String FIRST_NAME = "firstName";

        //성
        public static final String LAST_NAME = "lastName";

        //성별
        public static final String GENDER = "gender";

        //생년원일
        public static final String DAY_OF_BIRTH = "dayOfBirth";

        //키
        public static final String HEIGHT = "height";

        //키 단위
        public static final String HEIGHT_UNIT = "heightUnit";

        //국가코드
        public static final String COUNTRY = "country";

        //Profile 이미지
        public static final String PROFILE = "profile";

        //입력 일자
        public static final String INS_DT = "insDt";

        //제조사명
        public static final String SENSOR_COMPANY = "sensorCompany";

        //모델명
        public static final String SENSOR_MODEL = "sensorModel";

        //통신사명
        public static final String CARRIER = "carrier";

        //app version
        public static final String APP_VERSION_CREATE = "appVer";

        //os version
        public static final String OS_VERSION = "osVer";

        //os version
        public static final String DEVICE_MODEL = "deviceModel";

        //일련번호
        public static final String SENSOR_SN = "sensorSn";

        //메시지(사용자 입력)
        public static final String MESSAGE = "message";

        //측정 일자
        public static final String RECORD_DT = "recordDt";

        //혈압 수축기
        public static final String BP_SYS = "sys";

        //혈압 이완기
        public static final String BP_DIA = "dia";

        //혈압 평균
        public static final String BP_MEAN = "mean";

        //혈압 맥박
        public static final String BP_PULSE = "pulse";

        //혈압 판단 기준
        public static final String BP_TYPE = "bpType";

        //혈압 왼팔 오른팔
        public static final String BP_ARM = "arm";

        //혈압 목표 sys Min
        public static final String BP_SYS_MIN = "sysMin";

        //혈압 목표 sys Max
        public static final String BP_SYS_MAX = "sysMax";

        //혈압 목표 dia Min
        public static final String BP_DIA_MIN = "diaMin";

        //혈압 목표 dia Max
        public static final String BP_DIA_MAX = "diaMax";

        //체중
        public static final String WS_WEIGHT = "weight";

        //체중 BMI
        public static final String WS_BMI = "bmi";

        //체중 BMI 판단기준
        public static final String WS_BMI_TYPE = "bmiType";

        //혈당
        public static final String GLUCOSE = "glucose";

        //혈당 식후 식전
        public static final String GLUCOSE_MEAL = "meal";

        //혈당 타입
        public static final String GLUCOSE_TYPE = "bgType";

        //혈당 목표 식전 Min
        public static final String MEAL_B_MIN = "mealBMin";

        //혈당 목표 식전 Max
        public static final String MEAL_B_MAX = "mealBMax";

        //혈당 목표 식후 Min
        public static final String MEAL_A_MIN = "mealAMin";

        //혈당 목표 식후 Max
        public static final String MEAL_A_MAX = "mealAMax";


        //인앱결제 여부 키
        public static final String PAY_TOKEN = "payToken";

        //Profile 이미지
        public static final String PROFILE_IMG_FILE = "imgFile";

        //레포트 페이지 번호
        public static final String REPORT_VIEW_NUM = "reportViewNum";

        //레포트 period
        public static final String PERIOD = "period";

        //레포트 종료일
        public static final String END_DT = "endDt";

        //레포트 자기관리순위 조회 여부
        public static final String DAYS_YN = "daysYn";

        //레포트 목표달성순위 조회 여부
        public static final String GRADE_YN = "gradeYn";

        //레포트 분포도 조회 여부
        public static final String DIST_YN = "distYn";

        //레포트 혈당 조회 여부(없을 시 서버에 저장된 값)
        public static final String BG_YN = "bgYn";

        //레포트 체중 단위 - lb
        public static final String WEIGHT_UNIT = "weightUnit";

        //레포트 혈당 단위 - mmol
        public static final String GLUCOSE_UNIT = "glucoseUnit";
    }

    /**
     * StoreName
     */
    public final class AppStoreName {

        //Google Play Store
        public static final String APP_STORE_GOOGLE_PLAY = "G";

    }

    /**
     * Mobile OS
     */
    public final class MobileOs {

        //Android
        public static final String MOBILE_OS_ANDROID = "A";

        //iOS
        public static final String MOBILE_OS_IOS = "I";

        //Window
        public static final String MOBILE_OS_WIN = "W";

    }

    /**
     * Notification YN
     */
    public final class NotificationYN {

        //NOTIFICATION_Y
        public static final String NOTIFICATION_Y = "Y";

        //NOTIFICATION_N
        public static final String NOTIFICATION_N = "N";

    }

    public final class UsingBloodGlucoseYN {

        //Using_BloodGlucose_Y
        public static final String USING_BLOODGLUCOSE_Y = "Y";

        //Using_BloodGlucose_N
        public static final String USING_BLOODGLUCOSE_N = "N";
    }

    /**
     * Google Fit YN
     */
    public final class GoogleFitYN {

        //GOOGLE_FIT_Y
        public static final String GOOGLE_FIT_Y = "Y";

        //GOOGLE_FIT_N
        public static final String GOOGLE_FIT_N = "N";

    }

    /**
     * S Heath YN
     */
    public final class SHeathYN {

        //S_HEALTH_Y
        public static final String S_HEALTH_Y = "Y";

        //S_HEALTH_N
        public static final String S_HEALTH_N = "N";

    }

    /**
     * 서버 연동 YN
     */
    public final class ServerSyncYN {

        //서버 연동 완료
        public static final String SERVER_SYNC_Y = "Y";

        //서버 연동 미완료
        public static final String SERVER_SYNC_N = "N";

    }

    /**
     * 복약/측정 플래그
     */
    public final class AlarmSyncFlag {

        // 복약 플래그
        public static final String MEDICINE_SYNC_Y = "MEDICINE";

        // 측정 플래그
        public static final String MEASURE_SYNC_Y = "MEASURE";

    }

    /**
     * 복약/측정 토글 ON/OFF
     */
    public final class AlarmToggleState {

        // 토글 ON
        public static final String ALARM_TOGGLE_ON = "ON";

        // 토글 OFF
        public static final String ALARM_TOGGLE_OFF = "OFF";

    }

    /**
     * ResponseParamName Key 정의
     */
    public final class ResponseParamName {

        //처리 결과
        public static final String RESULT = "result";

        //서버 메세지
        public static final String MESSAGE = "message";

        //서버 reason
        public static final String REASON = "reason";

        //로그인 타입
        public static final String LOGIN_TYPE = "loginType";

        //앱 버전 구분
        public static final String APP_VERSION_TYPE = "versionType";

        //최신 버전
        public static final String APP_LAST_VERSION = "lastVersion";

        //앱 마켓 URL
        public static final String APP_MARKET_URL = "marketUrl";

        //사용자 프로필 이미지
        public static final String PROFILE_URL = "profileUrl";

        //인앱결제 여부 키
        public static final String PAY_TOKEN = "payToken";

        //공지사항 최종 번호
        public static final String BOARD_SEQ = "boardSeq";

        //프로필 맵 데이터
        public static final String PROFILE = "profile";

        //이름
        public static final String FIRST_NAME = "firstName";

        //성
        public static final String LAST_NAME = "lastName";

        //성별
        public static final String GENDER = "gender";

        //생년원일
        public static final String DAY_OF_BIRTH = "dayOfBirth";

        //키
        public static final String HEIGHT = "height";

        //키 단위
        public static final String HEIGHT_UNIT = "heightUnit";

        //혈당 여부
        public static final String BG_YN = "bgYn";

        //목표
        public static final String GOAL = "goal";

        //혈압 목표 sys Min
        public static final String BP_SYS_MIN = "sysMin";

        //혈압 목표 sys Max
        public static final String BP_SYS_MAX = "sysMax";

        //혈압 목표 dia Min
        public static final String BP_DIA_MIN = "diaMin";

        //혈압 목표 dia Max
        public static final String BP_DIA_MAX = "diaMax";

        //체중
        public static final String WEIGHT = "weight";

        //식전 목표 Min
        public static final String BG_MEAL_B_MIN = "mealBMin";

        //식전 목표 Max
        public static final String BG_MEAL_B_MAX = "mealBMax";

        //식후 목표 Min
        public static final String BG_MEAL_A_MIN = "mealAMin";

        //식후 목표 Max
        public static final String BG_MEAL_A_MAX = "mealAMax";

        //이벤트 팝업
        public static final String EVENT = "event";

        //약관 변경에 따른 동의 메시지
        public static final String AGREEMENT = "agreement";

        //공지사항 제목
        public static final String BOARD_TITLE = "TITLE";

        //공지사항 내용
        public static final String BOARD_CONTENT = "CONTENT";

        //공지사항 일자
        public static final String BOARD_START_DT = "START_DT";

        //공지사항 목록
        public static final String BOARD_LIST = "boardList";

        //혈압 데이터 동기화 데이터 목록
        public static final String BP_LIST = "bpList";

        //혈압 수축기
        public static final String BP_SYS = "SYS";

        //혈압 이완기
        public static final String BP_DIA = "DIA";

        //혈압 평균
        public static final String BP_MEAN = "MEAN";

        //혈압 맥박
        public static final String BP_PULSE = "PULSE";

        //혈압 판단 기준
        public static final String BP_TYPE = "BP_TYPE";

        //메시지(사용자 입력)
        public static final String BP_MESSAGE = "MESSAGE";

        //체중 데이터 동기화 데이터 목록
        public static final String WS_LIST = "weightList";

        //체중
        public static final String WS_WEIGHT = "WEIGHT";

        //체중 키
        public static final String WS_HEIGHT = "HEIGHT";

        //체중 BMI
        public static final String WS_BMI = "BMI";

        //체중 BMI 판단기준
        public static final String WS_BMI_TYPE = "BMI_TYPE";

        //메시지(사용자 입력)
        public static final String WS_MESSAGE = "MESSAGE";

        //혈당 데이터 동기화 데이터 목록
        public static final String GLUCOSE_LIST = "bgList";

        //혈당
        public static final String GLUCOSE = "GLUCOSE";

        //혈당
        public static final String GLUCOSE_MEAL = "MEAL";

        //혈당Type
        public static final String GLUCOSE_TYPE = "BG_TYPE";

        //메시지(사용자 입력)
        public static final String GLUCOSE_MESSAGE = "MESSAGE";

        //제조사명
        public static final String SENSOR_COMPANY = "SENSOR_COMPANY";

        //모델명
        public static final String SENSOR_MODEL = "SENSOR_MODEL";

        //일련번호
        public static final String SENSOR_SN = "SENSOR_SN";

        //측정 일자
        public static final String RECORD_DT = "RECORD_DT";

        //입력 일자
        public static final String INS_DT = "INS_DT";

        //시작일
        public static final String START_DT = "startDt";

        //종료료일
        public static final String END_DT = "endDt";

        //혈압 레포트 변화량
        public static final String P_VAR = "pVar";

        //변화량 리스트
        public static final String VAR_LIST = "varList";

        //목표 리스트
        public static final String GOAL_LIST = "goalList";

        public static final String SEQ = "SEQ";

        public static final String SYS = "SYS";

        public static final String DIA = "DIA";

        //체중 변화량 목록
        public static final String W_VAR = "wVar";

        //식전 혈당 변화량 목록
        public static final String GB_VAR = "gbVar";

        //식후 혈당 변화량 목록
        public static final String GA_VAR = "gaVar";

        //식전혈당 변화량 목록
        public static final String GB_VAR_LIST = "gbVarList";

        //식후혈당 변화량 목록
        public static final String GA_VAR_LIST = "gaVarList";

        //혈압 평균값 목록
        public static final String P_AVG_LIST = "pAvgList";

        //체중 평균값 목록
        public static final String W_AVG_LIST = "wAvgList";

        //혈당 평균값 목록
        public static final String G_AVG_LIST = "gAvgList";

        //혈당 식전 평균값
        public static final String B_GLUCOSE = "B_GLUCOSE";

        //혈당 식후 평균값
        public static final String A_GLUCOSE = "A_GLUCOSE";
    }

    /**
     * JSON 결과 코드 정의
     */
    public final class ResponseResult {

        //성공
        public static final String RESULT_TRUE = "true";

        //실패
        public static final String RESULT_FALSE = "false";

        //실패
        public static final String RESULT_MSG_ALREADY_JOIN = "already joined";

        /**
         * 필수값 없음
         */
        public static final String RESULT_FAIL_REASON_I = "I";

        /**
         * 서버에러
         */
        public static final String RESULT_FAIL_REASON_E = "E";

        /**
         * 계정없음
         */
        public static final String RESULT_FAIL_REASON_N = "N";

        /**
         * 잘못된 비밀번호
         */
        public static final String RESULT_FAIL_REASON_P = "P";

        /**
         * 잘못된 로그인 구분
         */
        public static final String RESULT_FAIL_REASON_L = "L";

        /**
         * 가입된 계정
         */
        public static final String RESULT_FAIL_REASON_J = "J";
    }

    //로그인 타입
    public final class LoginType {

        public static final String LOGIN_TYPE_GOOGLE = "G";

        public static final String LOGIN_TYPE_FACEBOOK = "F";

        public static final String LOGIN_TYPE_EMAIL = "E";
    }

    /**
     * Update Version Code 정의
     */
    public final class AppUpdateVersionType {

        /**
         * 최신버전
         */
        public static final String VERSION_TYPE_N = "N";

        //선택 다운로드
        public static final String VERSION_TYPE_O = "O";

        //필수 다운로드
        public static final String VERSION_TYPE_M = "M";

    }

    //혈압
    public final class ArmType {

        public static final String BP_ARM_LEFT = "L";

        public static final String BP_ARM_RIGHT = "R";

        public static final String BP_ARM_NOTHING = "";
    }

    //혈당 식전 식후
    public final class EatType {

        public static final String GLUCOSE_BEFORE = "B";

        public static final String GLUCOSE_AFTER = "A";

        public static final String GLUCOSE_ALL = "ALL";
    }

    /**
     * 측정기기 타입 정의
     */
    public final class DeviceType {

        //혈압계
        public static final String BLOOD_PRESSURE = "BP";

        //체중계
        public static final String WEIGHING_SCALE = "WS";

        //혈당
        public static final String GLUCOSE_BEFORE = "GLUCOSE_BEFORE";

        //혈당
        public static final String GLUCOSE_AFTER = "GLUCOSE_AFTER";

    }

    /**
     * 혈압계 정의
     */
    public final class BloodPressureDevice {

        //A&D 혈압계
        public static final String AnD_BP_UA_651_BLE = "A&D_UA-651BLE";

        //A&D 혈압계
        public static final String AnD_BP_UA_767PBT_C = "A&D UA-767PBT-C";

        //A&D 혈압계
        public static final String AnD_UA_851PBT_C = "A&D UA-851PBT-C";

        //Pora 혈압계
        public static final String PORA_D40B = "Foracare Fora D40b";

        public static final String HEM_7081_IT = "OMRON HEM-708-IT";

    }

    /**
     * 혈압계 정의
     */
    public final class WeightScale {

        //샤오미 체중계
        public static final String MI_SCALE = "MI_SCALE";

    }

    /**
     * 정규식
     */
    public final class ManagerPattern {

        //측정 데이터 값 Pattern 정의
        public static final String MEASURE_DATA_PATTERN = "[0-9]+\\.[0]+$";

        //측정 데이터 값 Pattern 정의 (체중계 - 소수점 이하 자릿수가 많음)
        public static final String MEASURE_DATA_PATTERN_SCALE = "[0-9]+(\\.[0-9]){0,1}$";

        public static final int PADDING_TOTAL_LENGTH = 12;

        //IP Address 정규식
        public static final String IP_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                                                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                                                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                                                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])"
                                                + "([\\:]?(\\d{3,5})|())$";

        //URL Address 정규식
        public static final String URL_PATTERN = "^(([a-z0-9]+)\\.|(www)\\.)" + "[a-z0-9]+"
                                                 + "(\\.[a-z0-9-]+)+"
                                                 + "([\\:]?(\\d{3,5})|())$";

        //Email Address 정규식
        public static final String EMAIL_PATTERN = "^([0-9a-zA-Z_-]+)@" + "([0-9a-zA-Z_-]+)(\\.[0-9a-zA-Z_-]+){1,2}$";

    }

    /**
     * Graph 관련 정의
     */
    public final class Graph {

        //index
        public static final String INDEX = "seq";

        //측정기 제조사명
        public static final String DEVICE_MANUFACTURER = "devicemanufacturer";

        //측정기 모델명
        public static final String DEVICE_MODEL = "devicemodel";

        //측정일
        public static final String DATE = "measuredt";

        //최고 혈압
        public static final String SYS = "sys";

        //최저 혈압
        public static final String DIA = "dia";

        //평균 혈압
        public static final String MEAN = "mean";

        //맥박
        public static final String PULSE = "pulse";

        //체중
        public static final String WEIGHT = "weight";

        //bmi
        public static final String BMI = "bmi";

    }

    /**
     * DB 조회 기간별 타입 정의
     */
    public final class PeriodType {

        //기간 하루 정의
        public static final int PERIOD_DAY = 0;

        //기간 일주일 정의
        public static final int PERIOD_WEEK = 1;

        //기간 한달 정의
        public static final int PERIOD_MONTH = 2;

        //기간 일년 정의
        public static final int PERIOD_YEAR = 3;

        //기간 전체
        public static final int PERIOD_ALL = 4;

    }

    /**
     * 결과 화면 그래프 리스트 타입 정의
     */
    public final class ResultViewType {

        //그래프 결과 화면 정의
        public static final int RESULT_VIEW_GRAPH = 0;

        //리스트 결과 화면 정의
        public static final int RESULT_VIEW_LIST = 1;

    }

    /**
     * 에니메이션 시간 정의
     */
    public final class AnimationTime {

        //delay time 정의
        public static final int DELAY_TIME = 2300;

        //duration time 정의
        public static final int DURATION_TIME = 1000;

    }

    public final class HaveBP {

        //혈압계 설정 페이지 안들어감
        public static final int DEFAULT = 0;

        //가지고 잇음
        public static final int HAVE = 1;

        //안가지고 있음
        public static final int DONT_HAVE = 2;

        //선택 메시지 보고옴
        public static final int SETUP = 3;
    }

    public final class Unit {

        public static final String MGDL = "mg/dL";

        public static final String MMOL = "mmol/L";

        public static final String KG = "kg";

        public static final String LBS = "lbs";

        public static final String CM = "cm";

        public static final String FT_IN = "ft.in";
        

    }

    public final class ReportType {

        //평균
        public static final String AVG = "avg";

        //변화량
        public static final String VAL = "val";
    }

    public final class PageType {

        //혈압
        public static final String TYPE_BP = "bp";

        //혈당
        public static final String TYPE_GLUCOSE = "glucose";

        //체중
        public static final String TYPE_WS = "ws";

    }
}
