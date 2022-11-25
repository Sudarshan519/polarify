package jp.co.polarify.onboarding.app.fragment;


import androidx.annotation.NonNull;

/**
 * 何もしない顔切り出しクラスです.
 */
public final class NullFaceCropper extends FaceCropper {
    /**
     * コンストラクタ.
     *
     * @param fragment フラグメント
     */
    public NullFaceCropper(@NonNull final DocumentConfirmationFragment fragment) {
        super(fragment);
    }

    /**
     * 顔切り出しを実行します.
     */
    @Override
    public void execute() {
        final DocumentConfirmationFragment fragment = getFragment();
        fragment.replaceNextFragment();
    }
}
