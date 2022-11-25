package jp.co.polarify.onboarding.app.fragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;


import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.app.TutorialActivity;
import jp.co.polarify.onboarding.app.toast.Toast;
import jp.co.polarify.onboarding.app.utils.ReturnDestinationConfirmer;
import jp.co.polarify.onboarding.sdk.PolarifyKycSdkFactory;
import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;
import jp.co.polarify.onboarding.sdk.types.callback.AddingDocumentsCallback;
import jp.co.polarify.onboarding.sdk.types.common.DocumentType;
import jp.co.polarify.onboarding.sdk.types.internal.ErrorResult;
import jp.co.polarify.onboarding.sdk.types.paramters.AddingDocumentsParameters;
import jp.co.polarify.onboarding.sdk.types.result.GetMatchingIDResult;

/**
 * 顔切り出し処理を行うクラスです.
 */
public final class FaceCropperImplement extends FaceCropper {
    /**
     * ロガーオブジェクト.
     */
    private Logger logger = LoggerFactory.getLogger(FaceCropperImplement.class);

    /**
     * コンストラクタ.
     *
     * @param fragment フラグメント
     */
    FaceCropperImplement(@NonNull final DocumentConfirmationFragment fragment) {
        super(fragment);
    }

    /**
     * 顔切り出しを実行します.
     */
    public void execute() {
        final DocumentConfirmationFragment fragment = getFragment();
        final TutorialActivity activity = fragment.getTutorialActivity();
        if (activity == null) {
            logger.error("Cannot get context");
            throw new IllegalStateException("Cannot get context");
        }

        final Bitmap image = fragment.getCorrectedImage();
        if (image == null) {
            Toast.showShort(activity, "失敗");
            return;
        }

        final DocumentType documentType = fragment.getDocumentType();
        final GetMatchingIDResult matchingIdResult = fragment.getMatchingIdResult();
        final AddingDocumentsParameters parameters = new AddingDocumentsParameters(matchingIdResult, documentType, image);
        final PolarifyKycSdkFactory factory = new PolarifyKycSdkFactory();
        factory.getInstance(activity).registerFaceOfDocument(parameters, new AddingDocumentsCallback() {
            @Override
            public void onSuccess() {
                Toast.showShort(activity, "成功");
                if (!image.isRecycled()) {
                    image.recycle();
                }
                fragment.replaceNextFragment(); // 次のフラグメントへ遷移

                fragment.registerFaceOfDocumentOnSuccess();
            }

            @Override
            public void onError(@NonNull final ErrorResult errorResult) {
                fragment.registerFaceOfDocumentOnError();

                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.showShort(activity, "失敗");
                        showErrorHandlerDialog(activity, errorResult);
                    }
                });
            }
        });
    }

    /**
     * 対象のエラーダイアログを表示します.
     *
     * @param activity    アクティビティ
     * @param errorResult エラー
     */
    private void showErrorHandlerDialog(@NonNull final TutorialActivity activity, @NonNull final ErrorResult errorResult) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.DialogStyle);
        //再撮影ダイアログを表示します.
        if (errorResult.equals(ErrorResult.SERVER_CONNECTION_ERROR)) {
            builder.setTitle(R.string.dialog_title_retake).setMessage(R.string.dialog_text_retake)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            final DocumentConfirmationFragment fragment = getFragment();
                            fragment.replacePreviousFragment();   // 前のフラグメントに戻る
                        }
                    });
        }
        //予期せぬエラーダイアログを表示します.
        if (errorResult.equals(ErrorResult.UNEXPECTED_ERROR)) {
            builder.setTitle(R.string.unexpected_dialog_title).setMessage(R.string.unexpected_dialog_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            ReturnDestinationConfirmer.execute(activity, errorResult);
                        }
                    });
        }
        builder.create().show();
    }
}
