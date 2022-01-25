
package de.uzl.lied.mtbimporter.model.mdr.centraxx;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import java.util.Map;

/**
 * Model for Form in Kairos CentraXX MDR.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "caption",
    "sections",
    "fields",
    "folderId",
    "version",
    "systemUrl",
    "validFrom",
    "validUntil",
    "approvalStatus",
    "modificationTime",
})
public class CxxForm {

    private String id;
    private Map<String, CxxCaption> caption;
    private List<CxxSection> sections;
    private List<CxxField> fields;
    private String folderId;
    private Integer version;
    private String systemUrl;
    private Object validFrom;
    private Object validUntil;
    private String approvalStatus;
    private Long modificationTime;
    private List<CxxLinks> links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, CxxCaption> getCaption() {
        return caption;
    }

    public void setCaption(Map<String, CxxCaption> caption) {
        this.caption = caption;
    }

    public List<CxxSection> getSections() {
        return sections;
    }

    public void setSections(List<CxxSection> sections) {
        this.sections = sections;
    }

    public List<CxxField> getFields() {
        return fields;
    }

    public void setFields(List<CxxField> fields) {
        this.fields = fields;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getSystemUrl() {
        return systemUrl;
    }

    public void setSystemUrl(String systemUrl) {
        this.systemUrl = systemUrl;
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

    public List<CxxLinks> getLinks() {
        return links;
    }

    public void setLinks(List<CxxLinks> links) {
        this.links = links;
    }

}
