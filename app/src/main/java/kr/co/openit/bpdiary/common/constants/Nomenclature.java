package kr.co.openit.bpdiary.common.constants;

/**
 * Nomenclature 상수 정의
 */
public final class Nomenclature {

    /**
     * PartitionCode 정의
     */
    public static class PartitionCode {

        /**
         * MDC_PART_OBJ
         */
        public static final short MDC_PART_OBJ = (short)0x0001;

        /**
         * MDC_PART_SCADA
         */
        public static final short MDC_PART_SCADA = (short)0x0002;

        /**
         * MDC_PART_DIM
         */
        public static final short MDC_PART_DIM = (short)0x0004;

        /**
         * MDC_PART_INFRA
         */
        public static final short MDC_PART_INFRA = (short)0x0008;

        /**
         * MDC_PART_PHD_DM
         */
        public static final short MDC_PART_PHD_DM = (short)0x0080;

        /**
         * MDC_PART_PHD_HF
         */
        public static final short MDC_PART_PHD_HF = (short)0x0081;

        /**
         * MDC_PART_PHD_AI
         */
        public static final short MDC_PART_PHD_AI = (short)0x0082;

        /**
         * MDC_PART_RET_CODE
         */
        public static final short MDC_PART_RET_CODE = (short)0x00ff;

        /**
         * MDC_PART_EXT_NOM
         */
        public static final short MDC_PART_EXT_NOM = (short)0x0100;

    }

    /**
     * ObjectInfra 정의
     */
    public static class ObjectInfra {

        /**
         * MDC_MOC_VMO_METRIC
         */
        public static final int MDC_MOC_VMO_METRIC = 4;

        /**
         * MDC_MOC_VMO_METRIC_ENUM
         */
        public static final int MDC_MOC_VMO_METRIC_ENUM = 5;

        /**
         * MDC_MOC_VMO_METRIC_NU
         */
        public static final int MDC_MOC_VMO_METRIC_NU = 6;

        /**
         * MDC_MOC_VMO_METRIC_SA_RT
         */
        public static final int MDC_MOC_VMO_METRIC_SA_RT = 9;

        /**
         * MDC_MOC_SCAN
         */
        public static final int MDC_MOC_SCAN = 16;

        /**
         * MDC_MOC_SCAN_CFG
         */
        public static final int MDC_MOC_SCAN_CFG = 17;

        /**
         * MDC_MOC_SCAN_CFG_EPI
         */
        public static final int MDC_MOC_SCAN_CFG_EPI = 18;

        /**
         * MDC_MOC_SCAN_CFG_PERI
         */
        public static final int MDC_MOC_SCAN_CFG_PERI = 19;

        /**
         * MDC_MOC_VMS_MDS_SIMP
         */
        public static final int MDC_MOC_VMS_MDS_SIMP = 37;

        /**
         * MDC_MOC_VMO_PMSTORE
         */
        public static final int MDC_MOC_VMO_PMSTORE = 61;

        /**
         * MDC_MOC_PM_SEGMENT
         */
        public static final int MDC_MOC_PM_SEGMENT = 62;

    }

    /**
     * MDC_PART_PHD_DM - Disease Mgmt
     */
    public static class DiseaseMgmt {

        /**
         * MDC_PEF_READING_STATUS
         */
        public static final int MDC_PEF_READING_STATUS = 30720;

    }

    /**
     * 공통 Attribute
     */
    public static class Attribute {

        /**
         * MDC_ATTR_ID_HANDLE
         */
        public static final int MDC_ATTR_ID_HANDLE = 2337;

        /**
         * MDC_ATTR_ID_INSTNO
         */
        public static final int MDC_ATTR_ID_INSTNO = 2338;

        /**
         * MDC_ATTR_ID_MODEL
         */
        public static final int MDC_ATTR_ID_MODEL = 2344;

        /**
         * MDC_ATTR_ID_PHYSIO
         */
        public static final int MDC_ATTR_ID_PHYSIO = 2347;

        /**
         * MDC_ATTR_ID_PROD_SPECN
         */
        public static final int MDC_ATTR_ID_PROD_SPECN = 2349;

        /**
         * MDC_ATTR_ID_TYPE
         */
        public static final int MDC_ATTR_ID_TYPE = 2351;

        /**
         * MDC_ATTR_METRIC_STORE_CAPAC_CNT
         */
        public static final int MDC_ATTR_METRIC_STORE_CAPAC_CNT = 2369;

        /**
         * MDC_ATTR_METRIC_STORE_SAMPLE_ALG
         */
        public static final int MDC_ATTR_METRIC_STORE_SAMPLE_ALG = 2371;

        /**
         * MDC_ATTR_METRIC_STORE_USAGE_CNT
         */
        public static final int MDC_ATTR_METRIC_STORE_USAGE_CNT = 2372;

        /**
         * MDC_ATTR_MSMT_STAT
         */
        public static final int MDC_ATTR_MSMT_STAT = 2375;

        /**
         * MDC_ATTR_NU_ACCUR_MSMT
         */
        public static final int MDC_ATTR_NU_ACCUR_MSMT = 2378;

        /**
         * MDC_ATTR_NUM_SEG
         */
        public static final int MDC_ATTR_NUM_SEG = 2385;

        /**
         * MDC_ATTR_OP_STAT
         */
        public static final int MDC_ATTR_OP_STAT = 2387;

        /**
         * MDC_ATTR_SEG_USAGE_CNT
         */
        public static final int MDC_ATTR_SEG_USAGE_CNT = 2427;

        /**
         * MDC_ATTR_SYS_ID
         */
        public static final int MDC_ATTR_SYS_ID = 2436;

        /**
         * MDC_ATTR_TIME_ABS
         */
        public static final int MDC_ATTR_TIME_ABS = 2439;

        /**
         * MDC_ATTR_TIME_END_SEG
         */
        public static final int MDC_ATTR_TIME_END_SEG = 2442;

        /**
         * MDC_ATTR_TIME_STAMP_ABS
         */
        public static final int MDC_ATTR_TIME_STAMP_ABS = 2448;

        /**
         * MDC_ATTR_TIME_START_SEG
         */
        public static final int MDC_ATTR_TIME_START_SEG = 2450;

        /**
         * MDC_ATTR_UNIT_CODE
         */
        public static final int MDC_ATTR_UNIT_CODE = 2454;

        /**
         * MDC_ATTR_TIME_REL_HI_RES
         */
        public static final int MDC_ATTR_TIME_REL_HI_RES = 2536;

        /**
         * MDC_ATTR_TIME_STAMP_REL_HI_RES
         */
        public static final int MDC_ATTR_TIME_STAMP_REL_HI_RES = 2537;

        /**
         * MDC_ATTR_DEV_CONFIG_ID
         */
        public static final int MDC_ATTR_DEV_CONFIG_ID = 2628;

        /**
         * MDC_ATTR_MDS_TIME_INFO
         */
        public static final int MDC_ATTR_MDS_TIME_INFO = 2629;

        /**
         * MDC_ATTR_METRIC_SPEC_SMALL
         */
        public static final int MDC_ATTR_METRIC_SPEC_SMALL = 2630;

        /**
         * MDC_ATTR_SOURCE_HANDLE_REF
         */
        public static final int MDC_ATTR_SOURCE_HANDLE_REF = 2631;

        /**
         * MDC_ATTR_ENUM_OBS_VAL_SIMP_OID
         */
        public static final int MDC_ATTR_ENUM_OBS_VAL_SIMP_OID = 2633;

        /**
         * MDC_ATTR_REG_CERT_DATA_LIST
         */
        public static final int MDC_ATTR_REG_CERT_DATA_LIST = 2635;

        /**
         * MDC_ATTR_NU_VAL_OBS_BASIC
         */
        public static final int MDC_ATTR_NU_VAL_OBS_BASIC = 2636;

        /**
         * MDC_ATTR_PM_STORE_CAPAB
         */
        public static final int MDC_ATTR_PM_STORE_CAPAB = 2637;

        /**
         * MDC_ATTR_PM_SEG_MAP
         */
        public static final int MDC_ATTR_PM_SEG_MAP = 2638;

        /**
         * MDC_ATTR_ATTRIBUTE_VAL_MAP
         */
        public static final int MDC_ATTR_ATTRIBUTE_VAL_MAP = 2645;

        /**
         * MDC_ATTR_NU_VAL_OBS_SIMP
         */
        public static final int MDC_ATTR_NU_VAL_OBS_SIMP = 2646;

        /**
         * MDC_ATTR_PM_STORE_LABEL_STRING
         */
        public static final int MDC_ATTR_PM_STORE_LABEL_STRING = 2647;

        /**
         * MDC_ATTR_PM_SEG_LABEL_STRING
         */
        public static final int MDC_ATTR_PM_SEG_LABEL_STRING = 2648;

        /**
         * MDC_ATTR_TIME_PD_MSMT_ACTIVE
         */
        public static final int MDC_ATTR_TIME_PD_MSMT_ACTIVE = 2649;

        /**
         * MDC_ATTR_SYS_TYPE_SPEC_LIST
         */
        public static final int MDC_ATTR_SYS_TYPE_SPEC_LIST = 2650;

        /**
         * MDC_ATTR_SUPPLEMENTAL_TYPES
         */
        public static final int MDC_ATTR_SUPPLEMENTAL_TYPES = 2657;

        /**
         * MDC_ATTR_TIME_ABS_ADJUST
         */
        public static final int MDC_ATTR_TIME_ABS_ADJUST = 2658;

        /**
         * MDC_ATTR_TRANSFER_TIMEOUT
         */
        public static final int MDC_ATTR_TRANSFER_TIMEOUT = 2660;

        /**
         * MDC_ATTR_ENUM_OBS_VAL_SIMP_BIT_STR
         */
        public static final int MDC_ATTR_ENUM_OBS_VAL_SIMP_BIT_STR = 2661;

        /**
         * MDC_ATTR_ENUM_OBS_VAL_BASIC_BIT_STR
         */
        public static final int MDC_ATTR_ENUM_OBS_VAL_BASIC_BIT_STR = 2662;

        /**
         * MDC_ATTR_METRIC_STRUCT_SMALL
         */
        public static final int MDC_ATTR_METRIC_STRUCT_SMALL = 2675;

        /**
         * MDC_ATTR_NU_CMPD_VAL_OBS_SIMP
         */
        public static final int MDC_ATTR_NU_CMPD_VAL_OBS_SIMP = 2676;

        /**
         * MDC_ATTR_NU_CMPD_VAL_OBS_BASIC
         */
        public static final int MDC_ATTR_NU_CMPD_VAL_OBS_BASIC = 2677;

        /**
         * MDC_ATTR_ID_PHYSIO_LIST
         */
        public static final int MDC_ATTR_ID_PHYSIO_LIST = 2678;

        /**
         * MDC_ATTR_CONTEXT_KEY
         */
        public static final int MDC_ATTR_CONTEXT_KEY = 2680;

    }

    /**
     * action-type | event-type
     */
    public static class Action {

        /**
         * MDC_ACT_SEG_GET_INFO
         */
        public static final int MDC_ACT_SEG_GET_INFO = 0x0c0d;

        /**
         * MDC_ACT_SET_TIME
         */
        public static final int MDC_ACT_SET_TIME = 0x0c17;

        /**
         * MDC_ACT_DATA_REQUEST
         */
        public static final int MDC_ACT_DATA_REQUEST = 0x0c1b;

        /**
         * MDC_ACT_SEG_TRIG_XFER
         */
        public static final int MDC_ACT_SEG_TRIG_XFER = 0x0c1c;

        /**
         * MDC_NOTI_CONFIG
         */
        public static final int MDC_NOTI_CONFIG = 0x0d1c;

        /**
         * MDC_NOTI_SCAN_REPORT_FIXED
         */
        public static final int MDC_NOTI_SCAN_REPORT_FIXED = 0x0d1d;

        /**
         * MDC_NOTI_SCAN_REPORT_VAR
         */
        public static final int MDC_NOTI_SCAN_REPORT_VAR = 0x0d1e;

        /**
         * MDC_NOTI_SCAN_REPORT_MP_FIXED
         */
        public static final int MDC_NOTI_SCAN_REPORT_MP_FIXED = 0x0d1f;

        /**
         * MDC_NOTI_SCAN_REPORT_MP_VAR
         */
        public static final int MDC_NOTI_SCAN_REPORT_MP_VAR = 0x0d20;

        /**
         * MDC_NOTI_SEGMENT_DATA
         */
        public static final int MDC_NOTI_SEGMENT_DATA = 0x0d21;

    }

    /**
     * Communication infrastructure (MDC_PART_INFRA)
     */
    public static class DeviceSpec {

        /**
         * puls oximeter (산소포화도)
         */
        public static final int MDC_DEV_SPEC_PROFILE_PULS_OXIM = 4100;

        /**
         * blood pressure (혈압계)
         */
        public static final int MDC_DEV_SPEC_PROFILE_BP = 4103;

        /**
         * temperature (체온계)
         */
        public static final int MDC_DEV_SPEC_PROFILE_TEMP = 4104;

        /**
         * scale weight (체중계)
         */
        public static final int MDC_DEV_SPEC_PROFILE_SCALE = 4111;

        /**
         * glucose meter (혈당계)
         */
        public static final int MDC_DEV_SPEC_PROFILE_GLUCOSE = 4113;

        /**
         * Body Composition Analyzer (체지방계)
         */
        public static final int MDC_DEV_SPEC_PROFILE_BCA = 4116;

        /**
         * Peak Expiratory Flow Monitor 10421
         */
        public static final int MDC_DEV_SPEC_PROFILE_PEFM = 4117;

        /**
         * Cardiovascular Fitness 10441
         */
        public static final int MDC_DEV_SPEC_PROFILE_HF_CARDIO = 4137;

        /**
         * 10442
         */
        public static final int MDC_DEV_SPEC_PROFILE_HF_STRENGTH = 4138;

        /**
         * 10471
         */
        public static final int MDC_DEV_SPEC_PROFILE_AI_ACTIVITY_HUB = 4167;

        /**
         * 10472
         */
        public static final int MDC_DEV_SPEC_PROFILE_AI_MED_MINDER = 4168;

    }

    /**
     * Dimensions 정의
     */
    public static class Dimensions {

        /**
         * dimension-less
         */
        public static final int MDC_DIM_DIMLESS = 512;

        /**
         * %
         */
        public static final int MDC_DIM_PERCENT = 544;

        /**
         * degree
         */
        public static final int MDC_DIM_ANG_DEG = 736;

        /**
         * m
         */
        public static final int MDC_DIM_X_M = 1280;

        /**
         * MDC_DIM_CENTI_M
         */
        public static final int MDC_DIM_CENTI_M = 1297;

        /**
         * ft
         */
        public static final int MDC_DIM_X_FOOT = 1344;

        /**
         * in
         */
        public static final int MDC_DIM_X_INCH = 1376;

        /**
         * l
         */
        public static final int MDC_DIM_X_L = 1600;

        /**
         * g
         */
        public static final int MDC_DIM_X_G = 1728;

        /**
         * kg
         */
        public static final int MDC_DIM_KILO_G = 1731;

        /**
         * lb
         */
        public static final int MDC_DIM_LB = 1760;

        /**
         * MDC_DIM_KG_PER_M_SQ
         */
        public static final int MDC_DIM_KG_PER_M_SQ = 1952;

        /**
         * mg dL-1
         */
        public static final int MDC_DIM_MILLI_G_PER_DL = 2130;

        /**
         * sec
         */
        public static final int MDC_DIM_SEC = 2176;

        /**
         * min
         */
        public static final int MDC_DIM_MIN = 2208;

        /**
         * y
         */
        public static final int MDC_DIM_YR = 2368;

        /**
         * bpm
         */
        public static final int MDC_DIM_BEAT_PER_MIN = 2720;

        /**
         * resp min-1
         */
        public static final int MDC_DIM_RESP_PER_MIN = 2784;

        /**
         * 
         */
        public static final int MDC_DIM_X_L_PER_MIN = 3072;

        /**
         * 
         */
        public static final int MDC_DIM_KILO_PASCAL = 3843;

        /**
         * 
         */
        public static final int MDC_DIM_MMHG = 3872;

        /**
         * j
         */
        public static final int MDC_DIM_X_JOULES = 3968;

        /**
         * W
         */
        public static final int MDC_DIM_X_WATT = 4032;

        /**
         * m min-1
         */
        public static final int MDC_DIM_X_M_PER_MIN = 6560;

        /**
         * 
         */
        public static final int MDC_DIM_X_STEP = 6656;

        /**
         * ft per minute
         */
        public static final int MDC_DIM_X_FOOT_PER_MIN = 6688;

        /**
         * inch per minute
         */
        public static final int MDC_DIM_X_INCH_PER_MIN = 6720;

        /**
         * step per minute
         */
        public static final int MDC_DIM_X_STEP_PER_MIN = 6752;

        /**
         * cal-calories
         */
        public static final int MDC_DIM_X_CAL = 6784;

        /**
         * rpm - revolutions per minute
         */
        public static final int MDC_DIM_RPM = 6816;

    }

    /**
     * Medical supervisory control and data acquisition (MDC_PART_SCADA)
     */
    public final class DataAcqu {

        /**
         * MDC_HF_ALT_GAIL
         */
        public static final int MDC_HF_ALT_GAIL = 100;

        /**
         * MDC_HF_ALT_LOSS
         */
        public static final int MDC_HF_ALT_LOSS = 101;

        /**
         * MDC_HF_ALT
         */
        public static final int MDC_HF_ALT = 102;

        /**
         * MDC_HF_DISTANCE
         */
        public static final int MDC_HF_DISTANCE = 103;

        /**
         * MDC_HF_ASC_TME_DIST
         */
        public static final int MDC_HF_ASC_TME_DIST = 104;

        /**
         * MDC_HF_DESC_TIME_DIST
         */
        public static final int MDC_HF_DESC_TIME_DIST = 105;

        /**
         * MDC_HF_LATITUDE
         */
        public static final int MDC_HF_LATITUDE = 106;

        /**
         * MDC_HF_LONGITUDE
         */
        public static final int MDC_HF_LONGITUDE = 107;

        /**
         * MDC_HF_PROGRAM_ID
         */
        public static final int MDC_HF_PROGRAM_ID = 108;

        /**
         * MDC_HF_SLOPES
         */
        public static final int MDC_HF_SLOPES = 109;

        /**
         * MDC_HF_SPEED
         */
        public static final int MDC_HF_SPEED = 110;

        /**
         * MDC_HF_CAD
         */
        public static final int MDC_HF_CAD = 111;

        /**
         * MDC_HF_INCLINE
         */
        public static final int MDC_HF_INCLINE = 112;

        /**
         * MDC_HF_HR_MAX_USER
         */
        public static final int MDC_HF_HR_MAX_USER = 113;

        /**
         * MDC_HF_HR
         */
        public static final int MDC_HF_HR = 114;

        /**
         * MDC_HF_POWER
         */
        public static final int MDC_HF_POWER = 115;

        /**
         * MDC_HF_RESIST
         */
        public static final int MDC_HF_RESIST = 116;

        /**
         * MDC_HF_STRIDE
         */
        public static final int MDC_HF_STRIDE = 117;

        /**
         * MDC_HF_ENERGY
         */
        public static final int MDC_HF_ENERGY = 119;

        /**
         * MDC_HF_CAL_INGEST
         */
        public static final int MDC_HF_CAL_INGEST = 120;

        /**
         * MDC_HF_CAL_INGEST_CARB
         */
        public static final int MDC_HF_CAL_INGEST_CARB = 121;

        /**
         * MDC_HF_SUST_PA_THRESHOLD
         */
        public static final int MDC_HF_SUST_PA_THRESHOLD = 122;

        /**
         * MDC_HF_SESSION
         */
        public static final int MDC_HF_SESSION = 123;

        /**
         * MDC_HF_SUBSESSION
         */
        public static final int MDC_HF_SUBSESSION = 124;

        /**
         * MDC_HF_ACTIVITY_TIME
         */
        public static final int MDC_HF_ACTIVITY_TIME = 125;

        /**
         * MDC_HF_AGE
         */
        public static final int MDC_HF_AGE = 126;

        /**
         * MDC_HF_ACTIVITY_INTENSITY
         */
        public static final int MDC_HF_ACTIVITY_INTENSITY = 127;

        /**
         * MDC_HF_ACT_AMB
         */
        public static final int MDC_HF_ACT_AMB = 1000;

        /**
         * MDC_HF_ACT_REST
         */
        public static final int MDC_HF_ACT_REST = 1001;

        /**
         * MDC_HF_ACT_MOTOR
         */
        public static final int MDC_HF_ACT_MOTOR = 1002;

        /**
         * MDC_HF_ACT_LYING
         */
        public static final int MDC_HF_ACT_LYING = 1003;

        /**
         * MDC_HF_ACT_SLEEP
         */
        public static final int MDC_HF_ACT_SLEEP = 1004;

        /**
         * MDC_HF_ACT_PHYS
         */
        public static final int MDC_HF_ACT_PHYS = 1005;

        /**
         * MDC_HF_ACT_SUS_PHYS
         */
        public static final int MDC_HF_ACT_SUS_PHYS = 1006;

        /**
         * MDC_HF_ACT_UNKNOWN
         */
        public static final int MDC_HF_ACT_UNKNOWN = 1007;

        /**
         * MDC_HF_ACT_MULTIPLE
         */
        public static final int MDC_HF_ACT_MULTIPLE = 1008;

        /**
         * MDC_HF_ACT_MONITOR
         */
        public static final int MDC_HF_ACT_MONITOR = 1009;

        /**
         * MDC_HF_ACT_SKI
         */
        public static final int MDC_HF_ACT_SKI = 1010;

        /**
         * MDC_HF_ACT_RUN
         */
        public static final int MDC_HF_ACT_RUN = 1011;

        /**
         * MDC_HF_ACT_BIKE
         */
        public static final int MDC_HF_ACT_BIKE = 1012;

        /**
         * MDC_HF_ACT_STAIR
         */
        public static final int MDC_HF_ACT_STAIR = 1013;

        /**
         * MDC_HF_ACT_ROW
         */
        public static final int MDC_HF_ACT_ROW = 1014;

        /**
         * MDC_HF_ACT_HOME
         */
        public static final int MDC_HF_ACT_HOME = 1015;

        /**
         * MDC_HF_ACT_WORK
         */
        public static final int MDC_HF_ACT_WORK = 1016;

        /**
         * MDC_HF_ACT_WALK
         */
        public static final int MDC_HF_ACT_WALK = 1017;

        /**
         * MDC_PULS_OXIM_PULS_RATE
         */
        public static final int MDC_PULS_OXIM_PULS_RATE = 18458;

        /**
         * MDC_PULS_RATE_NON_INV
         */
        public static final int MDC_PULS_RATE_NON_INV = 18474;

        /**
         * MDC_PRESS_BLD_NONINV
         */
        public static final int MDC_PRESS_BLD_NONINV = 18948;

        /**
         * MDC_PRESS_BLD_NONINV_SYS
         */
        public static final int MDC_PRESS_BLD_NONINV_SYS = 18949;

        /**
         * MDC_PRESS_BLD_NONINV_DIA
         */
        public static final int MDC_PRESS_BLD_NONINV_DIA = 18950;

        /**
         * MDC_PRESS_BLD_NONINV_MEAN
         */
        public static final int MDC_PRESS_BLD_NONINV_MEAN = 18951;

        /**
         * MDC_SAT_O2_QUAL
         */
        public static final int MDC_SAT_O2_QUAL = 19248;

        /**
         * MDC_TEMP_BODY
         */
        public static final int MDC_TEMP_BODY = 19292;

        /**
         * MDC_PULS_OXIM_PERF_REL
         */
        public static final int MDC_PULS_OXIM_PERF_REL = 19376;

        /**
         * MDC_PULS_OXIM_PLETH
         */
        public static final int MDC_PULS_OXIM_PLETH = 19380;

        /**
         * MDC_PULS_OXIM_SAT_O2
         */
        public static final int MDC_PULS_OXIM_SAT_O2 = 19384;

        /**
         * MDC_MODALITY_FAST
         */
        public static final int MDC_MODALITY_FAST = 19508;

        /**
         * MDC_MODALITY_SLOW
         */
        public static final int MDC_MODALITY_SLOW = 19512;

        /**
         * MDC_MODALITY_SPOT
         */
        public static final int MDC_MODALITY_SPOT = 19516;

        /**
         * MDC_PULS_OXIM_DEV_STATUS
         */
        public static final int MDC_PULS_OXIM_DEV_STATUS = 19532;

        /**
         * MDC_RESP_RATE
         */
        public static final int MDC_RESP_RATE = 20490;

        /**
         * MDC_FLOW_AWAY_EXP_FORCED_PEAK (peak expiratory flow)
         */
        public static final int MDC_FLOW_AWAY_EXP_FORCED_PEAK = 21512;

        /**
         * MDC_FLOW_AWAY_EXP_FORCED_PEAK_PB (personal best of PEF)
         */
        public static final int MDC_FLOW_AWAY_EXP_FORCED_PEAK_PB = 21513;

        /**
         * MDC_VOL_AWAY_EXP_FORCED_1S (forced expiratory volume over 1 second)
         */
        public static final int MDC_VOL_AWAY_EXP_FORCED_1S = 21514;

        /**
         * MDC_VOL_AWAY_EXP_FORCED_EXP_6S (forced expiratory volume over 6 seconds)
         */
        public static final int MDC_VOL_AWAY_EXP_FORCED_EXP_6S = 21515;

        /**
         * MDC_PEF_READING_STATUS
         */
        public static final int MDC_PEF_READING_STATUS = 30720;

        /**
         * MDC_CONC_GLU_GEN
         */
        public static final int MDC_CONC_GLU_GEN = 28948;

        /**
         * MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD
         */
        public static final int MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD = 29112;

        /**
         * MDC_CONC_GLU_CAPILLARY_PLASMA
         */
        public static final int MDC_CONC_GLU_CAPILLARY_PLASMA = 29116;

        /**
         * MDC_CONC_GLU_VENOUS_WHOLEBLOOD
         */
        public static final int MDC_CONC_GLU_VENOUS_WHOLEBLOOD = 29120;

        /**
         * MDC_CONC_GLU_VENOUS_PLASMA
         */
        public static final int MDC_CONC_GLU_VENOUS_PLASMA = 29124;

        /**
         * MDC_CONC_GLU_ARTERIAL_WHOLEBLOOD
         */
        public static final int MDC_CONC_GLU_ARTERIAL_WHOLEBLOOD = 29128;

        /**
         * MDC_CONC_GLU_ARTERIAL_PLASMA
         */
        public static final int MDC_CONC_GLU_ARTERIAL_PLASMA = 29132;

        /**
         * MDC_CONC_GLU_CONTROL
         */
        public static final int MDC_CONC_GLU_CONTROL = 29136;

        /**
         * MDC_CONC_GLU_ISF
         */
        public static final int MDC_CONC_GLU_ISF = 29140;

        /**
         * MDC_CONC_HBA1C
         */
        public static final int MDC_CONC_HBA1C = 29148;

        /**
         * MDC_CTXT_GLU_EXERCISE
         */
        public static final int MDC_CTXT_GLU_EXERCISE = 29152;

        /**
         * MDC_CTXT_GLU_MEAL
         */
        public static final int MDC_CTXT_GLU_MEAL = 29256;

        /**
         * MDC_CTXT_GLU_MEAL_PREPRANDIAL (식전)
         */
        public static final int MDC_CTXT_GLU_MEAL_PREPRANDIAL = 29260;

        /**
         * MDC_CTXT_GLU_MEAL_POSTPRANDIAL (식후)
         */
        public static final int MDC_CTXT_GLU_MEAL_POSTPRANDIAL = 29264;

        /**
         * MDC_CTXT_GLU_MEAL_FASTING (공복)
         */
        public static final int MDC_CTXT_GLU_MEAL_FASTING = 29268;

        /**
         * MDC_CTXT_GLU_MEAL_CASUAL (가벼운 식사)
         */
        public static final int MDC_CTXT_GLU_MEAL_CASUAL = 29272;

        /**
         * MDC_CTXT_GLU_MEAL_BEDTIME (취침전)
         */
        public static final int MDC_CTXT_GLU_MEAL_BEDTIME = 29300;

        /**
         * MDC_TRIG
         */
        public static final int MDC_TRIG = 53250;

        /**
         * MDC_TRIG_BEAT
         */
        public static final int MDC_TRIG_BEAT = 53251;

        /**
         * MDC_TRIG_BEAT_MAX_INRUSH
         */
        public static final int MDC_TRIG_BEAT_MAX_INRUSH = 53259;

        /**
         * MDC_MASS_BODY_ACTUAL
         */
        public static final int MDC_MASS_BODY_ACTUAL = 57664;

        /**
         * MDC_LEN_BODY_ACTUAL
         */
        public static final int MDC_LEN_BODY_ACTUAL = 57668;

        /**
         * MDC_BODY_FAT
         */
        public static final int MDC_BODY_FAT = 57676;

        /**
         * MDC_RATIO_MASS_BODY_LEN_SQ
         */
        public static final int MDC_RATIO_MASS_BODY_LEN_SQ = 57680;

        /**
         * MDC_MASS_BODY_FAT_FREE
         */
        public static final int MDC_MASS_BODY_FAT_FREE = 57684;

        /**
         * MDC_MASS_BODY_SOFT_LEAN
         */
        public static final int MDC_MASS_BODY_SOFT_LEAN = 57688;

        /**
         * MDC_BODY_WATER
         */
        public static final int MDC_BODY_WATER = 57692;

        /**
         * MDC_METRIC_NOS
         */
        public static final int MDC_METRIC_NOS = 61439;

    }
}
