package de.uzl.lied.mtbimporter.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Settings for usage with Docker.
 */
public class DockerSettings {

    @JsonProperty("studyFolder")
    private String studyFolder;
    @JsonProperty("compose")
    private DockerCompose dockerCompose;
    @JsonProperty("imageName")
    private String imageName;
    @JsonProperty("containerName")
    private String containerName;
    @JsonProperty("networkName")
    private String networkName;
    @JsonProperty("propertiesFile")
    private String propteriesFile;
    @JsonProperty("portalInfoVolume")
    private String portalInfoVolume;

    public String getStudyFolder() {
        return studyFolder;
    }

    @JsonProperty("studyFolder")
    private void setStudyFolder(String studyFolder) {
        String newStudyFolder = studyFolder;
        if (!newStudyFolder.endsWith("/")) {
            newStudyFolder = newStudyFolder + "/";
        }
        this.studyFolder = newStudyFolder;
    }

    public DockerCompose getCompose() {
        return dockerCompose;
    }

    public String getImageName() {
        return imageName;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getPropteriesFile() {
        return propteriesFile;
    }

    public String getPortalInfoVolume() {
        return portalInfoVolume;
    }

    public String getNetworkName() {
        return networkName;
    }

}
