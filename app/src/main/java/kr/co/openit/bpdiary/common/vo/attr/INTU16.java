/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * INT-U16 정의
 */
public class INTU16 extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7740516256738133378L;

    /**
     * 생성자
     */
    public INTU16() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public INTU16(int attrId) {
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append(value).append("\n");

        return sb.toString();
    }
}
