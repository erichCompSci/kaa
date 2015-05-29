package org.kaaproject.kaa.sandbox;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

public class DockerSandboxBuilder extends VeryAbstractSandboxBuilder {


    public static void main(String[] args) {
        DockerSandboxBuilder dockerSandboxBuilder = new DockerSandboxBuilder(new File("/home/sercv/kaa/sandbox/builder/target/sandbox"),OsType.DEBIAN,null,"anem", null, 22, 8080);
        try {
            dockerSandboxBuilder.buildSandboxImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LinkedList<String> dockerCommands = new LinkedList<>();




    @Override
    protected void waitForLongRunningTask(long seconds) {
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
    void onBuildFailure() {

    }

    @Override
    protected void build() throws Exception {

    }

    @Override
    protected void preBuild() throws Exception {

    }

    @Override
    protected void postBuild() throws Exception {

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










}
