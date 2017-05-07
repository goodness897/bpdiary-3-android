package kr.co.openit.bpdiary.services;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

import kr.co.openit.bpdiary.common.constants.ManagerConstants.RESTfulURI;
import kr.co.openit.bpdiary.common.helper.HTTPHelper;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class NoticeService {

    private final Context context;

    /**
     * 생성자
     */
    public NoticeService(Context context) {
        this.context = context;
    }

    /**
     * 공지사항 상세 조회
     */
    public JSONObject searchNoticeList(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.NOTICE_LIST;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }
        return jsonObject;
    }
}
