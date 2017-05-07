/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * StoSampleAlg 정의
 */
public class StoSampleAlg extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5533745543196321773L;

    /**
     * 생성자
     */
    public StoSampleAlg() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public StoSampleAlg(int attrId) {
        super(attrId);
    }

    /**
     * value
     */
    private int value;

    /**
     * value getter
     * 
     * @return value
     */
    public int getValue() {
        return value;
    }

    /**
     * value setter
     * 
     * @param value - the value to set
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * StoSampleAlg 명 getter
     * 
     * @return
     */
    public String getStoSampleAlgName() {
        switch (value) {
            case ST_ALG.ST_ALG_NOS:
                return "ST_ALG_NOS";

            case ST_ALG.ST_ALG_MOVING_AVERAGE:
                return "ST_ALG_MOVING_AVERAGE";

            case ST_ALG.ST_ALG_RECURSIVE:
                return "ST_ALG_RECURSIVE";

            case ST_ALG.ST_ALG_MIN_PICK:
                return "ST_ALG_MIN_PICK";

            case ST_ALG.ST_ALG_MAX_PICK:
                return "ST_ALG_MAX_PICK";

            case ST_ALG.ST_ALG_MEDIAN:
                return "ST_ALG_MEDIAN";

            case ST_ALG.ST_ALG_TRENDED:
                return "ST_ALG_TRENDED";

            case ST_ALG.ST_ALG_NO_DOWNSAMPLING:
                return "ST_ALG_NO_DOWNSAMPLING";

            default:
                return null;
        }
    }

    /**
     * ST_ALG 정의
     */
    public final class ST_ALG {

        /**
         * ST_ALG_NOS
         */
        public static final int ST_ALG_NOS = 0x0000;

        /**
         * ST_ALG_MOVING_AVERAGE
         */
        public static final int ST_ALG_MOVING_AVERAGE = 0x0001;

        /**
         * ST_ALG_RECURSIVE
         */
        public static final int ST_ALG_RECURSIVE = 0x0002;

        /**
         * ST_ALG_MIN_PICK
         */
        public static final int ST_ALG_MIN_PICK = 0x0003;

        /**
         * ST_ALG_MAX_PICK
         */
        public static final int ST_ALG_MAX_PICK = 0x0004;

        /**
         * ST_ALG_MEDIAN
         */
        public static final int ST_ALG_MEDIAN = 0x0005;

        /**
         * ST_ALG_TRENDED
         */
        public static final int ST_ALG_TRENDED = 0x0200;

        /**
         * ST_ALG_NO_DOWNSAMPLING
         */
        public static final int ST_ALG_NO_DOWNSAMPLING = 0x0400;

        /**
         * 생성자
         */
        private ST_ALG() {
            // default constructor
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append(getStoSampleAlgName()).append("\n");

        return sb.toString();
    }
}
