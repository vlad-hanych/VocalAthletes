package sheva.singapp.mvp.presenter.activities;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import sheva.singapp.App;
import sheva.singapp.mvp.datamanager.DataManager;
import sheva.singapp.mvp.model.entities.ExerciseResultWrapper;
import sheva.singapp.mvp.presenter.base.BasePresenter;
import sheva.singapp.mvp.presenter.interfaces.IProgressReportActivityPresenter;
import sheva.singapp.mvp.ui.interfaces.IProgressReportView;

/**
 * Created by Vlad Aspire Hanych on 24.01.2018.
 */

public class ProgressReportActivityPresenter  extends BasePresenter<IProgressReportView> implements IProgressReportActivityPresenter {
    @Inject
    DataManager manager;

    public ProgressReportActivityPresenter() {
        App.get().getAppComponent().inject(ProgressReportActivityPresenter.this);
    }

    @Override
    public void getProgressReports() {
        if (manager.isLogged()){
            final List<ExerciseResultWrapper> exerciseResultWrappersList = new ArrayList<>();
            manager.getProgressReports()
                    .subscribe(new Subscriber<ExerciseResultWrapper>() {
                        @Override
                        public void onCompleted() {
                            getView().onProgressReportsLoaded(exerciseResultWrappersList);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("MY", e.getMessage());
                        }

                        @Override
                        public void onNext(ExerciseResultWrapper exerciseResultWrapper) {
                            if (exerciseResultWrapper.getTempoScore() != null)
                                exerciseResultWrappersList.add(exerciseResultWrapper);
                        }
                    });
        } else {
            getView().onError("Erroro");
        }
    }

    @Override
    public void destroyPresenter() {

    }
}
