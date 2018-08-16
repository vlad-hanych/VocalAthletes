package sheva.singapp.di.components;

import javax.inject.Singleton;

import dagger.Component;
import sheva.singapp.di.modules.AppModule;
import sheva.singapp.di.modules.RetrofitModule;
import sheva.singapp.mvp.datamanager.DataManager;
import sheva.singapp.mvp.model.repositories.ServerRepository;
import sheva.singapp.mvp.presenter.activities.MenuActivityPresenter;
import sheva.singapp.mvp.presenter.activities.ProgressReportActivityPresenter;
import sheva.singapp.mvp.presenter.activities.SingProcessActivityPresenter;
import sheva.singapp.mvp.presenter.activities.SingSongActivityPresenter;
import sheva.singapp.mvp.presenter.fragments.EachItemFragmentPresenter;
import sheva.singapp.mvp.ui.activities.MenuActivity;
import sheva.singapp.mvp.ui.activities.ProgressReportActivity;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */
@Singleton
@Component(modules = {AppModule.class, RetrofitModule.class})
public interface AppComponent {
    void inject(ServerRepository repository);
    void inject(DataManager manager);
    void inject(MenuActivity menuActivity); /// TODO чи це тут потрібно?
    void inject(MenuActivityPresenter presenter);
    void inject(SingProcessActivityPresenter presenter);
    void inject(SingSongActivityPresenter presenter);
    void inject(EachItemFragmentPresenter presenter);
    void inject(ProgressReportActivity progressReportActivity); /// TODO чи це тут потрібно?
    void inject(ProgressReportActivityPresenter presenter);
}
