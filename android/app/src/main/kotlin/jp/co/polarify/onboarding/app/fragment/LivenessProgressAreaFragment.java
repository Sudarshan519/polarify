package jp.co.polarify.onboarding.app.fragment;


import androidx.annotation.LayoutRes;

import com.example.bnpj_polarify_re.R;

/**
 * ライブネス撮影画面のプログレスエリアのフラグメントです.
 */
public final class LivenessProgressAreaFragment extends ProgressAreaFragment {
    /**
     * フラグメントレイアウトのリソース ID を取得します.
     *
     * @return 取得したレイアウト ID
     */
    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.liveness_progress_view;
    }
}
