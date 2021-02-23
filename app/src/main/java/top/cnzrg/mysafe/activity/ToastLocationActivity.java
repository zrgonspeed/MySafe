package top.cnzrg.mysafe.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.PhoneInfo;
import top.cnzrg.mysafe.utils.SpUtil;

/**
 * FileName: ToastLocationActivity
 * Author: ZRG
 * Date: 2019/5/19 19:11
 */
public class ToastLocationActivity extends Activity {
    private static final String TAG = "ToastLocationActivity";

    private ImageView iv_drag;
    private View bt_text_location1;
    private View bt_text_location2;

    private int mScreenHeight;
    private int mScreenWidth;
    private int mStatusBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);

        initUI();
    }


    private void initUI() {
        // 可拖拽双击居中的图片
        iv_drag = findViewById(R.id.iv_drag);
        bt_text_location1 = findViewById(R.id.bt_text_location1);
        bt_text_location2 = findViewById(R.id.bt_text_location2);

        // 获取屏幕宽高方法
        mScreenHeight = PhoneInfo.getScreenHeight(this.getWindowManager());
        mScreenWidth = PhoneInfo.getScreenWidth(this.getWindowManager());

        // 获取状态栏高度
        mStatusBarHeight = PhoneInfo.getStatusBarHeight(this.getApplicationContext());

        Log.i(TAG, "屏幕高度: " + mScreenHeight);
        Log.i(TAG, "屏幕宽度: " + mScreenWidth);
        Log.i(TAG, "状态栏高度: " + mStatusBarHeight);

        int locationX = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        int locationY = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

        // 设置存储在sp中的归属地吐司照片坐标
        iv_drag.setX(locationX);
        iv_drag.setY(locationY);

        if (locationY > mScreenHeight / 2) {
            bt_text_location2.setVisibility(View.INVISIBLE);
            bt_text_location1.setVisibility(View.VISIBLE);
        } else {
            bt_text_location1.setVisibility(View.INVISIBLE);
            bt_text_location2.setVisibility(View.VISIBLE);
        }

        // 和拖拽事件冲突
        iv_drag.setOnClickListener(new View.OnClickListener() {
            private long[] mHits = new long[2]; // 数组大小代表n击事件
            public static final int CLICK_TIME = 500; // 代表多少毫秒内n击

            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();

                if (mHits[0] >= (SystemClock.uptimeMillis() - CLICK_TIME)) {
                    Log.i(TAG, "onClick: 2击事件");
                    Log.i(TAG, "双击居中");

                    // 新的图片左上角坐标为: x:屏幕宽度/2 - 图片宽度/2
                    // y: 屏幕高度/2 - 图片高度/2

                    int leftX = mScreenWidth / 2 - iv_drag.getWidth() / 2;
                    int leftY = mScreenHeight / 2 - iv_drag.getHeight() / 2;
                    iv_drag.setX(leftX);
                    iv_drag.setY(leftY);

                    // 保存坐标数据
                    SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, (int) iv_drag.getX());
                    SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, (int) iv_drag.getY());
                }
            }
        });

        // 监听某一个控件的拖拽过程(按下(1)，移动(n)，抬起(1))
        iv_drag.setOnTouchListener(new View.OnTouchListener() {
            private int newY;
            private int newX;
            private int moveX;
            private int moveY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        moveX = (int) event.getX();
                        moveY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        newX = (int) (iv_drag.getX() + (event.getX() - moveX));
                        newY = (int) (iv_drag.getY() + (event.getY() - moveY));

                        // 容错处理
                        if (newX < 0 || (newX + iv_drag.getWidth()) > mScreenWidth
                                || newY < 0 || (newY + iv_drag.getHeight()) > (mScreenHeight - mStatusBarHeight)) {
                            return true;
                        }

                        if (newY > mScreenHeight / 2) {
                            bt_text_location2.setVisibility(View.INVISIBLE);
                            bt_text_location1.setVisibility(View.VISIBLE);
                        } else {
                            bt_text_location1.setVisibility(View.INVISIBLE);
                            bt_text_location2.setVisibility(View.VISIBLE);
                        }

                        iv_drag.setTranslationX(newX);
                        iv_drag.setTranslationY(newY);
                        break;
                    case MotionEvent.ACTION_UP:
                        // 存储移动到的位置
                        SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, (int) iv_drag.getX());
                        SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, (int) iv_drag.getY());
                        break;
                }

                // 返回false代表不响应事件
                // 既要响应点击事件又要相应拖拽事件
                return false;
            }
        });

    }
}
