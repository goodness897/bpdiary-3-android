package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ByteUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * 측정 데이터 적재 VO 정의
 */
public class DataPrstApdu extends PrstApdu implements IApduBody {

    /**
     * 생성자 (response)
     */
    public DataPrstApdu() {
        // default constructor

        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_EVENT_REPORT);

        // invoke-id, eventType (setter 이용)
    }

    /**
     * 생성자 (response)
     * 
     * @param invokeId
     * @param eventType
     */
    public DataPrstApdu(int invokeId, int eventType) {

        setChoiceTypeLength(0x0012);
        setOctecStringLength(0x0010);

        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_EVENT_REPORT);

        setInvokeId(invokeId);
        this.eventType = eventType;

        setChoiceLength(0x000a);
    }

    /**
     * obj-handle
     */
    private int objHandle;

    /**
     * eventTime / currentTime = 0
     */
    private int eventTime;

    /**
     * event-type
     */
    private int eventType = Nomenclature.Action.MDC_NOTI_SCAN_REPORT_FIXED;

    /**
     * event-info length
     */
    private int eventInfoLength;

    /**
     * event-reply-info.length
     */
    private final int eventReplyInfoLength = 0x0000;

    /**
     * 측정 데이터 적재 리스트
     */
    private final ArrayList<MeasureData> dataList = new ArrayList<MeasureData>();

    /**
     * mdsObject getter
     * 
     * @return mdsObject
     */
    public int getObjHandle() {
        return objHandle;
    }

    /**
     * mdsObject setter
     * 
     * @param objHandle - the mdsObject to set
     */
    public void setObjHandle(int objHandle) {
        this.objHandle = objHandle;
    }

    /**
     * eventTime getter
     * 
     * @return eventTime
     */
    public int getEventTime() {
        return eventTime;
    }

    /**
     * eventTime setter
     * 
     * @param eventTime - the eventTime to set
     */
    public void setEventTime(int eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * eventType getter
     * 
     * @return eventType
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * eventType setter
     * 
     * @param eventType - the eventType to set
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    /**
     * eventInfoLength getter
     * 
     * @return eventInfoLength
     */
    public int getEventInfoLength() {
        return eventInfoLength;
    }

    /**
     * eventInfoLength setter
     * 
     * @param eventInfoLength - the eventInfoLength to set
     */
    public void setEventInfoLength(int eventInfoLength) {
        this.eventInfoLength = eventInfoLength;
    }

    /**
     * measureData setter
     * 
     * @param measureData
     */
    public void setMeasureData(MeasureData measureData) {
        dataList.add(measureData);
    }

    /**
     * measureData 적재
     *
     * @param objHandle
     * @param attrId
     * @param value
     */
    public void setMeasureData(int objHandle, int attrId, String value) {
        setMeasureData(objHandle, attrId, value, -1);
    }

    /**
     * measureData 적재
     *
     * @param objHandle
     * @param attrId
     * @param value
     * @param unitCd
     */
    public void setMeasureData(int objHandle, int attrId, String value, int unitCd) {
        if (isExistMeasureDataOfObjHandle(objHandle)) {
            for (int i = 0, len = dataList.size(); i < len; i++) {
                MeasureData md = dataList.get(i);

                if (md.getObjHandle() == objHandle) {

                    if (md.getAttrId() == attrId) {
                        md.setAttrId(attrId);
                        md.setValue(value);
                        md.setUnitCd(unitCd);

                        dataList.set(i, md);
                    } else {
                        dataList.add(new MeasureData(objHandle, attrId, value, unitCd));
                    }
                }
            }
        } else {
            dataList.add(new MeasureData(objHandle, attrId, value, unitCd));
        }
    }

    /**
     * measureData 적재
     * 
     * @param objHandle obj-handle
     * @param attrId 속성ID
     * @param personId multi person id
     * @param value 값
     */
    public void setMeasureData(int objHandle, int attrId, int personId, String value) {
        setMeasureData(objHandle, attrId, personId, value, -1);
    }

    /**
     * measureData 적재
     *
     * @param objHandle
     * @param attrId
     * @param personId
     * @param value
     * @param unitCd
     */
    public void setMeasureData(int objHandle, int attrId, int personId, String value, int unitCd) {
        if (isExistMeasureDataOfObjHandle(objHandle)) {
            for (int i = 0, len = dataList.size(); i < len; i++) {
                MeasureData md = dataList.get(i);

                if ((md.getObjHandle() == objHandle) && (md.getPersonId() == personId)) {

                    if (md.getAttrId() == attrId) {
                        md.setAttrId(attrId);
                        md.setValue(value);
                        md.setUnitCd(unitCd);
                        md.setPersonId(personId);

                        dataList.set(i, md);
                    } else {
                        dataList.add(new MeasureData(objHandle, attrId, personId, value, unitCd));
                    }
                }
            }
        } else {
            dataList.add(new MeasureData(objHandle, attrId, personId, value, unitCd));
        }
    }

    /**
     * 해당 objHandle 의 단위 코드 설정
     * 
     * @param objHandle
     * @param unitCd 단위 코드
     */
    public void setUnitCode(int objHandle, int unitCd) {
        if (isExistMeasureDataOfObjHandle(objHandle)) {
            for (int i = 0, len = dataList.size(); i < len; i++) {
                MeasureData data = dataList.get(i);

                if (objHandle == data.getObjHandle()) {
                    data.setUnitCd(unitCd);
                }
            }
        } else {
            MeasureData md = new MeasureData();
            md.setObjHandle(objHandle);
            md.setUnitCd(unitCd);

            dataList.add(md);
        }
    }

    /**
     * dataList getter
     * 
     * @return dataList
     */
    public ArrayList<MeasureData> getDataList() {
        return dataList;
    }

    /**
     * 측정일시 setter
     * 
     * @param objHandle obj-handle
     * @param dateTime 측정일시
     */
    public void setMeasureDateTime(int objHandle, String dateTime) {
        for (int i = 0, len = dataList.size(); i < len; i++) {
            MeasureData data = dataList.get(i);

            if ((data.getObjHandle() == objHandle) && (data.getPersonId() == 0)) {
                data.setDateTime(dateTime);
            }
        }
    }

    /**
     * 측정일시 setter
     * 
     * @param objHandle obj-handle
     * @param personId multi person id
     * @param dateTime 측정일시
     */
    public void setMeasureDateTime(int objHandle, int personId, String dateTime) {
        for (int i = 0, len = dataList.size(); i < len; i++) {
            MeasureData data = dataList.get(i);

            if ((data.getObjHandle() == objHandle) && (data.getPersonId() == personId)) {
                data.setDateTime(dateTime);
            }
        }
    }

    /**
     * subClass setter
     * 
     * @param objHandle
     * @param attrId
     * @param value
     */
    public void addSubClass(int objHandle, int attrId, String value) {
        if (isExistMeasureDataOfObjHandle(objHandle)) {
            for (int i = 0, len = dataList.size(); i < len; i++) {
                MeasureData data = dataList.get(i);

                if ((data.getObjHandle() == objHandle) && (data.getPersonId() == 0)) {
                    data.addSubClassList(attrId, value);
                }
            }
        } else {
            MeasureData md = new MeasureData();
            md.setObjHandle(objHandle);
            md.addSubClassList(attrId, value);

            dataList.add(md);
        }
    }

    /**
     * objHandle 의 데이터 존재여부 반환
     * 
     * @param objHandle
     * @return
     */
    public boolean isExistMeasureDataOfObjHandle(int objHandle) {
        for (int i = 0, len = dataList.size(); i < len; i++) {
            MeasureData md = dataList.get(i);

            if (md.getObjHandle() == objHandle) {
                return true;
            }
        }

        return false;
    }

    /**
     * getBytes override
     */
    @Override
    public byte[] getBytes() {

        byte[] arr = super.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(arr.length + getChoiceLength());

        buffer.put(arr);
        buffer.putShort(ByteUtil.ushort2short(objHandle));
        buffer.putInt(eventTime);
        buffer.putShort(ByteUtil.ushort2short(eventType));
        buffer.putShort(ByteUtil.ushort2short(eventReplyInfoLength));

        return buffer.array();

    }

    /**
     * toString override
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        sb.append("obj-handle = ").append(objHandle).append("\n");
        sb.append("event-time = ").append(Integer.toHexString(eventTime)).append("\n");
        sb.append("event-type = ").append(ManagerUtil.getActionName(eventType)).append("\n");
        sb.append("event-info.length = ").append(eventInfoLength).append("\n");
        sb.append("MeasureData [[").append(dataList.toString()).append(" ]]\n");

        return sb.toString();
    }
}
