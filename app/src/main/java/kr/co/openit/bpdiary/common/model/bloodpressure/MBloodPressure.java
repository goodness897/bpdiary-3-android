package kr.co.openit.bpdiary.common.model.bloodpressure;

import com.google.gson.annotations.SerializedName;

/**
 * 혈압 데이터 수집에 대한 모델 클래스로 서버로 데이터를 요청 하기 위해 JsonRequest를 상속 받아 만들어졌으며, 서버로 전송하기 위한 Request 데이터 클래스와, 서버에서 전달받은 Response
 * 데이터 클래스를 가지고 있다.
 * 
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2012. 5. 14.
 */
public class MBloodPressure {

    /**
     * 서비스 api 이름
     */
    public String serviceAPIName = "COLLECT_BLUETHOOTH_GATT_BloodPressure";

    /**
     * 서버 request 전송 여부
     */
    public boolean save = false;

    /**
     * Request 객체
     */
    public RQ bloodPressureRq = null;

    /**
     * Response 객체
     */
    public RS bloodPressureRs = null;

    /**
     * 생성시 입력되는 파라미터 값으로 서버로 전송하기 위한 json request 데이터를 생성
     * 
     * @param sys - 수축기 혈압 데이터 값 : mmHg
     * @param dia - 이완기 혈압 데이터 값 : mmHg
     * @param mean - 평균 혈압 데이터 값 : mmHg
     * @param pulse - 심장박동수 데이터 값 : bpm
     * @param collectDt - 측정 데이터 수집일시(수집 단말기 시간) : yyyyMMddHHmmss
     */
    public MBloodPressure(String sys, String dia, String mean, String pulse, String collectDt) {
        // TODO Auto-generated constructor stub
        this.bloodPressureRq = new RQ(sys, dia, mean, pulse, collectDt);
    }

    /**
     * 혈압 데이터 수집 Request
     * 
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @version
     * @since
     * @created 2012. 5. 14.
     */
    public static class RQ {

        /**
         * Health Up SDK 내부에서 관리하는 데이터 값들과 서버전송에 필요한 데이터값을 파라미터로 받아 Gson.jar를 통해 서버로 전송할 JSON 데이터를 생성
         * 
         * @param sys - 수축기 혈압 데이터 값
         * @param dia - 이완기 혈압 데이터 값
         * @param mean - 평균 혈압 데이터 값
         * @param pulse - 심장박동수 데이터 값
         * @param collectDt - 측정 데이터 수집일시(수집 단말기 시간) : yyyyMMddHHmmss
         */
        public RQ(String sys, String dia, String mean, String pulse, String collectDt) {
            this.sys = sys;
            this.dia = dia;
            this.mean = mean;
            this.pulse = pulse;
            this.collectDt = collectDt;
        }

        /**
         * 수축기 혈압 데이터 값
         */
        @SerializedName("sys")
        public String sys;

        /**
         * 이완기 혈압 데이터 값
         */
        @SerializedName("dia")
        public String dia;

        /**
         * 평균 혈압 데이터 값
         */
        @SerializedName("mean")
        public String mean;

        /**
         * 심장박동 수 데이터 값
         */
        @SerializedName("pulse")
        public String pulse;

        /**
         * 측정 데이터 수집 일시(수집 단말기 시간) : yyyyMMddHHmmss
         */
        @SerializedName("collectDt")
        public String collectDt;
    }

    /**
     * 혈압 데이터 수집 Response
     * 
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @version
     * @since
     * @created 2012. 5. 14.
     */
    public static class RS {

        /**
         * Response에서 Gson.jar로 data를 파싱 응답 데이터 : Map&ltString, Object&gt 형태
         */
        @SerializedName("data")
        public BloodPressureData data;

        /**
         * Response에서 data를 Gson.jar로 분석하여 들어있는 데이터 추출하기 위한 클래스
         * 
         * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
         * @version
         * @since
         * @created 2012. 5. 14.
         */
        public class BloodPressureData {

            /**
             * 혈압 수집 데이터 번호
             */
            @SerializedName("colSeq")
            public String colSeq;

            /**
             * 수축기 혈압 : mmHg
             */
            @SerializedName("sys")
            public String sys;

            /**
             * 이완기 혈압 : mmHg
             */
            @SerializedName("dia")
            public String dia;

            /**
             * 평균 혈압 : mmHg
             */
            @SerializedName("mean")
            public String mean;

            /**
             * 심박동수 : bpm
             */
            @SerializedName("pulse")
            public String pulse;

            /**
             * 기준 코드
             */
            @SerializedName("standardCd")
            public String standardCd;

            /**
             * 결과 코드 (정상, 고혈압)
             */
            @SerializedName("resultCd")
            public String resultCd;

            /**
             * 결과(정상, 고혈압)
             */
            @SerializedName("result")
            public String result;

            /**
             * 측정기기 모델명
             */
            @SerializedName("deviceModelNm")
            public String deviceModelNm;

            /**
             * 측정기기 제조사
             */
            @SerializedName("deviceManufacturer")
            public String deviceManufacturer;

            /**
             * 수집 방식 코드
             */
            @SerializedName("methodCd")
            public String methodCd;

            /**
             * 해당 서비스 여부
             */
            @SerializedName("svcYn")
            public String svcYn;

            /**
             * 등록 일시 : yyyyMMddHHmmss
             */
            @SerializedName("insDt")
            public String insDt;

        }
    } // End of RS
}
