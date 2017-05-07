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
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;

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
 * @created 2017.01.26.
 */
public class GraphVarOneStick extends View {

    private int widely = 0;

    private ManagerUtil.Localization localization = ManagerUtil.Localization.TYPE_01;

    private int dipInfo;

    /**
     * 그래프 데이터
     *
     * @author <a href="mailto:support@openit.co.kr">(주)오픈잇 | openit Inc.</a>
     * @created 2013. 1. 29.
     */
    private class GraphIntegerData {

        private final ArrayList<String> dataList;

        private final ArrayList<String> dataDateList;

        private final String strName;

        // 라인색
        private final int nLineColor;

        // 선택 아이콘 이미지
        private final Bitmap bmpSelIcon;

        // 채우기 색
        private final int fillColor;

        public GraphIntegerData(Context context,
                                String strName,
                                ArrayList<String> dataList,
                                ArrayList<String> dataDateList,
                                int nLineColor,
                                int bmpSelIcon,
                                int fillColor) {
            this.dataList = dataList;
            this.dataDateList = dataDateList;
            this.strName = strName;
            this.nLineColor = nLineColor;
            this.bmpSelIcon = BitmapFactory.decodeResource(context.getResources(), bmpSelIcon);
            this.fillColor = fillColor;
        }

        /**
         * list getter
         *
         * @return list
         */
        public ArrayList<String> getList() {
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

        public int getFillColor() {
            return this.fillColor;
        }

        public Bitmap getSelIcon() {
            return this.bmpSelIcon;
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
    // private static final int START_LEFT_X_SPACE = 50;
    private static final int START_LEFT_X_SPACE = 150;

    /**
     * 그래프 시작 RightX빈칸
     */
    // private static final int START_RIGHT_X_SPACE = 35;
    private static final int START_RIGHT_X_SPACE = 70;

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

    public static final int TYPE_TIME = 1;

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
    //    private final int cText = Color.rgb(0, 0, 0);

    private final int cFillBlue = Color.argb(61, 0, 125, 238);

    private final int cFillRed = Color.argb(61, 243, 105, 43);

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
    public GraphVarOneStick(Context context, Handler h) {
        super(context);
        this.mContext = context;
        hCurrentActivitiy = h;
        isInitialize = false;
        dipInfo = Math.round(PhoneUtil.dipInfo(mContext));
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
                              int selIcon,
                              int fillColor) {
        ArrayList<String> arrDateList = new ArrayList<String>();
        nDataAmount = list.size();
        if (dataType.equals(DATA_TYPE_INT)) {
            ArrayList<String> arrList = new ArrayList<String>();
            for (int i = 0; i < nDataAmount; i++) {
                String data = list.get(i).get(key);
                String strAddDate = list.get(i).get(date);
                arrList.add(data);
                arrDateList.add(strAddDate);
                if (Float.parseFloat(data) > 0) {
                    nFirstIdx = i;
                }
            }
            GraphIntegerData graphintegerdata =
                    new GraphIntegerData(mContext,
                            name,
                            arrList,
                            arrDateList,
                            lineColor,
                            selIcon,
                            fillColor);
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
        graphIntegerDataList.clear();
    }

    /**
     * 그래프를 초기화
     *
     * @param graphType  그래프 타입
     * @param showXValue X에 표시될 양
     * @param yMax       y값 최대
     * @param yMin       y값 최소
     * @param showYValue y에 표시될 양
     * @param startX     그래프 시작X
     * @param startY     그래프 시작Y
     * @param endX       그래프 끝X
     * @param endY       그래프 끝Y
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

        if (dipInfo <= 2) {
            rLeftBg = new RectF(this.nStartX, this.nStartY, this.fGraphStartX, this.nEndY);
        } else {
            rLeftBg = new RectF(this.nStartX, this.nStartY, this.fGraphStartX - 20, this.nEndY);

        }
        rRightBg = new RectF(this.fGraphEndX, this.nStartY, this.nEndX, this.nEndY);
        rTopBg = new RectF(this.nStartX, this.nStartY, this.nEndX, this.fGraphStartY - 170);
        rBotBg = new RectF(this.nStartX, this.fGraphEndY, this.nEndX, this.nEndY);
        rLeftOutLine =
                new RectF(this.fGraphStartX - 2, this.fGraphStartY - 170, this.fGraphStartX, this.fGraphEndY + 4);
        //        rBotOutLine = new RectF(119, this.fGraphEndY + 2, this.fGraphEndX - 119, this.fGraphEndY + 4);
        //        rBotOutLine = new RectF(this.fGraphStartX, this.fGraphEndY + 2, this.fGraphEndX, this.fGraphEndY + 4);
        //        rRightOutLine = new RectF(this.fGraphEndX, this.fGraphStartY - 170, this.fGraphEndX + 2, this.fGraphEndY + 4);
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
        pBg.setColor(getResources().getColor(R.color.color_00000000));
        // pBg.setColor(getResources().getColor(R.color.color_05000000));
        // 아래 부분 텍스트
        pBotBoldTxt = new Paint();
        switch (dipInfo) {
            case 0:
            case 1:
            case 2:
                pBotBoldTxt.setTextSize(START_BOTTOM_Y_SPACE / 5f);
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
            pBotTxt.setTextSize(START_LEFT_X_SPACE / 3f);
        } else {
            pBotTxt.setTextSize(START_LEFT_X_SPACE / 5f);
        }
        pBotTxt.setColor(cText);
        pBotTxt.setTextAlign(Align.CENTER);
        pBotTxt.setAntiAlias(true);
        // Y절편 텍스트
        pYValue = new Paint();
        pYValue.setColor(cText);
        if (PhoneUtil.dipInfo(mContext) > 2) {
            pYValue.setTextSize(START_LEFT_X_SPACE / 4f);
        } else {
            pYValue.setTextSize(START_LEFT_X_SPACE / 6f);
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
        fGraphSpace = fGraphWidth * 8;
        fGraphSpacehalf = fGraphSpace * 8;
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
            float selectX1 = getIndexToMiddle(0);
            float selectX2 = getIndexToMiddle(1);
            float y = fGraphStartY;
            canvas.drawLine(selectX1, y, selectX1, getGraphBottom(), pIndicator);
            canvas.drawLine(selectX2, y, selectX2, getGraphBottom(), pIndicator);
        }
        // 그래프 그리기
        drawGraph(canvas);
        // 그래프 화이트 영역
        canvas.drawRect(rTopBg, pWhite);
        canvas.drawRect(rBotBg, pWhite);
        if (dataType.equals(DATA_TYPE_INT)) {
            // 밑에 숫자
            for (int idx = 0, len = graphIntegerDataList.size(); idx < 1; idx++) {
                GraphIntegerData gd = graphIntegerDataList.get(idx);
                ArrayList<String> list = gd.getList();
                ArrayList<String> datelist = gd.getDateList();
                for (int i = 0; i < list.size(); i++) {
                    float midX = getIndexToMiddle(i);
                    if (midX < 0) {
                        // 그리지 않음
                    } else if (midX > nEndX) {
                        // 그리지 않음
                        break;
                    } else {
                        float y = 0;
                        int space = 0;
                        switch (dipInfo) {
                            case 0:
                            case 1:
                            case 2:
                                y = nEndY - (START_BOTTOM_Y_SPACE - 60) / 3;
                                space = 25;
                                break;
                            default:
                                y = nEndY - START_BOTTOM_Y_SPACE / 3;
                                space = 50;
                                break;
                        }
                        if (customDateType == CustomDate.MONTH) {

                            if (localization == ManagerUtil.Localization.TYPE_01) {
                                String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01,
                                        ManagerUtil.ShowFormatPosition.MINUTE,
                                        true,
                                        "/",
                                        null,
                                        "yyyyMMdd",
                                        datelist.get(i));
                                String[] arrSplitDate = arrDate[0].split("/");
                                if (i == 0) {
                                    canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2], midX + space, y, pBotBoldTxt);
                                } else {
                                    canvas.drawText(arrSplitDate[1] + "/" + arrSplitDate[2], midX - space, y, pBotBoldTxt);
                                }
                            } else if (localization == ManagerUtil.Localization.TYPE_02) {
                                String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_02,
                                        ManagerUtil.ShowFormatPosition.MINUTE,
                                        true,
                                        "/",
                                        null,
                                        "yyyyMMdd",
                                        datelist.get(i));
                                String[] arrSplitDate = arrDate[0].split("/");
                                if (i == 0) {
                                    canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1], midX + space, y, pBotBoldTxt);
                                } else {
                                    canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1], midX - space, y, pBotBoldTxt);
                                }
                            } else {
                                String[] arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_03,
                                        ManagerUtil.ShowFormatPosition.MINUTE,
                                        true,
                                        "/",
                                        null,
                                        "yyyyMMdd",
                                        datelist.get(i));
                                String[] arrSplitDate = arrDate[0].split("/");
                                if (i == 0) {
                                    canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1], midX + space, y, pBotBoldTxt);
                                } else {
                                    canvas.drawText(arrSplitDate[0] + "/" + arrSplitDate[1], midX - space, y, pBotBoldTxt);
                                }
                            }
                        }
                    }
                }
            }
        }
        canvas.drawRect(rLeftBg, pWhite);
        canvas.drawRect(rRightBg, pWhite);
        // 그래프 윤곽
        //        canvas.drawRect(rLeftOutLine, outLine);
        //        canvas.drawRect(rBotOutLine, outLine);
        //        canvas.drawRect(rRightOutLine, outLine);

        // y절편 -랑 숫자 적기
        ArrayList<String> listY1 = new ArrayList<String>();
        ArrayList<String> listY2 = new ArrayList<String>();
        listY1.add(graphIntegerDataList.get(0).getList().get(0));
        listY1.add(graphIntegerDataList.get(1).getList().get(0));
        listY2.add(graphIntegerDataList.get(0).getList().get(1));
        listY2.add(graphIntegerDataList.get(1).getList().get(1));
        for (int i = 0; i < listY1.size(); i++) {
            String showY1 = listY1.get(i);
            String showY2 = listY2.get(i);
            float y1 = 0;
            float y2 = 0;
            float endX = 0;

            if (Float.parseFloat(showY1) >= nMaxY) {
                y1 = getGraphBottom() - (getOneToPixel() * 100);
                y2 = getGraphBottom() - (getOneToPixel() * 100);
            } else if (Float.parseFloat(showY1) <= nMinY) {
                y1 = getGraphBottom();
                y2 = getGraphBottom();
            } else {
                y1 = (getGraphBottom()
                        - (getOneToPixel() * ((Float.parseFloat(showY1) - nMinY) / ((nMaxY - nMinY) * 1.0f)) * 100));
                y2 = (getGraphBottom()
                        - (getOneToPixel() * ((Float.parseFloat(showY2) - nMinY) / ((nMaxY - nMinY) * 1.0f)) * 100));
            }
            if (getYDataType().equals(Y_DATA_TYPE_TIME)) {
                showY1 = String.valueOf(Float.parseFloat(showY1) / 60);
                canvas.drawText(showY1 + "", fGraphStartX - 15, y1 + 4, pYValue);
                canvas.drawText(showY2 + "", fGraphStartX - 15, y2 + 4, pYValue);
            } else {
                float x = 0;
                switch (dipInfo) {
                    case 0:
                    case 1:
                    case 2:
                        x = fGraphStartX;
                        endX = nEndX - x;
                        break;
                    default:
                        x = fGraphStartX - 20;
                        endX = nEndX - x;

                        break;
                }
                canvas.drawText(showY1 + "", x - 10, y1 + 9, pYValue);
                pYValue.setTextAlign(Align.LEFT);
                canvas.drawText(showY2 + "", endX, y2 + 9, pYValue);
                pYValue.setTextAlign(Align.RIGHT);
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
        pline.setStrokeWidth(8);
        pline.setAntiAlias(true);
        pline.setStyle(Paint.Style.STROKE);
        pline.setStrokeCap(Paint.Cap.ROUND);
        if (dataType.equals(DATA_TYPE_INT)) {
            for (int idx = 0, len = graphIntegerDataList.size(); idx < len; idx++) {
                GraphIntegerData gd = graphIntegerDataList.get(idx);
                ArrayList<String> list = gd.getList();
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
                        float y1 = getIntegerIndexToTop(i, list);
                        if (x1 > nEndX) {
                            break;
                        } else {
                            if (i < list.size() - 1 && i < list.size() - 1) {
                                float x2 = getIndexToMiddle(i + 1);
                                float y2 = getIntegerIndexToTop(i + 1, list);

                                Paint fillLine = new Paint();
                                fillLine.setColor(gd.getFillColor());
                                fillLine.setStyle(Paint.Style.FILL);

                                Path path = new Path();
                                path.moveTo(x1, y1);
                                path.lineTo(x2, y2);
                                path.lineTo(x2, getHeight());
                                path.lineTo(x1, getHeight());
                                path.lineTo(x1, y1);
                                canvas.drawPath(path, fillLine);

                                if (x2 < 0) {
                                    // 그리지 않음
                                } else {
                                    if (Float.parseFloat(list.get(i)) > 0) {
                                        if ((list.size() - 1) != i && Float.parseFloat(list.get(i + 1)) > 0) {
                                            canvas.drawLine(x1, y1, x2, y2, pline);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // Bitmap찍기
                    for (int i = 0; i < list.size(); i++) {
                        if (list.size() <= i) {
                            break;
                        }
                        float x1 = getIndexToMiddle(i);
                        float y1 = getIntegerIndexToTop(i, list);

                        float x2 = 0;
                        float y2 = 0;
                        if (x1 < 0) {
                            // 그리지 않음
                        } else if (x1 > nEndX) {
                            break;
                        } else {
                            if (Float.parseFloat(list.get(i)) > 0) {
                                if (nLineX == i) {
                                    canvas.drawBitmap(gd.getSelIcon(),
                                            x1 - gd.getSelIcon().getWidth() / 2,
                                            y1 - gd.getSelIcon().getHeight() / 2,
                                            null);
                                } else {
                                    canvas.drawBitmap(gd.getSelIcon(),
                                            x1 - gd.getSelIcon().getWidth() / 2,
                                            y1 - gd.getSelIcon().getHeight() / 2,
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
        } else {
            // 다운 이벤트
            if (actionmask == MotionEvent.ACTION_DOWN) {
                // beforeLineX = lineX;
                // lineLocation(event.getX(), event.getY());
                touchDown = event.getX();
                touchX = event.getX();
                speedList.clear();
                isMove = false;
            }
            if (actionmask == MotionEvent.ACTION_UP) {
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
        float indexToMiddle = 0;
        switch (dipInfo) {
            case 0:
            case 1:
            case 2:
                indexToMiddle = (fGraphWidth * (index + 0.5f)) + (fGraphSpace * index) + fGraphStartX - fModX;
                break;
            default:
                indexToMiddle = (fGraphWidth * (index + 0.5f)) + (fGraphSpace * index) + fGraphStartX - fModX - 20;
                break;
        }
        return indexToMiddle;

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
            return (float) (getGraphBottom() - (getOneToPixel() * ((value - nMinY) / ((nMaxY - nMinY) * 1.0f)) * 100));
        }
    }

    private float getIntegerIndexToTop(int index, ArrayList<String> list) {
        String value = list.get(index);
        // if(dataType == TYPE_TIME){
        // if(value/60 > 0){
        // value = value/60;
        // }else{
        // value = value%60;
        // }
        // }
        if (Float.parseFloat(value) >= nMaxY) {
            return getGraphBottom() - (getOneToPixel() * 100);
        } else if (Float.parseFloat(value) <= nMinY) {
            return getGraphBottom();
        } else {
            return (getGraphBottom()
                    - (getOneToPixel() * ((Float.parseFloat(value) - nMinY) / ((nMaxY - nMinY) * 1.0f)) * 100));
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
