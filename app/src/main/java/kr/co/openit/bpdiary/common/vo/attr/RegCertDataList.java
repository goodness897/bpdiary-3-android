/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.util.ArrayList;
import java.util.List;

/**
 * RegCertDataList 정의
 */
public class RegCertDataList extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8091402181171374962L;

    /**
     * 생성자
     */
    public RegCertDataList() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public RegCertDataList(int attrId) {
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
     * 
     */
    private final List<RegCertData> list = new ArrayList<RegCertData>();

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
     * RegCertData add
     * 
     * @param regCertData
     */
    public void addRegCertData(RegCertData regCertData) {
        list.add(regCertData);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("RegCertDataList.count = ").append(count).append("\n");
        sb.append("RegCertDataList.length = ").append(length).append("\n");

        return sb.toString();
    }
}
