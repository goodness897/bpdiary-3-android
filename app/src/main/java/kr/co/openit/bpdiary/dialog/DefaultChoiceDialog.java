package kr.co.openit.bpdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.interfaces.IChoiceDialog;

/**
 * Created by hwangem on 2017-01-18.
 */

public class DefaultChoiceDialog extends Dialog implements OnClickListener {

    private IChoiceDialog iDefaultDialog;

    private Context context;

    private LinearLayout llFirstLayout;

    private LinearLayout llSecondLayout;

    private CheckBox cbFirstItem;

    private CheckBox cbSecondItem;

    private TextView tvFirstText;

    private TextView tvSecondText;

    private int chkItem;

    private String firstItem;

    private String secondItem;

    private String titleItem;

    private TextView tvTitleView;

    public DefaultChoiceDialog(Context context,
                               int chkItem,
                               String titleItem,
                               String firstItem,
                               String secondItem,
                               IChoiceDialog iDefaultDialog) {
        super(context, R.style.MyDialog);
        this.titleItem = titleItem;
        this.iDefaultDialog = iDefaultDialog;
        this.chkItem = chkItem;
        this.firstItem = firstItem;
        this.secondItem = secondItem;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        //        lpWindow.gravity = Gravity.CENTER;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_choice);

        llFirstLayout = (LinearLayout)findViewById(R.id.ll_first_item);
        llSecondLayout = (LinearLayout)findViewById(R.id.ll_second_item);
        cbFirstItem = (CheckBox)findViewById(R.id.cb_first_item);
        cbSecondItem = (CheckBox)findViewById(R.id.cb_second_item);
        tvFirstText = (TextView)findViewById(R.id.tv_first_item);
        tvSecondText = (TextView)findViewById(R.id.tv_second_item);
        tvTitleView = (TextView)findViewById(R.id.tv_dialog_title);

        tvFirstText.setText(firstItem);
        tvSecondText.setText(secondItem);
        tvTitleView.setText(titleItem);

        if (chkItem == 0) {
            cbFirstItem.setChecked(true);
        } else {
            cbSecondItem.setChecked(true);
        }

        cbFirstItem.setOnClickListener(this);
        cbSecondItem.setOnClickListener(this);

        llFirstLayout.setOnClickListener(this);
        llSecondLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_first_item :
                cbFirstItem.setChecked(true);
                cbSecondItem.setChecked(false);
                chkItem = 0;

                iDefaultDialog.onConfirm(chkItem);
                dismiss();
                break;

            case R.id.cb_second_item :
                cbFirstItem.setChecked(false);
                cbSecondItem.setChecked(true);
                chkItem = 1;

                iDefaultDialog.onConfirm(chkItem);
                dismiss();
                break;

            case R.id.ll_first_item:
                cbFirstItem.setChecked(true);
                cbSecondItem.setChecked(false);
                chkItem = 0;

                iDefaultDialog.onConfirm(chkItem);
                dismiss();
                break;

            case R.id.ll_second_item:
                cbFirstItem.setChecked(false);
                cbSecondItem.setChecked(true);
                chkItem = 1;

                iDefaultDialog.onConfirm(chkItem);
                dismiss();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }
}
