package de.uzl.lied.mtbimporter.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.ByteStreams;

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
    @JsonProperty("fhirServer")
    private static String fhirServer;
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
    @JsonIgnore
    private static long state;

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

    @JsonProperty("portalInfo")
    public void setPortalInfo(String newPortalInfo) {
        if (!newPortalInfo.endsWith("/")) {
            newPortalInfo = newPortalInfo + "/";
        }
        portalInfo = newPortalInfo;
    }

    public static String getImportScriptPath() {
        return importScriptPath;
    }

    @JsonProperty("importScriptPath")
    public void setImportScriptPath(String newImportScriptPath) {
        if (!newImportScriptPath.endsWith("/")) {
            newImportScriptPath = newImportScriptPath + "/";
        }
        importScriptPath = newImportScriptPath;
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

    @JsonProperty("studyFolder")
    public void setStudyFolder(String newStudyFolder) {
        if (!newStudyFolder.endsWith("/")) {
            newStudyFolder = newStudyFolder + "/";
        }
        studyFolder = newStudyFolder;
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

    @JsonProperty("resourceFolder")
    public void setResourceFolder(String newResourceFolder) {
        if (!newResourceFolder.endsWith("/")) {
            newResourceFolder = newResourceFolder + "/";
        }
        resourceFolder = newResourceFolder;
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

    public static String getFhirServer() {
        return fhirServer;
    }

    @JsonProperty("fhirServer")
    public void setFhirServer(String newFhirServer) {
        fhirServer = newFhirServer;
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

    public static long getState() throws FileNotFoundException, IOException {
        if (state == 0) {
            InputStream stateFile = new FileInputStream(studyFolder + "/.state");
            state = Long.parseLong(new String(ByteStreams.toByteArray(stateFile)));
        }
        return state;
    }

    public static void setState(Long newState) throws IOException {
        FileOutputStream fos = new FileOutputStream(studyFolder + "/.state");
        fos.write(String.valueOf(newState).getBytes());
        fos.close();
        state = newState;
    }

}