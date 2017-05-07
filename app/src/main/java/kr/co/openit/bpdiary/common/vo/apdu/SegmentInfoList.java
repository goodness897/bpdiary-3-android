package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ByteUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.common.vo.attr.AttrValMap;
import kr.co.openit.bpdiary.common.vo.attr.PmSegmentEntryMap;
import kr.co.openit.bpdiary.common.vo.attr.SegmEntryElem;
import kr.co.openit.bpdiary.common.vo.attr.TYPE;

/**
 * SegmentGetInfoPrstApdu VO 정의
 */
public class SegmentInfoList extends PrstApdu implements IApduBody {

    /**
     * 생성자
     * 
     * @param buffer
     */
    public SegmentInfoList(ByteBuffer buffer) {

        buffer.position(0);

        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_ACTION);

        actionInfoArgsLength = ByteUtil.short2ushort(buffer.getShort()); // action-info-args.length

        buffer.getShort(); // SegmentInfo.seg-inst-no
        buffer.getShort(); // SegmentInfo.length
        buffer.getShort(); // SegmentInfo.attributes[0].value

        int attrsCount = ByteUtil.short2ushort(buffer.getShort()); // SegmentInfo.seg-info.attributes.count
        ByteUtil.short2ushort(buffer.getShort()); // SegmentInfo.seg-info.attributes.length

        //        Log.d(TAG, "attrsCount : " + attrsCount + ", attrsLength : " + attrsLength);

        for (int i = 0; i < attrsCount; i++) {

            int attrId = ByteUtil.short2ushort(buffer.getShort()); // attribute-id
            int attrValueLength = ByteUtil.short2ushort(buffer.getShort()); // attribute-value.length

            byte[] attrArr = new byte[attrValueLength];
            buffer.get(attrArr, 0, attrValueLength);

            ByteBuffer attrBuf = ByteBuffer.allocate(attrArr.length).put(attrArr);
            attrBuf.position(0);

            if (attrId == Nomenclature.Attribute.MDC_ATTR_ID_INSTNO) {

                segInstNo = ByteUtil.short2ushort(attrBuf.getShort());
                attrMap.put(attrId, segInstNo);

                //                Log.i(TAG, HealthUpUtil.getAttributeName(attrId) + " : " + segInstNo);

            } else if (attrId == Nomenclature.Attribute.MDC_ATTR_PM_SEG_MAP) {

                PmSegmentEntryMap entryMap = new PmSegmentEntryMap(attrId);
                entryMap.setSegmEntryHeader(ByteUtil.short2ushort(attrBuf.getShort())); // SegmEntryHeader

                int count = attrBuf.getShort(); // SegmEntryElemList.count
                attrBuf.getShort(); // SegmEntryElemList.length

                for (int j = 0; j < count; j++) {

                    SegmEntryElem segmEntryElem = new SegmEntryElem();
                    segmEntryElem.setClassId(ByteUtil.short2ushort(attrBuf.getShort())); // SegmEntryElem.class-id

                    TYPE metricType = new TYPE();
                    metricType.setPartition(ByteUtil.short2ushort(attrBuf.getShort())); // SegmEntryElem.metric-type.nom-partition
                    metricType.setCode(ByteUtil.short2ushort(attrBuf.getShort())); // SegmEntryElem.metric-type.code
                    segmEntryElem.setMetricType(metricType);

                    segmEntryElem.setHandle(ByteUtil.short2ushort(attrBuf.getShort())); // SegmEntryElem.handle

                    AttrValMap attrValMap = new AttrValMap();
                    attrValMap.setCount(ByteUtil.short2ushort(attrBuf.getShort())); // SegmEntryElem.attrValMap.count
                    attrValMap.setLength(ByteUtil.short2ushort(attrBuf.getShort())); // SegmEntryElem.attrValMap.length

                    for (int k = 0; k < attrValMap.getCount(); k++) {
                        attrValMap.addAttrValMapEntry(ByteUtil.short2ushort(attrBuf.getShort()),
                                                      ByteUtil.short2ushort(attrBuf.getShort()));
                    }

                    segmEntryElem.setAttrValMap(attrValMap);

                    entryMap.addSegmEntryElem(segmEntryElem);
                }

                attrMap.put(attrId, entryMap);

            } else if (attrId == Nomenclature.Attribute.MDC_ATTR_OP_STAT) {

                // disabled(0), enabled(1), notAvailable(2)
                int operationState = ByteUtil.short2ushort(attrBuf.getShort());
                attrMap.put(attrId, operationState);

                //                Log.i(TAG, HealthUpUtil.getAttributeName(attrId) + " : " + operationState);

            } else if (attrId == Nomenclature.Attribute.MDC_ATTR_PM_SEG_LABEL_STRING) {

                String segLabel = ByteUtil.getString(attrBuf, attrValueLength);
                attrMap.put(attrId, segLabel);

                //                Log.i(TAG, HealthUpUtil.getAttributeName(attrId) + " : " + segLabel);

            } else if (attrId == Nomenclature.Attribute.MDC_ATTR_TIME_START_SEG) {

                StringBuffer sb = new StringBuffer();

                sb.append(Integer.toHexString(attrBuf.getShort())).append("-");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append("-");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append(" ");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append(":");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append(":");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append(".");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2));

                attrMap.put(attrId, sb.toString());

                //                Log.i(TAG, HealthUpUtil.getAttributeName(attrId) + " : " + sb.toString()); // value

            } else if (attrId == Nomenclature.Attribute.MDC_ATTR_TIME_END_SEG) {

                StringBuffer sb = new StringBuffer();

                sb.append(Integer.toHexString(attrBuf.getShort())).append("-");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append("-");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append(" ");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append(":");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append(":");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2))
                  .append(".");
                sb.append(ManagerUtil.leftPadding(Integer.toHexString(ByteUtil.byte2uchar(attrBuf.get())), 2));

                attrMap.put(attrId, sb.toString());

                //                Log.i(TAG, HealthUpUtil.getAttributeName(attrId) + " : " + sb.toString()); // value

            } else if (attrId == Nomenclature.Attribute.MDC_ATTR_SEG_USAGE_CNT) {

                long segUsageCnt = ByteUtil.int2uint(attrBuf.getInt());
                attrMap.put(attrId, segUsageCnt);

                //                Log.i(TAG, HealthUpUtil.getAttributeName(attrId) + " : " + segUsageCnt);

            } else if (attrId == Nomenclature.Attribute.MDC_ATTR_TRANSFER_TIMEOUT) {

                long transferTimeout = ByteUtil.int2uint(attrBuf.getInt());
                attrMap.put(attrId, transferTimeout);

                //                Log.i(TAG, HealthUpUtil.getAttributeName(attrId) + " : " + transferTimeout);
            }
        }
    }

    /**
     * obj handle
     */
    private int objHandle;

    /**
     * action type
     */
    private int actionType;

    /**
     * action-info-args.length
     */
    private final int actionInfoArgsLength;

    /**
     * segment instance number
     */
    private int segInstNo;

    /**
     * attribute 가 저장되어있는 map
     */
    private final Map<Integer, Object> attrMap = new HashMap<Integer, Object>();

    /**
     * objHandle setter
     * 
     * @param objHandle
     */
    public void setObjHandle(int objHandle) {
        this.objHandle = objHandle;
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
     * actionType setter
     * 
     * @param actionType
     */
    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    /**
     * segInstNo getter
     * 
     * @return segInstNo
     */
    public int getSegInstNo() {
        return segInstNo;
    }

    /**
     * attrId 에 해당하는 속성 반환
     * 
     * @param attrId
     * @return
     */
    public Object getAttr(int attrId) {
        return attrMap.get(attrId);
    }

    /**
     * getBytes override
     */
    @Override
    public byte[] getBytes() {
        return null;
    }

    /**
     * toString override
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        sb.append("obj-handle = ").append(objHandle).append("\n");
        sb.append("action-type = ").append(ManagerUtil.getActionName(actionType)).append("\n");
        sb.append("action-info-args = ").append(actionInfoArgsLength).append("\n");
        sb.append("SegmentInfo.seg-inst-no = ").append(segInstNo).append("\n");
        sb.append("SegmentInfo.seg-info.attributes.count = ").append(attrMap.size()).append("\n");

        Iterator<Integer> itr = attrMap.keySet().iterator();
        while (itr.hasNext()) {
            int attrId = itr.next();
            sb.append("attribute-id = ").append(ManagerUtil.getAttributeName(attrId)).append("\n");
            sb.append(attrMap.get(attrId)).append("\n");
        }

        return sb.toString();
    }
}
