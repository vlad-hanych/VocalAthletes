package sheva.singapp.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import sheva.singapp.mvp.datamanager.DataManager;
import sheva.singapp.mvp.model.repositories.SharedPreferencesRepository;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */
@Module
public class AppModule {
    private Context context;

    private Application application;
    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext(){
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public DataManager provideDataManager() {
        return new DataManager();
    }

    @Provides
    @Singleton
    public SharedPreferencesRepository providePreferencesRepository(Context context) {
        return new SharedPreferencesRepository(context);
    }
}
