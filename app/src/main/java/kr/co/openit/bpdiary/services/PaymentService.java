package kr.co.openit.bpdiary.services;

import android.content.Context;

import org.json.JSONObject;

import java.util.Map;

import kr.co.openit.bpdiary.common.constants.ManagerConstants.RESTfulURI;
import kr.co.openit.bpdiary.common.helper.HTTPHelper;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Main 관련 Service
 */
public class PaymentService {

    private final Context context;

    /**
     * 생성자
     */
    public PaymentService(Context context) {
        this.context = context;
    }

    /**
     * 회원 결제 여부 체크
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject creatPayment(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.PAY;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }
        return jsonObject;
    }
}
