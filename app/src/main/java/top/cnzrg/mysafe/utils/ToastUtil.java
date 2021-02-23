package top.cnzrg.mysafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * FileName: ToastUtil
 * Author: ZRG
 * Date: 2019/4/15 13:33
 */
public class ToastUtil {

    /**
     * @param context   上下文环境
     * @param msg   文本内容
     */
    public static void show(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
