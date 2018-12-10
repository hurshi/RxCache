package victoralbertos.io.android;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface CommonService {
    @GET("users/hurshi1")
    Single<User> getUsers();

    @GET("http://api.k780.com:88/?app=life.time&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=json")
    Single<CurTime> getCurTime();
}
