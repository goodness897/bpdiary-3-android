/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * AuthBodyAndStrucType 정의
 */
public class AuthBodyAndStrucType {

    /**
     * 생성자
     */
    public AuthBodyAndStrucType() {
        // default constructor
    }

    /**
     * authBody
     */
    private int authBody;

    /**
     * authBodyStrucType
     */
    private int authBodyStrucType;

    /**
     * authBody getter
     * 
     * @return authBody
     */
    public int getAuthBody() {
        return authBody;
    }

    /**
     * authBody setter
     * 
     * @param authBody - the authBody to set
     */
    public void setAuthBody(int authBody) {
        this.authBody = authBody;
    }

    /**
     * authBodyStrucType getter
     * 
     * @return authBodyStrucType
     */
    public int getAuthBodyStrucType() {
        return authBodyStrucType;
    }

    /**
     * authBodyStrucType setter
     * 
     * @param authBodyStrucType - the authBodyStrucType to set
     */
    public void setAuthBodyStrucType(int authBodyStrucType) {
        this.authBodyStrucType = authBodyStrucType;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        return sb.toString();
    }

}
