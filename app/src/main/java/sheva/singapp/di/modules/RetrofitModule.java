package sheva.singapp.di.modules;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import sheva.singapp.App;
import sheva.singapp.mvp.model.api.ServerAPI;
import sheva.singapp.mvp.model.repositories.ServerRepository;

/**
 * Created by shevc on 09.07.2017.
 * Let's GO!
 */
@Module
public class RetrofitModule {

    @Provides
    @Singleton
    public ServerAPI provideServerAPI(Gson gson){
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(App.BASE_SERVER_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        okHttpClientBuilder.interceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                // Check the method first.

                HttpUrl url = request.url();

                Log.d("Request URL", String.valueOf(url));

                Response response = chain.proceed(request);

                /*if (!response.headers("Set-Cookie").isEmpty()) {
                    HashSet<String> cookies = new HashSet<>();

                    cookies.addAll(response.headers("Set-Cookie"));
                }*/

                String responseString = response.body().string();

                Log.d("Response", responseString);

                request = request.newBuilder()
                        .url(url)
                        .build();

                // Proceed with chaining requests.
                return chain.proceed(request);
            }
        });

        OkHttpClient client = okHttpClientBuilder.build();

        Retrofit retrofit = retrofitBuilder.client(client).build();

        return retrofit.create(ServerAPI.class);
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }

    @Provides
    @Singleton
    public ServerRepository provideServerRepository() {
        return new ServerRepository();
    }
}
