/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * AbsoluteTimeAdjust 정의
 */
public class AbsoluteTimeAdjust extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2195571078385708874L;

    /**
     * 생성자
     */
    public AbsoluteTimeAdjust() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public AbsoluteTimeAdjust(int attrId) {
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
