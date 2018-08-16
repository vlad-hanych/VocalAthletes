package sheva.singapp.mvp.ui.interfaces;

/**
 * Created by shevc on 10.07.2017.
 * Let's GO!
 */

public interface IMenuActivityView extends IView {
    void loginUser(String username, String password);
    void showAlertInfo();
    void showLoginFragment();
    void setUsername(String username);
    void onSuccessfullyLogged(String username);
    void onLoginError();
    void onLogoutSuccess();
    void onLogoutError();
}
