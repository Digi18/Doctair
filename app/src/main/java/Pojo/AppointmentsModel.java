package Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppointmentsModel {

    @SerializedName("data")
    @Expose
    private List<Appointments> data = null;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("code")
    @Expose
    private Integer code;

    public AppointmentsModel(){
    }

    public List<Appointments> getData() {
        return data;
    }

    public void setData(List<Appointments> data) {
        this.data = data;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
