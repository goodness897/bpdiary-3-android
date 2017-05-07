package kr.co.openit.bpdiary.services;

import android.content.Context;

import org.json.JSONObject;

import kr.co.openit.bpdiary.common.constants.ManagerConstants.RESTfulURI;
import kr.co.openit.bpdiary.common.helper.HTTPHelper;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * 혈압 측정 서버 처리
 */
public class EventService {
    private final Context context;

    /**
     * 생성자
     */
    public EventService(Context context) {
        this.context = context;
    }


    /**
     * 광고
     *
     * @return
     * @throws Exception
     */
    public JSONObject searchAdsEvent(String adsSeq) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.ADS_EVENT;
            jsonObject = HTTPHelper.doGetForAds(path, adsSeq, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }
}
