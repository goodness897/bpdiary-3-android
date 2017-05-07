package kr.co.openit.bpdiary.common.vo.apdu;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ByteUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.common.vo.attr.AbsoluteTime;
import kr.co.openit.bpdiary.common.vo.attr.AbsoluteTimeAdjust;
import kr.co.openit.bpdiary.common.vo.attr.Attribute;
import kr.co.openit.bpdiary.common.vo.attr.ConfigId;
import kr.co.openit.bpdiary.common.vo.attr.MdsTimeInfo;
import kr.co.openit.bpdiary.common.vo.attr.OCTECSTRING;
import kr.co.openit.bpdiary.common.vo.attr.ProductionSpec;
import kr.co.openit.bpdiary.common.vo.attr.RegCertDataList;
import kr.co.openit.bpdiary.common.vo.attr.SystemModel;
import kr.co.openit.bpdiary.common.vo.attr.TypeVerList;

/**
 * GET MDS attribute service VO
 */
public class GetMDSPrstApdu extends PrstApdu implements IApduBody {

    /**
     * 생성자
     */
    public GetMDSPrstApdu() {
        // default constructor

        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_GET);

        setChoiceTypeLength(0x000e);
        setOctecStringLength(0x000c);

        setChoiceLength(0x0006);
    }

    /**
     * 생성자
     * 
     * @param buffer
     */
    public GetMDSPrstApdu(ByteBuffer buffer) {

        buffer.position(0);

        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_RESPONSE_GET);

        mdsObject = ByteUtil.short2ushort(buffer.getShort());
        attrListCount = ByteUtil.short2ushort(buffer.getShort());
        attrListLength = ByteUtil.short2ushort(buffer.getShort());

        byte[] attrListArr = new byte[attrListLength];
        buffer = buffer.get(attrListArr, 0, attrListLength);

        ByteBuffer attrListBuf = ByteBuffer.allocate(attrListArr.length).put(attrListArr);
        attrListBuf.position(0);

        for (int a = 0; a < attrListCount; a++) {

            int attrId = ByteUtil.short2ushort(attrListBuf.getShort()); // attribute id
            int attrValueLength = ByteUtil.short2ushort(attrListBuf.getShort()); // attribute-value.length

            byte[] attrArr = new byte[attrValueLength];
            attrListBuf = attrListBuf.get(attrArr, 0, attrValueLength);

            ByteBuffer attrBuf = ByteBuffer.allocate(attrArr.length).put(attrArr);
            attrBuf.position(0);

            switch (attrId) {
                case Nomenclature.Attribute.MDC_ATTR_SYS_TYPE_SPEC_LIST:

                    TypeVerList typeVerList = new TypeVerList(attrId);
                    typeVerList.setCount(ByteUtil.short2ushort(attrBuf.getShort()));
                    typeVerList.setLength(ByteUtil.short2ushort(attrBuf.getShort()));

                    for (int b = 0; b < typeVerList.getCount(); b++) {
                        typeVerList.addTypeVer(ByteUtil.short2ushort(attrBuf.getShort()),
                                               ByteUtil.short2ushort(attrBuf.getShort()));
                    }

                    attrMap.put(attrId, typeVerList);

                    break;

                case Nomenclature.Attribute.MDC_ATTR_ID_MODEL:

                    SystemModel systemModel = new SystemModel(attrId);
                    systemModel.setManufacturer(ByteUtil.getString(attrBuf, ByteUtil.short2ushort(attrBuf.getShort())));
                    systemModel.setModelNumber(ByteUtil.getString(attrBuf, ByteUtil.short2ushort(attrBuf.getShort())));

                    attrMap.put(attrId, systemModel);

                    break;

                case Nomenclature.Attribute.MDC_ATTR_SYS_ID:

                    OCTECSTRING str = new OCTECSTRING(attrId);
                    str.setLength(ByteUtil.short2ushort(attrBuf.getShort()));
                    str.setValue(ByteUtil.getString(attrBuf, str.getLength()));

                    attrMap.put(attrId, str);

                    break;

                case Nomenclature.Attribute.MDC_ATTR_DEV_CONFIG_ID:

                    ConfigId configId = new ConfigId(attrId);
                    configId.setValue(ByteUtil.short2ushort(attrBuf.getShort()));

                    attrMap.put(attrId, configId);

                    break;

                case Nomenclature.Attribute.MDC_ATTR_ID_PROD_SPECN:

                    ProductionSpec productionSpec = new ProductionSpec(attrId);
                    productionSpec.setCount(ByteUtil.short2ushort(attrBuf.getShort()));
                    productionSpec.setLength(ByteUtil.short2ushort(attrBuf.getShort()));

                    for (int b = 0; b < productionSpec.getCount(); b++) {
                        productionSpec.addProdSpecEntry(ByteUtil.short2ushort(attrBuf.getShort()),
                                                        ByteUtil.short2ushort(attrBuf.getShort()),
                                                        ByteUtil.getString(attrBuf,
                                                                           ByteUtil.short2ushort(attrBuf.getShort())));
                    }

                    attrMap.put(attrId, productionSpec);

                    break;

                case Nomenclature.Attribute.MDC_ATTR_TIME_ABS:

                    AbsoluteTime absoluteTime = new AbsoluteTime(attrId);
                    absoluteTime.setCentury(ByteUtil.byte2uchar(attrBuf.get()));
                    absoluteTime.setYear(ByteUtil.byte2uchar(attrBuf.get()));
                    absoluteTime.setMonth(ByteUtil.byte2uchar(attrBuf.get()));
                    absoluteTime.setDay(ByteUtil.byte2uchar(attrBuf.get()));
                    absoluteTime.setHour(ByteUtil.byte2uchar(attrBuf.get()));
                    absoluteTime.setMinute(ByteUtil.byte2uchar(attrBuf.get()));
                    absoluteTime.setSecond(ByteUtil.byte2uchar(attrBuf.get()));
                    absoluteTime.setSecFractions(ByteUtil.byte2uchar(attrBuf.get()));

                    attrMap.put(attrId, absoluteTime);

                    break;

                case Nomenclature.Attribute.MDC_ATTR_REG_CERT_DATA_LIST:

                    RegCertDataList regCertDataList = new RegCertDataList(attrId);
                    regCertDataList.setCount(ByteUtil.short2ushort(attrBuf.getShort()));
                    regCertDataList.setLength(ByteUtil.short2ushort(attrBuf.getShort()));

                    for (int b = 0; b < regCertDataList.getLength(); b++) {

                    }

                    attrMap.put(attrId, regCertDataList);

                    break;

                case Nomenclature.Attribute.MDC_ATTR_MDS_TIME_INFO:

                    MdsTimeInfo mdsTimeInfo = new MdsTimeInfo(attrId);
                    mdsTimeInfo.setMdsTimeCapState(ByteUtil.short2ushort(attrBuf.getShort()));
                    mdsTimeInfo.setTimeSyncProtocol(ByteUtil.short2ushort(attrBuf.getShort()));
                    mdsTimeInfo.setTimeSyncAccuracy(ByteUtil.int2uint(attrBuf.getInt()));
                    mdsTimeInfo.setTimeResolutionAbsTime(ByteUtil.short2ushort(attrBuf.getShort()));
                    mdsTimeInfo.setTimeResolutionRelTime(ByteUtil.short2ushort(attrBuf.getShort()));
                    mdsTimeInfo.setTimeResolutionHighResTime(ByteUtil.int2uint(attrBuf.getInt()));

                    attrMap.put(attrId, mdsTimeInfo);

                    break;

                default:
                    Log.d(TAG, ManagerUtil.getAttributeName(attrId) + " : " + ByteUtil.byte2hexa(attrBuf.array()));

                    break;
            }
        }
    }

    /**
     * obj-handle (MDS object)
     */
    private int mdsObject;

    /**
     * attribute-list.count
     */
    private int attrListCount;

    /**
     * attribute-list.length
     */
    private int attrListLength;

    /**
     * attribute
     */
    private final Map<Integer, Attribute> attrMap = new HashMap<Integer, Attribute>();

    /**
     * attrMap getter
     * 
     * @return attrMap
     */
    public Map<Integer, Attribute> getAttrMap() {
        return attrMap;
    }

    /**
     * attribute setter
     * 
     * @param attrId attribute id
     * @param attr attribute
     */
    public void setAttribute(int attrId, Attribute attr) {
        attrMap.put(attrId, attr);
    }

    /**
     * 해당 attrId 의 Attribute 반환
     * 
     * @param attrId 속성ID
     * @return MDSAttribute
     */
    public Attribute getAttribute(int attrId) {
        return attrMap.get(attrId);
    }

    /**
     * getBytes override
     */
    @Override
    public byte[] getBytes() {
        byte[] arr = super.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(arr.length + getChoiceLength());
        buffer.put(arr);
        buffer.putShort(ByteUtil.ushort2short(mdsObject));
        buffer.putShort(ByteUtil.ushort2short(attrListCount));
        buffer.putShort(ByteUtil.ushort2short(attrListLength));

        return buffer.array();
    }

    /**
     * toString override
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());

        sb.append("obj-handle = ").append(mdsObject).append("(MDS object)\n");
        sb.append("attribute-list.count = ").append(attrListCount).append("\n");
        sb.append("attribute-list.length = ").append(attrListLength).append("\n");

        Iterator<Integer> itr = attrMap.keySet().iterator();
        while (itr.hasNext()) {
            int attrId = itr.next();

            switch (attrId) {
                case Nomenclature.Attribute.MDC_ATTR_SYS_TYPE_SPEC_LIST:
                    TypeVerList typeVerList = (TypeVerList)attrMap.get(attrId);
                    sb.append(typeVerList.toString());
                    break;

                case Nomenclature.Attribute.MDC_ATTR_ID_MODEL:
                    SystemModel systemModel = (SystemModel)attrMap.get(attrId);
                    sb.append(systemModel.toString());
                    break;

                case Nomenclature.Attribute.MDC_ATTR_SYS_ID:
                    OCTECSTRING str = (OCTECSTRING)attrMap.get(attrId);
                    sb.append(str.toString());
                    break;

                case Nomenclature.Attribute.MDC_ATTR_DEV_CONFIG_ID:
                    ConfigId configId = (ConfigId)attrMap.get(attrId);
                    sb.append(configId.toString());
                    break;

                case Nomenclature.Attribute.MDC_ATTR_ID_PROD_SPECN:
                    ProductionSpec productionSpec = (ProductionSpec)attrMap.get(attrId);
                    sb.append(productionSpec.toString());
                    break;

                case Nomenclature.Attribute.MDC_ATTR_TIME_ABS:
                    AbsoluteTime absoluteTime = (AbsoluteTime)attrMap.get(attrId);
                    sb.append(absoluteTime.getAbsoluteTime());
                    break;

                case Nomenclature.Attribute.MDC_ATTR_TIME_ABS_ADJUST:
                    AbsoluteTimeAdjust adjust = (AbsoluteTimeAdjust)attrMap.get(attrId);
                    sb.append(adjust.toString());
                    break;

                default:
                    Attribute attr = attrMap.get(attrId);
                    sb.append(attr.toString());
                    break;
            }
        }

        return sb.toString();
    }
}
