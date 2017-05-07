package kr.co.openit.bpdiary.services;

import android.content.Context;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.RESTfulURI;
import kr.co.openit.bpdiary.common.helper.HTTPHelper;
import kr.co.openit.bpdiary.common.measure.dao.MeasureDAO;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * 혈당 측정 서버 처리
 */
public class GlucoseService {
    private final Context context;
    /**
     * measureDAO
     */
    private final MeasureDAO measureDAO;

    /**
     * 생성자
     */
    public GlucoseService(Context context) {
        this.context = context;
        measureDAO = new MeasureDAO(context);
    }

    /**
     * 혈당 측정 데이터 저장(DB)
     *
     * @param pMap
     * @return
     */
    public int createGlucoseData(Map<String, String> pMap) {
        return measureDAO.insertGlucoseData(pMap);
    }

    /**
     * 혈당 측정 데이터 저장(Server)
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject sendGlucoseData(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.GLUCOSE_SEND;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 혈당 측정 데이터 서버 전송 성공 여부
     *
     * @param strSeq
     */
    public void updateSendToServerYN(String strSeq) {
        measureDAO.updateSendToServerYN(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE,
                ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                strSeq);
    }

    /**
     * 혈당 측정 데이터 Message 저장(DB)
     *
     * @param strSeq
     * @param strMessage
     */
    public int updateMessage(String strSeq, String strMessage) {
        return measureDAO.updateMessage(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE,
                ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                strSeq,
                strMessage);
    }

    /**
     * 혈당 측정 데이터 Message 저장(DB)
     *
     * @param strSeq
     * @param strMeal
     */
    public int updateMeal(String strSeq, String strMeal, String strTypeValue) {
        return measureDAO.updateMeal(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE,
                ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                strSeq,
                strMeal,
                strTypeValue);
    }

    /**
     * 혈당 측정 데이터 수정(Server)
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject modifyMeasureData(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.GLUCOSE_UPDATE;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 혈당 측정 마지막 데이터 조회(최근 측정 데이터)
     *
     * @return
     */
    public List<Map<String, String>> searchGlucoseLastMeasureData() {
        return measureDAO.selectGlucoseLastMeasureData();
    }

    /**
     * 혈당 측정 마지막 데이터 조회(최근 입력 데이터)
     *
     * @return
     */
    public List<Map<String, String>> searchGlucoseLastInputData() {
        return measureDAO.selectGlucoseLastInputData();
    }

    /**
     * 혈당 측정 데이터 전체 리스트 조회
     *
     * @return
     */
    public List<Map<String, String>> searchGlucoseDataList() {
        return measureDAO.selectGlucoseDataList();
    }

    /**
     * 혈당 측정 데이터 서버 미전송 리스트 조회
     *
     * @return
     */
    public List<Map<String, String>> searchGlucoseNotSyncDataList() {
        return measureDAO.selectGlucoseNotSendDataList();
    }

    /**
     * 혈당 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public List<Map<String, String>> searchGlucoseDataListPeriodGraph(Map<String, String> pMap, int nPeriodType, String strDateLength, String strPeriodStart, String strPeriodEnd) {
        return measureDAO.selectGlucoseDataListPeriodGraph(pMap, nPeriodType, strDateLength, strPeriodStart, strPeriodEnd);
    }

    /**
     * 혈압 측정 데이터 리스트 기간별 평균 조회
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public Map<String, String> searchGlucoseAvg(Map<String, String> pMap, int nPeriodType, String strDateLength, String strPeriodStart, String strPeriodEnd) {
        return measureDAO.searchGlucoseBeforeAvg(strDateLength, strPeriodStart, strPeriodEnd);
    }

    /**
     * 혈당 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public List<Map<String, String>> searchGlucoseDataListPeriodGraph(Map<String, String> pMap, int nPeriodType) {
        return measureDAO.selectGlucoseDataListPeriodGraph(pMap, nPeriodType);
    }

    /**
     * 혈당 측정 데이터 리스트 기간별 조회(그래프)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public List<Map<String, String>> searchGlucoseDataListPeriodList(Map<String, String> pMap, int nPeriodType) {
        return measureDAO.selectGlucoseDataListPeriodList(pMap, nPeriodType);
    }

    /**
     * 혈당 측정 데이터 리스트 기간별 조회(리스트)
     *
     * @param pMap
     * @param nPeriodType
     * @return
     */
    public List<Map<String, String>> searchGlucoseDataGraphAll(Map<String, String> pMap, int nPeriodType) {
        return measureDAO.selectGlucoseDataListPeriodGraphAll(pMap, nPeriodType);
    }

    /**
     * DB 에 저장된 전체 개수
     *
     * @return
     */
    public int searchTotalCount() {
        return measureDAO.selectTotalCount(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE,
                ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ);
    }

    /**
     * 선택 데이터 삭제(DB)
     *
     * @param strSeq
     */
    public void deleteMeasureData(String strSeq) {
        measureDAO.deleteMeasureData(ManagerConstants.DataBase.TABLE_NAME_GLUCOSE,
                ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ,
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

            String path = RESTfulURI.GLUCOSE_REMOVE;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 혈당 측정 동기화 데이터 조회(Server)
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject syncMeasureData(Map<Object, Object> data) throws Exception {

        JSONObject jsonObject = new JSONObject();

        try {

            String path = RESTfulURI.GLUCOSE_SYNC;

            jsonObject = HTTPHelper.doPost(path, data, PreferenceUtil.getLanguage(context));

        } catch (Exception e) {
            //Log.e(getClass().getSimpleName(), e.getMessage(), e);
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 혈압&체중 측정 데이터 마이그레이션
     */
    public void migrationDBData() {
        measureDAO.migrationMeasureData();
    }

    /**
     * 혈당 목표 저장(Server)
     *
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject modifyGlucoseGoal(Map<Object, Object> data) throws Exception {

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
