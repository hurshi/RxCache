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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.rx_cache2.MigrationCache;
import io.rx_cache2.internal.cache.memory.ReferenceMapMemory;
import io.rx_cache2.internal.interceptor.Interceptor;
import io.victoralbertos.jolyglot.JolyglotGenerics;

@Module
public final class RxCacheModule {
  private final File cacheDirectory;
  private final Integer maxMgPersistenceCache;
  private final List<MigrationCache> migrations;
  private final JolyglotGenerics jolyglot;
  private final Map<Class,Interceptor> interceptors;

  public RxCacheModule(File cacheDirectory,
                       Integer maxMgPersistenceCache, List<MigrationCache> migrations, JolyglotGenerics jolyglot,
                       Map<Class,Interceptor> interceptors) {
    this.cacheDirectory = cacheDirectory;
    this.maxMgPersistenceCache = maxMgPersistenceCache;
    this.migrations = migrations;
    this.jolyglot = jolyglot;
    this.interceptors = interceptors;
  }

  @Singleton @Provides File provideCacheDirectory() {
    return cacheDirectory;
  }

  @Singleton @Provides Persistence providePersistence(io.rx_cache2.internal.Disk disk) {
    return disk;
  }

  @Singleton @Provides Map<Class,Interceptor> provideInterceptors(){return interceptors;}

  @Singleton @Provides io.rx_cache2.internal.Memory provideMemory() {
    return new ReferenceMapMemory();
  }

  @Singleton @Provides Integer maxMbPersistenceCache() {
    return maxMgPersistenceCache != null ? maxMgPersistenceCache : 100;
  }

  @Singleton @Provides List<MigrationCache> provideMigrations() {
    return migrations != null ? migrations : new ArrayList<MigrationCache>();
  }

  @Singleton @Provides JolyglotGenerics provideJolyglot() {
    return jolyglot;
  }

  @Provides io.rx_cache2.internal.ProcessorProviders provideProcessorProviders(
      io.rx_cache2.internal.ProcessorProvidersBehaviour processorProvidersBehaviour) {
    return processorProvidersBehaviour;
  }
}
