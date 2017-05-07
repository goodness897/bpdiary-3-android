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
 * SupplementalTypeList 정의
 */
public class SupplementalTypeList extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6209870144936417173L;

    /**
     * 생성자
     */
    public SupplementalTypeList() {
        // default constructor
    }

    /**
     * 생성자
     */
    public SupplementalTypeList(int attrId) {
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
     * TYPE list
     */
    private final List<TYPE> typeList = new ArrayList<TYPE>();

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
     * TYPE add
     * 
     * @param partition
     * @param code
     */
    public void addTYPE(int partition, int code) {
        TYPE type = new TYPE();
        type.setPartition(partition);
        type.setCode(code);
        typeList.add(type);
    }

    /**
     * TYPE 반환 (index : 0)
     * 
     * @return
     */
    public TYPE getTYPE() {
        return typeList.get(0);
    }

    /**
     * TYPE 반환
     * 
     * @param index
     * @return
     */
    public TYPE getTYPE(int index) {
        return typeList.get(index);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("SupplementalTypeList.count = ").append(count).append("\n");
        sb.append("SupplementalTypeList.length = ").append(length).append("\n");

        for (int i = 0; i < count; i++) {
            sb.append(typeList.get(i).getPartitionName())
              .append(" | ")
              .append(ManagerUtil.getAttributeName(typeList.get(i).getCode()))
              .append("\n");
        }

        return sb.toString();
    }
}
