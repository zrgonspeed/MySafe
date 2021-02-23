package top.cnzrg.mysafe.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.SpUtil;
import top.cnzrg.mysafe.utils.ToastUtil;

/**
 * FileName: BootReceiver
 * Author: ZRG
 * Date: 2019/5/3 12:43
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String tag = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(tag, "我收到手机开机广播了！！！！！！！！！！！");

        // 1.获取开机后手机的sim卡的序列号
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.SEND_SMS
        };

        // 2.获取sp中存储的序列卡号
        String sp_serialNumber = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");

        // 没有授权(读取sim序列号，发短信)或没有绑定sim卡就不执行了
        if (!permissionAllGranted(context, permissions) || TextUtils.isEmpty(sp_serialNumber)) {
            Log.i(tag,"没有授权或没有绑定sim卡");
            return;
        }

        // 防报错用
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String simSerialNumber = tm.getSimSerialNumber() + "xxx";

        Log.i(tag, "sim卡序列号:" + simSerialNumber);
        Log.i(tag, "SP中的sim卡序列号" + sp_serialNumber);

        // 3.比对
        if (!simSerialNumber.equals(sp_serialNumber)) {
            Log.i(tag, "开始发送短信");
            ToastUtil.show(context, "SIM卡变更！开始发送短信");

            // 发送短信给选中联系人
            SmsManager sms = SmsManager.getDefault();
            // 这里的5554一般是该模拟器自身
            sms.sendTextMessage("5554", null, "sim卡改变了#*alarm*#,#*location*#,#*lockscreen*#,#*wipedata*#", null, null);
        }
    }

    // 权限是否被授权了
    private boolean permissionAllGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则不能完成你需要的功能就返回 false
                return false;
            }
        }
        return true;
    }
}
