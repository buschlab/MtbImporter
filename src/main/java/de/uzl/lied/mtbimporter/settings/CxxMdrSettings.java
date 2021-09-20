package de.uzl.lied.mtbimporter.settings;

import java.util.UUID;

public class CxxMdrSettings {
    
    private String url;
    private UUID token;
    private Long tokenExpiration;
    private String username;
    private String password;
    private String basicUsername;
    private String basicPassword;
    private boolean mappingEnabled;

    public String getUrl() {
        return url;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token, int expiresIn) {
        this.token = token;
        tokenExpiration = System.currentTimeMillis() + 1000 * expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getBasicUsername() {
        return basicUsername;
    }

    public String getBasicPassword() {
        return basicPassword;
    }

    public boolean isTokenExpired() {
        return System.currentTimeMillis() > tokenExpiration;
    }

    public boolean isMappingEnabled() {
        return mappingEnabled;
    }

}
