package sheva.singapp.mvp.ui.interfaces;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */

public interface ISettingsActivityView extends IActivityView{
    void loginUser(String username, String password);
    void showAlertInfo();
    void showLoginFragment();
    void setUsername(String username);
    void onSuccessfullyLogged(String username);
    void onLoginError();
    void onLogoutSuccess();
    void onLogoutError();
}
