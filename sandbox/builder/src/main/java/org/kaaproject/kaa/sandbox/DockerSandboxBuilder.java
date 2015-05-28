package org.kaaproject.kaa.sandbox;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

public class DockerSandboxBuilder extends AbstractSandboxBuilder {


    public static void main(String[] args) {
        DockerSandboxBuilder dockerSandboxBuilder = new DockerSandboxBuilder(new File("/home/sercv/kaa/sandbox/builder/target/sandbox"),OsType.DEBIAN,null,"anem", null, 22, 8080);
        try {
            dockerSandboxBuilder.buildSandboxImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LinkedList<String> dockerCommands = new LinkedList<>();

    public DockerSandboxBuilder(File basePath, OsType osType, URL baseImageUrl, String boxName, File imageOutputFile, int sshForwardPort, int webAdminForwardPort) {
        super(basePath, osType, baseImageUrl, boxName, imageOutputFile, sshForwardPort, webAdminForwardPort);
    }


    @Override
    protected void sleepForSeconds(long seconds) {
        dockerCommands.add("RUN su -c 'sleep "+seconds+";'");
        System.out.println(dockerCommands.getLast());
    }

    @Override
    protected String executeSudoSandbox(String command) {
        dockerCommands.add("RUN " + command);
        System.out.println(dockerCommands.getLast());
        return "";
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
    protected void transferAllFromDir(String dir, String to) throws IOException {
        dockerCommands.add("COPY " + dir + "/* " + to);
        System.out.println(dockerCommands.getLast());
    }















    @Override
    protected String executeScheduledSandboxCommands() {
        return "";
    }

    @Override
    protected void loadBoxImpl() throws Exception {
    }

    @Override
    protected void prepareBoxImpl() throws Exception {
    }

    @Override
    protected void startBoxImpl() throws Exception {
    }

    @Override
    protected void provisionBoxImpl() throws Exception {
    }

    @Override
    protected void unprovisionBoxImpl() throws Exception {
    }

    @Override
    protected void stopBoxImpl() throws Exception {
    }

    @Override
    protected void cleanupBoxImpl() throws Exception {
    }

    @Override
    protected void exportBoxImpl() throws Exception {
    }

    @Override
    protected void unloadBoxImpl() throws Exception {
    }

    @Override
    protected boolean boxLoaded() throws Exception {
        return false;
    }

    @Override
    protected boolean boxRunning() throws Exception {
        return false;
    }

    @Override
    protected void loadBox() throws Exception {

    }
}
