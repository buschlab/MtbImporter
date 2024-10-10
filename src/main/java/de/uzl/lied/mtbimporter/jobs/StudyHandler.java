package de.uzl.lied.mtbimporter.jobs;

import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.ClinicalSample;
import de.uzl.lied.mtbimporter.model.Cna;
import de.uzl.lied.mtbimporter.model.Maf;
import de.uzl.lied.mtbimporter.model.Meta;
import de.uzl.lied.mtbimporter.model.MutationalSignature;
import de.uzl.lied.mtbimporter.model.Timeline;
import de.uzl.lied.mtbimporter.settings.Settings;
import de.uzl.lied.mtbimporter.tasks.AddClinicalData;
import de.uzl.lied.mtbimporter.tasks.AddGeneticData;
import de.uzl.lied.mtbimporter.tasks.AddMetaFile;
import de.uzl.lied.mtbimporter.tasks.AddResourceData;
import de.uzl.lied.mtbimporter.tasks.AddSignatureData;
import de.uzl.lied.mtbimporter.tasks.AddTimelineData;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.io.FileUtils;

/**
 * Class for handling operations on study objects.
 */
public final class StudyHandler {

    private StudyHandler() {
    }

    /**
     * Load a study from files into memory.
     * @param studyId id of the study that will be loaded
     * @return corresponding jvm object of the study
     * @throws IOException
     */
    public static CbioPortalStudy load(String studyId) throws IOException {
        CbioPortalStudy study = new CbioPortalStudy();
        Path stateFile = Path.of(Settings.getStudyFolder().getAbsolutePath(), studyId, ".state");
        study.setStudyId(studyId);
        if (!stateFile.toFile().exists()) {
            FileUtils.copyDirectory(new File(Settings.getStudyTemplate()),
                    new File(Settings.getStudyFolder(), study.getStudyId() + "/0"));
            study.setState(0L);
        }
        long state = Long.parseLong(Files.lines(stateFile).findFirst().get());
        study.setState(state);
        AddGeneticData.processMafFile(study,
                new File(Settings.getStudyFolder(), studyId + "/" + state + "/data_mutation_extended.maf"));
        AddGeneticData.processSegFile(study,
                new File(Settings.getStudyFolder(), studyId + "/" + state + "/data_cna.seg"));
        File gpm = new File(Settings.getStudyFolder(), studyId + "/" + state + "/data_gene_panel_matrix.txt");
        if (!gpm.exists()) {
            Files.copy(new File(Settings.getStudyTemplate(), "data_gene_panel_matrix.txt").toPath(), gpm.toPath());
            Files.copy(new File(Settings.getStudyTemplate(), "meta_gene_panel_matrix.txt").toPath(),
                    new File(Settings.getStudyFolder(),
                            studyId + "/" + state + "/meta_gene_panel_matrix.txt").toPath());
        }
        AddGeneticData.processGenePanelFile(study, gpm);

        AddClinicalData.processClinicalPatient(study,
                new File(Settings.getStudyFolder(), studyId + "/" + state + "/data_clinical_patient.txt"));
        AddClinicalData.processClinicalSample(study,
                new File(Settings.getStudyFolder(), studyId + "/" + state + "/data_clinical_sample.txt"));
        File sampleResource = new File(Settings.getStudyFolder(), studyId + "/" + state + "/data_resource_sample.txt");
        if (!sampleResource.exists()) {
            Files.copy(new File(Settings.getStudyTemplate(), "data_resource_sample.txt").toPath(),
                    sampleResource.toPath());
            Files.copy(new File(Settings.getStudyTemplate(), "meta_resource_sample.txt").toPath(),
                    new File(Settings.getStudyFolder(), studyId + "/" + state + "/meta_resource_sample.txt").toPath());
        }
        study.setSampleResources(AddResourceData.readResourceFile(sampleResource));
        File mutCon = new File(
                Settings.getStudyFolder(), studyId + "/" + state + "/data_mutational_signature_contribution.txt");
        if (!mutCon.exists()) {
            Files.copy(new File(Settings.getStudyTemplate(), "data_mutational_signature_contribution.txt").toPath(),
                    mutCon.toPath());
            Files.copy(new File(Settings.getStudyTemplate(), "meta_mutational_signature_contribution.txt").toPath(),
                    new File(Settings.getStudyFolder(),
                            studyId + "/" + state + "/meta_mutational_signature_contribution.txt").toPath());
        }
        AddSignatureData.processContribution(study, mutCon);
        File mutLimit = new File(
                Settings.getStudyFolder(), studyId + "/" + state + "/data_mutational_signature_limit.txt");
        if (!mutLimit.exists()) {
            Files.copy(new File(Settings.getStudyTemplate(), "data_mutational_signature_limit.txt").toPath(),
                    mutLimit.toPath());
            Files.copy(new File(Settings.getStudyTemplate(), "meta_mutational_signature_limit.txt").toPath(),
                    new File(Settings.getStudyFolder(),
                            studyId + "/" + state + "/meta_mutational_signature_limit.txt").toPath());
        }
        AddSignatureData.processLimit(study, mutLimit);

        File cnas = new File(Settings.getStudyFolder(), studyId + "/" + state + "/data_cna.txt");
        if (!cnas.exists()) {
            Files.copy(new File(Settings.getStudyTemplate(), "data_cna.txt").toPath(), cnas.toPath());
            Files.copy(new File(Settings.getStudyTemplate(), "meta_cna.txt").toPath(),
                    new File(Settings.getStudyFolder(), studyId + "/" + state + "/meta_cna.txt").toPath());
        }
        AddGeneticData.processCnaFile(study, cnas);
        File[] timelines = new File(Settings.getStudyFolder(), studyId + "/" + state)
                .listFiles((File f) -> f.getName().startsWith("data_timeline"));
        File[] metaFiles = new File(Settings.getStudyFolder(), studyId + "/" + state)
                .listFiles((File f) -> f.getName().startsWith("meta_"));
        for (File t : timelines) {
            AddTimelineData.processTimelineFile(study, t);
        }
        for (File m : metaFiles) {
            AddMetaFile.processMetaFile(study, m);
        }
        return study;
    }

    /**
     * Merge two study objects into one.
     * Merging multiple study objects.
     * @param study old study where the new studies will be merged into
     * @param newStudy the new study that will be merged into the other
     * @return the updated study object
     */
    public static CbioPortalStudy merge(CbioPortalStudy study, CbioPortalStudy newStudy) {
        study.addMaf(newStudy.getMaf());
        study.addSeg(newStudy.getSeg());
        study.addPatient(newStudy.getPatients());
        study.addSample(newStudy.getSamples());
        study.addCna(newStudy.getCna());
        study.addMutationalContribution(newStudy.getMutationalContribution());
        study.addMutationalLimit(newStudy.getMutationalLimit());
        study.addGenePanelMatrix(newStudy.getGenePanelMatrix());
        study.addSampleResource(newStudy.getSampleResources());
        for (Entry<String, List<Timeline>> t : newStudy.getTimelines().entrySet()) {
            study.addTimeline(t.getKey(), t.getValue());
        }

        return study;
    }

    /**
     * Writes a study to disk.
     * @param study study that is being written to disk
     * @param state state that will be assined to the study and its path on disk
     * @throws IOException
     */
    public static void write(CbioPortalStudy study, Long state) throws IOException {
        AddGeneticData.writeMafFile(study.getMaf(),
                new File(Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/data_mutation_extended.maf"));
        AddGeneticData.writeSegFile(study.getSeg(),
                new File(Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/data_cna.seg"));
        File cnas = new File(Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/data_cna.txt");
        AddGeneticData.writeCnaFile(study.getCna(), cnas);
        boolean empty = true;
        for (Cna c : study.getCna()) {
            empty = empty && c.getSamples().isEmpty();
        }
        if (empty) {
            Files.delete(cnas.toPath());
            study.getMetaFiles().remove("meta_cna.txt");
            Files.delete(new File(Settings.getStudyFolder(),
                    study.getStudyId() + "/" + state + "/meta_cna.txt").toPath());
        }
        File gpm = new File(
                Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/data_gene_panel_matrix.txt");
        AddGeneticData.writeGenePanelFile(study.getGenePanelMatrix(), gpm);
        if (study.getGenePanelMatrix().isEmpty()) {
            Files.delete(gpm.toPath());
            study.getMetaFiles().remove("meta_gene_panel_matrix.txt");
            Files.delete(new File(
                Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/meta_gene_panel_matrix.txt").toPath());
        }
        File resourceSample = new File(
                    Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/data_resource_sample.txt");
        AddResourceData.writeResourceFile(study.getSampleResources(), resourceSample);
        if (study.getSampleResources().isEmpty()) {
            Files.delete(resourceSample.toPath());
            study.getMetaFiles().remove("meta_resource_sample.txt");
            Files.delete(new File(Settings.getStudyFolder(),
                    study.getStudyId() + "/" + state + "/meta_resource_sample.txt").toPath());
        }
        File mutLimit = new File(
                Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/data_mutational_signature_limit.txt");
        File mutCon = new File(Settings.getStudyFolder(),
                study.getStudyId() + "/" + state + "/data_mutational_signature_contribution.txt");
        AddSignatureData.writeSignatureData(study.getMutationalLimit(), mutLimit);
        AddSignatureData.writeSignatureData(study.getMutationalContribution(), mutCon);
        empty = true;
        for (MutationalSignature m : study.getMutationalLimit()) {
            empty = empty && m.getSamples().isEmpty();
        }
        if (empty) {
            Files.delete(mutLimit.toPath());
            study.getMetaFiles().remove("meta_mutational_signature_limit.txt");
            Files.delete(new File(Settings.getStudyFolder(),
                    study.getStudyId() + "/" + state + "/meta_mutational_signature_limit.txt").toPath());
        }
        empty = true;
        for (MutationalSignature m : study.getMutationalContribution()) {
            empty = empty && m.getSamples().isEmpty();
        }
        if (empty) {
            Files.delete(mutCon.toPath());
            study.getMetaFiles().remove("meta_mutational_signature_contribution.txt");
            Files.delete(new File(Settings.getStudyFolder(),
                    study.getStudyId() + "/" + state + "/meta_mutational_signature_contribution.txt").toPath());
        }

        Set<String> caseListAll = new HashSet<>();
        for (ClinicalSample s : study.getSamples()) {
            caseListAll.add(s.getSampleId());
        }
        AddGeneticData.writeCaseList(
                new File(Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/case_lists/cases_all.txt"),
                study.getStudyId(), caseListAll);

        Set<String> caseListSequenced = new HashSet<>();
        for (Maf m : study.getMaf()) {
            caseListSequenced.add(m.getTumorSampleBarcode());
        }
        if (caseListSequenced.isEmpty()) {
            caseListSequenced = caseListAll;
        }
        File seqCases = new File(
            Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/case_lists/cases_sequenced.txt");
        AddGeneticData.writeCaseList(seqCases, study.getStudyId(), caseListSequenced);
        File cnaCases = new File(
                Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/case_lists/cases_cna.txt");
        AddGeneticData.writeCaseList(cnaCases, study.getStudyId(), study.getCnaSampleIds());
        if (study.getCnaSampleIds().isEmpty()) {
            Files.delete(cnaCases.toPath());
        }
        caseListSequenced.retainAll(study.getCnaSampleIds());
        File cnaSeqCases = new File(
                    Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/case_lists/cases_cnaseq.txt");
        AddGeneticData.writeCaseList(cnaSeqCases, study.getStudyId(), caseListSequenced);
        if (caseListSequenced.isEmpty()) {
            Files.delete(cnaSeqCases.toPath());
        }

        for (Entry<String, List<Timeline>> e : study.getTimelines().entrySet()) {
            AddTimelineData.writeTimelineFile(e.getValue(), e.getKey(), new File(Settings.getStudyFolder(),
                    study.getStudyId() + "/" + state + "/data_timeline_" + e.getKey() + ".txt"));
        }

        study.getMaf().forEach(m -> study.getMetaFile("meta_mutations_extended.txt")
                .addNamespace(m.getAdditionalProperties()));

        for (Entry<String, Meta> e : study.getMetaFiles().entrySet()) {
            AddMetaFile.writeMetaFile(e.getValue(),
                    new File(Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/" + e.getKey()));
        }

        AddClinicalData.writeClinicalPatient(study.getPatients(), study.getPatientAttributes(),
                new File(Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/data_clinical_patient.txt"));
        AddClinicalData.writeClinicalSample(study.getSamples(), study.getSampleAttributes(),
                new File(Settings.getStudyFolder(), study.getStudyId() + "/" + state + "/data_clinical_sample.txt"));
    }

    /**
     * Get a study containing only the data of a single patient.
     * @param study study where the data shall be extraced from
     * @param patientId id of the patient that is requested
     * @return CbioPortalStudy object containing only the data of the patied with the provided id
     */
    public static CbioPortalStudy getPatientStudy(CbioPortalStudy study, String patientId) {
        CbioPortalStudy patientStudy = new CbioPortalStudy();

        patientStudy.addPatient(study.getPatient(patientId));
        patientStudy.addSample(study.getSamplesByPatient(patientId));
        for (ClinicalSample s : patientStudy.getSamples()) {
            patientStudy.addSeg(study.getSegBySampleId(s.getSampleId()));
            patientStudy.addSampleResource(study.getSampleResourcesBySampleId(s.getSampleId()));
            patientStudy.addGenePanelMatrix(study.getGenePanelMatrixBySampleId(s.getSampleId()));
            patientStudy.addMaf(study.getMafBySampleId(s.getSampleId()));

            patientStudy.addCna(study.getCnaBySampleId(s.getSampleId()));
            patientStudy.addMutationalContribution(study.getMutationalContributionBySampleId(s.getSampleId()));
            patientStudy.addMutationalLimit(study.getMutationalLimitBySampleId(s.getSampleId()));
        }
        patientStudy.setTimelines(study.getTimelinesByPatient(patientId));

        return patientStudy;
    }

}
