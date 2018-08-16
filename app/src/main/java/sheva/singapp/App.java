package sheva.singapp;

import android.app.Application;

import sheva.singapp.di.components.AppComponent;
import sheva.singapp.di.components.DaggerAppComponent;
import sheva.singapp.di.modules.AppModule;

/**
 * Created by shevc on 06.07.2017.
 * Let's GO!
 */

public class App extends Application {
    public static final String BASE_SERVER_URL = "http://174.138.54.52";

    private static App instance;
    private AppComponent appComponent;
    public static App get() {
        return instance;
    }
    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(instance))
                .build();
    }
}
