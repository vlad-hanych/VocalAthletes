package sheva.singapp.mvp.datamanager;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import rx.Observable;
import sheva.singapp.App;
import sheva.singapp.mvp.model.entities.ExerciseResultWrapper;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;
import sheva.singapp.mvp.model.entities.LogoutEntity;
import sheva.singapp.mvp.model.entities.ResultEntity;
import sheva.singapp.mvp.model.entities.Scores;
import sheva.singapp.mvp.model.repositories.ServerRepository;
import sheva.singapp.mvp.model.repositories.SharedPreferencesRepository;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */

public class DataManager {
    @Inject
    public ServerRepository serverRepository;
    @Inject
    public SharedPreferencesRepository sharedPreferencesRepository;

    public DataManager() {
        App.get().getAppComponent().inject(this);
    }

    public Observable<ResultEntity> loginUser(String username, String password) {
        return serverRepository.loginUser(username, password);
    }

    public void rememberUser(String username, String token) {
        sharedPreferencesRepository.rememberUser(username, token);
    }

    public String getUsername() {
        if (sharedPreferencesRepository.isLogged()) {
            return sharedPreferencesRepository.getUsername();
        } else {
            return null;
        }
    }

    public boolean isLogged() {
        return sharedPreferencesRepository.isLogged();
    }

    public String getToken() {
        return sharedPreferencesRepository.getToken();
    }

    public Observable<LogoutEntity> logOut() {
        sharedPreferencesRepository.deleteCurrentUser();
        return serverRepository.logoutUser();
    }

    public Observable<ExerciseWrapper> getSongs() {
        return serverRepository.getSongsList("Token " + getToken());
    }

    public Observable<ResponseBody> getMIDIFile(String endpoint) {
        return serverRepository.getMidiFile(endpoint);
    }

    public Observable<ResponseBody> uploadVideoFile(MultipartBody.Part body, String exerciseId) {
        return serverRepository.uploadVideoFile("Token " + getToken(), body, exerciseId);
    }

    public Observable<ResponseBody> uploadScores(Scores scores, String exerciseId) {
        return serverRepository.uploadScore("Token " + getToken(), scores, exerciseId);
    }

    public Observable<ExerciseResultWrapper> getProgressReports() {
        return serverRepository.getProgressReportsList("Token " + getToken());
    }
}
