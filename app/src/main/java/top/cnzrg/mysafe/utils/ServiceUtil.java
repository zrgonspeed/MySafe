package top.cnzrg.mysafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * FileName: ServiceUtil
 * Author: ZRG
 * Date: 2019/5/13 14:47
 */
public class ServiceUtil {

    /**
     * @param context     上下文环境
     * @param serviceName 判断是否正在运行的服务的全包名名称
     * @return true 运行  false 没有运行
     */
    public static boolean isRunning(Context context, String serviceName) {
        // 1.获取ActivityManager管理者对象，可以去获取当前手机正在运行的所有服务
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 2.获取手机中正在运行的服务
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(100);
        // 3.遍历服务集合
        for (ActivityManager.RunningServiceInfo info : runningServices) {
            // 4.获取每一个真正运行服务的名称,是带包名的！
            String name = info.service.getClassName();

            if (serviceName.equals(name)) {
                // 如果指定的Service正在运行
                return true;
            }
        }

        return false;
    }
}
