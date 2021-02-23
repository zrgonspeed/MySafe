package top.cnzrg.mysafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.engine.AddressDao;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.SpUtil;

/**
 * FileName: PhoneAddressService
 * Author: ZRG
 * Date: 2019/5/13 13:49
 */
public class PhoneAddressService extends Service {
    private static final String TAG = "PhoneAddressService";
    private TelephonyManager mTelephonyService;
    private MyPhoneStateListener myPSL;
    private View mToastView;
    private WindowManager mWindowManager;
    private String mAddress;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(mAddress);
        }
    };
    private TextView tv_toast;
    private int[] mToastColors;
    private InnerOutCallReceiver mInnerOutCallReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: 开启来电归属地显示服务");

        // 第一次开启服务以后，就需要管理吐司的显示
        // 电话状态的监听
        // 1.电话管理者对象
        mTelephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        // 2.监听电话状态
        // 自定义内部类监听器
        myPSL = new MyPhoneStateListener();
        mTelephonyService.listen(myPSL, PhoneStateListener.LISTEN_CALL_STATE);

        // 获取窗体对象
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 监听播出电话的广播过滤条件,代码注册  (权限)
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

        // 创建广播接收者
        mInnerOutCallReceiver = new InnerOutCallReceiver();
        registerReceiver(mInnerOutCallReceiver, intentFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mTelephonyService == null || myPSL == null) {
            Log.i(TAG, "onDestroy: mTelephonyService 或 myPSL为空");
            return;
        }

        if (mInnerOutCallReceiver != null) {
            // 去电广播接收者的反注册
            unregisterReceiver(mInnerOutCallReceiver);
        }

        // 停止监听
        mTelephonyService.listen(myPSL, PhoneStateListener.LISTEN_NONE);

        // 服务关闭的时候停止监听
        Log.i(TAG, "onDestroy: 停止监听电话状态");

        // 销毁吐司
        Log.i(TAG, "onDestroy: 销毁吐司");
    }

    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

    /**
     * 显示自定义的吐司
     *
     * @param incomingNumber
     */
    public void showToast(String incomingNumber) {
        // Toast.makeText(getApplicationContext(), "来电号码:" + incomingNumber, Toast.LENGTH_LONG).show();

        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.format = PixelFormat.TRANSLUCENT;

        // 在响铃的时候显示吐司
        // 8.0以上又换了
        if (Build.VERSION.SDK_INT >= 26) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;   // 8.0以上
        } else if (Build.VERSION.SDK_INT >= 23) {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;  // 6.0以上
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;  // 低版本的,通过
        }

        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        // 指定吐司所在位置
        params.gravity = Gravity.LEFT + Gravity.TOP;

        // 吐司显示效果,自定义布局 xml -> view 吐司,将吐司挂到WindowManager窗体上显示
        mToastView = View.inflate(this, R.layout.toast_view, null);
        tv_toast = mToastView.findViewById(R.id.tv_toast);

        // 吐司可拖拽事件
        mToastView.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        params.x = params.x + disX;
                        params.y = params.y + disY;

                        // 告知窗体吐司需要按照手势的移动，更新位置
                        mWindowManager.updateViewLayout(mToastView, params);

                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, params.x);
                        SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, params.y);
                        break;
                }

                // 返回false代表不响应事件
                // 既要响应点击事件又要相应拖拽事件
                return false;
            }
        });


        // "透明", "橙色", "蓝色", "灰色", "绿色"
        mToastColors = new int[]{
                R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};

        // 从sp中获取色值文字的索引，匹配图片，用作展示
        int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);

        // 读取sp中存储吐司位置的坐标,设置来电时吐司的坐标
        params.x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        params.y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

        Log.i(TAG, "showToast: x: " + params.x);
        Log.i(TAG, "showToast: y: " + params.y);

        // 设置吐司颜色
        tv_toast.setBackgroundResource(mToastColors[toastStyleIndex]);

        // 在窗体上挂一个View，权限
        mWindowManager.addView(mToastView, mParams);

        Log.i(TAG, "来电号码是: " + incomingNumber);

        // 获取到来电号码后，作号码归属地查询
        query(incomingNumber);
    }


    /**
     * 查询电话归属地
     *
     * @param incomingNumber
     */
    private void query(final String incomingNumber) {
        new Thread() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        // 手动重写，电话状态发生改变会触发的方法
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            Log.i(TAG, "onCallStateChanged: " + state + ":" + phoneNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    // 空闲状态,没有任何获得
                    Log.i(TAG, "onCallStateChanged: 空闲状态");

                    // 挂断电话市，窗体需要移除
                    if (mWindowManager != null && mToastView != null) {
                        mWindowManager.removeView(mToastView);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // 摘机状态
                    Log.i(TAG, "onCallStateChanged: 摘机状态");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    // 响铃状态,显示吐司
                    Log.i(TAG, "onCallStateChanged: 响铃了！！！！！");

                    showToast(phoneNumber);
                    break;
            }
        }
    }

    private class InnerOutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收到此广播后，需要显示自定义的吐司，显示播出归属地号码
            // 获取播出电话号码的字符串
            String phone = getResultData();
            showToast(phone);
        }
    }
}
