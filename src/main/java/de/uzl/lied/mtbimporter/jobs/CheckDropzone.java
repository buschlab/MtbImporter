package de.uzl.lied.mtbimporter.jobs;

import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.ClinicalPatient;
import de.uzl.lied.mtbimporter.settings.InputFolder;
import de.uzl.lied.mtbimporter.settings.Settings;
import de.uzl.lied.mtbimporter.tasks.AddGeneticData;
import de.uzl.lied.mtbimporter.tasks.AddHisData;
import de.uzl.lied.mtbimporter.tasks.AddMetaData;
import de.uzl.lied.mtbimporter.tasks.AddResourceData;
import de.uzl.lied.mtbimporter.tasks.AddSignatureData;
import de.uzl.lied.mtbimporter.tasks.ImportStudy;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.tinylog.Logger;

/**
 * Class providing the cronjob for scheduled data processing.
 */
public class CheckDropzone extends TimerTask {

    private CbioPortalStudy study;

    public CheckDropzone(CbioPortalStudy study) {
        this.study = study;
    }

    @Override
    @SuppressWarnings({"checkstyle:IllegalCatch"})
    public void run() {

        Long newState = System.currentTimeMillis();
        int count = 0;
        CbioPortalStudy newStudy = new CbioPortalStudy();
        newStudy.setStudyId(study.getStudyId());

        Logger.info("Checking for files!");
        for (InputFolder inputfolder : Settings.getInputFolders()) {
            File[] files = new File(inputfolder.getSource()).listFiles();

            if (files.length == 0 || files.length == 1 && files[0].getName().equals(".gitkeep")) {
                Logger.info("No new files at " + inputfolder.getSource() + ".");
                continue;
            } else {
                count++;
            }

            for (File f : files) {
                if (f.getName().equals(".gitkeep")) {
                    continue;
                }
                Logger.info("Found " + f.getAbsolutePath());
                try {
                    Binding b = new Binding();
                    GroovyShell s = new GroovyShell(b);
                    b.setVariable("csv", f);
                    b.setVariable("study", newStudy);
                    s.evaluate(new File("mapper/prepare.groovy"));
                } catch (IOException e) {
                    Logger.error("Could not access file mapper/prepare.groovy");
                    Logger.debug(e);
                } catch (CompilationFailedException e) {
                    Logger.error("Could not compile Groovy script mapper/prepare.groovy");
                    Logger.debug(e);
                } catch (Exception e) {
                    Logger.error("Error executing Groovy script mapper/prepare.groovy");
                    Logger.debug(e);
                }
                try {
                    switch (FilenameUtils.getExtension(f.getName())) {
                        case "maf":
                            AddGeneticData.processMafFile(newStudy, f);
                            break;
                        case "txt":
                            switch (determineDatatype(f)) {
                                case "cna":
                                    AddGeneticData.processCnaFile(newStudy, f);
                                    break;
                                case "mutsigLimit":
                                    AddSignatureData.processLimit(newStudy, f);
                                    break;
                                case "mutsigContribution":
                                    AddSignatureData.processContribution(newStudy, f);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case "seg":
                            AddGeneticData.processSegFile(newStudy, f);
                            break;
                        case "pdf":
                            AddResourceData.processPdfFile(newStudy, f);
                            break;
                        case "RData":
                            AddMetaData.processRData(newStudy, f);
                            break;
                        case "csv":
                            AddHisData.processCsv(newStudy, f);
                            break;
                        default:
                            break;
                    }

                    if (inputfolder.getTarget() == null || inputfolder.getTarget().length() == 0) {
                        Files.delete(f.toPath());
                    } else {
                        File folder = new File(inputfolder.getTarget(), Long.toString(newState));
                        folder.mkdirs();
                        Files.move(f.toPath(),
                                new File(folder, f.getName()).toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                    Logger.info("Processed file " + f.getAbsolutePath());
                } catch (IOException | ExecutionException | MdrConnectionException | MdrInvalidResponseException e) {
                    Logger.error("Could not process file " + f.getAbsolutePath());
                    Logger.debug("Received following exception:", e);
                }
            }
        }

        if (count > 0) {
            try {
                File folder = new File(Settings.getStudyFolder(), study.getStudyId());
                FileUtils.copyDirectory(
                        new File(folder, Long.toString(study.getState())),
                        new File(Long.toString(newState)));

                StudyHandler.merge(study, newStudy);
                StudyHandler.write(study, newState);
                ImportStudy.importStudy(study.getStudyId(), newState, Settings.getOverrideWarnings());
                study.setState(newState);

                Map<String, List<String>> patientsByDate = new HashMap<>();
                Map<String, CbioPortalStudy> patientStudy = new HashMap<>();
                for (ClinicalPatient patient : newStudy.getPatients()) {
                    if (patient.getAdditionalAttributes().containsKey("PRESENTATION_DATE")) {
                        List<String> al = patientsByDate.getOrDefault(
                                (String) patient.getAdditionalAttributes().get("PRESENTATION_DATE"),
                                new ArrayList<>());
                        al.add(patient.getPatientId());
                        patientsByDate.put((String) patient.getAdditionalAttributes().get("PRESENTATION_DATE"), al);
                        patientStudy.put(patient.getPatientId(),
                                StudyHandler.getPatientStudy(study, patient.getPatientId()));
                    }
                }
                for (Entry<String, List<String>> e : patientsByDate.entrySet()) {
                    CbioPortalStudy s = StudyHandler.load(study.getStudyId() + "_" + e.getKey());
                    FileUtils.copyDirectory(new File(Settings.getStudyFolder(), s.getStudyId() + "/" + s.getState()),
                            new File(Settings.getStudyFolder(), s.getStudyId() + "/" + newState));
                    s.getMetaFile("meta_study.txt").setAdditionalAttributes("name", e.getKey() + " "
                            + study.getMetaFile("meta_study.txt").getAdditionalAttributes().get("name"));
                    s.getMetaFile("meta_study.txt").setAdditionalAttributes("short_name", e.getKey() + " "
                            + study.getMetaFile("meta_study.txt").getAdditionalAttributes().get("short_name"));
                    s.getMetaFile("meta_study.txt").setAdditionalAttributes("description", e.getKey() + " "
                            + study.getMetaFile("meta_study.txt").getAdditionalAttributes().get("description"));
                    for (String patient : e.getValue()) {
                        StudyHandler.merge(s, patientStudy.get(patient));
                    }
                    StudyHandler.write(s, newState);
                    s.setState(newState);
                    ImportStudy.importStudy(s.getStudyId(), newState, Settings.getOverrideWarnings());
                }
            } catch (IOException e) {
                Logger.error("Importing study failed with timestamp", newState);
                Logger.debug(e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private String determineDatatype(File f) throws FileNotFoundException {

        Scanner scanner = new Scanner(f);
        String header = "";
        String content = "";
        if (scanner.hasNextLine()) {
            header = scanner.nextLine();
        }
        if (scanner.hasNextLine()) {
            content = scanner.nextLine();
        }
        scanner.close();
        if (header.contains("Entrez_Gene_Id") && header.contains("Hugo_Symbol")) {
            return "cna";
        }
        if (header.contains("ENTITY_STABLE_ID") && header.contains("NAME") && header.contains("DESCRIPTION")) {
            if (content.contains("contribution")) {
                return "mutsigContribution";
            }
            if (content.contains("limit")) {
                return "mutsigLimit";
            }
        }

        return "";

    }

}
