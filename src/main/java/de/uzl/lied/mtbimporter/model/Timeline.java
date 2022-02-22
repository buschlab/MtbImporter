package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generic timeline entry.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "PATIENT_ID", "START_DATE", "STOP_DATE", "EVENT_TYPE", "BEGIN", "END" })
public class Timeline {
    @JsonProperty("PATIENT_ID")
    private String patientId;
    @JsonProperty("START_DATE")
    private Long startDate;
    @JsonProperty("STOP_DATE")
    private Long stopDate;
    @JsonProperty("EVENT_TYPE")
    private String eventType;
    @JsonProperty("NOTE")
    private String note;
    @JsonProperty("BEGIN")
    private String begin;
    @JsonProperty("END")
    private String end;
    @JsonIgnore
    private Date tmpStartDate;
    @JsonIgnore
    private Date tmpStopDate;
    @JsonIgnore
    private Date tmpBaseDate;
    @JsonIgnore
    private Map<String, Object> additionalAttributes = new HashMap<>();

    public String getPatientId() {
        return this.patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Long getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getStopDate() {
        return this.stopDate;
    }

    public void setStopDate(Long stopDate) {
        this.stopDate = stopDate;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getTmpBaseDate() {
        return tmpBaseDate;
    }

    public void setTmpBaseDate(Date tmpBaseDate) {
        this.tmpBaseDate = tmpBaseDate;
    }

    public Date getTmpStartDate() {
        return tmpStartDate;
    }

    public void setTmpStartDate(Date tmpStartDate) {
        this.tmpStartDate = tmpStartDate;
    }

    public Date getTmpStopDate() {
        return tmpStopDate;
    }

    public void setTmpStopDate(Date tmpStopDate) {
        this.tmpStopDate = tmpStopDate;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalAttributes() {
        return this.additionalAttributes;
    }

    @JsonAnySetter
    public void setAdditionalAttributes(String name, Object value) {
        this.additionalAttributes.put(name, value);
    }

    /**
     * Merge to timeline objects.
     */
    public static <T> List<T> merge(Collection<T> timeline1, Collection<T> timeline2) {
        return new ArrayList<>(
                Stream.of(timeline1, timeline2).flatMap(Collection::stream).collect(Collectors.toMap(r -> {
                    Timeline t = (Timeline) r;
                    return t.getPatientId() + ";" + t.getEventType() + ";" + t.getStartDate() + ";" + t.getStopDate();
                }, Function.identity(), (T x, T y) -> y)).values());
    }

}
