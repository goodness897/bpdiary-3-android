package kr.co.openit.bpdiary.common.vo.apdu;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ByteUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.common.vo.attr.AttrValMap;
import kr.co.openit.bpdiary.common.vo.attr.Attribute;
import kr.co.openit.bpdiary.common.vo.attr.FLOATType;
import kr.co.openit.bpdiary.common.vo.attr.HANDLE;
import kr.co.openit.bpdiary.common.vo.attr.INTU16;
import kr.co.openit.bpdiary.common.vo.attr.INTU32;
import kr.co.openit.bpdiary.common.vo.attr.MetricIdList;
import kr.co.openit.bpdiary.common.vo.attr.MetricStructureSmall;
import kr.co.openit.bpdiary.common.vo.attr.OCTECSTRING;
import kr.co.openit.bpdiary.common.vo.attr.OIDType;
import kr.co.openit.bpdiary.common.vo.attr.OperationalState;
import kr.co.openit.bpdiary.common.vo.attr.PMStoreCapab;
import kr.co.openit.bpdiary.common.vo.attr.StoSampleAlg;
import kr.co.openit.bpdiary.common.vo.attr.SupplementalTypeList;
import kr.co.openit.bpdiary.common.vo.attr.TYPE;

/**
 * remote operation invoke event report configuration
 */
public class ConfigReport extends PrstApdu implements IApduBody, Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5843156100531844610L;

    /**
     * 생성자
     */
    public ConfigReport() {
        // default constructor

        // response 용 ConfigPrstApdu 생성
        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_EVENT_REPORT);

        // invoke-id, config-report-id, configResult 설정 (setter 이용)
    }

    /**
     * 생성자
     * 
     * @param invokeId
     * @param configReportId
     */
    public ConfigReport(int invokeId, int configReportId) {
        // response 용 ConfigPrstApdu 생성
        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_EVENT_REPORT);

        // invoke-id, config-report-id 설정
        setInvokeId(invokeId);
        this.configReportId = configReportId;

        setChoiceTypeLength(0x0016);
        setOctecStringLength(0x0014);

        setChoiceLength(0x000e);
    }

    /**
     * 생성자
     * 
     * @param invokeId
     * @param configReportId
     * @param configResult
     */
    public ConfigReport(int invokeId, int configReportId, int configResult) {
        // response 용 ConfigPrstApdu 생성
        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_EVENT_REPORT);

        // invoke-id, config-report-id 설정
        setInvokeId(invokeId);
        this.configReportId = configReportId;

        setChoiceTypeLength(0x0016);
        setOctecStringLength(0x0014);

        setChoiceLength(0x000e);
        this.configResult = configResult;
    }

    /**
     * 생성자
     * 
     * @param buffer
     */
    public ConfigReport(ByteBuffer buffer) {

        buffer.position(0);

        // request 용 ConfigPrstApdu 생성
        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_EVENT_REPORT);

        eventInfoLength = buffer.getShort();
        configReportId = ByteUtil.short2ushort(buffer.getShort()); // config-report-id

        configObjListCount = ByteUtil.short2ushort(buffer.getShort()); // config-obj-list.count
        //        configObjListLength = ByteUtil.short2ushort(buffer.getShort()); // config-obj-list.length
        buffer.getShort();
        configObjListLength = eventInfoLength - 6; // config-obj-list.length

        byte[] configObjListArr = new byte[configObjListLength];
        buffer = buffer.get(configObjListArr, 0, configObjListLength);

        ByteBuffer configObjListBuf = ByteBuffer.allocate(configObjListArr.length).put(configObjListArr);
        configObjListBuf.position(0);

        // config-obj
        for (int a = 0; a < configObjListCount; a++) {

            Map<String, Object> confMap = new HashMap<String, Object>();

            int objClass = ByteUtil.short2ushort(configObjListBuf.getShort()); // obj-class
            confMap.put(HealthcareConstants.ConfigurationKey.OBJ_CLASS, objClass);

            int objHandle = ByteUtil.short2ushort(configObjListBuf.getShort()); // obj-handle
            confMap.put(HealthcareConstants.ConfigurationKey.OBJ_HANDLE, objHandle);

            int attrsCount = ByteUtil.short2ushort(configObjListBuf.getShort()); // attributes.count
            int attrsLength = ByteUtil.short2ushort(configObjListBuf.getShort()); // attributes.length

            byte[] attrsArr = new byte[attrsLength];
            configObjListBuf = configObjListBuf.get(attrsArr, 0, attrsLength);

            ByteBuffer attrsBuf = ByteBuffer.allocate(attrsArr.length).put(attrsArr);
            attrsBuf.position(0);

            Map<Integer, Attribute> attrMap = new HashMap<Integer, Attribute>();

            // attributes
            for (int b = 0; b < attrsCount; b++) {

                int attrId = ByteUtil.short2ushort(attrsBuf.getShort()); //  attribute-id
                int attrValueLength = ByteUtil.short2ushort(attrsBuf.getShort()); // attribute-value.length

                //                Log.w(TAG, "attrId : " + attrId + ", attrValueLength : " + attrValueLength);

                // 올메디쿠스 혈당계 size exception
                byte[] attrArr = new byte[attrValueLength];
                try {
                    attrsBuf = attrsBuf.get(attrArr, 0, attrValueLength);
                } catch (Exception e) {
                    attrsBuf = attrsBuf.get(attrArr, 0, attrsBuf.remaining());
                    attrArr = new byte[] {(byte)0x00,
                                          (byte)0x02,
                                          (byte)0x00,
                                          (byte)0x08,
                                          (byte)0x09,
                                          (byte)0x90,
                                          (byte)0x00,
                                          (byte)0x08,
                                          (byte)0x0a,
                                          (byte)0x49,
                                          (byte)0x00,
                                          (byte)0x02};
                }

                ByteBuffer attrBuf = ByteBuffer.allocate(attrArr.length).put(attrArr);
                attrBuf.position(0);

                switch (attrId) {
                    case Nomenclature.Attribute.MDC_ATTR_ID_TYPE:

                        TYPE type = new TYPE(attrId);
                        type.setPartition(ByteUtil.short2ushort(attrBuf.getShort())); // nom-partition
                        type.setCode(ByteUtil.short2ushort(attrBuf.getShort())); // code

                        attrMap.put(attrId, type);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_METRIC_STRUCT_SMALL:

                        MetricStructureSmall metricStructureSmall = new MetricStructureSmall(attrId);
                        metricStructureSmall.setMsStruct(ByteUtil.byte2uchar(attrBuf.get())); // ms-struct
                        metricStructureSmall.setMsCompNo(ByteUtil.byte2uchar(attrBuf.get())); // ms-comp-no

                        attrMap.put(attrId, metricStructureSmall);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_ID_PHYSIO_LIST:

                        MetricIdList metricIdList = new MetricIdList(attrId);
                        metricIdList.setCount(ByteUtil.short2ushort(attrBuf.getShort())); // MetricIdList.count
                        metricIdList.setLength(ByteUtil.short2ushort(attrBuf.getShort())); // MetricIdList.length

                        for (int c = 0; c < metricIdList.getCount(); c++) {
                            metricIdList.addMetricId(ByteUtil.short2ushort(attrBuf.getShort()));
                        }

                        attrMap.put(attrId, metricIdList);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_UNIT_CODE:

                        OIDType oidType = new OIDType(attrId);
                        oidType.setValue(ByteUtil.short2ushort(attrBuf.getShort()));

                        attrMap.put(attrId, oidType);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_NU_ACCUR_MSMT:
                        // TODO : FLOAT-Type 만 있음. 해석을 어떻게 해야하나...측정의 편차값이라고 하는 듯 하며, optional value 인 듯

                        FLOATType floatType = new FLOATType(attrId);
                        floatType.setValue(ByteUtil.int2uint(attrBuf.getInt()));

                        attrMap.put(attrId, floatType);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_ATTRIBUTE_VAL_MAP:

                        AttrValMap attrValMap = new AttrValMap(attrId);
                        attrValMap.setCount(ByteUtil.short2ushort(attrBuf.getShort()));
                        attrValMap.setLength(ByteUtil.short2ushort(attrBuf.getShort()));

                        for (int c = 0; c < attrValMap.getCount(); c++) {
                            attrValMap.addAttrValMapEntry(ByteUtil.short2ushort(attrBuf.getShort()),
                                                          ByteUtil.short2ushort(attrBuf.getShort()));
                        }

                        attrMap.put(attrId, attrValMap);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES:

                        SupplementalTypeList supplementalTypeList = new SupplementalTypeList(attrId);
                        supplementalTypeList.setCount(ByteUtil.short2ushort(attrBuf.getShort()));
                        supplementalTypeList.setLength(ByteUtil.short2ushort(attrBuf.getShort()));

                        supplementalTypeList.addTYPE(ByteUtil.short2ushort(attrBuf.getShort()),
                                                     ByteUtil.short2ushort(attrBuf.getShort()));

                        attrMap.put(attrId, supplementalTypeList);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_OP_STAT:

                        OperationalState operationalState = new OperationalState(attrId);
                        operationalState.setValue(ByteUtil.short2ushort(attrBuf.getShort()));

                        attrMap.put(attrId, operationalState);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_NUM_SEG:

                        INTU16 intu16 = new INTU16(attrId);
                        intu16.setValue(ByteUtil.short2ushort(attrBuf.getShort()));

                        attrMap.put(attrId, intu16);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_PM_STORE_CAPAB:

                        PMStoreCapab pmStoreCapab = new PMStoreCapab(attrId);
                        pmStoreCapab.setValue(ByteUtil.short2ushort(attrBuf.getShort()));

                        attrMap.put(attrId, pmStoreCapab);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_METRIC_STORE_SAMPLE_ALG:

                        StoSampleAlg stoSampleAlg = new StoSampleAlg(attrId);
                        stoSampleAlg.setValue(ByteUtil.short2ushort(attrBuf.getShort()));

                        attrMap.put(attrId, stoSampleAlg);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_METRIC_STORE_USAGE_CNT:
                    case Nomenclature.Attribute.MDC_ATTR_METRIC_STORE_CAPAC_CNT:

                        INTU32 intu32 = new INTU32(attrId);
                        intu32.setValue(ByteUtil.int2uint(attrBuf.getInt()));

                        attrMap.put(attrId, intu32);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_PM_STORE_LABEL_STRING:

                        OCTECSTRING str = new OCTECSTRING(attrId);
                        str.setLength(attrValueLength);
                        str.setValue(ByteUtil.getString(attrBuf, attrValueLength));

                        attrMap.put(attrId, str);

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_SOURCE_HANDLE_REF:

                        HANDLE sourceHandleRef = new HANDLE(attrId);
                        sourceHandleRef.setValue(ByteUtil.short2ushort(attrBuf.getShort()));

                        attrMap.put(attrId, sourceHandleRef);

                    default:
                        break;
                }
            }

            confMap.put(HealthcareConstants.ConfigurationKey.ATTRIBUTE, attrMap);

            configMap.put(objHandle, confMap);
        }

        //        Log.d(TAG, configMap.toString());
    }

    /**
     * mdsObject
     */
    private int mdsObject;

    /**
     * eventTime
     */
    private int eventTime;

    /**
     * eventType
     */
    private final int eventType = Nomenclature.Action.MDC_NOTI_CONFIG;

    /**
     * eventInfoLength
     */
    private int eventInfoLength;

    /**
     * configReportId
     */
    private int configReportId;

    /**
     * configObjListCount
     */
    private int configObjListCount;

    /**
     * configObjListLength
     */
    private int configObjListLength;

    /**
     * configResult
     */
    private int configResult = 0x0000;

    /**
     * eventReplyInfoLength
     */
    private final int eventReplyInfoLength = 0x0004;

    /**
     * 
     */
    private final Map<Integer, Map<String, Object>> configMap = new HashMap<Integer, Map<String, Object>>();

    /**
     * mdsObject getter
     * 
     * @return mdsObject
     */
    public int getMDSObject() {
        return mdsObject;
    }

    /**
     * mdsObject setter
     * 
     * @param mdsObject - the mdsObject to set
     */
    public void setMDSObject(int mdsObject) {
        this.mdsObject = mdsObject;
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
     * configReportId getter
     * 
     * @return configReportId
     */
    public int getConfigReportId() {
        return configReportId;
    }

    /**
     * configReportId setter
     * 
     * @param configReportId - the configReportId to set
     */
    public void setConfigReportId(int configReportId) {
        this.configReportId = configReportId;
    }

    /**
     * configObjListCount getter
     * 
     * @return configObjListCount
     */
    public int getConfigObjListCount() {
        return configObjListCount;
    }

    /**
     * configObjListCount setter
     * 
     * @param configObjListCount - the configObjListCount to set
     */
    public void setConfigObjListCount(short configObjListCount) {
        this.configObjListCount = configObjListCount;
    }

    /**
     * configObjListLength getter
     * 
     * @return configObjListLength
     */
    public int getConfigObjListLength() {
        return configObjListLength;
    }

    /**
     * configObjListLength setter
     * 
     * @param configObjListLength - the configObjListLength to set
     */
    public void setConfigObjListLength(int configObjListLength) {
        this.configObjListLength = configObjListLength;
    }

    /**
     * configMap getter
     * 
     * @return configMap
     */
    public Map<Integer, Map<String, Object>> getConfigMap() {
        return configMap;
    }

    /**
     * objHandle 에 해당하는 confMap 반환
     * 
     * @param objHandle
     * @return
     */
    public Map<String, Object> getConfigMap(int objHandle) {
        return configMap.get(objHandle);
    }

    /**
     * objHandle 에 해당하는 confMap 설정
     * 
     * @param objHandle
     * @param confMap
     */
    public void setConfigMap(int objHandle, Map<String, Object> confMap) {
        this.configMap.put(objHandle, confMap);
    }

    /**
     * configResult setter
     * 
     * @param configResult - the configResult to set
     */
    public void setConfigResult(int configResult) {
        this.configResult = configResult;
    }

    /**
     * 
     */
    @Override
    public byte[] getBytes() {

        byte[] arr = super.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(arr.length + getChoiceLength());

        buffer.put(arr);
        buffer.putShort(ByteUtil.ushort2short(mdsObject));
        buffer.putInt(eventTime);
        buffer.putShort(ByteUtil.ushort2short(eventType));
        buffer.putShort(ByteUtil.ushort2short(eventReplyInfoLength));
        buffer.putShort(ByteUtil.ushort2short(configReportId));
        buffer.putShort(ByteUtil.ushort2short(configResult));

        return buffer.array();
    }

    /**
     * 
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        sb.append("obj-handle = ").append(mdsObject).append(" (MDS Object)\n");
        sb.append("event-time = ").append(Integer.toHexString(eventTime)).append("\n");

        sb.append("event-type = MDC_NOTI_CONFIG\n");
        sb.append("event-info.length = ").append(eventInfoLength).append("\n");
        sb.append("config-report-id = ").append(Integer.toHexString(configReportId)).append("\n");
        sb.append("config-obj-list.count = ").append(configObjListCount).append("\n");
        sb.append("config-obj-list.length = ").append(configObjListLength).append("\n");

        Iterator<Integer> itr = configMap.keySet().iterator();
        while (itr.hasNext()) {

            int objHandle = itr.next();

            Map<String, Object> confMap = configMap.get(objHandle);

            int objClass = (Integer)confMap.get(HealthcareConstants.ConfigurationKey.OBJ_CLASS);

            // obj-class
            sb.append("####################################################\n");
            sb.append("obj-class = ").append(ManagerUtil.getObjectInfraName(objClass)).append("\n");

            // obj-handle
            sb.append("obj-handle = ").append(objHandle).append("\n");

            @SuppressWarnings("unchecked")
            Map<Integer, Attribute> attrMap = (Map<Integer, Attribute>)confMap.get(HealthcareConstants.ConfigurationKey.ATTRIBUTE);

            sb.append("attributes.count = ").append(attrMap.size()).append("\n");

            Iterator<Integer> it = attrMap.keySet().iterator();
            while (it.hasNext()) {

                int attrId = it.next();
                switch (attrId) {
                    case Nomenclature.Attribute.MDC_ATTR_ID_TYPE:

                        TYPE type = (TYPE)attrMap.get(attrId);
                        sb.append(type.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_METRIC_STRUCT_SMALL:

                        MetricStructureSmall metricStructureSmall = (MetricStructureSmall)attrMap.get(attrId);
                        sb.append(metricStructureSmall.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_ID_PHYSIO_LIST:

                        MetricIdList metricIdList = (MetricIdList)attrMap.get(attrId);
                        sb.append(metricIdList.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_UNIT_CODE:

                        OIDType oidType = (OIDType)attrMap.get(attrId);
                        sb.append(oidType.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_NU_ACCUR_MSMT:

                        // TODO : FLOAT-Type 만 있음. 해석을 어떻게 해야하나...측정의 편차값이라고 하는 듯 하며, optional value 인 듯
                        FLOATType floatType = (FLOATType)attrMap.get(attrId);
                        sb.append(floatType.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_ATTRIBUTE_VAL_MAP:

                        AttrValMap attrValMap = (AttrValMap)attrMap.get(attrId);
                        sb.append(attrValMap.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_SUPPLEMENTAL_TYPES:

                        SupplementalTypeList supplementalTypeList = (SupplementalTypeList)attrMap.get(attrId);
                        sb.append(supplementalTypeList.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_OP_STAT:

                        OperationalState operationalState = (OperationalState)attrMap.get(attrId);
                        sb.append(operationalState.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_NUM_SEG:

                        INTU16 intu16 = (INTU16)attrMap.get(attrId);
                        sb.append(intu16.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_PM_STORE_CAPAB:

                        PMStoreCapab pmStoreCapab = (PMStoreCapab)attrMap.get(attrId);
                        sb.append(pmStoreCapab.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_METRIC_STORE_SAMPLE_ALG:

                        StoSampleAlg stoSampleAlg = (StoSampleAlg)attrMap.get(attrId);
                        sb.append(stoSampleAlg.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_METRIC_STORE_USAGE_CNT:
                    case Nomenclature.Attribute.MDC_ATTR_METRIC_STORE_CAPAC_CNT:

                        INTU32 intu32 = (INTU32)attrMap.get(attrId);
                        sb.append(intu32.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_PM_STORE_LABEL_STRING:

                        OCTECSTRING str = (OCTECSTRING)attrMap.get(attrId);
                        sb.append(str.toString());

                        break;

                    case Nomenclature.Attribute.MDC_ATTR_SOURCE_HANDLE_REF:

                        HANDLE sourceHandleRef = (HANDLE)attrMap.get(attrId);
                        sb.append(sourceHandleRef.toString());

                        break;

                    default:
                        break;
                }
            }
        }

        return sb.toString();
    }
}
