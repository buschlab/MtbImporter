package de.uzl.lied.mtbimporter.settings;

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
