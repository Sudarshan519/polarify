package jp.co.polarify.onboarding.app.apilogger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class ResponseValue {

    @SerializedName("status")
    @Expose
    private Integer status;


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}
