package kr.co.openit.bpdiary.services;

import android.content.Context;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.RESTfulURI;
import kr.co.openit.bpdiary.common.helper.HTTPHelper;
import kr.co.openit.bpdiary.utils.PreferenceUtil;
import kr.co.openit.bpdiary.common.measure.dao.MeasureDAO;

/**
 * 체중 측정 서버 처리
 */
public class WeighingScaleService {
    private final Context context;
    /**
     * measureDAO
     */
    private final MeasureDAO measureDAO;

    /**
     * 생성자
     */
    public WeighingScaleService(Context context) {
        this.context = context;
        measureDAO = new MeasureDAO(context);
    }

    /**
     * 체중 측정 데이터 저장(DB)
     *
     * @param pMap
     * @return
     */
    public int createWeighingScaleData(Map<String, String> pMap) {
        return measureDAO.insertWSData(pMap);
    }

    /**
     * 체중 측정 데이터 저장(Server)
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject sendWeighingScaleData(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.WS_SEND;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 체중 측정 데이터 서버 전송 성공 여부
     *
     * @param strSeq
     */
    public void updateSendToServerYN(String strSeq) {
        measureDAO.updateSendToServerYN(ManagerConstants.DataBase.TABLE_NAME_WS,
                ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ,
                strSeq);
    }

    /**
     * 체중 측정 데이터 Message 저장(DB)
     *
     * @param strSeq
     * @param strMessage
     */
    public int updateMessage(String strSeq, String strMessage) {
        return measureDAO.updateMessage(ManagerConstants.DataBase.TABLE_NAME_WS,
                ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ,
                strSeq,
                strMessage);
    }

    /**
     * 혈압 측정 데이터 수정(Server)
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject modifyMeasureData(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.WS_UPDATE;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 체중 측정 마지막 데이터 조회(최근 측정 데이터)
     *
     * @return
     */
    public List<Map<String, String>> searchWeighinhScaleLastMeasureData() {
        return measureDAO.selectWSLastMeasureData();
    }

    /**
     * 체중 측정 마지막 데이터 조회(최근 입력 데이터)
     *
     * @return
     */
    public List<Map<String, String>> searchWeighinhScaleLastInputData() {
        return measureDAO.selectWSLastInputData();
    }

    /**
     * 체중 측정 데이터 전체 리스트 조회
     *
     * @return
     */
    public List<Map<String, String>> searchWeighinhScaleDataList() {
        return measureDAO.selectWSDataList();
    }

    /**
     * 체중 측정 데이터 서버 미전송 리스트 조회
     *
     * @return
     */
    public List<Map<String, String>> searchWeighinhScaleNotSyncDataList() {
        return measureDAO.selectWSNotSendDataList();
    }

    /**
     * 체중 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public List<Map<String, String>> searchWeighinhScaleDataListPeriodGrapth(Map<String, String> pMap, int nPeriodType, String strDateLength, String strPeriodStart, String strPeriodEnd) {
        if (nPeriodType == ManagerConstants.PeriodType.PERIOD_YEAR) {
            return measureDAO.selectWSDataListPeriodGrapthForYear(pMap, strDateLength, strPeriodStart, strPeriodEnd);
        } else {
            return measureDAO.selectWSDataListPeriodGrapth(pMap, nPeriodType, strDateLength, strPeriodStart, strPeriodEnd);
        }

    }

    /**
     * 체중 측정 데이터 리스트 기간별 평균 조회
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public Map<String, String> searchWsAvg(Map<String, String> pMap, int nPeriodType, String strDateLength, String strPeriodStart, String strPeriodEnd) {
        return measureDAO.selectWSAvg(pMap, nPeriodType, strDateLength, strPeriodStart, strPeriodEnd);
    }

    /**
     * 체중 측정 데이터 리스트 기간별 조회(리스트)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public List<Map<String, String>> searchWeighinhScaleDataGraphAll(Map<String, String> pMap, int nPeriodType) {
        return measureDAO.selectWSDataListPeriodGraphAll(pMap, nPeriodType);
    }

    /**
     * 체중 측정 데이터 리스트 기간별 조회(리스트)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public List<Map<String, String>> searchWeighinhScaleDataListPeriodList(Map<String, String> pMap, int nPeriodType) {
        return measureDAO.selectWSDataListPeriodList(pMap, nPeriodType);
    }

    /**
     * DB 에 저장된 전체 개수
     *
     * @return
     */
    public int searchTotalCount() {
        return measureDAO.selectTotalCount(ManagerConstants.DataBase.TABLE_NAME_WS,
                ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ);
    }

    /**
     * 선택 데이터 삭제(DB)
     *
     * @param strSeq
     */
    public void deleteMeasureData(String strSeq) {
        measureDAO.deleteMeasureData(ManagerConstants.DataBase.TABLE_NAME_WS,
                ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ,
                strSeq);
    }

    /**
     * 선택 데이터 삭제(Server)
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject removeMeasureData(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.WS_REMOVE;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 체중 측정 동기화 데이터 조회(Server)
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject syncMeasureData(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.WS_SYNC;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 체중 목표 저장(Server)
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject modifyWSGoal(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.MOD_GOAL;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }
}
