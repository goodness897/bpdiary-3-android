package kr.co.openit.bpdiary.common.vo.apdu;

import android.util.Log;

import java.nio.ByteBuffer;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * SetTimePrstApdu VO 정의
 */
public class SetTimePrstApdu extends PrstApdu implements IApduBody {

    /**
     * debugging
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * 생성자
     */
    public SetTimePrstApdu() {
        // default constructor

        setChoice(HealthcareConstants.Choice.REMOTE_OPERATION_INVOKE_CONFIRMED_ACTION);

        setChoiceTypeLength((short)0x001a);
        setOctecStringLength((short)0x0018);

        setChoiceLength((short)0x0012);
    }

    /**
     * MDS Object
     */
    private short mdsObject = (short)0x0000;

    /**
     * action type
     */
    private short actionType = Nomenclature.Action.MDC_ACT_SET_TIME;

    /**
     * action-info-args.length
     */
    private short actionInfoArgsLength = (short)0x000c;

    /**
     * dateTime
     */
    private final String dateTime = ManagerUtil.getCurrentDateTime();

    /**
     * accuracy
     */
    private final int accuracy = 0x00000000;

    /**
     * mdsObject getter
     * 
     * @return mdsObject
     */
    public short getMdsObject() {
        return mdsObject;
    }

    /**
     * mdsObject setter
     * 
     * @param mdsObject - the mdsObject to set
     */
    public void setMdsObject(short mdsObject) {
        this.mdsObject = mdsObject;
    }

    /**
     * actionType getter
     * 
     * @return actionType
     */
    public short getActionType() {
        return actionType;
    }

    /**
     * actionType setter
     * 
     * @param actionType - the actionType to set
     */
    public void setActionType(short actionType) {
        this.actionType = actionType;
    }

    /**
     * actionInfoArgsLength getter
     * 
     * @return actionInfoArgsLength
     */
    public short getActionInfoArgslength() {
        return actionInfoArgsLength;
    }

    /**
     * actionInfoArgsLength setter
     * 
     * @param actionInfoArgslength - the actionInfoArgslength to set
     */
    public void setActionInfoArgslength(short actionInfoArgslength) {
        this.actionInfoArgsLength = actionInfoArgslength;
    }

    /**
     * dateTime getter
     * 
     * @return
     */
    public String getDateTime() {
        return this.dateTime;
    }

    /**
     * getBytes override
     */
    @Override
    public byte[] getBytes() {
        byte[] arr = super.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(arr.length + getChoiceLength());

        buffer.put(arr);
        buffer.putShort(mdsObject);
        buffer.putShort(actionType);
        buffer.putShort(actionInfoArgsLength);

        Log.d(TAG, "datetime : " + dateTime);

        buffer.put((byte)Integer.parseInt(dateTime.substring(0, 2), 16)).array();
        buffer.put((byte)Integer.parseInt(dateTime.substring(2, 4), 16)).array();
        buffer.put((byte)Integer.parseInt(dateTime.substring(4, 6), 16)).array();
        buffer.put((byte)Integer.parseInt(dateTime.substring(6, 8), 16)).array();
        buffer.put((byte)Integer.parseInt(dateTime.substring(8, 10), 16)).array();
        buffer.put((byte)Integer.parseInt(dateTime.substring(10, 12), 16)).array();
        buffer.put((byte)Integer.parseInt(dateTime.substring(12), 16)).array();
        buffer.put((byte)0x00);

        buffer.putInt(accuracy);

        return buffer.array();
    }

    /**
     * toString override
     */
    @Override
    public String toString() {
        return null;
    }
}
