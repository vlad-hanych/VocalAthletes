package sheva.singapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shevc on 10.07.2017.
 * Let's GO!
 */

public class LogoutEntity {
    @SerializedName("detail")
    @Expose
    private String detail;

    /**
     * No args constructor for use in serialization
     *
     */
    public LogoutEntity() {
    }

    /**
     *
     * @param detail
     */
    public LogoutEntity(String detail) {
        super();
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
