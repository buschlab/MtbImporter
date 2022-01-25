package de.uzl.lied.mtbimporter.settings;

/**
 * Settings for Samply MDR.
 */
public class SamplyMdrSettings {

    private String url;
    private String sourceNamespace;
    private String targetNamespace;
    private String language;
    private boolean mappingEnabled;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSourceNamespace() {
        return sourceNamespace;
    }

    public void setSourceNamespace(String sourceNamespace) {
        this.sourceNamespace = sourceNamespace;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isMappingEnabled() {
        return mappingEnabled;
    }

}
