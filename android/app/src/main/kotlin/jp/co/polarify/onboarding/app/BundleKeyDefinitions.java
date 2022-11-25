package jp.co.polarify.onboarding.app;

public interface BundleKeyDefinitions {
    /**
     * 照合用 ID のキー文字列.
     */
    String APP_KEY_MATCHING_ID_RESULT = "APP_KEY_MATCHING_ID_RESULT";

    /**
     * ID チェック ID のキー文字列.
     */
    String APP_KEY_ID_CHECK_ID = "APP_KEY_ID_CHECK_ID";

    /**
     * 書類の向きのインテントキー.
     */
    String APP_KEY_DOCUMENT_SIDE = "APP_KEY_DOCUMENT_SIDE";

    /**
     * 書類枠のインテントキー.
     */
    String APP_KEY_GUIDE_POINT = "APP_KEY_GUIDE_POINT";

    /**
     * チュートリアルのユニークキー.
     */
    String APP_KEY_TUTORIAL_TYPE = "APP_KEY_TUTORIAL_TYPE";

    /**
     * 書類表全画像のキー文字列.
     */
    String APP_KEY_FRONT_FULL_IMAGE = "APP_KEY_FRONT_FULL_IMAGE";

    /**
     * 書類表ドキュメント画像のキー文字列.
     */
    String APP_KEY_FRONT_DOCUMENT_IMAGE = "APP_KEY_FRONT_DOCUMENT_IMAGE";

    /**
     * 書類表補正後のキー文字列.
     */
    String APP_KEY_FRONT_CORRECTED_IMAGE = "APP_KEY_FRONT_CORRECTED_IMAGE";

    /**
     * 書類表斜め画像のキー文字列.
     */
    String APP_KEY_TILTED_FULL_IMAGE = "APP_KEY_TILTED_FULL_IMAGE";

    /**
     * 書類斜めドキュメント画像のキー文字列.
     */
    String APP_KEY_TILTED_DOCUMENT_IMAGE = "APP_KEY_TILTED_DOCUMENT_IMAGE";

    /**
     * 書類斜め補正後のキー文字列.
     */
    String APP_KEY_TILTED_CORRECTED_IMAGE = "APP_KEY_TILTED_CORRECTED_IMAGE";

    /**
     * 書類裏全画像のキー文字列.
     */
    String APP_KEY_BACK_FULL_IMAGE = "APP_KEY_BACK_FULL_IMAGE";

    /**
     * 書類裏ドキュメント画像のキー文字列.
     */
    String APP_KEY_BACK_DOCUMENT_IMAGE = "APP_KEY_BACK_DOCUMENT_IMAGE";

    /**
     * 汎用全画像のキー文字列.
     */
    String APP_KEY_FULL_IMAGE = "APP_KEY_FULL_IMAGE";

    /**
     * 汎用ドキュメント画像のキー文字列.
     */
    String APP_KEY_DOCUMENT_IMAGE = "APP_KEY_DOCUMENT_IMAGE";

    /**
     * 汎用補正後画像のキー文字列.
     */
    String APP_KEY_CORRECTED_IMAGE = "APP_KEY_CORRECTED_IMAGE";

    /**
     * 直接カメラ画面を起動するフラグ.
     */
    String APP_KEY_DIRECT_START_CAPTURE = "APP_KEY_DIRECT_START_CAPTURE";

    /**
     * 撮影タイプ.
     * ・免許証
     * ・マイナンバーカード
     * ・在留カード
     */
    String APP_KEY_CAPTURE_KIND = "APP_KEY_CAPTURE_KIND";

}
