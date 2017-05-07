/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.io.Serializable;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * AttrValMapEntry 정의
 */
public class AttrValMapEntry implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8547338182792661242L;

    /**
     * 생성자
     */
    public AttrValMapEntry() {
        // default constructor
    }

    /**
     * 생성자
     */
    public AttrValMapEntry(int attributeId, int attributeLen) {
        this.attributeId = attributeId;
        this.attributeLen = attributeLen;
    }

    /**
     * attributeId
     */
    private int attributeId;

    /**
     * attributeLen
     */
    private int attributeLen;

    /**
     * attributeId getter
     * 
     * @return attributeId
     */
    public int getAttributeId() {
        return attributeId;
    }

    /**
     * attributeId setter
     * 
     * @param attributeId - the attributeId to set
     */
    public void setAttributeId(int attributeId) {
        this.attributeId = attributeId;
    }

    /**
     * attributeLen getter
     * 
     * @return attributeLen
     */
    public int getAttributeLen() {
        return attributeLen;
    }

    /**
     * attributeLen setter
     * 
     * @param attributeLen - the attributeLen to set
     */
    public void setAttributeLen(int attributeLen) {
        this.attributeLen = attributeLen;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("    ")
          .append(ManagerUtil.getAttributeName(attributeId))
          .append(" | value length = ")
          .append(attributeLen)
          .append("\n");

        return sb.toString();
    }
}
