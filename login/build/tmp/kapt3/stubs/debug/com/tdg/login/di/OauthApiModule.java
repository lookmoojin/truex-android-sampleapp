package com.tdg.login.di;

@dagger.Module
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u0007\u00a8\u0006\t"}, d2 = {"Lcom/tdg/login/di/OauthApiModule;", "", "()V", "providesOauthApiInterface", "Lcom/tdg/login/api/OauthApiInterface;", "okHttpClient", "Lokhttp3/OkHttpClient;", "gsonConverterFactory", "Lretrofit2/Converter$Factory;", "login_debug"})
public final class OauthApiModule {
    @org.jetbrains.annotations.NotNull
    public static final com.tdg.login.di.OauthApiModule INSTANCE = null;
    
    private OauthApiModule() {
        super();
    }
    
    @dagger.Provides
    @javax.inject.Singleton
    @org.jetbrains.annotations.NotNull
    public final com.tdg.login.api.OauthApiInterface providesOauthApiInterface(@DefaultOkHttp
    @org.jetbrains.annotations.NotNull
    okhttp3.OkHttpClient okHttpClient, @GsonConverter
    @org.jetbrains.annotations.NotNull
    retrofit2.Converter.Factory gsonConverterFactory) {
        return null;
    }
}