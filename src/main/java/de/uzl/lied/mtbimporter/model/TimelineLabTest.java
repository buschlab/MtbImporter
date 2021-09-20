package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimelineLabTest extends Timeline {

    @JsonProperty("TEST")
    private String test;
    @JsonProperty("RESULT")
    private String result;

    public TimelineLabTest() {
        setEventType("LAB_TEST");
    }
    
    public String getTest() {
        return this.test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
