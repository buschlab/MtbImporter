package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Matrix entry representing the sequencing panel of a sample.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenePanelMatrix {

    @JsonProperty("SAMPLE_ID")
    private String sampleId;
    @JsonProperty("mutations")
    private String mutations;

    public GenePanelMatrix(@JsonProperty("SAMPLE_ID") String sampleId, @JsonProperty("mutations") String mutations) {
        this.sampleId = sampleId;
        this.mutations = mutations;
    }

    public String getSampleId() {
        return sampleId;
    }

    public String getMutations() {
        return mutations;
    }

}
