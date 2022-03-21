package de.uzl.lied.mtbimporter.model.mdr.centraxx;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model for Token in Kairos CentraXX MDR.
 */
public class MdrToken {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("scope")
    private String scope;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

}
