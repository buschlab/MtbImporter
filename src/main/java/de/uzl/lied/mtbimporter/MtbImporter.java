package de.uzl.lied.mtbimporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;

import de.uzl.lied.mtbimporter.jobs.CheckDropzone;
import de.uzl.lied.mtbimporter.jobs.FhirResolver;
import de.uzl.lied.mtbimporter.jobs.mdr.centraxx.CxxMdrLogin;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.settings.ConfigurationLoader;
import de.uzl.lied.mtbimporter.settings.Mdr;
import de.uzl.lied.mtbimporter.settings.Settings;
import de.uzl.lied.mtbimporter.tasks.AddClinicalData;
import de.uzl.lied.mtbimporter.tasks.AddGeneticData;
import de.uzl.lied.mtbimporter.tasks.AddResourceData;
import de.uzl.lied.mtbimporter.tasks.AddSignatureData;
import de.uzl.lied.mtbimporter.tasks.AddTimelineData;

public final class MtbImporter {
    /**
     * 
     * @param args The arguments of the program.
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        InputStream settingsYaml = ClassLoader.getSystemClassLoader().getResourceAsStream("settings.yaml");
        if (args.length == 1) {
            settingsYaml = new FileInputStream(args[0]);
        }

        ConfigurationLoader configLoader = new ConfigurationLoader();
        configLoader.loadConfiguration(settingsYaml, Settings.class);

        FhirResolver.initalize();
        for (Mdr m : Settings.getMdr()) {
            if(m.getCxx() != null) {
                CxxMdrLogin.login(m.getCxx());
            }
        }

        CbioPortalStudy study = new CbioPortalStudy();
        AddGeneticData.processMafFile(study, new File(Settings.getStudyFolder() + Settings.getState() + "/data_mutation_extended.maf"));
        AddGeneticData.processSegFile(study, new File(Settings.getStudyFolder() + Settings.getState() + "/data_cna.seg"));
        AddGeneticData.processGenePanelFile(study, new File(Settings.getStudyFolder() + Settings.getState() + "/data_gene_panel_matrix.txt"));
        AddClinicalData.processClinicalPatient(study, new File(Settings.getStudyFolder() + Settings.getState() + "/data_clinical_patient.txt"));
        AddClinicalData.processClinicalSample(study, new File(Settings.getStudyFolder() + Settings.getState() + "/data_clinical_sample.txt"));
        study.setSampleResources(AddResourceData.readResourceFile(new File(Settings.getStudyFolder() + Settings.getState() + "/data_resource_sample.txt")));
        AddSignatureData.processContribution(study, new File(Settings.getStudyFolder() + Settings.getState() + "/data_mutational_signature_contribution.txt"));
        AddSignatureData.processLimit(study, new File(Settings.getStudyFolder() + Settings.getState() + "/data_mutational_signature_limit.txt"));
        AddGeneticData.processCnaFile(study, new File(Settings.getStudyFolder() + Settings.getState() + "/data_cna.txt"));
        File[] timelines = new File(Settings.getStudyFolder() + Settings.getState()).listFiles((File f) -> f.getName().startsWith("data_timeline"));
        for(File t : timelines) {
            AddTimelineData.processTimelineFile(study, t);
        }

        Timer t = new Timer();
        CheckDropzone checkDropzone = new CheckDropzone(study);
        t.scheduleAtFixedRate(checkDropzone, 0, Settings.getCronIntervall());

        System.out.println("MtbImporter started!");
    }
}