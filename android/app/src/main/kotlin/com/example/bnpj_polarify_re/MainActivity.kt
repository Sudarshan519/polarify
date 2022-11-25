package com.example.bnpj_polarify_re
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import com.example.bnpj_polarify_re.ekyc.EkycMainActivity
import com.example.bnpj_polarify_re.nfc.RCReaderActivity
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterFragmentActivity() {
    private val EKYC_CHANNEL = "com.nitv.bnpjcredit/ekyc"
    private val NFC_CHANNEL = "com.nitv.bnpjcredit/nfc"

    private var result: MethodChannel.Result? = null
    object MainActivityObject {
        const val NFC_RESULT = 111
        const val KYC_RESULT = 222
    }
    companion object {
        var isManualEKYC: Boolean = false
        var ICProfileImage: String = ""
    }
       override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        setMethodChannel()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setMethodChannel(){
        MethodChannel(flutterEngine?.dartExecutor?.binaryMessenger,NFC_CHANNEL).setMethodCallHandler{
                call, result ->
            this.result = result
            if(call.method.equals("openDocumentScanner")){
                val documentType: String = call.argument("document_type") ?: ""
                val documentNumber: String = call.argument("document_number") ?: ""
                val pin2: String = call.argument("pin2") ?: ""

                when(documentType){
                    "RESIDENCE_CARD" ->
                    {

                        val intent = Intent(this, RCReaderActivity::class.java)
                        intent.putExtra("documentNumber", documentNumber)
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivityForResult(intent, MainActivityObject.NFC_RESULT)

                    }
                }
            }else if(call.method.equals("isNFCAvailable")){
                val mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
                if (mNfcAdapter == null) {
                    result.success(true)
                }else{
                    result.success(true)
                }
            }
        } ;
        MethodChannel(flutterEngine?.dartExecutor?.binaryMessenger,EKYC_CHANNEL).setMethodCallHandler{
                call, result ->
            this.result = result

            if(call.method.equals("openFrontDocumentScanner")){
                val intent = Intent(this, jp.co.polarify.onboarding.app.MainActivity::class.java)

                val documentType: String = call.argument("documentType") ?: ""
                val isManualEKYCString: String = call.argument("isManualEKYC") ?: "1"
                val ICProfileImageString: String = call.argument("ICProfileImage") ?: ""

                isManualEKYC = isManualEKYCString == "1"
                ICProfileImage = ICProfileImageString

                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                intent.putExtra("fromDocumentScanner", true)
                intent.putExtra("documentType", documentType)
                startActivityForResult(intent, MainActivityObject.KYC_RESULT)
            }
            else if(call.method.equals("openBackDocumentScanner")){
                result.notImplemented()
            }
            else{
                result.notImplemented()
            }

        } ;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode ==MainActivityObject.NFC_RESULT) {
//            val hashMap = data?.getSerializableExtra("data")
//            this.result?.success(hashMap)
//        }
    }
}
