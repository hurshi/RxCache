### RxCache
* Focked from   [VictorAlbertos/RxCache](https://github.com/VictorAlbertos/RxCache)
* Read doc from  [VictorAlbertos/RxCache](https://github.com/VictorAlbertos/RxCache)

### Diff from [VictorAlbertos/RxCache](https://github.com/VictorAlbertos/RxCache)

1. UseExpiredDataIfLoaderNotAvailable
    1. Features:

       1. Enable feature descripted by `.useExpiredDataIfLoaderNotAvailable`
       2. Use local overdue data if network has some error

    2. Deprecated( Removed ) ~~RxCache.Builder().useExpiredDataIfLoaderNotAvailable~~

    3. Add annotation `@UseExpiredDataIfNotLoaderAvailable`

    4. Usage:

       ```java
       @UseExpiredDataIfNotLoaderAvailable
       @LifeCache(duration = 2, timeUnit = TimeUnit.HOURS)
       public Observable<Person> getPersion(Observable<Person> personSingle)

       ```

2. Interceptor

   1. Deprecated encryption features

      ```java
      // @Encrypt
      // @EncryptKey
      ```

   2. add Interceptor, you can realize encrypt by interceptor

   3. Usage: [to Demo](./android/src/main/java/victoralbertos/io/android/MainActivity.java)

      ```
      import io.rx_cache2.internal.interceptor.Interceptor;
      public class EncryptionInterceptor implements Interceptor {
          @Override
          public String onSave(String string) {
              return AESUtil.encrypt(string);
          }
      
          @Override
          public String onRetrieve(String string) {
              return AESUtil.decrypt(string);
          }
      }
      
      ```

      ```
      private CommonCache getCommonCache() {
              return new RxCache.Builder()
                      .addInterceptor(new EncryptionInterceptor())
                      .persistence(...)
                      .using(CommonCache.class);
      }
      ```

      ```
      @Interceptors(classes = EncryptionInterceptor.class)
      @LifeCache(duration = 10, timeUnit = TimeUnit.SECONDS)
      Single<CurTime> getCurTime(Single<CurTime> single);
      ```

   4. you can do more with interceptor.