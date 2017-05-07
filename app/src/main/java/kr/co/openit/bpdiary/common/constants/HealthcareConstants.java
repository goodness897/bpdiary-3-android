package kr.co.openit.bpdiary.common.constants;

/**
 * 헬스업 상수 정의
 */
public final class HealthcareConstants {

    /**
     * PAN Manager -> 측정 Device 로 전송하는 메시지 Key 정의
     */
    public final static class SendMessage {

        /**
         * Get MDS
         */
        public static final String GET_MDS = "GetMDS";

        /**
         * Set Time
         */
        public static final String SET_TIME = "SetTime";

        /**
         * Get Segment Info
         */
        public static final String GET_SEG_INFO = "GetSegInfo";

        /**
         * Segment Data Xfer Request
         */
        public static final String SEG_TRIG_XFER = "SegTrigXfer";

        /**
         * Noti Segment Data
         */
        public static final String NOTI_SEGMENT_DATA = "NotiSegData";

    }

    /**
     * 데이터 분석 결과 코드 정의
     */
    public final class DataParsingResultCode {

        /**
         * success
         */
        public static final int SUCCESS = 1000;

        /**
         * floating point / short floating point 의 special value
         */
        public static final int SPECIAL_VALUES = 1001;

        /**
         * default unit code change ...
         */
        public static final int UNIT_CODE_DO_NOT_CHANGE = 1002;

        /**
         * get mds info ...
         */
        public static final int GET_MDS_INFO = 1003;

    }

    /**
     * MDS Object 키 정의
     */
    public final class GetMDSKey {

        /**
         * type
         */
        public static final String TYPE = "type";

        /**
         * version
         */
        public static final String VERSION = "version";

        /**
         * manufacturer
         */
        public static final String MANUFACTURER = "manufacturer";

        /**
         * model-number
         */
        public static final String MODEL_NUMBER = "model-number";

        /**
         * 
         */
        public static final String SYSTEM_ID = "system-id";

        /**
         * 
         */
        public static final String DEV_CONFIG_ID = "dev-config-id";

        /**
         * 
         */
        public static final String SPEC_TYPE = "spec-type";

        /**
         * 
         */
        public static final String COMPONENT_ID = "component-id";

        /**
         * 
         */
        public static final String PROD_SPEC = "prod-spec";

        /**
         * 
         */
        public static final String PROD_SPECN = "prod-specd";

        /**
         * 
         */
        public static final String ABSOLUTE_TIME = "absolute-time";

    }

    /**
     * Configuration 키 정의
     */
    public final class ConfigurationKey {

        /**
         * obj-class
         */
        public static final String OBJ_CLASS = "obj-class";

        /**
         * obj-handle
         */
        public static final String OBJ_HANDLE = "obj-handle";

        /**
         * attributes
         */
        public static final String ATTRIBUTE = "attributes";

    }

    /**
     * Device 별 표준 Configuration 정의
     */
    public final static class StandardConfiguration {

        /**
         * 
         */
        public static final int PULSE_OXIMETER_TOTAL_LENGTH = 104;

        /**
         * 산소포화도
         */
        public static final byte[] PULSE_OXIMETER = new byte[] {/*
                                                                 * (byte)0xe7, (byte)0x00, // APDU CHOICE Type
                                                                 * (PrstApdu) (byte)0x00, (byte)0x68, // CHOICE.length =
                                                                 * 104
                                                                 */
        (byte)0x00, (byte)0x66, // OCTEC STRING.length = 102
                    (byte)0x00,
                    (byte)0x01, // invoke-id
                    (byte)0x01,
                    (byte)0x01, // CHOICE  (Remote Operation Invoke | Confirmed Event Report)
                    (byte)0x00,
                    (byte)0x60, // CHOICE.length = 96
                    (byte)0x00,
                    (byte)0x00, // obj-handle = 0 (MDS object)
                    (byte)0xff,
                    (byte)0xff,
                    (byte)0xff,
                    (byte)0xff, // event-time = 0xFFFFFFFF
                    (byte)0x0d,
                    (byte)0x1c, // event-type = MDC_NOTI_CONFIG
                    (byte)0x00,
                    (byte)0x56, // event-info.length = 86 (start of ConfigReport)
                    (byte)0x01,
                    (byte)0x90, // config-report-id
                    (byte)0x00,
                    (byte)0x02, // config-obj-list.conut = 2
                    (byte)0x00,
                    (byte)0x50, // config-obj-list.length = 80
                    (byte)0x00,
                    (byte)0x06, // obj-class = MDC_MOC_VMO_METRIC_NU
                    (byte)0x00,
                    (byte)0x01, // obj-handle = 1 (1st Measurement is SpO2)
                    (byte)0x00,
                    (byte)0x04, // attributes.count = 4
                    (byte)0x00,
                    (byte)0x20, // attributes.length = 32
                    (byte)0x09,
                    (byte)0x2f, // attribute-id = MDC_ATTR_ID_TYPE
                    (byte)0x00,
                    (byte)0x04, // attribute-value.length = 4
                    (byte)0x00,
                    (byte)0x02, // MDC_PART_SCADA
                    (byte)0x4b,
                    (byte)0xb8, // MDC_PULS_OXIM_SAT_O2
                    (byte)0x0a,
                    (byte)0x46, // attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0x40,
                    (byte)0x40, // avail-stored-data, acc-agent-init, measured
                    (byte)0x09,
                    (byte)0x96, // attibute-id = MDC_ATTR_UNIT_CODE
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0x02,
                    (byte)0x20, // MDC_DIM_PERCENT
                    (byte)0x0a,
                    (byte)0x55, // attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
                    (byte)0x00,
                    (byte)0x08, // attribute-value.length = 8
                    (byte)0x00,
                    (byte)0x01, // AttrValMap.count = 1
                    (byte)0x00,
                    (byte)0x04, // AttrValMap.length = 4
                    (byte)0x0a,
                    (byte)0x4c, // MDC_ATTR_NU_VAL_OBS_BASIC
                    (byte)0x00,
                    (byte)0x02, // value length = 2
                    (byte)0x00,
                    (byte)0x06, // obj-class = MDC_MOC_VMO_METRIC_NU
                    (byte)0x00,
                    (byte)0x0a, // obj-handle = 10 (2nd Measurement is pulse rate)
                    (byte)0x00,
                    (byte)0x04, // attributes.count = 4
                    (byte)0x00,
                    (byte)0x20, // attributes.length = 32
                    (byte)0x09,
                    (byte)0x2f, // attribute-id = MDC_ATTR_ID_TYPE
                    (byte)0x00,
                    (byte)0x04, // attribute-value.length = 4
                    (byte)0x00,
                    (byte)0x02, // MDC_PART_SCADA
                    (byte)0x48,
                    (byte)0x1a, // MDC_PULS_OXIM_PULS_RATE
                    (byte)0x0a,
                    (byte)0x46, // attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0x40,
                    (byte)0x40, // avail-stored-data, acc-agent-init, measured
                    (byte)0x09,
                    (byte)0x96, // attribute-id = MDC_ATTR_UNIT_CODE
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0x0a,
                    (byte)0xa0, // MDC_DIM_BEAT_PER_MIN
                    (byte)0x0a,
                    (byte)0x55, // attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
                    (byte)0x00,
                    (byte)0x08, // attribute-value.length = 8
                    (byte)0x00,
                    (byte)0x01, // AttrValMap.count = 1
                    (byte)0x00,
                    (byte)0x04, // AttrValMap.length = 4
                    (byte)0x0a,
                    (byte)0x4c,
                    (byte)0x00,
                    (byte)0x02 // MDC_ATTR_NU_VAL_OBS_BASIC, 2
        };

        /**
         * 
         */
        public static final int BLOOD_PRESSURE_TOTAL_LENGTH = 132;

        /**
         * 혈압계
         */
        public static final byte[] BLOOD_PRESSURE = new byte[] {/*
                                                                 * (byte)0xe7, (byte)0x00, // APDU CHOICE Type
                                                                 * (PrstApdu) (byte)0x00, (byte)0x84, // CHOICE.length =
                                                                 * 132
                                                                 */
        (byte)0x00, (byte)0x82, // OCTET STRING.length = 130
                    (byte)0x00,
                    (byte)0x01, // invoke-id (differentiates this message from any other outstanding)
                    (byte)0x01,
                    (byte)0x01, // CHOICE (Remote Operation Invoke | Confirmed Event Report)
                    (byte)0x00,
                    (byte)0x7c, // CHOICE.length = 124
                    (byte)0x00,
                    (byte)0x00, // obj-handle = 0 (MDS object)
                    (byte)0xff,
                    (byte)0xff,
                    (byte)0xff,
                    (byte)0xff, // event-time
                    (byte)0x0d,
                    (byte)0x1c, // event-type = MDC_NOTI_CONFIG
                    (byte)0x00,
                    (byte)0x72, // event-info.length = 114 (start of ConfigReport)
                    (byte)0x02,
                    (byte)0xbc, // config-report-id (Dev-Configuration-Id value)
                    (byte)0x00,
                    (byte)0x02, // config-obj-list.count = 2 Measurement objects will be “announced”
                    (byte)0x00,
                    (byte)0x6c, // config-obj-list.length = 108
                    (byte)0x00,
                    (byte)0x06, // obj-class = MDC_MOC_VMO_METRIC_NU
                    (byte)0x00,
                    (byte)0x01, // obj-handle = 1 (1st Measurement is systolic, diastolic, MAP)
                    (byte)0x00,
                    (byte)0x06, // attributes.count = 6
                    (byte)0x00,
                    (byte)0x38, // attributes.length = 56
                    (byte)0x09,
                    (byte)0x2f, // attribute-id = MDC_ATTR_ID_TYPE
                    (byte)0x00,
                    (byte)0x04, // attribute-value.length = 4
                    (byte)0x00,
                    (byte)0x02, // MDC_PART_SCADA
                    (byte)0x4a,
                    (byte)0x04, // MDC_PRESS_BLD_NONINV
                    (byte)0x0a,
                    (byte)0x46, // attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0xf0,
                    (byte)0x40, // intermittent, stored data, upd & msmt aperiodic, agent init, measured
                    (byte)0x0a,
                    (byte)0x73, // attribute-id = MDC_ATTR_METRIC_STRUCT_SMALL
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0x03,
                    (byte)0x03, // {ms-struct-compound-fix, 3}
                    (byte)0x0a,
                    (byte)0x76, // attribute-id = MDC_ATTR_ID_PHYSIO_LIST
                    (byte)0x00,
                    (byte)0x0a, // attribute-value.length = 10
                    (byte)0x00,
                    (byte)0x03, // MetricIdList.count = 3
                    (byte)0x00,
                    (byte)0x06, // MetricIdList.length = 6
                    (byte)0x4a,
                    (byte)0x05, // {MDC_PRESS_BLD_NONINV_SYS,
                    (byte)0x4a,
                    (byte)0x06, // MDC_PRESS_BLD_NONINV_DIA,
                    (byte)0x4a,
                    (byte)0x07, // MDC_PRESS_BLD_NONINV_MEAN}
                    (byte)0x09,
                    (byte)0x96, // attribute-id = MDC_ATTR_UNIT_CODE
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0x0f,
                    (byte)0x20, // MDC_DIM_MMHG
                    (byte)0x0a,
                    (byte)0x55, // attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
                    (byte)0x00,
                    (byte)0x0c, // attribute-value.length = 12
                    (byte)0x00,
                    (byte)0x02, // AttrValMap.count = 2
                    (byte)0x00,
                    (byte)0x08, // AttrValMap.length = 8
                    (byte)0x0a,
                    (byte)0x75,
                    (byte)0x00,
                    (byte)0x0a, // MDC_ATTR_NU_CMPD_VAL_OBS_BASIC | value length = 10
                    (byte)0x09,
                    (byte)0x90,
                    (byte)0x00,
                    (byte)0x08, // MDC_ATTR_TIME_STAMP_ABS | value length = 8
                    (byte)0x00,
                    (byte)0x06, // obj-class = MDC_MOC_VMO_METRIC_NU
                    (byte)0x00,
                    (byte)0x02, // obj-handle = 2 (2nd Measurement is pulse rate)
                    (byte)0x00,
                    (byte)0x04, // attributes.count = 4
                    (byte)0x00,
                    (byte)0x24, // attributes.length = 36
                    (byte)0x09,
                    (byte)0x2f, // attribute-id = MDC_ATTR_ID_TYPE
                    (byte)0x00,
                    (byte)0x04, // attribute-value.length = 4
                    (byte)0x00,
                    (byte)0x02, // MDC_PART_SCADA
                    (byte)0x48,
                    (byte)0x2a, // MDC_PULS_RATE_NON_INV
                    (byte)0x0a,
                    (byte)0x46, // attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0xf0,
                    (byte)0x40, // intermittent, stored data, upd & msmt aperiodic, agent init, measured
                    (byte)0x09,
                    (byte)0x96, // attribute-id = MDC_ATTR_UNIT_CODE
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0x0a,
                    (byte)0xa0, // MDC_DIM_BEAT_PER_MIN
                    (byte)0x0a,
                    (byte)0x55, // attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
                    (byte)0x00,
                    (byte)0x0c, // attribute-value.length = 12
                    (byte)0x00,
                    (byte)0x02, // AttrValMap.count = 2
                    (byte)0x00,
                    (byte)0x08, // AttrValMap.length = 8
                    (byte)0x0a,
                    (byte)0x4c,
                    (byte)0x00,
                    (byte)0x02, // MDC_ATTR_NU_VAL_OBS_BASIC | value length = 2
                    (byte)0x09,
                    (byte)0x90,
                    (byte)0x00,
                    (byte)0x08 // MDC_ATTR_TIME_STAMP_ABS | value length = 8
        };

        /**
         * 
         */
        public static final int WEIGHING_SCALE_TOTAL_LENGTH = 68;

        /**
         * 체중계
         */
        public static final byte[] WEIGHING_SCALE = new byte[] {/*
                                                                 * (byte)0xe7, (byte)0x00, // APDU CHOICE Type
                                                                 * (PrstApdu) (byte)0x00, (byte)0x44, // CHOICE.length =
                                                                 * 68
                                                                 */
        (byte)0x00, (byte)0x42, // OCTET STRING.length = 66
                    (byte)0x00,
                    (byte)0x01, // invoke-id (differentiates this message from any other outstanding)
                    (byte)0x01,
                    (byte)0x01, // CHOICE (Remote Operation Invoke | Confirmed Event Report)
                    (byte)0x00,
                    (byte)0x3c, // CHOICE.length = 60
                    (byte)0x00,
                    (byte)0x00, // obj-handle = 0 (MDS object)
                    (byte)0xff,
                    (byte)0xff,
                    (byte)0xff,
                    (byte)0xff, // event-time
                    (byte)0x0d,
                    (byte)0x1c, // event-type = MDC_NOTI_CONFIG
                    (byte)0x00,
                    (byte)0x32, // event-info.length = 50 (start of ConfigReport)
                    (byte)0x05,
                    (byte)0xdc, // config-report-id (Dev-Configuration-Id value)
                    (byte)0x00,
                    (byte)0x01, // config-obj-list.count = 1 Measurement object will be “announced”
                    (byte)0x00,
                    (byte)0x2c, // config-obj-list.length = 44
                    (byte)0x00,
                    (byte)0x06, // obj-class = MDC_MOC_VMO_METRIC_NU
                    (byte)0x00,
                    (byte)0x01, // obj-handle = 1 (1st Measurement is body weight)
                    (byte)0x00,
                    (byte)0x04, // attributes.count = 4
                    (byte)0x00,
                    (byte)0x24, // attributes.length = 36
                    (byte)0x09,
                    (byte)0x2f, // attribute-id = MDC_ATTR_ID_TYPE
                    (byte)0x00,
                    (byte)0x04, // attribute-value.length = 4
                    (byte)0x00,
                    (byte)0x02, // MDC_PART_SCADA
                    (byte)0xe1,
                    (byte)0x40, // MDC_MASS_BODY_ACTUAL
                    (byte)0x0a,
                    (byte)0x46, // attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0xf0,
                    (byte)0x40, // intermittent, stored data, upd & msmt aperiodic, agent init, measured
                    (byte)0x09,
                    (byte)0x96, // attribute-id = MDC_ATTR_UNIT_CODE
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0x06,
                    (byte)0xc3, // MDC_DIM_KILO_G
                    (byte)0x0a,
                    (byte)0x55, // attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
                    (byte)0x00,
                    (byte)0x0c, // attribute-value.length = 12
                    (byte)0x00,
                    (byte)0x02, // AttrValMap.count = 2
                    (byte)0x00,
                    (byte)0x08, // AttrValMap.length = 8
                    (byte)0x0a,
                    (byte)0x56,
                    (byte)0x00,
                    (byte)0x04, // MDC_ATTR_NU_VAL_OBS_SIMP | value length = 4
                    (byte)0x09,
                    (byte)0x90,
                    (byte)0x00,
                    (byte)0x08 // MDC_ATTR_TIME_STAMP_ABS | value length = 8
        };

        /**
         * 
         */
        public static final int GLUCOSE_METER_TOTAL_LENGTH = 68;

        /**
         * 혈당계
         */
        public static final byte[] GLUCOSE_METER = new byte[] {/*
                                                                * (byte)0xe7, (byte)0x00, // APDU CHOICE Type (PrstApdu)
                                                                * (byte)0x00, (byte)0x44, // CHOICE.length = 68
                                                                */
        (byte)0x00, (byte)0x42, // OCTET STRING.length = 66
                    (byte)0x00,
                    (byte)0x01, // invoke-id
                    (byte)0x01,
                    (byte)0x01, // CHOICE(Remote Operation Invoke | Confirmed Event Report)
                    (byte)0x00,
                    (byte)0x3c, // CHOICE.length = 60
                    (byte)0x00,
                    (byte)0x00, // obj-handle = 0 (MDS object)
                    (byte)0xff,
                    (byte)0xff,
                    (byte)0xff,
                    (byte)0xff, // event-time
                    (byte)0x0d,
                    (byte)0x1c, // event-type = MDC_NOTI_CONFIG
                    (byte)0x00,
                    (byte)0x32, // event-info.length = 50 (start of ConfigReport)
                    (byte)0x06,
                    (byte)0xa4, // config-report-id (Dev-Configuration-Id value)
                    (byte)0x00,
                    (byte)0x01, // config-obj-list.count = 1 Measurement object will be “announced”
                    (byte)0x00,
                    (byte)0x2c, // config-obj-list.length = 44
                    (byte)0x00,
                    (byte)0x06, // obj-class = MDC_MOC_VMO_METRIC_NU
                    (byte)0x00,
                    (byte)0x01, // obj-handle = 1 (1st Measurement is blood glucose)
                    (byte)0x00,
                    (byte)0x04, // attributes.count = 4
                    (byte)0x00,
                    (byte)0x24, // attributes.length = 36
                    (byte)0x09,
                    (byte)0x2f, // attribute-id = MDC_ATTR_ID_TYPE
                    (byte)0x00,
                    (byte)0x04, // attribute-value.length = 4
                    (byte)0x00,
                    (byte)0x02, // MDC_PART_SCADA
                    (byte)0x71,
                    (byte)0xb8, // MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD
                    (byte)0x0a,
                    (byte)0x46, // attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0xf0,
                    (byte)0x40, // intermittent, stored data, upd & msmt aperiodic, agent init, measured
                    (byte)0x09,
                    (byte)0x96, // attribute-id = MDC_ATTR_UNIT_CODE
                    (byte)0x00,
                    (byte)0x02, // attribute-value.length = 2
                    (byte)0x08,
                    (byte)0x52, // MDC_DIM_MILLI_G_PER_DL
                    (byte)0x0a,
                    (byte)0x55, // attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
                    (byte)0x00,
                    (byte)0x0c, // attribute-value.length = 12
                    (byte)0x00,
                    (byte)0x02, // AttrValMap.count = 2
                    (byte)0x00,
                    (byte)0x08, // AttrValMap.length = 8
                    (byte)0x0a,
                    (byte)0x4c,
                    (byte)0x00,
                    (byte)0x02, // MDC_ATTR_NU_VAL_OBS_BASIC | value length = 2
                    (byte)0x09,
                    (byte)0x90,
                    (byte)0x00,
                    (byte)0x08 // MDC_ATTR_TIME_STAMP_ABS | value length = 8
        };
    }

    /**
     * APDU Choice Type 정의
     */
    public final class ApduChoiceType {

        /**
         * association request (0xe200)
         */
        public static final int ASSOCIATION_REQUEST = 0xe200;

        /**
         * association response (0xe300)
         */
        public static final int ASSOCIATION_RESPONSE = 0xe300;

        /**
         * association release request (0xe400)
         */
        public static final int ASSOCIATION_RELEASE_REQUEST = 0xe400;

        /**
         * association release response (0xe500)
         */
        public static final int ASSOCIATION_RELEASE_RESPONSE = 0xe500;

        /**
         * association abort (0xe600)
         */
        public static final int ASSOCIATION_ABORT = 0xe600;

        /**
         * presentation APDU (0xe700)
         */
        public static final int PRESENTATION_APDU = 0xe700;
    }

    /**
     * CHOICE 정의
     */
    public final class Choice {

        /**
         * <pre>
         * roiv-cmip-event-report [256]             EventReportArgumentSimple, -- [0x0100]
         * roiv-cmip-confirmed-event-report [257]   EventReportArgumentSimple, -- [0x0101]
         * roiv-cmip-get [259]                      GetArgumentSimple, -- [0x0103]
         * roiv-cmip-set [260]                      SetArgumentSimple, -- [0x0104]
         * roiv-cmip-confirmed-set [261]            SetArgumentSimple, -- [0x0105]
         * roiv-cmip-action [262]                   ActionArgumentSimple, -- [0x0106]
         * roiv-cmip-confirmed-action [263]         ActionArgumentSimple, -- [0x0107]
         * rors-cmip-confirmed-event-report [513]   EventReportResultSimple, -- [0x0201]
         * rors-cmip-get [515]                      GetResultSimple, -- [0x0203]
         * rors-cmip-confirmed-set [517]            SetResultSimple, -- [0x0205]
         * rors-cmip-confirmed-action [519]         ActionResultSimple, -- [0x0207]
         * roer [768]                               ErrorResult, -- [0x0300]
         * rorj [1024]                              RejectResult -- [0x0400]
         * </pre>
         */

        /**
         * Remote Operation Invoke | Event Report
         */
        public static final int REMOTE_OPERATION_INVOKE_EVENT_REPORT = 0x0100;

        /**
         * Remote Operation Invoke | Confirmed Event Report
         */
        public static final int REMOTE_OPERATION_INVOKE_CONFIRMED_EVENT_REPORT = 0x0101;

        /**
         * Remote Operation Invoke | GET
         */
        public static final int REMOTE_OPERATION_INVOKE_GET = 0x0103;

        /**
         * Remote Operation Invoke | SET
         */
        public static final int REMOTE_OPERATION_INVOKE_SET = 0x0104;

        /**
         * Remote Operation Invoke | Confirmed SET
         */
        public static final int REMOTE_OPERATION_INVOKE_CONFIRMED_SET = 0x0105;

        /**
         * Remote Operation Invoke | Action
         */
        public static final int REMOTE_OPERATION_INVOKE_ACTION = 0x0106;

        /**
         * Remote Operation Invoke | Confirmed Action
         */
        public static final int REMOTE_OPERATION_INVOKE_CONFIRMED_ACTION = 0x0107;

        /**
         * Remote Operation Response | Confirmed Event Report
         */
        public static final int REMOTE_OPERATION_RESPONSE_CONFIRMED_EVENT_REPORT = 0x0201;

        /**
         * Remote Operation Response | GET
         */
        public static final int REMOTE_OPERATION_RESPONSE_GET = 0x0203;

        /**
         * Remote Operation Response | Confirmed Action
         */
        public static final int REMOTE_OPERATION_RESPONSE_CONFIRMED_ACTION = 0x0207;

    }

    /**
     * Association 관련 정의
     */
    public final class Association {

        /**
         * association version
         */
        public static final int ASSOCIATION_VERSION = 0x80000000;

        /**
         * data proto id
         */
        public static final short DATA_PROTO_ID = (short)0x0000;

        /**
         * data proto id 20601
         */
        public static final short DATA_PROTO_ID_20601 = (short)0x5079;

        /**
         * data proto id external
         */
        public static final short DATA_PROTO_ID_EXTERNAL = (short)0xffff;

        /**
         * protocol version
         */
        public static final int PROTOCOL_VERSION = 0x80000000;

    }

    /**
     * Encoding Rule 정의
     */
    public final class EncodingRule {

        /**
         * MDER
         */
        public static final int MDER = 0x8000;

        /**
         * XER
         */
        public static final int XER = 0x4000;

        /**
         * PER
         */
        public static final int PER = 0x2000;

        /**
         * MDER or XER
         */
        public static final int MDER_XER = 0xc000;

        /**
         * MDER or PER
         */
        public static final int MDER_PER = 0xa000;

        /**
         * XER or PER
         */
        public static final int XER_PER = 0x6000;

        /**
         * MDER or XER or PER
         */
        public static final int MDER_XER_PER = 0xe000;

    }

    /**
     * Association Request Result 정의
     */
    public final class AssociationResult {

        /**
         * accepted
         */
        public static final short ACCEPTED = (short)0x0000;

        /**
         * rejected-permanent
         */
        public static final short REJECTED_PERMANENT = (short)0x0001;

        /**
         * rejected-transient
         */
        public static final short REJECTED_TRANSIENT = (short)0x0002;

        /**
         * accepted-unknown-config
         */
        public static final short ACCEPTED_UNKNOWN_CONFIG = (short)0x0003;

        /**
         * rejected-no-common-protocol
         */
        public static final short REJECTED_NO_COMMON_PROTOCOL = (short)0x0004;

        /**
         * rejected-no-common-parameter
         */
        public static final short REJECTED_NO_COMMON_PARAMETER = (short)0x0005;

        /**
         * rejected-unknown
         */
        public static final short REJECTED_UNKNOWN = (short)0x0006;

        /**
         * rejected-unauthorized
         */
        public static final short REJECTED_UNAUTHORIZED = (short)0x0007;

        /**
         * rejected-unsupported-assoc-version
         */
        public static final short REJECTED_UNSUPPORTED_ASSOC_VERSION = (short)0x0008;

    }

    /**
     * Timeout 정의
     */
    public final class Timeout {

        /**
         * association (10sec)
         */
        public static final int ASSOCIATION = 10000;

        /**
         * configuration (10sec)
         */
        public static final int CONFIGURATION = 10000;

        /**
         * association release (3sec)
         */
        public static final int ASSOCIATION_RELEASE = 3000;

        /**
         * confirmed event report (3sec)
         */
        public static final int CONFIRMED_EVENT_REPORT = 3000;

        /**
         * confirm action (3sec)
         */
        public static final int CONFIRM_ACTION = 3000;

        /**
         * 
         */
        public static final int SEG_CONFIRM_ACTION = 30000;

        /**
         * get (3sec)
         */
        public static final int GET = 3000;

    }

    /**
     * Reason for the Abort
     */
    public final class AbortReason {

        /**
         * undefined
         */
        public static final int UNDEFINED = 0x0000;

        /**
         * buffer-overflow
         */
        public static final int BUFFER_OVERFLOW = 0x0001;

        /**
         * response-timeout
         */
        public static final int RESPONSE_TIMEOUT = 0x0002;

        /**
         * configuration-timeout
         */
        public static final int CONFIGURATION_TIMEOUT = 0x0003;

    }

    /**
     * ConfigResult
     */
    public final class ConfigResult {

        /**
         * accepted-config
         */
        public static final int ACCEPTED_CONFIG = 0x0000;

        /**
         * unsupported-config
         */
        public static final int UNSUPPORTED_CONFIG = 0x0001;

        /**
         * standard-config-unknown
         */
        public static final int STANDARD_CONFIG_UNKNOWN = 0x0002;

    }

    /**
     * device 별 standard devConfigId
     */
    public final class StandardDevConfigId {

        /**
         * Pulse oximeter
         */
        public static final int PULSE_OXIMETER_400 = 0x0190;

        /**
         * Pulse oximeter
         */
        public static final int PULSE_OXIMETER_401 = 0x0191;

        /**
         * Blood Pressure
         */
        public static final int BLOOD_PRESSURE = 0x02bc;

        /**
         * Weighing scale
         */
        public static final int WEIGHING_SCALE = 0x05dc;

        /**
         * Glucose meter
         */
        public static final int GLUCOSE_METER_1700 = 0x06a4;

        /**
         * Glucose meter
         */
        public static final int GLUCOSE_METER_1701 = 0x06a5;

        /**
         * Thermometer
         */
        public static final int THERMOMETER = 0x0320;

        /**
         * BCA
         */
        public static final int BODY_COMPOSITION_ANALYZER = 0x07d0;

        /**
         * Peak Expiratory Flow Monitor
         */
        public static final int PEFM = 0x0834;

        /**
         * Adherence monitor (7200)
         */
        public static final int ADHERENCE_MONITOR_7200 = 0x1c20;

        /**
         * Adherence monitor (7201)
         */
        public static final int ADHERENCE_MONITOR_7201 = 0x1c21;

        /**
         * Adherence monitor (7202)
         */
        public static final int ADHERENCE_MONITOR_7202 = 0x1c22;

        /**
         * Adherence monitor (7203)
         */
        public static final int ADHERENCE_MONITOR_7203 = 0x1c23;

    }

    /**
     * Manager Status Variables
     */
    public final class State {

        /**
         * Disconnected
         */
        public static final int DISCONNECTED = -1;

        /**
         * Connected (Not Disconnected)
         */
        public static final int CONNECTED = 0;

        /**
         * 
         */
        public static final int UNASSOCIATED = 1;

        /**
         * The agent moves to the Associating state and sends an Association Request to the manager
         */
        public static final int ASSOCIATING = 3;

        /**
         * operating & configuring
         */
        public static final int ASSOCIATED = 10;

        /**
         * 
         */
        public static final int OPERATING = 15;

        /**
         * checking config & waiting for config
         */
        public static final int CONFIGURING = 11;

        /**
         * The agent moves to the Disassociating state and sends an Association Release Request to the manager
         */
        public static final int DISASSOCIATING = 2;

    }

    /**
     * 데이터 판단 범위 MIN, MAX, DEFAULT
     */
    public final class DataRange {

        /**
         * sys default
         */
        public static final int SYS_DEFAULT = 90;

        /**
         * sys min
         */
        public static final int SYS_MIN = 0;

        /**
         * sys max
         */
        public static final int SYS_MAX = 200;

        /**
         * dia default
         */
        public static final int DIA_DEFAULT = 61;

        /**
         * dia min
         */
        public static final int DIA_MIN = 0;

        /**
         * dia max
         */
        public static final int DIA_MAX = 200;

        /**
         * pulse default
         */
        public static final int PULSE_DEFAULT = 60;

        /**
         * pulse min
         */
        public static final int PULSE_MIN = 0;

        /**
         * pulse max
         */
        public static final int PULSE_MAX = 100;

        /**
         * spo2 default
         */
        public static final int SPO2_DEFAULT = 95;

        /**
         * spo2 min
         */
        public static final int SPO2_MIN = 0;

        /**
         * spo2 max
         */
        public static final int SPO2_MAX = 100;

        /**
         * weight (kg) default
         */
        public static final int WEIGHT_KG_DEFAULT = 40;

        /**
         * weight (kg) min
         */
        public static final int WEIGHT_KG_MIN = 0;

        /**
         * weight (kg) max
         */
        public static final int WEIGHT_KG_MAX = 150;

        /**
         * weight (lb) default
         */
        public static final int WEIGHT_LB_DEFAULT = 88;

        /**
         * weight (lb) min
         */
        public static final int WEIGHT_LB_MIN = 0;

        /**
         * weight (lb) max
         */
        public static final int WEIGHT_LB_MAX = 330;

        /**
         * blood sugar default
         */
        public static final int BLOOD_SUGAR_DEFAULT = 80;

        /**
         * blood sugar max
         */
        public static final int BLOOD_SUGAR_MAX = 400;

        /**
         * blood sugar min
         */
        public static final int BLOOD_SUGAR_MIN = 0;

    }

    /**
     * 혈압 진단 상수값
     */
    public final static class BloodPressureState {

        /**
         * 저혈압
         */
        public static final String BP_LOW = "LO";

        /**
         * 정상
         */
        public static final String BP_NORMAL = "NO";

        /**
         * 고혈압 전단계
         */
        public static final String BP_APPROACH = "AP";

        /**
         * 고혈압 1단계
         */
        public static final String BP_HIGH_ONE = "HO";

        /**
         * 고혈압 2단계
         */
        public static final String BP_HIGH_TWO = "HT";

        /**
         * 높은 고혈압
         */
        public static final String BP_VERY_HIGH = "VH";
    }

    /**
     * 체중 진단 상수값
     */
    public final static class WeighingScaleState {

        /**
         * 저체중
         */
        public static final String WS_LOW = "LO";

        /**
         * 정상
         */
        public static final String WS_NORMAL = "NO";

        /**
         * 고체중
         */
        public static final String WS_OVER_WEIGHT = "OW";

        /**
         * 비만
         */
        public static final String WS_OBESITY = "OB";

        /**
         * 고도비만
         */
        public static final String WS_VERY_OBESITY = "VO";

    }

    /**
     * Glucose 진단 상수값
     */
    public final static class GlucoseState {

        /**
         * 저혈당
         */
        public static final String GLUCOSE_LOW = "LO";

        /**
         * 정상
         */
        public static final String GLUCOSE_NORMAL = "NO";

        /**
         * 고혈당
         */
        public static final String GLUCOSE_OVER = "HY";

    }
}
