package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimelineSpecimen extends Timeline {
    
    @JsonProperty("SPECIMEN_REFERENCE_NUMBER")
    private String specimenReferenceNumber;
    @JsonProperty("SPECIMEN_SITE")
    private String specimenSite;
    @JsonProperty("SPECIMEN_TYPE")
    private String specimenType;
    @JsonProperty("SOURCE")
    private String source;

    public TimelineSpecimen() {
        setEventType("SPECIMEN");
    }

    public String getSpecimenReferenceNumber() {
        return this.specimenReferenceNumber;
    }

    public void setSpecimenReferenceNumber(String specimenReferenceNumber) {
        this.specimenReferenceNumber = specimenReferenceNumber;
    }

    public String getSpecimenSite() {
        return this.specimenSite;
    }

    public void setSpecimenSite(String specimenSite) {
        this.specimenSite = specimenSite;
    }

    public String getSpecimenType() {
        return this.specimenType;
    }

    public void setSpecimenType(String specimenType) {
        this.specimenType = specimenType;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
