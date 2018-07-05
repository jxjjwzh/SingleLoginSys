package wangzh.single_login.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PreferencesUtil，提供封装的SharedPreference方法
 * <ul>
 * <strong>Preference名称</strong>
 * <li>你可以通过 {@link #PREFERENCE_NAME}去改变名称</li>
 * </ul>
 * <ul>
 * <strong>写值</strong>
 * <li>put string {@link #putString(Context, String, String)}</li>
 * <li>put int {@link #putInt(Context, String, int)}</li>
 * <li>put long {@link #putLong(Context, String, long)}</li>
 * <li>put boolean {@link #putFloat(Context, String, float)}</li>
 * <li>put boolean {@link #putBoolean(Context, String, boolean)}</li>
 * </ul>
 * <ul>
 * <strong>读值</strong>
 * <li>get string {@link #getString(Context, String)}, {@link #getString(Context, String, String)}</li>
 * <li>get int {@link #getInt(Context, String)}, {@link #getInt(Context, String, int)}</li>
 * <li>get long {@link #getLong(Context, String)}, {@link #getLong(Context, String, long)}</li>
 * <li>get float {@link #getFloat(Context, String)}, {@link #getFloat(Context, String, float)}</li>
 * <li>get boolean {@link #getBoolean(Context, String)}, {@link #getBoolean(Context, String, boolean)}</li>
 * </ul>
 *
 * Created by zhuduanchang on 2015/6/17.
 */
public class PreferencesUtil {

    public static String PREFERENCE_NAME = "thinkive";

    /**
     * 写string值
     *
     * @param context 上下文
     * @param key     待写入的key
     * @param value   新值
     * @return 写入成功返回true，否则返回false
     */
    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 读string值
     *
     * @param context 上下文
     * @param key     待读值的key
     * @return 存在返回值，否则返回null，如果不是String类型抛出ClassCastException
     * @see #getString(Context, String, String)
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     * 获取string值
     *
     * @param context      上下文
     * @param key          待读值的key
     * @param defaultValue 该值不存在，返回返回该值
     * @return 存在返回值，否则返回defaultValue，如果不是String类型抛出ClassCastException
     */
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     * 写int值
     *
     * @param context 上下文
     * @param key     待写入的key
     * @param value   新值
     * @return 写入成功返回true，否则返回false
     */
    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * 获取int值
     *
     * @param context 上下文
     * @param key     待读取的key
     * @return 存在返回值，否则返回-1，如果不是int类型抛出ClassCastException
     * @see #getInt(Context, String, int)
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * 获取int值
     *
     * @param context      上下文
     * @param key          待读值的key
     * @param defaultValue 该值不存在，返回返回该值
     * @return 存在返回值，否则返回defaultValue，如果不是int类型抛出ClassCastException
     */
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    /**
     * 写long值
     *
     * @param context 上下文
     * @param key     待写入的key
     * @param value   新值
     * @return 写入成功返回true，否则返回false
     */
    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * 获取long值
     *
     * @param context 上下文
     * @param key     待读取的key
     * @return 存在返回值，否则返回-1，如果不是long类型抛出ClassCastException
     * @see #getLong(Context, String, long)
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    /**
     * 读long值
     *
     * @param context      上下文
     * @param key          待读值的key
     * @param defaultValue 该值不存在，返回返回该值
     * @return 存在返回值，否则返回defaultValue，如果不是long类型抛出ClassCastException
     */
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    /**
     * 写float值
     *
     * @param context 上下文
     * @param key     待写入的key
     * @param value   新值
     * @return 写入成功返回true，否则返回false
     */
    public static boolean putFloat(Context context, String key, float value) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * 获取float值
     *
     * @param context 上下文
     * @param key     待读取的key
     * @return 存在返回值，否则返回-1，如果不是float类型抛出ClassCastException
     * @see #getFloat(Context, String, float)
     */
    public static float getFloat(Context context, String key) {
        return getFloat(context, key, -1);
    }

    /**
     * 读float值
     *
     * @param context      上下文
     * @param key          待读值的key
     * @param defaultValue 该值不存在，返回返回该值
     * @return 存在返回值，否则返回defaultValue，如果不是float类型抛出ClassCastException
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * 写boolean值
     *
     * @param context 上下文
     * @param key     待写入的key
     * @param value   新值
     * @return 写入成功返回true，否则返回false
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * 获取boolean值
     *
     * @param context 上下文
     * @param key     待读取的key
     * @return 存在返回值，否则返回-1，如果不是boolean类型抛出ClassCastException
     * @see #getBoolean(Context, String, boolean)
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * 读boolean值
     *
     * @param context      上下文
     * @param key          待读值的key
     * @param defaultValue 该值不存在，返回返回该值
     * @return 存在返回值，否则返回defaultValue，如果不是boolean类型抛出ClassCastException
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences settings
          = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }
}
