package racearoundyou.ray;

import android.app.Application;

import racearoundyou.ray.API.FunctionsAPI;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

    public class Database extends Application {
    private Retrofit retrofit;
    private static FunctionsAPI Function;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://us-central1-racearoundyou-164620.cloudfunctions.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Function = retrofit.create(FunctionsAPI.class);
    }

    public static FunctionsAPI useApi(){
        return Function;
    }
}