package sheva.singapp.mvp.presenter.interfaces;

/**
 * Created by shevc on 10.07.2017.
 * Let's GO!
 */

public interface IMenuActivityPresenter extends IPresenter {
    void loginUser(String username, String password);
    void showAlertInfo();
    void showLoginFragment();
    void getUsername();
    void logout();
}
