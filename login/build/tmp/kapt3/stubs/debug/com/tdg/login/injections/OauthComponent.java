package com.tdg.login.injections;

@javax.inject.Singleton
@dagger.Component(modules = {com.tdg.login.di.NetworkModule.class, com.tdg.login.di.OauthApiModule.class, com.tdg.login.di.OauthModule.class})
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\bg\u0018\u0000 \u00022\u00020\u0001:\u0002\u0002\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/tdg/login/injections/OauthComponent;", "", "Companion", "Factory", "login_debug"})
public abstract interface OauthComponent {
    @org.jetbrains.annotations.NotNull
    public static final com.tdg.login.injections.OauthComponent.Companion Companion = null;
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0005\u001a\u00020\u0004J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/tdg/login/injections/OauthComponent$Companion;", "", "()V", "oauthComponent", "Lcom/tdg/login/injections/OauthComponent;", "getInstance", "initialize", "", "login_debug"})
    public static final class Companion {
        private static com.tdg.login.injections.OauthComponent oauthComponent;
        
        private Companion() {
            super();
        }
        
        public final void initialize(@org.jetbrains.annotations.NotNull
        com.tdg.login.injections.OauthComponent oauthComponent) {
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.tdg.login.injections.OauthComponent getInstance() {
            return null;
        }
    }
    
    @dagger.Component.Factory
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\bg\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&\u00a8\u0006\u0004"}, d2 = {"Lcom/tdg/login/injections/OauthComponent$Factory;", "", "create", "Lcom/tdg/login/injections/OauthComponent;", "login_debug"})
    public static abstract interface Factory {
        
        @org.jetbrains.annotations.NotNull
        public abstract com.tdg.login.injections.OauthComponent create();
    }
}