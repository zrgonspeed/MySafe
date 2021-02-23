package top.cnzrg.mysafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * FileName: AddressDao
 * Author: ZRG
 * Date: 2019/5/9 18:13
 */
public class AddressDao {
    // 指定访问数据库的路径
    public static String path = "data/data/" + "top.cnzrg.mysafe" + "/files/address.db";
    private static String tag = "AddressDao";

    /**
     * 开启数据库连接，进行访问
     * 传递一个电话号码，返回一个归属地
     *
     * @param phone
     */
    public static String getAddress(String phone) {
        String address = "未知号码";

        // 正则表达式，匹配手机号
        boolean matches = phone.matches("^1[3-8]\\d{9}");
        if (matches) {
            // 截取号码前7位
            phone = phone.substring(0, 7);

            // 开启数据库连接,只读的形式打开
            SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

            // 数据库查询
            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);

            // 查到即可,不用while
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);
                Log.i(tag, "outkey=" + outkey);

                // 通过data1查询到的结果，作为外键查询data2中的id
                Cursor cursor2 = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);

                if (cursor2.moveToNext()) {
                    address = cursor2.getString(0);
                    Log.i(tag, "address=" + address);
                }
            }
        }
        return address;
    }
}
