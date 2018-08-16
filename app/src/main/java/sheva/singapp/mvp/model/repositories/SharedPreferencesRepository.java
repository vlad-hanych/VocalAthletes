package sheva.singapp.mvp.model.repositories;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */

public class SharedPreferencesRepository {
    private static final String USERNAME_KEY = "username";
    private static final String IS_LOGGED = "islogged";
    private static final String TOKEN = "token";
    private static final String PREFERENCES_NAME = "SingAppUserInfo";

    private SharedPreferences sp;

    public SharedPreferencesRepository(Context context) {
        sp = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getUsername() {
        return sp.getString(USERNAME_KEY, "");
    }

    public String getToken() {
        return sp.getString(TOKEN, "");
    }

    public boolean isLogged() {
        return sp.getBoolean(IS_LOGGED, false);
    }

    public void rememberUser(String username, String token) {
        sp.edit()
                .putString(USERNAME_KEY, username)
                .putBoolean(IS_LOGGED, true)
                .putString(TOKEN, token)
                .apply();
    }

    public void deleteCurrentUser() {
        sp.edit()
                .putString(USERNAME_KEY, "")
                .putBoolean(IS_LOGGED, false)
                .putString(TOKEN, "")
                .apply();
    }
}
