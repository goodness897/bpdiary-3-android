/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * InstNumber 정의
 */
public class InstNumber extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8695297825731965078L;

    /**
     * 생성자
     */
    public InstNumber() {
        // default constructor
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
