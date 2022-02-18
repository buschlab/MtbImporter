package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Timeline entry for images.
 */
public class TimelineImaging extends Timeline {

    @JsonProperty("DIAGNOSTIC_TYPE")
    private String diagnosticType;
    @JsonProperty("DIAGNOSTIC_TYPE_DETAILED")
    private String diagnosticTypeDetailed;
    @JsonProperty("RESULT")
    private String result;
    @JsonProperty("SOURCE")
    private String source;

    public TimelineImaging() {
        setEventType("IMAGING");
    }

    public String getDiagnosticType() {
        return this.diagnosticType;
    }

    public void setDiagnosticType(String diagnosticType) {
        this.diagnosticType = diagnosticType;
    }

    public String getDiagnosticTypeDetailed() {
        return this.diagnosticTypeDetailed;
    }

    public void setDiagnosticTypeDetailed(String diagnosticTypeDetailed) {
        this.diagnosticTypeDetailed = diagnosticTypeDetailed;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
