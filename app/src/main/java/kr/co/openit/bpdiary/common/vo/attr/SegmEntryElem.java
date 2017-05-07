/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.io.Serializable;

/**
 * SegmEntryElem
 */
public class SegmEntryElem implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2673750887965399767L;

    /**
     * 생성자
     */
    public SegmEntryElem() {
        // default constructor
    }

    /**
     * class id
     */
    private int classId;

    /**
     * metricType
     */
    private TYPE metricType;

    /**
     * handle
     */
    private int handle;

    /**
     * attrValMap
     */
    private AttrValMap attrValMap;

    /**
     * classId getter
     * 
     * @return classId
     */
    public int getClassId() {
        return classId;
    }

    /**
     * classId setter
     * 
     * @param classId - the classId to set
     */
    public void setClassId(int classId) {
        this.classId = classId;
    }

    /**
     * metricType getter
     * 
     * @return metricType
     */
    public TYPE getMetricType() {
        return metricType;
    }

    /**
     * metricType setter
     * 
     * @param metricType - the metricType to set
     */
    public void setMetricType(TYPE metricType) {
        this.metricType = metricType;
    }

    /**
     * handle getter
     * 
     * @return handle
     */
    public int getHandle() {
        return handle;
    }

    /**
     * handle setter
     * 
     * @param handle - the handle to set
     */
    public void setHandle(int handle) {
        this.handle = handle;
    }

    /**
     * attrValMap getter
     * 
     * @return attrValMap
     */
    public AttrValMap getAttrValMap() {
        return attrValMap;
    }

    /**
     * attrValMap setter
     * 
     * @param attrValMap - the attrValMap to set
     */
    public void setAttrValMap(AttrValMap attrValMap) {
        this.attrValMap = attrValMap;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("SegmEntryElem.class-id = ").append(classId).append("\n");
        sb.append("    ")
          .append("SegmEntryElem.metric-type.nom-partition = ")
          .append(metricType.getPartition())
          .append(" (")
          .append(metricType.getPartitionName())
          .append(")\n");
        sb.append("    ").append("SegmEntryElem.metric-type.code = ").append(metricType.getCode()).append("\n");
        sb.append("    ").append("SegmEntryElem.handle = ").append(handle).append("\n");
        sb.append("    ").append("SegmEntryElem.attrValMap.count = ").append(attrValMap.getCount()).append("\n");

        for (int i = 0, cnt = attrValMap.getCount(); i < cnt; i++) {
            sb.append("        ").append(attrValMap.getAttrValMapEntry(i));
        }

        return sb.toString();
    }
}
