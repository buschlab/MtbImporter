package de.uzl.lied.mtbimporter.settings;

import java.net.URL;

/**
 * Settings for DataElementHub MDR.
 */
public class DataElementHubSettings {

    private static final int SECONDMILLISECOND = 1000;
    private URL url;
    private URL tokenUrl;
    private String token;
    private Long tokenExpiration;
    private String refreshToken;
    private Long refreshTokenExpiration;
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String sourceNamespace;
    private String targetNamespace;
    private String language;
    private boolean mappingEnabled;

    public URL getUrl() {
        return url;
    }

    public URL getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(URL tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String newToken, int expiresIn) {
        this.token = newToken;
        tokenExpiration = System.currentTimeMillis() + SECONDMILLISECOND * expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String newRefreshToken, int expiresIn) {
        this.refreshToken = newRefreshToken;
        refreshTokenExpiration = System.currentTimeMillis() + SECONDMILLISECOND * expiresIn;
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

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public boolean isTokenExpired() {
        return System.currentTimeMillis() > tokenExpiration;
    }

    public boolean isRefreshTokenExpired() {
        return System.currentTimeMillis() > refreshTokenExpiration;
    }

    public String getSourceNamespace() {
        return sourceNamespace;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isMappingEnabled() {
        return mappingEnabled;
    }

}
