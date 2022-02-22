package de.uzl.lied.mtbimporter.tasks;

import de.uzl.lied.mtbimporter.jobs.FhirResolver;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.ClinicalSample;
import de.uzl.lied.mtbimporter.model.GenePanelMatrix;
import java.io.File;
import java.io.IOException;

/**
 * Adds meta data from R data frame to study.
 */
public final class AddMetaData {

    private static final String[] RSCRIPT = {"Rscript", "--vanilla", "-e", "replaceme"};
    private static final int CATCOMMAND = 3;

    private AddMetaData() {
    }

    /**
     * Add meta data to study.
     * @param study Study where the metadata will be added
     * @param rData File with R data frame
     * @throws IOException
     */
    public static void processRData(CbioPortalStudy study, File rData) throws IOException {
        String load = "options(warn = -1); load(\"" + rData.getAbsolutePath() + "\"); ";

        String getSampleId = load + "cat(id);";
        String getTmb = load + "cat(round(x = filt_result_td$tmb, digits = 2));";
        String getMsiStatus = load + "cat(as.character(filt_result_td$msi$result$MSI_status));";
        String getMsiScore = load + "cat(as.character(filt_result_td$msi$scores$ratio*100));";
        String getPanel = load + "cat(sureselect_type);";
        String getProtocol = load + "cat(protocol);";
        String getCoveredRegion = load + "cat(covered_region);";

        String sampleId = runRscriptCommand(getSampleId);
        String patientId = FhirResolver.resolvePatientFromSample(sampleId);
        String protocol = runRscriptCommand(getProtocol);
        String panel = runRscriptCommand(getPanel);
        String tmb = runRscriptCommand(getTmb);
        String msiScore = runRscriptCommand(getMsiScore);
        String msiStatus = runRscriptCommand(getMsiStatus);
        if (msiStatus.isEmpty()) {
            msiStatus = "NA";
        } else if ("Non-MSI-H".equals(msiStatus)) {
            msiStatus = "Stable";
        } else if ("MSI-H".equals(msiStatus)) {
            msiStatus = "Instable";
        }
        String coveredRegion = runRscriptCommand(getCoveredRegion);

        AddClinicalData.addDummyPatient(study, sampleId);
        ClinicalSample cs = new ClinicalSample();
        cs.setPatientId(patientId);
        cs.setSampleId(sampleId);
        cs.setAdditionalAttributes("CVR_TMB_SCORE", tmb);
        cs.setAdditionalAttributes("MSI_SCORE", msiScore);
        cs.setAdditionalAttributes("MSI_TYPE", msiStatus);
        cs.setAdditionalAttributes("ANALYSIS_TYPE", protocol.startsWith("somatic") ? "WES" : protocol);
        cs.setAdditionalAttributes("COVERED_BASES", coveredRegion);

        study.addSample(cs);
        study.addGenePanelMatrix(new GenePanelMatrix(sampleId, panel));
    }

    private static String runRscriptCommand(String s) throws IOException {
        RSCRIPT[CATCOMMAND] = s;
        ProcessBuilder pb = new ProcessBuilder(RSCRIPT);
        pb.environment().put("PATH", System.getenv("PATH"));
        final Process process = pb.start();
        String result = new String(process.getInputStream().readAllBytes());
        return result;
    }

}
