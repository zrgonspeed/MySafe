package top.cnzrg.mysafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.service.BlackNumberService;
import top.cnzrg.mysafe.service.PhoneAddressService;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.ServiceUtil;
import top.cnzrg.mysafe.utils.SpUtil;
import top.cnzrg.mysafe.view.SettingItemClickView;
import top.cnzrg.mysafe.view.SettingItemView;

/**
 * FileName: SettingActivity
 * Author: ZRG
 * Date: 2019/4/18 12:31
 */
public class SettingActivity extends Activity {
    public static final String PHONE_ADDRESS_SERVICE = "top.cnzrg.mysafe.service.PhoneAddressService";
    public static final String tag = "SettingActivity";
    private String[] mToastStyleDesc;
    private int mToast_style;
    private SettingItemClickView sicv_toast_style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
        initPhoneAddress();
        initToastStyle();
        initToastLocation();

        initBlackNumber();
    }

    /**
     * 拦截黑名单设置
     */
    private void initBlackNumber() {
        final SettingItemView siv_blacknumber = findViewById(R.id.siv_blacknumber);

        // 设置当前是否选中，通过判断当前服务是否正在运行来弄
        boolean running = ServiceUtil.isRunning(this, "top.cnzrg.mysafe.service.BlackNumberService");
        siv_blacknumber.setCheck(running);

        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = siv_blacknumber.isChecked();
                siv_blacknumber.setCheck(!checked);

                // 点击之后的状态
                if (!checked) {
                    // 开启服务
                    startService(new Intent(getApplicationContext(), BlackNumberService.class));
                } else {
                    // 关闭服务
                    stopService(new Intent(getApplicationContext(), BlackNumberService.class));
                }
            }
        });

    }

    /**
     * 归属地位置设置
     */
    private void initToastLocation() {
        SettingItemClickView sicv_toast_location = findViewById(R.id.sicv_toast_location);
        sicv_toast_location.setTitle("归属地提示框的位置");
        sicv_toast_location.setDesc("设置归属地提示框的位置");

        sicv_toast_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开启新的半透明的Activity
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    private void initToastStyle() {
        sicv_toast_style = findViewById(R.id.sicv_toast_style);
        sicv_toast_style.setTitle("设置归属地显示风格");

        // 创建描述文字所在的String类型数组
        mToastStyleDesc = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};

        // Sp获取吐司显示样式的索引值，用于获取描述文字
        mToast_style = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);

        // 通过索引，获取字符串数组中的文字，显示给描述内容的控件
        sicv_toast_style.setDesc(mToastStyleDesc[mToast_style]);

        // 监听点击事件
        sicv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastStyleDialog();
            }
        });
    }

    /**
     * 显示吐司选择样式的对话框
     */
    private void showToastStyleDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.safe360);
        builder.setTitle("请选择归属地样式");

        // 选择单个条目事件监听
        // 1.String数组描述颜色的
        // 2.弹出对话框的时候的选中条目的索引值
        // 3.点击事件,记录选中的索引值,关闭对话框,显示选中色值的文字
        builder.setSingleChoiceItems(
                mToastStyleDesc,
                mToast_style,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 1.记录选中的索引值, which就是
                        SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
                        // 3.显示选中色值的文字
                        sicv_toast_style.setDesc(mToastStyleDesc[which]);

                        // 记录索引值
                        mToast_style = which;

                        // 2.关闭对话框
                        dialog.dismiss();
                    }
                }
        );

        // 取消事件
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void initPhoneAddress() {
        final SettingItemView siv_phone_address = findViewById(R.id.siv_phone_address);

        boolean isRunning = ServiceUtil.isRunning(this, PHONE_ADDRESS_SERVICE);
        siv_phone_address.setCheck(isRunning);

        siv_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 绘制窗口权限申请
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(SettingActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Service跳转到Activity 要加这个标记
                        startActivity(intent);
                        return;
                    } else {
                        //绘ui代码, 这里说明6.0系统已经有权限了
                        Log.i(tag, "onCreate: 有绘图权限了");
                    }
                }

                // 获取之前的选中状态
                boolean checked = siv_phone_address.isChecked();
                // 取反
                siv_phone_address.setCheck(!checked);

                // 点击之后的状态
                if (!checked) {
                    // 开启服务
                    startService(new Intent(getApplicationContext(), PhoneAddressService.class));
                } else {
                    // 关闭服务
                    stopService(new Intent(getApplicationContext(), PhoneAddressService.class));
                }
            }
        });
    }

    private void initUpdate() {
        final SettingItemView siv_update = findViewById(R.id.siv_update);

        // 获取已有的开关状态,用作显示
        boolean open_update = SpUtil.getBoolen(this, ConstantValue.OPEN_UPDATE, false);
        siv_update.setCheck(open_update);

        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 如果之前是选中的，点击过后，变成未选中

                // 获取之前的选中状态
                boolean checked = siv_update.isChecked();
                // 取反
                siv_update.setCheck(!checked);

                // 将取反后的状态存储到相应的sp中
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !checked);
            }
        });

    }


}
