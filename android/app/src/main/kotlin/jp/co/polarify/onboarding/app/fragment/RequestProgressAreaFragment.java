package jp.co.polarify.onboarding.app.fragment;

import androidx.annotation.LayoutRes;

import com.example.bnpj_polarify_re.R;


/**
 * 書類撮影時のプログレスエリアのフラグメントです.
 */
public class RequestProgressAreaFragment extends ProgressAreaFragment {
    /**
     * フラグメントレイアウトのリソース ID を取得します.
     *
     * @return 取得したレイアウト ID
     */
    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.header_request;
    }
}
