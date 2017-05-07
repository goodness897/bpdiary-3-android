/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.io.Serializable;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * TypeVer 정의
 */
public class TypeVer implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4181309404476949806L;

    /**
     * 생성자
     */
    public TypeVer() {
        // default constructor
    }

    /**
     * type
     */
    private int type;

    /**
     * version
     */
    private int version;

    /**
     * type getter
     * 
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * type setter
     * 
     * @param type - the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * version getter
     * 
     * @return version
     */
    public int getVersion() {
        return version;
    }

    /**
     * version setter
     * 
     * @param version - the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("type = ").append(ManagerUtil.getDeviceSpecName(type)).append("\n");
        sb.append("version = ").append(version).append("\n");

        return sb.toString();
    }
}
