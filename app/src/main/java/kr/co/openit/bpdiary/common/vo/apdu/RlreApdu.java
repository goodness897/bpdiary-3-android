package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;

/**
 * association release response VO
 */
public class RlreApdu extends Apdu implements IApduBody {

    /**
     * 생성자
     */
    public RlreApdu() {
        // default constructor
        setChoiceType(HealthcareConstants.ApduChoiceType.ASSOCIATION_RELEASE_RESPONSE);
        setChoiceTypeLength((short)0x0002);
    }

    /**
     * 생성자
     * 
     * @param reason
     */
    public RlreApdu(short reason) {
        setChoiceType(HealthcareConstants.ApduChoiceType.ASSOCIATION_RELEASE_RESPONSE);
        setChoiceTypeLength((short)0x0002);

        this.reason = reason;
    }

    /**
     * reason - noraml
     */
    private short reason = (short)0x0000;

    /**
     * reason 반환
     * 
     * @return reason
     */
    public short getReason() {
        return reason;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(getChoiceTypeLength() + 4);

        buffer.put(super.getBytes());
        buffer.putShort(reason);

        return buffer.array();
    }

    @Override
    public String toString() {
        return null;
    }

}
