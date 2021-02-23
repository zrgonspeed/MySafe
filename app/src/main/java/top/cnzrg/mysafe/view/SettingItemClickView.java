package top.cnzrg.mysafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import top.cnzrg.mysafe.R;

/**
 * 带箭头的设置项
 */
public class SettingItemClickView extends RelativeLayout {
    private static final String tag = "SettingItemClickView";
    private TextView tv_setting_desc;
    private TextView tv_setting_title;


    public SettingItemClickView(Context context) {
        this(context, null);
    }

    public SettingItemClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // xml -> View 将设置界面的一个条目转换成View对象
        View.inflate(context, R.layout.setting_item_click_view, this);

        tv_setting_title = this.findViewById(R.id.tv_setting_title);
        tv_setting_desc = this.findViewById(R.id.tv_setting_desc);

    }

    /**
     * 设置标题内容
     *
     * @param title
     */
    public void setTitle(String title) {
        tv_setting_title.setText(title);
    }

    /**
     * 设置描述内容
     *
     * @param desc
     */
    public void setDesc(String desc) {
        tv_setting_desc.setText(desc);
    }
}
