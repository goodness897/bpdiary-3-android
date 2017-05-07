/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2013, openit Inc.
 * All rights reserved.
 */
package kr.co.openit.bpdiary.common.vo.attr;

/**
 * MdsTimeInfo 정의
 */
public class MdsTimeInfo extends Attribute {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -9068806405043526739L;

    /**
     * 생성자
     */
    public MdsTimeInfo() {
        // default constructor
    }

    /**
     * 생성자
     * 
     * @param attrId
     */
    public MdsTimeInfo(int attrId) {
        super(attrId);
    }

    /**
     * mdsTimeCapState
     */
    private int mdsTimeCapState;

    /**
     * timeSyncProtocol
     */
    private int timeSyncProtocol;

    /**
     * timSyncAccuracy
     */
    private long timeSyncAccuracy;

    /**
     * timeResolutionAbsTime
     */
    private int timeResolutionAbsTime;

    /**
     * timeResolutionRelTime
     */
    private int timeResolutionRelTime;

    /**
     * timeResolutionHighResTime
     */
    private long timeResolutionHighResTime;

    /**
     * mdsTimeCapState getter
     * 
     * @return mdsTimeCapState
     */
    public int getMdsTimeCapState() {
        return mdsTimeCapState;
    }

    /**
     * mdsTimeCapState setter
     * 
     * @param mdsTimeCapState - the mdsTimeCapState to set
     */
    public void setMdsTimeCapState(int mdsTimeCapState) {
        this.mdsTimeCapState = mdsTimeCapState;
    }

    /**
     * timeSyncProtocol getter
     * 
     * @return timeSyncProtocol
     */
    public int getTimeSyncProtocol() {
        return timeSyncProtocol;
    }

    /**
     * timeSyncProtocol setter
     * 
     * @param timeSyncProtocol - the timeSyncProtocol to set
     */
    public void setTimeSyncProtocol(int timeSyncProtocol) {
        this.timeSyncProtocol = timeSyncProtocol;
    }

    /**
     * timeSyncAccuracy getter
     * 
     * @return timeSyncAccuracy
     */
    public long getTimeSyncAccuracy() {
        return timeSyncAccuracy;
    }

    /**
     * timeSyncAccuracy setter
     * 
     * @param timeSyncAccuracy - the timeSyncAccuracy to set
     */
    public void setTimeSyncAccuracy(long timeSyncAccuracy) {
        this.timeSyncAccuracy = timeSyncAccuracy;
    }

    /**
     * timeResolutionAbsTime getter
     * 
     * @return timeResolutionAbsTime
     */
    public int getTimeResolutionAbsTime() {
        return timeResolutionAbsTime;
    }

    /**
     * timeResolutionAbsTime setter
     * 
     * @param timeResolutionAbsTime - the timeResolutionAbsTime to set
     */
    public void setTimeResolutionAbsTime(int timeResolutionAbsTime) {
        this.timeResolutionAbsTime = timeResolutionAbsTime;
    }

    /**
     * timeResolutionRelTime getter
     * 
     * @return timeResolutionRelTime
     */
    public int getTimeResolutionRelTime() {
        return timeResolutionRelTime;
    }

    /**
     * timeResolutionRelTime setter
     * 
     * @param timeResolutionRelTime - the timeResolutionRelTime to set
     */
    public void setTimeResolutionRelTime(int timeResolutionRelTime) {
        this.timeResolutionRelTime = timeResolutionRelTime;
    }

    /**
     * timeResolutionHighResTime getter
     * 
     * @return timeResolutionHighResTime
     */
    public long getTimeResolutionHighResTime() {
        return timeResolutionHighResTime;
    }

    /**
     * timeResolutionHighResTime setter
     * 
     * @param timeResolutionHighResTime - the timeResolutionHighResTime to set
     */
    public void setTimeResolutionHighResTime(long timeResolutionHighResTime) {
        this.timeResolutionHighResTime = timeResolutionHighResTime;
    }

    /**
     * mdsTimeCapState 가 MDS_TIME_MGR_SET_TIME 인지 여부 반환
     * 
     * @return
     */
    public boolean isMdsTimeMgrSetTime() {
        byte b = (byte)((mdsTimeCapState >> 4) & 1);

        if (b == (byte)0x01) {
            return true;
        }

        return false;
    }

    /**
     * MdsTimeCapState
     */
    public static class MdsTimeCapState {

        /**
         * device supports an internal RTC
         */
        public static final int MDS_TIME_CAPAB_REAL_TIME_CLOCK = 0x8000;

        /**
         * device supports Set Time Action
         */
        public static final int MDS_TIME_CAPAB_SET_CLOCK = 0x4000;

        /**
         * device supports RelativeTime
         */
        public static final int MDS_TIME_CAPAB_RELATIVE_TIME = 0x2000;

        /**
         * device supports HighResRelativeTime
         */
        public static final int MDS_TIME_CAPAB_HIGH_RES_RELATIVE_TIME = 0x1000;

        /**
         * device syncs AbsoluteTime
         */
        public static final int MDS_TIME_CAPAB_SYNC_ABS_TIME = 0x0800;

        /**
         * device syncs RelativeTime
         */
        public static final int MDS_TIME_CAPAB_SYNC_REL_TIME = 0x0400;

        /**
         * device syncs HiResRelativeTime
         */
        public static final int MDS_TIME_CAPAB_SYNC_HI_RES_RELATIVE_TIME = 0x0200;

        /**
         * AbsoluteTime is synced
         */
        public static final int MDS_TIME_STATE_ABS_TIME_SYNCED = 0x0080;

        /**
         * RelativeTime is synced
         */
        public static final int MDS_TIME_STATE_REL_TIME_SYNCED = 0x0040;

        /**
         * HiResRelativeTime is synced
         */
        public static final int MDS_TIME_STATE_HI_RES_RELATIVE_TIME_SYNCED = 0x0020;

        /**
         * manager is encouraged to set the time
         */
        public static final int MDS_TIME_MGR_SET_TIME = 0x0010;

        /**
         * 생성자
         */
        private MdsTimeCapState() {
            // default constructor
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(super.toString());
        sb.append("    mds-time-cap-state = ").append(mdsTimeCapState).append("\n");
        sb.append("    time-sync-protocol = ").append(timeSyncProtocol).append("\n");
        sb.append("    time-sync-accuracy = ").append(Long.toHexString(timeSyncAccuracy)).append("\n");
        sb.append("    time-resolution-abs-time = ").append(timeResolutionAbsTime).append("\n");
        sb.append("    time-resolution-rel-time = ").append(timeResolutionRelTime).append("\n");
        sb.append("    time-resolution-high-res-time = ").append(timeResolutionHighResTime).append("\n");

        return sb.toString();
    }
}
