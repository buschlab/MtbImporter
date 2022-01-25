package de.uzl.lied.mtbimporter.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Settings for cBioPortal when using Docker-Compose.
 */
public class DockerCompose {

    @JsonProperty("workdir")
    private String workdir;
    @JsonProperty("serviceName")
    private String serviceName;

    public String getWorkdir() {
        return workdir;
    }

    public String getServiceName() {
        return serviceName;
    }

}
