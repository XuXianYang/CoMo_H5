package com.dianxian.user.filter;

public class RedirectGson {
    private boolean isNotAuthenticated;
    private String location;

    public RedirectGson(String location) {
        isNotAuthenticated = true;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public boolean isNotAuthenticated() {
        return isNotAuthenticated;
    }
}
