package top.cnzrg.mysafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.service.LocationService;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.SpUtil;
import top.cnzrg.mysafe.utils.ToastUtil;

/**
 * FileName: SmsReceiver
 * Author: ZRG
 * Date: 2019/5/5 18:18
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String tag = "SmsReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        //  1.判断是否开启了防盗保护
        boolean open_security = SpUtil.getBoolen(context, ConstantValue.OPEN_SECURITY, false);

        if (open_security) {
            // 2.获取短信内容
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            for (Object obj : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);

                // 获取短信对象的基本信息
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();

                // 判断是否包含播放音乐的关键字
                if (messageBody.contains("#*alarm*#")) {
                    Log.i(tag, "开始播放音乐");

                    // 播放音乐(准备音乐)
                    MediaPlayer mp = MediaPlayer.create(context, R.raw.ylzs);
                    // 设置循环播放
                    mp.setLooping(true);
                    mp.start();

                    /*mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                            Log.i(tag, "onPrepared()-----------------");
                        }
                    });*/

                    Log.i(tag, "开始播放音乐start");
                }

                if (messageBody.contains("#*location*#")) {
                    // 开启获取位置服务
                    Intent intent1 = new Intent(context, LocationService.class);
                    context.startService(intent1);
                }

                if (messageBody.contains("#*lockscreen*#")) {

                }

                if (messageBody.contains("#*wipedata*#")) {

                }
            }
        }
    }
}
