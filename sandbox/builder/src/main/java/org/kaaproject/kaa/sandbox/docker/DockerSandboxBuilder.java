package org.kaaproject.kaa.sandbox.docker;


import org.kaaproject.kaa.sandbox.OsType;
import org.kaaproject.kaa.sandbox.VeryAbstractSandboxBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class DockerSandboxBuilder extends VeryAbstractSandboxBuilder {


    private static final Logger LOG = LoggerFactory.getLogger(DockerSandboxBuilder.class);

    LinkedList<String> dockerInstructions = new LinkedList<>();

    public DockerSandboxBuilder(File basePath,
                                   OsType osType,
                                   String boxName,
                                   int sshForwardPort,
                                   int webAdminForwardPort) {
        super(basePath, osType, boxName, sshForwardPort, webAdminForwardPort);
    }


    @Override
    protected void waitForLongRunningTask(long seconds) {
        dockerInstructions.add("RUN su -c 'sleep " + seconds + ";'");
    }

    @Override
    protected void provisionBoxImpl() throws Exception {

    }

    @Override
    protected void unprovisionBoxImpl() throws Exception {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/home/sercv/SandBoxDockerFile", true)))) {
            for(String instruction: dockerInstructions)
            out.println(instruction);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
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
        dockerInstructions.add("RUN " + command);
        return null;
    }

    @Override
    protected void scheduleSudoSandboxCommand(String command) {
        dockerInstructions.add("RUN " + command);
    }


    @Override
    protected void transferFile(String file, String to) {
        dockerInstructions.add("COPY " + file + " " + to);
    }

    @Override
    protected void transferAllFromDir(String dir, String to) {
        dockerInstructions.add("COPY " + dir + "/* " + to);
    }




}
