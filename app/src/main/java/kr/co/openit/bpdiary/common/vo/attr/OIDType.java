/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * OIDType 정의
 */
public class OIDType extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8386932713433019254L;

    /**
     * 생성자
     */
    public OIDType() {
        // default constructor
    }

    /**
     * 생성자
     */
    public OIDType(int attrId) {
        // default constructor
        super(attrId);
    }

    /**
     * 생성자
     * 
     * @param attrId
     * @param value
     */
    public OIDType(int attrId, int value) {
        super(attrId);
        this.value = value;
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
     * 
     */
    public void setAttrId(int attrId) {
        setAttributeId(attrId);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        switch (getAttributeId()) {
            case Nomenclature.Attribute.MDC_ATTR_UNIT_CODE:
                sb.append(ManagerUtil.getUnitCodeName(value));
                break;

            default:
                sb.append(ManagerUtil.getAttributeName(value));
                break;
        }

        sb.append("\n");

        return sb.toString();
    }

}
