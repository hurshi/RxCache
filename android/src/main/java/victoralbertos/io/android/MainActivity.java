package victoralbertos.io.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by victor on 21/01/16.
 */
public class MainActivity extends Activity {
    private static final String TAG = ">>> RxCache TEST: ";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textview);

        getCommonCache().getCurTime(getCommonService().getCurTime())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CurTime>() {
                    @Override
                    public void accept(CurTime time) throws Exception {
                        textView.setText(time.getResult().getDatetime_2());
                        Log.e(TAG, "api = " + new GsonSpeaker().toJson(time));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "get api wrong throw:");
                        throwable.printStackTrace();
                    }
                });


    }


    private CommonService getCommonService() {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(new OkHttpClient.Builder()
//                        .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CommonService.class);
    }

    private CommonCache getCommonCache() {
        return new RxCache.Builder()
                .persistence(MainActivity.this.getExternalCacheDir(), new GsonSpeaker())
                .using(CommonCache.class);
    }
}
