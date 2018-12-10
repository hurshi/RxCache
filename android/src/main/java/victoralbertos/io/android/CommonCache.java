package victoralbertos.io.android;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.rx_cache2.LifeCache;
import io.rx_cache2.UseExpiredDataIfNotLoaderAvailable;

public interface CommonCache {

    @LifeCache(duration = 1, timeUnit = TimeUnit.SECONDS)
    Single<User> getUser(Single<User> single);

    @UseExpiredDataIfNotLoaderAvailable
    @LifeCache(duration = 10, timeUnit = TimeUnit.SECONDS)
    Single<CurTime> getCurTime(Single<CurTime> single);
}
