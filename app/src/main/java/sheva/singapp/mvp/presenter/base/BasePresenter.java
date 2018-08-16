package sheva.singapp.mvp.presenter.base;

import android.support.annotation.Nullable;

import sheva.singapp.mvp.ui.interfaces.IView;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */

public abstract class BasePresenter<V extends IView> {

    @Nullable
    private V v;

    public void attachView(V v) {
        this.v = v;
    }

    @Nullable
    public V getView() {
        return v;
    }

    public void cleanView() {
        v = null;
    }
}
