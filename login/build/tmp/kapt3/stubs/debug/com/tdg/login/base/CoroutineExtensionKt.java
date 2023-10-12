package com.tdg.login.base;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000L\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a`\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\b\b\u0002\u0010\u0000\u001a\u00020\u000423\b\u0004\u0010\u0005\u001a-\b\u0001\u0012\u0013\u0012\u0011H\u0002\u00a2\u0006\f\b\u0007\u0012\b\b\b\u0012\u0004\b\b(\t\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0006H\u0086H\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\f\u001a\u0086\u0001\u0010\r\u001a\u00020\u000e*\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\r\u001a\u00020\u00142+\b\u0002\u0010\u0015\u001a%\b\u0001\u0012\u0004\u0012\u00020\u000f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b\u0018\u00010\u0006\u00a2\u0006\u0002\b\u00162\'\u0010\u0017\u001a#\b\u0001\u0012\u0004\u0012\u00020\u000f\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00010\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0006\u00a2\u0006\u0002\b\u0016\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0018\u001a\u001e\u0010\u0019\u001a\u00020\u000e\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010\u001a\u001a\u00020\u000f\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u001b"}, d2 = {"collectSafe", "", "T", "Lkotlinx/coroutines/flow/Flow;", "Lcom/tdg/login/base/CollectSafe;", "action", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "value", "Lkotlin/coroutines/Continuation;", "", "(Lkotlinx/coroutines/flow/Flow;Lcom/tdg/login/base/CollectSafe;Lkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "launchSafe", "Lkotlinx/coroutines/Job;", "Lkotlinx/coroutines/CoroutineScope;", "context", "Lkotlin/coroutines/CoroutineContext;", "start", "Lkotlinx/coroutines/CoroutineStart;", "Lcom/tdg/login/base/LaunchSafe;", "onStart", "Lkotlin/ExtensionFunctionType;", "block", "(Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;Lkotlinx/coroutines/CoroutineStart;Lcom/tdg/login/base/LaunchSafe;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function2;)Lkotlinx/coroutines/Job;", "launchSafeIn", "scope", "login_debug"})
public final class CoroutineExtensionKt {
    
    @org.jetbrains.annotations.NotNull
    public static final kotlinx.coroutines.Job launchSafe(@org.jetbrains.annotations.NotNull
    kotlinx.coroutines.CoroutineScope $this$launchSafe, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.CoroutineContext context, @org.jetbrains.annotations.NotNull
    kotlinx.coroutines.CoroutineStart start, @org.jetbrains.annotations.NotNull
    com.tdg.login.base.LaunchSafe launchSafe, @org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function2<? super kotlinx.coroutines.CoroutineScope, ? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> onStart, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function2<? super kotlinx.coroutines.CoroutineScope, ? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> block) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final <T extends java.lang.Object>kotlinx.coroutines.Job launchSafeIn(@org.jetbrains.annotations.NotNull
    kotlinx.coroutines.flow.Flow<? extends T> $this$launchSafeIn, @org.jetbrains.annotations.NotNull
    kotlinx.coroutines.CoroutineScope scope) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public static final <T extends java.lang.Object>java.lang.Object collectSafe(@org.jetbrains.annotations.NotNull
    kotlinx.coroutines.flow.Flow<? extends T> $this$collectSafe, @org.jetbrains.annotations.NotNull
    com.tdg.login.base.CollectSafe collectSafe, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function2<? super T, ? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> action, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private static final <T extends java.lang.Object>java.lang.Object collectSafe$$forInline(kotlinx.coroutines.flow.Flow<? extends T> $this$collectSafe, com.tdg.login.base.CollectSafe collectSafe, kotlin.jvm.functions.Function2<? super T, ? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> action, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}