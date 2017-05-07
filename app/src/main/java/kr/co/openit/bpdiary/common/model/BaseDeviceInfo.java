package kr.co.openit.bpdiary.common.model;

/**
 * Health Up SDK에서 관리하는 디바이스 정보를 확인하기 위한 기본 인터페이스
 * 
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2012. 5. 14.
 */
public interface BaseDeviceInfo {

	/**
	 * 디바이스의 이름
	 * 
	 * @return 디바이스의 이름
	 */
	public String getName();

	/**
	 * 디바이스 페어링 여부
	 * 
	 * @return 페어링 여부
	 */
	public boolean isPairing();
	
	public String getDeviceType();
}
