package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Timeline entry for status.
 */
public class TimelineStatus extends Timeline {

    @JsonProperty("STATUS")
    private String status;
    @JsonProperty("SOURCE")
    private String source;

    public TimelineStatus() {
        setEventType("STATUS");
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
