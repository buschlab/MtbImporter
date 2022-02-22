package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * Mutational signature entry.
 */
@JsonPropertyOrder({
    "ENTITY_STABLE_ID",
    "NAME",
    "DESCRIPTION"
})
public class MutationalSignature {

    @JsonProperty("ENTITY_STABLE_ID")
    private String entityStableId;
    @JsonProperty("NAME")
    private String name;
    @JsonProperty("DESCRIPTION")
    private String description;
    @JsonIgnore
    private Map<String, Number> samples = new HashMap<>();

    public String getEntityStableId() {
        return entityStableId;
    }

    public void setEntityStableId(String entityStableId) {
        this.entityStableId = entityStableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonAnyGetter
    public Map<String, Number> getSamples() {
        return this.samples;
    }

    @JsonAnySetter
    public void setSamples(String key, Number value) {
        this.samples.put(key, value);
    }

}
