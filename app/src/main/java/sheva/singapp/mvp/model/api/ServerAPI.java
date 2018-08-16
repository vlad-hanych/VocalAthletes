package sheva.singapp.mvp.model.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;
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

public interface ServerAPI {
    @POST("/authorization/login/")
    Observable<ResultEntity> loginUser(@Body UserEntity entity);

    @POST("/authorization/logout/")
    Observable<LogoutEntity> logoutUser();

    @GET("/exercises/my_exercise_results/")
    Observable<List<ExerciseWrapper>> getSongsList(@Header("Authorization") String token);

    @GET
    Observable<ResponseBody> getMIDIFile(@Url String fileUrl);

    @POST("/exercises/upload_video_file/{exercise_results_id}/")
    @Multipart
    Observable<ResponseBody> uploadResultVideo(@Header("Authorization") String token,

                                               @Part MultipartBody.Part file,
                                               @Path("exercise_results_id") String exerciseResultsId);

    @PATCH("/exercises/update_exercise_results/{exercise_results_id}/")
    Observable<ResponseBody> uploadScores(@Header("Authorization") String token, @Body Scores scores, @Path("exercise_results_id") String exerciseResultsId);

    @GET("/exercises/my_exercise_results/")
    Observable<List<ExerciseResultWrapper>> getProgressReportsList(@Header("Authorization") String token);
}
