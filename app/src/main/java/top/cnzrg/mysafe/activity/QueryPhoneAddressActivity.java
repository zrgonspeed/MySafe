package top.cnzrg.mysafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.engine.AddressDao;

/**
 * FileName: QueryPhoneAddressActivity
 * Author: ZRG
 * Date: 2019/5/8 11:13
 */
public class QueryPhoneAddressActivity extends Activity {
    private static String tag = "QueryPhoneAddressActivity";
    private TextView et_phone;
    private TextView tv_query_result;
    private String mAddress;
    private String phone = "";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 控件使用查询结果
            tv_query_result.setText(mAddress);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_phone_address);

        initUI();
    }

    private void initUI() {
        et_phone = findViewById(R.id.et_phone);
        Button bt_query = findViewById(R.id.bt_query);
        tv_query_result = findViewById(R.id.tv_query_result);

        // 点击按钮查询
        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = et_phone.getText().toString().trim();

                // 空字符串就抖动
                if (TextUtils.isEmpty(phone)) {
                    Animation shake = AnimationUtils.loadAnimation(QueryPhoneAddressActivity.this, R.anim.shake);
                    et_phone.startAnimation(shake);

                    // 手机振动效果
                    Vibrator vibratorService = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // vibratorService.vibrate(2000);  // 过时方法，振动毫秒值
                    // (振动规则(不振动时间,振动时间,不振动时间,振动时间...)), -1代表不重复
                    vibratorService.vibrate(new long[]{2000, 5000, 2000, 10000}, -1);  // 振动毫秒值,振动规则

                    //vibratorService.vibrate(VibrationEffect.createOneShot(2000,1));  // 新方法api26以上才能用


                    // 自定义插补器
                    /*shake.setInterpolator(new Interpolator() {
                        // y = ax + b
                        @Override
                        public float getInterpolation(float x) {

                            return 0;
                        }
                    });*/
                } else {
                    query(phone);
                }
            }
        });

        // 实时查询,监听输入框的文本变化
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(tag, "改变之前");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(tag, "改变！");

                phone = et_phone.getText().toString().trim();
                query(phone);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(tag, "改变之后");
            }
        });

    }

    /**
     * 耗时操作
     * 获取电话号码归属地
     *
     * @param phone
     */
    private void query(final String phone) {
        // 查询数据库是耗时操作，要放到子线程中
        new Thread() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                // 消息机制,告知主线程查询结束，可以去使用查询结果
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
