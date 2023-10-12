package com.tdg.login.di;

@dagger.Module
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007J\b\u0010\u0005\u001a\u00020\u0006H\u0007\u00a8\u0006\u0007"}, d2 = {"Lcom/tdg/login/di/NetworkModule;", "", "()V", "provideDefaultOkHttp", "Lokhttp3/OkHttpClient;", "provideGsonConverterFactory", "Lretrofit2/Converter$Factory;", "login_debug"})
public final class NetworkModule {
    @org.jetbrains.annotations.NotNull
    public static final com.tdg.login.di.NetworkModule INSTANCE = null;
    
    private NetworkModule() {
        super();
    }
    
    @dagger.Provides
    @javax.inject.Singleton
    @GsonConverter
    @org.jetbrains.annotations.NotNull
    public final retrofit2.Converter.Factory provideGsonConverterFactory() {
        return null;
    }
    
    @dagger.Provides
    @javax.inject.Singleton
    @DefaultOkHttp
    @org.jetbrains.annotations.NotNull
    public final okhttp3.OkHttpClient provideDefaultOkHttp() {
        return null;
    }
}