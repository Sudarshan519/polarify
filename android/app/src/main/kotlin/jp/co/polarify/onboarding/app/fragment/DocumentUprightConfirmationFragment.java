package jp.co.polarify.onboarding.app.fragment;


import com.example.bnpj_polarify_re.R;

/**
 * 表面や裏面などの正面撮影をする書類の撮影確認を行うフラグメントです.
 */
abstract class DocumentUprightConfirmationFragment extends DocumentConfirmationFragment {
    /**
     * 失敗例の画像イメージのリソース ID を取得します.
     *
     * @return 取得した画像イメージリソース ID
     */
    @Override
    protected int getFailPatternImageId() {
        return R.drawable.drive_license_fail_pattern;
    }
}
