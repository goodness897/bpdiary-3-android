package kr.co.openit.bpdiary.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.common.vo.ResultData;

/**
 * bundle 을 통해 데이터 전달을 위한 ParcelableMap 정의
 */
public class ParcelableMap implements Parcelable {

    /**
     * 속성 데이터 적재
     */
    private Map<String, String> sMap = new HashMap<String, String>();

    /**
     * 측정 결과 데이터 적재
     */
    private Map<String, ResultData> rMap = new HashMap<String, ResultData>();

    /**
     * 생성자
     */
    public ParcelableMap() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param parcel
     */
    public ParcelableMap(Parcel parcel) {
        parcel.readMap(sMap, ParcelableMap.class.getClassLoader());
    }

    /**
     * 속성과 속성값을 저장
     * 
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        sMap.put(key, value);
    }

    /**
     * 측정 데이터 속성과 측정 데이터 저장
     * 
     * @param key
     * @param resultData
     */
    public void put(String key, ResultData resultData) {
        rMap.put(key, resultData);
    }

    /**
     * key 의 속성값 반환
     * 
     * @param key
     * @return
     */
    public String getString(String key) {
        return sMap.get(key);
    }

    /**
     * key 의 측정 데이터 반환
     * 
     * @param key
     * @return
     */
    public ResultData getResultData(String key) {
        return rMap.get(key);
    }

    /**
     * 속성 데이터를 적재한 map 설정
     * 
     * @param map
     */
    public void setStringMap(Map<String, String> map) {
        this.sMap = map;
    }

    /**
     * 측정 데이터를 적재한 map 설정
     *
     * @param rMap
     */
    public void setResultDataMap(Map<String, ResultData> rMap) {
        this.rMap = rMap;
    }

    /**
     * 속성 데이터를 적재한 map 반환
     * 
     * @return
     */
    public Map<String, String> getStringMap() {
        return sMap;
    }

    /**
     * 측정 데이터를 적재한 map 반환
     * 
     * @return
     */
    public Map<String, ResultData> getResultDataMap() {
        return rMap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(sMap);
    }

    public static final Parcelable.Creator<ParcelableMap> CREATOR = new Parcelable.Creator<ParcelableMap>() {

        @Override
        public ParcelableMap createFromParcel(Parcel source) {
            return new ParcelableMap(source);
        }

        @Override
        public ParcelableMap[] newArray(int size) {
            return new ParcelableMap[size];
        }
    };

    @Override
    public String toString() {
        if (sMap != null && !sMap.isEmpty()) {
            return sMap.toString();
        } else if (rMap != null && !rMap.isEmpty()) {
            return rMap.toString();
        }

        return "";
    }
}
