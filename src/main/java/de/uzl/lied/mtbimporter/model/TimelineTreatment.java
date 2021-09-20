package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimelineTreatment extends Timeline {
    
    @JsonProperty("TREATMENT_TYPE")
    private String treatmentType;
    @JsonProperty("SUBTYPE")
    private String subtype;
    @JsonProperty("AGENT")
    private String agent;

    public TimelineTreatment() {
        setEventType("TREATMENT");
    }

    public String getTreatmentType() {
        return this.treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getAgent() {
        return this.agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

}
