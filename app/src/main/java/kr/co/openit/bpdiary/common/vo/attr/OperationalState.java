/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * OperationalState 정의
 */
public class OperationalState extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5173460755903640382L;

    /**
     * 생성자
     */
    public OperationalState() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public OperationalState(int attrId) {
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
     * OS 정의
     */
    public final class OS {

        /**
         * OS_DISABLED
         */
        public static final int OS_DISABLED = 0;

        /**
         * OS_ENABLED
         */
        public static final int OS_ENABLED = 1;

        /**
         * OS_NOT_AVAILABLE
         */
        public static final int OS_NOT_AVAILABLE = 2;

        /**
         * 생성자
         */
        private OS() {
            // default constructor
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append(value).append("\n");

        return sb.toString();
    }
}
