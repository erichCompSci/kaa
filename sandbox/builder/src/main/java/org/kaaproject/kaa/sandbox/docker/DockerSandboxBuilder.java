package org.kaaproject.kaa.sandbox.docker;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.kaaproject.kaa.sandbox.OsType;
import org.kaaproject.kaa.sandbox.VeryAbstractSandboxBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;

public class DockerSandboxBuilder extends VeryAbstractSandboxBuilder {


    private static final Logger LOG = LoggerFactory.getLogger(DockerSandboxBuilder.class);

    private StringBuilder dockerInstructions = new StringBuilder();


    public DockerSandboxBuilder(File basePath,
                                OsType osType,
                                String boxName,
                                int sshForwardPort,
                                int webAdminForwardPort) {
        super(basePath, osType, boxName, sshForwardPort, webAdminForwardPort);
    }


    @Override
    protected void waitForLongRunningTask(long seconds) {

    }

    @Override
    protected void provisionBoxImpl() throws Exception {

    }

    @Override
    protected void buildSandboxMeta(String demoProjectsXML, String startServicesCommand) throws Exception {
        LOG.info("BUILDING SANDBOX META...");
        executeSudoSandboxCommand("service postgresql start && su - postgres -c " +
                "\"psql --command \\\\\"alter user postgres with password 'admin';\\\\\" && " +
                "psql --command \\\\\"CREATE DATABASE kaa;\\\\\"\" && "+
                "java -jar " + SANDBOX_FOLDER + "/meta-builder.jar " + webAdminForwardPort);
        LOG.info("SANDBOX META BUILD FINISHED");
    }

    ;

    @Override
    protected void unprovisionBoxImpl() throws Exception {
        String dockerfileTemplate = org.kaaproject.kaa.server.common.utils.FileUtils.readResource(DOCKERFILE_TEMPLATE);
        String dockerfileSource = dockerfileTemplate.replaceAll(KAA_DOCKERFILE_PART, dockerInstructions.toString());
        File dockerfile = new File(DOCKER_BUILD_PATH + "/" + DOCKERFILE);
        FileOutputStream fos = new FileOutputStream(dockerfile);
        fos.write(dockerfileSource.getBytes());
        fos.flush();
        fos.close();
    }

    @Override
    protected String executeScheduledSandboxCommands() {
        return null;
    }


    @Override
    protected void preBuild() throws Exception {
        LOG.info("Starting Dockerfile creation");
    }

    @Override
    protected void postBuild() throws Exception {
        LOG.info("Dockerfile creation finished");
    }

    @Override
    protected void cleanUp() throws Exception {

    }

    @Override
    protected void onBuildFailure() {

    }


    @Override
    protected String executeSudoSandboxCommand(String command) {
        dockerInstructions.append("RUN ").append(command).append("\n");
        return null;
    }

    @Override
    protected void scheduleSudoSandboxCommand(String command) {
        dockerInstructions.append("RUN ").append(command).append("\n");
    }


    @Override
    protected void transferFile(String file, String to) throws IOException {
        File from = new File(file);
        File fromDocker = new File(DOCKER_BUILD_PATH + "/" + from.getName());
        IOUtils.copy(new FileInputStream(file), new FileOutputStream(fromDocker));
        dockerInstructions.append("COPY ").append(fromDocker.getName()).append(" ").append(to).append("\n");
    }

    @Override
    protected void transferAllFromDir(String dir, String to) throws IOException {
        File from = new File(dir);
        if (from.isDirectory()) {
            for (File file : from.listFiles()) {
                File fromDocker = new File(DOCKER_BUILD_PATH + "/" + file.getName());
                if (file.isDirectory()) {
                    FileUtils.copyDirectory(file, fromDocker);
                } else {
                    FileUtils.copyFile(file, fromDocker);
                }
            }
            dockerInstructions.append("COPY ").append(dir).append("/* ").append(to).append("\n");
        } else {
            transferFile(dir, to);
        }
    }


}
