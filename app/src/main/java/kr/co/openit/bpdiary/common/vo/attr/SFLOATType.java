/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * SFLOATType 정의
 */
public class SFLOATType extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8781904478283283538L;

    /**
     * 생성자
     */
    public SFLOATType() {
        // default constructor
    }

    /**
     * 생성자
     */
    public SFLOATType(int attrId) {
        super(attrId);
    }

    /**
     * value
     */
    private short value;

    /**
     * value getter
     * 
     * @return value
     */
    public short getValue() {
        return value;
    }

    /**
     * value setter
     * 
     * @param value - the value to set
     */
    public void setValue(short value) {
        this.value = value;
    }

    /**
     * ShortFloatingPoint Special Value
     */
    public final class ShortFloatingPointSpecialValue {

        /**
         * NaN
         */
        public static final short NAN = (short)0x07FF;

        /**
         * NRes
         */
        public static final short NRES = (short)0x0800;

        /**
         * +INFINITY
         */
        public static final short PLUS_INFINITY = (short)0x07FE;

        /**
         * -INFINITY
         */
        public static final short MINUS_INFINITY = (short)0x0802;

        /**
         * Reserved for future use
         */
        public static final short RESERVED_FOR_FUTURE_USE = (short)0x0801;

        /**
         * 생성자
         */
        private ShortFloatingPointSpecialValue() {
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
