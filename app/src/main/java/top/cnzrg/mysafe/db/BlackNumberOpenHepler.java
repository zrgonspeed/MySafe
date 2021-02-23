package top.cnzrg.mysafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * FileName: BlackNumberOpenHepler
 * Author: ZRG
 * Date: 2019/6/22 19:15
 */
public class BlackNumberOpenHepler extends SQLiteOpenHelper {
    public BlackNumberOpenHepler(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库中的表
        db.execSQL("    create table blacknumber(\n" +
                "           _id integer primary key autoincrement,\n" +
                "           phone varchar(20),\n" +
                "           mode varchar(5)\n" +
                "    );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
