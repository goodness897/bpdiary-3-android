/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * OCTEC STRING 정의
 */
public class OCTECSTRING extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6061384456717951414L;

    /**
     * 생성자
     */
    public OCTECSTRING() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public OCTECSTRING(int attrId) {
        // default constructor
        super(attrId);
    }

    /**
     * length
     */
    private int length;

    /**
     * value
     */
    private String value;

    /**
     * length getter
     * 
     * @return length
     */
    public int getLength() {
        return length;
    }

    /**
     * length setter
     * 
     * @param length - the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * value getter
     * 
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * value setter
     * 
     * @param value - the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("octec string : ").append(value).append("\n");

        return sb.toString();
    }

}
