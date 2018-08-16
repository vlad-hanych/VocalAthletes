package sheva.singapp.mvp.presenter.activities;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.functions.Func1;
import sheva.singapp.App;
import sheva.singapp.mvp.datamanager.DataManager;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;
import sheva.singapp.mvp.model.entities.Exercise;
import sheva.singapp.mvp.presenter.base.BasePresenter;
import sheva.singapp.mvp.presenter.interfaces.ISingSongActivityPresenter;
import sheva.singapp.mvp.ui.interfaces.ISingSongActivityView;

/**
 * Created by shevc on 23.07.2017.
 * Let's GO!
 */

public class SingSongActivityPresenter extends BasePresenter<ISingSongActivityView> implements ISingSongActivityPresenter {
    @Inject
    DataManager manager;

    public SingSongActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void destroyPresenter() {
        cleanView();
    }


    @Override
    public void showError(String message, String title) {
        getView().onError(message, title, "", "");
    }
}
