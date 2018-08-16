package sheva.singapp.mvp.presenter.fragments;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.functions.Func1;
import sheva.singapp.App;
import sheva.singapp.mvp.datamanager.DataManager;
import sheva.singapp.mvp.model.entities.Exercise;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;
import sheva.singapp.mvp.presenter.base.BasePresenter;
import sheva.singapp.mvp.ui.interfaces.IEachItemFragmentView;

/**
 * Created by shevc on 23.07.2017.
 * Let's GO!
 */

public class EachItemFragmentPresenter extends BasePresenter<IEachItemFragmentView> implements sheva.singapp.mvp.presenter.interfaces.IEachItemFragmentPresenter {
    @Inject
    DataManager manager;

    public EachItemFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void destroyPresenter() {
        cleanView();
    }

    @Override
    public void loadSongs() {
        if (manager.isLogged()){
            final List<ExerciseWrapper> exerciseWrappersList = new ArrayList<>();
            manager.getSongs()
                    .subscribe(new Subscriber<ExerciseWrapper>() {
                        @Override
                        public void onCompleted() {
                            getView().onSongsLoaded(exerciseWrappersList);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("MY", e.getMessage());
                        }

                        @Override
                        public void onNext(ExerciseWrapper exerciseWrapper) {
                            exerciseWrappersList.add(exerciseWrapper);
                        }
                    });
        } else {
            getView().showError("You are not logged!", "Error");
        }
    }


    @Override
    public void showError(String message, String title) {
        getView().showError(message, title);
    }
}
