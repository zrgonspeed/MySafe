package top.cnzrg.mysafe.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import top.cnzrg.mysafe.R;
import top.cnzrg.mysafe.utils.ConstantValue;
import top.cnzrg.mysafe.utils.SpUtil;
import top.cnzrg.mysafe.utils.StreamUtil;
import top.cnzrg.mysafe.utils.ToastUtil;


public class SplashActivity extends BasePermissionActivity {
    protected static final String tag = "SplashActivity";

    // 要申请的权限
    private static String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.RECEIVE_SMS
    };

    /**
     * 更新新版本的状态码
     */
    private static final int UPDATE_VERSION = 100;

    /**
     * 进入应用程序主界面的状态码
     */
    private static final int ENTER_HOME = 101;

    /**
     * URL地址错误
     */
    private static final int URL_ERROR = 102;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;
    private static final String HTTP_UPDATE_MYSAFE_JSON = "http://192.168.43.47:8080/updatemysafe.json";

    private TextView tv_version_name;
    private long mLocalVersionCode;
    private String mVersionDesc;
    private String mDownloadUrl;

    private RelativeLayout rl_root;
    private static final int REQUEST_CODE = 1001;
    private boolean mStopEnterHome = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    // 弹出对话框,提示用户更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    // 进入应用程序主界面,Activity跳转
                    if (!mStopEnterHome) {
                        enterHome();
                    }
                    break;
                case URL_ERROR:
                    ToastUtil.show(getApplicationContext(), "url异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(getApplicationContext(), "io异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(getApplicationContext(), "json解析异常");
                    enterHome();
                    break;
            }
        }
    };


    /**
     * 弹出对话框，提示用户更新
     */
    private void showUpdateDialog() {
        // 对话框是依赖于Activity存在的
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.home_apps);
        builder.setTitle("版本更新");
        // 设置描述内容
        builder.setMessage(mVersionDesc);

        builder.setPositiveButton("点我更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 下载apk,apk地址, downloadUrl
                downloadApk();
            }
        });

        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 关闭对话，进入主界面
                enterHome();
            }
        });

        // 点击取消的事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 即使用户点击取消，也需要让其进入应用程序主界面
                enterHome();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * 下载最新版本的apk
     */
    private void downloadApk() {
        // apk下载地址

        // 放置apk的所在路径

        // 1.判断sd卡是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 2.获取sd卡路径
            String apkPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "mysafe.apk";
            Log.i(tag, apkPath);
            // 3.发送请求,获取apk,并且放置到指定路径
            HttpUtils httpUtils = new HttpUtils();
            // 4.发送请求,传递参数(下载地址,apk放置位置)
            httpUtils.download(mDownloadUrl, apkPath, new RequestCallBack<File>() {
                // 下载成功
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    // 下载过后的放置在sd卡中的apk
                    File file = responseInfo.result;
                    Log.i(tag, "下载成功");

                    // 提示用户安装
                    installApk(file);
                }

                // 下载失败
                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(tag, "下载失败");
                    Log.i(tag, e.getMessage());
                }

                // 刚开始下载的方法
                @Override
                public void onStart() {
                    Log.i(tag, "刚开始下载");
                    super.onStart();
                }

                // 下载中的方法
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    Log.i(tag, "下载中.....");
                    Log.i(tag, "total = " + total);
                    Log.i(tag, "current = " + current);
                    super.onLoading(total, current, isUploading);
                }
            });
        }
    }

    /**
     * 安装下载的apk
     */
    private void installApk(File file) {
        // 系统应用界面，源码，安装apk入口
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");

        Log.i(tag, this.getPackageName());

        Uri uriForFile = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 7.0以上
            uriForFile = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uriForFile = Uri.fromFile(file);
        }
        Log.i(tag, uriForFile.toString());

        intent.setDataAndType(uriForFile, "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    // 开启一个Activity后，返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 进入应用程序主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        // 在开启一个新界面后，将导航界面关闭
        finish();
    }

    /**
     * 获取数据方法
     */
    private void initData() {
        // 1.应用版本名称
        tv_version_name.setText("版本名:" + getVersionName());

        // 检测应用是否有更新(本地版本号和服务器版本号),有则提示用户下载
        // 2.获取本地版本号
        mLocalVersionCode = getVersionCode();

        // 3.获取服务器版本号(客户端发请求，服务端给响应,传输数据格式：json)
        // http://www.xxxx.com/updatemysafe.json?key=value 返回200请求成功,流的方式将数据读取下来
        /*
            json中的内容包含:
             最新版本的版本名称
             最新版本的版本号(服务器版本号)
             最新版本的描述信息
             最新版本apk下载地址
        */

        if (SpUtil.getBoolen(this, ConstantValue.OPEN_UPDATE, false)) {
            checkVersion();
        } else {
            // 进入主界面
            // enterHome();
            // 消息机制
            // mHandler.sendMessageDelayed(, 4000);
            // 在发送消息4秒后去处理ENTER_HOME指向的消息
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }
    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();

                long start = System.currentTimeMillis();

                // 发送请求获取数据
                try {
                    // 模拟器访问tomcat
                    // 1.封装URL地址
                    URL url = new URL(HTTP_UPDATE_MYSAFE_JSON);
                    // 2.开启一个链接
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    // 3.设置常见请求参数(请求头)
                    urlConnection.setConnectTimeout(3000);
                    urlConnection.setReadTimeout(3000); // 读取超时
                    urlConnection.setRequestMethod("GET");

                    // 4.获取响应码  200:请求成功
                    if (urlConnection.getResponseCode() == 200) {
                        // 5.以流的形式将数据获取下来;
                        InputStream inputStream = urlConnection.getInputStream();
                        // 6.将流转换成字符串
                        String json = StreamUtil.streamToString(inputStream);
                        Log.i(tag, json);
                        // 7.json解析
                        JSONObject jsonObject = new JSONObject(json);

                        String versionName = jsonObject.getString("versionName");
                        String versionCode = jsonObject.getString("versionCode");
                        mVersionDesc = jsonObject.getString("versionDesc");
                        mDownloadUrl = jsonObject.getString("downloadUrl");

                        // debug
                        Log.i(tag, versionName);
                        Log.i(tag, versionCode);
                        Log.i(tag, mVersionDesc);
                        Log.i(tag, mDownloadUrl);

                        // 8.比对版本号(服务器版本号>本地版本号,提示用户更新)
                        if (mLocalVersionCode < Integer.valueOf(versionCode)) {
                            // 提示用户更新,弹出对话框(UI),消息机制
                            msg.what = UPDATE_VERSION;  //ctrl+shift+U 大小写转换

                        } else {
                            // 进入应用程序主界面
                            msg.what = ENTER_HOME;
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    // 指定睡眠时间，请求网络的时长超过4秒则不做处理
                    // 请求网络的时长小于4秒，强制让其睡眠满4秒
                    long end = System.currentTimeMillis();
                    if (end - start < 4000) {
                        Log.i(tag, "不够4秒");
                        try {
                            Thread.sleep(4000 - (end - start));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!mStopEnterHome) {
                        mHandler.sendMessage(msg);
                    }
                }
            }
        }).start();
    }

    /**
     * 返回版本号
     *
     * @return 非0代表获取成功
     */
    private long getVersionCode() {
        // 1.包管理者对象packageManager
        PackageManager packageManager = getPackageManager();
        // 2.从包管理对象中，获取指定包名的基本信息(版本名称，版本号)

        try {
            // 0代表获取基本信息
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);

            // 3.获取版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 获取版本名称:从清单文件中获取
     *
     * @return 应用版本名称 返回null代表异常
     */
    private String getVersionName() {
        // 1.包管理者对象packageManager
        PackageManager packageManager = getPackageManager();
        // 2.从包管理对象中，获取指定包名的基本信息(版本名称，版本号)

        try {
            // 0代表获取基本信息
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);

            // 3.获取版本名称
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 初始化UI方法
     */
    private void initUI() {
        tv_version_name = findViewById(R.id.tv_version_name);
        rl_root = findViewById(R.id.rl_root);
    }

    /**
     * 初始化动画,添加淡入动画
     */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);

        rl_root.startAnimation(alphaAnimation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // 6.0以上需要动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            initPermission(permissions, REQUEST_CODE);
        } else {
            init();
        }

    }

    protected void init() {
        setContentView(R.layout.activity_splash);

        initUI();
        initData();

        // 初始化动画
        initAnimation();

        // 初始化数据库,电话归属地
        initDB();
    }

    private void initDB() {
        // 1.归属地数据库拷贝过程
        initAddressDB("address.db");
    }

    /**
     * 拷贝数据库至files文件夹下
     *
     * @param dbName
     */
    private void initAddressDB(String dbName) {
        // 在filesDir文件夹下创建同名dbName数据库文件
        File filesDir = getFilesDir(); // .toString(): /data/user/0/top.cnzrg.mysafe/files
        File file = new File(filesDir, dbName);
        // 只在安装完应用第一次启动之后拷贝
        if (file.exists()) {
            return;
        }

        InputStream input = null;
        FileOutputStream out = null;
        try {
            // 读取第三方资源目录下的文件,address.db
            input = getAssets().open(dbName);

            // 将读取的内容写入到指定文件夹的文件中去
            out = new FileOutputStream(file);

            // 流操作
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = input.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null && input != null) {
                try {
                    out.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /*getCacheDir();
        Environment.getExternalStorageDirectory();*/
    }

    @Override
    public void onBackPressed() {
        mStopEnterHome = true;
        super.onBackPressed();
    }
}
