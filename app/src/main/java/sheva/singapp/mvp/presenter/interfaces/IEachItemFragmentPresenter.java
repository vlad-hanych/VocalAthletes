package sheva.singapp.mvp.presenter.interfaces;

/**
 * Created by shevc on 23.07.2017.
 * Let's GO!
 */

public interface IEachItemFragmentPresenter extends IPresenter {
    void loadSongs();
    void showError(String message, String title);
}
