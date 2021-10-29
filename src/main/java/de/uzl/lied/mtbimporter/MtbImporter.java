package de.uzl.lied.mtbimporter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;

import de.uzl.lied.mtbimporter.jobs.CheckDropzone;
import de.uzl.lied.mtbimporter.jobs.FhirResolver;
import de.uzl.lied.mtbimporter.jobs.StudyHandler;
import de.uzl.lied.mtbimporter.jobs.mdr.centraxx.CxxMdrLogin;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.settings.ConfigurationLoader;
import de.uzl.lied.mtbimporter.settings.Mdr;
import de.uzl.lied.mtbimporter.settings.Settings;

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

        CbioPortalStudy study = StudyHandler.load(Settings.getMainStudyId());

        Timer t = new Timer();
        CheckDropzone checkDropzone = new CheckDropzone(study);
        t.scheduleAtFixedRate(checkDropzone, 0, Settings.getCronIntervall());

        System.out.println("MtbImporter started!");
    }
}