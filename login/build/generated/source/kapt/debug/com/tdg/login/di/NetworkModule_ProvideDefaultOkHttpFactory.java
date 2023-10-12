// Generated by Dagger (https://dagger.dev).
package com.tdg.login.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.tdg.login.di.DefaultOkHttp")
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class NetworkModule_ProvideDefaultOkHttpFactory implements Factory<OkHttpClient> {
  @Override
  public OkHttpClient get() {
    return provideDefaultOkHttp();
  }

  public static NetworkModule_ProvideDefaultOkHttpFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OkHttpClient provideDefaultOkHttp() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideDefaultOkHttp());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideDefaultOkHttpFactory INSTANCE = new NetworkModule_ProvideDefaultOkHttpFactory();
  }
}
