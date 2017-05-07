package kr.co.openit.bpdiary.dialog;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;

/**
 * Created by srpark on 2016-12-12.
 */

public class DialogPermissions extends Dialog {

    private IDefaultDialog iDefaultDialog;

    private LinearLayout llPopUpOk, llPopUpCancel;

    private TextView tvMsg;

    private TextView tvPopUpOk;

    private TextView tvPopUpCancel;

    private Context context;

    private boolean b = false;

    private boolean isSuccess = false;

    private String msg;

    private String title;

    private TextView tvTitle;

    @Override
    public void onBackPressed() {
    }

    public DialogPermissions(Context context) {
        super(context, R.style.PermissonDialog);
    }

    public DialogPermissions(Context context, String msg, IDefaultDialog iDefaultDialog) {
        super(context, R.style.PermissonDialog);
        this.iDefaultDialog = iDefaultDialog;
        this.context = context;
        if (msg.equals(Manifest.permission.GET_ACCOUNTS)) {
            this.title = context.getString(R.string.pop_up_permission_accounts_title);
            this.msg = context.getString(R.string.pop_up_permission_accounts_msg);
        } else if (msg.equals(Manifest.permission.READ_EXTERNAL_STORAGE)
                   || msg.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            this.title = context.getString(R.string.pop_up_permission_storage_title);
            this.msg = context.getString(R.string.pop_up_permission_storage_msg);
        }
        //        else if (msg.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
        //            this.title = context.getString(R.string.pop_up_permission_location_title);
        //            this.msg = context.getString(R.string.pop_up_permission_location_msg);
        //        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.gravity = Gravity.CENTER;
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.dialog_permission);

        setLayout();

        tvTitle.setText(title);
        tvMsg.setText(msg);
        tvPopUpOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iDefaultDialog.onConfirm();
                cancel();
            }
        });
        tvPopUpCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                iDefaultDialog.onCancel();
                cancel();
            }
        });
    }

    private void setLayout() {
        tvMsg = (TextView)findViewById(R.id.tv_msg);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvPopUpOk = (TextView)findViewById(R.id.tv_pop_up_ok);
        tvPopUpCancel = (TextView)findViewById(R.id.tv_pop_up_cancel);
    }
}
