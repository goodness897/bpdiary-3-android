package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.utils.ByteUtil;

/**
 * Abort
 */
public class AbrtApdu extends Apdu implements IApduBody {

    /**
     * 생성자
     */
    public AbrtApdu() {
        // default constructor
        setChoiceType(HealthcareConstants.ApduChoiceType.ASSOCIATION_ABORT);
        setChoiceTypeLength(0x0002);
    }

    /**
     * 생성자
     * 
     * @param reason
     */
    public AbrtApdu(int reason) {
        setChoiceType(HealthcareConstants.ApduChoiceType.ASSOCIATION_ABORT);
        setChoiceTypeLength(0x0002);

        this.reason = reason;
    }

    /**
     * reason - noraml
     */
    private int reason = HealthcareConstants.AbortReason.UNDEFINED;

    /**
     * reason 반환
     * 
     * @return reason
     */
    public int getReason() {
        return reason;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(getChoiceTypeLength() + 4);

        buffer.put(super.getBytes());
        buffer.putShort(ByteUtil.ushort2short(reason));

        return buffer.array();
    }

    @Override
    public String toString() {
        return null;
    }

}
