
package de.uzl.lied.mtbimporter.model.mdr.centraxx;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "itemType",
    "caption",
    "unit",
    "visible",
    "mandatory",
    "calculated",
    "series",
    "definition",
    "version",
    "validFrom",
    "validUntil",
    "systemUrl",
    "referenceRange",
    "source",
    "validator",
    "linkedForm",
    "linkedSection",
    "approvalStatus",
    "modificationTime"
})
public class CxxItem {

    private String id;
    private String itemType;
    private Map<String, CxxCaption> caption;
    private Object unit;
    private Boolean visible;
    private Boolean mandatory;
    private Boolean calculated;
    private Boolean series;
    private String definition;
    private Integer version;
    private Object validFrom;
    private Object validUntil;
    private String systemUrl;
    private Object referenceRange;
    private Object source;
    private Object validator;
    private Object linkedForm;
    private Object linkedSection;
    private String approvalStatus;
    private Long modificationTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Map<String, CxxCaption> getCaption() {
        return caption;
    }

    public void setCaption(Map<String, CxxCaption> caption) {
        this.caption = caption;
    }

    public Object getUnit() {
        return unit;
    }

    public void setUnit(Object unit) {
        this.unit = unit;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Boolean getCalculated() {
        return calculated;
    }

    public void setCalculated(Boolean calculated) {
        this.calculated = calculated;
    }

    public Boolean getSeries() {
        return series;
    }

    public void setSeries(Boolean series) {
        this.series = series;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
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

    public String getSystemUrl() {
        return systemUrl;
    }

    public void setSystemUrl(String systemUrl) {
        this.systemUrl = systemUrl;
    }

    public Object getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(Object referenceRange) {
        this.referenceRange = referenceRange;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getValidator() {
        return validator;
    }

    public void setValidator(Object validator) {
        this.validator = validator;
    }

    public Object getLinkedForm() {
        return linkedForm;
    }

    public void setLinkedForm(Object linkedForm) {
        this.linkedForm = linkedForm;
    }

    public Object getLinkedSection() {
        return linkedSection;
    }

    public void setLinkedSection(Object linkedSection) {
        this.linkedSection = linkedSection;
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
