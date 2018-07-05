package wangzh.single_login.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * Created by liujianwei on 15/6/5.
 */
public class DeviceUtil {
    //外网ip；
    public static String NETWORK_IP = "";

    /**
     * 设备唯一标识编码
     */
    private static String sDeviceUniqueCode;

    /**
     * 内置
     */
    private static String SDCARD_INTERNAL = "internal";


    /**
     * 外置
     */
    private static String SDCARD_EXTERNAL = "external";

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取sdk版本号
     *
     * @return
     */
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * android 发布版本名(如:5.1.1)
     *
     * @return
     */
    public static String getRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 求设备唯一标识，有缓存先取缓存
     * 具体求出设备唯一表示的方法：{@link #buildDeviceUniqueCodeWithoutCache(Context)}
     *
     * @return 设备唯一标识
     */
    @SuppressLint("HardwareIds")
    private static String buildDeviceUniqueCode(Context context) {
        // 能取到缓存的时候，直接用缓存
        String cacheKey = "DEVICE_ID_PERSISTENCE_CACHE_" + AppUtil.getPackageName(context); // 缓存key拼接包名
        String cacheResult = PreferencesUtil.getString(context, cacheKey);
        if (!TextUtils.isEmpty(cacheResult)) {
            return cacheResult;
        }

        // 其他项目做法，设备唯一标识不需要MD5加密
        String result = buildDeviceUniqueCodeWithoutCache(context);

        // 当获取成功时，存入持久性缓存
        if (!TextUtils.isEmpty(result)) {
            PreferencesUtil.putString(context, cacheKey, result);
        }
        return result;
    }

    /**
     * 求设备唯一标识，
     * 按是否成功先后获取：MEID、MAC地址、Android ID、硬件特征码。
     *
     * @return 设备唯一标识
     */
    @SuppressLint("HardwareIds")
    private static String buildDeviceUniqueCodeWithoutCache(Context context) {
//        String result = getMEID(context);
//        if (TextUtils.isEmpty(result) || result.contains("00000000")) {
//            result = getMacAddress();
//            if (TextUtils.isEmpty(result)
//                    || "00:00:00:00:00:00".equals(result)) {
//                result = getAndroidId(context);
//                if (TextUtils.isEmpty(result)) {
//                    result = Build.SERIAL;
//                }
//            }
//        }
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

    /**
     * 获取设备唯一标识，IMEI、mac地址、AndroidId、硬件编码，依次判断有效性，有什么获取什么
     *
     * @return 根据一定策略取出的设备唯一标识
     */
    public static String getDeviceId(Context context) {
        if (TextUtils.isEmpty(sDeviceUniqueCode)) {
            sDeviceUniqueCode = buildDeviceUniqueCode(context);
        }
        return sDeviceUniqueCode;
    }

    /**
     * 获取设备id
     * 仅仅只对Android手机有效
     */
    public static String getMEID(Context context) {
        String IMEI = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                try {
                    IMEI = telephonyManager.getDeviceId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Class clazz = telephonyManager.getClass();
            //为了确保 cdma 的手机也返回 imei
            Method getImei;//(int slotId)
            getImei = clazz.getDeclaredMethod("getImei", int.class);
            String i1 = (String) getImei.invoke(telephonyManager, 0);
            String i2 = (String) getImei.invoke(telephonyManager, 1);
            if (!TextUtils.isEmpty(i1) && TextUtils.isDigitsOnly(i1) && !i1.contains("0000000000")) {
                IMEI = i1;
                return IMEI;
            } else if (!TextUtils.isEmpty(i2) && TextUtils.isDigitsOnly(i2) && !i2.contains("0000000000")) {
                IMEI = i2;
                return IMEI;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMEI;
        }
        return IMEI;
    }

    /**
     * 如果Android Pad没有IMEI,用此方法获取设备ANDROID_ID：
     * 通常被认为不可信，因为它有时为null。
     * 开发文档中说明了：这个ID会改变如果进行了出厂设置。
     * 并且，如果某个Andorid手机被Root过的话，这个ID也可以被任意改变
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    /**
     * 兼容旧版API，
     *
     * @deprecated 请调用 {@link #getMacAddress()}
     */
    @Deprecated
    public static String getMacAddress(Context context) {
        return getMacAddress();
    }

    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }


    /**
     * 获取手机的MAC地址
     * 请使用{@link #getMacAddress(Context)}取代
     */
    @Deprecated
    public static String getMac() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec(
                    "cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        return macSerial;
    }

    public static String loadFileAsString(String fileName) throws Exception {
        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    public static String loadReaderAsString(Reader reader) throws Exception {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    /**
     * 获取手机ip
     *
     * @param context
     * @return
     */
    public static String getIpAddress(Context context) {
        String ipAddress = "";
        if (context == null) {
            return ipAddress;
        }

        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ip = 0;
            if (wifiInfo != null) {
                ip = wifiInfo.getIpAddress();
            }
            if (ip != 0) {
                ipAddress = intToIp(ip);
                return ipAddress;
            }
        }

        try {
            Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
            while (ni.hasMoreElements()) {
                NetworkInterface networkInterface = ni.nextElement();
                for (Enumeration<InetAddress> netAddresses = networkInterface.getInetAddresses(); netAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = netAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ipAddress = inetAddress.getHostAddress().toString();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }


    /**
     * 获取外网实际IP
     *
     * @return
     */
    public static String getNetWorkIpAddress() {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            infoUrl = new URL("https://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setReadTimeout(6 * 1000);
            httpConnection.setConnectTimeout(6 * 1000);
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    strber.append(line + "\n");
                }
                inStream.close();

                //从反馈的结果中提取出IP地址
                JSONObject jsonObject = new JSONObject(strber.substring(strber.indexOf("{"), strber.indexOf("}") + 1).toString());
                NETWORK_IP = jsonObject.getString("cip");
                return jsonObject.getString("cip");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取内存存储可用空间
     *
     * @return long
     */
    public static long getAvailableInternalStorageSize() {
        File path = Environment.getDataDirectory(); // 获取数据目录
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取内存存储总空间
     *
     * @return long
     */
    public static long getTotalInternalStorageSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 判断外部存储是否可用
     *
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState());
    }

    /**
     * 获取外部可用空间大小
     *
     * @return
     */
    public static long getAvailableExternalStorageSize() {
        if (isExternalStorageAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * 获取外部总共空间大小
     *
     * @return
     */
    public static long getTotalExternalStorageSize() {
        if (isExternalStorageAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }

    }

    /**
     * BASEBAND-VER
     * 基带版本
     * return String
     */
    public static String getBasebandVersion() {
        String Version = "";
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[]{String.class, String.class});
            Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
            Version = (String) result;
        } catch (Exception e) {
        }
        return Version;
    }

    /**
     * MIUI版本,只能得到简单版本代号
     */
    public static String getMIUI_VerCode() {
        String Version = "";
        if (!android.os.Build.MANUFACTURER.equals("Xiaomi")) return Version;
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[]{String.class, String.class});
            Object result = m.invoke(invoker, new Object[]{"ro.miui.ui.version.code", "no message"});
            Version = (String) result;
        } catch (Exception e) {
        }
        return Version;
    }

    /**
     * MIUI版本,只能得到简单版本名
     */
    public static String getMIUI_VerName() {
        String Version = "";
        if (!android.os.Build.MANUFACTURER.equals("Xiaomi")) return Version;
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[]{String.class, String.class});
            Object result = m.invoke(invoker, new Object[]{"ro.miui.ui.version.name", "no message"});
            Version = (String) result;
        } catch (Exception e) {
        }
        return Version;
    }

    /**
     * CORE-VER
     * 内核版本
     * return String
     */

    public static String getLinuxCoreVersion() {
        Process process = null;
        String kernelVersion = "";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // get the output line
        InputStream outs = process.getInputStream();
        InputStreamReader isrout = new InputStreamReader(outs);
        BufferedReader brout = new BufferedReader(isrout, 8 * 1024);

        String result = "";
        String line;
        // get the whole standard output string
        try {
            while ((line = brout.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        try {
            if (result != "") {
                String Keyword = "version ";
                int index = result.indexOf(Keyword);
                line = result.substring(index + Keyword.length());
                index = line.indexOf(" ");
                kernelVersion = line.substring(0, index);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } finally {
            try {
                brout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return kernelVersion;
    }

    /**
     * INNER-VER
     * 内部版本
     * return String
     */

    public static String getInnerVersion() {
        String ver = "";

//        if (android.os.Build.DISPLAY.contains(android.os.Build.VERSION.INCREMENTAL)) {
        ver = android.os.Build.DISPLAY;
//        } else {
//            ver = android.os.Build.VERSION.INCREMENTAL;
//        }
        return ver;

    }


    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "."
                + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
    }

}