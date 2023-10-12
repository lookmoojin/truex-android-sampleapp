package com.tdg.login.base;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u00020\u00032\n\u0010\u0004\u001a\u00060\u0005j\u0002`\u0006H\'\u00a8\u0006\u0007"}, d2 = {"Lcom/tdg/login/base/CollectSafe;", "", "onCancellation", "", "e", "Ljava/util/concurrent/CancellationException;", "Lkotlinx/coroutines/CancellationException;", "login_debug"})
public abstract interface CollectSafe {
    
    @androidx.annotation.WorkerThread
    public abstract void onCancellation(@org.jetbrains.annotations.NotNull
    java.util.concurrent.CancellationException e);
}