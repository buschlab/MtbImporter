package de.uzl.lied.mtbimporter.settings;

public class SamplyMdrSettings {

    private String url;
    private String namespace;
    private String language;
    private boolean mappingEnabled;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
