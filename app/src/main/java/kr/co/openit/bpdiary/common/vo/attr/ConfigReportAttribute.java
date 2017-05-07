/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2012, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.util.ArrayList;
import java.util.List;

/**
 * configuration attribute value object
 */
public class ConfigReportAttribute extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1593153184662783478L;

    /**
     * 생성자
     */
    public ConfigReportAttribute() {
        // default constructor
        super();
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public ConfigReportAttribute(int attrId) {
        super(attrId);
    }

    /**
     * attribute value list
     */
    private final List<Integer> valueList = new ArrayList<Integer>();

    /**
     * sub attribute list
     */
    private final List<ConfigReportAttribute> subList = new ArrayList<ConfigReportAttribute>();

    /**
     * value 추가
     * 
     * @param value
     */
    public void addValue(int value) {
        valueList.add(value);
    }

    /**
     * 해당 index 에 value 수정
     * 
     * @param index
     * @param value
     */
    public void setValue(int index, int value) {
        valueList.set(index, value);
    }

    /**
     * value 반환
     * 
     * @return
     */
    public Integer getValue() {
        return valueList.get(0);
    }

    /**
     * 해당 index 의 value 반환
     * 
     * @param index
     * @return
     */
    public Integer getValue(int index) {
        return valueList.get(index);
    }

    /**
     * sub attribute 추가
     * 
     * @param subAttrId
     * @param value
     */
    public void addSubAttribute(int subAttrId, int value) {
        ConfigReportAttribute sub = new ConfigReportAttribute(subAttrId);
        sub.addValue(value);

        subList.add(sub);
    }

    /**
     * sub attribute 의 value 수정
     * 
     * @param subAttrId
     * @param index
     * @param value
     */
    public void setSubAttribute(int subAttrId, int index, int value) {
        ConfigReportAttribute sub = new ConfigReportAttribute(subAttrId);
        sub.addValue(value);

        subList.set(index, sub);
    }

    /**
     * sub attribute 의 모든 element 삭제
     */
    public void clearSubAttribute() {
        subList.clear();
    }

    /**
     * sub attribute list 반환
     * 
     * @return
     */
    public List<ConfigReportAttribute> getSubAttributeList() {
        return subList;
    }

    /**
     * value count 반환
     * 
     * @return
     */
    public int getValueCount() {
        return valueList.size();
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

        for (int a = 0, len = valueList.size(); a < len; a++) {
            sb.append(valueList.get(a)).append(", ");
        }

        if (subList != null && !subList.isEmpty()) {
            sb.append("\n##sub attribute    : ##");

            for (int b = 0, loop = subList.size(); b < loop; b++) {
                sb.append(subList.get(b).toString());
            }

            sb.append("\n#######################");
        }

        return sb.toString();
    }
}
