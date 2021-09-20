package de.uzl.lied.mtbimporter.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.uzl.lied.mtbimporter.settings.Settings;

public class ImportStudy {

    public static void importStudy(Long state) throws IOException, InterruptedException {
        importStudy(state, false);
    }

    public static void importStudy(Long state, Boolean overrideWarnings) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        String[] portal = getPortal();
        List<String> args = new ArrayList<String>();

        if (Settings.getDocker() != null) {
            if (Settings.getDocker().getCompose() != null) {
                pb.directory(new File(Settings.getDocker().getCompose().getWorkdir()));
                args.add("docker-compose");
                args.add("run");
                args.add("--rm");
                args.add(Settings.getDocker().getCompose().getServiceName());
            } else {
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
            args.add("metaImport.py");
            args.add("-s");
            args.add(Settings.getDocker().getStudyFolder() + state);
        } else {
            args.add(Settings.getImportScriptPath());
            args.add("metaImport.py");
            args.add("-s");
            args.add(Settings.getStudyFolder() + state);
            File f = new File(Settings.getImportScriptPath());
            pb.directory(f.getParentFile());
        }

        args.add(portal[0]);
        args.add(portal[1]);

        if (overrideWarnings) {
            args.add("-o");
        }

        pb.command(args);
        pb.environment().put("PATH", System.getenv("PATH"));

        final Process process = pb.start();
        String importResult = new String(process.getInputStream().readAllBytes());
        String importError = new String(process.getErrorStream().readAllBytes());

        System.out.println(importResult);
        System.out.println(importError);

        if (Settings.getRestartAfterImport()) {
            args.clear();
            if (Settings.getDocker() != null) {
                if (Settings.getDocker().getCompose() != null) {
                    args.add("docker-compose");
                    args.add("restart");
                    args.add(Settings.getDocker().getCompose().getServiceName());
                } else {
                    args.add("docker");
                    args.add("restart");
                    args.add(Settings.getDocker().getContainerName());
                }
            } else {
                pb.command(Settings.getRestartCommand().split(" "));
            }
            final Process restartProcess = pb.start();
            String restartResult = new String(restartProcess.getInputStream().readAllBytes());
            String restartError = new String(restartProcess.getErrorStream().readAllBytes());
            System.out.println(restartResult);
            System.out.println(restartError);
            System.out.println("Restarted cBioPortal!");
        }
    }

    private static String[] getPortal() {
        if (Settings.getPortalInfo() != null) {
            return new String[] { "-p", Settings.getPortalInfo() };
        }
        if (Settings.getPortalUrl() != null) {
            return new String[] { "-u", Settings.getPortalUrl() };
        }
        return new String[] { "", "" };
    }

}
