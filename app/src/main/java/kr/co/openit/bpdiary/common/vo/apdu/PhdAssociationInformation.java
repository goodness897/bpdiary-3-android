package kr.co.openit.bpdiary.common.vo.apdu;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.utils.ByteUtil;

/**
 * PhdAssociationInformation VO
 */
public class PhdAssociationInformation {

    /**
     * Debugging
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * 생성자
     * 
     * @param body
     */
    public PhdAssociationInformation(short dataProtoId, short dataProtoInfoLength, byte[] body) {

        this.dataProtoId = dataProtoId;
        this.dataProtoInfoLength = dataProtoInfoLength;

        ByteBuffer buf = ByteBuffer.allocate(body.length).put(body);
        buf.position(0);

        if (buf.position() < dataProtoInfoLength) {
            buf.getInt(); // protocolVersion
        }

        if (buf.position() < dataProtoInfoLength) {
            encodingRule = ByteUtil.short2ushort(buf.getShort()); // encoding rules
        }

        if (buf.position() < dataProtoInfoLength) {
            buf.getInt(); // nomenclatureVersion
        }

        if (buf.position() < dataProtoInfoLength) {
            functionalUnits = buf.getInt(); // functionalUnits
        }

        if (buf.position() < dataProtoInfoLength) {
            buf.getInt(); // systemType
        }

        if (buf.position() < dataProtoInfoLength) {
            systemIdLength = buf.getShort();
            systemId = ByteUtil.getString(buf, systemIdLength);
        }

        if (buf.position() < dataProtoInfoLength) {
            devConfigId = buf.getShort();
        }

        if (buf.position() < dataProtoInfoLength) {
            buf.getShort(); // data-req-mode-flags
        }

        if (buf.position() < dataProtoInfoLength) {
            buf.getShort(); // data-req-init-agent-count
        }

        if (buf.position() < dataProtoInfoLength) {
            buf.getShort(); // optioneList count
        }

        if (buf.position() < dataProtoInfoLength) {
            buf.getShort(); // optioneList length
        }

    }

    /**
     * data-proto-id = 20601
     */
    private short dataProtoId;

    /**
     * data-proto-info length = 38
     */
    private short dataProtoInfoLength;

    /**
     * protocolVersion
     */
    private final int protocolVersion = 0x80000000;

    /**
     * encoding rules = MDER or PER
     */
    private int encodingRule = 0xa000;

    /**
     * nomenclatureVersion
     */
    private final int nomenclatureVersion = 0x80000000;

    /**
     * functionalUnits
     */
    private int functionalUnits;

    /**
     * systemType = sys-type-agent
     */
    private final int systemType = 0x00800000;

    /**
     * system-id length = 8
     */
    private short systemIdLength;

    /**
     * system-id
     */
    private String systemId;

    /**
     * dev-config-id
     */
    private int devConfigId;

    /**
     * data-req-mode-flags
     */
    private final short dataReqModeFlags = (short)0x0001;

    /**
     * data-req-init-agent-count
     */
    private final byte dataReqInitAgentCount = 0x01;

    /**
     * data-req-init-manager-count
     */
    private final byte dataReqInitManagerCount = 0x00;

    /**
     * optionList.count = 0
     */
    private final short optionListCount = (short)0x0000;

    /**
     * optionList.length = 0
     */
    private final short optionListLength = (short)0x0000;

    /**
     * data-proto-id 반환
     * 
     * @return data-proto-id
     */
    public short getDataProtoId() {
        return dataProtoId;
    }

    /**
     * data-proto-id 설정
     * 
     * @param dataProtoId - the dataProtoId to set
     */
    public void setDataProtoId(short dataProtoId) {
        this.dataProtoId = dataProtoId;
    }

    /**
     * data-proto-info length 반환
     * 
     * @return data-proto-info length
     */
    public short getDataProtoInfoLength() {
        return dataProtoInfoLength;
    }

    /**
     * data-proto-info length 설정
     * 
     * @param dataProtoInfoLength - the dataProtoInfoLength to set
     */
    public void setDataProtoInfoLength(short dataProtoInfoLength) {
        this.dataProtoInfoLength = dataProtoInfoLength;
    }

    /**
     * functionalUnits 반환
     * 
     * @return functionalUnits
     */
    public int getFunctionalUnits() {
        return functionalUnits;
    }

    /**
     * functionalUnits 설정
     * 
     * @param functionalUnits - the functionalUnits to set
     */
    public void setFunctionalUnits(int functionalUnits) {
        this.functionalUnits = functionalUnits;
    }

    /**
     * system-id length 반환
     * 
     * @return system-id length
     */
    public short getSystemIdLength() {
        return systemIdLength;
    }

    /**
     * system-id length 설정
     * 
     * @param systemIdLength - the systemIdLength to set
     */
    public void setSystemIdLength(short systemIdLength) {
        this.systemIdLength = systemIdLength;
    }

    /**
     * system-id 반환
     * 
     * @return
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * system-id 설정
     * 
     * @param systemId - the systemId to set
     */
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * dev-config-id 반환
     * 
     * @return dev-config-id
     */
    public int getDevConfigId() {
        return devConfigId;
    }

    /**
     * dev-config-id 설정
     * 
     * @param devConfigId - the devConfigId to set
     */
    public void setDevConfigId(short devConfigId) {
        this.devConfigId = devConfigId;
    }

    /**
     * protocolVersion 반환
     * 
     * @return protocolVersion
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * encoding rules 반환
     * 
     * @return encoding rules
     */
    public int getEncodingRule() {
        return encodingRule;
    }

    /**
     * nomenclatureVersion 반환
     * 
     * @return nomenclatureVersion
     */
    public int getNomenclatureVersion() {
        return nomenclatureVersion;
    }

    /**
     * systemType 반환
     * 
     * @return systemType
     */
    public int getSystemType() {
        return systemType;
    }

    /**
     * data-req-mode-flags 반환
     * 
     * @return data-req-mode-flags
     */
    public short getDataReqModeFlags() {
        return dataReqModeFlags;
    }

    /**
     * data-req-init-agent-count 반환
     * 
     * @return data-req-init-agent-count
     */
    public byte getDataReqInitAgentCount() {
        return dataReqInitAgentCount;
    }

    /**
     * data-req-init-manager-count 반환
     * 
     * @return data-req-init-manager-count
     */
    public byte getDataReqInitManagerCount() {
        return dataReqInitManagerCount;
    }

    /**
     * optionList.count 반환
     * 
     * @return optionList.count
     */
    public short getOptionListCount() {
        return optionListCount;
    }

    /**
     * optionList.length 반환
     * 
     * @return optionList.length
     */
    public short getOptionListLength() {
        return optionListLength;
    }

}
