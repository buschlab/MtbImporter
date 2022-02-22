package de.uzl.lied.mtbimporter.tasks;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.ColumnType;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.uzl.lied.mtbimporter.jobs.FhirResolver;
import de.uzl.lied.mtbimporter.jobs.mdr.centraxx.CxxMdrConvert;
import de.uzl.lied.mtbimporter.jobs.mdr.centraxx.CxxMdrItemSet;
import de.uzl.lied.mtbimporter.jobs.mdr.samply.SamplyMdrConvert;
import de.uzl.lied.mtbimporter.jobs.mdr.samply.SamplyMdrItems;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.ClinicalPatient;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.CxxItem;
import de.uzl.lied.mtbimporter.model.mdr.centraxx.RelationConvert;
import de.uzl.lied.mtbimporter.settings.CxxMdrSettings;
import de.uzl.lied.mtbimporter.settings.Mapping;
import de.uzl.lied.mtbimporter.settings.Mdr;
import de.uzl.lied.mtbimporter.settings.SamplyMdrSettings;
import de.uzl.lied.mtbimporter.settings.Settings;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import org.hl7.fhir.r4.model.Coding;
import org.mozilla.universalchardet.ReaderFactory;
import org.tinylog.Logger;

/**
 * Process files and objects from hospital information system.
 */
public final class AddHisData {

    private AddHisData() {
    }

    /**
     * Process a csv file from hospital information system.
     * @param study
     * @param csv
     * @throws IOException
     * @throws ExecutionException
     * @throws MdrConnectionException
     * @throws MdrInvalidResponseException
     */
    public static void processCsv(CbioPortalStudy study, File csv)
            throws IOException, ExecutionException, MdrConnectionException, MdrInvalidResponseException {

        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(new TypeReference<HashMap<String, String>>() {
        }).with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator(';').withNullValue(""));
        MappingIterator<Map<String, Object>> mi = or.readValues(ReaderFactory.createBufferedReader(csv));
        List<Map<String, Object>> l = mi.readAll();

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

                for (Mapping mapping : Settings.getMapping()) {
                    input.setSourceProfileCode(mapping.getSource());
                    input.setTargetProfileCode(mapping.getTarget());
                    Object o = cxxMap(mapping.getModelClass(), cxxMdr, input);
                    if (o instanceof ClinicalPatient
                            && Settings.getFhir().getTerminology() != null
                            && ((ClinicalPatient) o).getAdditionalAttributes().containsKey("ICD_O_3_SITE")
                            && ((ClinicalPatient) o).getAdditionalAttributes().containsKey("ICD_O_3_HISTOLOGY")) {
                        Coding oncoTree = FhirResolver.resolveOncoTree(
                                (String) ((ClinicalPatient) o).getAdditionalAttributes().get("ICD_O_3_SITE"),
                                (String) ((ClinicalPatient) o).getAdditionalAttributes().get("ICD_O_3_HISTOLOGY"));
                        ((ClinicalPatient) o).getAdditionalAttributes().put("ONCOTREE_CODE", oncoTree.getCode());
                        ((ClinicalPatient) o).getAdditionalAttributes().put("CANCER_TYPE", oncoTree.getDisplay());
                    }
                    study.add(o);
                }

            } else if (Settings.getMappingMethod().equals("groovy") && samplyMdr != null) {

                for (Mapping mapping : Settings.getMapping()) {
                    input.setSourceProfileCode(mapping.getSource());
                    input.setTargetProfileCode(mapping.getTarget());
                    study.add(samplyMap(mapping.getModelClass(), samplyMdr, input));
                }

                // } else if (Settings.getMappingMethod().equals("none")) {

                // // TODO

            }
        }

    }

    /**
     * Converts a relation for MDR to a specific jvm pojo object.
     * @param <T> Target pojo object
     * @param c Target jvm pojo class
     * @param relation Relation used as input
     * @return relation converted to an object of class c
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private static <T> T readClass(Class<T> c, RelationConvert relation) throws IOException {
        CsvMapper om = new CsvMapper().enable(CsvParser.Feature.ALLOW_COMMENTS);
        ObjectReader or = om.readerFor(c)
                .with(CsvSchema.emptySchema().withHeader().withComments().withColumnSeparator('\t'));
        CsvSchema s = CsvSchema.builder().addColumns(relation.getValues().keySet(), ColumnType.NUMBER_OR_STRING).build()
                .withHeader().withColumnSeparator('\t');
        String str = om.writer(s).writeValueAsString(relation.getValues());
        return (T) or.readValues(str).next();
    }

    private static <T> T cxxMap(Class<T> c, CxxMdrSettings mdr, RelationConvert input)
            throws IOException {
        List<CxxItem> inputItems = CxxMdrItemSet.getItemList(CxxMdrItemSet.get(mdr, input.getSourceProfileCode()));
        for (CxxItem inputItem : inputItems) {
            if (Boolean.TRUE.equals(inputItem.getMandatory()) && !input.getValues().containsKey(inputItem.getId())) {
                Logger.debug("Does not fulfil criteria for source " + input.getSourceProfileCode());
                return null;
            }
        }
        RelationConvert output = CxxMdrConvert.convert(mdr, input);
        List<CxxItem> outputItems = CxxMdrItemSet.getItemList(CxxMdrItemSet.get(mdr, input.getTargetProfileCode()));
        for (CxxItem outputItem : outputItems) {
            if (Boolean.TRUE.equals(outputItem.getMandatory()) && !output.getValues().containsKey(outputItem.getId())) {
                Logger.debug("Does not fulfil criteria for target " + input.getTargetProfileCode());
                return null;
            }
        }
        if (!output.getValues().isEmpty()) {
            Logger.info("Successfully mapped object from " + input.getSourceProfileCode() + " to "
                    + input.getTargetProfileCode());
            return readClass(c, output);
        }
        return null;
    }

    private static <T> T samplyMap(Class<T> c, SamplyMdrSettings mdr, RelationConvert input) throws ExecutionException,
            MdrConnectionException, MdrInvalidResponseException, IOException {
        Map<String, Map<String, String>> inputItems = SamplyMdrItems.get(mdr, mdr.getSourceNamespace(),
                input.getSourceProfileCode());
        if (inputItems == null) {
            Logger.debug("Does not fulfil criteria for source " + input.getSourceProfileCode());
            return null;
        }
        for (Entry<String, Map<String, String>> inputItem : inputItems.entrySet()) {
            if (inputItem.getValue().containsKey("mandatory") && inputItem.getValue().get("mandatory").equals("true")
                    && !input.getValues().containsKey(inputItem.getKey())) {
                Logger.debug("Does not fulfil criteria for source " + input.getSourceProfileCode());
                return null;
            }
        }
        RelationConvert output = SamplyMdrConvert.convert(mdr, input);
        Map<String, Map<String, String>> outputItems = SamplyMdrItems.get(mdr, mdr.getTargetNamespace(),
                input.getTargetProfileCode());
        if (outputItems == null) {
            Logger.debug("Does not fulfil criteria for target " + input.getTargetProfileCode());
            return null;
        }
        for (Entry<String, Map<String, String>> outputItem : outputItems.entrySet()) {
            if (outputItem.getValue().containsKey("mandatory") && outputItem.getValue().get("mandatory").equals("true")
                    && !output.getValues().containsKey(outputItem.getKey())) {
                Logger.debug("Does not fulfil criteria for target " + input.getTargetProfileCode());
                return null;
            }
        }
        if (!output.getValues().isEmpty()) {
            Logger.info("Successfully mapped object from " + input.getSourceProfileCode() + " to "
                    + input.getTargetProfileCode());
            return readClass(c, output);
        }
        return null;
    }

}
