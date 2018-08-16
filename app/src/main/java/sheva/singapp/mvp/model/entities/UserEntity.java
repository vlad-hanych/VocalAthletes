package sheva.singapp.mvp.model.entities;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */

public class UserEntity {
    private String username;
    private String password;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "{\"username\":\"" + username + "\",\"password\":\"" + password +"\"}";
    }
}
