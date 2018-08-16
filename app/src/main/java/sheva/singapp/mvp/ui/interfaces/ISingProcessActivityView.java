package sheva.singapp.mvp.ui.interfaces;

import java.io.InputStream;

/**
 * Created by shevc on 11.07.2017.
 * Let's GO!
 */

public interface ISingProcessActivityView extends IActivityView {
    void downloadMIDIofMelodyInput();
    void onMIDIDownloaded(InputStream midiInputStream);
    void onUploadScores(int rhytm, int pitch);
    void onSongLoaded();
    void onVideoRecordingEnded();
    void onError(String message);
}
