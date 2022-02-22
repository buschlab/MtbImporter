package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/**
 * Pojo for discrete copy number alteration.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cna {

    @JsonProperty("Hugo_Symbol")
    private String hugoSymbol;
    @JsonProperty("Entrez_Gene_Id")
    private String entrezGeneId;
    @JsonIgnore
    private Map<String, String> samples = new HashMap<>();

    public String getHugoSymbol() {
        return this.hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    public String getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    @JsonAnyGetter
    public Map<String, String> getSamples() {
        return this.samples;
    }

    @JsonAnySetter
    public void setSamples(String name, String value) {
        this.samples.put(name, value);
    }
}
