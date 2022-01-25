package de.uzl.lied.mtbimporter.settings;

/**
 * Settings for the HL7 FHIR Server that will be used for ICD-O-3 to OncoTree
 * mapping.
 */
public class FhirTerminologySettings {

    private String serverUrl;
    private String icdO3Url;
    private String oncoTreeUrl;
    private String icdO3ToOncoTreeConceptMapUrl;
    private String icdO3ToOncoTreeConceptMapId;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getIcdO3Url() {
        return icdO3Url;
    }

    public void setIcdO3Url(String icdO3Url) {
        this.icdO3Url = icdO3Url;
    }

    public String getOncoTreeUrl() {
        return oncoTreeUrl;
    }

    public void setOncoTreeUrl(String oncoTreeUrl) {
        this.oncoTreeUrl = oncoTreeUrl;
    }

    public String getIcdO3ToOncoTreeConceptMapUrl() {
        return icdO3ToOncoTreeConceptMapUrl;
    }

    public void setIcdO3ToOncoTreeConceptMapUrl(String icdO3ToOncoTreeConceptMapUrl) {
        this.icdO3ToOncoTreeConceptMapUrl = icdO3ToOncoTreeConceptMapUrl;
    }

    public String getIcdO3ToOncoTreeConceptMapId() {
        return icdO3ToOncoTreeConceptMapId;
    }

    public void setIcdO3ToOncoTreeConceptMapId(String icdO3ToOncoTreeConceptMapId) {
        this.icdO3ToOncoTreeConceptMapId = icdO3ToOncoTreeConceptMapId;
    }

}
