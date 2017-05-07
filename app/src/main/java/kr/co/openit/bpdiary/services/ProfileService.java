package kr.co.openit.bpdiary.services;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

import kr.co.openit.bpdiary.common.constants.ManagerConstants.RESTfulURI;
import kr.co.openit.bpdiary.common.helper.HTTPHelper;
import kr.co.openit.bpdiary.utils.PreferenceUtil;
import kr.co.openit.bpdiary.common.measure.dao.MeasureDAO;

/**
 * 프로필 Service
 */
public class ProfileService {

    private final Context context;

    /**
     * measureDAO
     */
    private final MeasureDAO measureDAO;

    /**
     * 생성자
     */
    public ProfileService(Context context) {
        this.context = context;
        measureDAO = new MeasureDAO(context);
    }

    /**
     * 프로필 갱신
     */
    public JSONObject updateMember(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.PROFILE_UPDATE;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 서비스 해지
     */
    public JSONObject leaveMember(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.LEAVE;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 회원 탈퇴 DB 삭제
     */
    public void deleteLeaveMember() {
        measureDAO.deleteDBAllData();
    }
}
