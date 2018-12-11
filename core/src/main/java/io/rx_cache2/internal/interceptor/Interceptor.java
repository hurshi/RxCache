package io.rx_cache2.internal.interceptor;

public interface Interceptor {
    String onSave(String string);

    String onRetrieve(String string);

}
