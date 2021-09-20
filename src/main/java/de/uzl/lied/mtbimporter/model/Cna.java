package de.uzl.lied.mtbimporter.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cna {
    
    @JsonProperty("Hugo_Symbol")
    private String hugoSymbol;
    @JsonProperty("Entrez_Gene_Id")
    private String entrezGeneId;
    @JsonIgnore
    private Map<String, Integer> samples = new HashMap<String, Integer>();

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
    public Map<String, Integer> getSamples() {
        return this.samples;
    }

    @JsonAnySetter
    public void setSamples(String name, Integer value) {
        this.samples.put(name, value);
    }
}
