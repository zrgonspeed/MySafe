package top.cnzrg.mysafe.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.SpUtil;
import top.cnzrg.mysafe.utils.ToastUtil;
import top.cnzrg.mysafe.view.SettingItemView;

/**
 * FileName: Setup2Activity
 * Author: ZRG
 * Date: 2019/4/25 20:26
 */
public class Setup2Activity extends BaseSetupActivity {
    private static final int REQUEST_CODE = 1002;

    // 要申请的权限
    private static String[] permissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.RECEIVE_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        // 6.0以上需要动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            initPermission(permissions, REQUEST_CODE);
        } else {
            init();
        }

    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);

        finish();

        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    @Override
    protected void showNextPage() {
        String simSerialNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");

        if (!TextUtils.isEmpty(simSerialNumber)) {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();

            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(this, "请绑定SIM卡");
        }
    }

    private void initUI() {
        final SettingItemView siv = findViewById(R.id.siv_sim_bound);

        // 回显，之前可能绑定,读取已有的绑定状态,sp中是否存储了sim卡的序列号
        String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");

        if (TextUtils.isEmpty(sim_number)) {
            siv.setCheck(false);
        } else {
            siv.setCheck(true);
        }

        siv.setOnClickListener(new View.OnClickListener() {
            private String simSerialNumber;

            @Override
            public void onClick(View v) {
                siv.setCheck(!siv.isChecked());

                // 存储序列号
                if (siv.isChecked()) {
                    // 存储

                    // 获取SIM卡序列号
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                    // 防报错
                    if (ActivityCompat.checkSelfPermission(Setup2Activity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        siv.setCheck(false);
                        return;
                    }

                    simSerialNumber = manager.getSimSerialNumber();
                    SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
                } else {
                    // 将存储的序列卡号的节点从sp中删除
                    SpUtil.remove(getApplication(), ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

    @Override
    protected void init() {
        initUI();
    }

}
