package jp.co.polarify.onboarding.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.bnpj_polarify_re.R;

import jp.co.polarify.onboarding.sdk.log.Logger;
import jp.co.polarify.onboarding.sdk.log.LoggerFactory;

public class HomeTopFragment extends Fragment {
    /**
     * ロガーオブジェクト.
     */
    private static final Logger logger = LoggerFactory.getLogger(HomeTopFragment.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home_top, container, false);

        final TextView textView = view.findViewById(R.id.privacy_policy);
        final Button button = view.findViewById(R.id.opening_account_button);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                replaceFragment(new PrivacyPolicyFragment());
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                replaceFragment(new HomeFlowFragment());
            }
        });

        return view;
    }

    /**
     * 指定のフラグメントに切り替えます.
     *
     * @param fragment 切り替えるフラグメント
     */
    protected void replaceFragment(@NonNull final Fragment fragment) {
        final FragmentManager manager = getFragmentManager();
        if (manager == null) {
            logger.debug("Cannot get FragmentManager");
            return;
        }

        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).commit();
    }
}
