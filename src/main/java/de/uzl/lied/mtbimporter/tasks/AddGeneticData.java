package de.uzl.lied.mtbimporter.tasks;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import de.uzl.lied.mtbimporter.model.CaseList;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.Cna;
import de.uzl.lied.mtbimporter.model.ContinuousCna;
import de.uzl.lied.mtbimporter.model.GenePanelMatrix;
import de.uzl.lied.mtbimporter.model.Maf;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Add various types of genetic data to study.
 */
public final class AddGeneticData {

    private AddGeneticData() {
    }

    private static List<Maf> readMafFile(File maf) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(Maf.class)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));

        MappingIterator<Maf> inputIterator = or.readValues(maf);
        return inputIterator.readAll();
    }

    /**
     * Write mutation pojos to tsv.
     * @param mafs
     * @param maf
     * @throws IOException
     */
    public static void writeMafFile(List<Maf> mafs, File maf) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema s = om.schemaFor(Maf.class).withHeader().withColumnSeparator('\t').withoutQuoteChar();
        om.writer(s).writeValue(maf, mafs);
    }

    /**
     * Write continuous cna pojos to tsv.
     * @param segs
     * @param seg
     * @throws IOException
     */
    public static void writeSegFile(List<ContinuousCna> segs, File seg) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema s = om.schemaFor(ContinuousCna.class).withHeader().withColumnSeparator('\t').withoutQuoteChar();
        om.writer(s).writeValue(seg, segs);
    }

    private static List<ContinuousCna> readSegFile(File seg) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(ContinuousCna.class)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));

        MappingIterator<ContinuousCna> inputIterator = or.readValues(seg);
        return inputIterator.readAll();
    }

    public static void processSegFile(CbioPortalStudy study, File seg) throws IOException {
        study.addSeg(readSegFile(seg));
    }

    public static void processMafFile(CbioPortalStudy study, File maf) throws IOException {
        study.addMaf(readMafFile(maf));
    }

    public static void processGenePanelFile(CbioPortalStudy study, File input) throws IOException {
        study.setGenePanelMatrix(readGenePanelFile(input));
    }

    /**
     * Read gene panel file to pojo list.
     * @param panel
     * @return
     * @throws IOException
     */
    public static List<GenePanelMatrix> readGenePanelFile(File panel) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(GenePanelMatrix.class)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));

        MappingIterator<GenePanelMatrix> inputIterator = or.readValues(panel);
        return inputIterator.readAll();
    }

    /**
     * Write gene panel pojo to tsv.
     * @param panels
     * @param target
     * @throws IOException
     */
    public static void writeGenePanelFile(Collection<GenePanelMatrix> panels, File target) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema s = om.schemaFor(GenePanelMatrix.class).withHeader().withColumnSeparator('\t').withoutQuoteChar();
        om.writer(s).writeValue(target, panels);
    }

    /**
     * Read cna file to pojo list.
     * @param input
     * @return
     * @throws IOException
     */
    public static List<Cna> readCnaFile(File input) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(Cna.class)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));

        MappingIterator<Cna> inputIterator = or.readValues(input);
        return inputIterator.readAll();
    }

    /**
     * Write cna pojo to tsv.
     * @param cnas
     * @param target
     * @throws IOException
     */
    public static void writeCnaFile(Collection<Cna> cnas, File target) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema s = om.schemaFor(Cna.class).withHeader().withColumnSeparator('\t').withoutQuoteChar();

        if (!cnas.isEmpty()) {
            for (String id : cnas.iterator().next().getSamples().keySet()) {
                s = CsvSchema.builder().addColumnsFrom(s).addColumn(id).build();
            }
        }

        om.writer(s.withHeader().withColumnSeparator('\t').withoutQuoteChar()).writeValue(target, cnas);
    }

    public static void processCnaFile(CbioPortalStudy study, File cna) throws IOException {
        study.addCna(readCnaFile(cna));
    }

    /**
     * Write case list to file.
     * @param caseList
     * @param studyId
     * @param sampleIds
     * @throws IOException
     */
    public static void writeCaseList(File caseList, String studyId, Set<String> sampleIds) throws IOException {
        JavaPropsMapper jpm = new JavaPropsMapper();
        JavaPropsSchema jps = JavaPropsSchema.emptySchema().withKeyValueSeparator(": ");
        CaseList seq = jpm.readValue(caseList, CaseList.class);
        seq.setCancerStudyIdentifier(studyId);
        seq.setStableId(studyId + "_" + seq.getStableId().split("_")[seq.getStableId().split("_").length - 1]);
        seq.getCaseListIds().addAll(sampleIds);

        String seqStr = jpm.writer(jps).writeValueAsString(seq).replace("\\t", "\t");
        Files.write(caseList.toPath(), seqStr.getBytes());
    }
}
