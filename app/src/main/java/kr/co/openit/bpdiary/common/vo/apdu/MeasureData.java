package kr.co.openit.bpdiary.common.vo.apdu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * 측정 데이터를 가진 VO
 */
public class MeasureData implements Parcelable {

    /**
     * 생성자
     */
    public MeasureData() {
        // default constructor
    }

    /**
     * 생성자
     *
     * @param objHandle
     * @param attrId
     * @param value
     */
    public MeasureData(int objHandle, int attrId, String value) {
        this.handle = objHandle;
        this.attrId = attrId;
        this.value = value;
    }

    /**
     * 생성자
     *
     * @param objHandle
     * @param attrId
     * @param value
     * @param unitCd
     */
    public MeasureData(int objHandle, int attrId, String value, int unitCd) {
        this(objHandle, attrId, value);

        if (unitCd > 0) {
            this.unitCd = unitCd;
        }
    }

    /**
     * 생성자
     *
     * @param objHandle
     * @param attrId
     * @param personId
     * @param value
     * @param unitCd
     */
    public MeasureData(int objHandle, int attrId, int personId, String value, int unitCd) {
        this(objHandle, attrId, value, unitCd);

        this.personId = personId;
    }

    /**
     * obj-handle
     */
    private int handle;

    /**
     * 속성 id
     */
    private int attrId;

    /**
     * multiple person id
     */
    private int personId;

    /**
     * 데이터 값
     */
    private String value;

    /**
     * 단위 속성값
     */
    private int unitCd;

    /**
     * 측정 시간
     */
    private String dateTime;

    /**
     * 하위 속성 적재 리스트
     */
    private final ArrayList<HashMap<Integer, String>> subClassList = new ArrayList<HashMap<Integer, String>>();

    /**
     * handle getter
     * 
     * @return handle
     */
    public int getObjHandle() {
        return handle;
    }

    /**
     * handle setter
     * 
     * @param handle - the handle to set
     */
    public void setObjHandle(int handle) {
        this.handle = handle;
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
     * attrId setter
     * 
     * @param attrId - the attrId to set
     */
    public void setAttrId(int attrId) {
        this.attrId = attrId;
    }

    /**
     * personId getter
     * 
     * @return personId
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * personId setter
     * 
     * @param personId - the personId to set
     */
    public void setPersonId(int personId) {
        this.personId = personId;
    }

    /**
     * attrName setter
     * 
     * @return attrName
     */
    public String getValue() {
        return value;
    }

    /**
     * value setter
     * 
     * @param value - the value to set
     */
    public void setValue(String value) {
        this.value = value;
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
     * unitCd setter
     * 
     * @param unitCd - the unitCd to set
     */
    public void setUnitCd(int unitCd) {
        this.unitCd = unitCd;
    }

    /**
     * 측정일시 setter
     * 
     * @param dateTime 측정일시
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * 측정일시 getter
     * 
     * @return 측정일시
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * subclass List add
     * 
     * @param subClassAttrId
     * @param value
     */
    public void addSubClassList(int subClassAttrId, String value) {
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put(subClassAttrId, value);

        subClassList.add(map);
    }

    /**
     * subClass getter
     * 
     * @param subClassAttrId
     * @return
     */
    public String getSubClass(int subClassAttrId) {
        for (int i = 0, len = subClassList.size(); i < len; i++) {
            if (subClassList.get(i).containsKey(subClassAttrId)) {
                return subClassList.get(i).get(subClassAttrId);
            }
        }

        return null;
    }

    /**
     * subClassList 반환
     * 
     * @return
     */
    public ArrayList<HashMap<Integer, String>> getSubClassList() {
        return subClassList;
    }

    /**
     * subClassList empty 여부 반환
     * 
     * @return
     */
    public boolean isEmptySubClassList() {
        return subClassList.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[")
          .append(handle)
          .append("] ")
          .append(personId > 0 ? "#" : "")
          .append(personId > 0 ? personId : "")
          .append(personId > 0 ? "# " : "")
          .append(ManagerUtil.getAttributeName(attrId))
          .append(" : ")
          .append(value);

        if (unitCd > 0) {
            sb.append(" (").append(ManagerUtil.getUnitCodeName(unitCd)).append(") ");
        }

        if (dateTime != null) {
            sb.append(" (").append(dateTime).append(")");
        }

        for (int i = 0, len = subClassList.size(); i < len; i++) {

            Iterator<Integer> itr = subClassList.get(i).keySet().iterator();
            while (itr.hasNext()) {

                int key = itr.next();
                sb.append(", ")
                  .append(ManagerUtil.getAttributeName(key))
                  .append(" = ")
                  .append(subClassList.get(i).get(key));
            }
        }

        sb.append("\n");

        return sb.toString();
    }

    @Override
    public int describeContents() {
        // nothing
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // nothing
    }
}
