/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.util.ArrayList;
import java.util.List;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * MetricIdList 정의
 */
public class MetricIdList extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2535201412415412291L;

    /**
     * 생성자
     */
    public MetricIdList() {
        // default constructor
    }

    /**
     * 생성자
     */
    public MetricIdList(int attrId) {
        // default constructor
        super(attrId);
    }

    /**
     * count
     */
    private int count;

    /**
     * length
     */
    private int length;

    /**
     * OIDType list
     */
    private final List<OIDType> list = new ArrayList<OIDType>();

    /**
     * count getter
     * 
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * count setter
     * 
     * @param count - the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

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
     * metric id add
     * 
     * @param attrId
     */
    public void addMetricId(int attrId) {
        list.add(new OIDType(attrId));
    }

    /**
     * metric id add
     * 
     * @param attrId
     * @param value
     */
    public void addMetricId(int attrId, int value) {
        list.add(new OIDType(attrId, value));
    }

    /**
     * oidType 반환 (index : 0)
     * 
     * @return oidType
     */
    public OIDType getOIDType() {
        return list.get(0);
    }

    /**
     * oidType 반환
     * 
     * @param index
     * @return oidType
     */
    public OIDType getOIDType(int index) {
        return list.get(index);
    }

    /**
     * 해당 index 에 oidType 설정
     * 
     * @param index
     * @param oidType
     */
    public void setMetricId(int index, OIDType oidType) {
        list.set(index, oidType);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        sb.append("MetricIdList.count = ").append(count).append("\n");

        sb.append("MetricIdList.length = ").append(length).append("\n");

        for (int i = 0; i < count; i++) {
            sb.append(ManagerUtil.getAttributeName(list.get(i).getAttributeId())).append(",\n");
        }

        return sb.toString();
    }
}
