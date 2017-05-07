/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2012, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.io.Serializable;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * attribute vo
 */
public class Attribute implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8872955004154168005L;

    /**
     * 생성자
     */
    public Attribute() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public Attribute(int attrId) {
        this.attrId = attrId;
    }

    /**
     * attribute-id
     */
    private int attrId;

    /**
     * attribute id 반환
     * 
     * @return attribute id
     */
    public int getAttributeId() {
        return attrId;
    }

    /**
     * attribute id 설정
     * 
     * @param attrId
     */
    protected void setAttributeId(int attrId) {
        this.attrId = attrId;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("attribute-id = ").append(ManagerUtil.getAttributeName(attrId)).append("\n");

        return sb.toString();
    }
}
