package de.uzl.lied.mtbimporter.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.uzl.lied.mtbimporter.jobs.FhirResolver;
import de.uzl.lied.mtbimporter.jobs.mdr.centraxx.CxxMdrAttributes;
import de.uzl.lied.mtbimporter.jobs.mdr.samply.SamplyMdrAttributes;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.Clinical;
import de.uzl.lied.mtbimporter.model.ClinicalHeader;
import de.uzl.lied.mtbimporter.model.ClinicalPatient;
import de.uzl.lied.mtbimporter.model.ClinicalSample;
import de.uzl.lied.mtbimporter.settings.Mdr;
import de.uzl.lied.mtbimporter.settings.Settings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.tinylog.Logger;

/**
 * Adds data to both patients and samples.
 */
public final class AddClinicalData {

    private AddClinicalData() {
    }

    private static Collection<ClinicalPatient> readClinicalPatient(File clinicalPatient) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(ClinicalPatient.class)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));

        MappingIterator<ClinicalPatient> inputIterator = or.readValues(clinicalPatient);
        return inputIterator.readAll();

    }

    private static Map<String, ClinicalHeader> readClinicalAttributes(File clinicalFile) throws FileNotFoundException {
        Map<String, ClinicalHeader> clinicalHeaders = new HashMap<>();
        Scanner scanner = new Scanner(clinicalFile);
        String[] displayName = new String[0];
        String[] description = new String[0];
        String[] datatype = new String[0];
        String[] priority = new String[0];
        String[] keys = new String[0];
        if (scanner.hasNextLine()) {
            displayName = scanner.nextLine().split("\t");
            displayName[0] = displayName[0].replaceFirst("#", "");
        }
        if (scanner.hasNextLine()) {
            description = scanner.nextLine().split("\t");
            description[0] = description[0].replaceFirst("#", "");
        }
        if (scanner.hasNextLine()) {
            datatype = scanner.nextLine().split("\t");
            datatype[0] = datatype[0].replaceFirst("#", "");
        }
        if (scanner.hasNextLine()) {
            priority = scanner.nextLine().split("\t");
            priority[0] = priority[0].replaceFirst("#", "");
        }
        if (scanner.hasNextLine()) {
            keys = scanner.nextLine().split("\t");
        }

        scanner.close();

        for (int i = 0; i < keys.length; i++) {
            clinicalHeaders.put(keys[i],
                    new ClinicalHeader(displayName[i], description[i], datatype[i],
                            (int) Double.parseDouble(priority[i])));
        }

        return clinicalHeaders;
    }

    public static void writeClinicalPatient(Collection<ClinicalPatient> clinicalPatients,
            Map<String, ClinicalHeader> patientAttributes, File target) throws IOException {
        writeClinical(target, clinicalPatients, ClinicalPatient.class, patientAttributes, "cbioportal_patient");
    }

    public static void processClinicalPatient(CbioPortalStudy study, File clinicalPatient) throws IOException {
        study.addPatient(readClinicalPatient(clinicalPatient));
        study.addPatientAttributes(readClinicalAttributes(clinicalPatient));
    }

    private static Collection<ClinicalSample> readClinicalSample(File clinicalSample) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(ClinicalSample.class)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));

        MappingIterator<ClinicalSample> inputIterator = or.readValues(clinicalSample);
        return inputIterator.readAll();
    }

    public static void writeClinicalSample(Collection<ClinicalSample> clinicalSamples,
            Map<String, ClinicalHeader> sampleAttributes, File target) throws IOException {
        writeClinical(target, clinicalSamples, ClinicalSample.class, sampleAttributes, "cbioportal_sample");
    }

    public static void processClinicalSample(CbioPortalStudy study, File clinicalSample) throws IOException {
        study.addSample(readClinicalSample(clinicalSample));
        study.addSampleAttributes(readClinicalAttributes(clinicalSample));
    }

    /**
     * Merges content of two patients.
     * @param oldPatient
     * @param newPatient
     * @return
     */
    public static ClinicalPatient mergePatients(ClinicalPatient oldPatient, ClinicalPatient newPatient) {

        if (newPatient.getAdditionalAttributes() != null) {
            for (Entry<String, Object> e : newPatient.getAdditionalAttributes().entrySet()) {
                if (e.getKey().equals("PATIENT_DISPLAY_NAME") && e.getValue().equals("Unknown")) {
                    continue;
                }
                oldPatient.getAdditionalAttributes().put(e.getKey(), e.getValue());
            }
        }

        return oldPatient;

    }

    /**
     * Merges content of two samples.
     * @param oldSample
     * @param newSample
     * @return
     */
    public static ClinicalSample mergeSamples(ClinicalSample oldSample, ClinicalSample newSample) {

        if (newSample.getAdditionalAttributes() != null) {
            for (Entry<String, Object> e : newSample.getAdditionalAttributes().entrySet()) {
                oldSample.getAdditionalAttributes().put(e.getKey(), e.getValue());
            }
        }

        return oldSample;

    }

    private static <T> void writeClinical(File inputFile, Collection<T> clinicals, Class<T> cl,
            Map<String, ClinicalHeader> clinicalAttributes, String mdrProfile) throws JsonProcessingException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);

        Set<String> keys = new LinkedHashSet<>();
        keys.add("PATIENT_ID");
        if (cl.isAssignableFrom(ClinicalSample.class)) {
            keys.add("SAMPLE_ID");
        }

        for (T clinical : clinicals) {
            Clinical c = (Clinical) clinical;
            keys.addAll(c.getAdditionalAttributes().keySet());
        }

        Map<String, ClinicalHeader> attributes = new LinkedHashMap<>();
        for (String key : keys) {
            ClinicalHeader ch = null;
            Iterator<Mdr> mdrIterator = Settings.getMdr().iterator();
            while (ch == null && mdrIterator.hasNext()) {
                Mdr m = mdrIterator.next();
                if (m.getCxx() != null) {
                    ch = CxxMdrAttributes.getAttributes(m.getCxx(), mdrProfile, key);
                }
                if (m.getSamply() != null) {
                    try {
                        ch = SamplyMdrAttributes.getAttributes(m.getSamply(), mdrProfile, key);
                    } catch (ExecutionException | MdrConnectionException | MdrInvalidResponseException e) {
                        Logger.error("Could not get data from Samply MDR.");
                        Logger.debug(e);
                    }
                }
            }
            attributes.put(key, ch != null ? ch : clinicalAttributes.get(key));
        }

        Builder schemaBuilder = CsvSchema.builder();
        for (String col : keys) {
            schemaBuilder.addColumn(col);
        }
        CsvSchema schema = schemaBuilder.setColumnSeparator('\t').setUseHeader(true).build().withoutQuoteChar();

        String clinicalString = om.writer(schema).writeValueAsString(clinicals);
        try (FileOutputStream fos = new FileOutputStream(inputFile)) {
            StringBuilder displayNameString = new StringBuilder("#");
            StringBuilder descriptionString = new StringBuilder("#");
            StringBuilder dataTypesString = new StringBuilder("#");
            StringBuilder priorityString = new StringBuilder("#");
            for (ClinicalHeader ch : attributes.values()) {
                displayNameString.append("\t" + ch.getDisplayName());
                descriptionString.append("\t" + ch.getDescription());
                dataTypesString.append("\t" + ch.getDatatype());
                priorityString.append("\t" + ch.getPriority());
            }
            clinicalString = displayNameString.toString().replaceFirst("\t", "") + "\n"
                    + descriptionString.toString().replaceFirst("\t", "") + "\n"
                    + dataTypesString.toString().replaceFirst("\t", "") + "\n"
                    + priorityString.toString().replaceFirst("\t", "") + "\n" + clinicalString;
            fos.write(String.valueOf(clinicalString).getBytes());
        } catch (IOException e) {
            Logger.error("Cloud not write clinical file to disk.");
            Logger.debug(e);
        }
    }

    /**
     * Adds dummy patient.
     * @param study
     * @param sampleId
     */
    public static void addDummyPatient(CbioPortalStudy study, String sampleId) {
        ClinicalPatient cp = new ClinicalPatient();
        String patientId = FhirResolver.resolvePatientFromSample(sampleId.replace("_TD", ""));
        if (patientId == null) {
            return;
        }
        cp.setPatientId(patientId);
        cp.setAdditionalAttributes("PATIENT_DISPLAY_NAME", "Unknown");
        ClinicalSample cs = new ClinicalSample();
        cs.setPatientId(patientId);
        cs.setSampleId(sampleId);
        study.addSample(cs);
        if (study.getPatient(patientId) != null) {
            return;
        }
        study.add(cp);
    }

    /**
     * Adds multiple dummy patients.
     * @param study
     * @param sampleIds
     */
    public static void addDummyPatient(CbioPortalStudy study, Collection<String> sampleIds) {
        for (String sampleId : sampleIds) {
            ClinicalPatient cp = new ClinicalPatient();
            String patientId = FhirResolver.resolvePatientFromSample(sampleId.replace("_TD", ""));
            if (patientId == null) {
                return;
            }
            cp.setPatientId(patientId);
            cp.setAdditionalAttributes("PATIENT_DISPLAY_NAME", "Unknown");
            ClinicalSample cs = new ClinicalSample();
            cs.setPatientId(patientId);
            cs.setSampleId(sampleId);
            study.addSample(cs);
            if (study.getPatient(patientId) != null) {
                continue;
            }
            study.add(cp);
        }
    }

}
