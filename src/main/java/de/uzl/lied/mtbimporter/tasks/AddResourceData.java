package de.uzl.lied.mtbimporter.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import org.apache.commons.io.FileUtils;

import de.uzl.lied.mtbimporter.jobs.FhirResolver;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.SampleResource;
import de.uzl.lied.mtbimporter.settings.Settings;

public class AddResourceData {

    public static void processPdfFile(CbioPortalStudy study, File pdf) throws IOException {
        String sampleId = pdf.getName().replaceAll("somaticGermline_|somatic_|tumorOnly_|_Report|.pdf", "");
        String patientId = FhirResolver.resolvePatientFromSample(sampleId);
        FileUtils.copyFile(pdf,
                new File(Settings.getResourceFolder() + "/" + study.getStudyId() + "/" + patientId + "/" + pdf.getName()));
        SampleResource sr = new SampleResource();
        sr.setPatientId(patientId);
        sr.setSampleId(sampleId);
        sr.setResourceId("PATHOLOGY_SLIDE");
        try {
            sr.setUrl(Settings.getUrlBase() + "/" + study.getStudyId() +  "/" + patientId + "/" + pdf.getName());
        } catch (Exception e) {
            System.err.println("Skipped resource file due to invalid URL:" + Settings.getUrlBase() + "/" + patientId
                    + "/" + sampleId + "/" + pdf.getName());
        }
        study.addSampleResource(sr);
    }

    public static List<SampleResource> readResourceFile(File input) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);

        ObjectReader or = om.readerFor(SampleResource.class)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));

        MappingIterator<SampleResource> inputIterator = or.readValues(input);
        return inputIterator.readAll();
    }

    public static void writeResourceFile(Collection<SampleResource> resources, File target) throws JsonGenerationException, JsonMappingException, IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        CsvSchema s = om.schemaFor(SampleResource.class).withHeader().withColumnSeparator('\t').withoutQuoteChar();

        om.writer(s).writeValue(target, resources);
    }

}
