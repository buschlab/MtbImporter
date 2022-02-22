package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for common attributes of ClinicalPatient and ClinicalSample.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
public abstract class Clinical {

    @JsonProperty("PATIENT_ID")
    private String patientId;
    @JsonIgnore
    private Map<String, Object> additionalAttributes = new HashMap<>();

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalAttributes() {
        return this.additionalAttributes;
    }

    @JsonAnySetter
    public void setAdditionalAttributes(String name, Object value) {
        this.additionalAttributes.put(name, value);
    }

}
