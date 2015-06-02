package org.kaaproject.kaa.sandbox;


import org.apache.commons.lang.StringUtils;
import org.kaaproject.kaa.sandbox.demo.AbstractDemoBuilder;
import org.kaaproject.kaa.server.common.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class VeryAbstractSandboxBuilder implements SandboxBuilder, SandboxConstants {


    private static final Logger LOG = LoggerFactory.getLogger(VeryAbstractSandboxBuilder.class);

    protected final OsType osType;
    protected final File basePath;

    protected final String boxName;
    protected final int sshForwardPort;
    protected final int webAdminForwardPort;

    protected File distroPath;
    protected File demoProjectsPath;


    protected VeryAbstractSandboxBuilder(File basePath,
                                         OsType osType,
                                         String boxName,
                                         int sshForwardPort,
                                         int webAdminForwardPort) {
        this.basePath = basePath;
        this.osType = osType;
        this.boxName = boxName;
        this.sshForwardPort = sshForwardPort;
        this.webAdminForwardPort = webAdminForwardPort;
        this.distroPath = new File(basePath, "distro");
        this.demoProjectsPath = new File(basePath, "demo_projects");
    }


    @Override
    public void buildSandboxImage() throws Exception {
        cleanUp();
        try {
            preBuild();
            build();
            postBuild();
        } catch (Exception e) {
            onBuildFailure();
            throw e;
        } finally {
            cleanUp();
        }
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
        scheduleSudoSandboxCommand("chown -R " + SSH_USERNAME + ":" + SSH_USERNAME + " " + SANDBOX_FOLDER + "/" + DEMO_PROJECTS);
        scheduleSudoSandboxCommand("cp -r " + SANDBOX_PATH + "/*" + " " + ADMIN_FOLDER + "/");
        scheduleSudoSandboxCommand("chown -R " + SSH_USERNAME + ":" + SSH_USERNAME + " " + ADMIN_FOLDER + "/webapps");
        scheduleSudoSandboxCommand("sed -i \"s/\\(tenant_developer_user=\\).*\\$/\\1" + AbstractDemoBuilder.tenantDeveloperUser + "/\" " + ADMIN_FOLDER + "/conf/sandbox-server.properties");
        scheduleSudoSandboxCommand("sed -i \"s/\\(tenant_developer_password=\\).*\\$/\\1" + AbstractDemoBuilder.tenantDeveloperPassword + "/\" " + ADMIN_FOLDER + "/conf/sandbox-server.properties");
        String stopAdminCommand = osType.getStopServiceTemplate().replaceAll(SERVICE_NAME_VAR, KaaPackage.ADMIN.getServiceName());
        scheduleSudoSandboxCommand(stopAdminCommand);
        executeScheduledSandboxCommands();


        File changeKaaHostFile = prepareChangeKaaHostFile();
        File sandboxSplashFile = prepareSandboxSplashFile();
        transferFile(changeKaaHostFile.getAbsolutePath(), SANDBOX_FOLDER);
        transferFile(sandboxSplashFile.getAbsolutePath(), SANDBOX_FOLDER);


        executeSudoSandboxCommand("chmod +x " + SANDBOX_FOLDER + "/" + CHANGE_KAA_HOST);
        executeSudoSandboxCommand("chmod +x " + SANDBOX_FOLDER + "/" + SANDBOX_SPLASH_PY);
        String startAdminCommand = osType.getStartServiceTemplate().replaceAll(SERVICE_NAME_VAR, KaaPackage.ADMIN.getServiceName());
        executeSudoSandboxCommand(startAdminCommand);


        waitForLongRunningTask(50);

        buildSandboxMeta(SANDBOX_FOLDER + "/" + DEMO_PROJECTS +"/"+ DEMO_PROJECTS_XML);
        waitForLongRunningTask(20);
    }

    protected void buildSandboxMeta(String demoProjectsXML) throws Exception{
        transferFile(basePath.getAbsolutePath()+"/../../../meta-builder/target/meta-builder.jar",SANDBOX_FOLDER);
        executeSudoSandboxCommand("java -jar "+SANDBOX_FOLDER+"/meta-builder.jar "+DEFAULT_HOST+" "+webAdminForwardPort+" "+ demoProjectsXML);
    };

    private void provisionBox() throws Exception {
        provisionBoxImpl();
        scheduleSudoSandboxCommand("rm -rf " + "/" + SHARED_FOLDER);
        scheduleSudoSandboxCommand("mkdir -p " + "/" + SHARED_FOLDER);
        scheduleSudoSandboxCommand("mkdir -p " + SANDBOX_FOLDER);
        scheduleSudoSandboxCommand("chown -R " + SSH_USERNAME + ":" + SSH_USERNAME + " " + "/" + SHARED_FOLDER);
        scheduleSudoSandboxCommand("chown -R " + SSH_USERNAME + ":" + SSH_USERNAME + " " + SANDBOX_FOLDER);
        executeScheduledSandboxCommands();

        LOG.info("Transfering sandbox data...");
        transferAllFromDir(basePath.getAbsolutePath(), "/" + SHARED_FOLDER);
        LOG.info("Sandbox data transfered");
    }


    private void unprovisionBox() throws Exception {
        unprovisionBoxImpl();
        executeSudoSandboxCommand("rm -rf " + "/" + SHARED_FOLDER);
    }

    protected void build() throws Exception {
        provisionBox();
        schedulePackagesInstall();
        scheduleServicesStart();
        executeScheduledSandboxCommands();
        //wait for services deploy
        waitForLongRunningTask(80);
        initBoxData();
        unprovisionBox();
    }


    protected File prepareChangeKaaHostFile() throws IOException {
        //Change kaa hosts file
        String changeKaaHostFileTemplate = FileUtils.readResource(CHANGE_KAA_HOST_TEMPLATE);
        String stopServices = "";
        String setNewHosts = "";
        String startServices = "";
        for (KaaPackage kaaPackage : KaaPackage.values()) {
            if (kaaPackage.getHostProperties() != null &&
                    kaaPackage.getHostProperties().length > 0) {
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

        sandboxSplashFileSource = sandboxSplashFileSource.replaceAll(WEB_ADMIN_PORT_VAR, DEFAULT_WEB_ADMIN_PORT + "")
                .replaceAll(SSH_FORWARD_PORT_VAR, DEFAULT_SSH_FORWARD_PORT + "");

        File sandboxSplashFile = new File(distroPath, SANDBOX_SPLASH_PY);
        FileOutputStream fos = new FileOutputStream(sandboxSplashFile);
        fos.write(sandboxSplashFileSource.getBytes());
        fos.flush();
        fos.close();
        return sandboxSplashFile;
    }


    abstract protected void preBuild() throws Exception;

    abstract protected void postBuild() throws Exception;

    protected abstract void cleanUp() throws Exception;

    protected abstract void onBuildFailure();

    abstract protected String executeScheduledSandboxCommands();

    abstract protected String executeSudoSandboxCommand(String command);

    abstract protected void scheduleSudoSandboxCommand(String command);

    abstract protected void transferAllFromDir(String dir, String to);

    abstract protected void transferFile(String file, String to);

    abstract protected void waitForLongRunningTask(long seconds);

    protected abstract void provisionBoxImpl() throws Exception;

    protected abstract void unprovisionBoxImpl() throws Exception;
}
