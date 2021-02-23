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
 * FileName: SettingItemView
 * Author: ZRG
 * Date: 2019/4/18 12:55
 */
public class SettingItemView extends RelativeLayout {

    private final String NAMESPACE = "http://schemas.android.com/apk/res/top.cnzrg.mysafe";
    private CheckBox cb_box;
    private TextView tv_setting_desc;
    private static final String tag = "SettingItemView";
    private String mDesctitle;
    private String mDescoff;
    private String mDescon;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // xml -> View 将设置界面的一个条目转换成View对象
        View.inflate(context, R.layout.setting_item_view, this);

        TextView tv_setting_title = this.findViewById(R.id.tv_setting_title);
        tv_setting_desc = this.findViewById(R.id.tv_setting_desc);
        cb_box = this.findViewById(R.id.cb_box);

        //        View view = View.inflate(context, R.layout.setting_item_view, null);
        //        this.addView(view);

        // 获取自定义以及原生属性的操作
        initAttrs(attrs);

        // 标题赋值
        tv_setting_title.setText(mDesctitle);
    }

    /**
     * 构造方法中维护好的属性集合
     *
     * @param attrs
     */
    private void initAttrs(AttributeSet attrs) {
        // 获取xml中的属性个数
        //        Log.i(tag, "attrs.getAttributeCount() = " + attrs.getAttributeCount());
        //
        //        // 获取属性名称以及属性值
        //        for (int i = 0; i < attrs.getAttributeCount(); i++) {
        //            Log.i(tag, "name = " + attrs.getAttributeName(i));
        //            Log.i(tag, "value = " + attrs.getAttributeValue(i));
        //            Log.i(tag, "--------------------------------------------------------");
        //        }

        // 通过命名空间+属性名称获取
        mDesctitle = attrs.getAttributeValue(NAMESPACE, "desctitle");
        mDescoff = attrs.getAttributeValue(NAMESPACE, "descoff");
        mDescon = attrs.getAttributeValue(NAMESPACE, "descon");

        Log.i(tag, mDesctitle);
        Log.i(tag, mDescoff);
        Log.i(tag, mDescon);
    }

    /**
     * 返回当前SettingItemView是否选中状态 true开启 false关闭,跟checkbox的选中状态一致
     *
     * @return
     */
    public boolean isChecked() {
        System.out.println(cb_box.isChecked());
        return cb_box.isChecked();
    }

    /**
     * @param checked 是否作为开启的变量，点击过程中传递
     */
    public void setCheck(boolean checked) {
        cb_box.setChecked(checked);

        if (checked) {
            tv_setting_desc.setText(mDescon);
        } else {
            tv_setting_desc.setText(mDescoff);
        }
    }
}
