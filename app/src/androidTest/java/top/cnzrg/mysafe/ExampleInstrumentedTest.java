package top.cnzrg.mysafe;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;

import top.cnzrg.mysafe.db.dao.BlackNumberDao;
import top.cnzrg.mysafe.db.domain.BlackNumber;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("top.cnzrg.mysafe", appContext.getPackageName());
    }

    @Test
    public void insert() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);
//        dao.insert("8888-8888", "电话");

        Log.i("单元测试", "insert: ");

        for (int i = 0; i < 100; i++) {
            if (i < 10) {
                dao.insert("1234567890" + i, 1 + new Random().nextInt(3) + "");
            } else {
                dao.insert("123456780" + i, 1 + new Random().nextInt(3) + "");

            }
        }
    }

    @Test
    public void delete() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);
        dao.delete("777-7777");

        Log.i("单元测试", "delete: ");
    }

    @Test
    public void update() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);
        dao.update("8888-8888", "电脑");

        Log.i("单元测试", "update: ");
    }

    @Test
    public void selectAll() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);

        Log.i("单元测试", "selectAll: ");

        List<BlackNumber> all = dao.findAll();
        for (BlackNumber blackNumber: all) {
            Log.i("单元测试", "selectAll: " + blackNumber);
        }


    }
}
