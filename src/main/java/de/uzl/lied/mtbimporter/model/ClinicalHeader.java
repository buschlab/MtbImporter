package de.uzl.lied.mtbimporter.model;

/**
 * Dedicated class for header information of clinical files (patient and sample).
 */
public class ClinicalHeader {

    private String displayName;
    private String description;
    private String datatype;
    private Integer priority;

    public ClinicalHeader() {
    }

    /**
     * Constructor for ClinicalHeader with all Attributes present.
     */
    public ClinicalHeader(String displayName, String description, String datatype, Integer priority) {
        this.displayName = displayName;
        this.description = description;
        this.datatype = datatype;
        this.priority = priority;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

}
