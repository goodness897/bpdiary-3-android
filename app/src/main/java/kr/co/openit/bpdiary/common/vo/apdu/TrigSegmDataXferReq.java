package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ByteUtil;

/**
 * TrigSegmDataXferReq VO 정의
 */
public class TrigSegmDataXferReq extends PrstApdu implements IApduBody {

    /**
     * 생성자
     */
    public TrigSegmDataXferReq(int objHandle, int segInstNo) {

        // default constructor
        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_ACTION);

        setChoiceTypeLength((short)0x0010);
        setOctecStringLength((short)0x000e);

        setChoiceLength((short)0x0008);

        this.objHandle = ByteUtil.ushort2short(objHandle);
        this.segInstNo = ByteUtil.ushort2short(segInstNo);
    }

    /**
     * obj handle
     */
    private final short objHandle;

    /**
     * action type
     */
    private final short actionType = Nomenclature.Action.MDC_ACT_SEG_TRIG_XFER;

    /**
     * action-info-args.length
     */
    private final short actionInfoArgsLength = (short)0x0002;

    /**
     * 
     */
    private final short segInstNo;

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

        buffer.putShort(segInstNo);

        return buffer.array();
    }

    @Override
    public String toString() {
        return null;
    }
}
