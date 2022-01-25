package de.uzl.lied.mtbimporter.model.mdr.centraxx;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Model for Token in Kairos CentraXX MDR.
 */
public class MdrToken {

    @JsonProperty("access_token")
    private UUID accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("scope")
    private String scope;

    public UUID getAccessToken() {
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
