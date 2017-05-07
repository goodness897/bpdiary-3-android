package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.utils.ByteUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * TrigSegmDataXferReq VO 정의
 */
public class TrigSegmDataXferRsp extends PrstApdu implements IApduBody {

    /**
     * 생성자
     * 
     * @param buffer
     */
    public TrigSegmDataXferRsp(ByteBuffer buffer) {

        buffer.position(0);

        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_CONFIRMED_ACTION);
        actionInfoArgsLength = ByteUtil.short2ushort(buffer.getShort()); // action-info-args.length

        segInstNo = ByteUtil.short2ushort(buffer.getShort()); // segInstNo

        // tsxr-successful(0), tsxr-fail-no-such-segment(1), tsxr-fail-clear-in-process(2)
        // tsxr-fail-segm-empty(3), tsxr-fail-not-otherwise-specified(512)
        trigSegmXferRsp = ByteUtil.short2ushort(buffer.getShort()); // trigSegmXferRsp
    }

    /**
     * obj handle
     */
    private int objHandle;

    /**
     * 
     */
    private int actionType;

    /**
     * action-info-args.length
     */
    private final int actionInfoArgsLength;

    /**
     * segInstNo
     */
    private final int segInstNo;

    /**
     * trigSegmXferRsp
     */
    private final int trigSegmXferRsp;

    /**
     * objHandle setter
     * 
     * @param objHandle
     */
    public void setObjHandle(int objHandle) {
        this.objHandle = objHandle;
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
     * getBytes override
     */
    @Override
    public byte[] getBytes() {
        return null;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        sb.append("obj-handle = ").append(objHandle).append("\n");
        sb.append("action-type = ").append(ManagerUtil.getActionName(actionType)).append("\n");
        sb.append("action-info-args-length = ").append(actionInfoArgsLength).append("\n");
        sb.append("seg-inst-no = ").append(segInstNo).append("\n");
        sb.append("trig-segm-xfer-rsp = ").append(trigSegmXferRsp).append("\n");

        return sb.toString();
    }
}
