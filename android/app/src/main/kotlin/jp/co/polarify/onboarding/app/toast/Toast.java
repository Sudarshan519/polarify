package jp.co.polarify.onboarding.app.toast;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;

/**
 * Toast を表示するクラスです.
 * Debug 時は android の Toast を用いて表示します。
 * Release 時には何もしません。
 */
public final class Toast {
    /*
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(Toast.class);

    /**
     * 短いトーストを表示します.
     *
     * @param context コンテキスト
     * @param text    表示するメッセージ
     */
    public static void showShort(@NonNull final Context context, @Nullable final CharSequence text) {
        show(context, text, android.widget.Toast.LENGTH_SHORT);
    }

    /**
     * 長いトーストを表示します.
     *
     * @param context コンテキスト
     * @param text    表示するメßッセージ
     */
    public static void showLong(@NonNull final Context context, @Nullable final CharSequence text) {
        show(context, text, android.widget.Toast.LENGTH_LONG);
    }

    /**
     * トーストを表示します.
     *
     * @param context  コンテキスト
     * @param text     表示するメッセージ
     * @param duration 表示時間
     */
    private static void show(@NonNull final Context context, @Nullable final CharSequence text, final int duration) {
        // noinspection ConstantConditions
        if (context == null) {  // context は NonNull 指定だが、防御的に null チェックを行う
            logger.error("Context is null", new Throwable());   // null で呼び出した箇所をログに出力
            return;
        }

        android.widget.Toast.makeText(context, text, duration).show();
    }
}
