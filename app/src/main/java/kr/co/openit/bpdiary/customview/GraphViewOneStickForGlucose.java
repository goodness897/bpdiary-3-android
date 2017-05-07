/**
 * (주)오픈잇 | http://www.openit.co.kr
 * Copyright (c)2006-2014, openit Inc.
 * All rights reserved.
 */
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PhoneUtil;

/**
 * 그래프 유틸리티
 *
 * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
 * @created 2014. 7. 9.
 */
public class GraphViewOneStickForGlucose extends View {

    private int widely = 0;

    private ManagerUtil.Localization localization = ManagerUtil.Localization.TYPE_01;

    private int dipInfo;

    /**
     * 그래프 데이터
     *
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @created 2013. 1. 29.
     */
    private class GraphDoubleData {

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

        public String getName() {
            return this.strName;
        }

        public int getLineColor() {
            return this.nLineColor;
        }

        public Bitmap getDefIcon() {
            return this.bmpDefIcon;
        }

        public Bitmap getSelIcon() {
            return this.bmpSelIcon;
        }

        public Bitmap getBubble() {
            return this.bmpBubble;
        }
    }

    /**
     * 혈당그래프 데이터
     *
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @created 2013. 1. 29.
     */
    private class GraphIntegerData {

        private final List<Map<String, String>> dataList;

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

        // 기본 아이콘 이미지1
        private final Bitmap bmpDefIcon1;

        // 선택 아이콘 이미지2
        private final Bitmap bmpSelIcon1;

        // 값표시 이미지3
        private final Bitmap bmpBubble1;

        public GraphIntegerData(Context context,
                                String strName,
                                List<Map<String, String>> dataList,
                                ArrayList<String> dataDateList,
                                int nLineColor,
                                int bmpDefIcon,
                                int bmpSelIcon,
                                int bmpBubble,
                                int bmpDefIcon1,
                                int bmpSelIcon1,
                                int bmpBubble1) {
            this.dataList = dataList;
            this.dataDateList = dataDateList;
            this.strName = strName;
            this.nLineColor = nLineColor;
            this.bmpDefIcon = BitmapFactory.decodeResource(context.getResources(), bmpDefIcon);
            // this.defIcon = defIcon;
            this.bmpSelIcon = BitmapFactory.decodeResource(context.getResources(), bmpSelIcon);
            this.bmpBubble = BitmapFactory.decodeResource(context.getResources(), bmpBubble);
            this.bmpDefIcon1 = BitmapFactory.decodeResource(context.getResources(), bmpDefIcon1);
            // this.defIcon = defIcon;
            this.bmpSelIcon1 = BitmapFactory.decodeResource(context.getResources(), bmpSelIcon1);
            this.bmpBubble1 = BitmapFactory.decodeResource(context.getResources(), bmpBubble1);
        }

        /**
         * list getter
         *
         * @return list
         */
        public List<Map<String, String>> getList() {
            return dataList;
        }

        public ArrayList<String> getDateList() {
            return dataDateList;
        }

        public String getName() {
            return this.strName;
        }

        public int getLineColor() {
            return this.nLineColor;
        }

        public Bitmap getDefIcon() {
            return this.bmpDefIcon;
        }

        public Bitmap getSelIcon() {
            return this.bmpSelIcon;
        }

        public Bitmap getBubble() {
            return this.bmpBubble;
        }

        public Bitmap getDefIcon1() {
            return this.bmpDefIcon1;
        }

        public Bitmap getSelIcon1() {
            return this.bmpSelIcon1;
        }

        public Bitmap getBubble1() {
            return this.bmpBubble1;
        }
    }

    private class NormalRange {

        private final RectF rRange;

        private final Paint p;

        public NormalRange(int lowValue, int hightValue, int color) {
            rRange = new RectF(fGraphStartX, getIndexToTop(hightValue), fGraphEndX, getIndexToTop(lowValue));
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
    public static final int BAR = 1;

    public static final int LINE = 2;

    //
    // public static final int LINEFILL = 4;
    //
    // public static final int LINEANDFILL = 8;
    /**
     * 현재 그래프 퍼센트
     */
    public static float rGraphPercent = 1.0f;

    /**
     * 메세지 리턴 타입
     */
    public static final int MESSAGE_WHAT_RESULT_TWO = 2;

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
    // private static final int START_LEFT_X_SPACE = 50;
    private static final int START_LEFT_X_SPACE = 120;

    /**
     * 그래프 시작 RightX빈칸
     */
    // private static final int START_RIGHT_X_SPACE = 35;
    private static final int START_RIGHT_X_SPACE = 0;

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
     * y의 맥스 값
     */
    private int nMaxY;

    /**
     * y의 Min 값
     */
    private int nMinY;

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

    // /**
    // * 처음 2터치 됐을때 거리
    // */
    // private int nBaseDist;
    //
    // /**
    // * 현재 거리 비율
    // */
    // private int nBaseZoom;
    //
    // /**
    // * 몇 픽셀당 비율 높일것인지
    // */
    // private final int nPixToZoom = 30;
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
    //    private final int cIndicatorLine = Color.rgb(150, 150, 150);
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

    private final int selectTextColor = Color.rgb(120, 114, 108);

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
    float currentLineX;

    /**
     * 터치한 X좌표 (이전 move X좌표 와 비교하여 속도 조절)
     */
    float touchX;

    /**
     * 터치한 X좌표 (Move 되었는지 체크하기 위한 값)
     */
    float touchDown;

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
    public GraphViewOneStickForGlucose(Context context, Handler h) {
        super(context);
        this.mContext = context;
        hCurrentActivitiy = h;
        isInitialize = false;
        dipInfo = Math.round(PhoneUtil.dipInfo(mContext));
        Log.i("sgim", "dipInfo = " + dipInfo);
    }

    /**
     * 혈당 한줄에 두종류 그래프 그리기 추가
     *
     * @param list
     * @param name
     */
    public void addObjectList(List<Map<String, String>> list,
                              String key,
                              String name,
                              String date,
                              String meal,
                              int lineColor,
                              int defIcon,
                              int selIcon,
                              int bubble,
                              int defIcon1,
                              int selIcon1,
                              int bubble1) {
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
            ArrayList<String> arrMealList = new ArrayList<String>();
            for (int i = 0; i < nDataAmount; i++) {
                int data = strToInt(list.get(i).get(key));
                String strMeal = list.get(i).get(meal);
                String strAddDate = list.get(i).get(date);
                arrList.add(data);
                arrMealList.add(strMeal);
                arrDateList.add(strAddDate);
                if (data > 0) {
                    nFirstIdx = i;
                }
            }
            GraphIntegerData graphintegerdata = new GraphIntegerData(mContext,
                                                                     name,
                                                                     list,
                                                                     arrDateList,
                                                                     lineColor,
                                                                     defIcon,
                                                                     selIcon,
                                                                     bubble,
                                                                     defIcon1,
                                                                     selIcon1,
                                                                     bubble1);
            graphIntegerDataList.add(graphintegerdata);
        }
        calculate();
        getLineDataSendMessage();
        graphRotateAni();
        invalidate();
    }

    public void addRange(int lowValue, int highValue, int color) {
        rangeList.add(new NormalRange(lowValue, highValue, color));
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
     * @param graphType 그래프 타입
     * @param showXValue X에 표시될 양
     * @param yMax y값 최대
     * @param yMin y값 최소
     * @param showYValue y에 표시될 양
     * @param startX 그래프 시작X
     * @param startY 그래프 시작Y
     * @param endX 그래프 끝X
     * @param endY 그래프 끝Y
     */
    public void setInitialize(int graphType,
                              int showXValue,
                              int yMax,
                              int yMin,
                              int showYValue,
                              int startX,
                              int startY,
                              int endX,
                              int endY) {
        this.nGraphShape = graphType;
        this.nShowXValue = showXValue;
        this.nMaxY = yMax;
        this.nMinY = yMin;
        this.nShowYValue = showYValue;
        this.nStartX = startX;
        this.nStartY = startY;
        this.nEndX = endX;
        this.nEndY = endY;
        calculate();
        rGraphPercent = 1.0f;

        rLeftBg = new RectF(this.nStartX, this.nStartY, this.fGraphStartX, this.nEndY);
        rRightBg = new RectF(this.fGraphEndX, this.nStartY, this.nEndX, this.nEndY);
        rTopBg = new RectF(this.nStartX, this.nStartY, this.nEndX, this.fGraphStartY - 170);
        rBotBg = new RectF(this.nStartX, this.fGraphEndY, this.nEndX, this.nEndY);
        rLeftOutLine =
                     new RectF(this.fGraphStartX - 2, this.fGraphStartY - 170, this.fGraphStartX, this.fGraphEndY + 4);
        rBotOutLine = new RectF(0, this.fGraphEndY + 2, this.fGraphEndX, this.fGraphEndY + 4);
        //        rBotOutLine = new RectF(this.fGraphStartX, this.fGraphEndY + 2, this.fGraphEndX, this.fGraphEndY + 4);
        rRightOutLine = new RectF(this.fGraphEndX, this.fGraphStartY - 170, this.fGraphEndX + 2, this.fGraphEndY + 4);
        rGraphBg = new RectF(fGraphStartX, fGraphStartY - 170, fGraphEndX, fGraphEndY);
        // 아웃라인 Paint 초기화
        outLine = new Paint();
        outLine.setColor(cOutLine);
        // 흰부분 Paint
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
        switch (dipInfo) {
            case 0:
            case 1:
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
        if (PhoneUtil.dipInfo(mContext) > 2) {
            pBotTxt.setTextSize(START_LEFT_X_SPACE / 2.5f);
        } else {
            pBotTxt.setTextSize(START_LEFT_X_SPACE / 4.7f);
        }
        pBotTxt.setColor(cText);
        pBotTxt.setTextAlign(Align.CENTER);
        pBotTxt.setAntiAlias(true);
        // Y절편 텍스트
        pYValue = new Paint();
        pYValue.setColor(cText);
        if (PhoneUtil.dipInfo(mContext) > 2) {
            pYValue.setTextSize(START_LEFT_X_SPACE / 3f);
        } else {
            pYValue.setTextSize(START_LEFT_X_SPACE / 5f);
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
        widely = 0;
        float y = nEndY - START_BOTTOM_Y_SPACE;
        switch (dipInfo) {
            case 0:
            case 1:
            case 2:
                fGraphStartX = nStartX + START_LEFT_X_SPACE - 60;
                fGraphStartY = nStartY + START_TOP_Y_SPACE + 50;
                y += 50;
                widely = 40;
                break;
            case 3:
                fGraphStartX = nStartX + START_LEFT_X_SPACE;
                fGraphStartY = nStartY + START_TOP_Y_SPACE + 30;
                widely = 50;
                break;
            default:
                fGraphStartX = nStartX + START_LEFT_X_SPACE;
                fGraphStartY = nStartY + START_TOP_Y_SPACE + 30;
                widely = 55;
                break;
        }
        fGraphEndX = nEndX - START_RIGHT_X_SPACE;
        if (isBottomWidely) {
            fGraphEndY = y - widely;
        } else {
            fGraphEndY = y;
        }
        if (nDataAmount - nShowXValue > 0) {
            fModMaxX = (fGraphWidth * (nDataAmount - nShowXValue)) + (fGraphSpace * (nDataAmount - nShowXValue));
            if (!isLineX) {
                // fModX = fModMaxX;
                // nLineX = nDataAmount - 1;
                int data = nDataAmount - nShowXValue - nFirstIdx;
                if (data < 0) {
                    data = 0;
                }
                fModMaxX = (fGraphWidth * (data)) + (fGraphSpace * (data));
                if (nFirstIdx > nShowXValue) {
                    fModX =
                          (fGraphWidth * (nFirstIdx + 1 - nShowXValue)) + (fGraphSpace * (nFirstIdx + 1 - nShowXValue));
                } else {
                    //
                    fModX = 0;
                }
                nLineX = nFirstIdx;
                currentLineX = getIndexToMiddle(nLineX);
                isLineX = true;
            } else {
                // fModX = fModMaxX;
                if (nFirstIdx > nShowXValue) {
                    fModX =
                          (fGraphWidth * (nFirstIdx + 1 - nShowXValue)) + (fGraphSpace * (nFirstIdx + 1 - nShowXValue));
                } else {
                    //
                    fModX = 0;
                }
                // if(modX > modXMax){
                // modX = modXMax;
                // }
            }
        } else {
            fModMaxX = 0;
            fModX = 0;
            if (nDataAmount > 0) {
                if (!isLineX) {
                    // nLineX = nDataAmount - 1;
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
        rGraphPercent = 1.0f;
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

    /**
     * 내용 입력
     */
    public void addYMax() {
        this.nMaxY += 30;
        invalidate();
    }

    /**
     * 내용 입력
     */
    public void subYMax() {
        this.nMaxY -= 30;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInitialize) {
            return;
        }
        // 그래프 배경 색상
        canvas.drawRect(rGraphBg, pBg);
        // 범위 그리기
        for (NormalRange range : rangeList) {
            canvas.drawRect(range.getRange(), range.getPaint());
        }
        for (GoalRange range : goalList) {
            canvas.drawLine(fGraphStartX,
                            getIndexToTop(range.getValue()),
                            fGraphEndX,
                            getIndexToTop(range.getValue()),
                            range.getPaint());
        }
        // 선택라인 영역 그리기
        if (nDataAmount > 0) {
            float selectX = getIndexToMiddle(nLineX);
            float y = fGraphStartY;
            canvas.drawLine(selectX, y - 100, selectX, getGraphBottom() + 1, pIndicator);
        }
        // 그래프 그리기
        drawGraph(canvas);
        // 선택 영역 숫자...
        if (!isBubbleEnable) {
            if (nDataAmount > 0) {
                if (dataType.equals(DATA_TYPE_DOUBLE)) {
                    for (int idx = 0, len = graphDoubleDataList.size(); idx < len; idx++) {
                        GraphDoubleData gd = graphDoubleDataList.get(idx);
                        ArrayList<Double> list = gd.getList();
                        float x1 = getIndexToMiddle(nLineX);
                        float y1 = getDoubleIndexToTop(nLineX, list);
                        if (list.get(nLineX) > 0) {
                            canvas.drawBitmap(gd.getBubble(),
                                              x1 - gd.getBubble().getWidth() / 2,
                                              y1 - gd.getBubble().getHeight() - gd.getDefIcon().getHeight() / 3,
                                              null);
                            // canvas.drawLine(getIndexToTop(lineX, list),
                            // graphStartY, getIndexToMiddle(lineX), endY + 1,
                            // p1);
                            String data = list.get(nLineX) + "";
                            Paint pTxt = new Paint();
                            pTxt.setTextAlign(Align.CENTER);
                            pTxt.setColor(Color.WHITE);
                            if (gd.getBubble().getHeight() > 130) {
                                if (PhoneUtil.dipInfo(mContext) > 2) {
                                    if (data.length() < 4) {
                                        pTxt.setTextSize(gd.getBubble().getHeight() / 3f);
                                    } else {
                                        pTxt.setTextSize(gd.getBubble().getHeight() / 4f);
                                    }
                                } else {
                                    pTxt.setTextSize(gd.getBubble().getHeight() / 4.5f);
                                }
                            } else {
                                pTxt.setTextSize(gd.getBubble().getHeight() / 4f);
                            }
                            pTxt.setAntiAlias(true);
                            pTxt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            // if(dataType == TYPE_TIME){
                            // if(list.get(nLineX)/60 > 0){
                            // if(list.get(nLineX)/60 >= 60){
                            // data = (list.get(nLineX)/60)/60+"h";
                            // }else{
                            // data = list.get(nLineX)/60+"m";
                            // }
                            // }else{
                            // data = list.get(nLineX)+"s";
                            // }
                            // }else{
                            // }
                            canvas.drawText(data,
                                            x1,
                                            y1 - gd.getBubble().getHeight() / 2
                                                - gd.getDefIcon().getHeight() / 3
                                                + pTxt.getTextSize() / 4,
                                            pTxt);
                        }
                    }
                } else if (dataType.equals(DATA_TYPE_INT)) {
                    for (int idx = 0, len = graphIntegerDataList.size(); idx < len; idx++) {
                        GraphIntegerData gd = graphIntegerDataList.get(idx);
                        List<Map<String, String>> list = gd.getList();
                        ArrayList<Float> dataList = new ArrayList<Float>();
                        for (int i = 0; i < list.size(); i++) {
                            dataList.add(Float.parseFloat(list.get(i)
                                                              .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE)));
                        }
                        float x1 = getIndexToMiddle(nLineX);
                        float y1 = getIntegerIndexToTop(nLineX, dataList);
                        if (dataList.get(nLineX) > 0) {
                            canvas.drawBitmap(gd.getBubble(),
                                              x1 - gd.getBubble().getWidth() / 2,
                                              y1 - gd.getBubble().getHeight() - gd.getDefIcon().getHeight() / 3,
                                              null);
                            // canvas.drawLine(getIndexToTop(lineX, list),
                            // graphStartY, getIndexToMiddle(lineX), endY + 1,
                            // p1);
                            Paint pTxt = new Paint();
                            pTxt.setTextAlign(Align.CENTER);
                            pTxt.setColor(Color.WHITE);
                            if (gd.getBubble().getHeight() > 130) {
                                switch (dipInfo) {
                                    case 0:
                                    case 1:
                                    case 2:
                                        pTxt.setTextSize(gd.getBubble().getHeight() / 3.5f);
                                        break;
                                    default:
                                        pTxt.setTextSize(gd.getBubble().getHeight() / 4.5f);
                                        break;
                                }
                            } else {
                                pTxt.setTextSize(gd.getBubble().getHeight() / 2f);
                            }
                            pTxt.setAntiAlias(true);
                            pTxt.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            String data = "";
                            // if(dataType == TYPE_TIME){
                            // if(list.get(nLineX)/60 > 0){
                            // if(list.get(nLineX)/60 >= 60){
                            // data = (list.get(nLineX)/60)/60+"h";
                            // }else{
                            // data = list.get(nLineX)/60+"m";
                            // }
                            // }else{
                            // data = list.get(nLineX)+"s";
                            // }
                            // }else{
                            data = list.get(nLineX) + "";
                            // }
                            canvas.drawText(data,
                                            x1,
                                            y1 - gd.getBubble().getHeight() / 2
                                                - gd.getDefIcon().getHeight() / 3
                                                + pTxt.getTextSize() / 4,
                                            pTxt);
                        }
                    }
                }
            }
        }
        // 그래프 화이트 영역
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
                    float selectX = getIndexToMiddle(nLineX);
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
                                canvas.drawText(datelist.get(i).substring(4, 6) + "/"
                                                + datelist.get(i).substring(6, 8),
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

                            if (midX == selectX) {
                                pBotBoldTxt.setColor(selectTextColor);
                                pBotTxt.setColor(selectTextColor);
                            } else {
                                pBotBoldTxt.setColor(cText);
                                pBotTxt.setColor(cText);
                            }

                            String[] arrDate =
                                             ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01,
                                                                          ManagerUtil.ShowFormatPosition.MINUTE,
                                                                          true,
                                                                          "/",
                                                                          ":",
                                                                          "yyyyMMddHHmmss",
                                                                          datelist.get(i));
                            Log.i("sgim", "" + datelist.get(i));
                            float y = 0;
                            switch (dipInfo) {
                                case 0:
                                case 1:
                                case 2:
                                    y = nEndY - (START_BOTTOM_Y_SPACE - 80) / 3;
                                    break;
                                default:
                                    y = nEndY - START_BOTTOM_Y_SPACE / 3;
                                    break;
                            }
                            if (customDateType == CustomDate.TODAY) {
                                canvas.drawText(arrDate[1], midX, y, pBotBoldTxt);
                            } else if (customDateType == CustomDate.WEEK) {
                                canvas.drawText(arrDate[0], midX, y, pBotBoldTxt);
                            } else if (customDateType == CustomDate.MONTH) {
                                canvas.drawText(arrDate[0], midX, y, pBotBoldTxt);
                            } else if (customDateType == CustomDate.YEAR) {
                                canvas.drawText(arrDate[0], midX, y, pBotBoldTxt);
                            }
                        } else if (datelist.get(i).length() == 10) {
                            // 1일 데이터
                            canvas.drawText(datelist.get(i).substring(8, 10),
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                        } else if (datelist.get(i).length() == 8) {
                            // 1주일 데이터 && 한달 데이터
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/"
                                            + datelist.get(i).substring(6, 8),
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                        } else if (datelist.get(i).length() == 6) {
                            // 1년 데이터
                            canvas.drawText(datelist.get(i).substring(2, 4) + "/"
                                            + datelist.get(i).substring(4, 6),
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                        } else if (datelist.get(i).length() == 14 && isBottomWidely) {
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/"
                                            + datelist.get(i).substring(6, 8),
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE + 30,
                                            pBotBoldTxt);
                            canvas.drawText(datelist.get(i).substring(8, 10) + ":"
                                            + datelist.get(i).substring(10, 12),
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
                List<Map<String, String>> list = gd.getList();
                ArrayList<Float> dataList = new ArrayList<Float>();
                for (int i = 0; i < list.size(); i++) {
                    dataList.add(Float.parseFloat(list.get(i)
                                                      .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE)));
                }
                ArrayList<String> datelist = gd.getDateList();
                for (int i = 0; i < list.size(); i++) {
                    float midX = getIndexToMiddle(i);
                    float selectX = getIndexToMiddle(nLineX);
                    if (midX < 0) {
                        // 그리지 않음
                    } else if (midX > nEndX) {
                        // 그리지 않음
                        break;
                    } else {
                        datelist.get(i).length();
                        if (isStay && !isCustom) {
                            if (isBottomWidely) {
                                canvas.drawText(datelist.get(i).substring(4, 6) + "/"
                                                + datelist.get(i).substring(6, 8),
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

                            if (midX == selectX) {
                                pBotBoldTxt.setColor(selectTextColor);
                                pBotTxt.setColor(selectTextColor);
                            } else {
                                pBotBoldTxt.setColor(cText);
                                pBotTxt.setColor(cText);
                            }

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
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization,
                                                                                    ManagerUtil.ShowFormatPosition.MINUTE,
                                                                                    true,
                                                                                    "/",
                                                                                    "^",
                                                                                    "yyyyMMddHH",
                                                                                    datelist.get(i));
                                    canvas.drawText(arrDate[1] + ":00", midX, y - widely, pBotBoldTxt);
                                } else if (customDateType == CustomDate.WEEK) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization,
                                                                                    ManagerUtil.ShowFormatPosition.MINUTE,
                                                                                    true,
                                                                                    "/",
                                                                                    null,
                                                                                    "yyyyMMdd",
                                                                                    datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrSplitDate[1] + "/"
                                                        + arrSplitDate[2],
                                                        midX,
                                                        y - widely,
                                                        pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrSplitDate[0] + "/"
                                                        + arrSplitDate[1],
                                                        midX,
                                                        y - widely,
                                                        pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrSplitDate[0] + "/"
                                                        + arrSplitDate[1],
                                                        midX,
                                                        y - widely,
                                                        pBotBoldTxt);
                                    }
                                } else if (customDateType == CustomDate.MONTH) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization,
                                                                                    ManagerUtil.ShowFormatPosition.MINUTE,
                                                                                    true,
                                                                                    "/",
                                                                                    null,
                                                                                    "yyyyMMdd",
                                                                                    datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrSplitDate[1] + "/"
                                                        + arrSplitDate[2],
                                                        midX,
                                                        y - widely,
                                                        pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrSplitDate[0] + "/"
                                                        + arrSplitDate[1],
                                                        midX,
                                                        y - widely,
                                                        pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrSplitDate[0] + "/"
                                                        + arrSplitDate[1],
                                                        midX,
                                                        y - widely,
                                                        pBotBoldTxt);
                                    }
                                } else if (customDateType == CustomDate.YEAR) {
                                    String[] arrDate =
                                                     ManagerUtil.getDateCharacter(localization,
                                                                                  ManagerUtil.ShowFormatPosition.MINUTE,
                                                                                  true,
                                                                                  "/",
                                                                                  null,
                                                                                  "yyyyMM",
                                                                                  datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrSplitDate[1], midX, y - widely, pBotBoldTxt);
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrSplitDate[0], midX, y - widely, pBotBoldTxt);
                                    } else {
                                        canvas.drawText(arrSplitDate[1], midX, y - widely, pBotBoldTxt);
                                    }
                                } else if (customDateType == CustomDate.ALL) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization,
                                                                                    ManagerUtil.ShowFormatPosition.MINUTE,
                                                                                    true,
                                                                                    "/",
                                                                                    ":",
                                                                                    "yyyyMMddHHmmss",
                                                                                    datelist.get(i));
                                    //                                    String[] arrSplitDate = arrDate[0].split("/");
                                    DisplayMetrics metrics = new DisplayMetrics();
                                    WindowManager mWindowManager =
                                                                 (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
                                    mWindowManager.getDefaultDisplay().getMetrics(metrics);

                                    if (localization == ManagerUtil.Localization.TYPE_01) {
                                        canvas.drawText(arrDate[0].substring(5, arrDate[0].length()),
                                                        midX,
                                                        y - widely,
                                                        pBotBoldTxt);
                                        if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                                            canvas.drawText(arrDate[1], midX, y - widely + 30, pBotBoldTxt);
                                        } else {
                                            canvas.drawText(arrDate[1], midX, y - widely + 50, pBotBoldTxt);
                                        }
                                    } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                        canvas.drawText(arrDate[0].substring(0, arrDate[0].length() - 5),
                                                        midX,
                                                        y - widely,
                                                        pBotBoldTxt);
                                        if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                                            canvas.drawText(arrDate[1], midX, y - widely + 30, pBotBoldTxt);
                                        } else {
                                            canvas.drawText(arrDate[1], midX, y - widely + 50, pBotBoldTxt);
                                        }
                                    } else {
                                        canvas.drawText(arrDate[0].substring(0, arrDate[0].length() - 5),
                                                        midX,
                                                        y - widely,
                                                        pBotBoldTxt);
                                        if (metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                                            canvas.drawText(arrDate[1], midX, y - widely + 30, pBotBoldTxt);
                                        } else {
                                            canvas.drawText(arrDate[1], midX, y - widely + 50, pBotBoldTxt);
                                        }
                                    }
                                }
                            } else {
                                if (customDateType == CustomDate.TODAY) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(localization,
                                                                                    ManagerUtil.ShowFormatPosition.MINUTE,
                                                                                    true,
                                                                                    "/",
                                                                                    "^",
                                                                                    "yyyyMMddHH",
                                                                                    datelist.get(i));
                                    canvas.drawText(arrDate[1] + ":00", midX, y, pBotBoldTxt);
                                } else if (customDateType == CustomDate.WEEK) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01,
                                                                                    ManagerUtil.ShowFormatPosition.MINUTE,
                                                                                    true,
                                                                                    "/",
                                                                                    null,
                                                                                    "yyyyMMdd",
                                                                                    datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2], midX, y, pBotBoldTxt);
                                } else if (customDateType == CustomDate.MONTH) {
                                    String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01,
                                                                                    ManagerUtil.ShowFormatPosition.MINUTE,
                                                                                    true,
                                                                                    "/",
                                                                                    null,
                                                                                    "yyyyMMdd",
                                                                                    datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2], midX, y, pBotBoldTxt);
                                } else if (customDateType == CustomDate.YEAR) {
                                    String[] arrDate =
                                                     ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01,
                                                                                  ManagerUtil.ShowFormatPosition.MINUTE,
                                                                                  true,
                                                                                  "/",
                                                                                  null,
                                                                                  "yyyyMM",
                                                                                  datelist.get(i));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    canvas.drawText(arrSplitDate[1], midX, y, pBotBoldTxt);
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
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/"
                                            + datelist.get(i).substring(6, 8),
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                        } else if (datelist.get(i).length() == 6) {
                            // 1년 데이터
                            canvas.drawText(datelist.get(i).substring(2, 4) + "/"
                                            + datelist.get(i).substring(4, 6),
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE / 3,
                                            pBotBoldTxt);
                        } else if (datelist.get(i).length() == 14 && isBottomWidely) {
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/"
                                            + datelist.get(i).substring(6, 8),
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE + 30,
                                            pBotBoldTxt);
                            canvas.drawText(datelist.get(i).substring(8, 10) + ":"
                                            + datelist.get(i).substring(10, 12),
                                            midX,
                                            nEndY - START_BOTTOM_Y_SPACE + 80,
                                            pBotTxt);
                        } else if (datelist.get(i).length() == 14 && !isBottomWidely) {
                            canvas.drawText(datelist.get(i).substring(4, 6) + "/"
                                            + datelist.get(i).substring(6, 8),
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
        // 그래프 윤곽
        //        canvas.drawRect(rLeftOutLine, outLine);
        canvas.drawRect(rBotOutLine, outLine);
        canvas.drawRect(rRightOutLine, outLine);
        // y절편 -랑 숫자 적기
        for (int i = 0; i < nShowYValue; i++) {
            int showY = nMinY + ((nMaxY - nMinY) / nShowYValue) * (nShowYValue - i);
            float y = fGraphStartY + (((getGraphBottom() - fGraphStartY) / nShowYValue) * i);
            if (getYDataType().equals(Y_DATA_TYPE_TIME)) {
                showY = showY / 60;
                canvas.drawText(showY + "", fGraphStartX - 15, y + 4, pYValue);
            } else {
                float x = 0;
                switch (dipInfo) {
                    case 0:
                    case 1:
                    case 2:
                        x = fGraphStartX - 15;
                        break;
                    default:
                        x = fGraphStartX - 10;
                        break;
                }
                canvas.drawText(showY + "", x, y + 4, pYValue);
            }
        }
        if (rGraphPercent < 100) {
            rGraphPercent = rGraphPercent + 1;
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
            lineLocation(currentLineX, 0);
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
            lineLocation(currentLineX, 0);
            invalidate();
        } else {
            // fSpeedX = 0;
        }
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
        pline.setStyle(Paint.Style.STROKE);
        pline.setStrokeCap(Paint.Cap.ROUND);
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
                        float y1 = getDoubleIndexToTop(i, list);
                        if (x1 > nEndX) {
                            break;
                        } else {
                            if (i < list.size() - 1 && i < list.size() - 1) {
                                float x2 = getIndexToMiddle(i + 1);
                                float y2 = getDoubleIndexToTop(i + 1, list);
                                if (x2 < 0) {
                                    // 그리지 않음
                                } else {
                                    if (list.get(i) > 0) {
                                        if ((list.size() - 1) != i && list.get(i + 1) > 0) {
                                            int arcNum = 0;
                                            switch (dipInfo) {
                                                case 0:
                                                case 1:
                                                case 2:
                                                    arcNum = 50;
                                                    break;
                                                case 3:
                                                    arcNum = 100;
                                                    break;
                                                default:
                                                    arcNum = 100;
                                                    break;
                                            }

                                            final Path path = new Path();
                                            path.moveTo(x1, y1);
                                            if (list.get(i) > list.get(i + 1)) {
                                                path.cubicTo(x1 + arcNum, y1, x2 - arcNum, y2, x2, y2);
                                                canvas.drawPath(path, pline);
                                            } else if (list.get(i) < list.get(i + 1)) {
                                                path.cubicTo(x1 + arcNum, y1, x2 - arcNum, y2, x2, y2);
                                                canvas.drawPath(path, pline);
                                            } else if (list.get(i) == list.get((i + 1))) {
                                                path.quadTo(((x1 + x2) / 2) - 10, y1, x2, y2);
                                                canvas.drawLine(x1, y1, x2, y2, pline);
                                            } else {
                                                path.quadTo(((x1 + x2) / 2) - 10, y1, x2, y2);
                                                canvas.drawLine(x1, y1, x2, y2, pline);
                                            }
                                        } else {
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
                        float y1 = getDoubleIndexToTop(i, list);
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
                } else if (nGraphShape == BAR) { // 바형 그래프 그리기
                    // 표 그리기
                    for (int i = 0; i < list.size(); i++) {
                        if (list.size() <= i) {
                            break;
                        }
                        float left = getIndexToLeft(i);
                        float right = getIndexToRight(i);
                        float bottom = getGraphBottom();
                        float top = getDoubleIndexToTop(i, list);
                        // 그래프 그리기
                        canvas.drawRect(left, top, right, bottom, pline);
                        // // 패스로 라인그리기
                        // Paint pline1 = new Paint();
                        // pline1.setAntiAlias(true);
                        // pline1.setStrokeWidth(4);
                        //
                        // pline1.setColor(Color.YELLOW);
                        //
                        // canvas.drawLine(left, top, right, top, pline1);
                        // 1~xValue까지
                        // canvas.drawLine(getIndexToMiddle(i), endY,
                        // getIndexToMiddle(i), endY - AXIS_LINE, pAxisLine);
                    }
                }
            }
        } else if (dataType.equals(DATA_TYPE_INT)) {
            for (int idx = 0, len = graphIntegerDataList.size(); idx < len; idx++) {
                GraphIntegerData gd = graphIntegerDataList.get(idx);
                List<Map<String, String>> list = gd.getList();
                ArrayList<Float> dataList = new ArrayList<Float>();
                ArrayList<String> mealList = new ArrayList<String>();
                for (int i = 0; i < list.size(); i++) {
                    dataList.add(Float.parseFloat(list.get(i)
                                                      .get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_BEFORE)));
                    mealList.add(list.get(i).get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
                }
                ArrayList<String> datelist = gd.getDateList();
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
                        float y1 = getIntegerIndexToTop(i, dataList);
                        if (x1 > nEndX) {
                            break;
                        } else {
                            if (i < dataList.size() - 1 && i < dataList.size() - 1) {
                                float x2 = getIndexToMiddle(i + 1);
                                float y2 = getIntegerIndexToTop(i + 1, dataList);
                                if (x2 < 0) {
                                    // 그리지 않음
                                } else {
                                    if (dataList.get(i) > 0) {
                                        if ((dataList.size() - 1) != i && dataList.get(i + 1) > 0) {
                                            int arcNum = 0;
                                            switch (dipInfo) {
                                                case 0:
                                                case 1:
                                                case 2:
                                                    arcNum = 50;
                                                    break;
                                                case 3:
                                                    arcNum = 100;
                                                    break;
                                                default:
                                                    arcNum = 100;
                                                    break;
                                            }

                                            final Path path = new Path();
                                            path.moveTo(x1, y1);
                                            if (dataList.get(i) > dataList.get(i + 1)) {
                                                path.cubicTo(x1 + arcNum, y1, x2 - arcNum, y2, x2, y2);
                                                canvas.drawPath(path, pline);
                                            } else if (dataList.get(i) < dataList.get(i + 1)) {
                                                path.cubicTo(x1 + arcNum, y1, x2 - arcNum, y2, x2, y2);
                                                canvas.drawPath(path, pline);
                                            } else if (dataList.get(i) == dataList.get((i + 1))) {
                                                path.quadTo(((x1 + x2) / 2) - 10, y1, x2, y2);
                                                canvas.drawLine(x1, y1, x2, y2, pline);
                                            } else {
                                                path.quadTo(((x1 + x2) / 2) - 10, y1, x2, y2);
                                                canvas.drawLine(x1, y1, x2, y2, pline);
                                            }
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
                        float y1 = getIntegerIndexToTop(i, dataList);
                        if (x1 < 0) {
                            // 그리지 않음
                        } else if (x1 > nEndX) {
                            break;
                        } else {
                            if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(mealList.get(i))) {
                                if (dataList.get(i) > 0) {
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
                            } else if (ManagerConstants.EatType.GLUCOSE_AFTER.equals(mealList.get(i))) {
                                if (dataList.get(i) > 0) {
                                    if (nLineX == i) {
                                        canvas.drawBitmap(gd.getSelIcon1(),
                                                          x1 - gd.getSelIcon1().getWidth() / 2,
                                                          y1 - gd.getSelIcon1().getHeight() / 2,
                                                          null);
                                    } else {
                                        canvas.drawBitmap(gd.getDefIcon1(),
                                                          x1 - gd.getDefIcon1().getWidth() / 2,
                                                          y1 - gd.getDefIcon1().getHeight() / 2,
                                                          null);
                                    }
                                }
                            }
                        }
                    }
                } else if (nGraphShape == BAR) { // 바형 그래프 그리기
                    // 표 그리기
                    for (int i = 0; i < list.size(); i++) {
                        if (list.size() <= i) {
                            break;
                        }
                        float left = getIndexToLeft(i);
                        float right = getIndexToRight(i);
                        float bottom = getGraphBottom();
                        float top = getIntegerIndexToTop(i, dataList);
                        // 그래프 그리기
                        canvas.drawRect(left, top, right, bottom, pline);
                        // // 패스로 라인그리기
                        // Paint pline1 = new Paint();
                        // pline1.setAntiAlias(true);
                        // pline1.setStrokeWidth(4);
                        //
                        // pline1.setColor(Color.YELLOW);
                        //
                        // canvas.drawLine(left, top, right, top, pline1);
                        // 1~xValue까지
                        // canvas.drawLine(getIndexToMiddle(i), endY,
                        // getIndexToMiddle(i), endY - AXIS_LINE, pAxisLine);
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
        int dx = (int)(event.getX(0) - event.getX(1));
        int dy = (int)(event.getY(0) - event.getY(1));
        return (int)(Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isInitialize == false) {
            return false;
        }
        int actionmask = event.getActionMasked();
        if (event.getPointerCount() > 1) {
            // if (event.getPointerCount() == 2) {
            // int action = event.getAction();
            // int pureaction = action & MotionEvent.ACTION_MASK;
            // if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
            // nBaseZoom = 0;
            // nBaseDist = getDistance(event);
            // } else {
            // int delta = (getDistance(event) - nBaseDist) / nPixToZoom;
            // if (delta < nBaseZoom) {
            // addItem();
            // nBaseZoom = delta;
            // } else if (delta > nBaseZoom) {
            // subItem();
            // nBaseZoom = delta;
            // }
            // }
            //
            // return true;
            // }
        } else {
            // 다운 이벤트
            // if (actionmask == MotionEvent.ACTION_DOWN) {
            // beforeLineX = lineX;
            // lineLocation(event.getX(), event.getY());
            // }
            //
            // if (actionmask == MotionEvent.ACTION_UP) {
            // lineLocation(event.getX(), event.getY());
            // if (beforeLineX != lineX) {
            // graphRotateAni();
            // }
            // }
            if (actionmask == MotionEvent.ACTION_DOWN) {
                // beforeLineX = lineX;
                // lineLocation(event.getX(), event.getY());
                touchDown = event.getX();
                touchX = event.getX();
                speedList.clear();
                isMove = false;
            }
            if (actionmask == MotionEvent.ACTION_UP) {
                // Log.e("ssryu", "ACTION_UP");
                if (isMove) {
                    isMove = false;
                } else {
                    if (Math.abs(touchDown - event.getX()) < 20) {
                        currentLineX = event.getX();
                        lineLocation(event.getX(), event.getY());
                        if (nBeforeLineX != nLineX) {
                            graphRotateAni();
                        }
                    }
                }
                invalidate();
            }
        }
        if (actionmask == MotionEvent.ACTION_MOVE) {
            // Log.e("ssryu", "ACTION_MOVE");
            lineLocation(currentLineX, event.getY());
            if (isMove) {
                fModX = fModX + (touchX - event.getX()) * 1.5f;
                speedList.add((touchX - event.getX()) * 2f);
                if (speedList.size() > AVR_DATA) {
                    speedList.remove(0);
                }
                float temp = 0;
                for (Float f : speedList) {
                    temp = temp + f;
                }
                fSpeedX = temp / speedList.size();
                touchX = event.getX();
                if (fModX < 0) {
                    fModX = 0;
                } else if (fModX > fModMaxX) {
                    fModX = fModMaxX;
                }
                invalidate();
            } else {
                if (Math.abs(touchDown - event.getX()) > 20) {
                    isMove = true;
                }
                invalidate();
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
                    if (x > nEndX) { // 데이터 양보다 표시되는 양이 적을경우 화면 넘게 끌시 다음데이터로
                        // 연동되서
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
    private float getDoubleIndexToTop(int index, ArrayList<Double> list) {
        double value = list.get(index);
        // if(dataType == TYPE_TIME){
        // if(value/60 > 0){
        // value = value/60;
        // }else{
        // value = value%60;
        // }
        // }
        if (value >= nMaxY) {
            return getGraphBottom() - (getOneToPixel() * 100);
        } else if (value <= nMinY) {
            return getGraphBottom();
        } else {
            return (float)(getGraphBottom() - (getOneToPixel() * ((value - nMinY) / ((nMaxY - nMinY) * 1.0f)) * 100));
        }
    }

    private float getIntegerIndexToTop(int index, ArrayList<Float> list) {
        float value = list.get(index);
        // if(dataType == TYPE_TIME){
        // if(value/60 > 0){
        // value = value/60;
        // }else{
        // value = value%60;
        // }
        // }
        if (value >= nMaxY) {
            return getGraphBottom() - (getOneToPixel() * 100);
        } else if (value <= nMinY) {
            return getGraphBottom();
        } else {
            return (getGraphBottom() - (getOneToPixel() * ((value - nMinY) / ((nMaxY - nMinY) * 1.0f)) * 100));
        }
    }

    // 퍼센트 비중 없이 Top높이 구함
    private float getIndexToTop(int value) {
        if (value >= nMaxY) {
            return getGraphBottom() - ((getGraphBottom() - fGraphStartY));
        } else if (value <= nMinY) {
            return getGraphBottom();
        } else {
            return getGraphBottom()
                   - ((getGraphBottom() - fGraphStartY) * ((value - nMinY) / ((nMaxY - nMinY) * 1.0f)));
        }
    }

    /**
     * 현재 그래프 퍼센테이지
     *
     * @return
     */
    private float getOneToPixel() {
        return (getGraphBottom() - fGraphStartY) / (100 * (100 / rGraphPercent));
    }
}
