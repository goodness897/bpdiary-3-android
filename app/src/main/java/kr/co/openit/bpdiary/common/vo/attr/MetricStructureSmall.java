/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * MetricStructureSmall 정의
 */
public class MetricStructureSmall extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3110847290835675364L;

    /**
     * 생성자
     */
    public MetricStructureSmall() {
        // default constructor
    }

    /**
     * 생성자
     */
    public MetricStructureSmall(int attrId) {
        super(attrId);
    }

    /**
     * msStruct
     */
    private int msStruct;

    /**
     * msCompNo
     */
    private int msCompNo;

    /**
     * msStruct getter
     * 
     * @return msStruct
     */
    public int getMsStruct() {
        return msStruct;
    }

    /**
     * msStruct setter
     * 
     * @param msStruct - the msStruct to set
     */
    public void setMsStruct(int msStruct) {
        this.msStruct = msStruct;
    }

    /**
     * msCompNo getter
     * 
     * @return msCompNo
     */
    public int getMsCompNo() {
        return msCompNo;
    }

    /**
     * msCompNo setter
     * 
     * @param msCompNo - the msCompNo to set
     */
    public void setMsCompNo(int msCompNo) {
        this.msCompNo = msCompNo;
    }

    /**
     * ms-struct 정의
     */
    public final class MS_STRUCT {

        /**
         * MS_STRUCT_SIMPLE
         */
        public static final int MS_STRUCT_SIMPLE = 0;

        /**
         * MS_STRUCT_COMPOUND
         */
        public static final int MS_STRUCT_COMPOUND = 1;

        /**
         * MS_STRUCT_RESERVED
         */
        public static final int MS_STRUCT_RESERVED = 2;

        /**
         * MS_STRUCT_COMPOUND_FIX
         */
        public static final int MS_STRUCT_COMPOUND_FIX = 3;

        /**
         * 생성자
         */
        private MS_STRUCT() {
            // default constructor
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("ms-struct = ").append(getMsStruct()).append("\n");
        sb.append("ms-comp-no = ").append(getMsCompNo()).append("\n");

        return sb.toString();
    }
}
