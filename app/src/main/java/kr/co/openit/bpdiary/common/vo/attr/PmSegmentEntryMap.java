/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import java.util.ArrayList;
import java.util.List;

/**
 * PmSegmentEntryMap 정의
 */
public class PmSegmentEntryMap extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4154157785689410815L;

    /**
     * 생성자
     */
    public PmSegmentEntryMap() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public PmSegmentEntryMap(int attrId) {
        super(attrId);
    }

    /**
     * segmEntryHeader
     */
    private int segmEntryHeader;

    /**
     * SegmEntryElem List
     */
    private final List<SegmEntryElem> entryList = new ArrayList<SegmEntryElem>();

    /**
     * segmEntryHeader getter
     * 
     * @return segmEntryHeader
     */
    public int getSegmEntryHeader() {
        return segmEntryHeader;
    }

    /**
     * segmEntryHeader setter
     * 
     * @param segmEntryHeader - the segmEntryHeader to set
     */
    public void setSegmEntryHeader(int segmEntryHeader) {
        this.segmEntryHeader = segmEntryHeader;
    }

    /**
     * segmEntryElem add
     * 
     * @param segmEntryElem
     */
    public void addSegmEntryElem(SegmEntryElem segmEntryElem) {
        entryList.add(segmEntryElem);
    }

    /**
     * SegmEntryElemList count 반환
     * 
     * @return
     */
    public int getSegmEntryElemListCount() {
        return entryList.size();
    }

    /**
     * SegmEntryElem getter
     * 
     * @param index
     * @return
     */
    public SegmEntryElem getSegmEntryElem(int index) {
        return entryList.get(index);
    }

    /**
     * SegmEntryHeader 정의
     */
    public static final class SegmEntryHeader {

        /**
         * SEG_ELEM_HDR_ABSOLUTE_TIME
         */
        public static final int SEG_ELEM_HDR_ABSOLUTE_TIME = 0x8000;

        /**
         * SEG_ELEM_HDR_RELATIVE_TIME
         */
        public static final int SEG_ELEM_HDR_RELATIVE_TIME = 0x4000;

        /**
         * SEG_ELEM_HDR_HIRES_RELATIVE_TIME
         */
        public static final int SEG_ELEM_HDR_HIRES_RELATIVE_TIME = 0x2000;

        /**
         * getSegmEmtryHeaderName
         * 
         * @param value
         */
        public static String getSegmEmtryHeaderName(int value) {

            switch (value) {
                case SEG_ELEM_HDR_ABSOLUTE_TIME:
                    return "SEG_ELEM_HDR_ABSOLUTE_TIME";

                case SEG_ELEM_HDR_RELATIVE_TIME:
                    return "SEG_ELEM_HDR_RELATIVE_TIME";

                case SEG_ELEM_HDR_HIRES_RELATIVE_TIME:
                    return "SEG_ELEM_HDR_HIRES_RELATIVE_TIME";

                default:
                    return null;
            }
        }

        /**
         * 생성자
         */
        private SegmEntryHeader() {
            // default constructor
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("    ")
          .append("SegmEntryHeader = ")
          .append(SegmEntryHeader.getSegmEmtryHeaderName(segmEntryHeader))
          .append("\n");
        sb.append("    ").append("SegmEntryElemList.count = ").append(entryList.size()).append("\n");

        for (int i = 0, cnt = entryList.size(); i < cnt; i++) {
            sb.append("    ").append(entryList.get(i));
        }

        return sb.toString();
    }
}
