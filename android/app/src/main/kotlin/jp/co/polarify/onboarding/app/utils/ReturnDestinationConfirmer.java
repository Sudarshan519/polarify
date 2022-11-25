package jp.co.polarify.onboarding.app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

//import com.nitv.bnpjcredit.R;

import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.app.MainActivity;
import jp.co.polarify.onboarding.sdk.types.internal.ErrorResult;

/**
 * ホーム画面遷移を行うクラスです.
 */
public final class ReturnDestinationConfirmer {
    /**
     * ホーム画面に戻る処理です.
     *
     * @param context コンテキスト
     */
    @UiThread
    public static void execute(@NonNull final Context context) {
        new AlertDialog.Builder(context, R.style.DialogStyle)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_text)
                .setPositiveButton(R.string.dialog_button_continue, null)
                .setNegativeButton(R.string.dialog_button_return, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        startHomeActivity(context);
                    }
                })
                .create().show();    // ダイアログを生成して表示
    }

    /**
     * ホーム画面に戻る処理です.
     *
     * @param context     コンテキスト
     * @param errorResult エラー結果情報
     */
    public static void execute(@NonNull final Context context, @NonNull final ErrorResult errorResult) {
        if (errorResult.equals(ErrorResult.USER_CANCELLED)
                || errorResult.equals(ErrorResult.UNAUTHORIZED_CAMERA_PERMISSION_ERROR)
                || errorResult.equals(ErrorResult.SERVER_CONNECTION_ERROR)
                || errorResult.equals(ErrorResult.UNEXPECTED_ERROR)) {
            startHomeActivity(context);
        }
    }

    /**
     * ホーム画面に遷移するインテントを発行します.
     *
     * @param context コンテキスト
     */
    private static void startHomeActivity(@NonNull final Context context) {
        final Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
