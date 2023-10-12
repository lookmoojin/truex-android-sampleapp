package com.tdg.login.base;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&J\b\u0010\u0005\u001a\u00020\u0003H&J\b\u0010\u0006\u001a\u00020\u0003H&\u00a8\u0006\u0007"}, d2 = {"Lcom/tdg/login/base/CoroutineDispatcherProvider;", "", "default", "Lkotlinx/coroutines/CoroutineDispatcher;", "io", "main", "unconfined", "login_debug"})
public abstract interface CoroutineDispatcherProvider {
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.CoroutineDispatcher main();
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.CoroutineDispatcher io();
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.CoroutineDispatcher unconfined();
}