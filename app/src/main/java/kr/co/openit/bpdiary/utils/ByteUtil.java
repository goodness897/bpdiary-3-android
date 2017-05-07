package kr.co.openit.bpdiary.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * <pre>
 * 각 Type 형태의 변수를 Byte로 변환/역변환 해주는 유틸리티 클래스
 * byte[] byteArray = ByteUtil.int2byte(40);
 * </pre>
 */
public final class ByteUtil {

    /**
     * <pre>
     * 2진 바이트에서 저장하는 바이트의 순서를 나타내는 방법으로 
     * Big endian과 Little endian으로 변환하는 방법이 있는데 
     * Big endian은 최상위 비트 MSB(Mmost Significant Bit) 부터 부호화되어 저장되며,
     * Little endian은 최하위 비트 LSB(Least Significant Bit) 부터 부호화되어 저장 됩니다.
     * 
     * 순차 바이트 정렬 방식(Big endian) - Java/Network 표준인 BIG_ENDIAN (true, default)
     * 역순 바이트 정렬 방식(Little endian) - C 언어 호환성을 위한 LITTLE_ENDIAN (false)
     * </pre>
     */
    private static boolean ENDIAN = true;

    /**
     * <pre>
     * 생성자
     * </pre>
     */
    private ByteUtil() {
        // Do nothing.
    }

    //__________________________________________________________________________
    //
    // short type
    //__________________________________________________________________________
    /**
     * <pre>
     * short 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     *   
     * short -&gt; byte[] (len : 2 byte)
     * </pre>
     * 
     * @param s - short 형 데이터
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] short2byte(short s) {
        byte[] dest = new byte[2];
        return setshort(dest, 0, s);
    }

    /**
     * <pre>
     * short 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * </pre>
     * 
     * @param dest - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @param s - 변환 대상 데이타.
     * @return dest 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] setshort(byte[] dest, int offset, short s) {
        if (ENDIAN) { // BIG_ENDIAN
            dest[offset + 1] = (byte)(s & 0xff);
            dest[offset] = (byte)(s >>> 8 & 0xff);
        } else { // LITTLE_ENDIAN
            dest[offset] = (byte)(s & 0xff);
            dest[offset + 1] = (byte)(s >>> 8 & 0xff);
        }
        return dest;
    }

    /**
     * <pre>
     * short 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * byte[] -&gt; short (len : 2byte)
     * </pre>
     * 
     * @param src - 변환 대상 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static short getshort(byte[] src) {
        return getshort(src, 0);
    }

    /**
     * <pre>
     * short 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * </pre>
     * 
     * @param src - 변환 대상 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static short getshort(byte[] src, int offset) {
        if (ENDIAN) { // BIG_ENDIAN
            return (short)((src[offset] & 0xff) << 8 | src[offset + 1] & 0xff);
        } else { // LITTLE_ENDIAN
            return (short)((src[offset + 1] & 0xff) << 8 | src[offset] & 0xff);
        }
    }

    //__________________________________________________________________________
    //
    // int type
    //__________________________________________________________________________
    /**
     * <pre>
     * int 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * int -&gt; byte[] (len : 4 byte)
     * </pre>
     * 
     * @param i - 변환 대상 데이타.
     * @return 멤버 변수 Endian의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] int2byte(int i) {
        byte[] dest = new byte[4];
        return setint(dest, 0, i);
    }

    /**
     * <pre>
     * int 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * </pre>
     * 
     * @param dest - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @param i - 변환 대상 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] setint(byte[] dest, int offset, int i) {
        if (ENDIAN) { // BIG_ENDIAN
            dest[offset] = (byte)(i >>> 24 & 0xff);
            dest[offset + 1] = (byte)(i >>> 16 & 0xff);
            dest[offset + 2] = (byte)(i >>> 8 & 0xff);
            dest[offset + 3] = (byte)(i & 0xff);
        } else { // LITTLE_ENDIAN
            dest[offset + 3] = (byte)(i >>> 24 & 0xff);
            dest[offset + 2] = (byte)(i >>> 16 & 0xff);
            dest[offset + 1] = (byte)(i >>> 8 & 0xff);
            dest[offset] = (byte)(i & 0xff);
        }
        return dest;
    }

    /**
     * <pre>
     * int 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * int -&gt; byte[] (len : 4 byte)
     * </pre>
     * 
     * @param src - 변환 대상 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static int getint(byte[] src) {
        return getint(src, 0);
    }

    /**
     * <pre>
     * int 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * </pre>
     * 
     * @param src - 변환 대상 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static int getint(byte[] src, int offset) {
        if (ENDIAN) { // BIG_ENDIAN
            return (src[offset] & 0xff) << 24 | (src[offset + 1] & 0xff) << 16
                   | (src[offset + 2] & 0xff) << 8
                   | src[offset + 3]
                   & 0xff;
        } else { // LITTLE_ENDIAN
            return (src[offset + 3] & 0xff) << 24 | (src[offset + 2] & 0xff) << 16
                   | (src[offset + 1] & 0xff) << 8
                   | src[offset]
                   & 0xff;
        }
    }

    //__________________________________________________________________________
    //
    // long type
    //__________________________________________________________________________
    /**
     * <pre>
     * long 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * long -&gt; byte[] (len : 8 byte)
     * </pre>
     * 
     * @param l - 변환 대상 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] long2byte(long l) {
        byte[] dest = new byte[8];
        return setlong(dest, 0, l);
    }

    /**
     * <pre>
     * long 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * byte[] -&gt; long (len : 8 byte)
     * </pre>
     * 
     * @param dest - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @param l - 변환 대상 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] setlong(byte[] dest, int offset, long l) {
        if (ENDIAN) {
            setint(dest, offset, (int)(l >>> 32));
            setint(dest, offset + 4, (int)(l & 0xffffffffL));
        } else {
            setint(dest, offset + 4, (int)(l >>> 32));
            setint(dest, offset, (int)(l & 0xffffffffL));
        }
        return dest;
    }

    /**
     * <pre>
     * long 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * byte[] -&gt; long (len : 8 byte)
     * </pre>
     * 
     * @param src - byte 배열로 변환된 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static long getlong(byte[] src) {
        return getlong(src, 0);
    }

    /**
     * <pre>
     * long 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * byte[] -&gt; long (len : 8 byte)
     * </pre>
     * 
     * @param src - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static long getlong(byte[] src, int offset) {
        if (ENDIAN) {
            return (long)getint(src, offset) << 32 | getint(src, offset + 4) & 0xffffffffL;
        } else {
            return (long)getint(src, offset + 4) << 32 | getint(src, offset) & 0xffffffffL;
        }
    }

    //__________________________________________________________________________
    //
    // float type
    //__________________________________________________________________________
    /**
     * <pre>
     * float 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * float -&gt; byte[] (len : 4byte)
     * </pre>
     * 
     * @param f - 변환 대상 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] float2byte(float f) {
        byte[] dest = new byte[4];
        return setfloat(dest, 0, f);
    }

    /**
     * <pre>
     * float 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * float -&gt; byte[] (len : 4byte)
     * </pre>
     * 
     * @param dest - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @param f - 변환 대상 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] setfloat(byte[] dest, int offset, float f) {
        return setint(dest, offset, Float.floatToIntBits(f));
    }

    /**
     * <pre>
     * float 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * float -&gt; byte[] (len : 4byte)
     * </pre>
     * 
     * @param src - byte 배열로 변환된 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static float getfloat(byte[] src) {
        return getfloat(src, 0);
    }

    /**
     * <pre>
     * float 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * 
     * float -&gt; byte[] (len : 4byte)
     * </pre>
     * 
     * @param src - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static float getfloat(byte[] src, int offset) {
        return Float.intBitsToFloat(getint(src, offset));
    }

    //__________________________________________________________________________
    //
    // double type 
    //__________________________________________________________________________
    /**
     * <pre>
     * double 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * double -&gt; byte[] (len : 8byte)
     * </pre>
     * 
     * @param d - 변환 대상 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] double2byte(double d) {
        byte[] dest = new byte[8];
        return setdouble(dest, 0, d);
    }

    /**
     * <pre>
     * double 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * double -&gt; byte[] (len : 8byte)
     * </pre>
     * 
     * @param dest - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @param d - 변환 대상 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static byte[] setdouble(byte[] dest, int offset, double d) {
        return setlong(dest, offset, Double.doubleToLongBits(d));
    }

    /**
     * <pre>
     * double 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * double -&gt; byte[] (len : 8byte)
     * </pre>
     * 
     * @param src - byte 배열로 변환된 데이타.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static double getdouble(byte[] src) {
        return getdouble(src, 0);
    }

    /**
     * <pre>
     * double 타입의 객체를 byte 배열 객체를 멤버 변수 ENDIAN의 상태값에 의해 정렬 합니다.
     * double -&gt; byte[] (len : 8byte)
     * </pre>
     * 
     * @param src - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @return 멤버 변수 ENDIAN의 상태값에 의해 정렬된 데이타.
     */
    public static double getdouble(byte[] src, int offset) {
        return Double.longBitsToDouble(getlong(src, offset));
    }

    //__________________________________________________________________________
    //
    // byte type
    //__________________________________________________________________________
    /**
     * <pre>
     * 인수(dest) 배열에 지정 위치(offset)에서 소스 배열로부터 소스 배열(src)을 복사 합니다
     * 인수(dest) 배열의 카피되는 시작 인덱스는 '0' 입니다.
     * byte[] -&gt; byte[]
     * </pre>
     * 
     * @param dest - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @param src - 복사 대상 데이타.
     * @return 복사 된 배열 데이타.
     */
    public static byte[] setbytes(byte[] dest, int offset, byte[] src) {
        return setbytes(dest, offset, src, src.length);
    }

    /**
     * <pre>
     * 인수(dest) 배열에 지정 위치(offset)에서 소스 배열로부터 소스 배열(src)을 인수길이(len) 만큼 복사 합니다
     * byte[] -&gt; byte[]
     * </pre>
     * 
     * @param dest - byte 배열로 변환된 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @param src - 복사 대상 데이타.
     * @param len - 복사 될 배열 길이.
     * @return 복사 된 배열 데이타.
     */
    public static byte[] setbytes(byte[] dest, int offset, byte[] src, int len) {
        System.arraycopy(src, 0, dest, offset, len);
        return dest;
    }

    /**
     * <pre>
     * 소스 배열(src)의 시작 인덱스(offset)부터 인수길이(len)만큼 되돌려 줍니다.
     * byte[] -&gt; byte[]
     * </pre>
     * 
     * @param src - 복사 대상 데이타.
     * @param offset - 배열의 시점(index)을 제어하기 위한 변수.
     * @param len - 복사 될 배열 길이.
     * @return 카피 된 배열 데이타.
     */
    public static byte[] getbytes(byte[] src, int offset, int len) {
        byte[] dest = new byte[len];
        System.arraycopy(src, offset, dest, 0, len);
        return dest;
    }

    /**
     * <pre>
     * 소스 배열(src)로 부터 인수 인덱스(offset) 위치의 데이타을 되돌려 줍니다.
     * </pre>
     * 
     * @param src - 소스 배열.
     * @param offset - 소스 배열의 인덱스 위치.
     * @return - 인수 인덱스(offset) 위치의 데이타
     */
    public static byte getbyte(byte[] src, int offset) {
        return src[offset];
    }

    //__________________________________________________________________________
    //
    // byte array type
    //__________________________________________________________________________
    /**
     * <pre>
     * 첫번째 인수배열(src1)과 두번째 인수(src2)를 병합 된 배열로 되돌려 줍니다.
     * </pre>
     * 
     * @param src1 - 병합될 첫번째 데이타.
     * @param src2 - 병합될 두번째 데이타.
     * @return 병합된 데이타.
     * @throws Exception - 예외처리.
     */
    public static byte[] mergeArrays(byte[] src1, byte src2) throws Exception {

        byte[] src3 = new byte[] {src2};

        return mergeArrays(src1, src3);

    }

    /**
     * <pre>
     * 첫번째 인수배열(src1)과 두번째 인수배열(src2)을 병합 된 배열 데이타로 되돌려 줍니다.
     * 배열 병합은 src1+src2로 된다.
     * </pre>
     * 
     * @param src1 - 병합될 첫번째 데이타.
     * @param src2 - 병합될 두번째 데이타.
     * @return 병합된 데이타.
     * @throws Exception - 예외처리.
     */
    public static byte[] mergeArrays(byte[] src1, byte[] src2) throws Exception {
        byte[] dest = null;
        try {
            if (src1 == null) {
                src1 = new byte[0];
            }
            if (src2 == null) {
                src2 = new byte[0];
            }

            dest = new byte[src1.length + src2.length];

            System.arraycopy(src1, 0, dest, 0, src1.length);
            System.arraycopy(src2, 0, dest, src1.length, src2.length);
        } catch (Exception e) {
            throw e;
        }

        return dest;
    }

    /**
     * <pre>
     * 2차원 배열의 데이타를 1차원 배열의 데이타로 병합 된 배열 데이타로 되돌려 줍니다.
     * byte[][] -&gt; byte[]
     * </pre>
     * 
     * @param bs - 변환 될 데이타
     * @return 병합 된 데이타.
     * @throws Exception - 예외 처리.
     */
    public static byte[] bindArrays(byte[][] bs) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (int i = 0; i < bs.length; i++) {
            try {
                if (bs[i] != null) {
                    out.write(bs[i]);
                }
            } catch (IOException e) {
                throw e;
            }
        }
        return out.toByteArray();
    }

    //__________________________________________________________________________
    //
    // byte compare
    //__________________________________________________________________________

    /**
     * <pre>
     * 인수배열(b)로부 인수 문자열(s)와 같은 문자가 존재하는지 비교 합니다.
     * 존재하면 true, 그렇지 않으면 false.
     * 
     * </pre>
     * 
     * @param b - 비교 될 소스 데이타.
     * @param s - 비교 할 데이타.
     * @return 비교 결과 값.
     */
    public static boolean isEquals(byte[] b, String s) {
        int slen = s.length();

        if (b == null || s == null) {
            return false;
        }
        if (b.length != slen) {
            return false;
        }
        for (int i = slen; i-- > 0;) {
            if (b[i] != s.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * <pre>
     * 첫번째 인수배열(a)로부 두번째 인수 문자열(b)와 같은 문자가 존재하는지 비교 합니다.
     * 존재하면 true, 그렇지 않으면 false.
     * 
     * </pre>
     * 
     * @param a - 비교 될 소스 데이타.
     * @param b - 비교 할 데이타.
     * @return 비교 결과 값.
     */
    public static boolean isEquals(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i = a.length; i-- > 0;) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    //__________________________________________________________________________
    //
    // Hexa Dump Utility
    //__________________________________________________________________________

    /**
     * <pre>
     * int형 데이타를 Hexa 코드로 변환된 데이타를 문자열로 되돌려 줍니다.
     * 
     * </pre>
     * 
     * @param i - 변환 될 데이타.
     * @return 변환 된 문자열 데이타.
     */
    public static String intToHexa(int i) {
        String desc = Integer.toHexString(i);
        desc = "0x" + ((desc.length() == 1) ? "0" : "") + desc;
        return desc;
    }

    /**
     * <pre>
     * byte 배열의 데이타를 Hexa 코드로 변환된 데이타를 문자열로 되돌려 줍니다.
     * </pre>
     * 
     * @param data - 변환 될 데이타.
     * @return 변환 된 문자열 데이타.
     */
    public static String byte2hexa(byte[] data) {
        return byte2hexa(data, true);
    }

    /**
     * <pre>
     * byte 배열의 데이타를 Hexa 코드로 변환된 데이타를 문자열로 되돌려 줍니다.
     * 두번째 인수(detail)이 true 일 경우는 ASCII 변환표를 되돌려 줍니다.
     * </pre>
     * 
     * @param data - 변환 될 데이타.
     * @param detail - ASCII 변환표로 변환 여부.
     * @return 변환 된 문자열 데이타.
     */
    public static String byte2hexa(byte[] data, boolean detail) {
        try {
            if (detail) {
                return hexaDump(data, data.length);
            } else {
                return hexaDump(data);
            }
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Byte배열을 받아 Hexa 코드를 되돌려 줍니다.
     * 
     * @param data - 출력 할 데이타.
     * @return 변환 된 Hexa 코드를 되돌려 줍니다.
     * @throws IOException - 예외처리.
     */
    private static String hexaDump(byte[] data) throws IOException {
        int len = data.length;
        Writer out = new StringWriter();
        for (int i = 0; i < len; i++) {
            out.write(byteToHexa(data[i]) + " ");
        }
        return out.toString();
    }

    /**
     * Byte배열과 크기를 받아 Hexa 코드 및 ASCII 변환표를 되돌려 줍니다.
     * 
     * @param data - 출력 할 데이타.
     * @param len - ASCII 변환표로 변환 여부.
     * @return 변환 된 ASCII 코드를 되돌려 줍니다.
     * @throws IOException - 예외처리.
     */
    public static String hexaDump(byte[] data, int len) throws IOException {
        Writer out = new StringWriter();
        int index = 0;

        for (int i = 0; i < len; i++) {
            index = (i + 1) % 16;
            out.write(byteToHexa(data[i]) + " ");

            if (index == 0) {
                out.write("          " + new String(data, (i + 1) - 16, 16));
                out.write("\n");
            }
        }
        if (index != 0) {
            for (int i = 0; i < (16 - index); i++) {
                out.write("  " + " ");
            }
            out.write("          " + new String(data, data.length - index, index));
            out.write("\n");
        }

        return out.toString();
    }

    /**
     * <pre>
     * byte를 hexa코드로 dump 합니다.
     * </pre>
     * 
     * @param byteNum - 변환 될 데이타.
     * @return hexa코드로 변환 된 문자열.
     */
    private static String byteToHexa(byte byteNum) {
        char[] hexa = new char[2];
        byte highbit = (byte)((byteNum >> 4) & (byte)0x0f);
        byte lowbit = (byte)(byteNum & (byte)0x0f);

        switch (highbit) {
            case 15:
                hexa[0] = 'f';
                break;

            case 14:
                hexa[0] = 'e';
                break;

            case 13:
                hexa[0] = 'd';
                break;

            case 12:
                hexa[0] = 'c';
                break;

            case 11:
                hexa[0] = 'b';
                break;

            case 10:
                hexa[0] = 'a';
                break;

            default:
                hexa[0] = (char)(highbit + (byte)0x30);

                break;
        }

        switch (lowbit) {
            case 15:
                hexa[1] = 'f';
                break;

            case 14:
                hexa[1] = 'e';
                break;

            case 13:
                hexa[1] = 'd';
                break;

            case 12:
                hexa[1] = 'c';
                break;

            case 11:
                hexa[1] = 'b';
                break;

            case 10:
                hexa[1] = 'a';
                break;

            default:
                hexa[1] = (char)(lowbit + (byte)0x30);

                break;
        }

        return new String(hexa);
    }

    //__________________________________________________________________________
    //
    // Protocol Utility
    //__________________________________________________________________________

    /**
     * <pre>
     * 문자열을 10byte로 변환 (문자열의 길이가 10byte 보다 작은 경우 0x00으로 채움)
     * </pre>
     * 
     * @param text - 변환 될 문자열.
     * @return 문자열을 byte 배열로 변환하여 되돌려 줍니다.
     * @throws UnsupportedEncodingException - 예외처리.
     */
    public static byte[] String2Byte10(String text) throws UnsupportedEncodingException {
        byte[] size = new byte[10];
        setbytes(size, 0, text.getBytes());
        return size;
    }

    /**
     * <pre>
     * 문자열을 15byte로 변환 (문자열의 길이가 15byte 보다 작은 경우 0x00으로 채움)
     * </pre>
     * 
     * @param text - 변환 될 문자열.
     * @return 문자열을 byte 배열로 변환하여 되돌려 줍니다.
     * @throws UnsupportedEncodingException - 예외처리.
     */
    public static byte[] String2Byte15(String text) throws UnsupportedEncodingException {
        byte[] size = new byte[15];
        setbytes(size, 0, text.getBytes());
        return size;
    }

    //__________________________________________________________________________
    //
    // Database Resource Utility
    //__________________________________________________________________________

    /**
     * Convert Blob Image to Byte Array Added 2005.02.04
     * 
     * @author JSH
     * @param resource -
     * @return byteArray
     */
    public static byte[] getBlobToByteArray(Blob resource) {

        byte[] returnValue = null;
        InputStream in = null;

        try {
            if (resource != null) {
                returnValue = new byte[(int)resource.length()];
                in = resource.getBinaryStream();
                in.read(returnValue);

                if (in != null) {
                    in.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    /**
     * <pre>
     * byte 데이타를 String으로 변환 한다.
     * </pre>
     * 
     * @param buffer - ByteBuffer
     * @param length - size
     * @return data
     */
    public static String getString(ByteBuffer buffer, int length) {
        byte[] tmpStr = new byte[length];
        buffer.get(tmpStr);
        int i = 0;
        for (; i < tmpStr.length; i++) {
            if (tmpStr[i] == 0x00) {
                break;
            }
        }
        byte[] rtnByte = new byte[i];
        System.arraycopy(tmpStr, 0, rtnByte, 0, rtnByte.length);
        return new String(rtnByte);
    }

    /**
     * <pre>
     * String 데이타를 byte 로 변환한다.
     * </pre>
     * 
     * @param str - src data
     * @param len - size
     * @return data
     */
    public static byte[] getBytes(String str, int len) {
        byte[] dest = new byte[len];

        if (str == null) {
            return dest;
        } else {
            byte[] src = str.getBytes();
            System.arraycopy(src, 0, dest, 0, src.length);
            return dest;
        }

    }

    /**
     * 내용 입력
     * 
     * @param b
     * @return
     */
    public static int byte2uchar(byte b) {
        return (b & 0xff);
    }

    /**
     * 내용 입력
     * 
     * @param c
     * @return
     */
    public static byte uchar2byte(int c) {
        return (byte)(c & 0xff);
    }

    /**
     * 내용 입력
     * 
     * @param s
     * @return
     */
    public static int short2ushort(short s) {
        return (s & 0xffff);
    }

    /**
     * 내용 입력
     * 
     * @param s
     * @return
     */
    public static short ushort2short(int s) {
        return (short)(s & 0xffff);
    }

    /**
     * 내용 입력
     * 
     * @param i
     * @return
     */
    public static long int2uint(int i) {
        return (i & 0xffffffffL);
    }

    /**
     * 내용 입력
     * 
     * @param i
     * @return
     */
    public static int uint2int(long i) {
        return (int)(i & 0xffffffffL);
    }

}
