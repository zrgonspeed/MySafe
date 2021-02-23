package top.cnzrg.mysafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.SpUtil;
import top.cnzrg.mysafe.utils.ToastUtil;

/**
 * FileName: Setup4Activity
 * Author: ZRG
 * Date: 2019/4/25 21:29
 */
public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_open_safe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

        init();
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);

        finish();

        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    @Override
    protected void showNextPage() {
        ToastUtil.show(this, "当前是最后一步啦!");
    }

    @Override
    public void nextPage(View view) {
        boolean open_security = SpUtil.getBoolen(this, ConstantValue.OPEN_SECURITY, false);

        if (open_security) {
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);

            SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);
            finish();

            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);

        } else {
            ToastUtil.show(this, "您没有开启防盗保护！");
        }
    }

    private void initUI() {
        cb_open_safe = findViewById(R.id.cb_open_safe);

        // 数据回显
        boolean open_security = SpUtil.getBoolen(this, ConstantValue.OPEN_SECURITY, false);
        cb_open_safe.setChecked(open_security);

        if (open_security) {
            cb_open_safe.setText("安全设置已开启");
        } else {
            cb_open_safe.setText("安全设置已关闭");
        }

        // 点击事件,监听状态改变
        cb_open_safe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtil.putBoolean(Setup4Activity.this, ConstantValue.OPEN_SECURITY, isChecked);

                if (cb_open_safe.isChecked()) {
                    cb_open_safe.setText("安全设置已开启");
                } else {
                    cb_open_safe.setText("安全设置已关闭");
                }
            }
        });
    }

    @Override
    protected void init() {
        initUI();
    }
}
