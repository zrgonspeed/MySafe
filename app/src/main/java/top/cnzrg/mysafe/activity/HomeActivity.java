package top.cnzrg.mysafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.MD5Util;
import top.cnzrg.mysafe.utils.SpUtil;
import top.cnzrg.mysafe.utils.ToastUtil;

/**
 * FileName: HomeActivity
 * Author: ZRG
 * Date: 2019/4/15 13:11
 */
public class HomeActivity extends Activity {

    private GridView gv_home;
    private String[] mTitleStr;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initUI();
        initData();
    }

    private void initData() {
        // 准备gridview的数据9组文字和9张图片
        mTitleStr = new String[]{
                "手机防盗", "通信卫士", "软件管理",
                "进程管理", "流量统计", "手机杀毒",
                "缓存清理", "高级工具", "设置中心"
        };
        mDrawableIds = new int[]{
                R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
                R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
                R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings,
        };
        // 九宫格控件设置数据适配器
        gv_home.setAdapter(new HomeAdapter());

        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // 点中列表条目的索引
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // 开启对话框
                        showDialog0();
                        break;
                    case 1:
                        // 通信卫士模块
                        startActivity(new Intent(getApplicationContext(), BlackNumberActivity.class));
                        break;
                    case 7:
                        // 进入高级工具界面
                        Intent intent7 = new Intent(getApplicationContext(), AToolActivity.class);
                        startActivity(intent7);
                        break;
                    case 8:
                        Intent intent8 = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent8);
                        break;

                }
            }
        });
    }

    private void showDialog0() {
        // 判断本地是否有存储密码(sp 字符串类型)
        String psd = SpUtil.getString(this, ConstantValue.SAFE_PSD, "");

        if (TextUtils.isEmpty(psd)) {
            // 1.初始设置密码对话框
            showSetPsdDialog();

        } else {
            // 2.确认密码对话框
            showConfirmPsdDialog();
        }
    }

    /**
     * 初次设置密码
     * 自定义对话框的样式
     */
    private void showSetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        final View view = View.inflate(this, R.layout.dialog_safe_set_psd, null);

        // 设置自定义对话框界面
        dialog.setView(view);
        dialog.show();

        Button bt_safe_set_submit = view.findViewById(R.id.bt_safe_set_submit);
        Button bt_safe_set_cancel = view.findViewById(R.id.bt_safe_set_cancel);

        // 确认按钮事件
        bt_safe_set_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_safe_set_psd = view.findViewById(R.id.et_safe_set_psd);
                EditText et_safe_set_psd_confirm = view.findViewById(R.id.et_safe_set_psd_confirm);

                String psd = et_safe_set_psd.getText().toString();
                String confirmPsd = et_safe_set_psd_confirm.getText().toString();

                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
                    // 比对两次输入的密码
                    if (psd.equals(confirmPsd)) {
                        // 进入手机防盗模块,开启一个新的Activity
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();

                        // MD5加密后的字符串
                        String encoderPsd = MD5Util.encoder(psd);

                        SpUtil.putString(getApplicationContext(), ConstantValue.SAFE_PSD, encoderPsd);
                    } else {
                        ToastUtil.show(getApplicationContext(), "两次密码不一致");
                    }

                } else {
                    // 提示用户密码输入有为空的情况
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }

            }
        });

        // 取消按钮事件
        bt_safe_set_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 输入已有密码
     */
    private void showConfirmPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        final View view = View.inflate(this, R.layout.dialog_safe_confirm_psd, null);

        // 设置自定义对话框界面
        dialog.setView(view);
        dialog.show();

        Button bt_safe_confirm_submit = view.findViewById(R.id.bt_safe_confirm_submit);
        Button bt_safe_confirm_cancel = view.findViewById(R.id.bt_safe_confirm_cancel);

        final EditText et_safe_confirm_psd = view.findViewById(R.id.et_safe_confirm_psd);

        bt_safe_confirm_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_safe_confirm_psd.getText().toString();

                if (!TextUtils.isEmpty(psd)) {
                    String realPsd = SpUtil.getString(getApplicationContext(), ConstantValue.SAFE_PSD, psd);

                    // MD5加密后的字符串
                    String encoderPsd = MD5Util.encoder(psd);

                    if (encoderPsd.equals(realPsd)) {
                        // 进入手机防盗模块,开启一个新的Activity
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);

                        dialog.dismiss();
                    } else {
                        ToastUtil.show(getApplicationContext(), "密码错误");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });

        bt_safe_confirm_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    private void initUI() {
        gv_home = findViewById(R.id.gv_home);

    }

    private class HomeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = view.findViewById(R.id.tv_title);
            ImageView iv_icon = view.findViewById(R.id.iv_icon);

            tv_title.setText(mTitleStr[position]);
            iv_icon.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }
}
