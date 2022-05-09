package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Pojo for meta files.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Meta {

    @JsonProperty("cancer_study_identifier")
    private String cancerStudyIdentifier;
    @JsonIgnore
    private Set<String> namespaces = new HashSet<>();
    @JsonIgnore
    private Map<String, String> additionalAttributes = new HashMap<>();

    @JsonProperty("namespaces")
    public String getNamespaces() {
        return namespaces.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * adds Namespaces to meta_mutations_extended.
     * @param n Namespaced columns from Maf
     */
    @JsonIgnore
    public void addNamespace(Collection<String> n) {
        if (n != null && !n.isEmpty()) {
            n.forEach(a -> namespaces.add(a.split("\\.")[0]));
        }
    }

    public String getCancerStudyIdentifier() {
        return cancerStudyIdentifier;
    }

    public void setCancerStudyIdentifier(String cancerStudyIdentifier) {
        this.cancerStudyIdentifier = cancerStudyIdentifier;
    }

    @JsonAnyGetter
    public Map<String, String> getAdditionalAttributes() {
        return this.additionalAttributes;
    }

    /**
     * Prevents namespace from being read as an additional attribute.
     * @param name
     * @param value
     */
    @JsonAnySetter
    public void setAdditionalAttributes(String name, String value) {
        if (!"namespaces".equals(name)) {
            this.additionalAttributes.put(name, value);
        }
    }

}
