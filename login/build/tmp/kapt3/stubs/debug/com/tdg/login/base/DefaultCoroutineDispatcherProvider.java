package com.tdg.login.base;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\b\u0010\u0005\u001a\u00020\u0004H\u0016J\b\u0010\u0006\u001a\u00020\u0004H\u0016J\b\u0010\u0007\u001a\u00020\u0004H\u0016\u00a8\u0006\b"}, d2 = {"Lcom/tdg/login/base/DefaultCoroutineDispatcherProvider;", "Lcom/tdg/login/base/CoroutineDispatcherProvider;", "()V", "default", "Lkotlinx/coroutines/CoroutineDispatcher;", "io", "main", "unconfined", "login_debug"})
public final class DefaultCoroutineDispatcherProvider implements com.tdg.login.base.CoroutineDispatcherProvider {
    
    @javax.inject.Inject
    public DefaultCoroutineDispatcherProvider() {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.CoroutineDispatcher main() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.CoroutineDispatcher io() {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.CoroutineDispatcher unconfined() {
        return null;
    }
}