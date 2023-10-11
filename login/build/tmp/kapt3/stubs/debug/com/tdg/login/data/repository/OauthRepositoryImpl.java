package com.tdg.login.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/tdg/login/data/repository/OauthRepositoryImpl;", "Lcom/tdg/login/data/repository/OauthRepository;", "apiInterface", "Lcom/tdg/login/api/OauthApiInterface;", "(Lcom/tdg/login/api/OauthApiInterface;)V", "login", "Lkotlinx/coroutines/flow/Flow;", "Lcom/tdg/login/data/model/OauthResponse;", "request", "Lcom/tdg/login/data/model/OauthRequest;", "Companion", "login_debug"})
public final class OauthRepositoryImpl implements com.tdg.login.data.repository.OauthRepository {
    @org.jetbrains.annotations.NotNull
    private final com.tdg.login.api.OauthApiInterface apiInterface = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String LOGIN_FAILED = "login failed";
    @org.jetbrains.annotations.NotNull
    public static final com.tdg.login.data.repository.OauthRepositoryImpl.Companion Companion = null;
    
    @javax.inject.Inject
    public OauthRepositoryImpl(@org.jetbrains.annotations.NotNull
    com.tdg.login.api.OauthApiInterface apiInterface) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.flow.Flow<com.tdg.login.data.model.OauthResponse> login(@org.jetbrains.annotations.NotNull
    com.tdg.login.data.model.OauthRequest request) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/tdg/login/data/repository/OauthRepositoryImpl$Companion;", "", "()V", "LOGIN_FAILED", "", "login_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}