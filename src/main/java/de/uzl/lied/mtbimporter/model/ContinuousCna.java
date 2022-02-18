package de.uzl.lied.mtbimporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Pojo for continuous copy number alteration.
 */
@JsonPropertyOrder({ "ID", "chrom", "loc.start", "loc.end", "num.mark", "seg.mean" })
public class ContinuousCna {

    @JsonProperty("ID")
    private String id;
    @JsonProperty("chrom")
    private String chrom;
    @JsonProperty("loc.start")
    private String locStart;
    @JsonProperty("loc.end")
    private String locEnd;
    @JsonProperty("num.mark")
    private Integer numMark;
    @JsonProperty("seg.mean")
    private Number segMean;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.replaceAll("_TD", "");
    }

    public String getChrom() {
        return chrom;
    }

    public void setChrom(String chrom) {
        this.chrom = chrom;
    }

    public String getLocStart() {
        return locStart;
    }

    public void setLocStart(String locStart) {
        this.locStart = locStart;
    }

    public String getLocEnd() {
        return locEnd;
    }

    public void setLocEnd(String locEnd) {
        this.locEnd = locEnd;
    }

    public Integer getNumMark() {
        return numMark;
    }

    public void setNumMark(Integer numMark) {
        this.numMark = numMark;
    }

    public Number getSegMean() {
        return segMean;
    }

    public void setSegMean(Number segMean) {
        this.segMean = segMean;
    }

    /**
     * Method for merging two lists of continuous CNA.
     * @param continuous1 List 1
     * @param continuous2 List 2
     * @return Merged list of continuous cna.
     */
    public static List<ContinuousCna> merge(List<ContinuousCna> continuous1, List<ContinuousCna> continuous2) {
        return new ArrayList<ContinuousCna>(Stream.of(continuous1, continuous2).flatMap(List::stream)
                .collect(Collectors.toMap(
                        c -> c.getId() + ";" + c.getChrom() + ";" + c.getLocStart() + ";" + c.getLocEnd(),
                        Function.identity(), (ContinuousCna x, ContinuousCna y) -> y))
                .values());
    }

}
