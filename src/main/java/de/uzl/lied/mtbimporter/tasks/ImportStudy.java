package de.uzl.lied.mtbimporter.tasks;

import de.uzl.lied.mtbimporter.settings.Settings;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.tinylog.Logger;

/**
 * Class to import a study into cBioPortal.
 */
public final class ImportStudy {

    private ImportStudy() {
    }

    public static void importStudy(String studyId, Long state) throws IOException {
        importStudy(studyId, state, false);
    }

    /**
     * Method to import a study into cBioPortal.
     * @param studyId
     * @param state
     * @param overrideWarnings
     * @throws IOException
     */
    public static void importStudy(String studyId, Long state, Boolean overrideWarnings)
            throws IOException {
        ProcessBuilder pb = new ProcessBuilder();
        String[] portal = getPortal();
        List<String> args = new ArrayList<>();

        if (Settings.getDocker() != null && Settings.getDocker().getCompose() != null) {
            pb.directory(new File(Settings.getDocker().getCompose().getWorkdir()));
            args.add("docker-compose");
            args.add("run");
            args.add("--rm");
            args.add(Settings.getDocker().getCompose().getServiceName());
        }
        if (Settings.getDocker() != null && Settings.getDocker().getCompose() == null) {
            args.add("docker");
            args.add("run");
            args.add("--rm");
            args.add("--network=" + Settings.getDocker().getNetworkName());
            args.add("-v");
            args.add(Settings.getStudyFolder() + ":" + Settings.getDocker().getStudyFolder());
            args.add("-v");
            args.add(Settings.getDocker().getPropteriesFile() + ":/cbioportal/portal.properties");
            if (portal[0].equals("-p")) {
                args.add("-v");
                args.add(Settings.getDocker().getPortalInfoVolume() + ":" + Settings.getPortalInfo());
            }
            args.add(Settings.getDocker().getImageName());
        }
        if (Settings.getDocker() != null) {
            args.add("metaImport.py");
            args.add("-s");
            args.add(Settings.getDocker().getStudyFolder() + "/" + studyId + "/" + state);
        } else {
            args.add(Settings.getImportScriptPath().getPath());
            args.add("metaImport.py");
            args.add("-s");
            args.add(Settings.getStudyFolder().getPath() + state);
            pb.directory(Settings.getImportScriptPath().getParentFile());
        }

        args.add(portal[0]);
        args.add(portal[1]);

        if (Boolean.TRUE.equals(overrideWarnings)) {
            args.add("-o");
        }

        pb.command(args);
        pb.environment().put("PATH", System.getenv("PATH"));

        final Process process = pb.start();
        String importResult = new String(process.getInputStream().readAllBytes());
        String importError = new String(process.getErrorStream().readAllBytes());

        Logger.debug(importResult);
        Logger.error(importError);

    }

    /**
     * Concats the portal info string depending on the configuration.
     * @return String array for portal info.
     */
    private static String[] getPortal() {
        if (Settings.getPortalInfo() != null) {
            return new String[] {"-p", Settings.getPortalInfo().getPath()};
        }
        if (Settings.getPortalUrl() != null) {
            return new String[] {"-u", Settings.getPortalUrl().toString()};
        }
        return new String[] {"", ""};
    }

}
