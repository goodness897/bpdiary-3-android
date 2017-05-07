package kr.co.openit.bpdiary.common.model;

/**
 * Health Up에서 지원하는 각 디바이스들을 정보를 저장하기 위한 모델
 * 
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2012. 5. 14.
 */
public class MDevice implements BaseDeviceInfo {
	private String deviceName = "";
	private boolean pairing = false;
	private String address = "";
	private boolean duplicate = false;
	private int compareValue = 0;
	private boolean testing = false;
	private String deviceType = "";

	/**
	 * 생성자
	 */
	public MDevice() {
	}

	/**
	 * 디바이스 추가
	 * 
	 * @param deviceName
	 *            - 디바이스의 이름
	 * @param deviceType
	 *            - 디바이스의 타입
	 * @param pairing
	 *            - 페어링 여부
	 */
	public MDevice(String deviceName, String deviceType, boolean pairing) {
		this.deviceName = deviceName;
		this.deviceType = deviceType;
		this.pairing = pairing;
	}

	/**
	 * 디바이스 정보 추가
	 * 
	 * @param address
	 *            - 디바이스 주소
	 * @param compareValue
	 *            - 정렬 변수
	 */
	public void setData(String address, int compareValue) {
		this.address = address;
		this.compareValue = compareValue;
	}

	/**
	 * 디바이스의 이름
	 * 
	 * @return 디바이스의 이름
	 */
	@Override
	public String getName() {
		return deviceName;
	}

	/**
	 * 페어링 상태
	 * 
	 * @return false : 페어링 안됨(no pairing) <br>
	 *         true: 페어링 됨(pairing)
	 */
	@Override
	public boolean isPairing() {
		return pairing;
	}

	/**
	 * 디바이스의 이름
	 * 
	 * @param deviceName
	 *            - 디바이스의 이름
	 */
	public void setName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * 디바이스 어드레스
	 * 
	 * @return 디바이스 어드레스
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 디바이스 어드레스 저장
	 * 
	 * @param address
	 *            - 디바이스 어드레스
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 페어링 설정
	 * 
	 * @param pairing
	 *            - false : 페어링 안됨(no pairing) <br>
	 *            true: 페어링 됨(pairing)
	 */
	public void setPairing(boolean pairing) {
		this.pairing = pairing;
	}

	/**
	 * 디바이스 중복 체크
	 * 
	 * @return 디바이스 중복 상태
	 */
	public boolean isDuplicate() {
		return duplicate;
	}

	/**
	 * 디바이스 중복 체크
	 */
	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	/**
	 * 디바이스 리스트 정렬을 위해 사용
	 * 
	 * @return 정렬 값
	 */
	public int getCompareValue() {
		return compareValue;
	}

	/**
	 * 디바이스 리스트 정렬을 위해 사용
	 * 
	 * @param compareValue
	 *            정렬 값
	 */
	public void setCompareValue(int compareValue) {
		this.compareValue = compareValue;
	}

	/**
	 * 디바이스 테스트 제품
	 * 
	 * @return 테스트 디바이스 일경우 true
	 */
	public boolean isTesting() {
		return testing;
	}

	/**
	 * 테스트 디바이스 설정
	 * 
	 * @param testing
	 *            - 테스트 디바이스 일경우 true
	 */
	public void setTesting(boolean testing) {
		this.testing = testing;
	}

	@Override
	public String getDeviceType() {
		return deviceType;
	}
}
