package sheva.singapp.mvp.ui.interfaces;

import java.util.List;

import sheva.singapp.mvp.model.entities.ExerciseResultWrapper;

/**
 * Created by Vlad Aspire Hanych on 24.01.2018.
 */

public interface IProgressReportView extends IView {
    void onProgressReportsLoaded(List<ExerciseResultWrapper> exerciseResultWrappersList);
    void onError(String message);
}
