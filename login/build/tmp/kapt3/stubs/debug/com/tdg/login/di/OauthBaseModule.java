package com.tdg.login.di;

@dagger.Module
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007\u00a8\u0006\u0005"}, d2 = {"Lcom/tdg/login/di/OauthBaseModule;", "", "()V", "provideCoroutineDispatcherProvider", "Lcom/tdg/login/base/CoroutineDispatcherProvider;", "login_debug"})
public final class OauthBaseModule {
    
    public OauthBaseModule() {
        super();
    }
    
    @dagger.Provides
    @org.jetbrains.annotations.NotNull
    public final com.tdg.login.base.CoroutineDispatcherProvider provideCoroutineDispatcherProvider() {
        return null;
    }
}