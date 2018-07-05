package wangzh.single_login.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by liujianwei on 15/6/5.
 */
public class AppUtil {

    /**
     * 安装包信息
     *
     * @param context 上下文
     * @return packageInfo
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo packageInfo = null;
        String packageName = context.getPackageName();
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return packageInfo;
        }
        return packageInfo;
    }

    /**
     * 获取App的包名
     */
    public static String getPackageName(Context context) {
        if (context == null) {
            return "";
        }
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return "";
        } else {
            return packageInfo.packageName;
        }
    }


}
