package kr.co.openit.bpdiary.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BackPressEditText extends EditText {

    private OnBackPressListener _listener;

    private Button btnUp;

    private Button btnDown;

    public BackPressEditText(Context context) {
        super(context);
    }

    public BackPressEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackPressEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && _listener != null) {
            btnUp.setVisibility(View.GONE);
            btnDown.setVisibility(View.VISIBLE);
        }

        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnBackPressListener(OnBackPressListener $listener, Button btn1, Button btn2) {
        _listener = $listener;
        btnUp = btn1;
        btnDown = btn2;
    }

    public interface OnBackPressListener {

        public void onBackPress();
    }
}
