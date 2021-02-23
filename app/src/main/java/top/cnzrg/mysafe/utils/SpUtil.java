package top.cnzrg.mysafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * FileName: SpUtil
 * Author: ZRG
 * Date: 2019/4/21 0:13
 */
public class SpUtil {

    private static SharedPreferences sp;

    /**
     * 写入boolean变量至sp中
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putBoolean(Context context, String key, boolean value) {
        // 存储节点文件名称，读写方式
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 读取boolean变量的值
     *
     * @param context
     * @param key
     * @param defValue 没有此节点的默认值
     * @return 默认值或此节点读取到的值
     */
    public static boolean getBoolen(Context context, String key, boolean defValue) {
        // 存储节点文件名称，读写方式
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }


    public static void putString(Context context, String key, String value) {
        // 存储节点文件名称，读写方式
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    public static String getString(Context context, String key, String defValue) {
        // 存储节点文件名称，读写方式
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    /**
     * 移除节点
     *
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }

    /**
     * 获取索引
     *
     * @param context
     * @param key
     * @param defValue
     */
    public static int getInt(Context context, String key, int defValue) {
        // 存储节点文件名称，读写方式
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }

    public static void putInt(Context context, String key, int value) {
        // 存储节点文件名称，读写方式
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }
}
