/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductionSpec 정의
 */
public class ProductionSpec extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5042323076584410463L;

    /**
     * 생성자
     */
    public ProductionSpec() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public ProductionSpec(int attrId) {
        // default constructor
        super(attrId);
    }

    /**
     * count
     */
    public int count;

    /**
     * length
     */
    public int length;

    /**
     * ProdSpecEntry list
     */
    public List<ProdSpecEntry> entryList = new ArrayList<ProdSpecEntry>();

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
     * ProdSpecEntry add
     * 
     * @param specType
     * @param componentId
     * @param prodSpec
     */
    public void addProdSpecEntry(int specType, int componentId, String prodSpec) {
        ProdSpecEntry entry = new ProdSpecEntry();
        entry.setSpecType(specType);
        entry.setComponentId(componentId);
        entry.setProdSpec(prodSpec);

        entryList.add(entry);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        sb.append("ProductionSpec.count = ").append(count).append("\n");
        sb.append("ProductionSpec.length = ").append(length).append("\n");

        for (int i = 0; i < count; i++) {
            sb.append(entryList.get(i).toString());
        }

        return sb.toString();
    }
}
