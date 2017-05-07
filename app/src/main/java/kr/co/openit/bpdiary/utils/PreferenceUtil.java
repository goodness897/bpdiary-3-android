package kr.co.openit.bpdiary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.Gender;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.HaveBP;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.Language;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.SharedPreferencesName;

public class PreferenceUtil {

    private static SharedPreferences sharedPref = null;

    private static SharedPreferences.Editor sharedPrefEditor = null;

    public static void getPreferenceEditor(Context context) {

        if (sharedPref == null) {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        }

        if (sharedPrefEditor == null) {
            sharedPrefEditor = sharedPref.edit();
        }
    }

    /**
     * 초기화
     */
    public static void removeAllPreferences(Context context) {
        String strLanguage = getLanguage(context);
        getPreferenceEditor(context);
        sharedPrefEditor.clear();
        sharedPrefEditor.commit();
        setLanguage(context, strLanguage);
    }

    /**
     * Preference 변경용 버전
     *
     * @return 인증키
     */
    public static int getPrefVersion(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(ManagerConstants.SharedPreferencesName.SHARED_VERSION, 0);
    }

    /**
     * Preference 변경용 버전
     */
    public static void setPrefVersion(Context context, int version) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(ManagerConstants.SharedPreferencesName.SHARED_VERSION, version);
        sharedPrefEditor.commit();
    }

    /**
     * 인증키 반환
     *
     * @return 인증키
     */
    public static String getAuthKey(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_AUTH_KEY, "");
    }

    /**
     * 인증키 저장
     */
    public static void setAuthKey(Context context, String key) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_AUTH_KEY, key);
        sharedPrefEditor.commit();
    }

    /**
     * Payment Check 저장
     */
    public static void setPaymentCheck(Context context, String check) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_PAYMENT_CHECK, check);
        sharedPrefEditor.commit();
    }

    /**
     * Payment Check 가져오기
     */
    public static String getPaymentCheck(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_PAYMENT_CHECK, "");
    }

    /**
     * Payment Check 저장
     */
    public static void setPaymentSign(Context context, String sign) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_PAYMENT_SIGNATURE, sign);
        sharedPrefEditor.commit();
    }

    /**
     * Payment Check 가져오기
     */
    public static String getPaymentSign(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_PAYMENT_SIGNATURE, "");
    }

    /**
     * Gcm토큰 값
     */
    public static void setGcmToken(Context context, String token) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_GCM_TOKEN, token);
        sharedPrefEditor.commit();
    }

    /**
     * Gcm토큰 값
     */
    public static String getGcmToken(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_GCM_TOKEN, "");
    }

    /**
     * 공지사항 최종 번호
     */
    public static void setNewNoticeNumber(Context context, String strBoardSeq) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_NEW_BOARD_SEQ, strBoardSeq);
        sharedPrefEditor.commit();
    }

    /**
     * 공지사항 최종 번호
     */
    public static String getNewNoticeNumber(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_NEW_BOARD_SEQ, "");
    }

    /**
     * 공지사항 현재 번호
     */
    public static void setCurrentNoticeNumber(Context context, String strBoardSeq) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_CURRENT_BOARD_SEQ, strBoardSeq);
        sharedPrefEditor.commit();
    }

    /**
     * 공지사항 현재 번호
     */
    public static String getCurrentNoticeNumber(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_CURRENT_BOARD_SEQ, "");
    }

    /**
     * 이벤트 광고 1
     */
    public static void setEventAdsOne(Context context, String strSeq, String strTime) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_ONE_SEQ, strSeq);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_ONE_TIME, strTime);
        sharedPrefEditor.commit();
    }

    /**
     * 이벤트 광고 1
     */
    public static String getEventAdsOneSeq(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_ONE_SEQ, "");
    }

    /**
     * 이벤트 광고 1
     */
    public static String getEventAdsOneTime(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_ONE_TIME, "");
    }

    /**
     * /** 이벤트 광고 2
     */
    public static void setEventAdsTwo(Context context, String strSeq, String strTime) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_TWO_SEQ, strSeq);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_TWO_TIME, strTime);
        sharedPrefEditor.commit();
    }

    /**
     * 이벤트 광고 2
     */
    public static String getEventAdsTwoSeq(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_TWO_SEQ, "");
    }

    /**
     * 이벤트 광고 2
     */
    public static String getEventAdsTwoTime(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_TWO_TIME, "");
    }

    /**
     * /** 이벤트 광고 3
     */
    public static void setEventAdsThree(Context context, String strSeq, String strTime) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_THREE_SEQ, strSeq);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_THREE_TIME, strTime);
        sharedPrefEditor.commit();
    }

    /**
     * 이벤트 광고 3
     */
    public static String getEventAdsThreeSeq(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_THREE_SEQ, "");
    }

    /**
     * 이벤트 광고 3
     */
    public static String getEventAdsThreeTime(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_THREE_TIME, "");
    }

    /**
     * /** 이벤트 광고 4
     */
    public static void setEventAdsFour(Context context, String strSeq, String strTime) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_FOUR_SEQ, strSeq);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_FOUR_TIME, strTime);
        sharedPrefEditor.commit();
    }

    /**
     * 이벤트 광고 4
     */
    public static String getEventAdsFourSeq(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_FOUR_SEQ, "");
    }

    /**
     * 이벤트 광고 4
     */
    public static String getEventAdsFourTime(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_FOUR_TIME, "");
    }

    /**
     * /** 이벤트 광고 5
     */
    public static void setEventAdsFive(Context context, String strSeq, String strTime) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_FIVE_SEQ, strSeq);
        sharedPrefEditor.putString(SharedPreferencesName.EVENT_ADS_FIVE_TIME, strTime);
        sharedPrefEditor.commit();
    }

    /**
     * 이벤트 광고 5
     */
    public static String getEventAdsFiveSeq(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_FIVE_SEQ, "");
    }

    /**
     * 이벤트 광고 5
     */
    public static String getEventAdsFiveTime(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.EVENT_ADS_FIVE_TIME, "");
    }

    /**
     * 알림 상태
     */
    public static boolean getNotification(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_IS_NOTIFICATION, true);
    }

    /**
     * 알림 저장 상태
     */
    public static void setNotification(Context context, boolean isNoti) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_IS_NOTIFICATION, isNoti);
        sharedPrefEditor.commit();
    }

    /**
     * 구글 피트니스 상태
     */
    public static boolean getGoogleFit(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_IS_GOOGLE_FIT, false);
    }

    /**
     * 구글 피트니스 상태
     */
    public static void setGoogleFit(Context context, boolean isGooglefit) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_IS_GOOGLE_FIT, isGooglefit);
        sharedPrefEditor.commit();
    }

    /**
     * 구글 피트니스 SKIP 상태
     */
    public static void setIsGoogleFitSkip(Context context, boolean flag) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_IS_GOOGLE_FIT_SKP, flag);
        sharedPrefEditor.commit();
    }

    /**
     * 구글 피트니스 SKIP 상태
     */
    public static boolean getIsGoogleFitSkip(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_IS_GOOGLE_FIT_SKP, false);
    }

    /**
     * S 헬스 상태
     */
    public static boolean getSHealth(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_IS_S_HEALTH, false);
    }

    /**
     * S 헬스 상태
     */
    public static void setSHealth(Context context, boolean isSHealth) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_IS_S_HEALTH, isSHealth);
        sharedPrefEditor.commit();
    }

    /**
     * S 헬스 BP 상태
     */
    public static boolean getSHealthBP(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_IS_S_HEALTH_BP, false);
    }

    /**
     * S 헬스 BP 상태
     */
    public static void setSHealthBP(Context context, boolean isSHealthBP) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_IS_S_HEALTH_BP, isSHealthBP);
        sharedPrefEditor.commit();
    }

    /**
     * S 헬스 WS 상태
     */
    public static boolean getSHealthWS(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_IS_S_HEALTH_WS, false);
    }

    /**
     * S 헬스 WS 상태
     */
    public static void setSHealthWS(Context context, boolean isSHealthWS) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_IS_S_HEALTH_WS, isSHealthWS);
        sharedPrefEditor.commit();
    }

    /**
     * 페어링 기기 맥 주소 값
     */
    public static void setMacAddr(Context context, String strMacAddr) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_PAIRED_MAC_ADDRESS, strMacAddr);
        sharedPrefEditor.commit();
    }

    /**
     * 페어링 기기 맥 주소 값
     */
    public static String getMacAddr(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_PAIRED_MAC_ADDRESS, "");
    }

    /**
     * 혈압계 페어링 기기 이름
     */
    public static void setBPDeviceName(Context context, String strDeviceName) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_PAIRED_BP_DEIVCE_NAME, strDeviceName);
        sharedPrefEditor.commit();

    }

    /**
     * 혈압계 페어링 기기 이름
     */
    public static String getBPDeviceName(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_PAIRED_BP_DEIVCE_NAME, "");
    }

    /**
     * 체중계 페어링 기기 이름
     */
    public static void setWSDeviceName(Context context, String strDeviceName) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_PAIRED_WS_DEIVCE_NAME, strDeviceName);
        sharedPrefEditor.commit();

    }

    /**
     * 체중계 페어링 기기 이름
     */
    public static String getWSDeviceName(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_PAIRED_WS_DEIVCE_NAME, "");
    }

    /**
     * 로그인 상태 설정
     *
     * @param context
     * @return
     */
    public static boolean getIsLogin(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_USER_CREATE, false);
    }

    /**
     * 로그인 상태 체크
     *
     * @param context
     * @param isLogin
     */
    public static void setIsLogin(Context context, boolean isLogin) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_USER_CREATE, isLogin);
        sharedPrefEditor.commit();
    }

    /**
     * 로그인 타입 설정하기
     */
    public static void setLoginType(Context context, String loginType) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(ManagerConstants.RequestParamName.LOGIN_TYPE, loginType);
        sharedPrefEditor.commit();
    }

    public static String getLoginType(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(ManagerConstants.RequestParamName.LOGIN_TYPE, "G");
    }

    /**
     * 이메일 암호화 가져오기
     */
    public static String getEncEmail(Context context) {
        getPreferenceEditor(context);

        String strEmail = "";
        strEmail = sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_EMAIL, "");

        return strEmail;
    }

    /**
     * 이메일 암호화 저장하기
     */
    public static void setEncEmail(Context context, String strEmail) {
        getPreferenceEditor(context);

        String strEncEmail = "";

        try {
            strEncEmail = AesssUtil.encrypt(strEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_EMAIL, strEncEmail);
        sharedPrefEditor.commit();

    }

    /**
     * 비밀번호 암호화 가져오기
     */
    public static String getEncPassword(Context context) {
        getPreferenceEditor(context);

        String strEmail = "";
        strEmail = sharedPref.getString(ManagerConstants.RequestParamName.PASSWORD, "");

        return strEmail;
    }

    /**
     * 비밀번호 암호화 저장하기
     */
    public static void setEncPassword(Context context, String password) {
        getPreferenceEditor(context);

        String strEncPassword = "";

        try {
            strEncPassword = AesssUtil.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPrefEditor.putString(ManagerConstants.RequestParamName.PASSWORD, strEncPassword);
        sharedPrefEditor.commit();

    }

    /**
     * 이메일 복호화 가져오기
     */
    public static String getDecEmail(Context context) {
        getPreferenceEditor(context);

        String strDecEmail = "";

        try {
            strDecEmail = AesssUtil.decrypt(sharedPref.getString(ManagerConstants.SharedPreferencesName.SHARED_EMAIL,
                                                                 ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strDecEmail;
    }

    /**
     * 광고 결제
     */
    public static boolean getIsPayment(Context context) {
        getPreferenceEditor(context);

        boolean isPayment = false;

        String strPaymentSignature = sharedPref.getString(SharedPreferencesName.SHARED_PAYMENT_SIGNATURE, "");
        String strPaymentCheck = sharedPref.getString(SharedPreferencesName.SHARED_PAYMENT_CHECK, "");

        if ((null == strPaymentSignature || "".equals(strPaymentSignature) || "null".equals(strPaymentSignature))
            && (null == strPaymentCheck || "".equals(strPaymentCheck)
                || "null".equals(strPaymentCheck)
                || "N".equals(strPaymentCheck))) {
            isPayment = false;

        } else {
            isPayment = true;
        }

        return isPayment;

    }

    /**
     * 광고 결제
     */
    public static void setIsPayment(Context context, String strSignature, String strCheck) {
        getPreferenceEditor(context);

        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_PAYMENT_SIGNATURE, strSignature);
        sharedPrefEditor.putString(ManagerConstants.SharedPreferencesName.SHARED_PAYMENT_CHECK, strCheck);
        sharedPrefEditor.commit();

    }

    /**
     * 환자 이름 암호화 반환 (first name)
     *
     * @return 환자의 first name
     */
    public static String getEncFirstName(Context context) {
        getPreferenceEditor(context);

        String strEncfName = "";

        try {
            strEncfName = sharedPref.getString(SharedPreferencesName.SHARED_FIRST_NAME, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strEncfName;
    }

    /**
     * 환자 이름 암호화 저장(first anem)
     */
    public static void setEncFirstName(Context context, String name) {
        getPreferenceEditor(context);

        String strEncfName = "";
        try {
            strEncfName = AesssUtil.encrypt(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sharedPrefEditor.putString(SharedPreferencesName.SHARED_FIRST_NAME, strEncfName);
        sharedPrefEditor.commit();
    }

    /**
     * 환자 이름 복호화 반환 (first name)
     *
     * @return 환자의 first name
     */
    public static String getDecFirstName(Context context) {
        getPreferenceEditor(context);

        String strDecfName = "";

        try {
            strDecfName = AesssUtil.decrypt(sharedPref.getString(SharedPreferencesName.SHARED_FIRST_NAME, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strDecfName;
    }

    /**
     * 환자 이름 암호화 반환 (last name)
     *
     * @return 환자의 last name
     */
    public static String getEncLastName(Context context) {
        getPreferenceEditor(context);

        String strEnclName = "";

        try {
            strEnclName = sharedPref.getString(SharedPreferencesName.SHARED_LAST_NAME, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strEnclName;
    }

    /**
     * 환자 이름 세팅(first name)
     */
    public static void setEncLastName(Context context, String name) {
        getPreferenceEditor(context);
        String strEnclName = "";
        try {
            strEnclName = AesssUtil.encrypt(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sharedPrefEditor.putString(SharedPreferencesName.SHARED_LAST_NAME, strEnclName);
        sharedPrefEditor.commit();
    }

    /**
     * 환자 이름 반환 (last name)
     *
     * @return 환자의 last name
     */
    public static String getDecLastName(Context context) {
        getPreferenceEditor(context);

        String strDeclName = "";

        try {
            strDeclName = AesssUtil.decrypt(sharedPref.getString(SharedPreferencesName.SHARED_LAST_NAME, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strDeclName;
    }

    /**
     * 키 반환
     *
     * @return 환자 키
     */
    public static String getHeight(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.SHARED_HEIGHT, "170");
    }

    /**
     * 키 셋팅
     */
    public static void setHeight(Context context, String strHeight) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.SHARED_HEIGHT, strHeight);
        sharedPrefEditor.commit();
    }

    /**
     * 키 유닛 반환
     *
     * @return 환자 키
     */
    public static String getHeightUnit(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.SHARED_HEIGHT_UNIT, "cm");
    }

    /**
     * 키 유닛 셋팅
     */
    public static void setHeightUnit(Context context, String strHeightUnit) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.SHARED_HEIGHT_UNIT, strHeightUnit);
        sharedPrefEditor.commit();
    }

    /**
     * 몸무게 반환
     *
     * @return 몸무게 반환
     */
    public static String getWeight(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.SHARED_WEIGHT, "60");
    }

    /**
     * 몸무게 셋팅
     */
    public static void setWeightGoal(Context context, String strWeight) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.SHARED_WEIGHT, strWeight);
        sharedPrefEditor.commit();
    }

    /**
     * 체중 단위
     *
     * @return
     */
    public static String getWeightUnit(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.SHARED_WEIGHT_UNIT, "kg");
    }

    /**
     * 체중 단위
     *
     * @return
     */
    public static void setWeightUnit(Context context, String weightUnit) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.SHARED_WEIGHT_UNIT, weightUnit);
        sharedPrefEditor.commit();

    }

    /**
     * 당뇨 관리 사용 여부 가져오기
     *
     * @param context
     * @return
     */
    public static boolean getUsingBloodGlucose(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_USING_BLOODGLUCOSE, true);
    }

    /**
     * 당뇨 관리 사용 여부 세팅
     *
     * @param context
     * @param isUseingBloodGlucose
     */
    public static void setUsingBloodGlucose(Context context, boolean isUseingBloodGlucose) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_USING_BLOODGLUCOSE, isUseingBloodGlucose);
        sharedPrefEditor.commit();
    }

    /**
     * 성별 반환
     *
     * @return 환자 성별
     */
    public static String getGender(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.SHARED_GENDER, ManagerConstants.Gender.MALE);
    }

    /**
     * 성별 반환
     */
    public static void setGender(Context context, String gender) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.SHARED_GENDER, gender);
        sharedPrefEditor.commit();
    }

    /**
     * 생년월일 반환
     *
     * @return 생년월일
     */
    public static String getDayOfBirth(Context context) {
        getPreferenceEditor(context);

        String strBirth = "";

        try {
            strBirth = sharedPref.getString(SharedPreferencesName.SHARED_BIRTH, "19820101");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strBirth;
    }

    /**
     * 생년월일 반환
     *
     * @return 생년월일
     */
    public static String getDecDayOfBirth(Context context) {
        getPreferenceEditor(context);

        String strDeclBirth = "";

        try {
            strDeclBirth = AesssUtil.decrypt(sharedPref.getString(SharedPreferencesName.SHARED_BIRTH, "19820101"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strDeclBirth;
    }

    /**
     * 생년월일 셋팅
     *
     * @return 생년월일
     */
    public static void setEncDayOfBirth(Context context, String birth) {
        getPreferenceEditor(context);

        String strEnclBirth = "";
        try {
            strEnclBirth = AesssUtil.encrypt(birth);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPrefEditor.putString(SharedPreferencesName.SHARED_BIRTH, strEnclBirth);
        sharedPrefEditor.commit();
    }

    /**
     * 혈당 식전 최소값 반환
     *
     * @return 혈당 수축기 최소값
     */
    public static int getGlucoseMinBeforeMeal(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_GLUCOSE_MIN_BEFORE_MEAL, 81);
    }

    /**
     * 혈당 식전 최소값 세팅
     *
     * @param context
     * @param glucoseMinBeforeMeal 혈당 식전 최소값
     */

    public static void setGlucoseMinBeforeMeal(Context context, int glucoseMinBeforeMeal) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_GLUCOSE_MIN_BEFORE_MEAL, glucoseMinBeforeMeal);
        sharedPrefEditor.commit();
    }

    /**
     * 혈당 식전 최대값 반환
     *
     * @return 혈당 식전 최대값
     */
    public static int getGlucoseMaxBeforeMeal(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_GLUCOSE_MAX_BEFORE_MEAL, 120);
    }

    /**
     * 혈당 식전 최대값 세팅
     *
     * @param context
     * @param glucoseMaxBeforeMeal 혈당 식전 최대값
     */

    public static void setGlucoseMaxBeforeMeal(Context context, int glucoseMaxBeforeMeal) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_GLUCOSE_MAX_BEFORE_MEAL, glucoseMaxBeforeMeal);
        sharedPrefEditor.commit();
    }

    /**
     * 혈당 식후 최소값 반환
     *
     * @return 혈당 식후 최소값 반환
     */
    public static int getGlucoseMinAfterMeal(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_GLUCOSE_MIN_AFTER_MEAL, 121);
    }

    /**
     * 혈당 식후 최소값
     *
     * @param context
     * @param glucoseMinAfterMeal 혈당 식후 최소값
     */

    public static void setGlucoseMinAfterMeal(Context context, int glucoseMinAfterMeal) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_GLUCOSE_MIN_AFTER_MEAL, glucoseMinAfterMeal);
        sharedPrefEditor.commit();
    }

    /**
     * 혈당 식후 최대값
     *
     * @return 혈당 식후 최대값
     */
    public static int getGlucoseMaxAfterMeal(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_GLUCOSE_MAX_AFTER_MEAL, 160);
    }

    /**
     * 혈당 식후 최대값
     *
     * @param context
     * @param glucoseMaxAfterMeal 혈당 식후 최대값
     */

    public static void setGlucoseMaxAfterMeal(Context context, int glucoseMaxAfterMeal) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_GLUCOSE_MAX_AFTER_MEAL, glucoseMaxAfterMeal);
        sharedPrefEditor.commit();
    }

    /**
     * 혈압 수축기 최소값 반환
     *
     * @return 혈압 수축기 최소값
     */
    public static int getBPMinSystole(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_BP_MIN_SYSTOLE, 90);
    }

    /**
     * 혈압 수축기 세팅
     *
     * @param context
     * @param bpMinSystole 수축기 최소값
     */

    public static void setBPMinSystole(Context context, int bpMinSystole) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_BP_MIN_SYSTOLE, bpMinSystole);
        sharedPrefEditor.commit();
    }

    /**
     * 혈압 수축기 최대값 반환
     *
     * @return 혈압 수축기 최대값
     */
    public static int getBPMaxSystole(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_BP_MAX_SYSTOLE, 119);
    }

    /**
     * 혈압 수축기 세팅
     *
     * @param context
     * @param bpMaxSystole 수축기 최대값
     */

    public static void setBPMaxSystole(Context context, int bpMaxSystole) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_BP_MAX_SYSTOLE, bpMaxSystole);
        sharedPrefEditor.commit();
    }

    /**
     * 혈압 이완기 최소값 반환
     *
     * @return 혈압 이완기 최소값
     */
    public static int getBPMinDiastole(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_BP_MIN_DIASTOLE, 60);
    }

    /**
     * 혈압 이완기 세팅
     *
     * @param context
     * @param bpMinDiastole 이완기 최소값
     */

    public static void setBPMinDiastole(Context context, int bpMinDiastole) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_BP_MIN_DIASTOLE, bpMinDiastole);
        sharedPrefEditor.commit();
    }

    /**
     * 혈압 이완기 최대값 반환
     *
     * @return 혈압 이완기 최대값
     */
    public static int getBPMaxDiastole(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_BP_MAX_DIASTOLE, 79);
    }

    /**
     * 혈압 이완기 세팅
     *
     * @param context
     * @param bpMaxDiastole 이완기 최대값
     */

    public static void setBPMaxDiastole(Context context, int bpMaxDiastole) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_BP_MAX_DIASTOLE, bpMaxDiastole);
        sharedPrefEditor.commit();
    }

    /**
     * 언어 반환
     *
     * @return 언어
     */
    public static String getLanguage(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.SHARED_LANGUAGE, "");
    }

    /**
     * 언어 반환
     *
     * @return 언어
     */
    public static void setLanguage(Context context, String strLanguage) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.SHARED_LANGUAGE, strLanguage);
        sharedPrefEditor.commit();

    }

    /**
     * 혈당 단위
     *
     * @return
     */
    public static String getGlucoseUnit(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getString(SharedPreferencesName.SHARED_GLUCOSE_UNIT, "mg/dL");
    }

    /**
     * 혈당 단위
     *
     * @return
     */
    public static void setGlucoseUnit(Context context, String glucoseUnit) {
        getPreferenceEditor(context);
        sharedPrefEditor.putString(SharedPreferencesName.SHARED_GLUCOSE_UNIT, glucoseUnit);
        sharedPrefEditor.commit();

    }

    /**
     * 버전 반환
     *
     * @return 버전
     */
    public static String getVersion(Context context) {
        // 앱 버전 가져오기
        PackageInfo pakageInfo = null;
        try {
            pakageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pakageInfo.versionName;
    }

    /**
     * 언어 변경 상태 값
     */
    public static void setLanguageState(Context context, boolean flag) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_LANGUAGE_STATE, flag);
        sharedPrefEditor.commit();
    }

    /**
     * 언어 변경 상태 값
     */
    public static boolean getLanguageState(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_LANGUAGE_STATE, false);
    }

    /**
     * 기기 사용여부
     */
    public static void setHaveBP(Context context, int value) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_BRING_BP, value);
        sharedPrefEditor.commit();
    }

    /**
     * 기기 사용여부
     */
    public static int getHaveBP(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_BRING_BP, HaveBP.DEFAULT);
    }

    public static void versionCheck(Context context) {
        getPreferenceEditor(context);
        int prefVersion = sharedPref.getInt(SharedPreferencesName.SHARED_VERSION, 0);

        if (prefVersion != SharedPreferencesName.PREF_VERSION) {
            if (prefVersion == 0) {
                versionChangeZeroToOne(context);

                prefVersion++;
            }
            sharedPrefEditor.putInt(SharedPreferencesName.SHARED_VERSION, prefVersion);
            sharedPrefEditor.commit();
        }
    }

    private static void versionChangeZeroToOne(Context context) {
        //성별...
        String gender = sharedPref.getString(SharedPreferencesName.SHARED_GENDER, "");

        if ("남자".equals(gender) || "男性".equals(gender) || "Male".equals(gender)) {
            sharedPrefEditor.putString(SharedPreferencesName.SHARED_GENDER, Gender.MALE);
        } else {
            sharedPrefEditor.putString(SharedPreferencesName.SHARED_GENDER, Gender.FEMALE);
        }
    }

    /**
     * Config 파일 초기화용 버전 코드(앱 VersionCode이용)
     */
    public static int getPreVersionCode(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getInt(SharedPreferencesName.SHARED_PRE_VERSION_CODE, 0);
    }

    /**
     * Config 파일 초기화용 버전 코드(앱 VersionCode이용)
     */
    public static void setPreVersionCode(Context context, int code) {
        getPreferenceEditor(context);
        sharedPrefEditor.putInt(SharedPreferencesName.SHARED_PRE_VERSION_CODE, code);
        sharedPrefEditor.commit();
    }

    /**
     * DB 마이그레이션(DB 버전 2 -> 3)
     */
    public static boolean getIsDBMigration(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_IS_DB_MIGRATION, false);
    }

    /**
     * DB 마이그레이션(DB 버전 2 -> 3)
     */
    public static void setIsDBMigration(Context context, boolean isMigration) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_IS_DB_MIGRATION, isMigration);
        sharedPrefEditor.commit();
    }

    /**
     * Preference 마이그레이션(DB 버전 2 -> 3)
     */
    public static boolean getIsPreferenceMigration(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_IS_PRE_MIGRATION, false);
    }

    /**
     * Preference 마이그레이션(DB 버전 2 -> 3)
     */
    public static void setIsPreferenceMigration(Context context, boolean isMigration) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_IS_PRE_MIGRATION, isMigration);
        sharedPrefEditor.commit();
    }

    /**
     * 마이그레이션(DB 버전 2 -> 3)
     */
    public static void doPreferenceMigration(Context context) {
        getPreferenceEditor(context);

        try {

            String strEmail = sharedPref.getString(SharedPreferencesName.SHARED_EMAIL, "");

            if (!("".equals(strEmail))) {

                strEmail = AesssUtil.decryptOld(sharedPref.getString(SharedPreferencesName.SHARED_EMAIL, ""));
                String strFirstName = AesssUtil.decryptOld(sharedPref.getString(SharedPreferencesName.SHARED_FIRST_NAME,
                                                                                ""));
                String strLastName = AesssUtil.decryptOld(sharedPref.getString(SharedPreferencesName.SHARED_LAST_NAME,
                                                                               ""));
                String strLanguage = PreferenceUtil.getLanguage(context);
                String strGender = PreferenceUtil.getGender(context);

                PreferenceUtil.setEncEmail(context, strEmail);
                PreferenceUtil.setEncFirstName(context, strFirstName);
                PreferenceUtil.setEncLastName(context, strLastName);

                if ("jp".equals(strLanguage)) {
                    strLanguage = Language.JPN;
                    PreferenceUtil.setLanguage(context, strLanguage);
                }

                if ("male".equals(strGender)) {
                    PreferenceUtil.setGender(context, Gender.MALE);
                } else if ("female".equals(strGender)) {
                    PreferenceUtil.setGender(context, Gender.FEMALE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        PreferenceUtil.setIsPreferenceMigration(context, true);

        sharedPrefEditor.commit();

    }

    /**
     * 복약/측정 알람 전체 플래그 STATE (ON/OFF)
     */
    public static void setWholeToggleState(Context context, boolean isToggleOn) {
        getPreferenceEditor(context);
        sharedPrefEditor.putBoolean(SharedPreferencesName.SHARED_ALARM_WHOLE_TOGGLE, isToggleOn);
        sharedPrefEditor.commit();
    }

    public static boolean getWholeToggleState(Context context) {
        getPreferenceEditor(context);
        return sharedPref.getBoolean(SharedPreferencesName.SHARED_ALARM_WHOLE_TOGGLE, false);
    }
}
