package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Timeline entry for laboratory test.
 */
public class TimelineLabtest extends Timeline {

    @JsonProperty("TEST")
    private String test;
    @JsonProperty("RESULT")
    private String result;

    public TimelineLabtest() {
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
