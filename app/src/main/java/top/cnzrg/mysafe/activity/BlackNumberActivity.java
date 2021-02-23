package top.cnzrg.mysafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.db.dao.BlackNumberDao;
import top.cnzrg.mysafe.db.domain.BlackNumber;
import top.cnzrg.mysafe.utils.ToastUtil;

/**
 * FileName: BlackNumberActivity
 * Author: ZRG
 * Date: 2019/6/22 18:57
 */
public class BlackNumberActivity extends Activity {
    private static final String TAG = "BlackNumberActivity";

    private Button bt_add;
    private ListView lv_blacknumber;

    private BlackNumberDao mDao;
    private List<BlackNumber> mBlackNumbers;

    private Handler mHandler = new Handler() {
        // 4.告知ListView可以去设置数据适配器
        @Override
        public void handleMessage(Message msg) {
            if (mBlackNumberAdapter == null) {
                mBlackNumberAdapter = new BlackNumberAdapter();
                lv_blacknumber.setAdapter(mBlackNumberAdapter);
                return;
            }

            mBlackNumberAdapter.notifyDataSetChanged();
        }
    };
    private BlackNumberAdapter mBlackNumberAdapter;

    /**
     * 默认选中短信
     */
    private int mode = 1;
    private boolean mLoad;
    private int mCount;

    private class BlackNumberAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBlackNumbers.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumbers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // 1.优化 view
            // 2.优化 findViewById, 使用ViewHolder
            // 3. static viewholder

//            if (convertView == null) {
//                view = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
//            } else {
//                view = convertView;
//            }

            BlackNumberViewHolder holder = null;

            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);

                // 减少findViewById的次数
                holder = new BlackNumberViewHolder();

                holder.tv_blacknumber = convertView.findViewById(R.id.tv_blacknumber);
                holder.tv_blacknumber_mode = convertView.findViewById(R.id.tv_blacknumber_mode);
                holder.iv_blacknumber_trash = convertView.findViewById(R.id.iv_blacknumber_trash);

                convertView.setTag(holder);
            } else {
                holder = (BlackNumberViewHolder) convertView.getTag();
            }

            holder.tv_blacknumber.setText(mBlackNumbers.get(position).getPhone());

            int intMode = Integer.parseInt(mBlackNumbers.get(position).getMode());
            switch (intMode) {
                case 1:
                    holder.tv_blacknumber_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_blacknumber_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_blacknumber_mode.setText("拦截所有");
                    break;
                default:
                    break;
            }

            holder.iv_blacknumber_trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 删除对应记录
                    // 删除数据库中的
                    mDao.delete(mBlackNumbers.get(position).getPhone());

                    // 删除集合中的
                    mBlackNumbers.remove(position);

                    // 通知数据适配器
                    if (mBlackNumberAdapter != null) {
                        mBlackNumberAdapter.notifyDataSetChanged();
                    }
                }
            });

            return convertView;
        }


    }

    // ListView优化3, static
    static class BlackNumberViewHolder {
        TextView tv_blacknumber;
        TextView tv_blacknumber_mode;
        ImageView iv_blacknumber_trash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_blacknumber);

        init();
    }

    private void init() {
        initUI();
        initData();
    }


    private void initUI() {
        bt_add = findViewById(R.id.bt_add);
        lv_blacknumber = findViewById(R.id.lv_blacknumber);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });

        // 监听是否滚动到底部
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            // 滚动状态中，状态发生改变的调用
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                AbsListView.OnScrollListener.SCROLL_STATE_FLING;    飞速滚动
//                AbsListView.OnScrollListener.SCROLL_STATE_IDLE;       空闲状态
//                AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;   触摸的滚动状态

                if (mBlackNumbers == null) {
                    return;
                }

                // 条件1，滚动到停止状态
                // 调节2，最后一个条目可见 (最后一个条目的索引值 >= 集合的大小-1)
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        &&
                        lv_blacknumber.getLastVisiblePosition() >= mBlackNumbers.size() - 1
                        && !mLoad
                        ) {
                    // 加载下一页数据 mLoad避免滚动的时候又加载,如前面两个条件满足

                    // true表示正在加载
//                    mLoad = true;
                    Log.i(TAG, "onScrollStateChanged: mCount: " + mCount);
                    // 表中的总数大于集合大小，才加载更多
                    if (mCount <= mBlackNumbers.size()) {
                        Log.i(TAG, "没有数据了！ ");
                        return;
                    }


                    // 加载下一页数据 20, 20
                    new Thread() {
                        @Override
                        public void run() {
                            // 1.获取操作黑名单数据库的对象
                            mDao = BlackNumberDao.getInstance(getApplicationContext());

                            // 2.查询部分数据 (20)
                            List<BlackNumber> moreData = mDao.findByLimit(mBlackNumbers.size());

                            // 3.添加下一页数据的过程
                            mBlackNumbers.addAll(moreData);

                            // 4.通过消息机制告知主线程可以去使用包含数据的集合
                            mHandler.sendEmptyMessage(0);
                        }
                    }.start();

                }
            }

            // 滚动过程中调用的方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void showAddDialog() {
        final PopupWindow popupWindow;

        View view = LayoutInflater.from(this).inflate(R.layout.pop_window, null);
        popupWindow = new PopupWindow(
                view, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );

        Button btOk = view.findViewById(R.id.bt_ok);
        Button btCancel = view.findViewById(R.id.bt_cancel);

        final EditText et_blacknumber = view.findViewById(R.id.et_blacknumber);
        RadioGroup rg_blacknumber_mode = view.findViewById(R.id.rg_blacknumber_mode);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "点击了确定按钮", Toast.LENGTH_SHORT).show();

                // 获取输入框中的电话号码
                String blacknumberStr = et_blacknumber.getText().toString().trim();

                if (TextUtils.isEmpty(blacknumberStr)) {
                    ToastUtil.show(getApplicationContext(), "请输入拦截号码");
                    return;
                }

                // 号码不为空，插入数据库
                mDao.insert(blacknumberStr, String.valueOf(mode));

                // 更新当前黑名单集合,数据库和集合保持同步
                // 方法一: 重新从数据库读
                // 方法二: 手动向集合中插入

                BlackNumber blackNumber = new BlackNumber(blacknumberStr, String.valueOf(mode));

                // 将对象插入到集合最顶部
                mBlackNumbers.add(0, blackNumber);

                // 通知数据适配器刷新
                if (mBlackNumberAdapter != null) {
                    mBlackNumberAdapter.notifyDataSetChanged();
                }

                popupWindow.dismiss();
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "点击了取消按钮", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeBackgroundAlpha(1.0f);
            }
        });

        popupWindow.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popupWindow.showAtLocation(BlackNumberActivity.this.getWindow().getDecorView(),
                Gravity.CENTER, 0, 0
        );


        // 监听选中条目的切换
        rg_blacknumber_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_msg:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;
                    default:
                        Log.i(TAG, "onCheckedChanged: default");
                        break;
                }
            }
        });

        // 背景透明度改变
        changeBackgroundAlpha(0.5f);
    }

    /**
     * value: 1.0 ~ 0.0
     *
     * @param value
     */
    public void changeBackgroundAlpha(float value) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = value;
        getWindow().setAttributes(lp);
    }

    private void initData() {
        // 获取数据库中所有电话号码
        new Thread() {
            @Override
            public void run() {
                // 1.获取操作黑名单数据库的对象
                mDao = BlackNumberDao.getInstance(getApplicationContext());

                // 2.查询部分数据
                mBlackNumbers = mDao.findByLimit(0);

                // 获得表总记录数
                mCount = mDao.getCount();

                // 3.通过消息机制告知主线程可以去使用包含数据的集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }


}
