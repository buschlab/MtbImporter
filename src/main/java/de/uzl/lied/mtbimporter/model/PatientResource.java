package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resource entry for patient.
 */
public class PatientResource extends Resource {

    @JsonProperty("PATIENT_ID")
    private String patientId;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

}
