package sheva.singapp.mvp.ui.interfaces;

import java.util.List;

import sheva.singapp.mvp.model.entities.Exercise;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;

/**
 * Created by shevc on 23.07.2017.
 * Let's GO!
 */

public interface IEachItemFragmentView extends IView {
    void onSongsLoaded(List<ExerciseWrapper> exerciseList);
    void showError(String message, String title);
}
