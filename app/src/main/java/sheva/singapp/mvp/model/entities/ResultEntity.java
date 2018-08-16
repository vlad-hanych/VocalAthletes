package sheva.singapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */

public class ResultEntity {
    @SerializedName("key")
    @Expose
    private String key;

    /**
     * No args constructor for use in serialization
     *
     */
    public ResultEntity() {
    }

    public ResultEntity(String key) {
        super();
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
