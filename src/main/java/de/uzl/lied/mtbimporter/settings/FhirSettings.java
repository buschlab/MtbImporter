package de.uzl.lied.mtbimporter.settings;

/**
 * Settings for FHIR servers used for clinical and terminology data.
 */
public class FhirSettings {

    private String clinicalDataServerUrl;
    private FhirTerminologySettings terminology;

    public String getClinicalDataServerUrl() {
        return clinicalDataServerUrl;
    }

    public void setClinicalDataServerUrl(String clinicalDataServerUrl) {
        this.clinicalDataServerUrl = clinicalDataServerUrl;
    }

    public FhirTerminologySettings getTerminology() {
        return terminology;
    }

    public void setTerminology(FhirTerminologySettings terminology) {
        this.terminology = terminology;
    }

}
