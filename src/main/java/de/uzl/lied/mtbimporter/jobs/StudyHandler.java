package de.uzl.lied.mtbimporter.jobs;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.ClinicalSample;
import de.uzl.lied.mtbimporter.model.Maf;
import de.uzl.lied.mtbimporter.model.Timeline;
import de.uzl.lied.mtbimporter.settings.Settings;
import de.uzl.lied.mtbimporter.tasks.AddClinicalData;
import de.uzl.lied.mtbimporter.tasks.AddGeneticData;
import de.uzl.lied.mtbimporter.tasks.AddResourceData;
import de.uzl.lied.mtbimporter.tasks.AddSignatureData;
import de.uzl.lied.mtbimporter.tasks.AddTimelineData;

public class StudyHandler {
    
    public static CbioPortalStudy merge(CbioPortalStudy study, CbioPortalStudy... studies) throws JsonParseException, JsonMappingException, IOException {
        for(CbioPortalStudy newStudy : studies) {
            study = merge(study, newStudy);
        }
        return study;
    }

    public static CbioPortalStudy merge(CbioPortalStudy study, CbioPortalStudy newStudy) throws JsonParseException, JsonMappingException, IOException {
        study.addMaf(newStudy.getMaf());
        study.addSeg(newStudy.getSeg());
        study.addPatient(newStudy.getPatients());
        study.addSample(newStudy.getSamples());
        study.addCna(newStudy.getCna());
        study.addMutationalContribution(newStudy.getMutationalContribution());
        study.addMutationalLimit(newStudy.getMutationalLimit());
        study.addGenePanelMatrix(newStudy.getGenePanelMatrix());
        study.addSampleResource(newStudy.getSampleResources());
        
        return study;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void write(CbioPortalStudy study, Long state) throws JsonGenerationException, JsonMappingException, IOException {
        AddGeneticData.writeMafFile(study.getMaf(), new File(Settings.getStudyFolder() + state + "/data_mutation_extended.maf"));
        AddGeneticData.writeSegFile(study.getSeg(), new File(Settings.getStudyFolder() + state + "/data_cna.seg"));
        AddGeneticData.writeCnaFile(study.getCna(), new File(Settings.getStudyFolder() + state + "/data_cna.txt"));
        AddGeneticData.writeGenePanelFile(study.getGenePanelMatrix(), new File(Settings.getStudyFolder() + state + "/data_gene_panel_matrix.txt"));

        AddClinicalData.writeClinicalPatient(study.getPatients(), study.getPatientAttributes(), new File(Settings.getStudyFolder() + state + "/data_clinical_patient.txt"));
        AddClinicalData.writeClinicalSample(study.getSamples(), study.getSampleAttributes(), new File(Settings.getStudyFolder() + state + "/data_clinical_sample.txt"));

        AddResourceData.writeResourceFile(study.getSampleResources(), new File(Settings.getStudyFolder() + state + "/data_resource_sample.txt"));

        AddSignatureData.writeSignatureData(study.getMutationalLimit(),  new File(Settings.getStudyFolder() + state + "/data_mutational_signature_limit.txt"));
        AddSignatureData.writeSignatureData(study.getMutationalContribution(),  new File(Settings.getStudyFolder() + state + "/data_mutational_signature_contribution.txt"));

        Set<String> caseListSequenced = new HashSet<String>();
        for(Maf m : study.getMaf()) {
            caseListSequenced.add(m.getTumorSampleBarcode());
        }
        AddGeneticData.writeCaseList(new File(Settings.getStudyFolder() + state + "/case_lists/cases_sequenced.txt"), caseListSequenced);
        AddGeneticData.writeCaseList(new File(Settings.getStudyFolder() + state + "/case_lists/cases_cna.txt"), study.getCnaSampleIds());
        caseListSequenced.retainAll(study.getCnaSampleIds());
        AddGeneticData.writeCaseList(new File(Settings.getStudyFolder() + state + "/case_lists/cases_cnaseq.txt"), caseListSequenced);

        
        Set<String> caseListAll = new HashSet<String>();
        for(ClinicalSample s : study.getSamples()) {
            caseListAll.add(s.getSampleId());
        }
        AddGeneticData.writeCaseList(new File(Settings.getStudyFolder() + state + "/case_lists/cases_all.txt"), caseListAll);

        for(Entry<Class, List<Timeline>> e : study.getTimelines().entrySet()) {
            AddTimelineData.writeTimelineFile(e.getValue(), e.getKey(), new File(Settings.getStudyFolder() + state + "data_timeline_" + e.getKey().getSimpleName().toLowerCase().replaceFirst("Timeline", "")));
        }
    }

}
