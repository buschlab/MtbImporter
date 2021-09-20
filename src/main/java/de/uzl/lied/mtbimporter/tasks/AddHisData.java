package de.uzl.lied.mtbimporter.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;

import org.mozilla.universalchardet.ReaderFactory;

import de.uzl.lied.mtbimporter.jobs.mdr.centraxx.CxxMdrConvert;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.ClinicalPatient;
import de.uzl.lied.mtbimporter.model.ClinicalSample;
import de.uzl.lied.mtbimporter.model.Timeline;
import de.uzl.lied.mtbimporter.model.TimelineTreatment;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.RelationConvert;
import de.uzl.lied.mtbimporter.settings.CxxMdrSettings;
import de.uzl.lied.mtbimporter.settings.Mdr;
import de.uzl.lied.mtbimporter.settings.Settings;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class AddHisData {

    public static Map<String, String> stMap = null;

    public static void processCsv(CbioPortalStudy study, File csv) throws IOException {

        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(new TypeReference<HashMap<String, String>>() {
        }).with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator(';'));

        Iterator<Map<String, Object>> inputIterator = or.readValues(ReaderFactory.createBufferedReader(csv));
        List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();
        while (inputIterator.hasNext()) {
            l.add(inputIterator.next());
        }

        RelationConvert input = new RelationConvert();
        RelationConvert output;
        input.setSourceProfileVersion("1");
        input.setTargetProfileVersion("1");

        CxxMdrSettings mdr = null;

        for (Mdr m : Settings.getMdr()) {
            if (m.getCxx() != null && m.getCxx().isMappingEnabled()) {
                mdr = m.getCxx();
            }
        }

        for (Map<String, Object> m : l) {
            input.setValues(m);
            for(Entry<String, String> e : study.getPreparation((String) m.get("PID")).entrySet()) {
                input.getValues().put("_" + e.getKey(), e.getValue());
            }

            if (Settings.getMappingMethod().equals("cxx") && mdr != null) {

                input.setSourceProfileCode("L_Tumorboard_Molekular");
                input.setTargetProfileCode("cbioportal-patient");
                study.addPatient(cxxMap(ClinicalPatient.class, mdr, input));

                input.setTargetProfileCode("cbioportal-sample");
                study.addSample(cxxMap(ClinicalSample.class, mdr, input));
                
                input.setSourceProfileCode("L_Tumorboard_Molekular-Diagnosen");
                input.setTargetProfileCode("cbioportal-timeline-diagnostic");
                study.addTimeline(Timeline.class, cxxMap(Timeline.class, mdr, input));

                input.setSourceProfileCode("L_Tumorboard_Molekular-Therapielinien");
                input.setTargetProfileCode("cbioportal-timeline-treatment");
                study.addTimeline(TimelineTreatment.class, cxxMap(TimelineTreatment.class, mdr, input));

            } else if (Settings.getMappingMethod().equals("groovy")) {

                output = input;
                Map<String, Object> mapped = new LinkedHashMap<String, Object>();

                Binding b = new Binding();
                for (Entry<String, Object> e : input.getValues().entrySet()) {
                    b.setVariable(e.getKey(), e.getValue());
                }
                GroovyShell s = new GroovyShell(b);
                for (Entry<String, Object> e : input.getValues().entrySet()) {
                    String mapTarget = getMapTarget(e.getKey());
                    if (mapTarget == null) {
                        continue;
                    }
                    b.setVariable("src", e.getValue());
                    Object target = s.evaluate(new File("mapper/" + e.getKey().toLowerCase() + ".groovy"));
                    mapped.put(mapTarget, target);
                }

                output.setValues(mapped);
                study.addPatient(readClass(ClinicalPatient.class, output));

            } else if (Settings.getMappingMethod().equals("none")) {

                // TODO

            }
        }

        // END CXX MAGIX

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
            if(readClass(c, output) instanceof ClinicalSample) {
                ClinicalSample cs = (ClinicalSample) readClass(c, output);
                if(cs.getSampleId() == null) {
                    return null;
                }
            }
            return readClass(c, output);
        }
        return null;
    }

    private static String getMapTarget(String source) throws IOException {
        if (stMap == null) {
            CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
            TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
            };
            ObjectReader or = om.readerFor(typeRef)
                    .with(CsvSchema.emptySchema().withHeader().withColumnSeparator('\t'));

            Iterator<Map<String, String>> inputIterator = or
                    .readValues(ReaderFactory.createBufferedReader(new File("mapper/mappingTable.csv")));
            stMap = new HashMap<String, String>();
            while (inputIterator.hasNext()) {
                Map<String, String> v = inputIterator.next();
                stMap.put(v.get("source"), v.get("target"));
            }
        }

        return stMap.get(source);
    }

    public static void prepare(File csv, CbioPortalStudy study) throws IOException {

        Map<String, Map<String, String>> pMap = new HashMap<String, Map<String, String>>();
        
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(new TypeReference<HashMap<String, String>>() {
        }).with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator(';'));

        Iterator<Map<String, String>> inputIterator = or.readValues(ReaderFactory.createBufferedReader(csv));
        List<Map<String, String>> l = new ArrayList<Map<String, String>>();
        while(inputIterator.hasNext()) {
            l.add(inputIterator.next());
        }
        for(Map<String, String> m : l) {
            if(pMap.containsKey(m.get("PID"))) {
                Map<String, String> n = pMap.get(m.get("PID"));
                if(Integer.parseInt(m.get("Jahr_Text")) < Integer.parseInt(n.get("Jahr_Text"))) {
                    if(Integer.parseInt(m.get("Monat_Text")) < Integer.parseInt(n.get("Monat_Text"))) {
                        pMap.put(m.get("PID"), m);
                    }
                }
            } else {
                pMap.put(m.get("PID"), m);
            }
        }

        for(Entry<String, Map<String, String>> e : pMap.entrySet()) {
            study.addPreparation(e.getKey(), e.getValue());
        }

    }

}
