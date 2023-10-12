package com.tdg.login.di;

@dagger.Module
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\'J\b\u0010\n\u001a\u00020\u000bH\'\u00a8\u0006\f"}, d2 = {"Lcom/tdg/login/di/OauthModule;", "", "bindsLoginUseCase", "Lcom/tdg/login/domain/usecase/LoginUseCase;", "loginUseCaseImpl", "Lcom/tdg/login/domain/usecase/LoginUseCaseImpl;", "bindsOauthRepository", "Lcom/tdg/login/data/repository/OauthRepository;", "oauthRepositoryImpl", "Lcom/tdg/login/data/repository/OauthRepositoryImpl;", "getGsonConverterFactory", "Lretrofit2/Converter$Factory;", "login_debug"})
public abstract interface OauthModule {
    
    @GsonConverter
    @org.jetbrains.annotations.NotNull
    public abstract retrofit2.Converter.Factory getGsonConverterFactory();
    
    @dagger.Binds
    @org.jetbrains.annotations.NotNull
    public abstract com.tdg.login.data.repository.OauthRepository bindsOauthRepository(@org.jetbrains.annotations.NotNull
    com.tdg.login.data.repository.OauthRepositoryImpl oauthRepositoryImpl);
    
    @dagger.Binds
    @org.jetbrains.annotations.NotNull
    public abstract com.tdg.login.domain.usecase.LoginUseCase bindsLoginUseCase(@org.jetbrains.annotations.NotNull
    com.tdg.login.domain.usecase.LoginUseCaseImpl loginUseCaseImpl);
}