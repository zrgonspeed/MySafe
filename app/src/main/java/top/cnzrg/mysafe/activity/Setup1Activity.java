package top.cnzrg.mysafe.activity;

import android.content.Intent;
import android.os.Bundle;

import top.cnzrg.mysafe.R;

/**
 * FileName: Setup1Activity
 * Author: ZRG
 * Date: 2019/4/25 15:09
 */
public class Setup1Activity extends BaseSetupActivity {

    private static final String tag = "Setup1Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    protected void showPrePage() {
        // 空实现
    }

    @Override
    protected void showNextPage() {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);

        finish();

        // 开启平移动画
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
    }

    @Override
    protected void init() {

    }
}
