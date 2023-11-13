package com.example.security.oauth.provider;

import java.util.Map;

public class  NaverUserInfo implements OAuth2UserInfo {

    private Map<String, Object> atrributes;

    public NaverUserInfo(Map<String, Object> atrributes) {
        this.atrributes = atrributes;
    }

    @Override
    public String getProviderId() {
        return (String) atrributes.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String) atrributes.get("email");
    }

    @Override
    public String getName() {
        return (String) atrributes.get("name");
    }
}
