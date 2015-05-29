package org.kaaproject.kaa.sandbox;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

public class DockerSandboxBuilder extends VeryAbstractSandboxBuilder {


    private static final Logger LOG = LoggerFactory.getLogger(DockerSandboxBuilder.class);

//    public static void main(String[] args) {
//        DockerSandboxBuilder dockerSandboxBuilder = new AbstractSandboxBuilder(new File("/home/sercv/kaa/sandbox/builder/target/sandbox"),OsType.DEBIAN,null,"anem", null, 22, 8080);
//        try {
//            dockerSandboxBuilder.buildSandboxImage();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    LinkedList<String> dockerCommands = new LinkedList<>();

    protected DockerSandboxBuilder(File basePath, OsType osType) {
        super(basePath, osType);
    }


    @Override
    protected void buildDemoApplications() throws Exception {
        executeSudoSandboxCommand("RUN java -jar ApplicationDemoBuilder");
    }

    @Override
    protected void waitForLongRunningTask(long seconds) {
        dockerCommands.add("RUN su -c 'sleep "+seconds+";'");
        System.out.println(dockerCommands.getLast());
    }

    @Override
    protected void provisionBoxImpl() throws Exception {

    }

    @Override
    protected void unprovisionBoxImpl() throws Exception {

    }


    @Override
    protected String executeScheduledSandboxCommands() {
        return null;
    }

    @Override
    protected String executeSudoSandboxCommand(String command) {
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
    protected void scheduleSudoSandboxCommand(String command) {
        dockerCommands.add("RUN " + command);
        System.out.println(dockerCommands.getLast());
    }


    @Override
    protected void transferFile(String file, String to) {
        dockerCommands.add("COPY "+file+" "+to);
        System.out.println(dockerCommands.getLast());
    }

    @Override
    protected void transferAllFromDir(String dir, String to) {
        dockerCommands.add("COPY " + dir + "/* " + to);
        System.out.println(dockerCommands.getLast());
    }










}
