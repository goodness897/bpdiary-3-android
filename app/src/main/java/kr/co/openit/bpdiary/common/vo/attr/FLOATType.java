/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * FLOAT-Type 정의
 */
public class FLOATType extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3508490405300169830L;

    /**
     * 생성자
     */
    public FLOATType() {
        // default constructor
    }

    /**
     * 생성자
     */
    public FLOATType(int attrId) {
        super(attrId);
    }

    private long value;

    /**
     * value getter
     * 
     * @return value
     */
    public long getValue() {
        return value;
    }

    /**
     * value setter
     * 
     * @param value - the value to set
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * FloatingPoint Special Value
     */
    public final class FloatingPointSpecialValue {

        /**
         * NaN
         */
        public static final int NAN = 0x007FFFFF;

        /**
         * NRes
         */
        public static final int NRES = 0x00800000;

        /**
         * +INFINITY
         */
        public static final int PLUS_INFINITY = 0x007FFFFE;

        /**
         * -INFINITY
         */
        public static final int MINUS_INFINITY = 0x00800002;

        /**
         * Reserved for future use
         */
        public static final int RESERVED_FOR_FUTURE_USE = 0x00800001;

        /**
         * 생성자
         */
        private FloatingPointSpecialValue() {
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
