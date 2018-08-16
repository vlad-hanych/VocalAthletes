package sheva.singapp.mvp.model.repositories;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import sheva.singapp.App;
import sheva.singapp.mvp.model.api.ServerAPI;
import sheva.singapp.mvp.model.entities.ExerciseResult;
import sheva.singapp.mvp.model.entities.ExerciseResultWrapper;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;
import sheva.singapp.mvp.model.entities.LogoutEntity;
import sheva.singapp.mvp.model.entities.ResultEntity;
import sheva.singapp.mvp.model.entities.Scores;
import sheva.singapp.mvp.model.entities.UserEntity;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */

public class ServerRepository {
    @Inject
    ServerAPI api;

    public ServerRepository() {
        App.get().getAppComponent().inject(this);
    }

    public Observable<ResultEntity> loginUser(String username, String password) {
        Log.d("MY", "sending request");
        return api.loginUser(new UserEntity(username, password))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<LogoutEntity> logoutUser() {
        return api.logoutUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ExerciseWrapper> getSongsList(String token) {
        return api.getSongsList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<ExerciseWrapper>, Observable<ExerciseWrapper>>() {
                    @Override
                    public Observable<ExerciseWrapper> call(List<ExerciseWrapper> exerciseWrappers) {
                        return Observable.from(exerciseWrappers);
                    }
                });
    }

    public Observable<ResponseBody> getMidiFile(String endpoint) {
        return api.getMIDIFile(endpoint)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ResponseBody> uploadVideoFile(String token, MultipartBody.Part body, String exerciseId) {
        return api.uploadResultVideo(token, body, exerciseId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ResponseBody> uploadScore(String token, Scores scores, String exerciseId) {
        return api.uploadScores(token, scores, exerciseId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ExerciseResultWrapper> getProgressReportsList(String token) {
        return api.getProgressReportsList(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<List<ExerciseResultWrapper>, Observable<ExerciseResultWrapper>>() {
                    @Override
                    public Observable<ExerciseResultWrapper> call(List<ExerciseResultWrapper> exerciseResultWrappers) {
                        return Observable.from(exerciseResultWrappers);
                    }
                });
    }
}
