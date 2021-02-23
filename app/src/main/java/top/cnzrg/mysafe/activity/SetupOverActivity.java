package top.cnzrg.mysafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.SpUtil;

/**
 * FileName: SetupOverActivity
 * Author: ZRG
 * Date: 2019/4/25 14:56
 */
public class SetupOverActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 密码输入成功,并且4个导航界面设置完成->停留在设置完成功能列表界面
        // 密码输入成功,并且4个导航界面没有设置完成->跳转到导航界面1

        boolean setup_over = SpUtil.getBoolen(this, ConstantValue.SETUP_OVER, false);

        if (setup_over) {
            // 设置导航界面完成
            setContentView(R.layout.activity_setup_over);

            initUI();

        } else {
            // 设置导航界面未完成,跳转到导航界面1
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);

            finish();
        }
    }

    private void initUI() {
        TextView tv_safe_phone = findViewById(R.id.tv_safe_phone);
        TextView tv_reset_setup = findViewById(R.id.tv_reset_setup);

        // 设置成已存储在sp中的联系人号码
        String phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        tv_safe_phone.setText(phone);

        // 设置条目被点击
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);

                finish();
            }
        });
    }
}
