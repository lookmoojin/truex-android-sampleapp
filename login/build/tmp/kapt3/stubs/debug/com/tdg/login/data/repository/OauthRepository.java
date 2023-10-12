package com.tdg.login.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\u0007"}, d2 = {"Lcom/tdg/login/data/repository/OauthRepository;", "", "login", "Lkotlinx/coroutines/flow/Flow;", "Lcom/tdg/login/data/model/OauthResponse;", "request", "Lcom/tdg/login/data/model/OauthRequest;", "login_debug"})
public abstract interface OauthRepository {
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<com.tdg.login.data.model.OauthResponse> login(@org.jetbrains.annotations.NotNull
    com.tdg.login.data.model.OauthRequest request);
}