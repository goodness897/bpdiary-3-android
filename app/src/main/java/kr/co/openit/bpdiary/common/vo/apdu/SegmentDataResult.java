package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ByteUtil;

/**
 * SegmentDataResult 정의
 */
public class SegmentDataResult extends PrstApdu implements IApduBody {

    /**
     * 생성자
     */
    public SegmentDataResult(int invokeId) {
        // default constructor

        setChoiceTypeLength(0x001e);
        setOctecStringLength(0x001c);

        setInvokeId(invokeId);

        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_EVENT_REPORT);
        setChoiceLength(0x0016);
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
    private final int eventInfoLength = 0x000c;

    /**
     * segment instance
     */
    private int segmInstance;

    /**
     * segment event entry index
     */
    private long segmEvtEntryIndex;

    /**
     * segment event entry count
     */
    private long segmEvtEntryCount;

    /**
     * segment event status
     */
    private int segmEvtStatus;

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
     * @return segmInstance
     */
    public int getSegmInstance() {
        return segmInstance;
    }

    /**
     * segmInstance setter
     * 
     * @param segmInstance
     */
    public void setSegmInstances(int segmInstance) {
        this.segmInstance = segmInstance;
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
     * segmEvtEntryIndex setter
     * 
     * @param segmEvtEntryIndex
     */
    public void setSegmEvtEntryIndex(long segmEvtEntryIndex) {
        this.segmEvtEntryIndex = segmEvtEntryIndex;
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
     * segmEvtEntryCount getter
     * 
     * @param segmEvtEntryCount
     */
    public void setSegmEvtEntryCount(long segmEvtEntryCount) {
        this.segmEvtEntryCount = segmEvtEntryCount;
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
     * segmEvtStatus setter
     * 
     * @param segmEvtStatus
     */
    public void setSegmEvtStatus(int segmEvtStatus) {
        this.segmEvtStatus = segmEvtStatus;
    }

    @Override
    public byte[] getBytes() {
        byte[] arr = super.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(arr.length + getChoiceLength());

        buffer.put(arr);

        buffer.putShort(ByteUtil.ushort2short(objHandle));
        buffer.putInt(eventTime);
        buffer.putShort(ByteUtil.ushort2short(eventType));
        buffer.putShort(ByteUtil.ushort2short(eventInfoLength));
        buffer.putShort(ByteUtil.ushort2short(segmInstance));
        buffer.putInt(ByteUtil.uint2int(segmEvtEntryIndex));
        buffer.putInt(ByteUtil.uint2int(segmEvtEntryCount));
        buffer.putShort(ByteUtil.ushort2short(segmEvtStatus));

        return buffer.array();
    }

    @Override
    public String toString() {
        return null;
    }
}
