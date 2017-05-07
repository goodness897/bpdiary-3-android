package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ByteUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.common.vo.attr.AbsoluteTime;
import kr.co.openit.bpdiary.common.vo.attr.AttrValMapEntry;
import kr.co.openit.bpdiary.common.vo.attr.Attribute;
import kr.co.openit.bpdiary.common.vo.attr.OIDType;
import kr.co.openit.bpdiary.common.vo.attr.PmSegmentEntryMap;
import kr.co.openit.bpdiary.common.vo.attr.SegmEntryElem;
import kr.co.openit.bpdiary.common.vo.attr.TYPE;

/**
 * SegmentDataEvent 정의
 */
public class SegmentDataEvent extends PrstApdu implements IApduBody {

    /**
     * 생성자
     */
    public SegmentDataEvent(ConfigReport configReport, SegmentInfoList segmentInfoList, ByteBuffer buffer) {

        int unitCode = 0;
        int dataAttrId = 0;

        Map<Integer, Map<String, Object>> configMap = configReport.getConfigMap();
        Iterator<Integer> itr = configMap.keySet().iterator();
        while (itr.hasNext()) {

            int key = itr.next();
            //Log.d(TAG, "key : " + key + ", segmentInfoList.getObjHandle() : " + segmentInfoList.getObjHandle());
            if (key != segmentInfoList.getObjHandle()) {

                @SuppressWarnings("unchecked")
                Map<Integer, Attribute> attrMap = (Map<Integer, Attribute>)configMap.get(key)
                                                                                    .get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

                if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)) {
                    unitCode = ((OIDType)attrMap.get(Nomenclature.Attribute.MDC_ATTR_UNIT_CODE)).getValue();
                    //Log.d(TAG, "unitCode : " + unitCode);
                }

                if (attrMap.containsKey(Nomenclature.Attribute.MDC_ATTR_ID_TYPE)) {
                    dataAttrId = ((TYPE)attrMap.get(Nomenclature.Attribute.MDC_ATTR_ID_TYPE)).getCode();
                    //Log.d(TAG, "dataAttrId : " + dataAttrId);
                }
            }
        }

        buffer.position(0);

        eventInfoLength = buffer.getShort();

        segmInstance = ByteUtil.short2ushort(buffer.getShort()); // SegmentDataEvent.SegmDataEventDescr.segm_instance
        segmEvtEntryIndex = ByteUtil.int2uint(buffer.getInt()); // SegmentDataEvent.SegmDataEventDescr.segm_evt_entry_index
        segmEvtEntryCount = ByteUtil.int2uint(buffer.getInt()); // SegmentDataEvent.SegmDataEventDescr.segm_evt_entry_count
        segmEvtStatus = ByteUtil.short2ushort(buffer.getShort()); // SegmentDataEvent.SegmDataEventDescr.SegmEvtStatus

        int segmDataEventEntryLength = ByteUtil.short2ushort(buffer.getShort()); // SegmentDataEvent.segm_data_event_entries.length

        int bufLen = (int)(segmDataEventEntryLength / segmEvtEntryCount);

        PmSegmentEntryMap pmSegmentEntryMap = (PmSegmentEntryMap)segmentInfoList.getAttr(Nomenclature.Attribute.MDC_ATTR_PM_SEG_MAP);
        int segmCount = pmSegmentEntryMap.getSegmEntryElemListCount();

        for (int i = 0; i < segmEvtEntryCount; i++) {

            byte[] arr = new byte[bufLen];
            buffer = buffer.get(arr, 0, bufLen);

            ByteBuffer buf = ByteBuffer.allocate(arr.length).put(arr);
            buf.position(0);

            AbsoluteTime absoluteTime = new AbsoluteTime(Nomenclature.Attribute.MDC_ATTR_TIME_STAMP_ABS);
            absoluteTime.setCentury(ByteUtil.byte2uchar(buf.get()));
            absoluteTime.setYear(ByteUtil.byte2uchar(buf.get()));
            absoluteTime.setMonth(ByteUtil.byte2uchar(buf.get()));
            absoluteTime.setDay(ByteUtil.byte2uchar(buf.get()));
            absoluteTime.setHour(ByteUtil.byte2uchar(buf.get()));
            absoluteTime.setMinute(ByteUtil.byte2uchar(buf.get()));
            absoluteTime.setSecond(ByteUtil.byte2uchar(buf.get()));
            absoluteTime.setSecFractions(ByteUtil.byte2uchar(buf.get()));

            for (int j = 0; j < segmCount; j++) {
                SegmEntryElem segmEntryElem = pmSegmentEntryMap.getSegmEntryElem(j);

                MeasureData measureData = null;

                int attrValMapCount = segmEntryElem.getAttrValMap().getCount();
                for (int k = 0; k < attrValMapCount; k++) {

                    AttrValMapEntry attrValMapEntry = segmEntryElem.getAttrValMap().getAttrValMapEntry(k);
                    int attrId = attrValMapEntry.getAttributeId(); // attribute id

                    String value = null;
                    if ((attrId == Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_BASIC) || (attrId == Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_SIMP)) {

                        switch (attrId) {
                            case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_BASIC: // SFLOAT-Type

                                // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                // (mantissa) × (10**exponent)
                                short shortValue = buf.getShort();

                                if (ManagerUtil.getValidSFloatTypeValue(shortValue)) {
                                    value = Float.toString(ManagerUtil.convertShortToSFloat(shortValue));
                                }

                                break;

                            case Nomenclature.Attribute.MDC_ATTR_NU_VAL_OBS_SIMP: // FLOAT-Type

                                // 2 byte (16 bit) 중에 앞에 4 bit 는 지수(exponent), 뒤에 12 bit 는 가수(mantissa) : signed
                                // (mantissa) × (10**exponent)
                                int intValue = buf.getInt();

                                if (ManagerUtil.getValidFloatTypeValue(intValue)) {
                                    value = Double.toString(ManagerUtil.convertIntToDouble(intValue));
                                }

                                break;

                            default:
                                break;
                        }

                        if (measureData == null) {
                            measureData = new MeasureData();
                        }

                        measureData.setObjHandle(objHandle);
                        measureData.setAttrId(dataAttrId);
                        measureData.setValue(value);
                        measureData.setUnitCd(unitCode);

                    } else if (attrId == Nomenclature.Attribute.MDC_ATTR_TIME_STAMP_ABS) {

                        StringBuffer sb = new StringBuffer();

                        sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buf.get())), 2));
                        sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buf.get())), 2))
                          .append("-");
                        sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buf.get())), 2))
                          .append("-");
                        sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buf.get())), 2))
                          .append(" ");
                        sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buf.get())), 2))
                          .append(":");
                        sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buf.get())), 2))
                          .append(":");
                        sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buf.get())), 2))
                          .append(".");
                        sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(buf.get())), 2));

                        if (measureData == null) {
                            measureData = new MeasureData();
                        }

                        measureData.setDateTime(sb.toString());
                    }

                    dataList.add(measureData);
                }
            }
        }
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
    private int eventType = Nomenclature.Action.MDC_NOTI_SEGMENT_DATA;

    /**
     * event-info length
     */
    private int eventInfoLength;

    /**
     * segment instance
     */
    private final int segmInstance;

    /**
     * segment event entry index
     */
    private final long segmEvtEntryIndex;

    /**
     * segment event entry count
     */
    private final long segmEvtEntryCount;

    /**
     * segment event status
     */
    private final int segmEvtStatus;

    /**
     * 측정 데이터 적재 리스트
     */
    private final ArrayList<MeasureData> dataList = new ArrayList<MeasureData>();

    /**
     * objHandle setter
     * 
     * @param objHandle - the objHandle to set
     */
    public void setObjHandle(int objHandle) {
        this.objHandle = objHandle;
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
     * eventType setter
     * 
     * @param eventType - the eventType to set
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
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
     * objHandle getter
     * 
     * @return objHandle
     */
    public int getObjHandle() {
        return objHandle;
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
     * eventType getter
     * 
     * @return eventType
     */
    public int getEventType() {
        return eventType;
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
     * segmInstance getter
     *
     * @return
     */
    public int getSegmInstance() {
        return segmInstance;
    }

    /**
     * segmEvtEntryIndex getter
     * 
     * @return segmEvtEntryIndex
     */
    public long getSegmEvtEntryIndex() {
        return segmEvtEntryIndex;
    }

    /**
     * segmEvtEntryCount getter
     * 
     * @return segmEvtEntryCount
     */
    public long getSegmEvtEntryCount() {
        return segmEvtEntryCount;
    }

    /**
     * segmEvtStatus getter
     * 
     * @return segmEvtStatus
     */
    public int getSegmEvtStatus() {
        return segmEvtStatus;
    }

    /**
     * measure data list getter
     * 
     * @return
     */
    public ArrayList<MeasureData> getMeasureDataList() {
        return dataList;
    }

    /**
     * SegmEvtStatus 정의
     */
    public final class SegmEvtStatus {

        /**
         * SEVTSTA_FIRST_ENTRY (this event contains the first segment entry) - 0
         */
        public static final int SEVTSTA_FIRST_ENTRY = 0x8000;

        /**
         * SEVTSTA_LAST_ENTRY (this event contains the last segment entry (both first and last bits can be set if all
         * entries fit in one event)) - 1
         */
        public static final int SEVTSTA_LAST_ENTRY = 0x4000;

        /**
         * SEVTSTA_AGENT_ABORT (transfer aborted by agent (manager shall reply with the same status)) - 4
         */
        public static final int SEVTSTA_AGENT_ABORT = 0x0800;

        /**
         * SEVTSTA_MANAGER_CONFIRM (set in reply if segment was received correctly (if not set in reply, agent shall
         * repeat the last event)) - 8
         */
        public static final int SEVTSTA_MANAGER_CONFIRM = 0x0080;

        /**
         * SEVTSTA_MANAGER_ABORT (sent in reply by manager (agent shall stop sending messages)) - 12
         */
        public static final int SEVTSTA_MANAGER_ABORT = 0x0008;

        /**
         * 
         */
        public static final int SEVTSTA_FIRST_ENTRY_AGENT_ABORT = 0x8800;

        /**
         * 
         */
        public static final int SEVTSTA_LAST_ENTRY_AGENT_ABORT = 0x4800;

        /**
         * 
         */
        public static final int SEVTSTA_FIRST_ENTRY_MANAGER_CONFIRM = 0x8080;

        /**
         * 
         */
        public static final int SEVTSTA_LAST_ENTRY_MANAGER_CONFIRM = 0x4080;

        /**
         * 
         */
        public static final int SEVTSTA_FIRST_ENTRY_AGENT_ABORT_MANAGER_CONFIRM = 0x8880;

        /**
         * 
         */
        public static final int SEVTSTA_LAST_ENTRY_AGENT_ABORT_MANAGER_CONFIRM = 0x4880;

        /**
         * 생성자
         */
        private SegmEvtStatus() {
            // default constructor
        }
    }

    @Override
    public byte[] getBytes() {
        return null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("obj-handle = ").append(objHandle).append("\n");
        sb.append("eventTime = ").append(Integer.toHexString(eventTime)).append("\n");
        sb.append("eventType = MDC_NOTI_SEGMENT_DATA \n");
        sb.append("eventInfoLength = ").append(eventInfoLength).append("\n");
        sb.append("SegmentDataEvent.SegmDataEventDescr.segm_instance = ").append(segmInstance).append("\n");
        sb.append("SegmentDataEvent.SegmDataEventDescr.segm_evt_entry_index = ").append(segmEvtEntryIndex).append("\n");
        sb.append("SegmentDataEvent.SegmDataEventDescr.segm_evt_entry_count = ").append(segmEvtEntryCount).append("\n");
        sb.append("SegmentDataEvent.SegmDataEventDescr.segm_evt_status = ").append(segmEvtStatus).append("\n");
        sb.append("MeasureData [[").append(dataList.toString()).append(" ]]\n");

        return super.toString() + sb.toString();
    }

}
