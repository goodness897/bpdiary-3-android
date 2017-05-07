package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.utils.ByteUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * presentation VO
 */
public class PrstApdu extends Apdu {

    /**
     * 생성자
     */
    public PrstApdu() {
        // default constructor
        setChoiceType(HealthcareConstants.ApduChoiceType.PRESENTATION_APDU);
    }

    /**
     * OCTEC STRING.length
     */
    private int octecStringLength;

    /**
     * invoke-id
     */
    private int invokeId = 0x0001;

    /**
     * CHOICE
     */
    private int choice;

    /**
     * CHOICE.length
     */
    private int choiceLength;

    /**
     * OCTEC STRING.length 반환
     * 
     * @return OCTEC STRING.length
     */
    public int getOctecStringLength() {
        return octecStringLength;
    }

    /**
     * OCTEC STRING.length 설정
     * 
     * @param octecStringLength - the octecStringLength to set
     */
    public void setOctecStringLength(int octecStringLength) {
        this.octecStringLength = octecStringLength;
        setChoiceTypeLength(octecStringLength + 2);
    }

    /**
     * invoke-id 반환
     * 
     * @return invoke-id
     */
    public int getInvokeId() {
        return invokeId;
    }

    /**
     * invoke-id 설정
     * 
     * @param invokeId - the invokeId to set
     */
    public void setInvokeId(int invokeId) {
        this.invokeId = invokeId;
    }

    /**
     * CHOICE 반환
     * 
     * @return CHOICE
     */
    public int getChoice() {
        return choice;
    }

    /**
     * CHOICE 설정
     * 
     * @param choice - the choice to set
     */
    public void setChoice(int choice) {
        this.choice = choice;
    }

    /**
     * CHOICE.length 반환
     * 
     * @return CHOICE.length
     */
    public int getChoiceLength() {
        return choiceLength;
    }

    /**
     * CHOICE.length 설정
     * 
     * @param choiceLength - the choiceLength to set
     */
    public void setChoiceLength(int choiceLength) {
        this.choiceLength = choiceLength;
    }

    @Override
    protected byte[] getBytes() {

        byte[] arr = super.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(arr.length + 8);

        buffer.put(arr);

        buffer.putShort(ByteUtil.ushort2short(octecStringLength));
        buffer.putShort(ByteUtil.ushort2short(invokeId));
        buffer.putShort(ByteUtil.ushort2short(choice));
        buffer.putShort(ByteUtil.ushort2short(choiceLength));

        return buffer.array();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("OCTET STRING.length = ").append(octecStringLength).append("\n");
        sb.append("invoke-id = ").append(Integer.toHexString(invokeId)).append("\n");
        sb.append("CHOICE ").append(ManagerUtil.getChoiceName(choice)).append("\n");
        sb.append("CHOICE.length = ").append(choiceLength).append("\n");

        return sb.toString();
    }
}
