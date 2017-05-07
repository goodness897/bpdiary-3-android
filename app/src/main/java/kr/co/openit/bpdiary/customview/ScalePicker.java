package kr.co.openit.bpdiary.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.Arrays;

import kr.co.openit.bpdiary.R;

public class ScalePicker extends LinearLayout
                         implements WheelPicker.OnWheelClickListener, WheelNumberPicker.OnNumberSelectedListener {

    public static final boolean IS_CYCLIC_DEFAULT = false;

    public static final boolean IS_CURVED_DEFAULT = false;

    public static final boolean CAN_BE_ON_PAST_DEFAULT = false;

    public static final int DELAY_BEFORE_CHECK_PAST = 200;

    private static final int VISIBLE_ITEM_COUNT_DEFAULT = 7;

    private static final int MAX_VALUE = 250;

    private static final int MIN_VALUE = 1;

    private WheelNumberPicker kgDecimalPicker;

    private WheelNumberPicker kgNumberPicker;

    private WheelNumberPicker lbsDecimalPicker;

    private WheelNumberPicker lbsNumberPicker;

    private LinearLayout llKg;

    private LinearLayout llLbs;

    private Listener listener;

    private int textColor;

    private int selectedTextColor;

    private int textSize;

    private boolean isCyclic;

    private boolean isCurved;

    private int visibleItemCount;

    private boolean canBeOnPast;

    private int minValue;

    private int maxValue;

    private int unitType;

    public static final int KG = 1;

    public static final int LBS = 2;

    private String weightKg = "100";

    private String weightLbs = "100";

    public ScalePicker(Context context) {
        this(context, null);
    }

    public ScalePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScalePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        inflate(context, R.layout.layout_scale_picker, this);

        setLayout();

        updatePicker();
    }

    private void setLayout() {

        kgDecimalPicker = (WheelNumberPicker)findViewById(R.id.np_decimal_kg);

        kgNumberPicker = (WheelNumberPicker)findViewById(R.id.np_number_kg);

        lbsDecimalPicker = (WheelNumberPicker)findViewById(R.id.np_decimal_lbs);

        lbsNumberPicker = (WheelNumberPicker)findViewById(R.id.np_number_lbs);

        llKg = (LinearLayout)findViewById(R.id.ll_kg);

        llLbs = (LinearLayout)findViewById(R.id.ll_lbs);

        kgDecimalPicker.setOnWheelClickListener(this);

        kgNumberPicker.setOnWheelClickListener(this);

        lbsNumberPicker.setOnWheelClickListener(this);

        lbsDecimalPicker.setOnWheelClickListener(this);

        kgDecimalPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                weightKg = String.valueOf(kgNumberPicker.getCurrentNumber()) + "."
                           + String.valueOf(number);
                updateListener();
            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                weightKg = String.valueOf(kgNumberPicker.getCurrentNumber()) + "."
                           + String.valueOf(number);
                updateListener();

            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        kgNumberPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                weightKg = String.valueOf(number) + "."
                           + String.valueOf(kgDecimalPicker.getCurrentNumber());
                updateListener();

            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                weightKg = String.valueOf(number) + "."
                           + String.valueOf(kgDecimalPicker.getCurrentNumber());
                updateListener();

            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        lbsNumberPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                weightLbs = String.valueOf(number) + "."
                            + String.valueOf(lbsDecimalPicker.getCurrentNumber());
                updateListener();

            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                weightLbs = String.valueOf(number) + "."
                            + String.valueOf(lbsDecimalPicker.getCurrentNumber());
                updateListener();

            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        lbsDecimalPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                weightLbs = String.valueOf(lbsNumberPicker.getCurrentNumber()) + "."
                            + String.valueOf(number);
                updateListener();

            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                weightLbs = String.valueOf(lbsNumberPicker.getCurrentNumber()) + "."
                            + String.valueOf(number);
                updateListener();

            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

    }

    public void setCurved(boolean curved) {
        isCurved = curved;
        updatePicker();
    }

    public void setCyclic(boolean cyclic) {
        isCyclic = cyclic;
        updatePicker();
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        updatePicker();
    }

    public void setSelectedTextColor(int selectedTextColor) {
        this.selectedTextColor = selectedTextColor;
        updatePicker();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        updatePicker();
    }

    public void setVisibleItemCount(int visibleItemCount) {
        this.visibleItemCount = visibleItemCount;
        updatePicker();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        updatePicker();
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        updatePicker();

    }

    public int getUnitType() {
        return unitType;
    }

    public void setUnitType(int unitType) {
        this.unitType = unitType;

    }

    public void updatePicker() {
        if (kgDecimalPicker != null && kgNumberPicker != null && lbsNumberPicker != null && lbsDecimalPicker != null) {
            for (WheelPicker wheelPicker : Arrays.asList(kgDecimalPicker,
                                                         kgNumberPicker,
                                                         lbsDecimalPicker,
                                                         lbsNumberPicker)) {
                wheelPicker.setItemTextColor(textColor);
                wheelPicker.setSelectedItemTextColor(selectedTextColor);
                wheelPicker.setItemTextSize(textSize);
                wheelPicker.setCyclic(isCyclic);
                wheelPicker.setCurved(isCurved);
                wheelPicker.setVisibleItemCount(visibleItemCount);
                wheelPicker.setMinValue(minValue);
                wheelPicker.setMaxValue(maxValue);

                weightKg = String.valueOf(kgNumberPicker.getCurrentNumber()) + "."
                        + String.valueOf(kgDecimalPicker.getCurrentNumber());
                weightLbs = String.valueOf(lbsNumberPicker.getCurrentNumber()) + "."
                        + String.valueOf(lbsDecimalPicker.getCurrentNumber());
                updateListener();
            }
        }
    }

    //    private void checkInPast(final WheelPicker picker) {
    //        picker.postDelayed(new Runnable() {
    //            @Override
    //            public void run() {
    //                if (!canBeOnPast && isInPast(getDate())) {
    //                    kgDecimalPicker.scrollTo(kgDecimalPicker.getDefaultItemPosition());
    //                    kgNumberPicker.scrollTo(kgNumberPicker.getDefaultItemPosition());
    //                }
    //            }
    //        }, DELAY_BEFORE_CHECK_PAST);
    //    }
    //
    //    private boolean isInPast(Date date) {
    //        final Calendar todayCalendar = Calendar.getInstance();
    //        todayCalendar.set(Calendar.MILLISECOND, 0);
    //        todayCalendar.set(Calendar.SECOND, 0);
    //
    //        final Calendar dateCalendar = Calendar.getInstance();
    //        dateCalendar.set(Calendar.MILLISECOND, 0);
    //        dateCalendar.set(Calendar.SECOND, 0);
    //
    //        dateCalendar.setTime(date);
    //        return dateCalendar.before(todayCalendar);
    //    }
    //
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    //    public Date getDate() {
    //        final int hour = hoursPicker.getCurrentHour();
    //        final int minute = minutesPicker.getCurrentNumber();
    //
    //        final Calendar calendar = Calendar.getInstance();
    //        final Date dayDate = kgDecimalPicker.getCurrentDate();
    //        calendar.setTime(dayDate);
    //        calendar.set(Calendar.HOUR_OF_DAY, hour);
    //        calendar.set(Calendar.MINUTE, minute);
    //
    //        final Date time = calendar.getTime();
    //        return time;
    //    }

    //    public void selectDate(Calendar calendar) {
    //        if (calendar == null) {
    //            return;
    //        }
    //        Date date = calendar.getTime();
    //        int indexOfDay = kgDecimalPicker.findIndexOfDate(date);
    //        if (indexOfDay != 0) {
    //            kgDecimalPicker.setSelectedItemPosition(indexOfDay);
    //        }
    //        int indexOfHour = hoursPicker.findIndexOfDate(date);
    //        if (indexOfHour != 0) {
    //            hoursPicker.setSelectedItemPosition(indexOfHour);
    //        }
    //        int indexOfMin = minutesPicker.findIndexOfDate(date);
    //        if (indexOfMin != 0) {
    //            minutesPicker.setSelectedItemPosition(indexOfMin);
    //        }
    //    }

    private void updateListener() {
        String displayed = "";
        switch (unitType) {
            case KG:
                displayed = weightKg;
                break;
            case LBS:
                displayed = weightLbs;
        }

        if (listener != null) {
            listener.onNumberChanged(displayed, unitType);
        }
    }

    private void clickListener() {
        if (listener != null) {
            listener.onClickChanged();
            updateListener();
        }
    }

    public void setCanBeOnPast(boolean canBeOnPast) {
        this.canBeOnPast = canBeOnPast;
    }

    public boolean canBeOnPast() {
        return canBeOnPast;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleDateAndTimePicker);

        textColor = a.getColor(R.styleable.SingleDateAndTimePicker_picker_textColor,
                               getResources().getColor(R.color.color_5e5e5e));
        selectedTextColor = a.getColor(R.styleable.SingleDateAndTimePicker_picker_selectedTextColor,
                                       getResources().getColor(R.color.color_2e2e2e));
        textSize = a.getDimensionPixelSize(R.styleable.SingleDateAndTimePicker_picker_textSize,
                                           getResources().getDimensionPixelSize(R.dimen.WheelItemTextSize));
        isCurved = a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_curved, IS_CURVED_DEFAULT);
        isCyclic = a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_cyclic, IS_CYCLIC_DEFAULT);
        canBeOnPast = a.getBoolean(R.styleable.SingleDateAndTimePicker_picker_canBeOnPast, CAN_BE_ON_PAST_DEFAULT);
        visibleItemCount = a.getInt(R.styleable.SingleDateAndTimePicker_picker_visibleItemCount,
                                    VISIBLE_ITEM_COUNT_DEFAULT);

        maxValue = a.getInt(R.styleable.SingleDateAndTimePicker_picker_max_value, MAX_VALUE);

        minValue = a.getInt(R.styleable.SingleDateAndTimePicker_picker_min_value, MIN_VALUE);

        a.recycle();
    }

    @Override
    public void onWheelSelected() {
        clickListener();

    }

    @Override
    public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
        updateListener();
    }

    @Override
    public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
        updateListener();

    }

    @Override
    public void onNumberScrolledNewNumber(WheelNumberPicker picker) {
        updateListener();

    }

    public interface Listener {

        void onNumberChanged(String displayed, int number);

        void onClickChanged();
    }

    public WheelNumberPicker getKgNumberPicker() {
        return kgNumberPicker;
    }

    public void setKgNumberPicker(WheelNumberPicker kgNumberPicker) {
        this.kgNumberPicker = kgNumberPicker;
    }

    public WheelNumberPicker getKgDecimalPicker() {
        return kgDecimalPicker;
    }

    public void setKgDecimalPicker(WheelNumberPicker kgDecimalPicker) {
        this.kgDecimalPicker = kgDecimalPicker;
    }

    public WheelNumberPicker getLbsDecimalPicker() {
        return lbsDecimalPicker;
    }

    public void setLbsDecimalPicker(WheelNumberPicker lbsDecimalPicker) {
        this.lbsDecimalPicker = lbsDecimalPicker;
    }

    public WheelNumberPicker getLbsNumberPicker() {
        return lbsNumberPicker;
    }

    public void setLbsNumberPicker(WheelNumberPicker lbsNumberPicker) {
        this.lbsNumberPicker = lbsNumberPicker;
    }

    public LinearLayout getLlKg() {
        return llKg;
    }

    public void setLlKg(LinearLayout llKg) {
        this.llKg = llKg;
    }

    public LinearLayout getLlLbs() {
        return llLbs;
    }

    public void setLlLbs(LinearLayout llLbs) {
        this.llLbs = llLbs;
    }
}
