package org.kaaproject.kaa.sandbox.docker;


import org.apache.commons.io.IOUtils;
import org.kaaproject.kaa.sandbox.OsType;
import org.kaaproject.kaa.sandbox.VeryAbstractSandboxBuilder;
import org.kaaproject.kaa.server.common.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;

public class DockerSandboxBuilder extends VeryAbstractSandboxBuilder {


    private static final Logger LOG = LoggerFactory.getLogger(DockerSandboxBuilder.class);

    private static String dockerBuildPath = "";


    private LinkedList<String> dockerInstructions = new LinkedList<>();


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
    protected void buildSandboxMeta(String demoProjectsXML, String startServicesCommand) throws Exception{
        LOG.info("BUILDING SANDBOX META...");
        String meta = executeSudoSandboxCommand("service postgresql start && " +
                "su - postgres -c \"psql --command \\\"alter user postgres with password 'admin';\\\" && " +
                "psql --command \\\"CREATE DATABASE kaa;\\\"\" && java -jar " + SANDBOX_FOLDER + "/meta-builder.jar " + webAdminForwardPort);
        LOG.info(meta);
        LOG.info("SANDBOX META BUILD FINISHED");
    };

    @Override
    protected void unprovisionBoxImpl() throws Exception {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/home/sercv/SandBoxDockerFile")))) {
            for(String instruction: dockerInstructions)
            out.println(instruction);
        }catch (IOException e) {
            LOG.debug(e.getMessage());
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
    protected void transferFile(String file, String to) throws IOException {
        File from = new File(file);
        File fromDocker = new File(dockerBuildPath+"/"+from.getName());
        IOUtils.copy(new FileInputStream(file), new FileOutputStream(fromDocker));
        dockerInstructions.add("COPY " + fromDocker.getName() + " " + to);
    }

    @Override
    protected void transferAllFromDir(String dir, String to) throws IOException {
        File from = new File(dir);
        if (from.isDirectory()) {
            for(File file: from.listFiles()){
                File fromDocker = new File(dockerBuildPath+"/"+file.getName());
                IOUtils.copy(new FileInputStream(file),new FileOutputStream(fromDocker));
            }
        dockerInstructions.add("COPY " + dir + "/* " + to);
        }
        else{
            transferFile(dir,to);
        }
    }




}
