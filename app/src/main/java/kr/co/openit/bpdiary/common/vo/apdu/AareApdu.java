package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.utils.ByteUtil;

/**
 * association response VO
 */
public class AareApdu extends Apdu implements IApduBody {

    /**
     * 생성자
     */
    public AareApdu() {
        // default constructor
        setChoiceType(HealthcareConstants.ApduChoiceType.ASSOCIATION_RESPONSE);
        setChoiceTypeLength((short)0x002c);
    }

    /**
     * 생성자
     *
     * @param dataProtoId
     * @param result
     */
    public AareApdu(short dataProtoId, short result) {
        setChoiceType(HealthcareConstants.ApduChoiceType.ASSOCIATION_RESPONSE);
        setChoiceTypeLength((short)0x002c);

        this.dataProtoId = dataProtoId;
        this.result = result;
    }

    /**
     * result
     */
    private short result = (short)0x0000;

    /**
     * result msg (logging)
     */
    private String resultMsg;

    /**
     * data-proto-id
     */
    private short dataProtoId;

    /**
     * data-proto-info length
     */
    private final short dataProtoInfoLength = (short)0x0026;

    /**
     * protocolVersion
     */
    private final int protocolVersion = 0x80000000;

    /**
     * encoding rules
     */
    private final int encodingRules = 0x8000;

    /**
     * nomenclatureVersion
     */
    private final int nomenclatureVersion = 0x80000000;

    /**
     * functionalUnits - normal association
     */
    private final int functionalUnits = 0x00000000;

    /**
     * systemType = sys-type-manager
     */
    private final int systemType = 0x80000000;

    /**
     * system-id length
     */
    private final short systemIdLength = 0x0008;

    /**
     * system-id
     */
    private final String systemId = "openit";

    /**
     * config-id - Manager's response to config-id is always 0
     */
    private final short configId = (short)0x0000;

    /**
     * data-req-mode-flags - Manager's response to data-req-mode-flags is always 0
     */
    private final short dataReqModeFlags = (short)0x0000;

    /**
     * data-req-init-agent-count is always 0
     */
    private final byte dataReqInitAgentCount = (short)0x00;

    /**
     * data-req-init-manager-count is always 0
     */
    private final byte dataReqInitManagerCount = (short)0x00;

    /**
     * optionList.count = 0
     */
    private final short optionListCount = (short)0x0000;

    /**
     * optionList.length = 0
     */
    private final short optionListLength = (short)0x0000;

    /**
     * result getter
     * 
     * @return result
     */
    public short getResult() {
        return result;
    }

    /**
     * result setter
     * 
     * @param result - the result to set
     */
    public void setResult(short result) {
        this.result = result;

        switch (result) {
            case HealthcareConstants.AssociationResult.ACCEPTED:
                this.resultMsg = "accepted";
                break;

            case HealthcareConstants.AssociationResult.REJECTED_PERMANENT:
                this.resultMsg = "rejected-permanent";
                break;

            case HealthcareConstants.AssociationResult.REJECTED_TRANSIENT:
                this.resultMsg = "rejected-transient";
                break;

            case HealthcareConstants.AssociationResult.ACCEPTED_UNKNOWN_CONFIG:
                this.resultMsg = "accepted-unknown-config";
                break;

            case HealthcareConstants.AssociationResult.REJECTED_NO_COMMON_PROTOCOL:
                this.resultMsg = "rejected-no-common-protocol";
                break;

            case HealthcareConstants.AssociationResult.REJECTED_NO_COMMON_PARAMETER:
                this.resultMsg = "rejected-no-common-parameter";
                break;

            case HealthcareConstants.AssociationResult.REJECTED_UNKNOWN:
                this.resultMsg = "rejected-unknown";
                break;

            case HealthcareConstants.AssociationResult.REJECTED_UNAUTHORIZED:
                this.resultMsg = "rejected-unauthorized";
                break;

            case HealthcareConstants.AssociationResult.REJECTED_UNSUPPORTED_ASSOC_VERSION:
                this.resultMsg = "rejected-unsupported-assoc-version";
                break;
        }
    }

    /**
     * dataProtoId getter
     * 
     * @return dataProtoId
     */
    public short getDataProtoId() {
        return dataProtoId;
    }

    /**
     * dataProtoId setter
     * 
     * @param dataProtoId - the dataProtoId to set
     */
    public void setDataProtoId(short dataProtoId) {
        this.dataProtoId = dataProtoId;
    }

    /**
     * dataProtoInfoLength getter
     * 
     * @return dataProtoInfoLength
     */
    public short getDataProtoInfoLength() {
        return dataProtoInfoLength;
    }

    /**
     * protocolVersion getter
     * 
     * @return protocolVersion
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * encodingRules getter
     * 
     * @return encodingRules
     */
    public int getEncodingRules() {
        return encodingRules;
    }

    /**
     * nomenclatureVersion getter
     * 
     * @return nomenclatureVersion
     */
    public int getNomenclatureVersion() {
        return nomenclatureVersion;
    }

    /**
     * functionalUnits getter
     * 
     * @return functionalUnits
     */
    public int getFunctionalUnits() {
        return functionalUnits;
    }

    /**
     * systemType getter
     * 
     * @return systemType
     */
    public int getSystemType() {
        return systemType;
    }

    /**
     * systemIdLength getter
     * 
     * @return systemIdLength
     */
    public short getSystemIdLength() {
        return systemIdLength;
    }

    /**
     * systemId getter
     * 
     * @return systemId
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * configId getter
     * 
     * @return configId
     */
    public short getConfigId() {
        return configId;
    }

    /**
     * dataReqModeFlags getter
     * 
     * @return dataReqModeFlags
     */
    public short getDataReqModeFlags() {
        return dataReqModeFlags;
    }

    /**
     * dataReqInitAgentCount getter
     * 
     * @return dataReqInitAgentCount
     */
    public byte getDataReqInitAgentCount() {
        return dataReqInitAgentCount;
    }

    /**
     * dataReqInitManagerCount getter
     * 
     * @return dataReqInitManagerCount
     */
    public byte getDataReqInitManagerCount() {
        return dataReqInitManagerCount;
    }

    /**
     * optionListCount getter
     * 
     * @return optionListCount
     */
    public short getOptionListCount() {
        return optionListCount;
    }

    /**
     * optionListLength getter
     * 
     * @return optionListLength
     */
    public short getOptionListLength() {
        return optionListLength;
    }

    @Override
    public byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(getChoiceTypeLength() + 4);

        buffer.put(super.getBytes());

        buffer.putShort(result);

        if (result == HealthcareConstants.AssociationResult.ACCEPTED || result == HealthcareConstants.AssociationResult.ACCEPTED_UNKNOWN_CONFIG) {
            buffer.putShort(dataProtoId);
            buffer.putShort(dataProtoInfoLength);
            buffer.putInt(protocolVersion);
            buffer.putShort(ByteUtil.ushort2short(encodingRules));
            buffer.putInt(nomenclatureVersion);
            buffer.putInt(functionalUnits);
            buffer.putInt(systemType);
            buffer.putShort(systemIdLength);
            buffer.put(ByteUtil.getBytes(systemId, 8));
            buffer.putShort(configId);
            buffer.putShort(dataReqModeFlags);
            buffer.put(dataReqInitAgentCount);
            buffer.put(dataReqInitManagerCount);
            buffer.putShort(optionListCount);
            buffer.putShort(optionListLength);
        } else {
            buffer.putShort((short)0x0000);
            buffer.putShort((short)0x0000);
        }

        return buffer.array();
    }

    /**
     * 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(super.toString());

        sb.append(Integer.toString(result, 16)).append("\t\tresult = ").append(resultMsg).append("\n");
        sb.append(Integer.toString(dataProtoId, 16)).append("\t\tdata-proto-id = ").append(dataProtoId).append("\n");
        sb.append(Integer.toString(dataProtoInfoLength, 16))
          .append("\t\tdata-proto-info length = ")
          .append(dataProtoInfoLength)
          .append("\n");
        sb.append(Integer.toString(protocolVersion, 16)).append("\t\tprotocolVersion\n");
        sb.append(Integer.toString(encodingRules, 16)).append("\t\tencoding rules = ");

        switch (encodingRules) {
            case HealthcareConstants.EncodingRule.MDER:
                sb.append("MDER");
                break;

            case HealthcareConstants.EncodingRule.MDER_PER:
                sb.append("MDER or PER");
                break;

            case HealthcareConstants.EncodingRule.MDER_XER:
                sb.append("MDER or XER");
                break;

            case HealthcareConstants.EncodingRule.MDER_XER_PER:
                sb.append("MDER or XER or PER");
                break;

            case HealthcareConstants.EncodingRule.PER:
                sb.append("PER");
                break;

            case HealthcareConstants.EncodingRule.XER:
                sb.append("XER");
                break;

            case HealthcareConstants.EncodingRule.XER_PER:
                sb.append("XER or PER");
                break;
        }
        sb.append("\n");

        sb.append(Integer.toString(nomenclatureVersion, 16)).append("\t\tnomenclatureVersion\n");
        sb.append(Integer.toString(functionalUnits, 16)).append("\t\tfunctionalUnits\n");
        sb.append(Integer.toString(systemType, 16)).append("\t\tsystemType = sys-type-manager\n");
        sb.append(Integer.toString(systemIdLength, 16))
          .append("\t\tsystem-id length = 8 and value (manufacturer- and device- specific)\n")
          .append(systemId)
          .append("\n");
        sb.append(Integer.toString(configId, 16)).append("\t\tManager’s response to config-id is always 0\n");
        sb.append(Integer.toString(dataReqModeFlags, 16))
          .append("\t\tManager’s response to data-req-mode-flags is always 0\n");
        sb.append(Integer.toString(dataReqInitAgentCount, 16))
          .append(Integer.toString(dataReqInitManagerCount, 16))
          .append("\t\tdata-req-init-agent-count and data-req-init-manager-count are always 0\n");
        sb.append(Integer.toString(optionListCount, 16))
          .append(Integer.toString(optionListLength, 16))
          .append("\t\toptionList.count = 0 | optionList.length = 0\n");

        return sb.toString();
    }
}
