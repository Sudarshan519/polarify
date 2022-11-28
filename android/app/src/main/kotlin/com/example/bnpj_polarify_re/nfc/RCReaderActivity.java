package com.example.bnpj_polarify_re.nfc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bnpj_polarify_re.BaseActivity;
import com.example.bnpj_polarify_re.R;

import io.flutter.plugin.common.MethodChannel;

public class RCReaderActivity extends BaseActivity implements MethodChannel.Result{
    private String rcNumber;
private Button button;
ImageView imageView;
TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_activity_main);

        imageView = findViewById(R.id.scanImage);
        textView = findViewById(R.id.message);
        button=findViewById(R.id.cancel_button);
        getAnimation();
        Intent intent=getIntent();
        rcNumber = intent.getStringExtra("documentNumber");
          button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

               finish();
             }
         });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        RCReaderTask task = new RCReaderTask(this, tag);
        task.execute();
    }

    @Override
    public void success(@Nullable Object result) {

    }

    @Override
    public void error(String errorCode, @Nullable String errorMessage, @Nullable Object errorDetails) {

    }

    @Override
    public void notImplemented() {

    }


//    getRC
   String getRcNumber(){
        return rcNumber;
    }

    private void getAnimation(){
        Glide.with(this).load(R.raw.tap_to_scan).listener(new RequestListener<Drawable>() {
            /**
             * 画像の読み込み失敗時に呼び出されます.
             */
            @Override
            public boolean onLoadFailed(
                    @Nullable final GlideException e,
                    @NonNull final Object model,
                    @NonNull final Target target,
                    final boolean isFirstResource) {
                return false;
            }

            /**
             * 画像の読み込み成功時に呼び出されます.
             */
            @Override
            public boolean onResourceReady(
                    @NonNull final Drawable resource,
                    @NonNull final Object model,
                    @NonNull final Target<Drawable> target,
                    @NonNull final DataSource dataSource,
                    final boolean isFirstResource) {
                if (!(resource instanceof GifDrawable)) {
                    return false;
                } else {
                    ((GifDrawable) resource).setLoopCount(1);
                    return false;
                }
            }
        }).into(imageView);
    }


    protected void setMessage(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(message);
            }
        });
    }

    public MethodChannel.Result getResult() {
        return this;
    }
}