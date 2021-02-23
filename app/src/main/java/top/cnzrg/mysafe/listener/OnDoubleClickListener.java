package top.cnzrg.mysafe.listener;

import android.util.Log;
import android.view.View;

/**
 * FileName: OnDoubleClickListener
 * Author: ZRG
 * Date: 2019/5/20 20:21
 */
public abstract class OnDoubleClickListener implements View.OnClickListener {
    private long startTime = 0;

    @Override
    public void onClick(View v) {
        if (startTime != 0) {
            long endTime = System.currentTimeMillis();

            // 判断是否在指定时间内双击
            if (endTime - startTime < 500) {
                onDoubleClick(v);
            }

        }
        startTime = System.currentTimeMillis();
    }

    public abstract void onDoubleClick(View v);
}
