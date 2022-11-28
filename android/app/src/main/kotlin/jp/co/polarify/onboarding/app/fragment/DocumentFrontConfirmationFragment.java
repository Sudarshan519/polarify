package jp.co.polarify.onboarding.app.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import jp.co.polarify.onboarding.app.TutorialActivity;
import jp.co.polarify.onboarding.app.utils.DocumentKind;
import jp.co.polarify.onboarding.sdk.types.internal.DocumentSides;

import static jp.co.polarify.onboarding.app.BundleKeyDefinitions.APP_KEY_DIRECT_START_CAPTURE;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


/**
 * 書類表の撮影の確認を行うフラグメントです.
 */
public final class DocumentFrontConfirmationFragment extends DocumentUprightConfirmationFragment {
    /**
     * 撮影を行う書類の向きを取得します.
     *
     * @return 取得した撮影を行う書類の向き
     */
    @Override
    protected DocumentSides getDocumentSides(@NonNull final DocumentKind documentKind) {
        switch (documentKind) {
            case DRIVER_LICENSE_CARD:
                return DocumentSides.FRONT;

            case MY_NUMBER_CARD:
                return DocumentSides.FRONTM;

            case RESIDENCE_CARD:
                return DocumentSides.FRONTR;

            default:
                return DocumentSides.FRONT;
        }
    }

    @Override
    protected Fragment getPreviousFragment() {
        final Fragment fragment = new DocumentFrontCaptureTutorialFragment();
        final Bundle arguments = new Bundle();
        arguments.putBoolean(APP_KEY_DIRECT_START_CAPTURE, true);
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * 顔切り出しオブジェクトを生成します.
     *
     * @return 生成した顔切り出しオブジェクト
     */
    @Override
    protected FaceCropper createFaceCropper() {
        return new FaceCropperImplement(this);
    }

    /**
     * 次に表示するフラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @NonNull
    @Override
    protected Fragment getNextFragment() {
        if (MainActivity.Companion.isManualEKYC())
            return new DocumentTiltedCaptureTutorialFragment();
        else return new DocumentBackCaptureTutorialFragment();
    }

    /**
     * アクティビティから書類画像を取得します.
     *
     * @param activity アクティビティ
     * @return 取得した書類画像
     */
    @Override
    protected Bitmap getDocumentImage(@NonNull final TutorialActivity activity) {
        return activity.getFrontDocumentImage();
    }

    /**
     * アクティビティから補正済み書類画像を取得します.
     *
     * @param activity アクティビティ
     * @return 取得した補正済み書類画像
     */
    @Override
    protected Bitmap getCorrectedImage(@NonNull final TutorialActivity activity) {
        return activity.getFrontCorrectedImage();
    }
}
