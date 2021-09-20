package de.uzl.lied.mtbimporter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Maf {

    @JsonProperty("Hugo_Symbol")
    private String hugoSymbol;
    @JsonProperty("Entrez_Gene_Id")
    private String entrezGeneId;
    @JsonProperty("Center")
    private String center;
    @JsonProperty("NCBI_Build")
    private String NcbiBuild;
    @JsonProperty("Chromosome")
    private String chromosome;
    @JsonProperty("Start_Position")
    private Integer startPosition;
    @JsonProperty("End_Position")
    private Integer endPosition;
    @JsonProperty("Strand")
    private Character strand;
    @JsonProperty("Variant_Classification")
    private String variantClassification;
    @JsonProperty("Variant_Type")
    private String variantType;
    @JsonProperty("Reference_Allele")
    private String referenceAllele;
    @JsonProperty("Tumor_Seq_Allele1")
    private String tumorSeqAllele1;
    @JsonProperty("Tumor_Seq_Allele2")
    private String tumorSeqAllele2;
    @JsonProperty("dbSNP_RS")
    private String dbSnpRs;
    @JsonProperty("dbSNP_Val_Status")
    private String dbSnpValStatus;
    @JsonProperty("Tumor_Sample_Barcode")
    private String tumorSampleBarcode;
    @JsonProperty("Matched_Norm_Sample_Barcode")
    private String matchedNormSampleBarcode;
    @JsonProperty("Match_Norm_Seq_Allele1")
    private String matchNormSeqAllele1;
    @JsonProperty("Match_Norm_Seq_Allele2")
    private String matchNormSeqAllele2;
    @JsonProperty("Tumor_Validation_Allele1")
    private String tumorValidationAllele1;
    @JsonProperty("Tumor_Validation_Allele2")
    private String tumorValidationAllele2;
    @JsonProperty("Match_Norm_Validation_Allele1")
    private String matchNormValidationAllele1;
    @JsonProperty("Match_Norm_Validation_Allele2")
    private String matchNormValidationAllele2;
    @JsonProperty("Verification_Status")
    private String verificationStatus;
    @JsonProperty("Validation_Status")
    private String validationStatus;
    @JsonProperty("Mutation_Status")
    private String mutationStatus;
    @JsonProperty("Sequencing_Phase")
    private String sequencingPhase;
    @JsonProperty("Sequencing_Source")
    private String sequencingSource;
    @JsonProperty("Validation_Method")
    private String validationMethod;
    @JsonProperty("Score")
    private String score;
    @JsonProperty("BAM_File")
    private String bamFile;
    @JsonProperty("Sequencer")
    private String sequencer;
    @JsonProperty("HGVSp_Short")
    private String hgvspShort;
    @JsonProperty("TxChange")
    private String txChange;
    @JsonProperty("Transcript_Id")
    private String transcriptId;
    @JsonProperty("ENSEMBL_Gene_Id")
    private String EnsemblGeneId;
    @JsonProperty("t_ref_count")
    private Integer tRefCount;
    @JsonProperty("t_alt_count")
    private Integer tAltCount;
    @JsonProperty("n_ref_count")
    private Integer nRefCount;
    @JsonProperty("n_alt_count")
    private Integer nAltCount;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getTumorSampleBarcode() {
        return this.tumorSampleBarcode;
    }

    public void setTumorSampleBarcode(String tumorSampleBarcode) {
        this.tumorSampleBarcode = tumorSampleBarcode.replaceAll("_TD", "");
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public Integer getEndPosition() {
        return endPosition;
    }

    public String getTxChange() {
        return txChange;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static List<Maf> merge(Collection<Maf> maf1, Collection<Maf> maf2) {
        return new ArrayList<Maf>(Stream.of(maf1, maf2).flatMap(Collection::stream).collect(Collectors.toMap(m -> {
            String s = m.getTumorSampleBarcode() + ";";
            s += m.getStartPosition() + ";";
            s += m.getEndPosition() + ";";
            s += m.getTxChange();
            return s;
        }, Function.identity(), (Maf x, Maf y) -> y)).values());
    }

}
