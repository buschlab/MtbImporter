package de.uzl.lied.mtbimporter.jobs.mdr.dataelementhub;

import de.uzl.lied.mtbimporter.model.mdr.dataelementhub.MdrToken;
import de.uzl.lied.mtbimporter.settings.DataElementHubSettings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.tinylog.Logger;

/**
 * Class handling the login into a Kairos CentraXX MDR.
 */
public final class DataElementHubLogin {

    private DataElementHubLogin() {
    }

    /**
     * Login method for DataElementHub.
     *
     * @param mdr Configuration for MDR.
     * @return Updated configuration with access token and expiration timestamp.
     */
    public static DataElementHubSettings login(DataElementHubSettings mdr) {
        RestTemplate rt = new RestTemplate();
        rt.getInterceptors().add(new BasicAuthenticationInterceptor(mdr.getClientId(), mdr.getClientSecret()));
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.set("grant_type", "password");
        form.set("scope", "openid");
        form.set("username", mdr.getUsername());
        form.set("password", mdr.getPassword());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mdr.getTokenUrl().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        MdrToken token = rt.postForObject(builder.build().encode().toUri(), entity, MdrToken.class);
        if (token != null) {
            Logger.warn("DataElementHub MDR token not set. Login returned null.");
            mdr.setToken(token.getAccessToken(), token.getExpiresIn());
            mdr.setRefreshToken(token.getRefreshToken(), token.getRefreshExpiresIn());
        }
        return mdr;
    }

    /**
     * Refreshed access token for DataElementHub.
     *
     * @param mdr Configuration for MDR.
     * @return Updated configuration with access token and expiration timestamp.
     */
    public static DataElementHubSettings refresh(DataElementHubSettings mdr) {
        RestTemplate rt = new RestTemplate();
        rt.getInterceptors().add(new BasicAuthenticationInterceptor(mdr.getClientId(), mdr.getClientSecret()));
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.set("grant_type", "password");
        form.set("scope", "openid");
        form.set("username", mdr.getUsername());
        form.set("password", mdr.getPassword());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(mdr.getTokenUrl().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        MdrToken token = rt.postForObject(builder.build().encode().toUri(), entity, MdrToken.class);
        if (token != null) {
            Logger.warn("DataElementHub MDR token not set. Login returned null.");
            mdr.setToken(token.getAccessToken(), token.getExpiresIn());
            mdr.setRefreshToken(token.getRefreshToken(), token.getRefreshExpiresIn());
        }
        return mdr;
    }

}
