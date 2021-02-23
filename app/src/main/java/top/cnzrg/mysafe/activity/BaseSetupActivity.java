package top.cnzrg.mysafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * FileName: BaseSetupActivity
 * Author: ZRG
 * Date: 2019/5/3 0:54
 */
public abstract class BaseSetupActivity extends BasePermissionActivity {
    // 手势探测器
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            // velocity速度
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 监听手势移动
                if (e1.getX() - e2.getX() > 0) {
                    // 从右往左滑,移动到下一页

                    // 调用子类的下一页方法,抽象方法
                    showNextPage();
                } else if (e1.getX() - e2.getX() < 0) {
                    // 调用子类的上一页方法
                    showPrePage();
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /**
     * 上一页的抽象方法，由子类决定具体跳转到哪个界面
     */
    protected abstract void showPrePage();

    /**
     * 下一页的抽象方法，由子类决定具体跳转到哪个界面
     */
    protected abstract void showNextPage();

    /**
     * 点击下一页按钮的时候，根据子类的showNextPage方法做相应跳转
     * @param view
     */
    public void nextPage(View view) {
        showNextPage();
    }

    /**
     * 点击上一页按钮的时候，根据子类的showPrePage方法做相应跳转
     * @param view
     */
    public void prePage(View view) {
        showPrePage();
    }


    // 监听屏幕上的相应事件(按下1次，移动n次，抬起1次)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 接收多种类型的事件,通过手势处理类
        gestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

}
