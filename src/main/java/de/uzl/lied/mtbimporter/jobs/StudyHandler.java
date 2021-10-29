package de.uzl.lied.mtbimporter.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.io.ByteStreams;

import org.apache.commons.io.FileUtils;

import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.ClinicalSample;
import de.uzl.lied.mtbimporter.model.Maf;
import de.uzl.lied.mtbimporter.model.Meta;
import de.uzl.lied.mtbimporter.model.Timeline;
import de.uzl.lied.mtbimporter.settings.Settings;
import de.uzl.lied.mtbimporter.tasks.AddClinicalData;
import de.uzl.lied.mtbimporter.tasks.AddGeneticData;
import de.uzl.lied.mtbimporter.tasks.AddMetaFile;
import de.uzl.lied.mtbimporter.tasks.AddResourceData;
import de.uzl.lied.mtbimporter.tasks.AddSignatureData;
import de.uzl.lied.mtbimporter.tasks.AddTimelineData;

public class StudyHandler {

    public static CbioPortalStudy load(String studyId) throws FileNotFoundException, IOException {
        CbioPortalStudy study = new CbioPortalStudy();
        File stateFile = new File(Settings.getStudyFolder() + studyId + "/.state");
        study.setStudyId(studyId);
        if (!stateFile.exists()) {
            Long s = System.currentTimeMillis();
            FileUtils.copyDirectory(new File(Settings.getStudyTemplate()),
                    new File(Settings.getStudyFolder() + study.getStudyId() + "/" + s));
            study.setState(s);
        }
        InputStream stateStream = new FileInputStream(stateFile);
        long state = Long.parseLong(new String(ByteStreams.toByteArray(stateStream)));
        AddGeneticData.processMafFile(study, new File(Settings.getStudyFolder() + studyId + "/" + state + "/data_mutation_extended.maf"));
        AddGeneticData.processSegFile(study, new File(Settings.getStudyFolder() + studyId + "/" + state + "/data_cna.seg"));
        AddGeneticData.processGenePanelFile(study, new File(Settings.getStudyFolder() + studyId + "/" + state + "/data_gene_panel_matrix.txt"));
        AddClinicalData.processClinicalPatient(study, new File(Settings.getStudyFolder() + studyId + "/" + state + "/data_clinical_patient.txt"));
        AddClinicalData.processClinicalSample(study, new File(Settings.getStudyFolder() + studyId + "/" + state + "/data_clinical_sample.txt"));
        study.setSampleResources(AddResourceData.readResourceFile(new File(Settings.getStudyFolder() + studyId + "/" + state + "/data_resource_sample.txt")));
        AddSignatureData.processContribution(study, new File(Settings.getStudyFolder() + studyId + "/" + state + "/data_mutational_signature_contribution.txt"));
        AddSignatureData.processLimit(study, new File(Settings.getStudyFolder() + studyId + "/" + state + "/data_mutational_signature_limit.txt"));
        AddGeneticData.processCnaFile(study, new File(Settings.getStudyFolder() + studyId + "/" + state + "/data_cna.txt"));
        File[] timelines = new File(Settings.getStudyFolder() + studyId + "/" + state).listFiles((File f) -> f.getName().startsWith("data_timeline"));
        File[] metaFiles = new File(Settings.getStudyFolder() + studyId + "/" + state).listFiles((File f) -> f.getName().startsWith("meta_"));
        for(File t : timelines) {
            AddTimelineData.processTimelineFile(study, t);
        }
        for(File m : metaFiles) {
            AddMetaFile.processMetaFile(study, m);
        }
        return study;
    }
    
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
        for(Entry<Class, List<Timeline>> t : newStudy.getTimelines().entrySet()) {
            study.addTimeline(t.getKey(), t.getValue());
        }
        
        return study;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void write(CbioPortalStudy study, Long state) throws JsonGenerationException, JsonMappingException, IOException {
        AddGeneticData.writeMafFile(study.getMaf(), new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_mutation_extended.maf"));
        AddGeneticData.writeSegFile(study.getSeg(), new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_cna.seg"));
        AddGeneticData.writeCnaFile(study.getCna(), new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_cna.txt"));
        AddGeneticData.writeGenePanelFile(study.getGenePanelMatrix(), new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_gene_panel_matrix.txt"));

        AddResourceData.writeResourceFile(study.getSampleResources(), new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_resource_sample.txt"));

        AddSignatureData.writeSignatureData(study.getMutationalLimit(),  new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_mutational_signature_limit.txt"));
        AddSignatureData.writeSignatureData(study.getMutationalContribution(),  new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_mutational_signature_contribution.txt"));

        Set<String> caseListSequenced = new HashSet<String>();
        for(Maf m : study.getMaf()) {
            caseListSequenced.add(m.getTumorSampleBarcode());
        }
        AddGeneticData.writeCaseList(new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/case_lists/cases_sequenced.txt"), study.getStudyId(), caseListSequenced);
        AddGeneticData.writeCaseList(new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/case_lists/cases_cna.txt"), study.getStudyId(), study.getCnaSampleIds());
        caseListSequenced.retainAll(study.getCnaSampleIds());
        AddGeneticData.writeCaseList(new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/case_lists/cases_cnaseq.txt"), study.getStudyId(), caseListSequenced);

        
        Set<String> caseListAll = new HashSet<String>();
        for(ClinicalSample s : study.getSamples()) {
            caseListAll.add(s.getSampleId());
        }
        AddGeneticData.writeCaseList(new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/case_lists/cases_all.txt"), study.getStudyId(), caseListAll);

        for(Entry<Class, List<Timeline>> e : study.getTimelines().entrySet()) {
            AddTimelineData.writeTimelineFile(e.getValue(), e.getKey(), new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_timeline_" + e.getKey().getSimpleName().toLowerCase().replaceFirst("Timeline", "") + ".txt"));
        }

        for(Entry<String, Meta> e : study.getMetaFiles().entrySet()) {
            AddMetaFile.writeMetaFile(e.getValue(), new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/" + e.getKey()));
        }

        AddClinicalData.writeClinicalPatient(study.getPatients(), study.getPatientAttributes(), new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_clinical_patient.txt"));
        AddClinicalData.writeClinicalSample(study.getSamples(), study.getSampleAttributes(), new File(Settings.getStudyFolder() + study.getStudyId() + "/" + state + "/data_clinical_sample.txt"));
    }

    public static CbioPortalStudy getPatientStudy(CbioPortalStudy study, String patientId) {
        CbioPortalStudy patientStudy = new CbioPortalStudy();

        patientStudy.addPatient(study.getPatient(patientId));
        patientStudy.addSample(study.getSamplesByPatient(patientId));

        return patientStudy;
    }

}
