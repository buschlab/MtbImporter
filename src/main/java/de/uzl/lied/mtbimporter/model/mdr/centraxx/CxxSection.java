
package de.uzl.lied.mtbimporter.model.mdr.centraxx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;

/**
 * Model for Section in Kairos CentraXX MDR.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "code",
    "caption",
    "fields",
    "sections",
    "column",
    "row",
    "columnSpan",
    "rowSpan",
    "version",
    "validFrom",
    "validUntil",
    "multiValue",
    "approvalStatus",
    "modificationTime"
})
public class CxxSection {

    private String code;
    private Map<String, CxxCaption> caption;
    private List<CxxField> fields;
    private List<CxxSection> sections;
    private Integer column;
    private Integer row;
    private Integer columnSpan;
    private Integer rowSpan;
    private Integer version;
    private Object validFrom;
    private Object validUntil;
    private Boolean multiValue;
    private String approvalStatus;
    private Long modificationTime;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, CxxCaption> getCaption() {
        return caption;
    }

    public void setCaption(Map<String, CxxCaption> caption) {
        this.caption = caption;
    }

    public List<CxxField> getFields() {
        return fields;
    }

    public void setFields(List<CxxField> fields) {
        this.fields = fields;
    }

    public List<CxxSection> getSections() {
        return sections;
    }

    public void setSections(List<CxxSection> sections) {
        this.sections = sections;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(Integer columnSpan) {
        this.columnSpan = columnSpan;
    }

    public Integer getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(Integer rowSpan) {
        this.rowSpan = rowSpan;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Object getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Object validFrom) {
        this.validFrom = validFrom;
    }

    public Object getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Object validUntil) {
        this.validUntil = validUntil;
    }

    public Boolean getMultiValue() {
        return multiValue;
    }

    public void setMultiValue(Boolean multiValue) {
        this.multiValue = multiValue;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Long modificationTime) {
        this.modificationTime = modificationTime;
    }

}
