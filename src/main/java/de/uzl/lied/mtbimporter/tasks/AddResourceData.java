package de.uzl.lied.mtbimporter.tasks;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.uzl.lied.mtbimporter.jobs.FhirResolver;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.SampleResource;
import de.uzl.lied.mtbimporter.settings.Settings;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.tinylog.Logger;

/**
 * Adds resource files to study.
 */
public final class AddResourceData {

    private AddResourceData() {
    }

    /**
     * Adds PDF-Report of MIRACUM-Pipe to study.
     * @param study
     * @param pdf
     * @throws IOException
     */
    public static void processPdfFile(CbioPortalStudy study, File pdf) throws IOException {
        String sampleId = pdf.getName().replaceAll("somaticGermline_|somatic_|tumorOnly_|_tumorOnly|_Report|.pdf", "");
        String patientId = FhirResolver.resolvePatientFromSample(sampleId);
        File target = new File(
                Settings.getResourceFolder(), study.getStudyId() + "/" + patientId + "/" + pdf.getName());
        FileUtils.copyFile(pdf, target);
        Files.setPosixFilePermissions(target.toPath(), PosixFilePermissions.fromString("rw-r--r--"));
        SampleResource sr = new SampleResource();
        sr.setPatientId(patientId);
        sr.setSampleId(sampleId);
        sr.setResourceId("PATHOLOGY_SLIDE");
        try {
            sr.setUrl(Settings.getUrlBase() + "/" + study.getStudyId() + "/" + patientId + "/" + pdf.getName());
        } catch (MalformedURLException e) {
            Logger.error("Skipped resource file due to invalid URL:" + Settings.getUrlBase() + "/" + patientId
                    + "/" + sampleId + "/" + pdf.getName());
        }
        study.addSampleResource(sr);
    }

    /**
     * Read existing resource definitions.
     * @param input
     * @return
     * @throws IOException
     */
    public static List<SampleResource> readResourceFile(File input) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);

        ObjectReader or = om.readerFor(SampleResource.class)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));

        MappingIterator<SampleResource> inputIterator = or.readValues(input);
        return inputIterator.readAll();
    }

    /**
     * Write resource definitions from pojo to tsv.
     * @param resources
     * @param target
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static void writeResourceFile(Collection<SampleResource> resources, File target) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema s = om.schemaFor(SampleResource.class).withHeader().withColumnSeparator('\t').withoutQuoteChar();

        om.writer(s).writeValue(target, resources);
    }

}
