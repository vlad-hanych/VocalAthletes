
package sheva.singapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExerciseWrapper implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("exercise")
    @Expose
    private Exercise exercise;

    @SerializedName("video_file")
    @Expose
    private String videoFile;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ExerciseWrapper() {
    }

    /**
     * 
     * @param id
     * @param videoFile
     * @param exercise
     */
    public ExerciseWrapper(Integer id, Exercise exercise, String videoFile) {
        super();
        this.id = id;
        this.exercise = exercise;
        this.videoFile = videoFile;
    }

    /*protected ExerciseWrapper(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        exercise = in.readParcelable(Exercise.class.getClassLoader());

        videoFile = in.readString();
        if (in.readByte() == 0) {
            student = null;
        } else {
            student = in.readInt();
        }
    }*/

    protected ExerciseWrapper(Parcel in) {
        id = in.readInt();
        exercise = in.readParcelable(Exercise.class.getClassLoader());
    }

    public static final Creator<ExerciseWrapper> CREATOR = new Creator<ExerciseWrapper>() {
        @Override
        public ExerciseWrapper createFromParcel(Parcel in) {
            return new ExerciseWrapper(in);
        }

        @Override
        public ExerciseWrapper[] newArray(int size) {
            return new ExerciseWrapper[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public String getVideoFile() {
        return videoFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeParcelable(exercise, flags);
    }
}
