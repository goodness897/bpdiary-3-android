/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * SystemModel 정의
 */
public class SystemModel extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1546166537052214671L;

    /**
     * 생성자
     */
    public SystemModel() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public SystemModel(int attrId) {
        super(attrId);
    }

    /**
     * manufacturer
     */
    private String manufacturer;

    /**
     * modelNumber
     */
    private String modelNumber;

    /**
     * manufacturer getter
     * 
     * @return manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * manufacturer setter
     * 
     * @param manufacturer - the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * modelNumber getter
     * 
     * @return modelNumber
     */
    public String getModelNumber() {
        return modelNumber;
    }

    /**
     * modelNumber setter
     * 
     * @param modelNumber - the modelNumber to set
     */
    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        sb.append(manufacturer).append("\n");
        sb.append(modelNumber).append("\n");

        return sb.toString();
    }
}
