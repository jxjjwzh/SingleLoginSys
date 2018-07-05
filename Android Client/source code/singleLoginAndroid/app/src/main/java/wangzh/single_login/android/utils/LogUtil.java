package wangzh.single_login.android.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 单点登录的打印日志功能
 * Announcements：
 *
 * @author 王志鸿
 * @corporation Thinkive
 * @project singleLoginAndroid
 * @date 2018/7/4
 */
public class LogUtil {

    private static final String TAG = "SingleLogin";

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.flush();
        String stackTraceText = String.format("。错误堆栈如下: %n%s", sw);
        Log.e(TAG, msg + stackTraceText);
    }
}
