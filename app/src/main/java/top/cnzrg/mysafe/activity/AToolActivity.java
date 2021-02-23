package top.cnzrg.mysafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import top.cnzrg.mysafe.R;

/**
 * FileName: AToolActivity
 * Author: ZRG
 * Date: 2019/5/8 10:31
 */
public class AToolActivity extends Activity {

    private TextView tv_ownership;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);

        initUI();
        initPhoneAddress();
    }

    private void initUI() {
        tv_ownership = findViewById(R.id.tv_ownership);

        tv_ownership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QueryPhoneAddressActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initPhoneAddress() {

    }
}
