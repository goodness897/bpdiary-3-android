package kr.co.openit.bpdiary.activity.setting.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import kr.co.openit.bpdiary.R;

/**
 * Created by goodn on 2016-08-31.
 */
public class ListCheckView extends FrameLayout implements Checkable {

    private ImageView checkView;

    public ListCheckView(Context context) {
        super(context);
        init();
    }

    public ListCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_check, this);
        checkView = (ImageView)findViewById(R.id.iv_check);
    }

    boolean isChecked = false;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void drawCheck() {
        if (isChecked) {
            checkView.setBackgroundResource(R.drawable.ic_lang_check);
        } else {
            checkView.setBackground(null);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;
            drawCheck();
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }
}
