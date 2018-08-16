package sheva.singapp.mvp.model.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vlad Aspire Hanych on 15.03.2018.
 */

public class Scores {
    @SerializedName("tempo_score")
    @Expose
    private int tempoScore;

    @SerializedName("pitch_score")
    @Expose
    private int pitchScore;

    public Scores(int tempoScore, int pitchScore) {
        this.tempoScore = tempoScore;
        this.pitchScore = pitchScore;
    }
}
