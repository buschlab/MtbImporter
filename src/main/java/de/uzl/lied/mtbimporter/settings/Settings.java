package de.uzl.lied.mtbimporter.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Settings for MtbExporter.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Settings {

    @JsonProperty("portalUrl")
    private static String portalUrl;
    @JsonProperty("portalInfo")
    private static String portalInfo;
    @JsonProperty("importScriptPath")
    private static String importScriptPath;
    @JsonProperty("inputFolders")
    private static InputFolder[] inputFolders;
    @JsonProperty("cronIntervall")
    private static Integer cronIntervall;
    @JsonProperty("studyFolder")
    private static String studyFolder;
    @JsonProperty("studyTemplate")
    private static String studyTemplate;
    @JsonProperty("mainStudyId")
    private static String mainStudyId;
    @JsonProperty("urlBase")
    private static String urlBase;
    @JsonProperty("resourceFolder")
    private static String resourceFolder;
    @JsonProperty("overrideWarnings")
    private static Boolean overrideWarnings = false;
    @JsonProperty("restartAfterImport")
    private static Boolean restartAfterImport = false;
    @JsonProperty("restartCommand")
    private static String restartCommand;
    @JsonProperty("ensemblUrl")
    private static String ensemblUrl;
    @JsonProperty("fhir")
    private static FhirSettings fhir;
    @JsonProperty("docker")
    private static DockerSettings docker;
    @JsonProperty("mdr")
    private static List<Mdr> mdr;
    @JsonProperty("regex")
    private static List<Regex> regex;
    @JsonProperty("mappingMethod")
    private static String mappingMethod;
    @JsonProperty("mapping")
    private static List<Mapping> mapping;

    public static String getPortalUrl() {
        return portalUrl;
    }

    @JsonProperty("portalUrl")
    public void setPortalUrl(String newPortalUrl) {
        portalUrl = newPortalUrl;
    }

    public static String getPortalInfo() {
        return portalInfo;
    }

    /**
     * Sets path for portal info.
     * @param portalInfo
     */
    @JsonProperty("portalInfo")
    public void setPortalInfo(String portalInfo) {
        String newPortalInfo = portalInfo;
        if (!newPortalInfo.endsWith("/")) {
            newPortalInfo = newPortalInfo + "/";
        }
        Settings.portalInfo = newPortalInfo;
    }

    public static String getImportScriptPath() {
        return importScriptPath;
    }

    /**
     * Sets path for import script.
     * @param importScriptPath
     */
    @JsonProperty("importScriptPath")
    public void setImportScriptPath(String importScriptPath) {
        String newImportScriptPath = importScriptPath;
        if (!newImportScriptPath.endsWith("/")) {
            newImportScriptPath = newImportScriptPath + "/";
        }
        Settings.importScriptPath = newImportScriptPath;
    }

    public static InputFolder[] getInputFolders() {
        return inputFolders;
    }

    @JsonProperty("inputFolders")
    public void setMafPath(InputFolder[] newInputFolders) {
        inputFolders = newInputFolders;
    }

    public static Integer getCronIntervall() {
        return cronIntervall;
    }

    @JsonProperty("cronIntervall")
    public void setCronItnervall(Integer newCronIntervall) {
        cronIntervall = newCronIntervall;
    }

    public static String getStudyFolder() {
        return studyFolder;
    }

    /**
     * Sets path for study folder.
     */
    @JsonProperty("studyFolder")
    public void setStudyFolder(String studyFolder) {
        String newStudyFolder = studyFolder;
        if (!newStudyFolder.endsWith("/")) {
            newStudyFolder = newStudyFolder + "/";
        }
        Settings.studyFolder = newStudyFolder;
    }

    public static String getStudyTemplate() {
        return studyTemplate;
    }

    @JsonProperty("studyTemplate")
    public void setStudyTemplate(String newStudyTemplate) {
        studyTemplate = newStudyTemplate;
    }

    public static String getMainStudyId() {
        return mainStudyId;
    }

    @JsonProperty("mainStudyId")
    public void setMainStudyId(String newMainStudyId) {
        mainStudyId = newMainStudyId;
    }

    public static String getUrlBase() {
        return urlBase;
    }

    @JsonProperty("urlBase")
    public void setUrlBase(String newUrlBase) {
        urlBase = newUrlBase;
    }

    public static String getResourceFolder() {
        return resourceFolder;
    }

    /**
     * Sets path for resource folder.
     * @param resourceFolder
     */
    @JsonProperty("resourceFolder")
    public void setResourceFolder(String resourceFolder) {
        String newResourceFolder = resourceFolder;
        if (!newResourceFolder.endsWith("/")) {
            newResourceFolder = newResourceFolder + "/";
        }
        Settings.resourceFolder = newResourceFolder;
    }

    public static Boolean getOverrideWarnings() {
        return overrideWarnings;
    }

    @JsonProperty("overrideWarnings")
    public void setOverrideWarnings(Boolean newOverrideWarnings) {
        overrideWarnings = newOverrideWarnings;
    }

    public static Boolean getRestartAfterImport() {
        return restartAfterImport;
    }

    @JsonProperty("restartAfterImport")
    public void setRestartAfterImport(Boolean newRestartAfterImport) {
        restartAfterImport = newRestartAfterImport;
    }

    public static String getRestartCommand() {
        return restartCommand;
    }

    @JsonProperty("restartCommand")
    public void setRestartCommand(String newRestartCommand) {
        restartCommand = newRestartCommand;
    }

    public static String getEnsemblUrl() {
        return ensemblUrl;
    }

    @JsonProperty("ensemblUrl")
    public void setEnsemblUrl(String newEnsemblUrl) {
        ensemblUrl = newEnsemblUrl;
    }

    public static FhirSettings getFhir() {
        return fhir;
    }

    @JsonProperty("fhir")
    public void setFhir(FhirSettings newFhir) {
        fhir = newFhir;
    }

    public static DockerSettings getDocker() {
        return docker;
    }

    @JsonProperty("docker")
    public void setDocker(DockerSettings newDocker) {
        docker = newDocker;
    }

    public static List<Mdr> getMdr() {
        return mdr;
    }

    @JsonProperty("mdr")
    public void setMdr(List<Mdr> newMdr) {
        mdr = newMdr;
    }

    public static List<Regex> getRegex() {
        return regex;
    }

    @JsonProperty("regex")
    public void setRegex(List<Regex> newRegex) {
        regex = newRegex;
    }

    public static String getMappingMethod() {
        return mappingMethod;
    }

    @JsonProperty("mappingMethod")
    public void setMappingMethod(String newMappingMethod) {
        mappingMethod = newMappingMethod;
    }

    public static List<Mapping> getMapping() {
        return mapping;
    }

    @JsonProperty("mapping")
    public void setMapping(List<Mapping> newMapping) {
        mapping = newMapping;
    }

}
