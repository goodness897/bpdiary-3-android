/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.io.Serializable;

/**
 * ProdSpecEntry 정의
 */
public class ProdSpecEntry implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3298425080976874886L;

    /**
     * 생성자
     */
    public ProdSpecEntry() {
        // default constructor
    }

    /**
     * specType
     */
    private int specType;

    /**
     * componentId
     */
    private int componentId;

    /**
     * prodSpec
     */
    private String prodSpec;

    /**
     * specType getter
     * 
     * @return specType
     */
    public int getSpecType() {
        return specType;
    }

    /**
     * specType setter
     * 
     * @param specType - the specType to set
     */
    public void setSpecType(int specType) {
        this.specType = specType;
    }

    /**
     * componentId getter
     * 
     * @return componentId
     */
    public int getComponentId() {
        return componentId;
    }

    /**
     * componentId setter
     * 
     * @param componentId - the componentId to set
     */
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    /**
     * prodSpec getter
     * 
     * @return prodSpec
     */
    public String getProdSpec() {
        return prodSpec;
    }

    /**
     * prodSpec setter
     * 
     * @param prodSpec - the prodSpec to set
     */
    public void setProdSpec(String prodSpec) {
        this.prodSpec = prodSpec;
    }

    /**
     * Product Spec Type 정의
     */
    public static class SpecType {

        /**
         * UNSPECIFIED
         */
        public static final int UNSPECIFIED = 0;

        /**
         * SERIAL NUMBER
         */
        public static final int SERIAL_NUMBER = 1;

        /**
         * PART NUMBER
         */
        public static final int PART_NUMBER = 2;

        /**
         * HW REVISION
         */
        public static final int HW_REVISION = 3;

        /**
         * SW REVISION
         */
        public static final int SW_REVISION = 4;

        /**
         * FW REVISION
         */
        public static final int FW_REVISION = 5;

        /**
         * PROTOCOL REVISION
         */
        public static final int PROTOCOL_REVISION = 6;

        /**
         * PROD SPEC GMDN
         */
        public static final int PROD_SPEC_GMDN = 7;

        /**
         * 생성자
         */
        private SpecType() {
            // default constructor
        }

    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("    ").append("ProdSpecEntry.spec-type = ").append(getSpecType()).append("\n");
        sb.append("    ").append("ProdSpecEntry.component-id = ").append(getComponentId()).append("\n");
        sb.append("    ").append("ProdSpecEntry.prod-spec = ").append(getProdSpec()).append("\n");

        return sb.toString();
    }

}
