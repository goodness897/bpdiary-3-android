package kr.co.openit.bpdiary.common.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kr.co.openit.bpdiary.utils.ManagerUtil;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * UI 로 전달해 줄 측정 데이터 VO
 */
public class ResultData implements Parcelable {

    /**
     * objHandle
     */
    private int objHandle;

    /**
     * 속성 ID
     */
    private int attrId;

    /**
     * 측정 데이터 값
     */
    private String value;

    /**
     * 단위 ID
     */
    private int unitCd;

    /**
     * 측정일
     */
    private String measureDt;

    /**
     * 하위 속성 적재 리스트
     */
    private ArrayList<HashMap<Integer, String>> subClassList = new ArrayList<HashMap<Integer, String>>();

    /**
     * 생성자
     */
    public ResultData() {
        // default constructor
    }

    /**
     * 생성자
     *
     * @param attrId
     * @param value
     */
    public ResultData(int objHandle, int attrId, String value) {
        this.objHandle = objHandle;
        this.attrId = attrId;
        this.value = value;
    }

    /**
     * 생성자
     *
     * @param attrId
     * @param value
     * @param measureDt
     */
    public ResultData(int objHandle, int attrId, String value, String measureDt) {
        this(objHandle, attrId, value);

        this.measureDt = measureDt;
    }

    /**
     * 생성자
     *
     * @param attrId
     * @param value
     * @param unitCd
     */
    public ResultData(int objHandle, int attrId, String value, int unitCd) {
        this(objHandle, attrId, value);

        this.unitCd = unitCd;
    }

    /**
     * 생성자
     *
     * @param attrId
     * @param value
     * @param unitCd
     * @param measureDt
     */
    public ResultData(int objHandle, int attrId, String value, int unitCd, String measureDt) {
        this(objHandle, attrId, value, unitCd);

        this.measureDt = measureDt;
    }

    /**
     * 생성자
     *
     * @param attrId
     * @param value
     * @param unitCd
     * @param measureDt
     */
    public ResultData(int attrId, String value, int unitCd, String measureDt) {
        this(-1, attrId, value, unitCd, measureDt);
    }

    protected ResultData(Parcel in) {
        objHandle = in.readInt();
        attrId = in.readInt();
        value = in.readString();
        unitCd = in.readInt();
        measureDt = in.readString();
    }

    public static final Creator<ResultData> CREATOR = new Creator<ResultData>() {
        @Override
        public ResultData createFromParcel(Parcel in) {
            return new ResultData(in);
        }

        @Override
        public ResultData[] newArray(int size) {
            return new ResultData[size];
        }
    };

    /**
     * objHandle getter
     *
     * @return
     */
    public int getObjHandle() {
        return objHandle;
    }

    /**
     * attrId getter
     *
     * @return attrId
     */
    public int getAttrId() {
        return attrId;
    }

    /**
     * value getter
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * unitCd getter
     *
     * @return unitCd
     */
    public int getUnitCd() {
        return unitCd;
    }

    /**
     * measureDt getter
     *
     * @return measureDt
     */
    public String getMeasureDt() {
        return measureDt;
    }

    /**
     * 해당 attrId의 subClass 설정
     *
     * @param attrId
     * @param subClassAttrId
     * @param value
     */
    public void addSubClass(int subClassAttrId, String value) {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(subClassAttrId, value);

        subClassList.add(map);
    }

    /**
     * subClassList 설정
     *
     * @param subClassList
     */
    public void setSubClassList(ArrayList<HashMap<Integer, String>> subClassList) {
        this.subClassList = subClassList;
    }

    /**
     * 해당 attrId의 subClass 리스트 반환
     *
     * @param attrId
     * @return
     */
    public ArrayList<HashMap<Integer, String>> getSubClassList() {
        return subClassList;
    }

    /**
     * 해당 attrId의 subClass 값 반환
     *
     * @param attrId
     * @param subClassAttrId
     * @return
     */
    public String getSubClassValue(int subClassAttrId) {
        for (int i = 0, len = subClassList.size(); i < len; i++) {
            HashMap<Integer, String> map = subClassList.get(i);

            Iterator<Integer> itr = map.keySet().iterator();
            while (itr.hasNext()) {
                int key = itr.next();

                if (key == subClassAttrId) {
                    return map.get(key);
                }
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        // nothing
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // nothing
        dest.writeInt(objHandle);
        dest.writeInt(attrId);
        dest.writeString(value);
        dest.writeInt(unitCd);
        dest.writeString(measureDt);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (objHandle > 0) {
            sb.append("[").append(objHandle).append("] ");
        }

        sb.append(ManagerUtil.getDataAcquName(attrId)).append(" : ").append(value);

        if (unitCd > 0) {
            sb.append(" ").append(ManagerUtil.getUnitCodeName(unitCd));
        }

        if (measureDt != null && !measureDt.isEmpty()) {
            sb.append(" [").append(measureDt).append("]");
        }

        for (int i = 0, len = subClassList.size(); i < len; i++) {
            HashMap<Integer, String> map = subClassList.get(i);

            Iterator<Integer> itr = map.keySet().iterator();
            while (itr.hasNext()) {
                int key = itr.next();

                sb.append(",   ").append(ManagerUtil.getAttributeName(key)).append(" = ").append(map.get(key));
            }
        }

        sb.append("\n");

        return sb.toString();
    }
}
