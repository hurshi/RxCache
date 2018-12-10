package victoralbertos.io.android;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.rx_cache2.LifeCache;

public interface CommonCache {

    @LifeCache(duration = 1, timeUnit = TimeUnit.SECONDS)
    Single<User> getUser(Single<User> single);
}
