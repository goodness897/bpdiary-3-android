package kr.co.openit.bpdiary.common.constants;

/**
 * HealthUp에서 사용하는 Constants
 *
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @created 2012. 5. 14.
 */
public final class BleConstants {

    /**
     * HealthUpConstants 클래스 생성자
     */
    private BleConstants() {

    }

    /**
     * ================================ Type ================================================
     */

    /**
     * Bluetooth Low Energy Device
     */
    public static final String TYPE_BLE = "Bluetooth Low Energy";

    /**
     * ================================ Measuring Device Type ===========================================
     */

    /**
     * 측정 디바이스 타입 : 혈압계
     */
    public static final String MEASUR_DEVICE_TYPE_BLOOD_PRESSURE = "BloodPressure";

    /**
     * 측정 디바이스 타입 : 체중계
     */
    public static final String MEASUR_DEVICE_TYPE_WEIGHT_SCALE = "WeightScale";

    /**
     * ==============================================================================================
     */

    /**
     * ================================ 혈압계 목록 ===========================================
     */

    /**
     * A&D UA-651BLE 제품
     */
    public static final String UA_651BLE = "A&D UA-651BLE";


    /**
     * 체중계 목록
     */

    public static final String MI_SCALE = "Mi Scale";
}
