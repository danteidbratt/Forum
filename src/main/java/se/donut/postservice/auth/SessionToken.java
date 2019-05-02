package se.donut.postservice.auth;


import java.util.UUID;

public class SessionToken {

    private final String key;
    private final String userName;

    public SessionToken(String key, String username) {
        this.key = key;
        this.userName = username;
    }

    public String getKey() {
        return key;
    }

    public String getUserName() {
        return userName;
    }
}
