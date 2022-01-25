package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object for patients sample.
 */
public class ClinicalSample extends Clinical {

    @JsonProperty("SAMPLE_ID")
    private String sampleId;

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

}
