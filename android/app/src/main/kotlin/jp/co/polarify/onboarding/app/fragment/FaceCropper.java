package jp.co.polarify.onboarding.app.fragment;


import androidx.annotation.NonNull;

/**
 * 顔切り出しを行うクラスの抽象基底クラスです.
 */
abstract class FaceCropper {
    /**
     * フラグメント.
     */
    @NonNull
    private final DocumentConfirmationFragment fragment;

    /**
     * コンストラクタ.
     *
     * @param fragment フラグメント
     */
    FaceCropper(@NonNull final DocumentConfirmationFragment fragment) {
        this.fragment = fragment;
    }

    /**
     * 顔切り出しを実行します.
     */
    abstract public void execute();

    /**
     * フラグメントを取得します.
     *
     * @return 取得したフラグメント
     */
    @NonNull
    protected DocumentConfirmationFragment getFragment() {
        return fragment;
    }
}
