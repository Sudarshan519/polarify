package jp.co.polarify.onboarding.app.fragment;


import androidx.annotation.LayoutRes;

import com.example.bnpj_polarify_re.R;


/**
 * セルフィー撮影画面のプログレスエリアのフラグメントです.
 */
public final class SelfieProgressAreaFragment extends ProgressAreaFragment {
    /**
     * フラグメントレイアウトのリソース ID を取得します.
     *
     * @return 取得したレイアウト ID
     */
    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.selfie_child_progress;
    }
}
