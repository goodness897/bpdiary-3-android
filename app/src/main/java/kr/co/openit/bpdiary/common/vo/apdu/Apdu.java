package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.utils.ByteUtil;

/**
 * Apdu 상위 VO
 */
public class Apdu {

    /**
     * debuggingr
     */
    protected final String TAG = getClass().getSimpleName();

    /**
     * CHOICE Type
     */
    private int choiceType;

    /**
     * CHOICE length
     */
    private int choiceTypeLength;

    /**
     * 생성자
     */
    public Apdu() {
        // default constructor
    }

    /**
     * CHOICE Type 반환
     * 
     * @return CHOICE Type
     */
    public int getChoiceType() {
        return choiceType;
    }

    /**
     * CHOICE Type 설정
     * 
     * @param choiceType - the choiceType to set
     */
    protected void setChoiceType(int choiceType) {
        this.choiceType = choiceType;
    }

    /**
     * CHOICE length 반환
     * 
     * @return CHOICE length
     */
    public int getChoiceTypeLength() {
        return choiceTypeLength;
    }

    /**
     * CHOICE length 설정
     * 
     * @param choiceTypeLength - the choiceTypeLength to set
     */
    protected void setChoiceTypeLength(int choiceTypeLength) {
        this.choiceTypeLength = choiceTypeLength;
    }

    /**
     * byte 데이터로 생성하여 반환
     * 
     * @return
     */
    protected byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(4);

        buffer.putShort(ByteUtil.ushort2short(choiceType));
        buffer.putShort(ByteUtil.ushort2short(choiceTypeLength));

        return buffer.array();
    }

    /**
     * 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("APDU CHOICE Type ");

        if (choiceType == HealthcareConstants.ApduChoiceType.ASSOCIATION_REQUEST) {
            sb.append("(AarqApdu)\n");
        } else if (choiceType == HealthcareConstants.ApduChoiceType.ASSOCIATION_RESPONSE) {
            sb.append("(AareApdu)\n");
        } else if (choiceType == HealthcareConstants.ApduChoiceType.ASSOCIATION_RELEASE_REQUEST) {
            sb.append("(RlrqApdu)\n");
        } else if (choiceType == HealthcareConstants.ApduChoiceType.ASSOCIATION_RELEASE_RESPONSE) {
            sb.append("(RlreApdu)\n");
        } else if (choiceType == HealthcareConstants.ApduChoiceType.PRESENTATION_APDU) {
            sb.append("(PrstApdu)\n");
        } else if (choiceType == HealthcareConstants.ApduChoiceType.ASSOCIATION_ABORT) {
            sb.append("(AbrtApdu)\n");
        }

        sb.append("CHOICE.length = ").append(choiceTypeLength).append("\n");

        return sb.toString();
    }
}
