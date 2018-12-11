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

import java.util.List;

import io.rx_cache2.internal.interceptor.Interceptor;

/**
 * Provides the persistence layer for the cache A default implementation which store the objects in
 * disk is supplied:
 *
 * @see Disk
 */
public interface Persistence {

  /**
   * Save the data supplied based on a certain mechanism which provides persistence somehow
   *
   * @param key The key associated with the object to be persisted
   * @param object The object to be persisted
   */
  void save(String key, Object object, Class<Interceptor>[] interceptors);

  /**
   * Save the data supplied based on a certain mechanism which provides persistence somehow
   *
   * @param key The key associated with the record to be persisted
   * @param record The record to be persisted
   */
  void saveRecord(String key, Record record, Class<Interceptor>[] interceptors);

  /**
   * Delete the data associated with its particular key
   *
   * @param key The key associated with the object to be deleted from persistence
   */
  void evict(String key);

  /**
   * Delete all the data
   */
  void evictAll();

  /**
   * Retrieve the keys from all records persisted
   */
  List<String> allKeys();

  /**
   * Retrieve accumulated memory records in megabytes
   */
  int storedMB();

  /**
   * Retrieve the object associated with its particular key
   *
   * @param key The key associated with the object to be retrieved from persistence
   * @param <T> The data to be retrieved
   * @see Record
   */
  <T> T retrieve(String key, Class<T> clazz);

  /**
   * Retrieve the record associated with its particular key
   *
   * @param key The key associated with the Record to be retrieved from persistence
   * @see Record
   */
  <T> Record<T> retrieveRecord(String key, Class<Interceptor>[] interceptors);
}
