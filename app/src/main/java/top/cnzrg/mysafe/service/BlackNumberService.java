package top.cnzrg.mysafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.db.dao.BlackNumberDao;

/**
 * FileName: BlackNumberService
 * Author: ZRG
 * Date: 2019/6/30 17:43
 */
public class BlackNumberService extends Service {
    private static final String TAG = "BlackNumberService";
    private InnerSmsReceiver mInnerSmsReceiver;
    private BlackNumberDao mDao;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 拦截短信
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(Integer.MAX_VALUE);

        mInnerSmsReceiver = new InnerSmsReceiver();

        registerReceiver(mInnerSmsReceiver, intentFilter);
    }

    private class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: 我来了");
            
            // 获取短信内容，获取发送短信电话号码，如果此电话号码在黑名单中，并且拦截模式为1或3,就拦截短信
            // 1.获取短信内容
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            mDao = BlackNumberDao.getInstance(context);

            // 2.遍历每条短信
            for (Object obj : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);

                // 获取短信号码
                String originatingAddress = sms.getOriginatingAddress();

                int mode = mDao.getMode(originatingAddress);
                Log.i(TAG, "mode: " + mode);
                if (mode == 1 || mode == 3) {
                    Log.i(TAG, "拦截短信: " + originatingAddress);
                    // 拦截短信
                    abortBroadcast();
                }

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mInnerSmsReceiver != null) {
            unregisterReceiver(mInnerSmsReceiver);
        }
    }
}
