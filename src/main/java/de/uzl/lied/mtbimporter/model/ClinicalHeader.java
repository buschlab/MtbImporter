package de.uzl.lied.mtbimporter.model;

public class ClinicalHeader {

    private String displayName;
    private String description;
    private String datatype;
    private Number priority;

    public ClinicalHeader() {
    }

    public ClinicalHeader(String displayName, String description, String datatype, Number priority) {
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

    public Number getPriority() {
        return priority;
    }

    public void setPriority(Number priority) {
        this.priority = priority;
    }

}
