/*
 * Copyright (C) 2019 Polarify. All Rights Reserved.
 */

package jp.co.polarify.onboarding.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import jp.co.polarify.onboarding.sdk.types.result.FidoUserAndGetMatchingIDResult;
import jp.co.polarify.onboarding.sdk.types.result.GetMatchingIDResult;

/**
 * Preference 制御.
 */
public final class PreferenceManager {

    /**
     * プライベートコンストラクタ.
     */
    private PreferenceManager() {
    }

    /**
     * デフォルト値定義Interface.
     */
    public interface IDefaultValue {
        /**
         * String.
         */
        String STRING = "";
        /**
         * int.
         */
        int INT = -1;
        /**
         * long.
         */
        long LONG = -1;
        /**
         * boolean.
         */
        boolean BOOLEAN = false;
    }

    /**
     * インスタンスの取得.
     *
     * @param context コンテキスト
     * @return インスタンス
     */
    private static SharedPreferences getDefaultSharedPreferences(@NonNull final Context context) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * プリファレンス取得（String型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @return プリファレンス値
     */
    public static String getString(@NonNull final Context context, @NonNull final String key) {
        return getDefaultSharedPreferences(context).getString(key, IDefaultValue.STRING);
    }

    /**
     * プリファレンス取得（String型）.
     *
     * @param context  コンテキスト
     * @param key      プリファレンスキー
     * @param defValue Default値
     * @return プリファレンス値
     */
    public static String getString(@NonNull final Context context, @NonNull final String key, @NonNull final String defValue) {
        return getDefaultSharedPreferences(context).getString(key, defValue);
    }

    /**
     * プリファレンス取得（int型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @return プリファレンス値
     */
    public static int getInt(@NonNull final Context context, @NonNull final String key) {
        return getDefaultSharedPreferences(context).getInt(key, IDefaultValue.INT);
    }

    /**
     * プリファレンス取得（long型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @return プリファレンス値
     */
    public static long getLong(@NonNull final Context context, @NonNull final String key) {
        return getDefaultSharedPreferences(context).getLong(key, IDefaultValue.LONG);
    }

    /**
     * プリファレンス取得（boolean型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @return プリファレンス値
     */
    public static boolean getBoolean(@NonNull final Context context, @NonNull final String key) {
        return getDefaultSharedPreferences(context).getBoolean(key, IDefaultValue.BOOLEAN);
    }

    /**
     * プリファレンス取得（boolean型）.
     *
     * @param context  コンテキスト
     * @param key      プリファレンスキー
     * @param defValue Default値
     * @return プリファレンス値
     */
    public static boolean getBoolean(@NonNull final Context context, @NonNull final String key, final boolean defValue) {
        return getDefaultSharedPreferences(context).getBoolean(key, defValue);
    }

    /**
     * プリファレンスに保存（String型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @param value   プリファレンス値
     */
    public static void putString(@NonNull final Context context, @NonNull final String key, @NonNull final String value) {
        final SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * プリファレンスに保存（int型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @param value   プリファレンス値
     */
    public static void putInt(@NonNull final Context context, @NonNull final String key, final int value) {
        final SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * プリファレンスに保存（long型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @param value   プリファレンス値
     */
    public static void putLong(@NonNull final Context context, @NonNull final String key, final long value) {
        final SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * プリファレンスに保存（boolean型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @param value   プリファレンス値
     */
    public static void putBoolean(@NonNull final Context context, @NonNull final String key, final boolean value) {
        final SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * プリファレンスを削除.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     */
    public static void remove(@NonNull final Context context, @NonNull final String key) {
        final SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * プリファレンスを全削除.
     *
     * @param context コンテキスト
     */
    public static void reset(@NonNull final Context context) {
        android.preference.PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
    }


    /**
     * プリファレンスに保存（GetMatchingIDResult型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @param value   プリファレンス値
     */
    public static void putGetMatchingIDResult(@NonNull final Context context, @NonNull final String key, @NonNull final GetMatchingIDResult value) {
        final SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        final Gson gson = new Gson();
        editor.putString(key, gson.toJson(value));
        editor.commit();
    }

    /**
     * プリファレンス取得（GetMatchingIDResult型）.
     *
     * @param context コンテキスト
     * @param key     プリファレンスキー
     * @return プリファレンス値
     */
    public static GetMatchingIDResult getGetMatchingIDResult(@NonNull final Context context, @NonNull final String key) {
        final Gson gson = new Gson();
        return gson.fromJson(getDefaultSharedPreferences(context).getString(key, null), FidoUserAndGetMatchingIDResult.class);
    }
}
