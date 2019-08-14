package com.northmeter.camerameterwithblue.widget;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.northmeter.camerameterwithblue.R;
import com.northmeter.camerameterwithblue.widget.time.WheelView;
import com.northmeter.camerameterwithblue.widget.time.adapter.NumericWheelAdapter;

import java.util.Calendar;

/**
 * Created by dyd on 2017/10/26.
 */
public class TimeDialog extends AlertDialog {
    Context context;
    private WheelView hour, mins, second;

    public TimeDialog(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 显示时间
     */
    public void showTimeDialog(final SetTimeData setTimeData){
        final AlertDialog dialog = new Builder(context)
                .create();
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.timepick);
        // 设置宽高
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.AnimBottom_Dialog);

        hour = (WheelView) window.findViewById(R.id.hour);
        initHour();
        mins = (WheelView) window.findViewById(R.id.mins);
        initMins();
        second = (WheelView) window.findViewById(R.id.second);
        initSecond();

        Calendar c = Calendar.getInstance();
        // 设置当前时间
        hour.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(c.get(Calendar.MINUTE));
        second.setCurrentItem(c.get(Calendar.SECOND));

        hour.setVisibleItems(7);
        mins.setVisibleItems(7);
        second.setVisibleItems(7);

        // 设置监听
        Button ok = (Button) window.findViewById(R.id.set);
        Button cancel = (Button) window.findViewById(R.id.cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int Hour = hour.getCurrentItem();
                int Min = mins.getCurrentItem();
                int Sec = second.getCurrentItem();

                String str = String.format("%02d", Hour) + ":"
                        + String.format("%02d", Min) + ":"
                        + String.format("%02d", Sec);
                setTimeData.setTimeData(str);
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
    }

    /**
     * 初始化时
     */
    private void initHour() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,0, 23, "%02d");
        numericWheelAdapter.setLabel(" 时");
        //		numericWheelAdapter.setTextSize(15);  设置字体大小
        hour.setViewAdapter(numericWheelAdapter);
        hour.setCyclic(true);
    }

    /**
     * 初始化分
     */
    private void initMins() {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,0, 59, "%02d");
        numericWheelAdapter.setLabel(" 分");
//		numericWheelAdapter.setTextSize(15);  设置字体大小
        mins.setViewAdapter(numericWheelAdapter);
        mins.setCyclic(true);
    }

    /**
     * 初始化秒*/
    private void initSecond(){
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,0, 59, "%02d");
        numericWheelAdapter.setLabel(" 秒");
//		numericWheelAdapter.setTextSize(15);  设置字体大小
        second.setViewAdapter(numericWheelAdapter);
        second.setCyclic(true);

    }

    public interface SetTimeData{
        void setTimeData(String timeData);
    }



}
