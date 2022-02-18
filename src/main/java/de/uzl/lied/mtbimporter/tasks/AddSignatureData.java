package de.uzl.lied.mtbimporter.tasks;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.MutationalSignature;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Adds mutational signatures to study.
 */
public final class AddSignatureData {

    private AddSignatureData() {
    }

    public static void processLimit(CbioPortalStudy study, File limit) throws IOException {
        study.addMutationalLimit(readSignatureData(limit));
    }

    public static void processContribution(CbioPortalStudy study, File contribution) throws IOException {
        study.addMutationalContribution(readSignatureData(contribution));
    }

    /**
     * Reads existing files of mutational signatures.
     * @param input
     * @return
     * @throws IOException
     */
    public static List<MutationalSignature> readSignatureData(File input) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(MutationalSignature.class)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));

        MappingIterator<MutationalSignature> inputIterator = or.readValues(input);
        return inputIterator.readAll();

    }

    /**
     * Writes signature data das tsv file.
     * @param signatures
     * @param target
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static void writeSignatureData(Collection<MutationalSignature> signatures, File target)
            throws JsonGenerationException, JsonMappingException, IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema s = om.schemaFor(MutationalSignature.class).withHeader().withColumnSeparator('\t').withoutQuoteChar();

        for (String id : signatures.iterator().next().getSamples().keySet()) {
            s = CsvSchema.builder().addColumnsFrom(s).addColumn(id).build();
        }
        om.writer(s.withHeader().withColumnSeparator('\t').withoutQuoteChar()).writeValue(target, signatures);
    }

}
