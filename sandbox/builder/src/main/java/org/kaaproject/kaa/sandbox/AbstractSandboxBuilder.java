/*
 * Copyright 2014 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.sandbox;

import org.apache.commons.io.FilenameUtils;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHBase;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.apache.tools.ant.types.FileSet;
import org.kaaproject.kaa.sandbox.ssh.SandboxSshExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractSandboxBuilder extends VeryAbstractSandboxBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSandboxBuilder.class);


    protected final URL baseImageUrl;
    protected final File imageOutputFile;


    protected File baseImageFile;

    private CommandsResource commandsResource = new CommandsResource();
    private SandboxProject sandboxProject = new SandboxProject();

    public AbstractSandboxBuilder(File basePath,
                                  OsType osType,
                                  URL baseImageUrl,
                                  String boxName,
                                  File imageOutputFile,
                                  int sshForwardPort,
                                  int webAdminForwardPort) {

        super(basePath, osType, boxName, sshForwardPort, webAdminForwardPort);
        this.baseImageUrl = baseImageUrl;
        this.imageOutputFile = imageOutputFile;
    }

    @Override
    protected void preBuild() throws Exception {
        loadBox();
        prepareBox();
        startBox();
    }

    @Override
    protected void postBuild() throws Exception {
        stopBox();
        cleanupBox();
        exportBox();
    }

    @Override
    protected void buildSandboxMetaImpl(){
        executeSudoSandboxCommand("java -jar " + SANDBOX_FOLDER + "/" +META_BUILDER_JAR+ " " + webAdminForwardPort);
    }

    @Override
    protected void cleanUp() throws Exception {
        if (boxLoaded()) {
            LOG.info("Box with name '{}' already exists. Going to unload old instance ...", boxName);
            if (boxRunning()) {
                stopBox();
            }
            unloadBox();
        }
    }

    @Override
    protected void onBuildFailure(Exception e) {
        LOG.error("Failed to build sandbox image!", e);
        dumpLogs();
        LOG.error("Look for sandbox build logs at: [{}]",LOG_DUMP_LOCATION);
    }

    protected void loadBox() throws Exception {
        LOG.info("Loading box '{}' ...", boxName);
        String fileName = FilenameUtils.getName(baseImageUrl.toString());
        baseImageFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        LOG.info("Downloading base image to file '{}'...", baseImageFile.getAbsolutePath());
        SandboxImageDownloader.downloadFile(baseImageUrl, baseImageFile);

        if (baseImageFile.exists() && baseImageFile.isFile()) {
            LOG.info("Downloaded base image to file '{}'.", baseImageFile.getAbsolutePath());
        } else {
            LOG.error("Unable to download image file to '{}'", baseImageFile.getAbsolutePath());
            throw new RuntimeException("Unable to download image file!");
        }
        loadBoxImpl();
        LOG.info("Box '{}' is loaded.", boxName);
    }


    private void prepareBox() throws Exception {
        LOG.info("Preparing box '{}' ...", boxName);
        prepareBoxImpl();
        LOG.info("Box '{}' is prepared.", boxName);
    }

    private void startBox() throws Exception {
        LOG.info("Starting box '{}' ...", boxName);
        startBoxImpl();
        LOG.info("Box '{}' is started.", boxName);
    }

    private void stopBox() throws Exception {
        LOG.info("Stopping box '{}' ...", boxName);
        stopBoxImpl();
        LOG.info("Box '{}' is stopped.", boxName);
    }

    private void cleanupBox() throws Exception {
        LOG.info("Cleaning box '{}' ...", boxName);
        cleanupBoxImpl();
        LOG.info("Box '{}' is cleaned.", boxName);
    }

    private void exportBox() throws Exception {
        LOG.info("Exporting box '{}' ...", boxName);
        if (imageOutputFile.exists()) {
            if (!imageOutputFile.delete()) {
                LOG.error("Unable to delete previous output image file '{}'", imageOutputFile.getAbsoluteFile());
                throw new RuntimeException("Failed to export sandbox image!");
            }
        } else if (!imageOutputFile.getParentFile().exists()) {
            imageOutputFile.getParentFile().mkdirs();
        }
        exportBoxImpl();
        LOG.info("Box '{}' was exported.", boxName);
    }

    private void unloadBox() throws Exception {
        LOG.info("Unloading box '{}' ...", boxName);
        unloadBoxImpl();
        LOG.info("Box '{}' was unloaded.", boxName);
    }


    protected void waitForLongRunningTask(long seconds) {
        LOG.info("Sleeping {} sec.", seconds);
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            LOG.debug(e.getMessage());
        }
    }

    protected boolean isWin32() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    protected String execute(boolean logOutput, String... command) throws Exception {
        return execute(logOutput, Arrays.asList(command));
    }

    protected String execute(boolean logOutput, List<String> command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command).directory(basePath);
        Process p = pb.start();
        String result = handleStream(p.getInputStream(), logOutput);
        handleStream(p.getErrorStream(), true);
        p.waitFor();
        p.destroy();
        return result;
    }

    protected String handleStream(InputStream input) throws IOException {
        return handleStream(input, false);
    }

    protected String handleStream(InputStream input, boolean logOutput) throws IOException {
        String line;
        StringWriter output = new StringWriter();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        BufferedWriter writer = new BufferedWriter(output);
        while ((line = reader.readLine()) != null) {
            if (logOutput) {
                LOG.info(line);
            }
            writer.write(line);
            writer.newLine();
        }
        input.close();
        writer.close();
        return output.toString();
    }

    protected String executeSudoSandboxCommand(String command) {
        SandboxSshExec sshExec = createSshExec();
        sshExec.setCommand("sudo " + command);
        sshExec.setOutputproperty("sshOutProp");
        sshExec.execute();
        return sandboxProject.getProperty("sshOutProp");
    }

    protected void scheduleSudoSandboxCommand(String command) {
        commandsResource.addCommand("sudo " + command);
    }

    protected String executeScheduledSandboxCommands() {
        LOG.info("Executing scheduled commands...");
        SandboxSshExec sshExec = createSshExec();
        sshExec.setCommandResource(commandsResource);
        sshExec.setOutputproperty("sshOutProp");
        sshExec.execute();
        LOG.info("Scheduled commands execution is completed.");
        return sandboxProject.getProperty("sshOutProp");
    }

    private SandboxSshExec createSshExec() {
        SandboxSshExec sshExec = new SandboxSshExec();
        initSsh(sshExec);
        return sshExec;
    }

    @Override
    protected void transferAllFromDir(String dir, String to) {
        Scp scp = createScp();
        FileSet fileSet = new FileSet();
        fileSet.setDir(new File(dir));
        fileSet.setIncludes("**/*");
        scp.addFileset(fileSet);
        scp.setRemoteTodir(SSH_USERNAME + "@" + DEFAULT_HOST + ":" + to);
        scp.execute();
    }

    @Override
    protected void transferFile(String file, String to) {
        Scp scp = createScp();
        scp.setLocalFile(file);
        scp.setRemoteTodir(SSH_USERNAME + "@" + DEFAULT_HOST + ":" + to);
        scp.execute();
    }

    private void dumpLogs() {
        File dumpedLogsFolder = new File(LOG_DUMP_LOCATION);
        if (!dumpedLogsFolder.exists()) {
            dumpedLogsFolder.mkdirs();
        }
        Scp scp = createScp();
        scp.setLocalTodir(LOG_DUMP_LOCATION);
        scp.setFile(SSH_USERNAME + "@" + DEFAULT_HOST + ":/var/log/kaa/*");
        scp.execute();
    }

    private Scp createScp() {
        Scp scp = new Scp();
        initSsh(scp);
        return scp;
    }

    private void initSsh(SSHBase ssh) {
        ssh.setProject(sandboxProject);
        ssh.setUsername(SSH_USERNAME);
        ssh.setPassword(SSH_PASSWORD);
        ssh.setPort(sshForwardPort);
        ssh.setHost(DEFAULT_HOST);
        ssh.setTrust(true);
    }


    protected abstract void loadBoxImpl() throws Exception;

    protected abstract void prepareBoxImpl() throws Exception;

    protected abstract void startBoxImpl() throws Exception;

    protected abstract void stopBoxImpl() throws Exception;

    protected abstract void cleanupBoxImpl() throws Exception;

    protected abstract void exportBoxImpl() throws Exception;

    protected abstract void unloadBoxImpl() throws Exception;

    protected abstract boolean boxLoaded() throws Exception;

    protected abstract boolean boxRunning() throws Exception;
}
