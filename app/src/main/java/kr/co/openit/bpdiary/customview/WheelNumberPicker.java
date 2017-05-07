package kr.co.openit.bpdiary.customview;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WheelNumberPicker extends WheelPicker {

    public static final int STEP_NUMBER = 1;

    private int defaultNumber;

    private WheelPicker.Adapter adapter;

    int lastScrollPosition;

    private OnNumberSelectedListener onNumberSelectedListener;

    public WheelNumberPicker(Context context) {
        this(context, null);
    }

    public WheelNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        List<String> numbers = new ArrayList<>();
        for (int num = getMinValue(); num <= getMaxValue(); num += STEP_NUMBER)
            numbers.add(String.valueOf(num));
        adapter = new Adapter(numbers);
        setAdapter(adapter);

        defaultNumber = getMaxValue() / 2;

        updateDefaultNumber();
    }

    public void setOnNumberSelectedListener(OnNumberSelectedListener onNumberSelectedListener) {
        this.onNumberSelectedListener = onNumberSelectedListener;
    }

    @Override
    protected void onItemSelected(int position, Object item) {
        if (onNumberSelectedListener != null) {
            onNumberSelectedListener.onNumberSelected(this, position, convertItemToNumber(item));
        }
    }

    @Override
    protected void onItemCurrentScroll(int position, Object item) {
        if (lastScrollPosition != position) {
            onNumberSelectedListener.onNumberCurrentScrolled(this, position, convertItemToNumber(item));
            if (lastScrollPosition == 11 && position == 0)
                if (onNumberSelectedListener != null) {
                    onNumberSelectedListener.onNumberScrolledNewNumber(this);
                }
            lastScrollPosition = position;
        }
    }

    private int findIndexOfMinute(int number) {

        final int itemCount = adapter.getItemCount();
        for (int i = 0; i < itemCount; ++i) {
            final String object = adapter.getItemText(i);
            final Integer value = Integer.valueOf(object);
            if (number < value) {
                return i - 1;
            } else if(number == value) {
                return i;
            }
        }
        return 0;
    }

    protected String getFormattedValue(Object value) {
        Object valueItem = value;
        if (value instanceof Date) {
            Calendar instance = Calendar.getInstance();
            instance.setTime((Date) value);
            valueItem = instance.get(Calendar.MINUTE);
        }
        return String.format(getCurrentLocale(), FORMAT, valueItem);
    }

    private void updateDefaultNumber() {
        setSelectedItemPosition(findIndexOfMinute(defaultNumber));
    }

    public void setDefaultNumber(int number) {
        this.defaultNumber = number;
        updateDefaultNumber();
    }

    @Override
    public int getDefaultItemPosition() {
        return findIndexOfMinute(defaultNumber);
    }

    private int convertItemToNumber(Object item) {
        return Integer.valueOf(String.valueOf(item));
    }

    public int getCurrentNumber() {
        return convertItemToNumber(adapter.getItem(getCurrentItemPosition()));
    }

    public interface OnNumberSelectedListener {
        void onNumberSelected(WheelNumberPicker picker, int position, int number);

        void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number);

        void onNumberScrolledNewNumber(WheelNumberPicker picker);
    }

}