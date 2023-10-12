package com.truedigital.common.share.datalegacy.data.repository.profile.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aof on 9/16/15 AD.
 */
public class TrueIDCredential {

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("expires_in")
    private long expire;

    public TrueIDCredential() {
        this.accessToken = null;
        this.refreshToken = null;
        this.expire = -1;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}