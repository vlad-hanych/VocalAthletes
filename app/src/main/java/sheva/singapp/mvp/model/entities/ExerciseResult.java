package sheva.singapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vlad Aspire Hanych on 16.03.2018.
 */

public class ExerciseResult {
    @SerializedName("title")
    @Expose
    private String title;

    public ExerciseResult(String title) {
        super();

        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
