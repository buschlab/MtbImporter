package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.MalformedURLException;

/**
 * Resource entry.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Resource {

    @JsonProperty("RESOURCE_ID")
    private String resourceId;
    @JsonProperty("URL")
    private String url;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Set url for resource entry.
     * @param url
     * @throws MalformedURLException
     */
    public void setUrl(String url) throws MalformedURLException {
        if (!(url.startsWith("http") || url.startsWith("https"))) {
            throw new MalformedURLException("Invalid URL! URLs must start with http or https");
        }
        this.url = url;
    }

}
