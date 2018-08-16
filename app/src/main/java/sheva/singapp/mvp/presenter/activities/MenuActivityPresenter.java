package sheva.singapp.mvp.presenter.activities;

import android.util.Log;

import javax.inject.Inject;

import rx.Subscriber;
import sheva.singapp.App;
import sheva.singapp.mvp.datamanager.DataManager;
import sheva.singapp.mvp.model.entities.LogoutEntity;
import sheva.singapp.mvp.model.entities.ResultEntity;
import sheva.singapp.mvp.presenter.base.BasePresenter;
import sheva.singapp.mvp.presenter.interfaces.IMenuActivityPresenter;
import sheva.singapp.mvp.ui.interfaces.IMenuActivityView;

/**
 * Created by shevc on 10.07.2017.
 * Let's GO!
 */

public class MenuActivityPresenter extends BasePresenter<IMenuActivityView> implements IMenuActivityPresenter {
    @Inject
    DataManager manager;

    public MenuActivityPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void destroyPresenter() {
        cleanView();
    }

    @Override
    public void loginUser(final String username, String password) {
        manager.loginUser(username, password).subscribe(new Subscriber<ResultEntity>() {
            @Override
            public void onCompleted() {
                Log.e("MY", "Completed");
                getView().onSuccessfullyLogged(username);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("MY", e.getMessage());
                getView().onLoginError();
            }

            @Override
            public void onNext(ResultEntity resultEntity) {
                manager.rememberUser(username, resultEntity.getKey());
            }
        });
    }

    @Override
    public void showAlertInfo() {
        getView().showAlertInfo();
    }

    @Override
    public void showLoginFragment() {
        getView().showLoginFragment();
    }

    @Override
    public void getUsername() {
        String username = manager.getUsername();
        if (username != null) {
            getView().setUsername(username);
        }
    }

    @Override
    public void logout() {
        manager.logOut().subscribe(new Subscriber<LogoutEntity>() {
            @Override
            public void onCompleted() {
                Log.e("MY", "Completed");
                getView().onLogoutSuccess();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("MY", e.getMessage());
                getView().onLogoutError();
            }

            @Override
            public void onNext(LogoutEntity logoutEntity) {
                //logout success
            }
        });
    }
}
