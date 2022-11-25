package jp.co.polarify.onboarding.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import jp.co.polarify.onboarding.app.IntroductionActivity;
import jp.co.polarify.onboarding.app.toast.Toast;
import jp.co.polarify.onboarding.app.utils.DocumentKind;
import jp.co.polarify.onboarding.app.utils.PreferenceManager;
import jp.co.polarify.onboarding.sdk.PolarifyKycSdkFactory;
import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;
import jp.co.polarify.onboarding.sdk.types.callback.GetMatchingIDCallback;
import jp.co.polarify.onboarding.sdk.types.internal.ErrorResult;
import jp.co.polarify.onboarding.sdk.types.result.GetMatchingIDResult;
import jp.co.polarify.onboarding.sdk.view.SafetyButton;

import static jp.co.polarify.onboarding.app.BundleKeyDefinitions.APP_KEY_CAPTURE_KIND;
import static jp.co.polarify.onboarding.app.BundleKeyDefinitions.APP_KEY_MATCHING_ID_RESULT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.bnpj_polarify_re.R;


/**
 * 書類撮影前・ホーム画面を生成フラグメントです.
 */
public class HomeFlowFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(HomeFlowFragment.class);

    /**
     * 登録を始めるボタンです.
     */
    private SafetyButton button = null;

    /**
     * 次へ進む時ネット通信Progressです.
     */
    private ProgressBar progressBar = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_flow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.waitting_progress_bar_id);

        button = view.findViewById(R.id.start_register_button);  // 登録を始めるボタン
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progressBar.setVisibility(View.VISIBLE);

                final RadioGroup radgroup = (RadioGroup) view.findViewById(R.id.radioGroup);
                switch (radgroup.getCheckedRadioButtonId()) {
                    case R.id.radioDriverLicenseCard:
                        PreferenceManager.putInt(getContext(), APP_KEY_CAPTURE_KIND, DocumentKind.DRIVER_LICENSE_CARD.ordinal());
                        break;

                    case R.id.radiomyNumberCard:
                        PreferenceManager.putInt(getContext(), APP_KEY_CAPTURE_KIND, DocumentKind.MY_NUMBER_CARD.ordinal());
                        break;

                    case R.id.radioResidenceCard:
                        PreferenceManager.putInt(getContext(), APP_KEY_CAPTURE_KIND, DocumentKind.RESIDENCE_CARD.ordinal());
                        break;

                    default:
                        PreferenceManager.putInt(getContext(), APP_KEY_CAPTURE_KIND, DocumentKind.DRIVER_LICENSE_CARD.ordinal());
                        break;
                }

                getMatchingID();
            }
        }, false);
    }

    /**
     * イントロダクションを開始します.
     */
    private void startIntroduction() {
        startIntroductionActivity(IntroductionActivity.IntroductionType.FIRST);
    }

    /**
     * イントロダクションを開始します.
     *
     * @param type イントロダクションの種類
     */
    private void startIntroductionActivity(@NonNull final IntroductionActivity.IntroductionType type) {
        final Intent intent = new Intent(getContext(), IntroductionActivity.class);
        intent.putExtra(IntroductionActivity.KEY_INTRODUCTION_TYPE, type);
        logger.debug("send IntroductionActivity start intent");
        startActivity(intent);
    }

    /**
     * 照合用 ID 取得を行います.
     */
    private void getMatchingID() {
        final Context context = requireContext();
        PolarifyKycSdkFactory factory = new PolarifyKycSdkFactory();
        factory.getInstance(context).getMatchingID(new GetMatchingIDCallback() {
            @Override
            public void onSuccess(@NonNull final GetMatchingIDResult result) {
                Toast.showLong(getContext(), "成功");
                logger.debug("getMatchingID is onSuccess(ID = \"{}\")", result.getId());
                PreferenceManager.putGetMatchingIDResult(context, APP_KEY_MATCHING_ID_RESULT, result);
                startIntroduction();

                button.unlock();
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(@NonNull final ErrorResult errorResult) {
                android.widget.Toast.makeText(getContext(), errorResult.getMessage(), android.widget.Toast.LENGTH_LONG).show(); // Release版でも通信エラーが分かるようにAndroid標準のトースト表示を行う

                button.unlock();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
