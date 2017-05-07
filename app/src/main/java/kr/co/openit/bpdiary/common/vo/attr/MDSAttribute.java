/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2012, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.util.HashMap;
import java.util.Map;

/**
 * MDS Attribute
 */
public class MDSAttribute extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 512023160524925286L;

    /**
     * 생성자
     */
    public MDSAttribute() {
        // default constructor
        super();
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public MDSAttribute(int attrId) {
        super(attrId);
    }

    /**
     * value 저장소
     */
    private final Map<String, Object> valueMap = new HashMap<String, Object>();

    /**
     * value 설정
     * 
     * @param key
     * @param value
     */
    public void setValue(String key, Object value) {
        valueMap.put(key, value);
    }

    /**
     * value 반환
     * 
     * @param key
     * @return
     */
    public Object getValue(String key) {
        return valueMap.get(key);
    }

    /*
     * (non-Javadoc)
     * @see kr.co.openit.healthup.common.vo.attr.Attribute#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("value            : ");
        sb.append(valueMap);

        return sb.toString();
    }
}
