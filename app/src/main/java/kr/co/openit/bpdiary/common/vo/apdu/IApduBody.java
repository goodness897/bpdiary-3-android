package kr.co.openit.bpdiary.common.vo.apdu;

/**
 * IApduBody 인터페이스
 */
public interface IApduBody {

    /**
     * byte 데이터를 생성하여 반환하는 메소드 (구현)
     * 
     * @return byte 배열
     */
    byte[] getBytes();

}
