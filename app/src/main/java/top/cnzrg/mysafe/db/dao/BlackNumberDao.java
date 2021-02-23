package top.cnzrg.mysafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import top.cnzrg.mysafe.db.BlackNumberOpenHepler;
import top.cnzrg.mysafe.db.domain.BlackNumber;

/**
 * FileName: BlackNumberDao
 * Author: ZRG
 * Date: 2019/6/22 19:19
 */
public class BlackNumberDao {
    private final BlackNumberOpenHepler blackNumberOpenHepler;
    // 搞成单例模式
    // 1.私有化构造方法
    // 2.声明一个当前类的对象
    // 3.提供一个方法，如果当前的类对象为空，创建一个新的

    private BlackNumberDao(Context context) {
        // 创建数据库以及其表结构
        blackNumberOpenHepler = new BlackNumberOpenHepler(context);
    }

    private static BlackNumberDao blackNumberDao = null;

    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }

        return blackNumberDao;
    }

    /**
     * 插入一条数据
     *
     * @param phone 拦截的电话号码
     * @param mode  拦截类型(1:短信 2:电话 3:所有)
     */
    public void insert(String phone, String mode) {
        // 开启数据库，准备写入操作
        SQLiteDatabase db = blackNumberOpenHepler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);

        db.insert("blacknumber", null, values);

        db.close();
    }

    /**
     * 删除一条数据, 按照电话号码来删
     *
     * @param phone
     */
    public void delete(String phone) {
        // 开启数据库，准备写入操作
        SQLiteDatabase db = blackNumberOpenHepler.getWritableDatabase();

        db.delete("blacknumber", "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 更新一条数据
     *
     * @param phone 要更新的电话号码
     * @param mode  更新之后的拦截类型(1:短信 2:电话 3:所有)
     */
    public void update(String phone, String mode) {
        // 开启数据库，准备写入操作
        SQLiteDatabase db = blackNumberOpenHepler.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("mode", mode);

        db.update("blacknumber", values, "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 查询blacknumber表中的所有数据
     *
     * @return
     */
    public List<BlackNumber> findAll() {
        // 开启数据库，准备写入操作
        SQLiteDatabase db = blackNumberOpenHepler.getWritableDatabase();

        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");

        ArrayList<BlackNumber> blackNumbers = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackNumber blackNumber = new BlackNumber(cursor.getString(0), cursor.getString(1));
            blackNumbers.add(blackNumber);
        }
        cursor.close();
        db.close();

        return blackNumbers;
    }

    /**
     * 分页查询
     *
     * @param index
     * @return
     */
    public List<BlackNumber> findByLimit(int index) {
        // 开启数据库，准备写入操作
        SQLiteDatabase db = blackNumberOpenHepler.getWritableDatabase();

        Cursor cursor = db.rawQuery("select phone, mode from blacknumber order by _id desc limit ?, 20;", new String[]{index + ""});

        ArrayList<BlackNumber> blackNumbers = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackNumber blackNumber = new BlackNumber(cursor.getString(0), cursor.getString(1));
            blackNumbers.add(blackNumber);
        }

        cursor.close();
        db.close();

        return blackNumbers;
    }

    /**
     * blacknumber中的条目个数，返回0代表没有数据或查询异常
     *
     * @return
     */
    public int getCount() {
        SQLiteDatabase db = blackNumberOpenHepler.getWritableDatabase();

        Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);

        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    /**
     * 根据电话号码查询对应的拦截模式
     * 0代表没有拦截, 1: 短信  2：电话 3：所有
     * @param phone
     * @return
     */
    public int getMode(String phone) {
        SQLiteDatabase db = blackNumberOpenHepler.getWritableDatabase();

        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null, null);

        int mode = 0;
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return mode;
    }
}
