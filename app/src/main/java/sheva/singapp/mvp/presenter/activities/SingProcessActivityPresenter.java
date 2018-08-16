package sheva.singapp.mvp.presenter.activities;

import android.util.Log;

import java.io.File;
import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;
import sheva.singapp.App;
import sheva.singapp.mvp.datamanager.DataManager;
import sheva.singapp.mvp.model.entities.Scores;
import sheva.singapp.mvp.presenter.base.BasePresenter;
import sheva.singapp.mvp.presenter.interfaces.ISingProccessActivityPresenter;
import sheva.singapp.mvp.ui.interfaces.ISingProcessActivityView;

/**
 * Created by shevc on 11.07.2017.
 * Let's GO!
 */

public class SingProcessActivityPresenter extends BasePresenter<ISingProcessActivityView> implements ISingProccessActivityPresenter {
    private static final String MEDIA_TYPE = "multipart/form-data";

    private static final String REQUEST_BODY_KEY = "file";

    @Inject
    DataManager manager;

    public SingProcessActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void getSongs(String type) {

    }

    @Override
    public void getMIDI(String endpoint) {
        manager.getMIDIFile(endpoint).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                ///Log.e("MY", "Completed");

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                InputStream inputStream;

                ///byte[] fileReader = new byte[4096];

                ///long fileSize = responseBody.contentLength();
                ///long fileSizeDownloaded = 0;

                inputStream = responseBody.byteStream();

                    /*while (true) {
                        int read = inputStream.read(fileReader);

                        if (read == -1) {
                            break;
                        }

                        fileSizeDownloaded += read;


                        Log.d("TAG", "file download: " + fileSizeDownloaded + " of " + fileSize);
                    }*/

                getView().onMIDIDownloaded(inputStream);

            }
        });
    }

    @Override
    public void uploadVideo(File videoFile, String exerciseId) {
        // creates RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse(MEDIA_TYPE), videoFile);
        // MultipartBody.Part is used to send also the actual filename
        MultipartBody.Part body = MultipartBody.Part.createFormData(REQUEST_BODY_KEY, videoFile.getName(), requestFile);
        // adds another part within the multipart request
        // executes the request

        manager.uploadVideoFile(body, exerciseId).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                Log.d("uploadVideo...", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("uploadVideo...", "onError");
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                Log.d("uploadVideo...", "onNext");
            }
        });
    }

    @Override
    public void uploadScores(int rhytm, int pitch, String exerciseId) {
        manager.uploadScores(new Scores(rhytm, pitch), exerciseId).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                Log.d("uploadScores...", "onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("uploadScores...", "onError");
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                Log.d("uploadScores...", "onNext");
            }
        });
    }

    @Override
    public void destroyPresenter() {
        cleanView();
    }
}
