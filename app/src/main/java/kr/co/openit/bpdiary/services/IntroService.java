package kr.co.openit.bpdiary.services;

import android.content.Context;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.ManagerConstants.RESTfulURI;
import kr.co.openit.bpdiary.common.helper.HTTPHelper;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Main 관련 Service
 */
public class IntroService {

    /**
     * context 변수
     */
    private final Context context;

    /**
     * 생성자
     */
    public IntroService(Context context) {
        this.context = context;
    }

    /**
     * APP 버전 정보
     */
    public JSONObject searchVersion(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.APP_VERSION_CHECK;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 로그인 체크
     */
    public JSONObject searchLoginCheck(Map<Object, Object> data) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {

            String path = RESTfulURI.LOGIN;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 사용자 정보 생성
     */
    public JSONObject createUserInfo(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.JOIN;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 회원 결제 여부 체크
     */
    public JSONObject searchPayment(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.PAY;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 가입 여부
     */
    public JSONObject checkJoin(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.CHECK_JOIN;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 비밀번호 찾기
    */
    public JSONObject findPassword(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.FIND_PASSWORD;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 비밀번호 변경
    */
    public JSONObject changePassword(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.CHANGE_PASSWORD;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
