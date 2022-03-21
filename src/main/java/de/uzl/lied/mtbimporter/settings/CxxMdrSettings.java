package de.uzl.lied.mtbimporter.settings;

/**
 * Settings for Kairos CentraXX MDR.
 */
public class CxxMdrSettings {

    private static final int SECONDMILLISECOND = 1000;
    private String url;
    private String token;
    private Long tokenExpiration;
    private String username;
    private String password;
    private String basicUsername;
    private String basicPassword;
    private boolean mappingEnabled;

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String newToken, int expiresIn) {
        this.token = newToken;
        tokenExpiration = System.currentTimeMillis() + SECONDMILLISECOND * expiresIn;
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
