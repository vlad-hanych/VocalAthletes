package sheva.singapp.mvp.presenter.interfaces;

import java.io.File;

/**
 * Created by shevc on 11.07.2017.
 * Let's GO!
 */

public interface ISingProccessActivityPresenter extends IPresenter{
    void getSongs(String type);
    void getMIDI(String endpoint);
    void uploadVideo(File videoFile, String exerciseId);
    void uploadScores(int rhytm, int pitch, String exerciseId);
}
