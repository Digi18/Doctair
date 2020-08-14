package remote;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static Retrofit retrofit = null;

    public static Retrofit getInstance(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(22, TimeUnit.SECONDS)
                .readTimeout(22, TimeUnit.SECONDS)
                .writeTimeout(22, TimeUnit.SECONDS)
                .build();

        if(retrofit == null)
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://doctair.in/api_server/")
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();

        return retrofit;
    }
}
