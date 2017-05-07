/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * INT-U32 정의
 */
public class INTU32 extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7452438692344090356L;

    /**
     * 생성자
     */
    public INTU32() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public INTU32(int attrId) {
        // default constructor
        super(attrId);
    }

    /**
     * value
     */
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append(value).append("\n");

        return sb.toString();
    }
}
