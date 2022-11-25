package jp.co.polarify.onboarding.app.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.daon.fido.client.sdk.uaf.UafMessageUtils;

/**
 * FacetID を生成、確認するためのクラスです.
 */
public class CheckFacetID {
    private final String facetID; // facetID
    private final Context context;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public CheckFacetID(final Context context) {
        this.context = context;
        facetID = UafMessageUtils.getFacetId(context); // facetID を生成
    }

    /**
     * facetID を取得します.
     *
     * @return facetID
     */
    public String getFacetID() {
        return facetID;
    }

    /**
     * facetID をログで出力します.
     */
    public void checkByLog() {
        Log.d("CheckFacetId", "facetID = { " + getFacetID() + " }");
        System.out.println("CheckFacetId" + " facetID = { " + getFacetID() + " }");
    }

    /**
     * facetID をトーストで表示します.
     */
    public void checkByToast() {
        Toast.makeText(context, "facetID = { " + getFacetID() + " }", Toast.LENGTH_LONG).show();
    }

    /**
     * facetID をダイアログで表示します.
     */
    public void checkByDialog() {
        new AlertDialog.Builder(context)
                .setTitle("facetID")
                .setMessage(getFacetID())
                .setPositiveButton("OK", null)
                .show();
    }
}

