/*
 * Copyright 2016 Victor Albertos
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

package io.rx_cache2.internal.cache;

import javax.inject.Inject;

import io.rx_cache2.internal.Locale;
import io.rx_cache2.internal.Memory;
import io.rx_cache2.internal.Persistence;
import io.rx_cache2.internal.Record;
import io.rx_cache2.internal.interceptor.Interceptor;

public final class SaveRecord extends Action {
  private final Integer maxMgPersistenceCache;
  private final io.rx_cache2.internal.cache.EvictExpirableRecordsPersistence
      evictExpirableRecordsPersistence;

  @Inject public SaveRecord(Memory memory, Persistence persistence, Integer maxMgPersistenceCache,
      io.rx_cache2.internal.cache.EvictExpirableRecordsPersistence evictExpirableRecordsPersistence) {
    super(memory, persistence);
    this.maxMgPersistenceCache = maxMgPersistenceCache;
    this.evictExpirableRecordsPersistence = evictExpirableRecordsPersistence;
  }

  void save(final String providerKey, final String dynamicKey, final String dynamicKeyGroup,
            final Object data, final long lifeTime, final boolean isExpirable,
            final Class<Interceptor>[] interceptors, boolean useExpiredDataIfNotLoaderAvailable) {
    String composedKey = composeKey(providerKey, dynamicKey, dynamicKeyGroup);

    Record record = new Record(data, isExpirable, lifeTime , useExpiredDataIfNotLoaderAvailable);
    memory.put(composedKey, record);

    if (persistence.storedMB() >= maxMgPersistenceCache) {
      System.out.println(Locale.RECORD_CAN_NOT_BE_PERSISTED_BECAUSE_WOULD_EXCEED_THRESHOLD_LIMIT);
    } else {
      persistence.saveRecord(composedKey, record, interceptors);
    }

    evictExpirableRecordsPersistence.startTaskIfNeeded(interceptors);
  }
}
