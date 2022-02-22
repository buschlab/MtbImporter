package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Case list meta file.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaseList {

    @JsonProperty("cancer_study_identifier")
    private String cancerStudyIdentifier;
    @JsonProperty("stable_id")
    private String stableId;
    @JsonProperty("case_list_category")
    private String caseListCategory;
    @JsonProperty("case_list_name")
    private String caseListName;
    @JsonProperty("case_list_description")
    private String caseListDescription;
    private Set<String> caseListIds = new HashSet<>();

    public String getCancerStudyIdentifier() {
        return cancerStudyIdentifier;
    }

    public void setCancerStudyIdentifier(String cancerStudyIdentifier) {
        this.cancerStudyIdentifier = cancerStudyIdentifier;
    }

    public String getStableId() {
        return stableId;
    }

    public void setStableId(String stableId) {
        this.stableId = stableId;
    }

    @JsonProperty("case_list_ids")
    public void setCaseListIds(String ids) {
        caseListIds.addAll(Arrays.asList(ids.split("\t")));
    }

    public Set<String> getCaseListIds() {
        return this.caseListIds;
    }

    @JsonProperty("case_list_ids")
    public String getCaseListIdsString() {
        return caseListIds.stream()
                .map(n -> String.valueOf(n))
                .collect(Collectors.joining("\t"));
    }

}
