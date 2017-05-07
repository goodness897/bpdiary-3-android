/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.util.ArrayList;
import java.util.List;

/**
 * AttrValMap 정의
 */
public class AttrValMap extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2447370717796077428L;

    /**
     * 생성자
     */
    public AttrValMap() {
        // default constructor
    }

    /**
     * 생성자
     */
    public AttrValMap(int attrId) {
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
     * AttrValMapEntry list
     */
    private final List<AttrValMapEntry> entryList = new ArrayList<AttrValMapEntry>();

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
     * AttrValMapEntry add
     * 
     * @param attrId
     * @param length
     */
    public void addAttrValMapEntry(int attrId, int length) {
        entryList.add(new AttrValMapEntry(attrId, length));
    }

    /**
     * AttrValMapEntryList 반환
     * 
     * @return
     */
    public List<AttrValMapEntry> getAttrValMapEntryList() {
        return entryList;
    }

    /**
     * AttrValMapEntry getter
     * 
     * @param index
     * @return
     */
    public AttrValMapEntry getAttrValMapEntry(int index) {
        return entryList.get(index);
    }

    /**
     * 해당 attribute id 가 존재하는지 여부
     * 
     * @param attrId
     * @return
     */
    public int isExistAttrId(int attrId) {
        for (int i = 0, len = entryList.size(); i < len; i++) {
            if (attrId == entryList.get(i).getAttributeId()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("    ").append("AttrValMap.count = ").append(count).append("\n");
        sb.append("    ").append("AttrValMap.length = ").append(length).append("\n");

        for (int i = 0; i < count; i++) {
            sb.append(entryList.get(i).toString());
        }

        return sb.toString();
    }
}
