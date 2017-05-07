package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

/**
 * association release request VO
 */
public class RlrqApdu implements IApduBody {

    /**
     * 생성자
     * 
     * @param body
     */
    public RlrqApdu(byte[] body) {
        ByteBuffer buffer = ByteBuffer.allocate(body.length).put(body);
        buffer.position(0);

        reason = buffer.getShort();
    }

    /**
     * reason - normal
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
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

}
