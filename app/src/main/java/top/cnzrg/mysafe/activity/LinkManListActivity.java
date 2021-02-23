package top.cnzrg.mysafe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.utils.ToastUtil;

/**
 * FileName: LinkManListActivity
 * Author: ZRG
 * Date: 2019/4/29 12:45
 */
public class LinkManListActivity extends Activity {

    private static final String tag = "LinkManListActivity";
    // 联系人的列表视图
    private ListView lv_linkman;

    // 权限请求码
    private static final int REQUEST_CODE = 1003;

    // 联系人列表数据
    private List<HashMap<String, String>> contactList = new ArrayList<>();
    // 联系人数据适配器
    private ContactAdapter mContactAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 填充数据适配器
            mContactAdapter = new ContactAdapter();
            lv_linkman.setAdapter(mContactAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkmanlist);

        // 6.0以上需要动态申请权限
        /*if (Build.VERSION.SDK_INT >= 23) {
            Log.i(tag, "我666666666");
            int REQUEST_CODE = 103;
            String[] permissions = {
                    Manifest.permission.READ_CONTACTS
            };

            //验证是否许可权限
            for (String str : permissions) {
                Log.i(tag, "进入循环");
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    Log.i(tag, "申请权限");

                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE);
                }
            }
        }*/

        // 6.0以上需要动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            initPermission();
        } else {
            init();
        }
    }

    private void init() {
        initUI();
        initData();
    }

    private void initPermission() {
        // 要申请的权限
        String[] permissions = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };

        // 调用permissionAllGranted方法，判断是否需要申请权限
        boolean permission = permissionAllGranted(permissions);

        if (!permission) {
            // 没有权限时
            // 请求权限
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
        } else {
            //有权限时
            init();
        }
    }

    // 权限是否被授权了
    private boolean permissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则不能完成你需要的功能就返回 false
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) { // 将这里的requestCode改成你的任意数字，上边的一致就行
            boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;//是否授权，可以根据permission作为标记
            if (granted) {
                // 授权了
                init();
            } else {
                // 没有授予，比如可以给用户弹窗告诉用户，你拒绝了权限，所以不能实现某个功能，如果想实现你可以跳转到设置，如果不想实现那么直接把这个弹窗取消
                ToastUtil.show(getApplicationContext(), "您拒绝了授权");
                finish();
            }
        }
    }


    // 获取系统联系人数据
    private void initData() {
        Log.i(tag, "我是initData()噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢噢");

        // 用子线程跑耗时操作
        new Thread() {
            @Override
            public void run() {
                // 1.获取内容解析器的对象
                ContentResolver contentResolver = getContentResolver();
                // 2.查询系统联系人数据库(要加读取联系人权限)
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null,   // 查询条件 "a = ?"
                        null,
                        null
                );

                contactList.clear();

                // 3.循环游标，直到没有数据
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);

                    if (id == null)
                        continue;

                    // Log.i(tag, id);

                    // 4. 根据用户唯一性id，查询data表和mimetype表生成的视图，获取data和mimetype字段
                    Cursor indexCursor = contentResolver.query(
                            Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ?",
                            new String[]{id},
                            null
                    );

                    HashMap<String, String> hashMap = new HashMap<>();

                    // 5.获取每一个联系人的电话号码以及姓名，数据类型
                    while (indexCursor.moveToNext()) {
                        String data = indexCursor.getString(0);
                        String mimetype = indexCursor.getString(1);

                        Log.i(tag, "data = " + data);  // data1字段
                        Log.i(tag, "mimetype = " + mimetype); // mimetype字段

                        if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("phone", data);
                            }
                        } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("name", data);
                            }
                        }
                    }

                    indexCursor.close();
                    contactList.add(hashMap);
                }

                cursor.close();

                // 消息机制,简化了,handleMessage()方法一定会执行
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        lv_linkman = findViewById(R.id.lv_linkman);
        lv_linkman.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 1.获取点中条目的索引指向集合中的对象
                if (mContactAdapter != null) {
                    HashMap<String, String> hashMap = mContactAdapter.getItem(position);

                    // 2.获取电话号码
                    String phone = hashMap.get("phone");

                    // 3.需要将电话号码给第三个导航界面使用
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);

                    // 4.数据返回了，关闭该界面
                    finish();
                }
            }
        });
    }

    /**
     * 联系人数据适配器
     */
    private class ContactAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);

            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_phone = view.findViewById(R.id.tv_phone);

            tv_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));

            return view;
        }
    }

}
