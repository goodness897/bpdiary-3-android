/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2014, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * AbsoluteTime 정의
 */
public class AbsoluteTime extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8653592909433255461L;

    /**
     * 생성자
     */
    public AbsoluteTime() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public AbsoluteTime(int attrId) {
        // default constructor
        super(attrId);
    }

    /**
     * 
     */
    private int century;

    /**
     * 
     */
    private int year;

    /**
     * 
     */
    private int month;

    /**
     * 
     */
    private int day;

    /**
     * 
     */
    private int hour;

    /**
     * 
     */
    private int minute;

    /**
     * 
     */
    private int second;

    /**
     * 
     */
    private int secFractions;

    /**
     * century setter
     * 
     * @param century - the century to set
     */
    public void setCentury(int century) {
        this.century = century;
    }

    /**
     * year setter
     * 
     * @param year - the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * month setter
     * 
     * @param month - the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * day setter
     * 
     * @param day - the day to set
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * hour setter
     * 
     * @param hour - the hour to set
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * minute setter
     * 
     * @param minute - the minute to set
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * second setter
     * 
     * @param second - the second to set
     */
    public void setSecond(int second) {
        this.second = second;
    }

    /**
     * secFractions setter
     * 
     * @param secFractions - the secFractions to set
     */
    public void setSecFractions(int secFractions) {
        this.secFractions = secFractions;
    }

    /**
     * absoluteTime 반환
     * 
     * @return absoluteTime
     */
    public String getAbsoluteTime() {

        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append(ManagerUtil.leftPadding(Integer.toHexString(century), 2));
        sb.append(ManagerUtil.leftPadding(Integer.toHexString(year), 2));
        sb.append(ManagerUtil.leftPadding(Integer.toHexString(month), 2));
        sb.append(ManagerUtil.leftPadding(Integer.toHexString(day), 2));
        sb.append(ManagerUtil.leftPadding(Integer.toHexString(hour), 2));
        sb.append(ManagerUtil.leftPadding(Integer.toHexString(minute), 2));
        sb.append(ManagerUtil.leftPadding(Integer.toHexString(second), 2));
        sb.append(ManagerUtil.leftPadding(Integer.toHexString(secFractions), 2)).append("\n");

        return sb.toString();
    }

}
