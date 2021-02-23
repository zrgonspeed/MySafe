package top.cnzrg.mysafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * FileName: FocusTextView
 * Author: ZRG
 * Date: 2019/4/17 1:42
 * 能够获取焦点的自定义控件
 */
public class FocusTextView extends android.support.v7.widget.AppCompatTextView {
    // 通过java代码创建控件
    public FocusTextView(Context context) {
        super(context);
    }

    // 由系统调用(带属性)           xml->对象
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 由系统调用(带属性) + 样式文件        xml->对象
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 重写获取焦点的方法
    @Override
    public boolean isFocused() {
        return true;
    }
}
