package jp.co.polarify.onboarding.app.apilogger;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("api_error_log/save")
    Observable<Response<ResponseBody>> sendLogData(@Body LogModel logModel);

    @FormUrlEncoded
    @POST("/v1/api/ekyc")
    Observable<Response<ResponseBody>> sendData(@Field("company_code") String companyCode);
}
