package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SampleResource extends PatientResource {
    
    @JsonProperty("SAMPLE_ID")
    private String sampleId;

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

}
