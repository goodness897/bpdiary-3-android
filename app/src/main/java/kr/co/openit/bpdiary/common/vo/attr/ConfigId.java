/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * ConfigId 정의
 */
public class ConfigId extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4785349671311730908L;

    /**
     * 생성자
     */
    public ConfigId() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public ConfigId(int attrId) {
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
     * CONFIG 정의
     */
    public final class CONFIG {

        /**
         * MANAGER_CONFIG_RESPONSE
         */
        public static final int MANAGER_CONFIG_RESPONSE = 0x0000;

        /**
         * STANDARD_CONFIG_START
         */
        public static final int STANDARD_CONFIG_START = 0x0001;

        /**
         * STANDARD_CONFIG_END
         */
        public static final int STANDARD_CONFIG_END = 0x3fff;

        /**
         * EXTENDED_CONFIG_START
         */
        public static final int EXTENDED_CONFIG_START = 0x4000;

        /**
         * EXTENDED_CONFIG_END
         */
        public static final int EXTENDED_CONFIG_END = 0x7fff;

        /**
         * RESERVED_START
         */
        public static final int RESERVED_START = 0x8000;

        /**
         * RESERVED_END
         */
        public static final int RESERVED_END = 0xffff;

        /**
         * 생성자
         */
        private CONFIG() {
            // default constructor
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("dev-config-id = ").append(Integer.toHexString(value)).append("\n");

        return sb.toString();
    }
}
