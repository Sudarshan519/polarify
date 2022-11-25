package jp.co.polarify.onboarding.app.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;


import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.app.TutorialActivity;
import jp.co.polarify.onboarding.app.utils.DocumentKind;
import jp.co.polarify.onboarding.sdk.types.internal.DocumentSides;
import jp.co.polarify.onboarding.sdk.types.result.DocumentResult;

/**
 * 本人確認書類うら-チュートリアル画面を生成フラグメントです.
 */
public final class DocumentBackCaptureTutorialFragment extends DocumentUprightCaptureTutorialFragment {
    /**
     * 撮影を行う書類の向きを習得します.
     *
     * @return 取得した撮影を行う書類の向き
     */
    @Override
    protected DocumentSides getDocumentSides(@NonNull final DocumentKind documentKind) {
        switch (documentKind) {
            case DRIVER_LICENSE_CARD:
                return DocumentSides.BACK;

            case MY_NUMBER_CARD:
                return DocumentSides.BACKM;

            case RESIDENCE_CARD:
                return DocumentSides.BACKR;

            default:
                return DocumentSides.BACK;
        }
    }

    /**
     * 処理開始時に表示するメッセージのリソース ID を取得します.
     *
     * @return 取得したメッセージのリソース ID
     */
    @Override
    @StringRes
    protected int getOpeningMessageId() {
        return R.string.back_capture_tutorial_text;
    }

    /**
     * キャプチャー結果をアクティビティに通知します.
     *
     * @param activity アクティビティ
     * @param result   キャップチャー結果
     */
    @Override
    protected void notifyDocumentResult(@NonNull final TutorialActivity activity, @NonNull final DocumentResult result) {
        activity.notifyBackImages(result);
    }

    /**
     * 次に表示するフラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @NonNull
    @Override
    protected Fragment getNextFragment() {
        return new DocumentBackConfirmationFragment();
    }
}
