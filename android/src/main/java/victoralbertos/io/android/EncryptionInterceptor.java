package victoralbertos.io.android;

import android.util.Log;

import io.rx_cache2.internal.interceptor.Interceptor;

public class EncryptionInterceptor implements Interceptor {
    private final String TEST = "HELLOWORLD";

    @Override
    public String onSave(String string) {
        return string + TEST;
    }

    @Override
    public String onRetrieve(String string) {
        return string.replace(TEST, "");
    }
}
