package kr.co.openit.bpdiary.common.model.weightscale;

import com.google.gson.annotations.SerializedName;

/**
 * 체성분 데이터 수집에 대한 모델 클래스로 서버로 데이터를 요청 하기 위해 JsonRequest를 상속 받아 만들어졌으며, 서버로 전송하기
 * 위한 Request 데이터 클래스와, 서버에서 전달받은 Response 데이터 클래스를 가지고 있다.
 * 
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @version
 * @since
 * @created 2012. 5. 14.
 */
public class MBodyComposition {

	/**
	 * 서비스 api 이름
	 */
	public String serviceAPIName = "COLLECT_BLUETHOOTH_GATT_BodyComposition";

	/**
	 * 서버 request 전송 여부
	 */
	public boolean save = false;

	/**
	 * Request 객체
	 */
	public RQ bodyCompositionRq = null;

	/**
	 * Response 객체
	 */
	public RS bodyCompositionRs = null;

	/**
	 * 생성시 입력되는 파라미터 값으로 서버로 전송하기 위한 json request 데이터를 생성
	 * 
	 * @param height
	 *            - 키
	 * @param heightUnit
	 *            - 키 단위 : cm, ft
	 * @param weight
	 *            - 체중
	 * @param weightUnit
	 *            - 체중 단위 : kg, lb
	 * @param fat
	 *            - 체지방율
	 * @param deviceSerialNo
	 *            - 측정기기 일련번호
	 * @param deviceModelNm
	 *            - 측정기기 모델명
	 * @param deviceManufacturer
	 *            - 측정기기 제조사
	 * @param methodCd
	 *            - 수집 방식 코드
	 * @param collectDt
	 *            - 측정 데이터 수집일시(수집 단말기 시간) : yyyyMMddHHmmss
	 */
	public MBodyComposition(String height,
							String heightUnit,
							String weight,
							String weightUnit,
							String fat,
							String deviceSerialNo,
							String deviceModelNm,
							String deviceManufacturer,
							String methodCd,
							String collectDt) {
		this.bodyCompositionRq = new RQ(height, heightUnit, weight, weightUnit, fat, deviceSerialNo, deviceModelNm, deviceManufacturer, methodCd,
				collectDt);
	}

	/**
	 * 체성분 데이터 수집 Request
	 * 
	 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
	 * @version
	 * @since
	 * @created 2012. 5. 14.
	 */
	public static class RQ {

		/**
		 * Health Up SDK 내부에서 관리하는 데이터 값들과 서버전송에 필요한 데이터값을 파라미터로 받아 Gson.jar를 통해
		 * 서버로 전송할 JSON 데이터를 생성
		 * 
		 * @param height
		 *            - 키
		 * @param heightUnit
		 *            - 키 단위 : cm, ft
		 * @param weight
		 *            - 체중
		 * @param weightUnit
		 *            - 체중 단위 : kg, lb
		 * @param fat
		 *            - 체지방율
		 * @param deviceSerialNo
		 *            - 측정기기 일련번호
		 * @param deviceModelNm
		 *            - 측정기기 모델명
		 * @param deviceManufacturer
		 *            - 측정기기 제조사
		 * @param methodCd
		 *            - 수집 방식 코드
		 * @param collectDt
		 *            - 측정 데이터 수집일시(수집 단말기 시간) : yyyyMMddHHmmss
		 */
		public RQ(	String height,
					String heightUnit,
					String weight,
					String weightUnit,
					String fat,
					String deviceSerialNo,
					String deviceModelNm,
					String deviceManufacturer,
					String methodCd,
					String collectDt) {
			this.height = height;
			this.heightUnit = heightUnit;
			this.weight = weight;
			this.weightUnit = weightUnit;
			this.fat = fat;
			this.deviceSerialNo = deviceSerialNo;
			this.deviceModelNm = deviceModelNm;
			this.deviceManufacturer = deviceManufacturer;
			this.methodCd = methodCd;
			this.collectDt = collectDt;
		}

		/**
		 * accessToken 값
		 */
		@SerializedName("accessToken")
		private String accessToken;

		/**
		 * 서비스 ID
		 */
		@SerializedName("svcId")
		public String svcId;

		/**
		 * APP ID
		 */
		@SerializedName("appId")
		public String appId;

		/**
		 * timeZoneId
		 */
		@SerializedName("timeZoneId")
		public String timeZoneId;

		/**
		 * 키
		 */
		@SerializedName("height")
		public String height;

		/**
		 * 키 단위 : cm, ft
		 */
		@SerializedName("heightUnit")
		public String heightUnit;

		/**
		 * 체중
		 */
		@SerializedName("weight")
		public String weight;

		/**
		 * 체중 단위 : kg, lb
		 */
		@SerializedName("weightUnit")
		public String weightUnit;

		/**
		 * 체지방율
		 */
		@SerializedName("fat")
		public String fat;

		/**
		 * 측정기기 일련번호
		 */
		@SerializedName("deviceSerialNo")
		public String deviceSerialNo;

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
		 * 수집 단말기 일련번호
		 */
		@SerializedName("mobileSerialNo")
		public String mobileSerialNo;

		/**
		 * 측정 데이터 수집일시(수집 단말기 시간) : yyyyMMddHHmmss
		 */
		@SerializedName("collectDt")
		public String collectDt;
	}

	/**
	 * 체성분 데이터 수집 Response
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
		public BodyCompositionData data;

		/**
		 * Response에서 data를 Gson.jar로 분석하여 들어있는 데이터 추출하기 위한 클래스
		 * 
		 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit
		 *         Inc.</a>
		 * @version
		 * @since
		 * @created 2012. 5. 14.
		 */
		public class BodyCompositionData {

			/**
			 * 체성분 수집 데이터 번호
			 */
			@SerializedName("colSeq")
			public String colSeq;

			/**
			 * 키
			 */
			@SerializedName("height")
			public String height;

			/**
			 * 키 단위 : cm, ft
			 */
			@SerializedName("heightUnit")
			public String heightUnit;

			/**
			 * 체중
			 */
			@SerializedName("weight")
			public String weight;

			/**
			 * 체중 단위 : kg, lb
			 */
			@SerializedName("weightUnit")
			public String weightUnit;

			/**
			 * 체질량 지수
			 */
			@SerializedName("bmi")
			public String bmi;

			/**
			 * 체지방율
			 */
			@SerializedName("fat")
			public String fat;

			/**
			 * 기준 코드
			 */
			@SerializedName("standardCd")
			public String standardCd;

			/**
			 * 체중 결과 코드 : 정상, 저체중, 비만 등
			 */
			@SerializedName("weightResultCd")
			public String weightResultCd;

			/**
			 * 체중 결과 : 정상, 저체중, 비만 등
			 */
			@SerializedName("weightResult")
			public String weightResult;

			/**
			 * 체질량 지수 결과 코드 : 정상, 저체중, 비만 등
			 */
			@SerializedName("bmiResultCd")
			public String bmiResultCd;

			/**
			 * 체질량 지수 결과 : 정상, 저체중, 비만 등
			 */
			@SerializedName("bmiResult")
			public String bmiResult;

			/**
			 * 체지방율 결과 코드 : 정상, 저체중, 비만 등
			 */
			@SerializedName("fatResultCd")
			public String fatResultCd;

			/**
			 * 체지방율 결과 : 정상, 저체중, 비만 등
			 */
			@SerializedName("fatResult")
			public String fatResult;

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
			 * 해당 서비스 여부 : Y, N
			 */
			@SerializedName("scvYn")
			public String scvYn;

			/**
			 * 등록 일시 : yyyyMMddHHmmss
			 */
			@SerializedName("insDt")
			public String insDt;
		}
	} // End of RS
}
