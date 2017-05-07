package kr.co.openit.bpdiary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import kr.co.openit.bpdiary.R;

/**
 * 다이얼로그
 */
public class CustomProgressDialog extends Dialog {

    private ImageView imgMeasuring;

    private Context context;

    /**
     * 측정중 애니메이션
     */
    private static AnimationDrawable aniMeasuring;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.custom_dialog_progress);
        imgMeasuring = (ImageView)findViewById(R.id.progress_img_loading);

        aniMeasuring = (AnimationDrawable)imgMeasuring.getBackground();
        aniMeasuring.start();
    }
//
//    @Override
//    public void show() {
//        aniMeasuring.start();
//        super.show();
//    }
//
//    @Override
//    public void hide() {
//        aniMeasuring.stop();
//        super.hide();
//    }
//
//    @Override
//    public void dismiss() {
//        aniMeasuring.stop();
//        super.dismiss();
//    }
}
