package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ByteUtil;

/**
 * SegmentGetInfoPrstApdu VO 정의
 */
public class SegmSelection extends PrstApdu implements IApduBody {

    /**
     * 생성자
     */
    public SegmSelection(int objHandle) {
        // default constructor
        setChoiceTypeLength(0x0014);
        setOctecStringLength(0x0012);

        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_ACTION);

        setChoiceLength(0x000c);

        this.objHandle = ByteUtil.ushort2short(objHandle);
    }

    /**
     * obj handle
     */
    private final short objHandle;

    /**
     * action type
     */
    private final short actionType = Nomenclature.Action.MDC_ACT_SEG_GET_INFO;

    /**
     * action-info-args.length
     */
    private final short actionInfoArgsLength = (short)0x0006;

    /**
     * all-segments
     */
    private short allSegments = (short)0x0001;

    /**
     * segment-id-list
     */
    private short segmIdList = (short)0x0002;

    /**
     * abs-time-range
     */
    private short absTimeRange = (short)0x0000;

    /**
     * all-segments getter
     * 
     * @return all-segments
     */
    public short getAllSegments() {
        return allSegments;
    }

    /**
     * all-segments setter
     * 
     * @param allSegments - the allSegments to set
     */
    public void setAllSegments(short allSegments) {
        this.allSegments = allSegments;
    }

    /**
     * segment-id-list getter
     * 
     * @return segment-id-list
     */
    public short getSegmIdList() {
        return segmIdList;
    }

    /**
     * segment-id-list setter
     * 
     * @param segmIdList - the segmIdList to set
     */
    public void setSegmIdList(short segmIdList) {
        this.segmIdList = segmIdList;
    }

    /**
     * abs-time-range setter
     * 
     * @return abs-time-range
     */
    public short getAbsTimeRange() {
        return absTimeRange;
    }

    /**
     * abs-time-range getter
     * 
     * @param absTimeRange - the absTimeRange to set
     */
    public void setAbsTimeRange(short absTimeRange) {
        this.absTimeRange = absTimeRange;
    }

    /**
     * getBytes override
     */
    @Override
    public byte[] getBytes() {
        byte[] arr = super.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(arr.length + getChoiceLength());

        buffer.put(arr);
        buffer.putShort(objHandle);
        buffer.putShort(actionType);
        buffer.putShort(actionInfoArgsLength);

        buffer.putShort(allSegments);
        buffer.putShort(segmIdList);
        buffer.putShort(absTimeRange);

        return buffer.array();
    }

    /**
     * toString override
     */
    @Override
    public String toString() {
        return null;
    }

}
