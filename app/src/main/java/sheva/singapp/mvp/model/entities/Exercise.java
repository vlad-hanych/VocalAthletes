
package sheva.singapp.mvp.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exercise implements Parcelable{

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("back_music_file")
    @Expose
    private String audioFile;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Exercise() {
    }

    /**
     *
     * @param title
     * @param audioFile
     */
    public Exercise(String title, String audioFile) {
        super();
        this.title = title;
        this.audioFile = audioFile;
    }

    protected Exercise(Parcel in) {
        title = in.readString();
        audioFile = in.readString();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAudioFile() {
        return audioFile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(audioFile);
    }
}
