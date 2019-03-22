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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.rx_cache2.internal.Memory;
import io.rx_cache2.internal.Persistence;
import io.rx_cache2.internal.Record;

@Singleton
public final class EvictExpiredRecordsPersistence extends Action {
  private final HasRecordExpired hasRecordExpired;

  @Inject public EvictExpiredRecordsPersistence(Memory memory, Persistence persistence,
      HasRecordExpired hasRecordExpired) {
    super(memory, persistence);
    this.hasRecordExpired = hasRecordExpired;
  }

  public Observable<Integer> startEvictingExpiredRecords() {
    List<String> allKeys = persistence.allKeys();

    for (String key : allKeys) {
      Record record = persistence.retrieveRecord(key, null);
      if (record != null && hasRecordExpired.hasRecordExpired(record) && !record.isUseExpiredDataIfNotLoaderAvailable()) {
        persistence.evict(key);
      }
    }

    return Observable.just(1);
  }
}
