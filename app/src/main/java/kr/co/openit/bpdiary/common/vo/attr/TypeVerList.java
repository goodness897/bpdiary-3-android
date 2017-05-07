/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.util.ArrayList;
import java.util.List;

/**
 * TypeVerList 정의
 */
public class TypeVerList extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5528693192779750990L;

    /**
     * 생성자
     */
    public TypeVerList() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public TypeVerList(int attrId) {
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
     * typeVerList
     */
    private final List<TypeVer> typeVerList = new ArrayList<TypeVer>();

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
     * typeVer add
     * 
     * @param type
     * @param version
     */
    public void addTypeVer(int type, int version) {
        TypeVer typeVer = new TypeVer();
        typeVer.setType(type);
        typeVer.setVersion(version);

        typeVerList.add(typeVer);
    }

    /**
     * typeVer 반환 (index : 0)
     * 
     * @return
     */
    public TypeVer getTypeVer() {
        return typeVerList.get(0);
    }

    /**
     * typeVer 반환
     * 
     * @param index 반환할 index
     * @return
     */
    public TypeVer getTypeVer(int index) {
        return typeVerList.get(index);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        sb.append("TypeVerList.count = ").append(count).append("\n");
        sb.append("TypeVerList.length = ").append(length).append("\n");

        for (int i = 0; i < count; i++) {
            sb.append(typeVerList.get(i).toString());
        }

        return sb.toString();
    }
}
