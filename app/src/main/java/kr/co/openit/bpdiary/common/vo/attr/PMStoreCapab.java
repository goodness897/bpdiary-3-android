/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * PmStoreCapab
 */
public class PMStoreCapab extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5771272363655695237L;

    /**
     * 생성자
     */
    public PMStoreCapab() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public PMStoreCapab(int attrId) {
        // default constructor
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
     * PMStoreCapab 명 getter
     * 
     * @return PMStoreCapab 명
     */
    public String getPMStoreCapabName() {
        switch (value) {
            case PMSC.PMSC_VAR_NO_OF_SEGM:
                return "PMSC_VAR_NO_OF_SEGM";

            case PMSC.PMSC_EPI_SEG_ENTRIES:
                return "PMSC_EPI_SEG_ENTRIES";

            case PMSC.PMSC_PERI_SEG_ENTRIES:
                return "PMSC_PERI_SEG_ENTRIES";

            case PMSC.PMSC_ABS_TIME_SELECT:
                return "PMSC_ABS_TIME_SELECT";

            case PMSC.PMSC_CLEAR_SEGM_BY_LIST_SUP:
                return "PMSC_CLEAR_SEGM_BY_LIST_SUP";

            case PMSC.PMSC_CLEAR_SEGM_BY_TIME_SUP:
                return "PMSC_CLEAR_SEGM_BY_TIME_SUP";

            case PMSC.PMSC_CLEAR_SEGM_REMOVE:
                return "PMSC_CLEAR_SEGM_REMOVE";

            case PMSC.PMSC_MULTI_PERSON:
                return "PMSC_MULTI_PERSON";

            default:
                return null;
        }
    }

    /**
     * PMSC 정의
     */
    public final class PMSC {

        /**
         * PMSC_VAR_NO_OF_SEGM
         */
        public static final int PMSC_VAR_NO_OF_SEGM = 0x8000;

        /**
         * PMSC_EPI_SEG_ENTRIES
         */
        public static final int PMSC_EPI_SEG_ENTRIES = 0x0800;

        /**
         * PMSC_PERI_SEG_ENTRIES
         */
        public static final int PMSC_PERI_SEG_ENTRIES = 0x0400;

        /**
         * PMSC_ABS_TIME_SELECT
         */
        public static final int PMSC_ABS_TIME_SELECT = 0x0200;

        /**
         * PMSC_CLEAR_SEGM_BY_LIST_SUP
         */
        public static final int PMSC_CLEAR_SEGM_BY_LIST_SUP = 0x0100;

        /**
         * PMSC_CLEAR_SEGM_BY_TIME_SUP
         */
        public static final int PMSC_CLEAR_SEGM_BY_TIME_SUP = 0x0080;

        /**
         * PMSC_CLEAR_SEGM_REMOVE
         */
        public static final int PMSC_CLEAR_SEGM_REMOVE = 0x0040;

        /**
         * PMSC_MULTI_PERSON
         */
        public static final int PMSC_MULTI_PERSON = 0x0008;

        /**
         * 생성자
         */
        private PMSC() {
            // default constructor
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append(getPMStoreCapabName()).append("\n");

        return sb.toString();
    }
}
