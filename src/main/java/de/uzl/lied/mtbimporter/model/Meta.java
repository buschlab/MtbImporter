package de.uzl.lied.mtbimporter.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Meta {
    
    @JsonProperty("cancer_study_identifier")
    private String cancerStudyIdentifier;
    @JsonIgnore
    private Map<String, String> additionalAttributes = new HashMap<String, String>();

    public String getCancerStudyIdentifier() {
        return cancerStudyIdentifier;
    }

    public void setCancerStudyIdentifier(String cancerStudyIdentifier) {
        this.cancerStudyIdentifier = cancerStudyIdentifier;
    }

    @JsonAnyGetter
    public Map<String, String> getAdditionalAttributes() {
        return this.additionalAttributes;
    }

    @JsonAnySetter
    public void setAdditionalAttributes(String name, String value) {
        this.additionalAttributes.put(name, value);
    }

}
