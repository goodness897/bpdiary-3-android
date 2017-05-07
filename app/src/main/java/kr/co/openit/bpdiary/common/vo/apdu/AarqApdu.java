package kr.co.openit.bpdiary.common.vo.apdu;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;

/**
 * association request VO
 */
public class AarqApdu extends Apdu implements IApduBody {

    /**
     * 생성자
     *
     * @param body
     */
    public AarqApdu(byte[] body) {

        setChoiceType(HealthcareConstants.ApduChoiceType.ASSOCIATION_REQUEST);
        setChoiceTypeLength((short)body.length);

        ByteBuffer buffer = ByteBuffer.allocate(body.length).put(body);
        buffer.position(0);

        associationVersion = buffer.getInt(); // assoc-version

        dataProtoListCount = buffer.getShort();
        dataProtoListLength = buffer.getShort();

        for (int a = 0; a < dataProtoListCount; a++) {

            short dataProtoId = buffer.getShort();
            short dataProtoInfoLength = buffer.getShort();

            byte[] arr = new byte[dataProtoInfoLength];
            buffer.get(arr, 0, dataProtoInfoLength);

            PhdAssociationInformation dataProtoInfo = new PhdAssociationInformation(dataProtoId,
                                                                                    dataProtoInfoLength,
                                                                                    arr);

            dataProtoInfoMap.put(Short.valueOf(dataProtoId), dataProtoInfo);
        }
    }

    /**
     * assoc-version
     */
    private int associationVersion;

    /**
     * data-proto-list.count = 1
     */
    private short dataProtoListCount;

    /**
     * data-proto-list.length = 42
     */
    private short dataProtoListLength;

    /**
     * dataProtoInfoMap
     */
    private final Map<Short, PhdAssociationInformation> dataProtoInfoMap = new HashMap<Short, PhdAssociationInformation>();

    /**
     * data-proto-list.count 반환
     * 
     * @return data-proto-list.count
     */
    public short getDataProtoListCount() {
        return dataProtoListCount;
    }

    /**
     * data-proto-list.count 설정
     * 
     * @param dataProtoListCount - the dataProtoListCount to set
     */
    public void setDataProtoListCount(short dataProtoListCount) {
        this.dataProtoListCount = dataProtoListCount;
    }

    /**
     * data-proto-list.length 반환
     * 
     * @return data-proto-list.length
     */
    public short getDataProtoListLength() {
        return dataProtoListLength;
    }

    /**
     * data-proto-list.length 설정
     * 
     * @param dataProtoListLength - the dataProtoListLength to set
     */
    public void setDataProtoListLength(short dataProtoListLength) {
        this.dataProtoListLength = dataProtoListLength;
    }

    /**
     * assoc-version 반환
     * 
     * @return assoc-version
     */
    public int getAssociationVersion() {
        return associationVersion;
    }

    /**
     * dataProtoId 유효성 여부 반환
     * 
     * @return dataProtoId 유효성 여부
     */
    public boolean isValidDataProtoId() {
        if (dataProtoInfoMap.containsKey(HealthcareConstants.Association.DATA_PROTO_ID) || dataProtoInfoMap.containsKey(HealthcareConstants.Association.DATA_PROTO_ID_20601)) {
            return true;
        }

        return false;
    }

    /**
     * dataProtoId 반환
     * 
     * @return dataProtoId
     */
    public short getDataProtoId() {

        if (isValidDataProtoId()) {
            if (dataProtoInfoMap.containsKey(HealthcareConstants.Association.DATA_PROTO_ID)) {
                return HealthcareConstants.Association.DATA_PROTO_ID;
            } else if (dataProtoInfoMap.containsKey(HealthcareConstants.Association.DATA_PROTO_ID_20601)) {
                return HealthcareConstants.Association.DATA_PROTO_ID_20601;
            }
        }

        return (short)0x0000;
    }

    /**
     * encoding rule 유효성 여부 반환
     * 
     * @return encoding rule 유효성 여부
     */
    public boolean isValidEncodingRule() {

        Iterator<Short> itr = dataProtoInfoMap.keySet().iterator();

        while (itr.hasNext()) {
            short key = itr.next();

            PhdAssociationInformation assocInfo = dataProtoInfoMap.get(key);

            Log.d(TAG, "encoding rule : " + Integer.toString(assocInfo.getEncodingRule(), 16));

            if ((HealthcareConstants.EncodingRule.MDER == assocInfo.getEncodingRule()) || (HealthcareConstants.EncodingRule.PER == assocInfo.getEncodingRule())
                || (HealthcareConstants.EncodingRule.XER == assocInfo.getEncodingRule())
                || (HealthcareConstants.EncodingRule.MDER_PER == assocInfo.getEncodingRule())
                || (HealthcareConstants.EncodingRule.MDER_XER == assocInfo.getEncodingRule())
                || (HealthcareConstants.EncodingRule.XER_PER == assocInfo.getEncodingRule())
                || (HealthcareConstants.EncodingRule.MDER_XER_PER == assocInfo.getEncodingRule())) {

                Log.d(TAG, "encoding rule true ");

                return true;
            }
        }

        return false;
    }

    /**
     * systemId 반환
     * 
     * @return systemId
     */
    public String getSystemId() {

        // dataProtoInfo 는 여러 개 존재 가능
        Iterator<Short> itr = dataProtoInfoMap.keySet().iterator();

        while (itr.hasNext()) {
            short key = itr.next();

            PhdAssociationInformation assocInfo = dataProtoInfoMap.get(key);

            if (assocInfo.getSystemId() != null && !assocInfo.getSystemId().isEmpty()) {
                return assocInfo.getSystemId();
            }
        }

        return null;
    }

    /**
     * devConfigId 반환
     * 
     * @return devConfigId
     */
    public int getDevConfigId() {

        // dataProtoInfo 는 여러 개 존재 가능
        Iterator<Short> itr = dataProtoInfoMap.keySet().iterator();

        while (itr.hasNext()) {
            short key = itr.next();

            PhdAssociationInformation assocInfo = dataProtoInfoMap.get(key);

            if (assocInfo.getDevConfigId() > 0) {
                return assocInfo.getDevConfigId();
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(super.toString());

        sb.append("assoc-version = ").append(Integer.toHexString(associationVersion)).append("\n");
        sb.append("data-proto-list.count = ").append(dataProtoListCount).append("\n");
        sb.append("data-proto-list.length = ").append(dataProtoListLength).append("\n");

        Iterator<Short> itr = dataProtoInfoMap.keySet().iterator();
        while (itr.hasNext()) {
            sb.append(dataProtoInfoMap.get(itr.next()).toString());
        }

        return sb.toString();
    }

    @Override
    public byte[] getBytes() {
        return null;
    }

}
