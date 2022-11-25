package jp.co.polarify.onboarding.app.apilogger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtilUngate {

    public static Retrofit getAdapter() {
        Retrofit retrofit;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(logging);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                //for production
                Request request = original.newBuilder()
                        .header("X-Api-Token", "jI2OLaO9K8de7f4Jcv71Eid74a3085fcppIe5")
                        .build();

                //for dev/stg
//                Request request = original.newBuilder()
//                        .header("X-Api-Token", "jI2OLa3c08de7f0fVa719id74a3085fc09cE8")
//                        .build();

                return chain.proceed(request);
            }
        });

//         for production
        retrofit = new Retrofit.Builder()
                .baseUrl("https://psrv00031.ungate.co.jp/")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        // for dev/stg
//        retrofit = new Retrofit.Builder()
//                .baseUrl("https://dsrv00031.ungate.co.jp/")
//                .client(httpClient.build())
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build();

        return retrofit;
    }
}
