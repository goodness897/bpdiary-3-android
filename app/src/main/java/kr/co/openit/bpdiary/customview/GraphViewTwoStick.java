package kr.co.openit.bpdiary.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PhoneUtil;

/**
 * 그래프 유틸리티
 */
public class GraphViewTwoStick extends View {
    private int widely = 0;
    private ManagerUtil.Localization localization = ManagerUtil.Localization.TYPE_01;
    private String weightType;
    private int dipInfo;

    private interface GraphBaseData {
        public String getName();

        public int getLineColor();

        public Bitmap getDefIcon();

        public Bitmap getSelIcon();

        public Bitmap getBubble();
    }

    /**
     * 그래프 데이터
     *
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @created 2013. 1. 29.
     */
    private class GraphDoubleData implements GraphBaseData {
        private final ArrayList<Double> dataList;
        private final ArrayList<String> dataDateList;
        private final String strName;
        // 라인색
        private final int nLineColor;
        // 기본 아이콘 이미지
        private final Bitmap bmpDefIcon;
        // 선택 아이콘 이미지
        private final Bitmap bmpSelIcon;
        // 값표시 이미지
        private final Bitmap bmpBubble;

        public GraphDoubleData(Context context,
                               String strName,
                               ArrayList<Double> dataList,
                               ArrayList<String> dataDateList,
                               int nLineColor,
                               int bmpDefIcon,
                               int bmpSelIcon,
                               int bmpBubble) {
            this.dataList = dataList;
            this.dataDateList = dataDateList;
            this.strName = strName;
            this.nLineColor = nLineColor;
            this.bmpDefIcon = BitmapFactory.decodeResource(context.getResources(), bmpDefIcon);
            // this.defIcon = defIcon;
            this.bmpSelIcon = BitmapFactory.decodeResource(context.getResources(), bmpSelIcon);
            this.bmpBubble = BitmapFactory.decodeResource(context.getResources(), bmpBubble);
        }

        /**
         * list getter
         *
         * @return list
         */
        public ArrayList<Double> getList() {
            return dataList;
        }

        public ArrayList<String> getDateList() {
            return dataDateList;
        }

        @Override
        public String getName() {
            return strName;
        }

        @Override
        public int getLineColor() {
            return nLineColor;
        }

        @Override
        public Bitmap getDefIcon() {
            return bmpDefIcon;
        }

        @Override
        public Bitmap getSelIcon() {
            return bmpSelIcon;
        }

        @Override
        public Bitmap getBubble() {
            return bmpBubble;
        }
    }

    /**
     * 그래프 데이터
     */
    private class GraphIntegerData implements GraphBaseData {
        private final ArrayList<Integer> dataList;
        private final ArrayList<String> dataDateList;
        private final String strName;
        // 라인색
        private final int nLineColor;
        // 기본 아이콘 이미지
        private final Bitmap bmpDefIcon;
        // 선택 아이콘 이미지
        private final Bitmap bmpSelIcon;
        // 값표시 이미지
        private final Bitmap bmpBubble;

        /**
         * 생성자
         *
         * @param context
         * @param strName
         * @param dataList
         * @param dataDateList
         * @param nLineColor
         * @param bmpDefIcon
         * @param bmpSelIcon
         * @param bmpBubble
         */
        public GraphIntegerData(Context context,
                                String strName,
                                ArrayList<Integer> dataList,
                                ArrayList<String> dataDateList,
                                int nLineColor,
                                int bmpDefIcon,
                                int bmpSelIcon,
                                int bmpBubble) {
            this.dataList = dataList;
            this.dataDateList = dataDateList;
            this.strName = strName;
            this.nLineColor = nLineColor;
            this.bmpDefIcon = BitmapFactory.decodeResource(context.getResources(), bmpDefIcon);
            // this.defIcon = defIcon;
            this.bmpSelIcon = BitmapFactory.decodeResource(context.getResources(), bmpSelIcon);
            this.bmpBubble = BitmapFactory.decodeResource(context.getResources(), bmpBubble);

        }

        public ArrayList<Integer> getList() {
            return dataList;
        }

        public ArrayList<String> getDateList() {
            return dataDateList;
        }

        @Override
        public String getName() {
            return strName;
        }

        @Override
        public int getLineColor() {
            return nLineColor;
        }

        @Override
        public Bitmap getDefIcon() {
            return bmpDefIcon;
        }

        @Override
        public Bitmap getSelIcon() {
            return bmpSelIcon;
        }

        @Override
        public Bitmap getBubble() {
            return bmpBubble;
        }
    }

    private class NormalRange {
        private final RectF rRange;
        private final Paint p;

        public NormalRange(int lowValue, int hightValue, int color, int yIndex) {
            rRange = new RectF(fGraphStartX, getIndexToTop(hightValue, yIndex), fGraphEndX, getIndexToTop(lowValue,
                    yIndex));
            p = new Paint();
            p.setColor(color);
        }

        public RectF getRange() {
            return rRange;
        }

        public Paint getPaint() {
            return p;
        }
    }

    private class GoalRange {
        private final int value;
        private final Paint p;

        public GoalRange(int value, int color) {
            this.value = value;
            p = new Paint();
            p.setColor(color);
        }

        public int getValue() {
            return value;
        }

        public Paint getPaint() {
            return p;
        }
    }

    /**
     * 그래프 모양 타입
     */
    //	public static final int BAR = 1;

    public static final int LINE = 2;

    //
    //	public static final int LINEFILL = 4;
    //
    //	public static final int LINEANDFILL = 8;
    /**
     * 현재 그래프 퍼센트
     */
    public static float fGraphPercent = 1.0f;
    /**
     * 메세지 리턴 타입
     */
    public static final int MESSAGE_WHAT_RESULT_TWO = 0;
    public static final int MESSAGE_ROTATE_ANIMATION = 1;
    /**
     * 그래프 시작 TopY빈칸
     */
    private static final int START_TOP_Y_SPACE = 35;
    /**
     * 그래프 시작 BottomY빈칸
     */
    private static final int START_BOTTOM_Y_SPACE = 100;
    /**
     * 그래프 시작 LeftX빈칸
     */
    //    private static final int START_LEFT_X_SPACE = 35;
    private static final int START_LEFT_X_SPACE = 80;
    /**
     * 그래프 시작 RightX빈칸
     */
    //    private static final int START_RIGHT_X_SPACE = 35;
    private static final int START_RIGHT_X_SPACE = 80;
    /**
     * x축에 표시 해야할 데이터 량
     */
    private int nShowXValue;
    /**
     * y축에 표시 할 데이터 량
     */
    private int nShowYValue;
    /**
     * 첫 시작 인덱스
     */
    private int nFirstIdx = 0;
    /**
     * 전체 좌표
     */
    private int nStartX;
    private int nStartY;
    private int nEndX;
    private int nEndY;
    /**
     * 그래프 좌표
     */
    private float fGraphStartX;
    private float fGraphStartY;
    private float fGraphEndX;
    private float fGraphEndY;
    /**
     * y1의 맥스 값
     */
    private int nMaxY1;
    /**
     * y1의 Min 값
     */
    private int nMinY1;
    /**
     * y2의 맥스값
     */
    private int nMaxY2;
    /**
     * y2의 min값
     */
    private int nMinY2;
    /**
     * 그래프 하나의 폭
     */
    private float fGraphWidth;
    /**
     * 한 막대간의 간격
     */
    private float fGraphSpace;
    /**
     * 한 막대간의 간격 1/2
     */
    private float fGraphSpacehalf;
    /**
     * 그래프 모양
     */
    private int nGraphShape;
    /**
     * 현재 선택된 라인
     */
    private int nLineX = 0;
    /**
     * 이전에 선택된 라인
     */
    private final int nBeforeLineX = 0;
    /**
     * 데이터 값의 갯수
     */
    private int nDataAmount = 0;
    /**
     * 초기화 여부
     */
    private boolean isInitialize;
    private String dataType = DATA_TYPE_INT;
    public static final String DATA_TYPE_INT = "int";
    public static final String DATA_TYPE_DOUBLE = "double";
    public static final int TYPE_TIME = 1;
    private boolean isBubbleEnable = true;
    private boolean isBottomWidely = false;
    /**
     * 처음 2터치 됐을때 거리
     */
    private int nBaseDist;
    /**
     * 현재 거리 비율
     */
    private int nBaseZoom;
    /**
     * 몇 픽셀당 비율 높일것인지
     */
    private final int nPixToZoom = 30;
    /**
     * 그래픽 유틸을 쓰고있는 액티비티의 핸들러
     */
    private final Handler hCurrentActivitiy;
    /**
     * 데이터의 리스트가 들어있는 값
     */
    private final ArrayList<GraphDoubleData> graphDoubleDataList = new ArrayList<GraphDoubleData>();
    /**
     * 데이터의 리스트가 들어있는 값
     */
    private final ArrayList<GraphIntegerData> graphIntegerDataList = new ArrayList<GraphIntegerData>();
    /**
     * 그래프 범위표시 리스트가 들어있는 값
     */
    private final ArrayList<NormalRange> rangeList = new ArrayList<NormalRange>();
    private final ArrayList<GoalRange> goalList = new ArrayList<GoalRange>();
    private final Context mContext;
    /**
     * 스크롤용
     */
    private float fModX = 0;
    /**
     * 스피드 X
     */
    private float fSpeedX = 0.0f;
    /**
     * 스크롤X값의 Max값
     */
    private float fModMaxX;
    /**
     * IndicatorLine 색상
     */
    private final int cIndicatorLine = Color.rgb(229, 229, 229);
    /**
     * 그래프 테두리 색상
     */
    private final int cOutLine = Color.rgb(229, 229, 229);
    /**
     * 그래프 배경 색상
     */
    private final int cBg = Color.rgb(255, 255, 255);
    /**
     * 텍스트 색상
     */
    // private final int cText = Color.rgb(208, 208, 208);
//    private final int cText = Color.rgb(96, 96, 96);
    private final int cText = Color.rgb(195, 192, 189);
    // 흰색 테두리 부분
    private RectF rLeftBg;
    private RectF rRightBg;
    private RectF rTopBg;
    private RectF rBotBg;
    private RectF rGraphBg;
    // 회색 그래프 테두리 부분
    private RectF rLeftOutLine;
    private RectF rBotOutLine;
    private RectF rRightOutLine;
    // 페인트
    private Paint pBg;
    private Paint outLine;
    private Paint pWhite;
    private Paint pBotBoldTxt;
    private Paint pBotTxt;
    private Paint pYValue;
    private Paint pIndicator;
    /**
     * 처음 라인 설정
     */
    boolean isLineX = false;
    /**
     * 현재 Line X 좌표
     */
    float fCurrentLineX;
    /**
     * 터치한 X좌표 (이전 move X좌표 와 비교하여 속도 조절)
     */
    float fTouchX;
    /**
     * 터치한 X좌표 (Move 되었는지 체크하기 위한 값)
     */
    float fTouchDown;
    /**
     * 움직임 여부
     */
    boolean isMove;
    /**
     * 그래프의 평균속도 구하기 위해
     */
    ArrayList<Float> speedList = new ArrayList<Float>();
    /**
     * 몇개의 데이터에서 평균 구할지
     */
    private final int AVR_DATA = 10;
    /**
     * today일 경우 00:00형태로 표기
     */
    private boolean isToday = false;
    /**
     *
     */
    private boolean isStay = false;
    private boolean isCustom = false;
    private CustomDate customDateType = CustomDate.TODAY;

    public enum CustomDate {
        TODAY, WEEK, MONTH, YEAR, ALL
    }

    /**
     * 생성자
     *
     * @param context
     * @param h
     */
    public GraphViewTwoStick(Context context, Handler h) {
        super(context);
        this.mContext = context;
        hCurrentActivitiy = h;
        isInitialize = false;
        dipInfo = Math.round(PhoneUtil.dipInfo(mContext));
    }

    /**
     * JSON오브젝트를 받아와 리스트에 저장
     *
     * @param intList
     * @param name
     */
    public void addObjectList(JSONObject[] intList,
                              String name,
                              String date,
                              int lineColor,
                              int defIcon,
                              int selIcon,
                              int bubble) {
        ArrayList<String> arrDateList = new ArrayList<String>();
        nDataAmount = intList.length;
        if (dataType.equals(DATA_TYPE_DOUBLE)) {
            ArrayList<Double> arrList = new ArrayList<Double>();
            for (int i = 0; i < nDataAmount; i++) {
                try {
                    arrList.add(intList[i].getDouble(name));
                    arrDateList.add(intList[i].getString(date));
                } catch (JSONException e) {
                    e.printStackTrace();
                    nDataAmount = 0;
                    return;
                }
            }
            GraphDoubleData graphdoubledata = new GraphDoubleData(mContext,
                    name,
                    arrList,
                    arrDateList,
                    lineColor,
                    defIcon,
                    selIcon,
                    bubble);
            graphDoubleDataList.add(graphdoubledata);
        } else if (dataType.equals(DATA_TYPE_INT)) {
            ArrayList<Integer> arrList = new ArrayList<Integer>();
            for (int i = 0; i < nDataAmount; i++) {
                try {
                    arrList.add(intList[i].getInt(name));
                    arrDateList.add(intList[i].getString(date));
                } catch (JSONException e) {
                    e.printStackTrace();
                    nDataAmount = 0;
                    return;
                }
            }
            GraphIntegerData graphintegerdata = new GraphIntegerData(mContext,
                    name,
                    arrList,
                    arrDateList,
                    lineColor,
                    defIcon,
                    selIcon,
                    bubble);
            graphIntegerDataList.add(graphintegerdata);
        }
        calculate();
        getLineDataSendMessage();
        graphRotateAni();
        invalidate();
    }

    /**
     * ArrayList<Map<String,String>>을 받아와 리스트에 저장
     *
     * @param list
     * @param name
     */
    public void addObjectList(List<Map<String, String>> list,
                              String key,
                              String name,
                              String date,
                              int lineColor,
                              int defIcon,
                              int selIcon,
                              int bubble) {
        ArrayList<String> arrDateList = new ArrayList<String>();
        nDataAmount = list.size();
        if (dataType.equals(DATA_TYPE_DOUBLE)) {
            ArrayList<Double> arrList = new ArrayList<Double>();
            for (int i = 0; i < nDataAmount; i++) {
                double data = Double.parseDouble(list.get(i).get(key));
                String strAddDate = list.get(i).get(date);
                arrList.add(data);
                arrDateList.add(strAddDate);
                if (data > 0) {
                    nFirstIdx = i;
                }
            }
            GraphDoubleData graphdoubledata = new GraphDoubleData(mContext,
                    name,
                    arrList,
                    arrDateList,
                    lineColor,
                    defIcon,
                    selIcon,
                    bubble);
            graphDoubleDataList.add(graphdoubledata);
        } else if (dataType.equals(DATA_TYPE_INT)) {
            ArrayList<Integer> arrList = new ArrayList<Integer>();
            for (int i = 0; i < nDataAmount; i++) {
                int data = strToInt(list.get(i).get(key));
                String strAddDate = list.get(i).get(date);
                arrList.add(data);
                arrDateList.add(strAddDate);
                if (data > 0) {
                    nFirstIdx = i;
                }
            }
            GraphIntegerData graphintegerdata = new GraphIntegerData(mContext,
                    name,
                    arrList,
                    arrDateList,
                    lineColor,
                    defIcon,
                    selIcon,
                    bubble);
            graphIntegerDataList.add(graphintegerdata);
        }
        calculate();
        getLineDataSendMessage();
        graphRotateAni();
        invalidate();
    }

    /**
     * List<ArrayList<Map<String,String>>>을 받아와 리스트에 저장
     *
     * @param list
     * @param name
     */
    public void addObjectList(List<ArrayList<Map<String, String>>> list,
                              int idx,
                              String key,
                              String name,
                              String date,
                              int lineColor,
                              int defIcon,
                              int selIcon,
                              int bubble) {
        ArrayList<String> arrDateList = new ArrayList<String>();
        nDataAmount = list.size();
        if (dataType.equals(DATA_TYPE_DOUBLE)) {
            ArrayList<Double> arrList = new ArrayList<Double>();
            for (int i = 0; i < nDataAmount; i++) {
                double data = Double.parseDouble(list.get(i).get(idx).get(key));
                String strAddDate = list.get(i).get(idx).get(date);
                arrList.add(data);
                arrDateList.add(strAddDate);
                if (data > 0) {
                    nFirstIdx = i;
                }
            }
            GraphDoubleData graphdoubledata = new GraphDoubleData(mContext,
                    name,
                    arrList,
                    arrDateList,
                    lineColor,
                    defIcon,
                    selIcon,
                    bubble);
            graphDoubleDataList.add(graphdoubledata);
        } else if (dataType.equals(DATA_TYPE_INT)) {
            ArrayList<Integer> arrList = new ArrayList<Integer>();
            for (int i = 0; i < nDataAmount; i++) {
                int data = strToInt(list.get(i).get(idx).get(date));
                String strAddDate = list.get(i).get(idx).get(date);
                arrList.add(data);
                arrDateList.add(strAddDate);
                if (data > 0) {
                    nFirstIdx = i;
                }
            }
            GraphIntegerData graphintegerdata = new GraphIntegerData(mContext,
                    name,
                    arrList,
                    arrDateList,
                    lineColor,
                    defIcon,
                    selIcon,
                    bubble);
            graphIntegerDataList.add(graphintegerdata);
        }
        calculate();
        getLineDataSendMessage();
        graphRotateAni();
        invalidate();
    }

    public void addRange(int lowValue, int highValue, int color, int yIndex) {
        rangeList.add(new NormalRange(lowValue, highValue, color, yIndex));
        calculate();
        invalidate();
    }

    public void addGoal(int value, int color) {
        goalList.add(new GoalRange(value, color));
        calculate();
        invalidate();
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setBubbleEnabled(boolean isBubbleEnable) {
        this.isBubbleEnable = isBubbleEnable;
    }

    public boolean isBubbleEnabled() {
        return isBubbleEnable;
    }

    public void setBottomWidely(boolean isBottomWidely) {
        this.isBottomWidely = isBottomWidely;
    }

    public boolean isBottomWidely() {
        return isBottomWidely;
    }

    public void setTodayText(boolean isToday) {
        this.isToday = isToday;
    }

    public void setStayDataX(boolean isStay) {
        this.isStay = isStay;
    }

    public void setCustomDataX(boolean isCustom) {
        this.isCustom = isCustom;
    }

    public void setCustomDataX(CustomDate customDateType) {
        this.customDateType = customDateType;
    }

    public void setLocalizationTYPE(ManagerUtil.Localization localization) {
        this.localization = localization;
    }

    private String yType = "";
    public static final String Y_DATA_TYPE_TIME = "time";

    public void setYDataType(String yType) {
        this.yType = yType;
    }

    public String getYDataType() {
        return yType;
    }

    /**
     * string 을 int 로 변경하여 반환
     *
     * @param data
     * @return
     */
    private int strToInt(Object data) {
        int result = 0;

        if (data == null) {
            return result;
        }

        try {
            String value = data.toString().trim();

            if (value.indexOf(".") > -1) {
                value = value.substring(0, value.indexOf("."));
            }

            result = Integer.parseInt(value);
        } catch (Exception e) {
            return result;
        }

        return result;
    }

    /**
     * 데이터를 모두 삭제
     */
    public void removeDataAll() {
        nDataAmount = 0;
        graphDoubleDataList.clear();
        graphIntegerDataList.clear();
    }

    /**
     * 그래프를 초기화
     *
     * @param graphType
     * @param showXValue
     * @param yMax1
     * @param yMin1
     * @param yMax2
     * @param yMin2
     * @param showYValue
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public void setInitialize(int graphType,
                              int showXValue,
                              int yMax1,
                              int yMin1,
                              int yMax2,
                              int yMin2,
                              int showYValue,
                              int startX,
                              int startY,
                              int endX,
                              int endY) {
        this.nGraphShape = graphType;
        this.nShowXValue = showXValue;
        this.nMaxY1 = yMax1;
        this.nMinY1 = yMin1;
        this.nMaxY2 = yMax2;
        this.nMinY2 = yMin2;
        this.nShowYValue = showYValue;
        this.nStartX = startX;
        this.nStartY = startY;
        this.nEndX = endX;
        this.nEndY = endY;

        calculate();
        fGraphPercent = 1.0f;

        rLeftBg = new RectF(this.nStartX, this.nStartY, this.fGraphStartX, this.nEndY);
        rRightBg = new RectF(this.fGraphEndX, this.nStartY, this.nEndX, this.nEndY);
        rTopBg = new RectF(this.nStartX, this.nStartY, this.nEndX, this.fGraphStartY);
        rBotBg = new RectF(this.nStartX, this.fGraphEndY, this.nEndX, this.nEndY);

        rLeftOutLine = new RectF(this.fGraphStartX - 2, this.fGraphStartY, this.fGraphStartX, this.fGraphEndY + 4);
        rBotOutLine = new RectF(0, this.fGraphEndY + 2, this.fGraphEndX, this.fGraphEndY + 4);
//        rBotOutLine = new RectF(this.fGraphStartX, this.fGraphEndY + 2, this.fGraphEndX, this.fGraphEndY + 4);
        rRightOutLine = new RectF(this.fGraphEndX, this.fGraphStartY, this.fGraphEndX + 2, this.fGraphEndY + 4);
        rGraphBg = new RectF(fGraphStartX, fGraphStartY, fGraphEndX, fGraphEndY);

        //아웃라인 Paint 초기화
        outLine = new Paint();
        outLine.setColor(cOutLine);

        //흰부분 Paint 
        pWhite = new Paint();
        //외곽선 바깥 배경
        pWhite.setColor(getResources().getColor(R.color.color_fbfbfb));
        // pWhite.setColor(getResources().getColor(R.color.color_00000000));
        // 그래프 배경
        pBg = new Paint();
        pBg.setColor(getResources().getColor(R.color.color_fbfbfb));
        // pBg.setColor(getResources().getColor(R.color.color_05000000));
        // 아래 부분 텍스트
        pBotBoldTxt = new Paint();
        switch (Math.round(PhoneUtil.dipInfo(mContext))) {
            case 0:
            case 1:
                pBotBoldTxt.setTextSize(START_BOTTOM_Y_SPACE / 4.5f);
                break;
            case 2:
                pBotBoldTxt.setTextSize(START_BOTTOM_Y_SPACE / 4.0f);
                break;
            case 3:
                pBotBoldTxt.setTextSize(START_BOTTOM_Y_SPACE / 3.5f);
                break;
            default:
                pBotBoldTxt.setTextSize(START_BOTTOM_Y_SPACE / 3.0f);
                break;
        }

        pBotBoldTxt.setColor(cText);
        pBotBoldTxt.setTextAlign(Align.CENTER);
        pBotBoldTxt.setAntiAlias(true);
//        pBotBoldTxt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        // 아래 부분 텍스트
        pBotTxt = new Paint();
        switch (Math.round(PhoneUtil.dipInfo(mContext))) {
            case 0:
            case 1:
                pBotTxt.setTextSize(START_BOTTOM_Y_SPACE / 4.5f);
                break;
            case 2:
                pBotTxt.setTextSize(START_BOTTOM_Y_SPACE / 4.0f);
                break;
            case 3:
                pBotTxt.setTextSize(START_BOTTOM_Y_SPACE / 3.5f);
                break;
            default:
                pBotTxt.setTextSize(START_BOTTOM_Y_SPACE / 3.0f);
                break;
        }
        pBotTxt.setColor(cText);
        pBotTxt.setTextAlign(Align.CENTER);
        pBotTxt.setAntiAlias(true);
        // Y절편 텍스트
        pYValue = new Paint();
        pYValue.setColor(cText);
        switch (Math.round(PhoneUtil.dipInfo(mContext))) {
            case 0:
            case 1:
                pYValue.setTextSize(START_BOTTOM_Y_SPACE / 4.5f);
                break;
            case 2:
                pYValue.setTextSize(START_BOTTOM_Y_SPACE / 4.0f);
                break;
            case 3:
                pYValue.setTextSize(START_BOTTOM_Y_SPACE / 3.5f);
                break;
            default:
                pYValue.setTextSize(START_BOTTOM_Y_SPACE / 3.0f);
                break;
        }
        pYValue.setAntiAlias(true);
        pYValue.setTextAlign(Align.RIGHT);
//        pYValue.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        pIndicator = new Paint();
        pIndicator.setColor(cIndicatorLine);
        pIndicator.setStrokeWidth(2);

        isInitialize = true;
        invalidate();
    }

    /**
     * 그래프 너비 및 필요값 계산
     */
    private void calculate() {
        fGraphWidth = (fGraphEndX - fGraphStartX) / (nShowXValue + (nShowXValue) / 2.0f);
        fGraphSpace = fGraphWidth / 2;
        fGraphSpacehalf = fGraphSpace / 2;

        //        graphStartX = startX + graphSpacehalf;
        //        graphStartY = startY + START_Y_SPACE;
        fGraphStartX = nStartX + START_LEFT_X_SPACE;
        widely = 0;
        switch ((int) PhoneUtil.dipInfo(mContext)) {
            case 0:
            case 1:
                fGraphStartY = nStartY + START_TOP_Y_SPACE + 50;
                widely = 30;
                break;
            case 2:
                fGraphStartY = nStartY + START_TOP_Y_SPACE + 40;
                widely = 40;
                break;
            case 3:
                fGraphStartY = nStartY + START_TOP_Y_SPACE + 30;
                widely = 50;
                break;
            default:
                fGraphStartY = nStartY + START_TOP_Y_SPACE + 30;
                widely = 55;
                break;
        }
        fGraphEndX = nEndX - START_RIGHT_X_SPACE;
        if (isBottomWidely) {
            fGraphEndY = nEndY - START_BOTTOM_Y_SPACE - widely;
        } else {
            fGraphEndY = nEndY - START_BOTTOM_Y_SPACE;
        }
        if (nDataAmount - nShowXValue > 0) {

            fModMaxX = (fGraphWidth * (nDataAmount - nShowXValue)) + (fGraphSpace * (nDataAmount - nShowXValue));

            if (!isLineX) {

                //                fModX = fModMaxX;
                //                nLineX = nDataAmount - 1;

                int data = nDataAmount - nShowXValue - nFirstIdx;

                if (data < 0) {
                    data = 0;
                }

                fModMaxX = (fGraphWidth * (data)) + (fGraphSpace * (data));

                if (nFirstIdx > nShowXValue) {
                    fModX = (fGraphWidth * (nFirstIdx + 1 - nShowXValue)) + (fGraphSpace * (nFirstIdx + 1 - nShowXValue));
                } else {
                    //                
                    fModX = 0;
                }
                nLineX = nFirstIdx;

                fCurrentLineX = getIndexToMiddle(nLineX);
                isLineX = true;

            } else {
                //                fModX = fModMaxX;
                if (nFirstIdx > nShowXValue) {
                    fModX = (fGraphWidth * (nFirstIdx + 1 - nShowXValue)) + (fGraphSpace * (nFirstIdx + 1 - nShowXValue));
                } else {
                    //                
                    fModX = 0;
                }
                //				if(modX > modXMax){
                //					modX = modXMax;
                //				}
            }

        } else {

            fModMaxX = 0;
            fModX = 0;

            if (nDataAmount > 0) {
                if (!isLineX) {
                    //                    nLineX = nDataAmount - 1;
                    nLineX = nFirstIdx;
                    isLineX = true;
                }
            }
        }
    }

    /**
     * 그래프 모양 변경
     *
     * @param shape
     */
    public void setGraphShape(int shape) {
        this.nGraphShape = shape;
        fGraphPercent = 1.0f;
        invalidate();
    }

    /**
     * graphShape getter
     *
     * @return
     */
    public int getGraphShape() {
        return nGraphShape;
    }

    /**
     * item 추가
     */
    public void addItem() {
        this.nShowXValue += 1;
        if (this.nShowXValue > 20) {
            this.nShowXValue = 20;
        }

        calculate();
        invalidate();
    }

    /**
     * 내용 입력
     */
    public void subItem() {
        this.nShowXValue -= 1;
        if (this.nShowXValue < 10) {
            this.nShowXValue = 10;
        }

        calculate();
        invalidate();
    }

    //	/**
    //	 * 내용 입력
    //	 */
    //	public void addYMax() {
    //		this.yMax += 30;
    //		invalidate();
    //	}
    //
    //	/**
    //	 * 내용 입력
    //	 */
    //	public void subYMax() {
    //		this.yMax -= 30;
    //		invalidate();
    //	}

    @Override
    protected void onDraw(Canvas canvas) {

        if (!isInitialize) {
            return;
        }
        //그래프 배경 색상
        canvas.drawRect(rGraphBg, pBg);

        //범위 그리기
        for (NormalRange range : rangeList) {
            canvas.drawRect(range.getRange(), range.getPaint());
        }
//        for (GoalRange range : goalList) {
//            canvas.drawLine(fGraphStartX,
//                    getIndexToTop(range.getValue()),
//                    fGraphEndX,
//                    getIndexToTop(range.getValue()),
//                    range.getPaint());
//        }
        // 선택라인 영역 그리기
        if (nDataAmount > 0) {
            float selectX = getIndexToMiddle(nLineX);
            float y = fGraphStartY;
            canvas.drawLine(selectX, y, selectX, getGraphBottom() + 1, pIndicator);
        }

        //그래프 그리기
        drawGraph(canvas);

        // 선택 영역 숫자...
        if (!isBubbleEnable) {
            if (nDataAmount > 0) {
                if (dataType.equals(DATA_TYPE_DOUBLE)) {
                    setBubbleDoubleData(canvas);
                } else if (dataType.equals(DATA_TYPE_INT)) {
                    setBubbleIntData(canvas);
                }
            }
        }
        //그래프 화이트 영역
        canvas.drawRect(rTopBg, pWhite);
        canvas.drawRect(rBotBg, pWhite);
        if (dataType.equals(DATA_TYPE_DOUBLE)) {
            // 밑에 숫자
            for (int idx = 0, len = graphDoubleDataList.size(); idx < 1; idx++) {
                GraphDoubleData gd = graphDoubleDataList.get(idx);
                ArrayList<Double> list = gd.getList();
                ArrayList<String> datelist = gd.getDateList();
                for (int i = 0; i < list.size(); i++) {
                    float midX = getIndexToMiddle(i);
                    if (midX < 0) {
                        // 그리지 않음
                    } else if (midX > nEndX) {
                        // 그리지 않음
                        break;
                    } else {
                        datelist.get(i).length();
                        if (isStay && !isCustom) {
                            //                            Log.i("srpark", "date::" + datelist.get(i).length());
                            if (isBottomWidely && datelist.get(i).length() >= 12) {
                                canvas.drawText(datelist.get(i).substring(4, 6) + "/" + datelist.get(i).substring(6, 8),
                                        midX,
                                        nEndY - START_BOTTOM_Y_SPACE + 30,
                                        pBotBoldTxt);
                                canvas.drawText(datelist.get(i).substring(8, 10) + ":"
                                                + datelist.get(i).substring(10, 12),
                                        midX,
                                        nEndY - START_BOTTOM_Y_SPACE + 80,
                                        pBotTxt);

                            } else {
                                canvas.drawText(datelist.get(i), midX, nEndY - START_BOTTOM_Y_SPACE / 3, pBotBoldTxt);
                            }
                        } else if (isStay && isCustom) {
                            float y = 0;
                            switch (dipInfo) {
                                case 0:
                                case 1:
                                case 2:
                                    y = nEndY - (START_BOTTOM_Y_SPACE - 60) / 3;
                                    break;
                                default:
                                    y = nEndY - START_BOTTOM_Y_SPACE / 3;
                                    break;
                            }
                            if (isBottomWidely) {
                                if (customDateType == CustomDate.TODAY) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", "^", "yyyyMMddHH", datelist.get(i));
                                    canvas.drawText(arrDate[1] + ":00",
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                            pBotBoldTxt);
                                } else if (customDateType == CustomDate.WEEK) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMMdd", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                                pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                                pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                                pBotBoldTxt);
                                    }
                                } else if (customDateType == CustomDate.MONTH) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMMdd", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                                pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                                pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                                pBotBoldTxt);
                                    }
                                } else if (customDateType == CustomDate.YEAR) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMM", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                                pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrSplitDate[0],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                                pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely - 30,
                                                pBotBoldTxt);
                                    }
                                } else if (customDateType == CustomDate.ALL) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", ":", "yyyyMMddHHmmss", datelist.get(i));
//                                    String[] arrSplitDate = arrDate[0].split("/");
                                    DisplayMetrics metrics = new DisplayMetrics();
                                    WindowManager mWindowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
                                    mWindowManager.getDefaultDisplay().getMetrics(metrics);

                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrDate[0].substring(5, arrDate[0].length()),
                                                midX,
                                                nEndY - widely - 60,
                                                pBotBoldTxt);
                                        if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                                            canvas.drawText(arrDate[1],
                                                    midX,
                                                    nEndY - widely - 30,
                                                    pBotBoldTxt);
                                        } else {
                                            canvas.drawText(arrDate[1],
                                                    midX,
                                                    nEndY - widely - 10,
                                                    pBotBoldTxt);
                                        }
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrDate[0].substring(0, arrDate[0].length() - 5),
                                                midX,
                                                nEndY - widely - 60,
                                                pBotBoldTxt);
                                        if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                                            canvas.drawText(arrDate[1],
                                                    midX,
                                                    nEndY - widely - 30,
                                                    pBotBoldTxt);
                                        } else {
                                            canvas.drawText(arrDate[1],
                                                    midX,
                                                    nEndY - widely - 10,
                                                    pBotBoldTxt);
                                        }
                                    } else {
                                        canvas.drawText(arrDate[0].substring(0, arrDate[0].length() - 5),
                                                midX,
                                                nEndY - widely,
                                                pBotBoldTxt);
                                        canvas.drawText(arrDate[1],
                                                midX,
                                                nEndY - widely + 50,
                                                pBotBoldTxt);
                                    }
                                }
                            } else {
                                if (customDateType == CustomDate.TODAY) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", "^", "yyyyMMddHH", datelist.get(i));
                                    canvas.drawText(arrDate[1] + ":00",
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                                } else if (customDateType == CustomDate.WEEK) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMMdd", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2],
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                                } else if (customDateType == CustomDate.MONTH) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMMdd", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2],
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                                } else if (customDateType == CustomDate.YEAR) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMM", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    canvas.drawText(arrSplitDate[1],
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                                }
                            }
                        } else if (datelist.get(i).length() == 10) {
                            // 1일 데이터
                            canvas.drawText(datelist.get(i).substring(8, 10),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE / 3,
                                    pBotBoldTxt);
                        } else if (datelist.get(i).length() == 8) {
                            // 1주일 데이터 && 한달 데이터
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/" + datelist.get(i).substring(6, 8),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE / 3,
                                    pBotBoldTxt);
                        } else if (datelist.get(i).length() == 6) {
                            // 1년 데이터
                            canvas.drawText(datelist.get(i).substring(2, 4) + "/" + datelist.get(i).substring(4, 6),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE / 3,
                                    pBotBoldTxt);
                        } else if (datelist.get(i).length() == 14 && isBottomWidely) {
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/" + datelist.get(i).substring(6, 8),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE + 30,
                                    pBotBoldTxt);
                            canvas.drawText(datelist.get(i).substring(8, 10) + ":" + datelist.get(i).substring(10, 12),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE + 80,
                                    pBotTxt);
                        } else {
                            canvas.drawText(i + 1 + "", midX, nEndY - START_BOTTOM_Y_SPACE / 3, pBotBoldTxt);
                        }
                    }
                }
            }
        } else if (dataType.equals(DATA_TYPE_INT)) {
            // 밑에 숫자
            for (int idx = 0, len = graphIntegerDataList.size(); idx < 1; idx++) {
                GraphIntegerData gd = graphIntegerDataList.get(idx);
                ArrayList<Integer> list = gd.getList();
                ArrayList<String> datelist = gd.getDateList();
                for (int i = 0; i < list.size(); i++) {
                    float midX = getIndexToMiddle(i);
                    if (midX < 0) {
                        // 그리지 않음
                    } else if (midX > nEndX) {
                        // 그리지 않음
                        break;
                    } else {
                        datelist.get(i).length();
                        if (isStay && !isCustom) {
                            if (isBottomWidely) {
                                canvas.drawText(datelist.get(i).substring(4, 6) + "/" + datelist.get(i).substring(6, 8),
                                        midX,
                                        nEndY - START_BOTTOM_Y_SPACE + 30,
                                        pBotBoldTxt);
                                canvas.drawText(datelist.get(i).substring(8, 10) + ":"
                                                + datelist.get(i).substring(10, 12),
                                        midX,
                                        nEndY - START_BOTTOM_Y_SPACE + 80,
                                        pBotTxt);
                            } else {
                                canvas.drawText(datelist.get(i), midX, nEndY - START_BOTTOM_Y_SPACE / 3, pBotBoldTxt);
                            }
                        } else if (isStay && isCustom) {
                            float y = 0;
                            switch (dipInfo) {
                                case 0:
                                case 1:
                                case 2:
                                    y = nEndY - (START_BOTTOM_Y_SPACE - 60) / 3;
                                    break;
                                default:
                                    y = nEndY - START_BOTTOM_Y_SPACE / 3;
                                    break;
                            }
                            if (isBottomWidely) {
                                if (customDateType == CustomDate.TODAY) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", "^", "yyyyMMddHH", datelist.get(i));
                                    canvas.drawText(arrDate[1] + ":00",
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                            pBotBoldTxt);
                                } else if (customDateType == CustomDate.WEEK) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMMdd", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                                pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                                pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                                pBotBoldTxt);
                                    }
                                } else if (customDateType == CustomDate.MONTH) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMMdd", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                                pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                                pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                                pBotBoldTxt);
                                    }
                                } else if (customDateType == CustomDate.YEAR) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMM", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                                pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrSplitDate[0],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                                pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrSplitDate[1],
                                                midX,
                                                nEndY - START_BOTTOM_Y_SPACE / 3 - widely,
                                                pBotBoldTxt);
                                    }
                                } else if (customDateType == CustomDate.ALL) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", ":", "yyyyMMddHHmmss", datelist.get(i));
//                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrDate[0].substring(5, arrDate[0].length()),
                                                midX,
                                                nEndY - widely,
                                                pBotBoldTxt);
                                        canvas.drawText(arrDate[1],
                                                midX,
                                                nEndY - widely + 50,
                                                pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrDate[0].substring(0, arrDate[0].length() - 5),
                                                midX,
                                                nEndY - widely,
                                                pBotBoldTxt);
                                        canvas.drawText(arrDate[1],
                                                midX,
                                                nEndY - widely + 50,
                                                pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrDate[0].substring(0, arrDate[0].length() - 5),
                                                midX,
                                                nEndY - widely,
                                                pBotBoldTxt);
                                        canvas.drawText(arrDate[1],
                                                midX,
                                                nEndY - widely + 50,
                                                pBotBoldTxt);
                                    }
                                }
                            } else {
                                if (customDateType == CustomDate.TODAY) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", "^", "yyyyMMddHH", datelist.get(i));
                                    canvas.drawText(arrDate[1] + ":00",
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                                } else if (customDateType == CustomDate.WEEK) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMMdd", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2],
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                                } else if (customDateType == CustomDate.MONTH) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMMdd", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2],
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                                } else if (customDateType == CustomDate.YEAR) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01, ManagerUtil.ShowFormatPosition.MINUTE, true, "/", null, "yyyyMM", datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    canvas.drawText(arrSplitDate[1],
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                                }
                            }
                        } else if (datelist.get(i).length() == 10) {
                            // 1일 데이터
                            String date = "";
                            if (isToday) {
                                date = datelist.get(i).substring(8, 10) + ":00";
                            } else {
                                date = datelist.get(i).substring(8, 10);
                            }
                            canvas.drawText(date, midX, nEndY - START_BOTTOM_Y_SPACE / 3, pBotBoldTxt);
                        } else if (datelist.get(i).length() == 8) {
                            // 1주일 데이터 && 한달 데이터
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/" + datelist.get(i).substring(6, 8),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE / 3,
                                    pBotBoldTxt);
                        } else if (datelist.get(i).length() == 6) {
                            // 1년 데이터
                            canvas.drawText(datelist.get(i).substring(2, 4) + "/" + datelist.get(i).substring(4, 6),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE / 3,
                                    pBotBoldTxt);
                        } else if (datelist.get(i).length() == 14 && isBottomWidely) {
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/" + datelist.get(i).substring(6, 8),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE + 30,
                                    pBotBoldTxt);
                            canvas.drawText(datelist.get(i).substring(8, 10) + ":" + datelist.get(i).substring(10, 12),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE + 80,
                                    pBotTxt);
                        } else if (datelist.get(i).length() == 14 && !isBottomWidely) {
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/" + datelist.get(i).substring(6, 8),
                                    midX,
                                    nEndY - START_BOTTOM_Y_SPACE / 3,
                                    pBotBoldTxt);
                        } else {
                            canvas.drawText(i + 1 + "", midX, nEndY - START_BOTTOM_Y_SPACE / 3, pBotBoldTxt);
                        }
                    }
                }

            }
        }

        canvas.drawRect(rLeftBg, pWhite);
        canvas.drawRect(rRightBg, pWhite);

        //그래프 윤곽
        canvas.drawRect(rLeftOutLine, outLine);
        canvas.drawRect(rBotOutLine, outLine);
        canvas.drawRect(rRightOutLine, outLine);

        // y절편 -랑 숫자 적기
        for (int i = 0; i < nShowYValue; i++) {
            pYValue.setTextAlign(Align.RIGHT);
            int showY = nMinY1 + ((nMaxY1 - nMinY1) / nShowYValue) * (nShowYValue - i);
            float y = fGraphStartY + (((getGraphBottom() - fGraphStartY) / nShowYValue) * i);
            canvas.drawText(showY + "", fGraphStartX - 5, y + 4, pYValue);
        }

        // y절편 -랑 숫자 적기
        for (int i = 0; i < nShowYValue; i++) {
            pYValue.setTextAlign(Align.LEFT);
            int showY = nMinY2 + ((nMaxY2 - nMinY2) / nShowYValue) * (nShowYValue - i);
            float y = fGraphStartY + (((getGraphBottom() - fGraphStartY) / nShowYValue) * i);
            canvas.drawText(showY + "", this.fGraphEndX, y + 4, pYValue);
        }

        if (fGraphPercent < 100) {
            fGraphPercent = fGraphPercent + 1;
            invalidate();
        }

        if (fSpeedX > 0.1f && !isMove) {
            fSpeedX = fSpeedX / 1.1f;
            fModX = fModX + fSpeedX;
            if (fModX < 0) {
                fModX = 0;
            } else if (fModX > fModMaxX) {
                fModX = fModMaxX;
            }
            lineLocation(fCurrentLineX, 0);
            invalidate();
        }
        if (fSpeedX < -0.1f && !isMove) {
            fSpeedX = fSpeedX / 1.1f;
            fModX = fModX + fSpeedX;
            if (fModX < 0) {
                fModX = 0;
            } else if (fModX > fModMaxX) {
                fModX = fModMaxX;
            }
            lineLocation(fCurrentLineX, 0);
            invalidate();
        } else {
            //			fSpeedX = 0;
        }

    }

    private void setBubbleIntData(Canvas canvas) {
        for (int idx = 0, len = graphIntegerDataList.size(); idx < len; idx++) {
            GraphIntegerData gd = graphIntegerDataList.get(idx);
            ArrayList<Integer> list = gd.getList();
            float x1 = getIndexToMiddle(nLineX);
            float y1 = getIntegerIndexToTop(nLineX, list, idx);
            int data = list.get(nLineX);
            if (data > 0) {
                setBubbleData(canvas, gd, data, x1, y1);
            }
        }
    }

    private void setBubbleDoubleData(Canvas canvas) {
        for (int idx = 0, len = graphDoubleDataList.size(); idx < len; idx++) {
            GraphDoubleData gd = graphDoubleDataList.get(idx);
            ArrayList<Double> list = gd.getList();
            float x1 = getIndexToMiddle(nLineX);
            float y1 = getDoubleIndexToTop(nLineX, list, idx);
            Double data = list.get(nLineX);
            if (data > 0) {
                setBubbleData(canvas, gd, data, x1, y1);
            }
        }
    }

    public void setDataUnit(String weightType) {
        this.weightType = weightType;
    }

    private void setBubbleData(Canvas canvas, GraphBaseData gd, Object data, float x1, float y1) {
        if (dataType.equals(DATA_TYPE_DOUBLE)) {
            data = (Double) data;
        } else {
            data = (Integer) data;
        }
        canvas.drawBitmap(gd.getBubble(), x1 - gd.getBubble().getWidth() / 2, y1 - gd.getBubble()
                .getHeight()
                - gd.getDefIcon()
                .getHeight()
                / 3, null);
        Paint pTxt = new Paint();
        pTxt.setTextAlign(Align.CENTER);
        pTxt.setColor(Color.WHITE);
        if (gd.getBubble().getHeight() > 130) {
            if (PhoneUtil.dipInfo(mContext) > 2) {
                pTxt.setTextSize(gd.getBubble().getHeight() / 3f);
            } else {
                pTxt.setTextSize(gd.getBubble().getHeight() / 4.5f);
            }
        } else {
            pTxt.setTextSize(gd.getBubble().getHeight() / 2f);
        }
        pTxt.setAntiAlias(true);
        pTxt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        String strData = String.valueOf(data);
        if (dataType.equals(DATA_TYPE_DOUBLE)) {
            if ("kg".equals(weightType)) {
                strData = String.format("%.2f", data);
                if (strData.contains(",")) {
                    strData = strData.replace(",", ".");
                }
            } else {
                strData = String.format("%.1f", data);
                if (strData.contains(",")) {
                    strData = strData.replace(",", ".");
                }
            }
        }
        canvas.drawText(strData, x1, y1 - gd.getBubble().getHeight()
                / 2
                - gd.getDefIcon().getHeight()
                / 3
                + pTxt.getTextSize()
                / 4, pTxt);
    }

    /**
     * 내용 입력
     *
     * @param canvas
     */
    private void drawGraph(Canvas canvas) {
        // 글씨
        Paint pAxisLine = new Paint();
        pAxisLine.setColor(cText);
        // 표 라인 사이즈
        Paint pline = new Paint();
        pline.setStrokeWidth(10);
        pline.setAntiAlias(true);
        if (dataType.equals(DATA_TYPE_DOUBLE)) {

            for (int idx = 0, len = graphDoubleDataList.size(); idx < len; idx++) {
                GraphDoubleData gd = graphDoubleDataList.get(idx);
                ArrayList<Double> list = gd.getList();
                /**
                 * 라인 색상 세팅
                 */
                if (idx == 0) {
                    pline.setColor(gd.getLineColor());
                } else if (idx == 1) {
                    pline.setColor(gd.getLineColor());
                } else if (idx == 2) {
                    pline.setColor(gd.getLineColor());
                } else if (idx == 3) {
                    pline.setColor(gd.getLineColor());
                } else {
                    pline.setColor(gd.getLineColor());
                }
                if (nGraphShape == LINE) { // 라인형 그래프 그리기
                    // 표 그리기
                    for (int i = 0; i < list.size(); i++) {
                        if (list.size() <= i) {
                            break;
                        }
                        float x1 = getIndexToMiddle(i);
                        float y1 = getDoubleIndexToTop(i, list, idx);
                        if (x1 > nEndX) {
                            break;
                        } else {
                            if (i < list.size() - 1 && i < list.size() - 1) {
                                float x2 = getIndexToMiddle(i + 1);
                                float y2 = getDoubleIndexToTop(i + 1, list, idx);
                                if (x2 < 0) {
                                    // 그리지 않음
                                } else {
                                    if (list.get(i) > 0) {
                                        if ((list.size() - 1) != i && list.get(i + 1) > 0) {
                                            final Path path = new Path();
                                            path.moveTo(x1, y1);
                                            if (list.get(i) > list.get(i + 1)) {
                                                path.cubicTo(x1 + 40, y1, x2 - 40, y2, x2, y2);
                                            } else if (list.get(i) < list.get(i + 1)) {
                                                path.cubicTo(x1 + 40, y1, x2 - 40, y2, x2, y2);
                                            } else if (list.get(i) == list.get((i + 1))) {
                                                path.quadTo(((x1 + x2) / 2) - 10, y1, x2, y2);
                                            }
                                            canvas.drawPath(path, pline);
//                                            canvas.drawLine(x1, y1, x2, y2, pline);
                                        }
                                    }
                                }
                            }
                        }
                        // 1~xValue까지
                        // canvas.drawLine(getIndexToMiddle(i), endY,
                        // getIndexToMiddle(i), endY - AXIS_LINE, pAxisLine);
                    }
                    // Bitmap찍기
                    for (int i = 0; i < list.size(); i++) {
                        if (list.size() <= i) {
                            break;
                        }
                        float x1 = getIndexToMiddle(i);
                        float y1 = getDoubleIndexToTop(i, list, idx);
                        if (x1 < 0) {
                            // 그리지 않음
                        } else if (x1 > nEndX) {
                            break;
                        } else {
                            if (list.get(i) > 0) {
                                if (nLineX == i) {
                                    canvas.drawBitmap(gd.getSelIcon(),
                                            x1 - gd.getSelIcon().getWidth() / 2,
                                            y1 - gd.getSelIcon().getHeight() / 2,
                                            null);
                                } else {
                                    canvas.drawBitmap(gd.getDefIcon(),
                                            x1 - gd.getDefIcon().getWidth() / 2,
                                            y1 - gd.getDefIcon().getHeight() / 2,
                                            null);
                                }
                            }
                        }
                    }
                }
            }
        } else if (dataType.equals(DATA_TYPE_INT)) {
            for (int idx = 0, len = graphIntegerDataList.size(); idx < len; idx++) {
                GraphIntegerData gd = graphIntegerDataList.get(idx);
                ArrayList<Integer> list = gd.getList();
                /**
                 * 라인 색상 세팅
                 */
                if (idx == 0) {
                    pline.setColor(gd.getLineColor());
                } else if (idx == 1) {
                    pline.setColor(gd.getLineColor());
                } else if (idx == 2) {
                    pline.setColor(gd.getLineColor());
                } else if (idx == 3) {
                    pline.setColor(gd.getLineColor());
                } else {
                    pline.setColor(gd.getLineColor());
                }
                if (nGraphShape == LINE) { // 라인형 그래프 그리기
                    // 표 그리기
                    for (int i = 0; i < list.size(); i++) {
                        if (list.size() <= i) {
                            break;
                        }
                        float x1 = getIndexToMiddle(i);
                        float y1 = getIntegerIndexToTop(i, list, idx);
                        if (x1 > nEndX) {
                            break;
                        } else {
                            if (i < list.size() - 1 && i < list.size() - 1) {
                                float x2 = getIndexToMiddle(i + 1);
                                float y2 = getIntegerIndexToTop(i + 1, list, idx);
                                if (x2 < 0) {
                                    // 그리지 않음
                                } else {
                                    if (list.get(i) > 0) {
                                        if ((list.size() - 1) != i && list.get(i + 1) > 0) {
                                            final Path path = new Path();
                                            path.moveTo(x1, y1);
                                            if (list.get(i) > list.get(i + 1)) {
                                                path.cubicTo(x1 + 40, y1, x2 - 40, y2, x2, y2);
                                            } else if (list.get(i) < list.get(i + 1)) {
                                                path.cubicTo(x1 + 40, y1, x2 - 40, y2, x2, y2);
                                            } else if (list.get(i) == list.get((i + 1))) {
                                                path.quadTo(((x1 + x2) / 2) - 10, y1, x2, y2);
                                            }
                                            canvas.drawPath(path, pline);
//                                            canvas.drawLine(x1, y1, x2, y2, pline);
                                        }
                                    }
                                }
                            }
                        }
                        // 1~xValue까지
                        // canvas.drawLine(getIndexToMiddle(i), endY,
                        // getIndexToMiddle(i), endY - AXIS_LINE, pAxisLine);
                    }
                    // Bitmap찍기
                    for (int i = 0; i < list.size(); i++) {
                        if (list.size() <= i) {
                            break;
                        }
                        float x1 = getIndexToMiddle(i);
                        float y1 = getIntegerIndexToTop(i, list, idx);
                        if (x1 < 0) {
                            // 그리지 않음
                        } else if (x1 > nEndX) {
                            break;
                        } else {
                            if (list.get(i) > 0) {
                                if (nLineX == i) {
                                    canvas.drawBitmap(gd.getSelIcon(),
                                            x1 - gd.getSelIcon().getWidth() / 2,
                                            y1 - gd.getSelIcon().getHeight() / 2,
                                            null);
                                } else {
                                    canvas.drawBitmap(gd.getDefIcon(),
                                            x1 - gd.getDefIcon().getWidth() / 2,
                                            y1 - gd.getDefIcon().getHeight() / 2,
                                            null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 내용 입력
     *
     * @param event
     * @return
     */
    public int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));

        return (int) (Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isInitialize == false) {
            return false;
        }

        int actionmask = event.getActionMasked();

        if (event.getPointerCount() > 1) {

            if (event.getPointerCount() == 2) {
                int action = event.getAction();
                int pureaction = action & MotionEvent.ACTION_MASK;
                if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                    nBaseZoom = 0;
                    nBaseDist = getDistance(event);
                } else {
                    int delta = (getDistance(event) - nBaseDist) / nPixToZoom;
                    if (delta < nBaseZoom) {
                        addItem();
                        nBaseZoom = delta;
                    } else if (delta > nBaseZoom) {
                        subItem();
                        nBaseZoom = delta;
                    }
                }

                return true;
            }

        } else {
            // 다운 이벤트
            //            if (actionmask == MotionEvent.ACTION_DOWN) {
            //                beforeLineX = lineX;
            //                lineLocation(event.getX(), event.getY());
            //            }
            //
            //            if (actionmask == MotionEvent.ACTION_UP) {
            //                lineLocation(event.getX(), event.getY());
            //                if (beforeLineX != lineX) {
            //                    graphRotateAni();
            //                }
            //            }

            if (actionmask == MotionEvent.ACTION_DOWN) {
                //                beforeLineX = lineX;
                //                lineLocation(event.getX(), event.getY());
                fTouchDown = event.getX();
                fTouchX = event.getX();
                speedList.clear();
                isMove = false;
            }

            if (actionmask == MotionEvent.ACTION_UP) {
                if (isMove) {
                    isMove = false;
                    invalidate();
                } else {
                    if (Math.abs(fTouchDown - event.getX()) < 20) {
                        fCurrentLineX = event.getX();
                        lineLocation(event.getX(), event.getY());
                        if (nBeforeLineX != nLineX) {
                            graphRotateAni();
                        }
                    }
                }
            }
        }

        //        if (actionmask == MotionEvent.ACTION_MOVE) {
        //            lineLocation(event.getX(), event.getY());
        //        }

        if (actionmask == MotionEvent.ACTION_MOVE) {
            lineLocation(fCurrentLineX, event.getY());
            if (isMove) {
                fModX = fModX + (fTouchX - event.getX()) * 1.5f;
                speedList.add((fTouchX - event.getX()) * 2f);
                if (speedList.size() > AVR_DATA) {
                    speedList.remove(0);
                }
                float temp = 0;
                for (Float f : speedList) {
                    temp = temp + f;
                }
                fSpeedX = temp / speedList.size();
                fTouchX = event.getX();

                if (fModX < 0) {
                    fModX = 0;
                } else if (fModX > fModMaxX) {
                    fModX = fModMaxX;
                }
                invalidate();
            } else {
                if (Math.abs(fTouchDown - event.getX()) > 20) {
                    isMove = true;
                }
            }

        }
        return true;
    }

    /**
     * 내용 입력
     */
    private void graphRotateAni() {
        Message msg = new Message();
        msg.what = MESSAGE_ROTATE_ANIMATION;
        msg.getData().putInt(ManagerConstants.Graph.INDEX, nLineX);
        hCurrentActivitiy.sendMessage(msg);
    }

    /**
     * 내용 입력
     */
    private void getLineDataSendMessage() {
        Message msg = new Message();
        msg.what = MESSAGE_WHAT_RESULT_TWO;
        msg.getData().putInt(ManagerConstants.Graph.INDEX, nLineX);

        hCurrentActivitiy.sendMessage(msg);
    }

    /**
     * 내용 입력
     *
     * @param x
     * @param y
     */
    private void lineLocation(float x, float y) {
        for (int i = 0; i < nDataAmount; i++) {
            float left = getIndexToLeft(i) - fGraphSpacehalf;
            float right = getIndexToRight(i) + fGraphSpacehalf;

            if (x > left && x < right) { // 어떤 라인인지
                if (nLineX != i) {
                    if (x > nEndX) { // 데이터 양보다 표시되는 양이 적을경우 화면 넘게 끌시 다음데이터로 연동되서
                        nLineX = i - 1;
                    } else {
                        nLineX = i;
                    }

                    getLineDataSendMessage();
                    invalidate();
                }

                break;
            }
        }
    }

    /**
     * 그래프 인덱스의 Left값
     *
     * @param index
     * @return
     */
    private float getIndexToLeft(int index) {
        return (fGraphWidth * index) + (fGraphSpace * index) + fGraphStartX - fModX;
    }

    /**
     * 그래프 인덱스의 Right값
     *
     * @param index
     * @return
     */
    private float getIndexToRight(int index) {
        return (fGraphWidth * (index + 1)) + (fGraphSpace * index) + fGraphStartX - fModX;
    }

    /**
     * 내용 입력
     *
     * @param index
     * @return
     */
    private float getIndexToMiddle(int index) {
        return (fGraphWidth * (index + 0.5f)) + (fGraphSpace * index) + fGraphStartX - fModX;
    }

    /**
     * 내용 입력
     *
     * @return
     */
    private float getGraphBottom() {
        return fGraphEndY;
    }

    /**
     * Index
     *
     * @param index
     * @param list
     * @return
     */
    private float getDoubleIndexToTop(int index, ArrayList<Double> list, int yIndex) {
        double value = list.get(index);
        if (yIndex == 0) {
            if (value >= nMaxY1) {
                return getGraphBottom() - (getOneToPixel() * 100);
            } else if (value <= nMinY1) {
                return getGraphBottom();
            } else {
                return (float) (getGraphBottom() - (getOneToPixel() * ((value - nMinY1) / ((nMaxY1 - nMinY1) * 1.0f)) * 100));
            }
        } else {
            if (value >= nMaxY2) {
                return getGraphBottom() - (getOneToPixel() * 100);
            } else if (value <= nMinY2) {
                return getGraphBottom();
            } else {
                return (float) (getGraphBottom() - (getOneToPixel() * ((value - nMinY2) / ((nMaxY2 - nMinY2) * 1.0f)) * 100));
            }
        }
    }

    /**
     * Index
     *
     * @param index
     * @param list
     * @return
     */
    private float getIntegerIndexToTop(int index, ArrayList<Integer> list, int yIndex) {
        if (yIndex == 0) {
            int value = list.get(index);
            if (value >= nMaxY1) {
                return getGraphBottom() - (getOneToPixel() * 100);
            } else if (value <= nMinY1) {
                return getGraphBottom();
            } else {
                return getGraphBottom() - (getOneToPixel() * ((value - nMinY1) / ((nMaxY1 - nMinY1) * 1.0f)) * 100);
            }
        } else {
            int value = list.get(index);
            if (value >= nMaxY2) {
                return getGraphBottom() - (getOneToPixel() * 100);
            } else if (value <= nMinY2) {
                return getGraphBottom();
            } else {
                return getGraphBottom() - (getOneToPixel() * ((value - nMinY2) / ((nMaxY2 - nMinY2) * 1.0f)) * 100);
            }
        }
    }

    //퍼센트 비중 없이 Top높이 구함
    private float getIndexToTop(int value, int yIndex) {
        if (yIndex == 0) {
            if (value >= nMaxY1) {
                return getGraphBottom() - ((getGraphBottom() - fGraphStartY));
            } else if (value <= nMinY1) {
                return getGraphBottom();
            } else {
                return getGraphBottom() - ((getGraphBottom() - fGraphStartY) * ((value - nMinY1) / ((nMaxY1 - nMinY1) * 1.0f)));
            }
        } else {
            if (value >= nMaxY2) {
                return getGraphBottom() - ((getGraphBottom() - fGraphStartY));
            } else if (value <= nMinY2) {
                return getGraphBottom();
            } else {
                return getGraphBottom() - ((getGraphBottom() - fGraphStartY) * ((value - nMinY2) / ((nMaxY2 - nMinY2) * 1.0f)));
            }
        }

    }

    /**
     * 현재 그래프 퍼센테이지
     *
     * @return
     */
    private float getOneToPixel() {
        return (getGraphBottom() - fGraphStartY) / (100 * (100 / fGraphPercent));
    }

}
