/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * TYPE 정의
 */
public class TYPE extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -121651612995873175L;

    /**
     * 생성자
     */
    public TYPE() {
        // default constructor
    }

    /**
     * 생성자
     */
    public TYPE(int attrId) {
        // default constructor
        super(attrId);
    }

    /**
     * partition
     */
    private int partition;

    /**
     * code
     */
    private int code;

    /**
     * partition getter
     * 
     * @return partition
     */
    public int getPartition() {
        return partition;
    }

    /**
     * partition 명 getter
     * 
     * @return
     */
    public String getPartitionName() {
        switch (partition) {
            case PartitionCode.MDC_PART_OBJ:
                return "MDC_PART_OBJ";
            case PartitionCode.MDC_PART_SCADA:
                return "MDC_PART_SCADA";
            case PartitionCode.MDC_PART_DIM:
                return "MDC_PART_DIM";
            case PartitionCode.MDC_PART_INFRA:
                return "MDC_PART_INFRA";
            case PartitionCode.MDC_PART_PHD_DM:
                return "MDC_PART_PHD_DM";
            case PartitionCode.MDC_PART_PHD_HF:
                return "MDC_PART_PHD_HF";
            case PartitionCode.MDC_PART_PHD_AI:
                return "MDC_PART_PHD_AI";
            case PartitionCode.MDC_PART_RET_CODE:
                return "MDC_PART_RET_CODE";
            case PartitionCode.MDC_PART_EXT_NOM:
                return "MDC_PART_EXT_NOM";
            default:
                return null;
        }
    }

    /**
     * partition setter
     * 
     * @param partition - the partition to set
     */
    public void setPartition(int partition) {
        this.partition = partition;
    }

    /**
     * code getter
     * 
     * @return code
     */
    public int getCode() {
        return code;
    }

    /**
     * code setter
     * 
     * @param code - the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Partition Code 정의
     */
    public final class PartitionCode {

        /**
         * MDC_PART_OBJ
         */
        public static final int MDC_PART_OBJ = 1;

        /**
         * MDC_PART_SCADA
         */
        public static final int MDC_PART_SCADA = 2;

        /**
         * MDC_PART_DIM
         */
        public static final int MDC_PART_DIM = 4;

        /**
         * MDC_PART_INFRA
         */
        public static final int MDC_PART_INFRA = 8;

        /**
         * MDC_PART_PHD_DM
         */
        public static final int MDC_PART_PHD_DM = 128;

        /**
         * MDC_PART_PHD_HF
         */
        public static final int MDC_PART_PHD_HF = 129;

        /**
         * MDC_PART_PHD_AI
         */
        public static final int MDC_PART_PHD_AI = 130;

        /**
         * MDC_PART_RET_CODE
         */
        public static final int MDC_PART_RET_CODE = 255;

        /**
         * MDC_PART_EXT_NOM
         */
        public static final int MDC_PART_EXT_NOM = 256;

        /**
         * 생성자
         */
        private PartitionCode() {
            // default constructor
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append(getPartitionName()).append(" | ").append(ManagerUtil.getAttributeName(getCode())).append("\n");

        return sb.toString();
    }
}
