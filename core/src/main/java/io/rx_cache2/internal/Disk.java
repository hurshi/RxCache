/*
 * Copyright 2015 Victor Albertos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rx_cache2.internal;


import com.google.common.io.BaseEncoding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.rx_cache2.internal.interceptor.Interceptor;
import io.victoralbertos.jolyglot.JolyglotGenerics;

/**
 * Save objects in disk and evict them too. It uses Gson as json parser.
 */
public final class Disk implements Persistence {
    private final File cacheDirectory;
    private final JolyglotGenerics jolyglot;
    private final Map<Class, Interceptor> interceptorMap;
    private final String QUOTES = "\"";

    @Inject
    public Disk(File cacheDirectory, JolyglotGenerics jolyglot, Map<Class, Interceptor> interceptors) {
        this.cacheDirectory = cacheDirectory;
        this.jolyglot = jolyglot;
        this.interceptorMap = interceptors;
    }

    /**
     * Save in disk the Record passed.
     *
     * @param key    the key whereby the Record could be retrieved/deleted later. @see evict and @see
     *               retrieve.
     * @param record the record to be persisted.
     */
    @Override
    public void saveRecord(String key, io.rx_cache2.internal.Record record, Class<Interceptor>[] interceptors) {
        save(key, record, interceptors);
    }

    /**
     * Retrieve the names from all files in dir
     */
    @Override
    public List<String> allKeys() {
        List<String> nameFiles = new ArrayList<>();

        File[] files = cacheDirectory.listFiles();
        if (files == null) return nameFiles;

        for (File file : files) {
            if (file.isFile()) {
                nameFiles.add(file.getName());
            }
        }

        return nameFiles;
    }

    /**
     * Retrieve records accumulated memory in megabyte
     */
    @Override
    public int storedMB() {
        long bytes = 0;

        final File[] files = cacheDirectory.listFiles();
        if (files == null) return 0;

        for (File file : files) {
            bytes += file.length();
        }

        double megabytes = Math.ceil((double) bytes / 1024 / 1024);
        return (int) megabytes;
    }

    /**
     * Save in disk the object passed.
     *
     * @param key  the key whereby the object could be retrieved/deleted later. @see evict and @see
     *             retrieve.
     * @param data the object to be persisted.
     */
    public void save(String key, Object data, Class<Interceptor>[] interceptors) {
        key = safetyKey(key);

        String wrapperJSONSerialized;

        if (data instanceof io.rx_cache2.internal.Record) {
            Type type = jolyglot.newParameterizedType(data.getClass(), Object.class);
            wrapperJSONSerialized = jolyglot.toJson(data, type);

            if (null != interceptors && interceptors.length > 0) {
                String userData = jolyglot.toJson(((Record) data).getData(), Object.class);
                String userDataIntercepted = userData;
                for (Class itClass : interceptors) {
                    Interceptor it = getInterceptorByClass(itClass);
                    if (null != it) {
                        userDataIntercepted = it.onSave(userDataIntercepted);
                    }
                }
                StringBuilder userDataBase64Encoded = new StringBuilder(QUOTES)
                        .append(BaseEncoding.base64().encode(userDataIntercepted.getBytes(Charset.defaultCharset())))
                        .append(QUOTES);
                wrapperJSONSerialized = wrapperJSONSerialized.replace(userData, userDataBase64Encoded);
            }

        } else {
            wrapperJSONSerialized = jolyglot.toJson(data);
        }


        File file = new File(cacheDirectory, key);
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(wrapperJSONSerialized);
            fileWriter.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete the object previously saved.
     *
     * @param key the key whereby the object could be deleted.
     */
    @Override
    public void evict(String key) {
        key = safetyKey(key);
        final File file = new File(cacheDirectory, key);
        file.delete();
    }

    /**
     * Delete all objects previously saved.
     */
    @Override
    public void evictAll() {
        File[] files = cacheDirectory.listFiles();

        if (null != files) {
            for (File file : files) {
                if (file != null)
                    file.delete();
            }
        }

    }

    /**
     * Retrieve the object previously saved.
     *
     * @param key   the key whereby the object could be retrieved.
     * @param clazz the type of class against the object need to be serialized
     */
    public <T> T retrieve(String key, final Class<T> clazz) {
        key = safetyKey(key);

        StringBuilder contentBuilder = new StringBuilder();
        File file = new File(cacheDirectory, key);
        if (!file.exists()) {
            return null;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jolyglot.fromJson(contentBuilder.toString(), clazz);
    }

    /**
     * Retrieve the Record previously saved.
     *
     * @param key the key whereby the object could be retrieved.
     */
    @Override
    public <T> io.rx_cache2.internal.Record<T> retrieveRecord(String key, Class<Interceptor>[] interceptors) {
        key = safetyKey(key);

        File file = new File(cacheDirectory, key);

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String wrapperJSONSerialized = contentBuilder.toString();

        io.rx_cache2.internal.Record<T> diskRecord = null;
        try {
            Type partialType = jolyglot.newParameterizedType(io.rx_cache2.internal.Record.class, Object.class);
            diskRecord = jolyglot.fromJson(wrapperJSONSerialized, partialType);

            if (null != interceptors && interceptors.length > 0) {
                String userDataBase64Encoded = diskRecord.getData().toString();
                String userDataBase64Decoded = new String(BaseEncoding.base64().decode(userDataBase64Encoded), Charset.defaultCharset());
                for (Class itClass : interceptors) {
                    Interceptor it = getInterceptorByClass(itClass);
                    if (null != it) {
                        userDataBase64Decoded = it.onRetrieve(userDataBase64Decoded);
                    }
                }
                StringBuilder stringBuilder = new StringBuilder(QUOTES)
                        .append(userDataBase64Encoded)
                        .append(QUOTES);
                wrapperJSONSerialized = wrapperJSONSerialized.replace(stringBuilder, userDataBase64Decoded);
            }

            Class classData = diskRecord.getDataClassName() == null
                    ? Object.class : Class.forName(diskRecord.getDataClassName());
            Class classCollectionData = diskRecord.getDataCollectionClassName() == null
                    ? Object.class : Class.forName(diskRecord.getDataCollectionClassName());

            boolean isCollection = Collection.class.isAssignableFrom(classCollectionData);
            boolean isArray = classCollectionData.isArray();
            boolean isMap = Map.class.isAssignableFrom(classCollectionData);
//            io.rx_cache2.internal.Record<T> diskRecord;

            if (isCollection) {
                Type typeCollection = jolyglot.newParameterizedType(classCollectionData, classData);
                Type typeRecord = jolyglot.newParameterizedType(io.rx_cache2.internal.Record.class, typeCollection);
                diskRecord = jolyglot.fromJson(wrapperJSONSerialized, typeRecord);
            } else if (isArray) {
                Type typeRecord = jolyglot.newParameterizedType(io.rx_cache2.internal.Record.class, classCollectionData);
                diskRecord = jolyglot.fromJson(wrapperJSONSerialized, typeRecord);
            } else if (isMap) {
                Class classKeyMap = Class.forName(diskRecord.getDataKeyMapClassName());
                Type typeMap = jolyglot.newParameterizedType(classCollectionData, classKeyMap, classData);
                Type typeRecord = jolyglot.newParameterizedType(io.rx_cache2.internal.Record.class, typeMap);
                diskRecord = jolyglot.fromJson(wrapperJSONSerialized, typeRecord);
            } else {
                Type type = jolyglot.newParameterizedType(io.rx_cache2.internal.Record.class, classData);
                diskRecord = jolyglot.fromJson(wrapperJSONSerialized, type);
            }

            diskRecord.setSizeOnMb(file.length() / 1024f / 1024f);
        } catch (Exception ignore) {
//            return null;
        }
        return diskRecord;
    }


    /**
     * Retrieve a collection previously saved.
     *
     * @param key             the key whereby the object could be retrieved.
     * @param classCollection type class collection
     * @param classData       type class contained by the collection, not the collection itself
     */
    public <C extends Collection<T>, T> C retrieveCollection(String key, Class<C> classCollection,
                                                             Class<T> classData) {
        key = safetyKey(key);

        try {
            File file = new File(cacheDirectory, key);
            Type typeCollection = jolyglot.newParameterizedType(classCollection, classData);
            T data = jolyglot.fromJson(file, typeCollection);
            return (C) data;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieve a Map previously saved.
     *
     * @param key           the key whereby the object could be retrieved.
     * @param classMap      type class Map
     * @param classMapKey   type class of the Map key
     * @param classMapValue type class of the Map value
     */
    public <M extends Map<K, V>, K, V> M retrieveMap(String key, Class classMap, Class<K> classMapKey,
                                                     Class<V> classMapValue) {
        key = safetyKey(key);

        try {
            File file = new File(cacheDirectory, key);

            Type typeMap = jolyglot.newParameterizedType(classMap, classMapKey, classMapValue);
            Object data = jolyglot.fromJson(file, typeMap);

            return (M) data;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieve an Array previously saved.
     *
     * @param key       the key whereby the object could be retrieved.
     * @param classData type class contained by the Array
     */
    public <T> T[] retrieveArray(String key, Class<T> classData) {
        key = safetyKey(key);

        try {
            File file = new File(cacheDirectory, key);

            Class<?> clazzArray = Array.newInstance(classData, 1).getClass();
            Object data = jolyglot.fromJson(file, clazzArray);

            return (T[]) data;
        } catch (Exception e) {
            return null;
        }
    }

    private String safetyKey(String key) {
        return key.replaceAll("/", "_");
    }

    private Interceptor getInterceptorByClass(Class clazz) {
        return interceptorMap.get(clazz);
    }
}