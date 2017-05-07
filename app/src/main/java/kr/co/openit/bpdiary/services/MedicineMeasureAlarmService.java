package kr.co.openit.bpdiary.services;

/**
 * Created by hwangem on 2017-02-03.
 */

import android.content.Context;

import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.measure.dao.MeasureDAO;

/**
 * 복약/측정 알림 처리 Service
 */
public class MedicineMeasureAlarmService {

    private final Context context;

    /**
     * measureDAO
     */
    private final MeasureDAO measureDAO;

    /**
     * 생성자
     */
    public MedicineMeasureAlarmService(Context context) {
        this.context = context;
        measureDAO = new MeasureDAO(context);
    }

    /**
     * 복약/측정 알림 데이터 저장(DB)
     *
     * @param pMap
     * @return
     */
    public int insertMedicineMeasureAlarmData(Map<String, String> pMap) {
        return measureDAO.insertMedicineMeasureAlarmData(pMap);
    }

    /**
     * 복약 알림 리스트 조회
     *
     * @return
     */
    public List<Map<String, String>> searchMedicineData() {
        return measureDAO.selectMedicineDataList();
    }

    /**
     * 측정 알림 리스트 조회
     *
     * @return
     */
    public List<Map<String, String>> searchMeasureData() {
        return measureDAO.selectMeasureDataList();
    }

    /**
     * 복약/측정 선택 데이터 삭제(DB)
     *
     * @param strSeq
     * @param strSeq2
     * @return
     */
    public int deleteMedicineMeasureData(String strSeq, int strSeq2) {
        return measureDAO.deleteMedicineMeasureData(ManagerConstants.DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM,
                                                    strSeq,
                                                    strSeq2);
    }

    /**
     * 복약/측정 전체 데이터 삭제(DB)
     *
     * @return
     */
    public int deleteMedicineMeasureWholeData() {
        return measureDAO.deleteMedicineMeasureWholeData(ManagerConstants.DataBase.TABLE_NAME_MEDICINE_MEASURE_ALARM);
    }

    /**
     * 복약/측정 알림 토글 갱신
     *
     * @param strSeq
     * @param strSeq2
     * @param toggleStatus
     * @return
     */
    public int updateMedicinMeasureAlarmData(String strSeq, String strSeq2, String toggleStatus) {
        return measureDAO.updateMedicineMeasureToggleState(strSeq, strSeq2, toggleStatus);
    }

    /**
     * 복약/측정 노티SEQ(노티ID) 조회 (가장 최근의)
     *
     * @param alarmFlag
     * @return
     */
    public int getMedicineMeasureAlarmLatestSEQ(String alarmFlag) {
        return measureDAO.selectMedicineMeasureData(alarmFlag);
    }
}
