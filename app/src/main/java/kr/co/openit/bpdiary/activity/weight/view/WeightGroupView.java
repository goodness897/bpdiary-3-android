package kr.co.openit.bpdiary.activity.weight.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.weight.item.WeightGroupItem;
import kr.co.openit.bpdiary.customview.CustomCircleView;


/**
 * Created by goodn on 2016-12-24.
 */

public class WeightGroupView extends FrameLayout{

    private TextView tvWeight;

    private TextView tvBMI;

    private TextView tvDate;

    private TextView tvTime;

    private CustomCircleView ccvStandard;

    private WeightGroupItem item;

    public WeightGroupView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_weight_group_item, this);

        tvWeight = (TextView)findViewById(R.id.tv_weight);
        tvBMI = (TextView)findViewById(R.id.tv_bmi);
        tvDate = (TextView)findViewById(R.id.tv_date);
        tvTime = (TextView)findViewById(R.id.tv_time);
        ccvStandard = (CustomCircleView)findViewById(R.id.ccv_standard);

    }
    public void setGroupItem(WeightGroupItem item){
        this.item = item;
        tvWeight.setText(item.getStrWeight());
        tvBMI.setText(item.getStrBmi());
        tvDate.setText(item.getStrDate());
        tvTime.setText(item.getStrTime());
        ccvStandard.setCircleColor(getResources().getColor(R.color.color_6aa6e9));
    }

}
