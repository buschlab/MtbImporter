package de.uzl.lied.mtbimporter.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;

import org.mozilla.universalchardet.ReaderFactory;

import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.uzl.lied.mtbimporter.jobs.mdr.centraxx.CxxMdrConvert;
import de.uzl.lied.mtbimporter.jobs.mdr.samply.SamplyMdrConvert;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.ClinicalPatient;
import de.uzl.lied.mtbimporter.model.ClinicalSample;
import de.uzl.lied.mtbimporter.model.Timeline;
import de.uzl.lied.mtbimporter.model.TimelineTreatment;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.RelationConvert;
import de.uzl.lied.mtbimporter.settings.CxxMdrSettings;
import de.uzl.lied.mtbimporter.settings.Mdr;
import de.uzl.lied.mtbimporter.settings.SamplyMdrSettings;
import de.uzl.lied.mtbimporter.settings.Settings;

public class AddHisData {

    public static Map<String, String> stMap = null;

    public static void processCsv(CbioPortalStudy study, File csv)
            throws IOException, ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(new TypeReference<HashMap<String, String>>() {
        }).with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator(';'));

        Iterator<Map<String, Object>> inputIterator = or.readValues(ReaderFactory.createBufferedReader(csv));
        List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
        while (inputIterator.hasNext()) {
            l.add(inputIterator.next());
        }

        RelationConvert input = new RelationConvert();
        input.setSourceProfileVersion("1");
        input.setTargetProfileVersion("1");

        CxxMdrSettings cxxMdr = null;
        SamplyMdrSettings samplyMdr = null;

        for (Mdr m : Settings.getMdr()) {
            if (m.getCxx() != null && m.getCxx().isMappingEnabled()) {
                cxxMdr = m.getCxx();
            } else if (m.getSamply() != null && m.getSamply().isMappingEnabled()) {
                samplyMdr = m.getSamply();
            }
        }

        for (Map<String, Object> m : l) {
            input.setValues(m);
            for (Entry<String, String> e : study.getPreparation((String) m.get("PID")).entrySet()) {
                input.getValues().put("_" + e.getKey(), e.getValue());
            }

            if (Settings.getMappingMethod().equals("cxx") && cxxMdr != null) {

                input.setSourceProfileCode("orbis_l-tumorboard-molekular");
                input.setTargetProfileCode("cbioportal_patient");
                study.addPatient(cxxMap(ClinicalPatient.class, cxxMdr, input));

                input.setSourceProfileCode("orbis_l-tumorboard-molekular-vorbef-molekula");
                input.setTargetProfileCode("cbioportal_sample");
                study.addSample(cxxMap(ClinicalSample.class, cxxMdr, input));

                input.setSourceProfileCode("orbis_l-tumorboard-molekular-diagnosen-vorst");
                input.setTargetProfileCode("cbioportal_timeline-diagnostic");
                study.addTimeline(Timeline.class, cxxMap(Timeline.class, cxxMdr, input));

                input.setSourceProfileCode("orbis_l-tumorboard-molekular-therapielinien");
                input.setTargetProfileCode("cbioportal_timeline-treatment");
                study.addTimeline(TimelineTreatment.class, cxxMap(TimelineTreatment.class, cxxMdr, input));

            } else if (Settings.getMappingMethod().equals("groovy") && samplyMdr != null) {

                input.setSourceProfileCode("L_Tumorboard_Molekular");
                input.setTargetProfileCode("cbioportal-patient");
                study.addPatient(samplyMap(ClinicalPatient.class, samplyMdr, input));

                input.setTargetProfileCode("cbioportal-sample");
                study.addSample(samplyMap(ClinicalSample.class, samplyMdr, input));

                input.setSourceProfileCode("L_Tumorboard_Molekular-Diagnosen");
                input.setTargetProfileCode("cbioportal-timeline-diagnostic");
                study.addTimeline(Timeline.class, samplyMap(Timeline.class, samplyMdr, input));

                input.setSourceProfileCode("L_Tumorboard_Molekular-Therapielinien");
                input.setTargetProfileCode("cbioportal-timeline-treatment");
                study.addTimeline(TimelineTreatment.class, samplyMap(TimelineTreatment.class, samplyMdr, input));

            } else if (Settings.getMappingMethod().equals("none")) {

                // TODO

            }
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T readClass(Class<T> c, RelationConvert relation) throws JsonProcessingException, IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(c)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));
        CsvSchema s = CsvSchema.builder().addColumns(relation.getValues().keySet(), ColumnType.NUMBER_OR_STRING).build()
                .withHeader().withColumnSeparator('\t');
        String str = om.writer(s).writeValueAsString(relation.getValues());
        return (T) or.readValues(str).next();
    }

    private static <T> T cxxMap(Class<T> c, CxxMdrSettings mdr, RelationConvert input)
            throws JsonProcessingException, IOException {
        RelationConvert output = CxxMdrConvert.convert(mdr, input);
        if (!output.getValues().isEmpty()) {
            if (readClass(c, output) instanceof ClinicalSample) {
                ClinicalSample cs = (ClinicalSample) readClass(c, output);
                if (cs.getSampleId() == null) {
                    return null;
                }
            }
            return readClass(c, output);
        }
        return null;
    }

    private static <T> T samplyMap(Class<T> c, SamplyMdrSettings mdr, RelationConvert input) throws ExecutionException,
            MdrConnectionException, MdrInvalidResponseException, JsonProcessingException, IOException {
        RelationConvert output = SamplyMdrConvert.convert(mdr, input);
        if (!output.getValues().isEmpty()) {
            if (readClass(c, output) instanceof ClinicalSample) {
                ClinicalSample cs = (ClinicalSample) readClass(c, output);
                if (cs.getSampleId() == null) {
                    return null;
                }
            }
            return readClass(c, output);
        }
        return null;
    }

    public static void prepare(File csv, CbioPortalStudy study) throws IOException {

        Map<String, Map<String, String>> pMap = new HashMap<String, Map<String, String>>();

        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(new TypeReference<HashMap<String, String>>() {
        }).with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator(';'));

        Iterator<Map<String, String>> inputIterator = or.readValues(ReaderFactory.createBufferedReader(csv));
        List<Map<String, String>> l = new ArrayList<Map<String, String>>();
        while (inputIterator.hasNext()) {
            l.add(inputIterator.next());
        }
        for (Map<String, String> m : l) {
            if (pMap.containsKey(m.get("PID"))) {
                Map<String, String> n = pMap.get(m.get("PID"));
                if (Integer.parseInt(m.get("JAHR_TEXT")) < Integer.parseInt(n.get("JAHR_TEXT"))) {
                    if (Integer.parseInt(m.get("MONAT_TEXT")) < Integer.parseInt(n.get("MONAT_TEXT"))) {
                        pMap.put(m.get("PID"), m);
                    }
                }
            } else {
                pMap.put(m.get("PID"), m);
            }
        }

        for (Entry<String, Map<String, String>> e : pMap.entrySet()) {
            study.addPreparation(e.getKey(), e.getValue());
        }

    }

}
