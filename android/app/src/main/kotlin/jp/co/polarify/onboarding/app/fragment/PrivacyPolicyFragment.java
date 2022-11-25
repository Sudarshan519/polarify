package jp.co.polarify.onboarding.app.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.bnpj_polarify_re.R;


public class PrivacyPolicyFragment extends Fragment {
    /**
     * 利用規約ファイル名.
     */
    private static final String POLICY_FILE = "policy.html"; //

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final WebView webView = view.findViewById(R.id.privacyPolicyView);
        webView.loadUrl("file:///android_asset/" + POLICY_FILE);

        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new Object() {
            //policy.htmlのJavaScriptクリックエベント
            @JavascriptInterface
            public final void performClick() {
                final HomeTopFragment fragment = new HomeTopFragment();
                final FragmentManager manager = getFragmentManager();
                final FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.commit();
            }
        }, "close");
    }
}
