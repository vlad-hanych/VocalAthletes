package sheva.singapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vlad Aspire Hanych on 16.03.2018.
 */

public class ExerciseResultWrapper {
    @SerializedName("exercise")
    @Expose
    private ExerciseResult exerciseResult;

    @SerializedName("video_file")
    @Expose
    private String videoFile;

    @SerializedName("tempo_score")
    @Expose
    private String tempoScore;

    @SerializedName("pitch_score")
    @Expose
    private String pitchScore;

    public ExerciseResult getExerciseResult() {
        return exerciseResult;
    }

    public String getVideoFile() {
        return videoFile;
    }

    public String getTempoScore() {
        return tempoScore;
    }

    public String getPitchScore() {
        return pitchScore;
    }
}
