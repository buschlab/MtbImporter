package de.uzl.lied.mtbimporter.tasks;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.uzl.lied.mtbimporter.jobs.FhirResolver;
import de.uzl.lied.mtbimporter.model.CbioPortalStudy;
import de.uzl.lied.mtbimporter.model.ClinicalSample;
import de.uzl.lied.mtbimporter.model.GenePanelMatrix;

public class AddMetaData {

    public static void processRData(CbioPortalStudy study, File rData)
            throws JsonParseException, JsonMappingException, IOException {
        String[] getPatientId = { "Rscript", "--vanilla", "-e",
                "options(warn = -1); load(\"" + rData.getAbsolutePath() + "\"); cat(id);" };
        String[] getTmb = { "Rscript", "--vanilla", "-e", "options(warn = -1); load(\"" + rData.getAbsolutePath()
                + "\"); cat(round(x = filt_result_td$tmb, digits = 2));" };
        String[] getMsiStatus = { "Rscript", "--vanilla", "-e", "options(warn = -1); load(\"" + rData.getAbsolutePath()
                + "\"); cat(as.character(filt_result_td$msi$result$MSI_status));" };
        String[] getMsiScore = { "Rscript", "--vanilla", "-e", "options(warn = -1); load(\"" + rData.getAbsolutePath()
                + "\"); cat(as.character(filt_result_td$msi$scores$ratio*100));" };
        String[] getPanel = { "Rscript", "--vanilla", "-e",
                "options(warn = -1); load(\"" + rData.getAbsolutePath() + "\"); cat(sureselect_type);" };
        String[] getProtocol = { "Rscript", "--vanilla", "-e",
                "options(warn = -1); load(\"" + rData.getAbsolutePath() + "\"); cat(protocol);" };
        String[] getCoveredRegion = { "Rscript", "--vanilla", "-e",
                "options(warn = -1); load(\"" + rData.getAbsolutePath() + "\"); cat(covered_region);" };

        String sampleId = runRscriptCommand(getPatientId);
        String patientId = FhirResolver.resolvePatientFromSample(sampleId);
        String protocol = runRscriptCommand(getProtocol);
        String panel = runRscriptCommand(getPanel);
        String tmb = runRscriptCommand(getTmb);
        String msiScore = runRscriptCommand(getMsiScore);
        String msiStatus = runRscriptCommand(getMsiStatus);
        if (msiStatus.equals("")) {
            msiStatus = "NA";
        } else if (msiStatus.equals("Non-MSI-H")) {
            msiStatus = "Stable";
        } else if (msiStatus.equals("MSI-H")) {
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

    private static String runRscriptCommand(String[] cmd) throws IOException {
        ProcessBuilder pb;
        pb = new ProcessBuilder(cmd);
        String path = System.getenv("PATH");
        pb.environment().put("PATH", path);
        final Process process = pb.start();
        String result = new String(process.getInputStream().readAllBytes());
        return result;
    }

}
