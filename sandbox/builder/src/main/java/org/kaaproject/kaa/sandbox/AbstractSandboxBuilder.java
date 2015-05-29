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
import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.taskdefs.optional.ssh.SSHBase;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.apache.tools.ant.types.FileSet;
import org.kaaproject.kaa.sandbox.demo.AbstractDemoBuilder;
import org.kaaproject.kaa.sandbox.demo.DemoBuilder;
import org.kaaproject.kaa.sandbox.demo.DemoBuildersRegistry;
import org.kaaproject.kaa.sandbox.demo.projects.Project;
import org.kaaproject.kaa.sandbox.demo.projects.ProjectsConfig;
import org.kaaproject.kaa.sandbox.rest.SandboxClient;
import org.kaaproject.kaa.sandbox.ssh.SandboxSshExec;
import org.kaaproject.kaa.server.common.admin.AdminClient;
import org.kaaproject.kaa.server.common.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractSandboxBuilder implements SandboxBuilder, SandboxConstants {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSandboxBuilder.class);


    protected final File basePath;
    protected final OsType osType;
    protected final URL baseImageUrl;
    protected final String boxName;
    protected final File imageOutputFile;
    protected final int sshForwardPort;
    protected final int webAdminForwardPort;

    protected File distroPath;
    protected File demoProjectsPath;
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

        this.basePath = basePath;
        this.osType = osType;
        this.baseImageUrl = baseImageUrl;
        this.boxName = boxName;
        this.imageOutputFile = imageOutputFile;
        this.sshForwardPort = sshForwardPort;
        this.webAdminForwardPort = webAdminForwardPort;
        this.distroPath = new File(basePath, "distro");
        this.demoProjectsPath = new File(basePath, "demo_projects");
    }

    @Override
    public void buildSandboxImage() throws Exception {
        if (boxLoaded()) {
            LOG.info("Box with name '{}' already exists. Going to unload old instance ...", boxName);
            if (boxRunning()) {
                stopBox();
            }
            unloadBox();
        }
        loadBox();
        try {
            prepareBox();
            startBox();
            provisionBox();
            schedulePackagesInstall();
            scheduleServicesStart();
            LOG.info("Executing remote ssh commands...");
            executeScheduledSandboxCommands();
            LOG.info("Remote ssh commands execution is completed.");
            sleepForSeconds(80);
            initBoxData();
            sleepForSeconds(20);
            unprovisionBox();
            stopBox();
            cleanupBox();
            exportBox();
        } catch (Exception e) {
            LOG.error("Failed to build sandbox image!", e);
            dumpLogs();
            LOG.error("Look for sandbox build logs at: '{}'", LOG_DUMP_LOCATION);
            throw e;
        } finally {
            if (boxLoaded()) {
                if (boxRunning()) {
                    stopBox();
                }
//                unloadBox();
            }
        }
    }


    protected void sleepForSeconds(long seconds){
        LOG.info("Sleeping {} sec.", seconds);
        try {
            Thread.sleep(seconds*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private void provisionBox() throws Exception {
        provisionBoxImpl();
        scheduleSudoSandboxCommand("rm -rf " + "/"+SHARED_FOLDER);
        scheduleSudoSandboxCommand("mkdir -p " + "/"+SHARED_FOLDER);
        scheduleSudoSandboxCommand("mkdir -p " + SANDBOX_FOLDER);
        scheduleSudoSandboxCommand("chown -R " + SSH_USERNAME + ":" + SSH_USERNAME + " " + "/"+SHARED_FOLDER);
        scheduleSudoSandboxCommand("chown -R "+SSH_USERNAME+":"+SSH_USERNAME+" " + SANDBOX_FOLDER);
        executeScheduledSandboxCommands();

        LOG.info("Transfering sandbox data...");
        transferAllFromDir(basePath.getAbsolutePath(), "/"+SHARED_FOLDER);
        LOG.info("Sandbox data transfered");

    }

    private void unprovisionBox() throws Exception {
        executeSudoSandbox("rm -rf " + "/" + SHARED_FOLDER);
        unprovisionBoxImpl();
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
        } else if (!imageOutputFile.getParentFile().exists()){
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



    protected void schedulePackagesInstall() {
        for (KaaPackage kaaPackage : KaaPackage.values()) {
            String command = osType.getInstallPackageTemplate().
                    replaceAll(DISTRO_PATH_VAR, DISTRO_PATH).
                    replaceAll(PACKAGE_NAME_VAR, kaaPackage.getPackageName());
            scheduleSudoSandboxCommand(command);
        }
    }

    protected void scheduleServicesStart() {
        for (KaaPackage kaaPackage : KaaPackage.values()) {
            String command = osType.getStartServiceTemplate().
                    replaceAll(SERVICE_NAME_VAR, kaaPackage.getServiceName());
            scheduleSudoSandboxCommand(command);
        }
    }





    protected void initBoxData() throws Exception {

        scheduleSudoSandboxCommand("rm -rf " + SANDBOX_FOLDER + "/" + DEMO_PROJECTS);
        scheduleSudoSandboxCommand("rm -f " + SANDBOX_FOLDER + "/" + CHANGE_KAA_HOST);
        scheduleSudoSandboxCommand("rm -f " + SANDBOX_FOLDER + "/" + SANDBOX_SPLASH_PY);
        scheduleSudoSandboxCommand("cp -r " + DEMO_PROJECTS_PATH + " " + SANDBOX_FOLDER + "/");
        scheduleSudoSandboxCommand("chown -R "+SSH_USERNAME+":"+SSH_USERNAME+" " + SANDBOX_FOLDER + "/" + DEMO_PROJECTS);
        scheduleSudoSandboxCommand("cp -r " + SANDBOX_PATH+"/*" + " " + ADMIN_FOLDER + "/");
        scheduleSudoSandboxCommand("chown -R "+SSH_USERNAME+":"+SSH_USERNAME+" " + ADMIN_FOLDER + "/webapps");
        scheduleSudoSandboxCommand("sed -i \"s/\\(tenant_developer_user=\\).*\\$/\\1"+AbstractDemoBuilder.tenantDeveloperUser+"/\" " + ADMIN_FOLDER +"/conf/sandbox-server.properties");
        scheduleSudoSandboxCommand("sed -i \"s/\\(tenant_developer_password=\\).*\\$/\\1"+AbstractDemoBuilder.tenantDeveloperPassword+"/\" " + ADMIN_FOLDER +"/conf/sandbox-server.properties");
        String stopAdminCommand = osType.getStopServiceTemplate().replaceAll(SERVICE_NAME_VAR, KaaPackage.ADMIN.getServiceName());
        scheduleSudoSandboxCommand(stopAdminCommand);
        executeScheduledSandboxCommands();



        File changeKaaHostFile = prepareChangeKaaHostFile();
        File sandboxSplashFile = prepareSandboxSplashFile();
        transferFile(changeKaaHostFile.getAbsolutePath(), SANDBOX_FOLDER);
        transferFile(sandboxSplashFile.getAbsolutePath(), SANDBOX_FOLDER);


        executeSudoSandbox("chmod +x " + SANDBOX_FOLDER + "/" + CHANGE_KAA_HOST);
        executeSudoSandbox("chmod +x " + SANDBOX_FOLDER + "/" + SANDBOX_SPLASH_PY);
        String startAdminCommand = osType.getStartServiceTemplate().replaceAll(SERVICE_NAME_VAR, KaaPackage.ADMIN.getServiceName());
        executeSudoSandbox(startAdminCommand);

        sleepForSeconds(50);

        buildDemoApplications();
    }





    protected void buildDemoApplications() throws Exception {
        List<Project> projects = new ArrayList<>();
        AdminClient adminClient = new AdminClient(DEFAULT_HOST, webAdminForwardPort);
        List<DemoBuilder> demoBuilders = DemoBuildersRegistry.getRegisteredDemoBuilders();
        for (DemoBuilder demoBuilder : demoBuilders) {
            demoBuilder.buildDemoApplication(adminClient);
            projects.addAll(demoBuilder.getProjectConfigs());
        }
        File projectsXmlFile = prepareProjectsXmlFile(projects);
        transferFile(projectsXmlFile.getAbsolutePath(), SANDBOX_FOLDER + "/" + DEMO_PROJECTS);

        LOG.info("Building demo applications...");
        SandboxClient sandboxClient = new SandboxClient(DEFAULT_HOST, webAdminForwardPort);

        List<Project> sandboxProjects = sandboxClient.getDemoProjects();
        if (projects.size() != sandboxProjects.size()) {
            LOG.error("Demo projects count mismatch, expected {}, actual {}", projects.size(), sandboxProjects.size());
            throw new RuntimeException("Demo projects count mismatch!");
        }
        for (Project sandboxProject : sandboxProjects) {
            if (sandboxProject.getDestBinaryFile() != null &&
                    sandboxProject.getDestBinaryFile().length()>0) {
                LOG.info("[{}][{}] Building Demo Project...", sandboxProject.getPlatform(), sandboxProject.getName());
                String output = sandboxClient.buildProjectBinary(sandboxProject.getId());
                LOG.info("[{}][{}] Build output:\n{}", sandboxProject.getPlatform(), sandboxProject.getName(), output);
                if (!sandboxClient.isProjectBinaryDataExists(sandboxProject.getId())) {
                    LOG.error("Failed to build demo project '{}'", sandboxProject.getName());
                    throw new RuntimeException("Failed to build demo project '" + sandboxProject.getName() + "'!");
                }
            } else {
                LOG.info("[{}][{}] Skipping Demo Project build...", sandboxProject.getPlatform(), sandboxProject.getName());
            }
        }
        LOG.info("Finished building demo applications!");
    }


    protected File prepareChangeKaaHostFile() throws IOException {
        //Change kaa hosts file
        String changeKaaHostFileTemplate = FileUtils.readResource(CHANGE_KAA_HOST_TEMPLATE);
        String stopServices = "";
        String setNewHosts = "";
        String startServices = "";
        for (KaaPackage kaaPackage : KaaPackage.values()) {
            if (kaaPackage.getHostProperties() != null &&
                    kaaPackage.getHostProperties().length>0) {
                if (StringUtils.isNotBlank(stopServices)) {
                    stopServices += "\n";
                    startServices += "\n";
                }
                stopServices += osType.getStopServiceTemplate().
                        replaceAll(SERVICE_NAME_VAR, kaaPackage.getServiceName());
                startServices += osType.getStartServiceTemplate().
                        replaceAll(SERVICE_NAME_VAR, kaaPackage.getServiceName());

                for (String propertyName : kaaPackage.getHostProperties()) {
                    if (StringUtils.isNotBlank(setNewHosts)) {
                        setNewHosts += "\n";
                    }
                    setNewHosts += "setNewHost " + kaaPackage.getPropertiesFile() + " " + propertyName;
                }
            }
        }

        String changeKaaHostFileSource = changeKaaHostFileTemplate.replaceAll(STOP_SERVICES_VAR, stopServices)
                .replaceAll(SET_NEW_HOSTS, setNewHosts)
                .replaceAll(START_SERVICES_VAR, startServices);

        File changeKaaHostFile = new File(distroPath, CHANGE_KAA_HOST);
        FileOutputStream fos = new FileOutputStream(changeKaaHostFile);
        fos.write(changeKaaHostFileSource.getBytes());
        fos.flush();
        fos.close();
        return changeKaaHostFile;
    }


    protected File prepareSandboxSplashFile() throws IOException {
        //Prepare sandbox splash Python script
        String sandboxSplashFileTemplate = FileUtils.readResource(SANDBOX_SPLASH_PY_TEMPLATE);
        String sandboxSplashFileSource = AbstractDemoBuilder.updateCredentialsInfo(sandboxSplashFileTemplate);

        sandboxSplashFileSource = sandboxSplashFileSource.replaceAll(WEB_ADMIN_PORT_VAR, DEFAULT_WEB_ADMIN_PORT+"")
                .replaceAll(SSH_FORWARD_PORT_VAR, DEFAULT_SSH_FORWARD_PORT + "");

        File sandboxSplashFile = new File(distroPath, SANDBOX_SPLASH_PY);
        FileOutputStream fos = new FileOutputStream(sandboxSplashFile);
        fos.write(sandboxSplashFileSource.getBytes());
        fos.flush();
        fos.close();
        return sandboxSplashFile;
    }


    protected File prepareProjectsXmlFile(List<Project> projects) throws JAXBException {
        File projectsXmlFile = new File(demoProjectsPath, DEMO_PROJECTS_XML);
        ProjectsConfig projectsConfig = new ProjectsConfig();
        projectsConfig.getProjects().addAll(projects);

        JAXBContext jc = JAXBContext.newInstance("org.kaaproject.kaa.sandbox.demo.projects");
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(projectsConfig, projectsXmlFile);
        return projectsXmlFile;
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

    protected String executeSudoSandbox(String command) {
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
        SandboxSshExec sshExec = createSshExec();
        sshExec.setCommandResource(commandsResource);
        sshExec.setOutputproperty("sshOutProp");
        sshExec.execute();
        return sandboxProject.getProperty("sshOutProp");
    }

    private SandboxSshExec createSshExec() {
        SandboxSshExec sshExec = new SandboxSshExec();
        initSsh(sshExec);
        return sshExec;
    }

    protected void transferAllFromDir(String dir, String to) throws IOException {
        Scp scp = createScp();
        FileSet fileSet = new FileSet();
        fileSet.setDir(new File(dir));
        fileSet.setIncludes("**/*");
        scp.addFileset(fileSet);
        scp.setRemoteTodir(SSH_USERNAME+"@"+DEFAULT_HOST+":"+to);
        scp.execute();
    }

    protected void transferFile(String file, String to) {
        Scp scp = createScp();
        scp.setLocalFile(file);
        scp.setRemoteTodir(SSH_USERNAME+"@"+DEFAULT_HOST+":"+to);
        scp.execute();
    }

    private void dumpLogs(){
        File dumpedLogsFolder  = new File(LOG_DUMP_LOCATION);
        if(!dumpedLogsFolder.exists()){
            dumpedLogsFolder.mkdirs();
        }
        Scp scp = createScp();
        scp.setLocalTodir(LOG_DUMP_LOCATION);
        scp.setFile(SSH_USERNAME + "@" + sshForwardPort + ":/var/log/kaa/*.log");
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
    protected abstract void provisionBoxImpl() throws Exception;
    protected abstract void unprovisionBoxImpl() throws Exception;
    protected abstract void stopBoxImpl() throws Exception;
    protected abstract void cleanupBoxImpl() throws Exception;
    protected abstract void exportBoxImpl() throws Exception;
    protected abstract void unloadBoxImpl() throws Exception;
    protected abstract boolean boxLoaded() throws Exception;
    protected abstract boolean boxRunning() throws Exception;
}
