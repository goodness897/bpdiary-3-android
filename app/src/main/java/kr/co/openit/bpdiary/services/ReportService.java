package kr.co.openit.bpdiary.services;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

import kr.co.openit.bpdiary.common.constants.ManagerConstants.RESTfulURI;
import kr.co.openit.bpdiary.common.helper.HTTPHelper;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class ReportService {

    /**
     * context 변수
     */
    private final Context context;

    /**
     * 생성자
     */
    public ReportService(Context context) {
        this.context = context;
    }

    /**
     * APP 버전 정보
     */
    public JSONObject searchReport(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.SERACH_REPORT;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 레포트 공유
     */
    public JSONObject shareReport(Map<Object, Object> data) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {

            String path = RESTfulURI.SHARE_REPORT;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
