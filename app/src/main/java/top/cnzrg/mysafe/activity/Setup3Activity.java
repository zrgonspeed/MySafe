package top.cnzrg.mysafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.SpUtil;
import top.cnzrg.mysafe.utils.ToastUtil;

/**
 * FileName: Setup3Activity
 * Author: ZRG
 * Date: 2019/4/25 20:49
 */
public class Setup3Activity extends BaseSetupActivity {

    // 安全号码输入框
    private EditText et_linkman;
    // 选择联系人的按钮
    private Button bt_select_linkman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        init();
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    @Override
    protected void showNextPage() {
        // 点击下一步按钮的时候,需要校验号码
        String phone = et_linkman.getText().toString().trim();

        if (!TextUtils.isEmpty(phone)) {
            // 将联系人号码存储到sp中
            SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);

            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);

            finish();

            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(getApplicationContext(), "请输入安全号码");
        }
    }

    private void initUI() {
        et_linkman = findViewById(R.id.et_linkman);
        bt_select_linkman = findViewById(R.id.bt_select_linkman);

        // 电话号码回显
        String phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        et_linkman.setText(phone);

        // 点击按钮进入选择联系人的界面
        bt_select_linkman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LinkManListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            // 返回到当前界面，接收数据
            String phone = data.getStringExtra("phone");

            // 过滤phone中的横杆-和空格
            phone = phone.replace("-", "").replace(" ", "").trim();

            // 将号码设置到输入框
            et_linkman.setText(phone);

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void init() {
        initUI();
    }
}
